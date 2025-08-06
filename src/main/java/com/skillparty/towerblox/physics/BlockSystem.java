package com.skillparty.towerblox.physics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enhanced block system with realistic physics and beautiful animations
 * Features particle effects, impact animations, and dynamic visuals
 * 
 * @author joseAlejandro
 * @version 2.0 - Production Ready
 */
public class BlockSystem {
    
    // Block properties
    private double x, y;
    private double width, height;
    private double rotation;
    private int floor;
    private BlockType type;
    
    // Physics body reference
    private PhysicsEngine.PhysicsBody physicsBody;
    
    // Animation states
    private AnimationState currentAnimation;
    private double animationTime;
    private double squashAmount;
    private double stretchAmount;
    private double wobbleAmount;
    
    // Visual effects
    private List<Particle> particles;
    private double glowIntensity;
    private Color baseColor;
    private Color currentColor;
    
    // Impact effects
    private boolean justLanded;
    private double impactForce;
    private double shakeAmount;
    
    private static final Random random = new Random();
    
    /**
     * Block types with different properties
     */
    public enum BlockType {
        NORMAL(new Color(180, 120, 80), 1.0),
        GLASS(new Color(150, 200, 255, 180), 0.7),
        STEEL(new Color(140, 140, 160), 1.5),
        GOLD(new Color(255, 215, 0), 2.0),
        SPECIAL(new Color(255, 100, 255), 1.0);
        
        public final Color color;
        public final double weight;
        
        BlockType(Color color, double weight) {
            this.color = color;
            this.weight = weight;
        }
    }
    
    /**
     * Animation states
     */
    public enum AnimationState {
        IDLE,
        FALLING,
        LANDING,
        WOBBLING,
        SETTLING,
        PERFECT_LAND
    }
    
    /**
     * Particle class for effects
     */
    private static class Particle {
        double x, y, vx, vy;
        double life, maxLife;
        Color color;
        double size;
        
        Particle(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.vx = (random.nextDouble() - 0.5) * 200;
            this.vy = -random.nextDouble() * 150 - 50;
            this.color = color;
            this.size = random.nextDouble() * 4 + 2;
            this.maxLife = this.life = random.nextDouble() * 0.5 + 0.3;
        }
        
        void update(double dt) {
            x += vx * dt;
            y += vy * dt;
            vy += 300 * dt; // Gravity
            life -= dt;
        }
        
        void render(Graphics2D g, double cameraY) {
            if (life <= 0) return;
            
            double alpha = life / maxLife;
            g.setColor(new Color(
                color.getRed(), color.getGreen(), color.getBlue(),
                (int)(alpha * 255)
            ));
            
            int particleSize = (int)(size * alpha);
            g.fillOval(
                (int)(x - particleSize/2),
                (int)(y - cameraY - particleSize/2),
                particleSize, particleSize
            );
        }
    }
    
    /**
     * Constructs a new block
     */
    public BlockSystem(double x, double y, BlockType type, int floor) {
        this.x = x;
        this.y = y;
        this.width = 80;
        this.height = 40;
        this.type = type;
        this.floor = floor;
        this.rotation = 0;
        
        this.baseColor = type.color;
        this.currentColor = baseColor;
        this.particles = new ArrayList<>();
        
        this.currentAnimation = AnimationState.FALLING;
        this.animationTime = 0;
        this.squashAmount = 1.0;
        this.stretchAmount = 1.0;
        this.wobbleAmount = 0;
        
        // Create physics body
        this.physicsBody = new PhysicsEngine.PhysicsBody(x, y, width, height);
        this.physicsBody.mass = width * height * 0.001 * type.weight;
        this.physicsBody.userData = this;
    }
    
    /**
     * Updates the block
     */
    public void update(double deltaTime) {
        // Update position from physics body
        if (physicsBody != null) {
            x = physicsBody.x;
            y = physicsBody.y;
            rotation = physicsBody.rotation;
        }
        
        // Update animation
        updateAnimation(deltaTime);
        
        // Update particles
        particles.removeIf(p -> p.life <= 0);
        for (Particle p : particles) {
            p.update(deltaTime);
        }
        
        // Update visual effects
        updateVisualEffects(deltaTime);
        
        animationTime += deltaTime;
    }
    
    /**
     * Updates animation state
     */
    private void updateAnimation(double deltaTime) {
        switch (currentAnimation) {
            case FALLING:
                // Stretch while falling
                double velocity = physicsBody != null ? physicsBody.vy : 0;
                stretchAmount = 1.0 + Math.min(0.3, Math.abs(velocity) / 1000);
                squashAmount = 1.0 / stretchAmount;
                wobbleAmount = Math.sin(animationTime * 10) * 0.02;
                break;
                
            case LANDING:
                // Squash on impact
                double landProgress = Math.min(1.0, animationTime / 0.2);
                squashAmount = 1.0 + (0.3 * (1.0 - landProgress)) * impactForce;
                stretchAmount = 1.0 / squashAmount;
                shakeAmount = (1.0 - landProgress) * impactForce * 5;
                
                if (landProgress >= 1.0) {
                    currentAnimation = AnimationState.WOBBLING;
                    animationTime = 0;
                }
                break;
                
            case WOBBLING:
                // Wobble after landing
                double wobbleProgress = Math.min(1.0, animationTime / 0.5);
                wobbleAmount = Math.sin(animationTime * 15) * 0.05 * (1.0 - wobbleProgress);
                squashAmount = 1.0 + wobbleAmount * 0.5;
                stretchAmount = 1.0 - wobbleAmount * 0.5;
                
                if (wobbleProgress >= 1.0) {
                    currentAnimation = AnimationState.SETTLING;
                    animationTime = 0;
                }
                break;
                
            case SETTLING:
                // Settle to rest
                squashAmount = squashAmount * 0.95 + 1.0 * 0.05;
                stretchAmount = stretchAmount * 0.95 + 1.0 * 0.05;
                wobbleAmount *= 0.9;
                shakeAmount *= 0.9;
                
                if (Math.abs(squashAmount - 1.0) < 0.01) {
                    currentAnimation = AnimationState.IDLE;
                }
                break;
                
            case PERFECT_LAND:
                // Special animation for perfect placement
                glowIntensity = Math.sin(animationTime * 10) * 0.5 + 0.5;
                createSparkles();
                
                if (animationTime > 1.0) {
                    currentAnimation = AnimationState.IDLE;
                    glowIntensity = 0;
                }
                break;
                
            case IDLE:
                // Subtle idle animation
                wobbleAmount = Math.sin(animationTime * 2) * 0.005;
                break;
        }
    }
    
    /**
     * Updates visual effects
     */
    private void updateVisualEffects(double deltaTime) {
        // Update glow
        if (glowIntensity > 0) {
            glowIntensity = Math.max(0, glowIntensity - deltaTime);
        }
        
        // Update color based on state
        if (currentAnimation == AnimationState.PERFECT_LAND) {
            // Pulse color for perfect land
            int r = Math.min(255, baseColor.getRed() + (int)(glowIntensity * 50));
            int g = Math.min(255, baseColor.getGreen() + (int)(glowIntensity * 50));
            int b = Math.min(255, baseColor.getBlue() + (int)(glowIntensity * 50));
            currentColor = new Color(r, g, b, baseColor.getAlpha());
        } else {
            currentColor = baseColor;
        }
    }
    
    /**
     * Triggers landing animation
     */
    public void onLand(double velocity) {
        currentAnimation = AnimationState.LANDING;
        animationTime = 0;
        justLanded = true;
        impactForce = Math.min(1.0, Math.abs(velocity) / 500);
        
        // Create impact particles
        createImpactParticles();
        
        // Create dust cloud
        createDustCloud();
    }
    
    /**
     * Triggers perfect landing animation
     */
    public void onPerfectLand() {
        currentAnimation = AnimationState.PERFECT_LAND;
        animationTime = 0;
        glowIntensity = 1.0;
        
        // Create celebration particles
        createCelebrationParticles();
    }
    
    /**
     * Creates impact particles
     */
    private void createImpactParticles() {
        int particleCount = (int)(10 + impactForce * 20);
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(
                x + width/2 + (random.nextDouble() - 0.5) * width,
                y + height,
                new Color(100, 100, 100)
            ));
        }
    }
    
    /**
     * Creates dust cloud effect
     */
    private void createDustCloud() {
        for (int i = 0; i < 15; i++) {
            Particle dust = new Particle(
                x + width/2 + (random.nextDouble() - 0.5) * width * 1.5,
                y + height,
                new Color(150, 150, 150, 100)
            );
            dust.size = 8 + random.nextDouble() * 8;
            dust.vx *= 0.5;
            dust.vy *= 0.3;
            particles.add(dust);
        }
    }
    
    /**
     * Creates celebration particles
     */
    private void createCelebrationParticles() {
        Color[] colors = {
            new Color(255, 200, 0),
            new Color(255, 100, 100),
            new Color(100, 255, 100),
            new Color(100, 100, 255)
        };
        
        for (int i = 0; i < 20; i++) {
            Particle star = new Particle(
                x + width/2,
                y + height/2,
                colors[i % colors.length]
            );
            double angle = (i / 20.0) * Math.PI * 2;
            star.vx = Math.cos(angle) * 150;
            star.vy = Math.sin(angle) * 150;
            particles.add(star);
        }
    }
    
    /**
     * Creates sparkle effects
     */
    private void createSparkles() {
        if (random.nextDouble() < 0.1) {
            particles.add(new Particle(
                x + random.nextDouble() * width,
                y + random.nextDouble() * height,
                new Color(255, 255, 200)
            ));
        }
    }
    
    /**
     * Renders the block
     */
    public void render(Graphics2D g, double cameraY) {
        // Save transform
        AffineTransform originalTransform = g.getTransform();
        
        // Enable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Render particles behind block
        for (Particle p : particles) {
            p.render(g, cameraY);
        }
        
        // Apply transformations
        double centerX = x + width/2;
        double centerY = y + height/2;
        
        g.translate(centerX + shakeAmount * (random.nextDouble() - 0.5), 
                   centerY - cameraY + shakeAmount * (random.nextDouble() - 0.5));
        g.rotate(rotation + wobbleAmount);
        g.scale(squashAmount, stretchAmount);
        
        // Draw shadow
        drawShadow(g);
        
        // Draw glow effect
        if (glowIntensity > 0) {
            drawGlow(g);
        }
        
        // Draw main block
        drawBlock(g);
        
        // Draw details
        drawBlockDetails(g);
        
        // Restore transform
        g.setTransform(originalTransform);
    }
    
    /**
     * Draws block shadow
     */
    private void drawShadow(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 50));
        g.fillRect(
            (int)(-width/2 + 3),
            (int)(-height/2 + 3),
            (int)width, (int)height
        );
    }
    
    /**
     * Draws glow effect
     */
    private void drawGlow(Graphics2D g) {
        int glowSize = (int)(glowIntensity * 20);
        g.setColor(new Color(255, 255, 200, (int)(glowIntensity * 100)));
        for (int i = glowSize; i > 0; i -= 2) {
            g.drawRect(
                (int)(-width/2 - i),
                (int)(-height/2 - i),
                (int)(width + i*2),
                (int)(height + i*2)
            );
        }
    }
    
    /**
     * Draws the main block
     */
    private void drawBlock(Graphics2D g) {
        // Main block body
        g.setColor(currentColor);
        g.fillRect(
            (int)(-width/2),
            (int)(-height/2),
            (int)width, (int)height
        );
        
        // Block border
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawRect(
            (int)(-width/2),
            (int)(-height/2),
            (int)width, (int)height
        );
    }
    
    /**
     * Draws block details (windows, etc)
     */
    private void drawBlockDetails(Graphics2D g) {
        // Draw windows
        g.setColor(new Color(100, 150, 200, 200));
        int windowWidth = 10;
        int windowHeight = 12;
        int windowSpacing = 15;
        
        for (int i = -1; i <= 1; i++) {
            g.fillRect(
                i * windowSpacing - windowWidth/2,
                -windowHeight/2,
                windowWidth, windowHeight
            );
            
            // Window frame
            g.setColor(Color.DARK_GRAY);
            g.drawRect(
                i * windowSpacing - windowWidth/2,
                -windowHeight/2,
                windowWidth, windowHeight
            );
        }
        
        // Floor number (if high enough)
        if (floor > 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            String floorText = String.valueOf(floor);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(floorText, 
                -fm.stringWidth(floorText)/2,
                (int)(height/2) - 5
            );
        }
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public PhysicsEngine.PhysicsBody getPhysicsBody() { return physicsBody; }
    public BlockType getType() { return type; }
    public int getFloor() { return floor; }
    public AnimationState getAnimation() { return currentAnimation; }
}

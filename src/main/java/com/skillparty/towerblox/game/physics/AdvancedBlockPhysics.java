package com.skillparty.towerblox.game.physics;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Advanced physics system for blocks in Tower Bloxx 2025
 * Includes realistic physics, rotation, bouncing, and visual effects
 * 
 * @author joseAlejandro
 */
public class AdvancedBlockPhysics {
    
    // Enhanced physics constants
    private static final double GRAVITY = 0.8;
    private static final double AIR_RESISTANCE = 0.999;
    private static final double BOUNCE_DAMPING = 0.3;
    private static final double ROTATION_DAMPING = 0.95;
    private static final double MIN_VELOCITY = 0.05;
    private static final double MAX_FALL_SPEED = 15.0;
    private static final double WOBBLE_FACTOR = 0.02;
    
    /**
     * Advanced block physics state
     */
    public static class PhysicsState {
        public double velocityX = 0;
        public double velocityY = 0;
        public double angularVelocity = 0;
        public double rotation = 0;
        public boolean isStable = false;
        public boolean isFalling = false;
        public boolean hasLanded = false;
        public double wobble = 0;
        public long landingTime = 0;
        
        // Trail effect
        public java.util.List<TrailPoint> trail = new java.util.ArrayList<>();
        
        // Impact effect
        public boolean showImpact = false;
        public long impactTime = 0;
        public double impactIntensity = 0;
    }
    
    /**
     * Trail point for visual effects
     */
    public static class TrailPoint {
        public double x, y;
        public long time;
        public double alpha;
        
        public TrailPoint(double x, double y) {
            this.x = x;
            this.y = y;
            this.time = System.currentTimeMillis();
            this.alpha = 1.0;
        }
    }
    
    /**
     * Update block physics with advanced calculations
     */
    public static void updateAdvancedPhysics(Block block, PhysicsState physics, double deltaTime) {
        if (physics.isStable) {
            updateStableBlock(block, physics, deltaTime);
            return;
        }
        
        // Apply gravity
        if (physics.isFalling) {
            physics.velocityY += GRAVITY * deltaTime;
            
            // Apply air resistance
            physics.velocityX *= AIR_RESISTANCE;
            physics.velocityY = Math.min(physics.velocityY, MAX_FALL_SPEED);
            
            // Add slight wobble during fall
            physics.wobble = Math.sin(System.currentTimeMillis() * 0.01) * WOBBLE_FACTOR;
            physics.velocityX += physics.wobble;
            
            // Update rotation during fall
            physics.angularVelocity += (physics.velocityX * 0.1) * deltaTime;
            physics.angularVelocity *= ROTATION_DAMPING;
            physics.rotation += physics.angularVelocity * deltaTime;
            
            // Update position
            block.setX(block.getX() + physics.velocityX * deltaTime * 60);
            block.setY(block.getY() + physics.velocityY * deltaTime * 60);
            
            // Add trail points during fall
            addTrailPoint(block, physics);
        }
        
        // Update trail
        updateTrail(physics);
        
        // Check for stability
        if (Math.abs(physics.velocityX) < MIN_VELOCITY && 
            Math.abs(physics.velocityY) < MIN_VELOCITY &&
            physics.hasLanded) {
            
            physics.isStable = true;
            physics.isFalling = false;
            physics.velocityX = 0;
            physics.velocityY = 0;
            physics.angularVelocity *= 0.1; // Slow rotation to stop
        }
    }
    
    /**
     * Handle collision with improved physics
     */
    public static boolean handleCollision(Block block, PhysicsState physics, double groundY) {
        if (block.getY() + block.getHeight() >= groundY) {
            // Landing impact
            double impactVelocity = Math.abs(physics.velocityY);
            
            // Position correction
            block.setY(groundY - block.getHeight());
            
            // Bounce effect based on impact velocity
            if (impactVelocity > 3.0) {
                physics.velocityY = -impactVelocity * BOUNCE_DAMPING;
                physics.velocityX += (Math.random() - 0.5) * impactVelocity * 0.2; // Scatter effect
                
                // Add rotation from impact
                physics.angularVelocity += (Math.random() - 0.5) * impactVelocity * 0.1;
            } else {
                physics.velocityY = 0;
            }
            
            // Impact visual effect
            if (impactVelocity > 1.0) {
                physics.showImpact = true;
                physics.impactTime = System.currentTimeMillis();
                physics.impactIntensity = impactVelocity;
            }
            
            physics.hasLanded = true;
            return true;
        }
        return false;
    }
    
    /**
     * Handle block-to-block collision
     */
    public static boolean handleBlockCollision(Block block1, PhysicsState physics1, 
                                             Block block2, PhysicsState physics2) {
        if (isColliding(block1, block2)) {
            // Calculate collision response
            double overlapY = (block1.getY() + block1.getHeight()) - block2.getY();
            
            if (overlapY > 0) {
                // Separate blocks
                block1.setY(block1.getY() - overlapY / 2);
                block2.setY(block2.getY() + overlapY / 2);
                
                // Exchange momentum
                double tempVelY = physics1.velocityY;
                physics1.velocityY = physics2.velocityY * 0.8;
                physics2.velocityY = tempVelY * 0.8;
                
                // Add some horizontal spread
                physics1.velocityX += (Math.random() - 0.5) * 2.0;
                physics2.velocityX += (Math.random() - 0.5) * 2.0;
                
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if two blocks are colliding
     */
    private static boolean isColliding(Block block1, Block block2) {
        return block1.getX() < block2.getX() + block2.getWidth() &&
               block1.getX() + block1.getWidth() > block2.getX() &&
               block1.getY() < block2.getY() + block2.getHeight() &&
               block1.getY() + block1.getHeight() > block2.getY();
    }
    
    /**
     * Update stable block (slight movement for realism)
     */
    private static void updateStableBlock(Block block, PhysicsState physics, double deltaTime) {
        // Slight settling movement
        if (Math.abs(physics.angularVelocity) > 0.001) {
            physics.rotation += physics.angularVelocity * deltaTime;
            physics.angularVelocity *= 0.95; // Gradual stop
        }
        
        // Very subtle breathing effect for life-like appearance
        if (physics.landingTime > 0) {
            long timeSinceLanding = System.currentTimeMillis() - physics.landingTime;
            if (timeSinceLanding < 2000) { // 2 seconds of settling
                double settleFactor = 1.0 - (timeSinceLanding / 2000.0);
                physics.wobble = Math.sin(timeSinceLanding * 0.005) * 0.5 * settleFactor;
            }
        }
    }
    
    /**
     * Add trail point for visual effect
     */
    private static void addTrailPoint(Block block, PhysicsState physics) {
        if (physics.velocityY > 2.0) { // Only add trail when moving fast
            physics.trail.add(new TrailPoint(
                block.getX() + block.getWidth() / 2,
                block.getY() + block.getHeight() / 2
            ));
            
            // Limit trail length
            if (physics.trail.size() > 8) {
                physics.trail.remove(0);
            }
        }
    }
    
    /**
     * Update trail alpha for fading effect
     */
    private static void updateTrail(PhysicsState physics) {
        long currentTime = System.currentTimeMillis();
        physics.trail.removeIf(point -> {
            long age = currentTime - point.time;
            point.alpha = Math.max(0, 1.0 - (age / 500.0)); // Fade over 500ms
            return point.alpha <= 0;
        });
    }
    
    /**
     * Render block with advanced visual effects
     */
    public static void renderAdvanced(Graphics2D g2d, Block block, PhysicsState physics) {
        AffineTransform originalTransform = g2d.getTransform();
        
        // Apply rotation and wobble
        if (Math.abs(physics.rotation) > 0.001 || Math.abs(physics.wobble) > 0.001) {
            double centerX = block.getX() + block.getWidth() / 2;
            double centerY = block.getY() + block.getHeight() / 2;
            
            g2d.rotate(physics.rotation + physics.wobble, centerX, centerY);
        }
        
        // Render trail first (behind block)
        renderTrail(g2d, physics);
        
        // Render the block with original method
        block.render(g2d);
        
        // Render impact effect
        if (physics.showImpact) {
            renderImpactEffect(g2d, block, physics);
        }
        
        // Reset transform
        g2d.setTransform(originalTransform);
    }
    
    /**
     * Render trail effect
     */
    private static void renderTrail(Graphics2D g2d, PhysicsState physics) {
        if (physics.trail.isEmpty()) return;
        
        g2d.setStroke(new BasicStroke(2.0f));
        
        for (int i = 0; i < physics.trail.size(); i++) {
            TrailPoint point = physics.trail.get(i);
            
            // Trail color with fading alpha
            Color trailColor = new Color(255, 255, 255, 
                (int)(point.alpha * 100 * (i + 1) / physics.trail.size()));
            g2d.setColor(trailColor);
            
            // Draw trail point
            int size = (int)(4 * point.alpha * (i + 1) / physics.trail.size());
            g2d.fillOval((int)(point.x - size/2), (int)(point.y - size/2), size, size);
        }
    }
    
    /**
     * Render impact effect
     */
    private static void renderImpactEffect(Graphics2D g2d, Block block, PhysicsState physics) {
        long elapsed = System.currentTimeMillis() - physics.impactTime;
        if (elapsed > 200) { // 200ms impact effect
            physics.showImpact = false;
            return;
        }
        
        double progress = elapsed / 200.0;
        double alpha = 1.0 - progress;
        
        // Impact flash
        Color impactColor = new Color(255, 255, 0, (int)(alpha * 100));
        g2d.setColor(impactColor);
        
        // Expanding circle effect
        int radius = (int)(progress * 30 * physics.impactIntensity / 5.0);
        int x = (int)(block.getX() + block.getWidth() / 2 - radius / 2);
        int y = (int)(block.getY() + block.getHeight() - 5);
        
        g2d.drawOval(x, y, radius, radius / 2);
        
        // Dust particles
        for (int i = 0; i < 5; i++) {
            double angle = (i / 5.0) * Math.PI * 2;
            int px = x + (int)(Math.cos(angle) * radius * 0.7);
            int py = y + (int)(Math.sin(angle) * radius * 0.3);
            g2d.fillOval(px, py, 2, 2);
        }
    }
    
    /**
     * Initialize physics state for a new falling block
     */
    public static PhysicsState createFallingBlock(double initialVelX, double initialVelY) {
        PhysicsState physics = new PhysicsState();
        physics.velocityX = initialVelX;
        physics.velocityY = initialVelY;
        physics.isFalling = true;
        physics.isStable = false;
        physics.hasLanded = false;
        return physics;
    }
    
    /**
     * Make block stable (landed and settled)
     */
    public static void makeStable(PhysicsState physics) {
        physics.isStable = true;
        physics.isFalling = false;
        physics.velocityX = 0;
        physics.velocityY = 0;
        physics.landingTime = System.currentTimeMillis();
    }
}

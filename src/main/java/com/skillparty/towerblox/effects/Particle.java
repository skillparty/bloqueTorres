package com.skillparty.towerblox.effects;

import java.awt.*;

/**
 * Represents a single particle in the particle system
 * Used for visual effects like explosions, sparks, and trails
 */
public class Particle {
    private float x, y;
    private float velocityX, velocityY;
    private float life, maxLife;
    private Color color;
    private float size;
    private float gravity;
    private ParticleType type;
    private boolean active;
    
    public enum ParticleType {
        SPARK,      // Golden sparks for perfect placements
        FIREWORK,   // Colorful firework particles
        SMOKE,      // Smoke trails for falling blocks
        DUST,       // Impact dust particles
        STAR        // Star particles for celebrations
    }
    
    public Particle() {
        this.active = false;
    }
    
    /**
     * Initialize particle with properties
     */
    public void init(float x, float y, float velocityX, float velocityY, 
                    Color color, float life, float size, ParticleType type) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.color = color;
        this.life = life;
        this.maxLife = life;
        this.size = size;
        this.type = type;
        this.gravity = type == ParticleType.SMOKE ? -0.1f : 0.2f; // Smoke rises, others fall
        this.active = true;
    }
    
    /**
     * Update particle physics and life cycle
     */
    public void update(double deltaTime) {
        if (!active) return;
        
        float dt = (float) deltaTime;
        
        // Update position
        x += velocityX * dt;
        y += velocityY * dt;
        
        // Apply gravity
        velocityY += gravity * dt;
        
        // Apply air resistance
        velocityX *= 0.98f;
        velocityY *= 0.98f;
        
        // Update life
        life -= dt;
        
        // Deactivate if life expired
        if (life <= 0) {
            active = false;
        }
    }
    
    /**
     * Render the particle
     */
    public void render(Graphics2D g2d) {
        if (!active) return;
        
        // Calculate alpha based on remaining life
        float alpha = Math.max(0, Math.min(1, life / maxLife));
        
        // Create color with alpha
        Color renderColor = new Color(
            color.getRed() / 255f,
            color.getGreen() / 255f,
            color.getBlue() / 255f,
            alpha
        );
        
        g2d.setColor(renderColor);
        
        // Render based on particle type
        switch (type) {
            case SPARK:
                renderSpark(g2d);
                break;
            case FIREWORK:
                renderFirework(g2d);
                break;
            case SMOKE:
                renderSmoke(g2d);
                break;
            case DUST:
                renderDust(g2d);
                break;
            case STAR:
                renderStar(g2d);
                break;
        }
    }
    
    private void renderSpark(Graphics2D g2d) {
        // Render as a small bright line
        int length = (int)(size * 3);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine((int)x, (int)y, 
                    (int)(x - velocityX * 0.1f), 
                    (int)(y - velocityY * 0.1f));
    }
    
    private void renderFirework(Graphics2D g2d) {
        // Render as a bright circle
        int diameter = (int)size;
        g2d.fillOval((int)x - diameter/2, (int)y - diameter/2, diameter, diameter);
    }
    
    private void renderSmoke(Graphics2D g2d) {
        // Render as a soft circle
        int diameter = (int)(size * (1.5f - life / maxLife)); // Grows over time
        g2d.fillOval((int)x - diameter/2, (int)y - diameter/2, diameter, diameter);
    }
    
    private void renderDust(Graphics2D g2d) {
        // Render as small squares
        int size = (int)this.size;
        g2d.fillRect((int)x - size/2, (int)y - size/2, size, size);
    }
    
    private void renderStar(Graphics2D g2d) {
        // Render as a 4-pointed star
        int[] xPoints = {(int)x, (int)(x + size/2), (int)x, (int)(x - size/2)};
        int[] yPoints = {(int)(y - size), (int)y, (int)(y + size), (int)y};
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        // Add cross
        int[] xPoints2 = {(int)(x - size/2), (int)(x + size/2), (int)(x + size/2), (int)(x - size/2)};
        int[] yPoints2 = {(int)(y - size/2), (int)(y - size/2), (int)(y + size/2), (int)(y + size/2)};
        g2d.fillPolygon(xPoints2, yPoints2, 4);
    }
    
    // Getters
    public boolean isActive() { return active; }
    public float getX() { return x; }
    public float getY() { return y; }
    public ParticleType getType() { return type; }
    
    // Reset particle for reuse
    public void reset() {
        active = false;
        life = 0;
    }
}
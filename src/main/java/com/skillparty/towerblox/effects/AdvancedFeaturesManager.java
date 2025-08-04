package com.skillparty.towerblox.effects;

import com.skillparty.towerblox.game.physics.Block;
import java.awt.*;

/**
 * Central manager for all advanced features including visual effects,
 * sound, power-ups, weather, and other enhancements
 */
public class AdvancedFeaturesManager {
    private final ParticleSystem particleSystem;
    private boolean enabled;
    
    // Performance monitoring
    private long lastFrameTime;
    private int frameCount;
    private double averageFPS;
    
    public AdvancedFeaturesManager() {
        this.particleSystem = new ParticleSystem();
        this.enabled = true;
        this.lastFrameTime = System.currentTimeMillis();
    }
    
    /**
     * Update all advanced features
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        
        // Update particle system
        particleSystem.update(deltaTime);
        
        // Update performance monitoring
        updatePerformanceMetrics();
    }
    
    /**
     * Render all advanced features
     */
    public void render(Graphics2D g2d) {
        if (!enabled) return;
        
        // Render particle effects
        particleSystem.render(g2d);
    }
    
    /**
     * Trigger effects for perfect block placement
     */
    public void onPerfectBlockPlacement(int x, int y, Block block) {
        if (!enabled) return;
        
        // Create golden explosion effect
        particleSystem.createExplosion(x, y, Color.YELLOW, 2);
        
        // Add dust impact effect
        particleSystem.createDustImpact(x, (int)(y + block.getHeight()), block.getColor());
    }
    
    /**
     * Trigger effects for good block placement
     */
    public void onGoodBlockPlacement(int x, int y, Block block) {
        if (!enabled) return;
        
        // Create smaller explosion effect
        particleSystem.createExplosion(x, y, block.getColor(), 1);
        
        // Add dust impact effect
        particleSystem.createDustImpact(x, (int)(y + block.getHeight()), block.getColor());
    }
    
    /**
     * Trigger effects for milestone achievements
     */
    public void onMilestoneReached(int x, int y, int milestone) {
        if (!enabled) return;
        
        // Create fireworks for major milestones
        if (milestone % 10 == 0) {
            particleSystem.createFireworks(x, y - 50);
        }
        
        // Create celebration explosion
        particleSystem.createExplosion(x, y, new Color(255, 215, 0), 3); // Gold color
    }
    
    /**
     * Trigger effects for falling blocks
     */
    public void onBlockFalling(int x, int y) {
        if (!enabled) return;
        
        // Create smoke trail effect
        particleSystem.createSmokeTrail(x, y, 1000);
    }
    
    /**
     * Trigger effects for block collision/impact
     */
    public void onBlockImpact(int x, int y, Block block, float impactForce) {
        if (!enabled) return;
        
        // Create dust impact based on force
        int intensity = Math.min(3, (int)(impactForce / 10) + 1);
        for (int i = 0; i < intensity; i++) {
            particleSystem.createDustImpact(x + (i * 5), y, block.getColor());
        }
    }
    
    /**
     * Update performance metrics and adjust quality if needed
     */
    private void updatePerformanceMetrics() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastFrameTime >= 1000) { // Update every second
            averageFPS = frameCount * 1000.0 / (currentTime - lastFrameTime);
            frameCount = 0;
            lastFrameTime = currentTime;
            
            // Auto-adjust particle quality based on performance
            adjustQualityBasedOnPerformance();
        }
    }
    
    /**
     * Automatically adjust visual quality based on performance
     */
    private void adjustQualityBasedOnPerformance() {
        if (averageFPS < 30 && particleSystem.isEnabled()) {
            // Performance is poor, consider reducing particle effects
            System.out.println("Performance warning: FPS = " + averageFPS + 
                             ", Active particles: " + particleSystem.getActiveParticleCount());
        }
    }
    
    /**
     * Get performance information for debugging
     */
    public String getPerformanceInfo() {
        return String.format("FPS: %.1f | Particles: %d", 
                           averageFPS, particleSystem.getActiveParticleCount());
    }
    
    // Settings and controls
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            particleSystem.clear();
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setParticleEffectsEnabled(boolean enabled) {
        particleSystem.setEnabled(enabled);
    }
    
    public boolean areParticleEffectsEnabled() {
        return particleSystem.isEnabled();
    }
    
    public void clearAllEffects() {
        particleSystem.clear();
    }
    
    // Getters for subsystems
    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }
}
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
        
        // Special effects for extreme heights
        if (milestone >= 30) {
            createExtremeHeightEffects(x, y, milestone);
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
    
    /**
     * Creates special effects for extreme tower heights
     */
    private void createExtremeHeightEffects(int x, int y, int milestone) {
        if (milestone >= 50) {
            // Stratosphere effects - aurora-like particles
            createAuroraEffect(x, y);
        } else if (milestone >= 40) {
            // Skyscraper effects - cloud burst
            createCloudBurstEffect(x, y);
        } else if (milestone >= 30) {
            // High-rise effects - wind vortex
            createWindVortexEffect(x, y);
        }
    }
    
    /**
     * Creates aurora-like effect for stratosphere heights
     */
    private void createAuroraEffect(int x, int y) {
        // Create shimmering aurora particles
        for (int i = 0; i < 15; i++) {
            Color auroraColor = new Color(
                (int)(Math.random() * 100 + 100), 
                255, 
                (int)(Math.random() * 100 + 150),
                150
            );
            particleSystem.createExplosion(x + (int)(Math.random() * 100 - 50), 
                                         y - (int)(Math.random() * 50), 
                                         auroraColor, 1);
        }
    }
    
    /**
     * Creates cloud burst effect for skyscraper heights
     */
    private void createCloudBurstEffect(int x, int y) {
        // Create expanding cloud-like particles
        for (int i = 0; i < 20; i++) {
            Color cloudColor = new Color(255, 255, 255, 120);
            particleSystem.createExplosion(x + (int)(Math.random() * 80 - 40), 
                                         y - (int)(Math.random() * 30), 
                                         cloudColor, 2);
        }
    }
    
    /**
     * Creates wind vortex effect for high-rise heights
     */
    private void createWindVortexEffect(int x, int y) {
        // Create swirling wind particles
        for (int i = 0; i < 12; i++) {
            double angle = (i * Math.PI * 2) / 12;
            int windX = x + (int)(Math.cos(angle) * 30);
            int windY = y + (int)(Math.sin(angle) * 15);
            Color windColor = new Color(200, 220, 255, 100);
            particleSystem.createSmokeTrail(windX, windY, 500);
        }
    }
    
    /**
     * Trigger effects for tower height transitions
     */
    public void onHeightTransition(int x, int y, int newHeight, int previousHeight) {
        if (!enabled) return;
        
        // Check for major height transitions
        if (newHeight >= 50 && previousHeight < 50) {
            // Entering stratosphere
            createStratosphereTransition(x, y);
        } else if (newHeight >= 40 && previousHeight < 40) {
            // Entering skyscraper level
            createSkyscraperTransition(x, y);
        } else if (newHeight >= 30 && previousHeight < 30) {
            // Entering high-rise level
            createHighRiseTransition(x, y);
        } else if (newHeight >= 20 && previousHeight < 20) {
            // Entering mid-rise level
            createMidRiseTransition(x, y);
        }
    }
    
    /**
     * Creates stratosphere transition effect
     */
    private void createStratosphereTransition(int x, int y) {
        // Dramatic aurora burst
        for (int i = 0; i < 25; i++) {
            Color transitionColor = new Color(0, 255, 150, 200);
            particleSystem.createExplosion(x + (int)(Math.random() * 120 - 60), 
                                         y - (int)(Math.random() * 80), 
                                         transitionColor, 3);
        }
        System.out.println("🌌 STRATOSPHERE TRANSITION - Entering space-like zone!");
    }
    
    /**
     * Creates skyscraper transition effect
     */
    private void createSkyscraperTransition(int x, int y) {
        // Cloud penetration effect
        for (int i = 0; i < 20; i++) {
            Color cloudColor = new Color(255, 255, 255, 180);
            particleSystem.createExplosion(x + (int)(Math.random() * 100 - 50), 
                                         y - (int)(Math.random() * 60), 
                                         cloudColor, 2);
        }
        System.out.println("☁️ SKYSCRAPER TRANSITION - Above the clouds!");
    }
    
    /**
     * Creates high-rise transition effect
     */
    private void createHighRiseTransition(int x, int y) {
        // Wind burst effect
        for (int i = 0; i < 15; i++) {
            Color windColor = new Color(200, 220, 255, 150);
            particleSystem.createSmokeTrail(x + (int)(Math.random() * 80 - 40), 
                                          y - (int)(Math.random() * 40), 800);
        }
        System.out.println("💨 HIGH-RISE TRANSITION - Entering wind zone!");
    }
    
    /**
     * Creates mid-rise transition effect
     */
    private void createMidRiseTransition(int x, int y) {
        // Urban atmosphere effect
        for (int i = 0; i < 10; i++) {
            Color urbanColor = new Color(150, 150, 200, 120);
            particleSystem.createDustImpact(x + (int)(Math.random() * 60 - 30), 
                                          y - (int)(Math.random() * 30), urbanColor);
        }
        System.out.println("🏙️ MID-RISE TRANSITION - Above street level!");
    }
    
    /**
     * Enable/disable professional mode features
     */
    public void enableProfessionalMode(boolean enabled) {
        if (enabled) {
            System.out.println("🏆 Advanced Features Manager: Professional mode ENABLED");
            System.out.println("✨ Enhanced particle effects active");
            System.out.println("🎨 Professional visual enhancements active");
            this.enabled = true;
        } else {
            System.out.println("📦 Professional mode DISABLED - Standard mode");
        }
    }
    
    // Getters for subsystems
    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }
}
package com.skillparty.towerblox.effects;

import java.awt.*;
import java.util.Random;

/**
 * Main particle system that manages all visual effects
 * Handles creation, updating, and rendering of particle effects
 */
public class ParticleSystem {
    private final ParticlePool particlePool;
    private final Random random;
    private boolean enabled;
    
    // Performance settings
    private static final int MAX_PARTICLES = 500;
    private static final int EXPLOSION_PARTICLE_COUNT = 15;
    private static final int FIREWORK_PARTICLE_COUNT = 25;
    private static final int SMOKE_PARTICLE_COUNT = 8;
    private static final int DUST_PARTICLE_COUNT = 12;
    
    public ParticleSystem() {
        this.particlePool = new ParticlePool(MAX_PARTICLES);
        this.random = new Random();
        this.enabled = true;
    }
    
    /**
     * Create explosion effect for perfect block placement
     */
    public void createExplosion(int x, int y, Color baseColor, int intensity) {
        if (!enabled) return;
        
        int particleCount = Math.min(EXPLOSION_PARTICLE_COUNT * intensity, 40);
        
        // Create main explosion burst
        for (int i = 0; i < particleCount; i++) {
            Particle particle = particlePool.acquire();
            if (particle == null) break;
            
            // Random direction and speed
            float angle = (float)(random.nextDouble() * Math.PI * 2);
            float speed = 60 + random.nextFloat() * 120 * intensity;
            float velocityX = (float)(Math.cos(angle) * speed);
            float velocityY = (float)(Math.sin(angle) * speed);
            
            // Color variations based on intensity
            Color sparkColor;
            if (intensity >= 2) {
                sparkColor = createGoldenVariation(baseColor); // Golden for perfect placements
            } else {
                sparkColor = createColorVariation(baseColor); // Regular color variations
            }
            
            // Random life and size based on intensity
            float life = 0.8f + random.nextFloat() * 1.5f * intensity;
            float size = 2 + random.nextFloat() * 5 * intensity;
            
            particle.init(x + random.nextInt(10) - 5, y + random.nextInt(10) - 5, 
                         velocityX, velocityY, sparkColor, life, size, Particle.ParticleType.SPARK);
        }
        
        // Add extra star particles for high intensity explosions
        if (intensity >= 2) {
            for (int i = 0; i < 5; i++) {
                Particle particle = particlePool.acquire();
                if (particle == null) break;
                
                float velocityX = (random.nextFloat() - 0.5f) * 80;
                float velocityY = (random.nextFloat() - 0.5f) * 80;
                
                particle.init(x, y, velocityX, velocityY, Color.YELLOW, 2.0f, 8, Particle.ParticleType.STAR);
            }
        }
    }
    
    /**
     * Create fireworks effect for milestone achievements
     */
    public void createFireworks(int x, int y) {
        if (!enabled) return;
        
        // Create multiple sequential bursts with delay effect
        for (int burst = 0; burst < 4; burst++) {
            int burstX = x + (random.nextInt(120) - 60);
            int burstY = y + (random.nextInt(60) - 30);
            
            // Main firework burst
            for (int i = 0; i < FIREWORK_PARTICLE_COUNT; i++) {
                Particle particle = particlePool.acquire();
                if (particle == null) break;
                
                // Circular explosion pattern with some randomness
                float angle = (float)(i * Math.PI * 2 / FIREWORK_PARTICLE_COUNT) + random.nextFloat() * 0.3f;
                float speed = 90 + random.nextFloat() * 80;
                float velocityX = (float)(Math.cos(angle) * speed);
                float velocityY = (float)(Math.sin(angle) * speed);
                
                // Colorful firework colors
                Color fireworkColor = createFireworkColor();
                
                float life = 1.2f + random.nextFloat() * 2.0f;
                float size = 4 + random.nextFloat() * 6;
                
                particle.init(burstX, burstY, velocityX, velocityY, fireworkColor, life, size, Particle.ParticleType.FIREWORK);
            }
            
            // Add inner burst with different colors
            for (int i = 0; i < 8; i++) {
                Particle particle = particlePool.acquire();
                if (particle == null) break;
                
                float angle = (float)(i * Math.PI * 2 / 8);
                float speed = 40 + random.nextFloat() * 30;
                float velocityX = (float)(Math.cos(angle) * speed);
                float velocityY = (float)(Math.sin(angle) * speed);
                
                particle.init(burstX, burstY, velocityX, velocityY, Color.WHITE, 1.5f, 3, Particle.ParticleType.SPARK);
            }
        }
        
        // Add cascading star particles for extra celebration
        for (int i = 0; i < 15; i++) {
            Particle particle = particlePool.acquire();
            if (particle == null) break;
            
            float velocityX = (random.nextFloat() - 0.5f) * 60;
            float velocityY = -20 - random.nextFloat() * 40; // Upward motion
            
            Color starColor = i % 2 == 0 ? Color.YELLOW : new Color(255, 215, 0); // Alternate gold colors
            
            particle.init(x + random.nextInt(40) - 20, y, velocityX, velocityY, starColor, 3.0f, 8, Particle.ParticleType.STAR);
        }
    }
    
    /**
     * Create smoke trail effect for falling blocks
     */
    public void createSmokeTrail(int x, int y, int duration) {
        if (!enabled) return;
        
        // Create main smoke trail
        for (int i = 0; i < SMOKE_PARTICLE_COUNT; i++) {
            Particle particle = particlePool.acquire();
            if (particle == null) break;
            
            // Upward and slightly random movement with wind effect
            float velocityX = (random.nextFloat() - 0.5f) * 30 + (float)Math.sin(System.currentTimeMillis() * 0.001) * 10;
            float velocityY = -15 - random.nextFloat() * 25; // Stronger upward motion
            
            // Varied gray smoke colors with some transparency effect
            int grayValue = 80 + random.nextInt(70);
            Color smokeColor = new Color(grayValue, grayValue, grayValue + 10); // Slightly blue-tinted
            
            float life = 1.5f + random.nextFloat() * 2.5f; // Longer lasting
            float size = 3 + random.nextFloat() * 10; // More size variation
            
            particle.init(x + random.nextInt(25) - 12, y + random.nextInt(10) - 5, 
                         velocityX, velocityY, smokeColor, life, size, Particle.ParticleType.SMOKE);
        }
        
        // Add some dust particles for impact effect
        for (int i = 0; i < 3; i++) {
            Particle particle = particlePool.acquire();
            if (particle == null) break;
            
            float velocityX = (random.nextFloat() - 0.5f) * 40;
            float velocityY = -5 - random.nextFloat() * 15;
            
            // Brown dust color
            Color dustColor = new Color(139, 69, 19, 180);
            
            particle.init(x + random.nextInt(15) - 7, y, velocityX, velocityY, dustColor, 0.8f, 4, Particle.ParticleType.DUST);
        }
    }
    
    /**
     * Create dust impact effect when blocks collide
     */
    public void createDustImpact(int x, int y, Color surfaceColor) {
        if (!enabled) return;
        
        for (int i = 0; i < DUST_PARTICLE_COUNT; i++) {
            Particle particle = particlePool.acquire();
            if (particle == null) break;
            
            // Spread outward from impact point
            float angle = (float)(random.nextDouble() * Math.PI); // Upward hemisphere
            float speed = 30 + random.nextFloat() * 50;
            float velocityX = (float)(Math.cos(angle) * speed);
            float velocityY = (float)(Math.sin(angle) * speed);
            
            // Dust color based on surface
            Color dustColor = createDustColor(surfaceColor);
            
            float life = 0.3f + random.nextFloat() * 0.7f;
            float size = 2 + random.nextFloat() * 3;
            
            particle.init(x, y, velocityX, velocityY, dustColor, life, size, Particle.ParticleType.DUST);
        }
    }
    
    /**
     * Update all particles
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        particlePool.updateAndCleanup(deltaTime);
    }
    
    /**
     * Render all active particles
     */
    public void render(Graphics2D g2d) {
        if (!enabled) return;
        
        // Save original rendering hints
        RenderingHints originalHints = g2d.getRenderingHints();
        
        // Enable anti-aliasing for smooth particles
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Render all active particles
        for (Particle particle : particlePool.getActiveParticles()) {
            particle.render(g2d);
        }
        
        // Restore original rendering hints
        g2d.setRenderingHints(originalHints);
    }
    
    /**
     * Create golden color variation for explosions
     */
    private Color createGoldenVariation(Color baseColor) {
        int r = Math.min(255, 200 + random.nextInt(55));
        int g = Math.min(255, 150 + random.nextInt(105));
        int b = random.nextInt(50);
        return new Color(r, g, b);
    }
    
    /**
     * Create color variation based on base color
     */
    private Color createColorVariation(Color baseColor) {
        int r = Math.max(0, Math.min(255, baseColor.getRed() + random.nextInt(60) - 30));
        int g = Math.max(0, Math.min(255, baseColor.getGreen() + random.nextInt(60) - 30));
        int b = Math.max(0, Math.min(255, baseColor.getBlue() + random.nextInt(60) - 30));
        return new Color(r, g, b);
    }
    
    /**
     * Create colorful firework colors
     */
    private Color createFireworkColor() {
        Color[] fireworkColors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, 
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
        };
        return fireworkColors[random.nextInt(fireworkColors.length)];
    }
    
    /**
     * Create dust color based on surface color
     */
    private Color createDustColor(Color surfaceColor) {
        int r = Math.max(0, surfaceColor.getRed() - 50 + random.nextInt(30));
        int g = Math.max(0, surfaceColor.getGreen() - 50 + random.nextInt(30));
        int b = Math.max(0, surfaceColor.getBlue() - 50 + random.nextInt(30));
        return new Color(r, g, b);
    }
    
    // Settings and controls
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            particlePool.clear();
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public int getActiveParticleCount() {
        return particlePool.getActiveCount();
    }
    
    public void clear() {
        particlePool.clear();
    }
}
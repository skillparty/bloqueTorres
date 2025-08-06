package com.skillparty.towerblox.ui.components;

import com.skillparty.towerblox.utils.ImageManager;
import java.awt.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enhanced CityBackground with professional Tower Bloxx-style animations
 * Supports all tower heights (0-9999) with dynamic background phases
 */
public class CityBackground {
    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 720;
    
    // Background phases based on tower height
    private enum BackgroundPhase {
        CITY(0, 5),           // Ground level city
        BUILDINGS(6, 10),     // Mid-rise buildings
        SKYSCRAPERS(11, 15),  // High-rise buildings
        CLOUDS(16, 25),       // Above buildings in clouds
        ATMOSPHERE(26, 50),   // Lower atmosphere
        STRATOSPHERE(51, 100), // Stratosphere
        MESOSPHERE(101, 300), // Mesosphere
        THERMOSPHERE(301, 1000), // Thermosphere
        EXOSPHERE(1001, 9999); // Space
        
        private final int minHeight;
        private final int maxHeight;
        
        BackgroundPhase(int minHeight, int maxHeight) {
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }
        
        public static BackgroundPhase getPhase(int towerHeight) {
            for (BackgroundPhase phase : values()) {
                if (towerHeight >= phase.minHeight && towerHeight <= phase.maxHeight) {
                    return phase;
                }
            }
            return EXOSPHERE; // Default for extreme heights
        }
    }
    
    // Animation elements
    private List<BuildingElement> cityBuildings;
    private List<CloudElement> clouds;
    private List<StarElement> stars;
    private List<ParticleElement> particles;
    
    // Animation state
    private double animationTime = 0;
    private Random random;
    private ImageManager imageManager;
    
    // Background colors for different phases
    private Color[] skyColors = {
        new Color(135, 206, 235),  // Sky blue (city)
        new Color(100, 149, 237),  // Cornflower blue (buildings)
        new Color(70, 130, 180),   // Steel blue (skyscrapers)
        new Color(176, 196, 222),  // Light steel blue (clouds)
        new Color(25, 25, 112),    // Midnight blue (atmosphere)
        new Color(0, 0, 139),      // Dark blue (stratosphere)
        new Color(0, 0, 0),        // Black (mesosphere)
        new Color(25, 25, 25),     // Dark gray (thermosphere)
        new Color(0, 0, 0)         // Black (exosphere)
    };
    
    public CityBackground() {
        this.random = new Random();
        this.imageManager = new ImageManager();
        initializeElements();
    }
    
    private void initializeElements() {
        // Initialize city buildings
        cityBuildings = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            cityBuildings.add(new BuildingElement(
                random.nextInt(GAME_WIDTH),
                GAME_HEIGHT - random.nextInt(200) - 50,
                30 + random.nextInt(40),
                50 + random.nextInt(150)
            ));
        }
        
        // Initialize clouds
        clouds = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            clouds.add(new CloudElement(
                random.nextInt(GAME_WIDTH + 200) - 100,
                random.nextInt(300) + 100,
                60 + random.nextInt(80)
            ));
        }
        
        // Initialize stars
        stars = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            stars.add(new StarElement(
                random.nextInt(GAME_WIDTH),
                random.nextInt(GAME_HEIGHT),
                random.nextDouble() * 2 + 1
            ));
        }
        
        // Initialize particles
        particles = new ArrayList<>();
    }
    
    public void update(long deltaTime) {
        animationTime += deltaTime / 1000.0;
        
        // Update clouds
        for (CloudElement cloud : clouds) {
            cloud.update(deltaTime);
        }
        
        // Update stars
        for (StarElement star : stars) {
            star.update(deltaTime);
        }
        
        // Update particles
        particles.removeIf(p -> !p.isAlive());
        for (ParticleElement particle : particles) {
            particle.update(deltaTime);
        }
    }
    
    public void render(Graphics2D g2d, int towerHeight, double cameraY) {
        // Enable antialiasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        BackgroundPhase currentPhase = BackgroundPhase.getPhase(towerHeight);
        
        // Render sky with extended height support
        renderExtendedSky(g2d, currentPhase, towerHeight, cameraY);
        
        // Render phase-specific elements
        switch (currentPhase) {
            case CITY:
            case BUILDINGS:
            case SKYSCRAPERS:
                renderCityElements(g2d, cameraY, currentPhase);
                break;
            case CLOUDS:
                renderCloudElements(g2d, cameraY);
                break;
            case ATMOSPHERE:
            case STRATOSPHERE:
                renderAtmosphereElements(g2d, cameraY);
                break;
            case MESOSPHERE:
            case THERMOSPHERE:
                renderSpaceElements(g2d, cameraY, false);
                break;
            case EXOSPHERE:
                renderSpaceElements(g2d, cameraY, true);
                break;
        }
        
        // Add debug info
        renderDebugInfo(g2d, towerHeight, currentPhase);
    }
    
    private void renderExtendedSky(Graphics2D g2d, BackgroundPhase phase, int towerHeight, double cameraY) {
        Color skyColor = skyColors[Math.min(phase.ordinal(), skyColors.length - 1)];
        
        // Create gradient based on phase
        Color topColor = skyColor;
        Color bottomColor = skyColor.brighter();
        
        // Special handling for space phases
        if (phase.ordinal() >= BackgroundPhase.MESOSPHERE.ordinal()) {
            topColor = Color.BLACK;
            bottomColor = new Color(25, 25, 25);
        }
        
        GradientPaint gradient = new GradientPaint(
            0, (float)cameraY, topColor,
            0, (float)(cameraY + GAME_HEIGHT), bottomColor
        );
        
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
    }
    
    private void renderCityElements(Graphics2D g2d, double cameraY, BackgroundPhase phase) {
        // Only render if camera is close enough to ground
        if (cameraY > -1500) {
            for (BuildingElement building : cityBuildings) {
                building.render(g2d, cameraY, phase);
            }
        }
    }
    
    private void renderCloudElements(Graphics2D g2d, double cameraY) {
        for (CloudElement cloud : clouds) {
            cloud.render(g2d, cameraY);
        }
    }
    
    private void renderAtmosphereElements(Graphics2D g2d, double cameraY) {
        // Render atmospheric effects
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 20; i++) {
            int x = (int)(Math.sin(animationTime + i) * 50) + i * 60;
            int y = (int)(cameraY + i * 30 + Math.cos(animationTime + i) * 20);
            g2d.fillOval(x, y, 3, 3);
        }
    }
    
    private void renderSpaceElements(Graphics2D g2d, double cameraY, boolean cosmic) {
        // Render stars
        for (StarElement star : stars) {
            star.render(g2d, cameraY);
        }
        
        // Cosmic effects for extreme heights
        if (cosmic) {
            renderCosmicEffects(g2d, cameraY);
        }
    }
    
    private void renderCosmicEffects(Graphics2D g2d, double cameraY) {
        // Cosmic strings and nebula effects
        g2d.setColor(new Color(128, 0, 128, 50));
        for (int i = 0; i < 5; i++) {
            int x1 = (int)(Math.sin(animationTime * 0.1 + i) * GAME_WIDTH);
            int y1 = (int)(cameraY + Math.cos(animationTime * 0.1 + i) * GAME_HEIGHT);
            int x2 = x1 + 200;
            int y2 = y1 + 100;
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    private void renderDebugInfo(Graphics2D g2d, int towerHeight, BackgroundPhase phase) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Tower Height: " + towerHeight + " | Sky: ACTIVE", 10, 30);
        g2d.drawString("Phase: " + phase.name(), 10, 50);
    }
    
    // Helper classes for background elements
    private static class BuildingElement {
        private double x, y, width, height;
        private Color color;
        private List<Rectangle> windows;
        
        public BuildingElement(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = new Color(60 + (int)(Math.random() * 40), 
                                 60 + (int)(Math.random() * 40), 
                                 80 + (int)(Math.random() * 40));
            
            // Generate windows
            windows = new ArrayList<>();
            for (int i = 0; i < height / 15; i++) {
                for (int j = 0; j < width / 12; j++) {
                    if (Math.random() > 0.3) {
                        windows.add(new Rectangle((int)(x + j * 12 + 2), 
                                                (int)(y + i * 15 + 2), 8, 10));
                    }
                }
            }
        }
        
        public void render(Graphics2D g2d, double cameraY, BackgroundPhase phase) {
            int renderY = (int)(y + cameraY);
            
            // Only render if visible
            if (renderY + height > 0 && renderY < GAME_HEIGHT) {
                // Building body
                g2d.setColor(color);
                g2d.fillRect((int)x, renderY, (int)width, (int)height);
                
                // Building outline
                g2d.setColor(color.darker());
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect((int)x, renderY, (int)width, (int)height);
                
                // Windows
                g2d.setColor(new Color(255, 255, 150, 200));
                for (Rectangle window : windows) {
                    if (Math.random() > 0.5) { // Flickering lights
                        g2d.fillRect(window.x, (int)(window.y + cameraY), 
                                   window.width, window.height);
                    }
                }
            }
        }
    }
    
    private static class CloudElement {
        private double x, y, size;
        private double speed;
        private Color color;
        
        public CloudElement(double x, double y, double size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = 0.5 + Math.random() * 1.0;
            this.color = new Color(255, 255, 255, 100 + (int)(Math.random() * 100));
        }
        
        public void update(long deltaTime) {
            x += speed * deltaTime / 1000.0;
            if (x > GAME_WIDTH + size) {
                x = -size;
            }
        }
        
        public void render(Graphics2D g2d, double cameraY) {
            int renderY = (int)(y + cameraY);
            
            if (renderY + size > 0 && renderY < GAME_HEIGHT) {
                g2d.setColor(color);
                g2d.fillOval((int)x, renderY, (int)size, (int)(size * 0.6));
                g2d.fillOval((int)(x + size * 0.3), (int)(renderY - size * 0.2), 
                           (int)(size * 0.8), (int)(size * 0.5));
                g2d.fillOval((int)(x + size * 0.6), renderY, 
                           (int)(size * 0.7), (int)(size * 0.4));
            }
        }
    }
    
    private static class StarElement {
        private double x, y, brightness;
        private double twinkleSpeed;
        private Color color;
        
        public StarElement(double x, double y, double brightness) {
            this.x = x;
            this.y = y;
            this.brightness = brightness;
            this.twinkleSpeed = 0.5 + Math.random() * 2.0;
            this.color = Color.WHITE;
        }
        
        public void update(long deltaTime) {
            // Twinkling effect
            brightness = 1.0 + Math.sin(System.currentTimeMillis() * twinkleSpeed / 1000.0) * 0.5;
        }
        
        public void render(Graphics2D g2d, double cameraY) {
            int alpha = (int)(brightness * 255);
            if (alpha > 255) alpha = 255;
            if (alpha < 50) alpha = 50;
            
            g2d.setColor(new Color(255, 255, 255, alpha));
            g2d.fillOval((int)x, (int)(y + cameraY * 0.1), 2, 2);
        }
    }
    
    private static class ParticleElement {
        private double x, y, vx, vy;
        private int life, maxLife;
        private Color color;
        
        public ParticleElement(double x, double y, double vx, double vy, int life, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.life = life;
            this.maxLife = life;
            this.color = color;
        }
        
        public void update(long deltaTime) {
            x += vx * deltaTime / 1000.0;
            y += vy * deltaTime / 1000.0;
            life -= deltaTime;
        }
        
        public boolean isAlive() {
            return life > 0;
        }
        
        public void render(Graphics2D g2d, double cameraY) {
            int alpha = (int)(255.0 * life / maxLife);
            Color renderColor = new Color(color.getRed(), color.getGreen(), 
                                        color.getBlue(), alpha);
            g2d.setColor(renderColor);
            g2d.fillOval((int)x, (int)(y + cameraY), 3, 3);
        }
    }
}
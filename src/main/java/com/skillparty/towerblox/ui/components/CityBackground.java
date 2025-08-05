package com.skillparty.towerblox.ui.components;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Dynamic city background that changes as the tower grows
 */
public class CityBackground {
    private List<Building> backgroundBuildings;
    private List<Cloud> clouds;
    private List<ConstructionElement> constructionElements;
    private Random random;
    private long startTime;
    private int gameWidth;
    private int gameHeight;
    private int groundLevel;
    
    // Animation states based on tower height
    private enum SkyState {
        DAY, SUNSET, NIGHT, DAWN
    }
    
    // Animation phases that change as tower grows (activated after level 11)
    private enum AnimationPhase {
        GROUND_LEVEL,    // 0-11: Basic construction site
        LOW_RISE,        // 12-20: City life, traffic
        MID_RISE,        // 21-30: Above traffic, birds flying
        HIGH_RISE,       // 31-40: Cloud level, weather effects
        SKYSCRAPER,      // 41-50: Above clouds, atmospheric effects
        STRATOSPHERE,    // 51-70: Space-like effects, satellites
        THERMOSPHERE,    // 71-90: Extreme altitude, space station
        EXOSPHERE        // 91+: Edge of space, cosmic effects
    }
    
    // Background building class
    private static class Building {
        int x, y, width, height;
        Color baseColor;
        List<Window> windows;
        boolean hasAntenna;
        
        Building(int x, int y, int width, int height, Color baseColor) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.baseColor = baseColor;
            this.windows = new ArrayList<>();
            this.hasAntenna = Math.random() > 0.7;
            
            // Generate windows
            generateWindows();
        }
        
        private void generateWindows() {
            int windowWidth = 8;
            int windowHeight = 12;
            int spacing = 4;
            
            for (int row = 0; row < height / (windowHeight + spacing); row++) {
                for (int col = 0; col < width / (windowWidth + spacing); col++) {
                    if (Math.random() > 0.3) { // 70% chance of window
                        int wx = x + col * (windowWidth + spacing) + spacing;
                        int wy = y + row * (windowHeight + spacing) + spacing;
                        boolean isLit = Math.random() > 0.6;
                        windows.add(new Window(wx, wy, windowWidth, windowHeight, isLit));
                    }
                }
            }
        }
        
        void render(Graphics2D g2d, float timeOfDay) {
            // Building body with gradient
            GradientPaint gradient = new GradientPaint(
                x, y, baseColor.brighter(),
                x + width, y + height, baseColor.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillRect(x, y, width, height);
            
            // Building outline
            g2d.setColor(baseColor.darker().darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x, y, width, height);
            
            // Render windows
            for (Window window : windows) {
                window.render(g2d, timeOfDay);
            }
            
            // Antenna
            if (hasAntenna) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(x + width/2, y, x + width/2, y - 20);
                g2d.fillOval(x + width/2 - 3, y - 25, 6, 6);
            }
        }
    }
    
    // Window class for buildings
    private static class Window {
        int x, y, width, height;
        boolean isLit;
        float flickerTimer;
        
        Window(int x, int y, int width, int height, boolean isLit) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.isLit = isLit;
            this.flickerTimer = (float)(Math.random() * 100);
        }
        
        void render(Graphics2D g2d, float timeOfDay) {
            flickerTimer += 0.1f;
            
            // Window frame
            g2d.setColor(new Color(40, 40, 40));
            g2d.fillRect(x, y, width, height);
            
            // Window light
            if (isLit && (timeOfDay > 0.6 || timeOfDay < 0.2)) {
                float flicker = (float)(Math.sin(flickerTimer) * 0.1 + 0.9);
                Color lightColor = new Color(255, 255, 150, (int)(200 * flicker));
                g2d.setColor(lightColor);
                g2d.fillRect(x + 1, y + 1, width - 2, height - 2);
            } else {
                // Daytime reflection
                g2d.setColor(new Color(150, 200, 255, 100));
                g2d.fillRect(x + 1, y + 1, width - 2, height - 2);
            }
            
            // Window cross
            g2d.setColor(new Color(60, 60, 60));
            g2d.drawLine(x + width/2, y, x + width/2, y + height);
            g2d.drawLine(x, y + height/2, x + width, y + height/2);
        }
    }
    
    // Cloud class for sky animation
    private static class Cloud {
        float x, y, width, height, speed;
        Color color;
        
        Cloud(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = (float)(Math.random() * 0.5 + 0.2);
            this.color = new Color(255, 255, 255, 180);
        }
        
        void update() {
            x += speed;
            if (x > 900) {
                x = -width;
                y = (float)(Math.random() * 200 + 50);
            }
        }
        
        void render(Graphics2D g2d, float timeOfDay) {
            // Adjust cloud color based on time of day
            Color cloudColor = color;
            if (timeOfDay > 0.4 && timeOfDay < 0.6) {
                // Sunset clouds
                cloudColor = new Color(255, 180, 120, 150);
            } else if (timeOfDay > 0.6) {
                // Night clouds
                cloudColor = new Color(100, 100, 120, 100);
            }
            
            g2d.setColor(cloudColor);
            
            // Draw cloud as multiple overlapping circles
            int numCircles = 5;
            for (int i = 0; i < numCircles; i++) {
                float circleX = x + (width / numCircles) * i;
                float circleY = y + (float)(Math.sin(i) * height * 0.2);
                float circleSize = width / numCircles * 1.5f;
                g2d.fillOval((int)circleX, (int)circleY, (int)circleSize, (int)(height * 0.8));
            }
        }
    }
    
    // Construction elements (cranes, scaffolding, etc.)
    private static class ConstructionElement {
        int x, y;
        String type;
        float animationPhase;
        
        ConstructionElement(int x, int y, String type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.animationPhase = (float)(Math.random() * Math.PI * 2);
        }
        
        void update() {
            animationPhase += 0.02f;
        }
        
        void render(Graphics2D g2d) {
            g2d.setStroke(new BasicStroke(3));
            
            switch (type) {
                case "crane":
                    renderCrane(g2d);
                    break;
                case "scaffolding":
                    renderScaffolding(g2d);
                    break;
                case "truck":
                    renderTruck(g2d);
                    break;
            }
        }
        
        private void renderCrane(Graphics2D g2d) {
            // Crane mast
            g2d.setColor(new Color(200, 200, 0));
            g2d.drawLine(x, y, x, y - 100);
            
            // Crane arm
            float armOffset = (float)(Math.sin(animationPhase) * 10);
            g2d.drawLine(x, y - 80, x + 60 + (int)armOffset, y - 80);
            g2d.drawLine(x, y - 80, x - 30, y - 80);
            
            // Hook
            g2d.setColor(Color.GRAY);
            g2d.drawLine(x + 50 + (int)armOffset, y - 80, x + 50 + (int)armOffset, y - 60);
            g2d.fillRect(x + 48 + (int)armOffset, y - 65, 4, 8);
        }
        
        private void renderScaffolding(Graphics2D g2d) {
            g2d.setColor(new Color(150, 150, 150));
            
            // Vertical poles
            for (int i = 0; i < 3; i++) {
                g2d.drawLine(x + i * 20, y, x + i * 20, y - 60);
            }
            
            // Horizontal bars
            for (int i = 0; i < 4; i++) {
                g2d.drawLine(x, y - i * 15, x + 40, y - i * 15);
            }
        }
        
        private void renderTruck(Graphics2D g2d) {
            // Truck body
            g2d.setColor(new Color(255, 165, 0));
            g2d.fillRect(x, y - 15, 40, 15);
            
            // Truck cab
            g2d.setColor(new Color(200, 130, 0));
            g2d.fillRect(x + 30, y - 25, 15, 25);
            
            // Wheels
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x + 5, y - 8, 8, 8);
            g2d.fillOval(x + 30, y - 8, 8, 8);
        }
    }
    
    public CityBackground(int gameWidth, int gameHeight, int groundLevel) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.groundLevel = groundLevel;
        this.random = new Random();
        this.startTime = System.currentTimeMillis();
        this.backgroundBuildings = new ArrayList<>();
        this.clouds = new ArrayList<>();
        this.constructionElements = new ArrayList<>();
        
        generateCityscape();
        generateClouds();
        generateConstructionSite();
    }
    
    private void generateCityscape() {
        // Generate background buildings
        Color[] buildingColors = {
            new Color(100, 100, 120),
            new Color(120, 100, 100),
            new Color(100, 120, 100),
            new Color(110, 110, 110),
            new Color(90, 90, 110),
            new Color(120, 110, 90)
        };
        
        int currentX = -50;
        while (currentX < gameWidth + 100) {
            int width = random.nextInt(60) + 40;
            int height = random.nextInt(200) + 100;
            Color color = buildingColors[random.nextInt(buildingColors.length)];
            
            backgroundBuildings.add(new Building(currentX, groundLevel - height, width, height, color));
            currentX += width + random.nextInt(20) + 5;
        }
    }
    
    private void generateClouds() {
        for (int i = 0; i < 8; i++) {
            float x = (float)(Math.random() * gameWidth);
            float y = (float)(Math.random() * 200 + 50);
            float width = (float)(Math.random() * 80 + 40);
            float height = (float)(Math.random() * 30 + 20);
            clouds.add(new Cloud(x, y, width, height));
        }
    }
    
    private void generateConstructionSite() {
        // Add construction elements
        constructionElements.add(new ConstructionElement(100, groundLevel, "crane"));
        constructionElements.add(new ConstructionElement(650, groundLevel, "scaffolding"));
        constructionElements.add(new ConstructionElement(200, groundLevel, "truck"));
    }
    
    public void update() {
        // Update clouds
        for (Cloud cloud : clouds) {
            cloud.update();
        }
        
        // Update construction elements
        for (ConstructionElement element : constructionElements) {
            element.update();
        }
    }
    
    public void render(Graphics2D g2d, int towerHeight) {
        render(g2d, towerHeight, 0);
    }
    
    public void render(Graphics2D g2d, int towerHeight, double cameraY) {
        // Calculate time of day and animation phase based on tower height
        float timeOfDay = (towerHeight % 40) / 40.0f; // Cycle every 40 blocks
        AnimationPhase phase = getAnimationPhase(towerHeight);
        
        // Render sky gradient with extended height for camera movement
        renderSky(g2d, timeOfDay, cameraY);
        
        // Render distant mountains/hills
        renderMountains(g2d, timeOfDay, cameraY);
        
        // Render clouds (adjust position based on camera)
        for (Cloud cloud : clouds) {
            cloud.render(g2d, timeOfDay);
        }
        
        // DEFINITIVE FIX: Always render background elements with proper scaling
        // Render background buildings (always visible, scaled by distance)
        for (Building building : backgroundBuildings) {
            building.render(g2d, timeOfDay);
        }
        
        // Render construction site (visible when camera is not too high)
        if (cameraY < 400) {
            for (ConstructionElement element : constructionElements) {
                element.render(g2d);
            }
        }
        
        // Always render ground (extended for high cameras)
        renderGround(g2d);
        
        // Add atmospheric effects based on height
        renderAtmosphericEffects(g2d, towerHeight);
        
        // ENHANCED: Always render dynamic animations for heights >= 11
        if (towerHeight >= 11) {
            renderDynamicAnimations(g2d, phase, towerHeight, timeOfDay);
        }
        
        // ENHANCED: Render progressive altitude effects
        if (towerHeight >= 15) {
            renderProgressiveAltitudeEffects(g2d, towerHeight, timeOfDay);
        }
        
        // ENHANCED: Render space effects for very high towers
        if (towerHeight >= 25) {
            renderSpaceEffects(g2d, towerHeight, timeOfDay);
        }
    }
    
    private void renderSky(Graphics2D g2d, float timeOfDay) {
        renderSky(g2d, timeOfDay, 0);
    }
    
    private void renderSky(Graphics2D g2d, float timeOfDay, double cameraY) {
        Color skyTop, skyBottom;
        
        if (timeOfDay < 0.2) {
            // Dawn
            skyTop = new Color(135, 206, 250);
            skyBottom = new Color(255, 218, 185);
        } else if (timeOfDay < 0.4) {
            // Day
            skyTop = new Color(135, 206, 235);
            skyBottom = new Color(176, 224, 230);
        } else if (timeOfDay < 0.6) {
            // Sunset
            skyTop = new Color(255, 94, 77);
            skyBottom = new Color(255, 154, 0);
        } else {
            // Night
            skyTop = new Color(25, 25, 112);
            skyBottom = new Color(72, 61, 139);
        }
        
        // FIXED: Always render full sky regardless of camera position
        int renderHeight = gameHeight + (int)Math.abs(cameraY) + 200; // Extended sky for high cameras
        
        GradientPaint skyGradient = new GradientPaint(0, 0, skyTop, 0, renderHeight, skyBottom);
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, gameWidth, renderHeight);
        
        // Add stars at night
        if (timeOfDay > 0.7) {
            renderStars(g2d);
        }
    }
    
    private void renderStars(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        Random starRandom = new Random(12345); // Fixed seed for consistent stars
        
        for (int i = 0; i < 50; i++) {
            int x = starRandom.nextInt(gameWidth);
            int y = starRandom.nextInt(groundLevel / 2);
            int size = starRandom.nextInt(2) + 1;
            
            // Twinkling effect
            float twinkle = (float)(Math.sin(System.currentTimeMillis() * 0.01 + i) * 0.5 + 0.5);
            g2d.setColor(new Color(255, 255, 255, (int)(255 * twinkle)));
            g2d.fillOval(x, y, size, size);
        }
    }
    
    private void renderMountains(Graphics2D g2d, float timeOfDay) {
        renderMountains(g2d, timeOfDay, 0);
    }
    
    private void renderMountains(Graphics2D g2d, float timeOfDay, double cameraY) {
        Color mountainColor = new Color(100, 100, 120, 150);
        if (timeOfDay > 0.6) {
            mountainColor = new Color(50, 50, 80, 120);
        }
        
        g2d.setColor(mountainColor);
        
        // Draw mountain silhouette
        int[] xPoints = {0, 150, 300, 450, 600, 750, gameWidth};
        int[] yPoints = {groundLevel, groundLevel - 80, groundLevel - 120, groundLevel - 60, groundLevel - 100, groundLevel - 40, groundLevel};
        
        g2d.fillPolygon(xPoints, yPoints, xPoints.length);
    }
    
    private void renderGround(Graphics2D g2d) {
        // Construction site ground
        GradientPaint groundGradient = new GradientPaint(
            0, groundLevel, new Color(139, 69, 19),
            0, gameHeight, new Color(160, 82, 45)
        );
        g2d.setPaint(groundGradient);
        g2d.fillRect(0, groundLevel, gameWidth, gameHeight - groundLevel);
        
        // Add construction details on ground
        g2d.setColor(new Color(105, 105, 105));
        
        // Construction materials scattered around
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(gameWidth);
            int y = groundLevel + random.nextInt(20);
            g2d.fillRect(x, y, 15, 8);
        }
        
        // Construction lines/marks
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(255, 255, 0, 100));
        for (int i = 0; i < gameWidth; i += 100) {
            g2d.drawLine(i, groundLevel, i + 50, groundLevel + 30);
        }
    }
    
    private void renderAtmosphericEffects(Graphics2D g2d, int towerHeight) {
        // Add fog/haze effect at higher altitudes
        if (towerHeight > 20) {
            float fogIntensity = Math.min(0.3f, (towerHeight - 20) * 0.01f);
            g2d.setColor(new Color(255, 255, 255, (int)(fogIntensity * 255)));
            g2d.fillRect(0, 0, gameWidth, groundLevel);
        }
        
        // Add wind effect particles at very high altitudes
        if (towerHeight > 30) {
            renderWindParticles(g2d);
        }
    }
    
    private void renderWindParticles(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 100));
        long time = System.currentTimeMillis();
        
        for (int i = 0; i < 20; i++) {
            float x = (float)((time * 0.1 + i * 50) % (gameWidth + 100)) - 50;
            float y = (float)(Math.sin(time * 0.01 + i) * 100 + 200);
            g2d.fillOval((int)x, (int)y, 3, 1);
        }
    }
    
    /**
     * Determines animation phase based on tower height
     */
    private AnimationPhase getAnimationPhase(int towerHeight) {
        if (towerHeight <= 11) return AnimationPhase.GROUND_LEVEL;
        else if (towerHeight <= 20) return AnimationPhase.LOW_RISE;
        else if (towerHeight <= 30) return AnimationPhase.MID_RISE;
        else if (towerHeight <= 40) return AnimationPhase.HIGH_RISE;
        else if (towerHeight <= 50) return AnimationPhase.SKYSCRAPER;
        else if (towerHeight <= 70) return AnimationPhase.STRATOSPHERE;
        else if (towerHeight <= 90) return AnimationPhase.THERMOSPHERE;
        else return AnimationPhase.EXOSPHERE;
    }
    
    /**
     * Renders progressive altitude effects for towers 15+
     */
    private void renderProgressiveAltitudeEffects(Graphics2D g2d, int towerHeight, float timeOfDay) {
        long time = System.currentTimeMillis();
        
        // Enhanced cloud layer with depth
        for (int layer = 0; layer < 3; layer++) {
            int alpha = 80 - (layer * 20);
            g2d.setColor(new Color(255, 255, 255, alpha));
            
            for (int i = 0; i < 8; i++) {
                float speed = 0.03f + (layer * 0.02f);
                float cloudX = (float)((time * speed + i * 100 + layer * 50) % (gameWidth + 150)) - 75;
                float cloudY = (float)(120 + layer * 40 + Math.sin(time * 0.002 + i + layer) * 20);
                int size = 80 - (layer * 15);
                g2d.fillOval((int)cloudX, (int)cloudY, size, size / 2);
            }
        }
        
        // Flying aircraft at different altitudes
        g2d.setColor(new Color(100, 100, 100));
        for (int i = 0; i < 3; i++) {
            float planeX = (float)((time * 0.12 + i * 250) % (gameWidth + 100)) - 50;
            float planeY = (float)(180 + i * 60 + Math.sin(time * 0.001 + i) * 15);
            
            // Aircraft body
            g2d.fillOval((int)planeX, (int)planeY, 25, 6);
            // Wings
            g2d.fillRect((int)planeX + 8, (int)planeY - 3, 8, 12);
            // Contrail
            g2d.setColor(new Color(255, 255, 255, 60));
            for (int j = 1; j <= 15; j++) {
                g2d.fillOval((int)planeX - j * 4, (int)planeY + 1, 3, 2);
            }
            g2d.setColor(new Color(100, 100, 100));
        }
        
        // Atmospheric shimmer effect
        g2d.setColor(new Color(200, 220, 255, 40));
        for (int i = 0; i < 25; i++) {
            float shimmerX = (float)((time * 0.06 + i * 30) % gameWidth);
            float shimmerY = (float)(80 + Math.sin(time * 0.008 + i) * 60);
            float size = (float)(Math.sin(time * 0.01 + i) * 2 + 3);
            g2d.fillOval((int)shimmerX, (int)shimmerY, (int)size, (int)size);
        }
    }
    
    /**
     * Renders space effects for very high towers (25+)
     */
    private void renderSpaceEffects(Graphics2D g2d, int towerHeight, float timeOfDay) {
        long time = System.currentTimeMillis();
        
        // Enhanced star field
        Random starRandom = new Random(12345);
        for (int i = 0; i < 40; i++) {
            int starX = starRandom.nextInt(gameWidth);
            int starY = starRandom.nextInt(400);
            float twinkle = (float)(Math.sin(time * 0.008 + i * 0.5) * 0.4 + 0.6);
            
            // Different star colors and sizes
            Color starColor = i % 3 == 0 ? new Color(255, 200, 200) :
                             i % 3 == 1 ? new Color(200, 200, 255) :
                                        new Color(255, 255, 200);
            
            g2d.setColor(new Color(starColor.getRed(), starColor.getGreen(), starColor.getBlue(), 
                                 (int)(200 * twinkle)));
            
            int size = starRandom.nextInt(3) + 1;
            g2d.fillOval(starX, starY, size, size);
            
            // Bright stars get cross pattern
            if (size >= 2 && twinkle > 0.8) {
                g2d.drawLine(starX - 2, starY + 1, starX + size + 2, starY + 1);
                g2d.drawLine(starX + 1, starY - 2, starX + 1, starY + size + 2);
            }
        }
        
        // Satellites
        for (int i = 0; i < 2; i++) {
            float orbitRadius = 200 + i * 80;
            float orbitSpeed = 0.0004f + i * 0.0002f;
            float satX = (float)(gameWidth/2 + Math.cos(time * orbitSpeed + i * Math.PI) * orbitRadius);
            float satY = (float)(150 + Math.sin(time * orbitSpeed + i * Math.PI) * 40);
            
            if (satX >= -30 && satX <= gameWidth + 30) {
                // Satellite body
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect((int)satX, (int)satY, 12, 8);
                
                // Solar panels
                g2d.setColor(new Color(50, 50, 150));
                g2d.fillRect((int)satX - 8, (int)satY + 1, 6, 6);
                g2d.fillRect((int)satX + 14, (int)satY + 1, 6, 6);
                
                // Blinking light
                if ((time + i * 1000) % 2000 < 1000) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval((int)satX + 4, (int)satY + 2, 4, 4);
                }
            }
        }
        
        // Aurora effect for extreme heights
        if (towerHeight >= 30) {
            for (int layer = 0; layer < 2; layer++) {
                Color auroraColor = layer == 0 ? new Color(0, 255, 150, 100) :
                                               new Color(255, 100, 255, 80);
                g2d.setColor(auroraColor);
                
                for (int i = 0; i < gameWidth; i += 20) {
                    float auroraHeight = (float)(60 + layer * 40 + Math.sin(time * 0.003 + i * 0.02 + layer) * 80);
                    g2d.fillRect(i, (int)auroraHeight, 15, 100 - layer * 20);
                }
            }
        }
        
        // Meteor shower for very extreme heights
        if (towerHeight >= 35 && Math.random() < 0.02) {
            int meteorX = (int)(Math.random() * gameWidth);
            int meteorY = (int)(Math.random() * 200);
            
            g2d.setColor(new Color(255, 150, 0, 200));
            g2d.fillOval(meteorX, meteorY, 6, 6);
            
            // Meteor trail
            g2d.setColor(new Color(255, 200, 100, 150));
            for (int j = 1; j <= 12; j++) {
                int trailSize = Math.max(1, 5 - j/3);
                g2d.fillOval(meteorX - j * 5, meteorY + j * 3, trailSize, trailSize);
            }
        }
    }
    
    /**
     * Renders dynamic animations based on current phase (activated after level 11)
     */
    private void renderDynamicAnimations(Graphics2D g2d, AnimationPhase phase, int towerHeight, float timeOfDay) {
        long time = System.currentTimeMillis();
        
        switch (phase) {
            case LOW_RISE:
                renderLowRiseAnimations(g2d, time, timeOfDay);
                break;
            case MID_RISE:
                renderMidRiseAnimations(g2d, time);
                break;
            case HIGH_RISE:
                renderHighRiseAnimations(g2d, time, towerHeight);
                break;
            case SKYSCRAPER:
                renderSkyscraperAnimations(g2d, time, towerHeight);
                break;
            case STRATOSPHERE:
                renderStratosphereAnimations(g2d, time, towerHeight);
                break;
            case THERMOSPHERE:
                renderThermosphereAnimations(g2d, time, towerHeight);
                break;
            case EXOSPHERE:
                renderExosphereAnimations(g2d, time, towerHeight);
                break;
            default:
                // GROUND_LEVEL - no additional animations
                break;
        }
    }
    
    /**
     * Low rise animations: Traffic, pedestrians, city life
     */
    private void renderLowRiseAnimations(Graphics2D g2d, long time, float timeOfDay) {
        // Animated traffic lights
        for (int i = 0; i < 3; i++) {
            int lightX = 200 + i * 200;
            int lightY = groundLevel - 50;
            
            // Traffic light pole
            g2d.setColor(Color.GRAY);
            g2d.fillRect(lightX, lightY, 3, 30);
            
            // Traffic light colors (cycling)
            int cycle = (int)((time + i * 2000) / 3000) % 3;
            Color lightColor = cycle == 0 ? Color.RED : cycle == 1 ? Color.YELLOW : Color.GREEN;
            g2d.setColor(lightColor);
            g2d.fillOval(lightX - 3, lightY, 9, 9);
        }
        
        // Moving cars
        for (int i = 0; i < 4; i++) {
            float carX = (float)((time * 0.08 + i * 200) % (gameWidth + 50)) - 25;
            int carY = groundLevel - 15;
            
            // Car body
            g2d.setColor(i % 2 == 0 ? Color.BLUE : Color.RED);
            g2d.fillRect((int)carX, carY, 25, 10);
            
            // Car windows
            g2d.setColor(new Color(150, 200, 255, 150));
            g2d.fillRect((int)carX + 2, carY + 2, 21, 6);
        }
    }
    
    /**
     * Mid rise animations: Birds, helicopters, wind effects
     */
    private void renderMidRiseAnimations(Graphics2D g2d, long time) {
        // Flying birds in formation
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 8; i++) {
            float birdX = (float)(150 + i * 30 + Math.sin(time * 0.002 + i) * 50);
            float birdY = (float)(200 + Math.sin(time * 0.004 + i * 0.5) * 30);
            
            // Simple bird shape (V)
            g2d.drawLine((int)birdX - 3, (int)birdY, (int)birdX, (int)birdY - 2);
            g2d.drawLine((int)birdX, (int)birdY - 2, (int)birdX + 3, (int)birdY);
        }
        
        // Helicopter
        float heliX = (float)(300 + Math.sin(time * 0.001) * 200);
        float heliY = (float)(150 + Math.sin(time * 0.003) * 20);
        
        g2d.setColor(Color.RED);
        g2d.fillOval((int)heliX, (int)heliY, 20, 8);
        
        // Rotating blades
        g2d.setColor(Color.GRAY);
        double bladeAngle = (time * 0.1) % (Math.PI * 2);
        int bladeLength = 15;
        g2d.drawLine((int)heliX + 10, (int)heliY - 5,
                    (int)(heliX + 10 + Math.cos(bladeAngle) * bladeLength),
                    (int)(heliY - 5 + Math.sin(bladeAngle) * 3));
    }
    
    /**
     * High rise animations: Weather effects, lightning, storm clouds
     */
    private void renderHighRiseAnimations(Graphics2D g2d, long time, int towerHeight) {
        // Storm clouds
        g2d.setColor(new Color(70, 70, 90, 180));
        for (int i = 0; i < 4; i++) {
            float cloudX = (float)(i * 200 + Math.sin(time * 0.001 + i) * 30);
            float cloudY = (float)(100 + Math.sin(time * 0.002 + i) * 20);
            g2d.fillOval((int)cloudX, (int)cloudY, 80, 40);
        }
        
        // Lightning effect (random flashes)
        if (Math.random() < 0.02) { // 2% chance per frame
            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.setStroke(new BasicStroke(3));
            int lightningX = random.nextInt(gameWidth);
            g2d.drawLine(lightningX, 50, lightningX + random.nextInt(40) - 20, 150);
            g2d.drawLine(lightningX + random.nextInt(40) - 20, 150, lightningX + random.nextInt(60) - 30, 250);
        }
        
        // Rain effect
        g2d.setColor(new Color(150, 150, 200, 100));
        for (int i = 0; i < 50; i++) {
            float rainX = (float)((time * 0.2 + i * 16) % gameWidth);
            float rainY = (float)((time * 0.3 + i * 12) % groundLevel);
            g2d.drawLine((int)rainX, (int)rainY, (int)rainX - 2, (int)rainY + 8);
        }
    }
    
    /**
     * Skyscraper animations: Above clouds, airplanes, atmospheric effects
     */
    private void renderSkyscraperAnimations(Graphics2D g2d, long time, int towerHeight) {
        // Cloud layer below
        g2d.setColor(new Color(255, 255, 255, 120));
        for (int i = 0; i < gameWidth; i += 50) {
            float cloudHeight = (float)(400 + Math.sin(time * 0.001 + i * 0.01) * 30);
            g2d.fillOval(i, (int)cloudHeight, 60, 20);
        }
        
        // Commercial airplane
        float planeX = (float)((time * 0.15) % (gameWidth + 200)) - 100;
        float planeY = 180;
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)planeX, (int)planeY, 40, 8);
        g2d.fillRect((int)planeX + 35, (int)planeY - 3, 8, 14); // Tail
        
        // Contrails
        g2d.setColor(new Color(255, 255, 255, 80));
        for (int i = 0; i < 20; i++) {
            g2d.fillOval((int)planeX - i * 8, (int)planeY + 2, 6, 2);
        }
    }
    
    /**
     * Stratosphere animations: Space-like effects, satellites, aurora
     */
    private void renderStratosphereAnimations(Graphics2D g2d, long time, int towerHeight) {
        // Enhanced aurora effect with multiple colors - MORE VISIBLE
        for (int layer = 0; layer < 4; layer++) {
            Color auroraColor = layer == 0 ? new Color(0, 255, 150, 120) :
                               layer == 1 ? new Color(255, 100, 255, 100) :
                               layer == 2 ? new Color(100, 150, 255, 80) :
                                          new Color(255, 255, 0, 60);
            g2d.setColor(auroraColor);
            
            for (int i = 0; i < gameWidth; i += 12) {
                float auroraHeight = (float)(60 + layer * 40 + Math.sin(time * 0.004 + i * 0.02 + layer) * 80);
                float auroraWidth = 12 + layer * 4;
                g2d.fillRect(i, (int)auroraHeight, (int)auroraWidth, 140 - layer * 15);
            }
        }
        
        // Multiple satellites with orbital motion - MORE VISIBLE
        for (int i = 0; i < 4; i++) {
            float orbitRadius = 200 + i * 60;
            float orbitSpeed = 0.0004f + i * 0.0002f;
            float satX = (float)(gameWidth/2 + Math.cos(time * orbitSpeed + i * Math.PI * 2/4) * orbitRadius);
            float satY = (float)(120 + Math.sin(time * orbitSpeed + i * Math.PI * 2/4) * 60);
            
            // Render satellite with better visibility
            if (satX >= -30 && satX <= gameWidth + 30) {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect((int)satX, (int)satY, 12, 10);
                
                // Solar panels - brighter
                g2d.setColor(new Color(50, 50, 200));
                g2d.fillRect((int)satX - 10, (int)satY + 2, 8, 6);
                g2d.fillRect((int)satX + 14, (int)satY + 2, 8, 6);
                
                // Antenna
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine((int)satX + 6, (int)satY, (int)satX + 6, (int)satY - 12);
                
                // Blinking light - more frequent
                if ((time + i * 800) % 1600 < 800) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval((int)satX + 4, (int)satY + 2, 6, 6);
                }
            }
        }
        
        // Enhanced star field with different sizes and colors - MORE STARS
        Random starRandom = new Random(54321); // Fixed seed
        for (int i = 0; i < 80; i++) {
            int starX = starRandom.nextInt(gameWidth);
            int starY = starRandom.nextInt(400);
            int starSize = starRandom.nextInt(4) + 1;
            
            // Different star colors
            Color starColor = i % 5 == 0 ? new Color(255, 200, 200) :
                             i % 5 == 1 ? new Color(200, 200, 255) :
                             i % 5 == 2 ? new Color(255, 255, 200) :
                             i % 5 == 3 ? new Color(200, 255, 200) :
                                        Color.WHITE;
            
            float twinkle = (float)(Math.sin(time * 0.01 + i * 0.3) * 0.5 + 0.5);
            g2d.setColor(new Color(starColor.getRed(), starColor.getGreen(), starColor.getBlue(), 
                                 (int)(255 * twinkle)));
            g2d.fillOval(starX, starY, starSize, starSize);
            
            // Add cross pattern for brighter stars
            if (starSize >= 2 && twinkle > 0.7) {
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(starX - 3, starY + starSize/2, starX + starSize + 3, starY + starSize/2);
                g2d.drawLine(starX + starSize/2, starY - 3, starX + starSize/2, starY + starSize + 3);
            }
        }
        
        // Space debris/meteors - more frequent
        if (Math.random() < 0.01) { // 1% chance per frame
            int meteorX = random.nextInt(gameWidth);
            int meteorY = random.nextInt(300);
            
            g2d.setColor(new Color(255, 150, 0, 200));
            g2d.fillOval(meteorX, meteorY, 6, 6);
            
            // Meteor trail
            g2d.setColor(new Color(255, 200, 100, 150));
            for (int j = 1; j <= 10; j++) {
                int trailSize = Math.max(1, 4 - j/3);
                g2d.fillOval(meteorX - j * 4, meteorY + j * 3, trailSize, trailSize);
            }
        }
        
        // Earth curvature effect at extreme heights - more visible
        if (towerHeight >= 55) {
            g2d.setColor(new Color(100, 150, 255, 50));
            g2d.fillArc(-300, groundLevel + 150, gameWidth + 600, 500, 0, 180);
        }
        
        // Space station at very high altitudes
        if (towerHeight >= 65) {
            float stationX = (float)(gameWidth * 0.7 + Math.sin(time * 0.0005) * 100);
            float stationY = 100;
            
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect((int)stationX, (int)stationY, 40, 15);
            g2d.fillRect((int)stationX + 10, (int)stationY - 8, 20, 8);
            
            // Solar arrays
            g2d.setColor(new Color(0, 0, 150));
            g2d.fillRect((int)stationX - 15, (int)stationY + 3, 12, 8);
            g2d.fillRect((int)stationX + 43, (int)stationY + 3, 12, 8);
            
            // Lights
            if ((time % 3000) < 1500) {
                g2d.setColor(Color.YELLOW);
                g2d.fillOval((int)stationX + 18, (int)stationY + 5, 4, 4);
            }
        }
    }
    
    /**
     * Thermosphere animations: Space stations, extreme aurora, cosmic radiation
     */
    private void renderThermosphereAnimations(Graphics2D g2d, long time, int towerHeight) {
        // Intense cosmic aurora with plasma effects
        for (int layer = 0; layer < 4; layer++) {
            Color plasmaColor = layer == 0 ? new Color(255, 0, 255, 100) :
                               layer == 1 ? new Color(0, 255, 255, 80) :
                               layer == 2 ? new Color(255, 255, 0, 60) :
                                          new Color(255, 100, 0, 40);
            g2d.setColor(plasmaColor);
            
            for (int i = 0; i < gameWidth; i += 10) {
                float plasmaHeight = (float)(50 + layer * 25 + Math.sin(time * 0.005 + i * 0.05 + layer * 2) * 80);
                float plasmaIntensity = (float)(Math.sin(time * 0.008 + i * 0.02) * 0.5 + 0.5);
                int alpha = (int)(plasmaColor.getAlpha() * plasmaIntensity);
                
                g2d.setColor(new Color(plasmaColor.getRed(), plasmaColor.getGreen(), 
                                     plasmaColor.getBlue(), alpha));
                g2d.fillRect(i, (int)plasmaHeight, 8, (int)(150 * plasmaIntensity));
            }
        }
        
        // International Space Station
        float stationX = (float)(100 + Math.sin(time * 0.0002) * 400);
        float stationY = (float)(120 + Math.sin(time * 0.0003) * 30);
        
        if (stationX >= -50 && stationX <= gameWidth + 50) {
            // Main station body
            g2d.setColor(Color.WHITE);
            g2d.fillRect((int)stationX, (int)stationY, 40, 12);
            
            // Solar panel arrays
            g2d.setColor(new Color(0, 0, 200));
            g2d.fillRect((int)stationX - 20, (int)stationY + 2, 15, 8);
            g2d.fillRect((int)stationX + 45, (int)stationY + 2, 15, 8);
            g2d.fillRect((int)stationX - 20, (int)stationY - 15, 15, 8);
            g2d.fillRect((int)stationX + 45, (int)stationY - 15, 15, 8);
            
            // Communication arrays
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawLine((int)stationX + 20, (int)stationY, (int)stationX + 20, (int)stationY - 25);
            g2d.fillOval((int)stationX + 17, (int)stationY - 28, 6, 6);
            
            // Blinking lights
            if ((time / 500) % 2 == 0) {
                g2d.setColor(Color.GREEN);
                g2d.fillOval((int)stationX + 5, (int)stationY + 3, 3, 3);
                g2d.fillOval((int)stationX + 32, (int)stationY + 3, 3, 3);
            }
        }
        
        // Cosmic radiation particles
        g2d.setColor(new Color(255, 255, 255, 150));
        for (int i = 0; i < 30; i++) {
            float radX = (float)((time * 0.3 + i * 27) % (gameWidth + 100)) - 50;
            float radY = (float)(Math.sin(time * 0.01 + i) * 200 + 150);
            g2d.fillOval((int)radX, (int)radY, 2, 8);
        }
        
        // Extreme star density
        Random starRandom = new Random(98765);
        for (int i = 0; i < 100; i++) {
            int starX = starRandom.nextInt(gameWidth);
            int starY = starRandom.nextInt(400);
            float intensity = (float)(Math.sin(time * 0.01 + i * 0.3) * 0.3 + 0.7);
            
            g2d.setColor(new Color(255, 255, 255, (int)(255 * intensity)));
            g2d.fillOval(starX, starY, 1, 1);
        }
        
        System.out.println("🛰️ THERMOSPHERE ACTIVE - Space station and cosmic effects at height " + towerHeight);
    }
    
    /**
     * Exosphere animations: Edge of space, cosmic phenomena, galaxy views
     */
    private void renderExosphereAnimations(Graphics2D g2d, long time, int towerHeight) {
        // Deep space background with nebula effects
        for (int i = 0; i < 5; i++) {
            Color nebulaColor = i % 2 == 0 ? new Color(150, 0, 255, 40) : new Color(255, 100, 150, 30);
            g2d.setColor(nebulaColor);
            
            float nebulaX = (float)(i * 160 + Math.sin(time * 0.0001 + i) * 50);
            float nebulaY = (float)(100 + i * 40 + Math.cos(time * 0.0002 + i) * 30);
            
            // Create nebula cloud effect
            for (int j = 0; j < 8; j++) {
                float cloudX = nebulaX + (float)(Math.cos(j * Math.PI / 4) * 40);
                float cloudY = nebulaY + (float)(Math.sin(j * Math.PI / 4) * 25);
                g2d.fillOval((int)cloudX, (int)cloudY, 30, 20);
            }
        }
        
        // Distant galaxy spiral
        g2d.setColor(new Color(255, 200, 255, 80));
        int galaxyCenterX = gameWidth / 2;
        int galaxyCenterY = 200;
        
        for (int arm = 0; arm < 3; arm++) {
            for (int point = 0; point < 20; point++) {
                double angle = (time * 0.0001 + arm * Math.PI * 2 / 3 + point * 0.3);
                double radius = point * 8;
                
                int galaxyX = (int)(galaxyCenterX + Math.cos(angle) * radius);
                int galaxyY = (int)(galaxyCenterY + Math.sin(angle) * radius * 0.3);
                
                float brightness = (float)(Math.sin(time * 0.005 + point) * 0.3 + 0.7);
                g2d.setColor(new Color(255, 200, 255, (int)(80 * brightness)));
                g2d.fillOval(galaxyX, galaxyY, 3, 3);
            }
        }
        
        // Cosmic phenomena - pulsars
        for (int i = 0; i < 3; i++) {
            int pulsarX = 150 + i * 250;
            int pulsarY = 100 + i * 50;
            
            float pulseIntensity = (float)(Math.sin(time * 0.02 + i * Math.PI) * 0.5 + 0.5);
            int pulseSize = (int)(5 + pulseIntensity * 10);
            
            g2d.setColor(new Color(255, 255, 255, (int)(255 * pulseIntensity)));
            g2d.fillOval(pulsarX - pulseSize/2, pulsarY - pulseSize/2, pulseSize, pulseSize);
            
            // Pulsar beams
            if (pulseIntensity > 0.7) {
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(pulsarX, pulsarY - 50, pulsarX, pulsarY + 50);
                g2d.drawLine(pulsarX - 50, pulsarY, pulsarX + 50, pulsarY);
            }
        }
        
        // Extreme star field with different stellar types
        Random cosmicRandom = new Random(13579);
        for (int i = 0; i < 150; i++) {
            int starX = cosmicRandom.nextInt(gameWidth);
            int starY = cosmicRandom.nextInt(500);
            
            // Different stellar types
            Color starColor;
            int starType = i % 5;
            switch (starType) {
                case 0: starColor = new Color(255, 100, 100); break; // Red giant
                case 1: starColor = new Color(100, 100, 255); break; // Blue giant
                case 2: starColor = new Color(255, 255, 100); break; // Yellow star
                case 3: starColor = new Color(255, 200, 150); break; // Orange star
                default: starColor = Color.WHITE; break; // White dwarf
            }
            
            float twinkle = (float)(Math.sin(time * 0.008 + i * 0.7) * 0.4 + 0.6);
            g2d.setColor(new Color(starColor.getRed(), starColor.getGreen(), 
                                 starColor.getBlue(), (int)(255 * twinkle)));
            
            int starSize = starType == 0 || starType == 1 ? 3 : 1; // Giants are bigger
            g2d.fillOval(starX, starY, starSize, starSize);
        }
        
        // Earth as a small blue dot (overview effect)
        if (towerHeight >= 100) {
            g2d.setColor(new Color(100, 150, 255, 200));
            g2d.fillOval(gameWidth - 50, groundLevel + 100, 8, 8);
            
            // Atmosphere glow
            g2d.setColor(new Color(150, 200, 255, 50));
            g2d.fillOval(gameWidth - 55, groundLevel + 95, 18, 18);
        }
        
        System.out.println("🌌 EXOSPHERE ACTIVE - Edge of space effects at height " + towerHeight);
    }
}
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
        // Calculate time of day based on tower height
        float timeOfDay = (towerHeight % 40) / 40.0f; // Cycle every 40 blocks
        
        // Render sky gradient
        renderSky(g2d, timeOfDay, cameraY);
        
        // Render distant mountains/hills
        renderMountains(g2d, timeOfDay, cameraY);
        
        // Render clouds
        for (Cloud cloud : clouds) {
            cloud.render(g2d, timeOfDay);
        }
        
        // Render background buildings
        for (Building building : backgroundBuildings) {
            building.render(g2d, timeOfDay);
        }
        
        // Render construction site
        for (ConstructionElement element : constructionElements) {
            element.render(g2d);
        }
        
        // Render ground with construction details
        renderGround(g2d);
        
        // Add atmospheric effects based on height
        renderAtmosphericEffects(g2d, towerHeight);
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
        
        // Extend rendering area when camera moves up
        int renderHeight = (int)(groundLevel - Math.min(0, cameraY) + 100);
        
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
}
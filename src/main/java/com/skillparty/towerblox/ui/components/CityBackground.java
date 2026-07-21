package com.skillparty.towerblox.ui.components;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Random;

/**
 * Dynamic Parallax City Background - STABLE, INFINITE SCROLL & EPILEPSY SAFE
 * Renders in screen space with parallax offsets to prevent height limit breakage.
 */
public class CityBackground {
    private final int gameWidth;
    private final int gameHeight;
    private final int groundLevel;
    
    // Static pre-generated building skylines for flicker-free rendering
    private final int[] smallBuildingHeights = new int[10];
    private final int[] mediumBuildingHeights = new int[8];
    private final int[] tallBuildingHeights = new int[6];
    private final int[] skyscraperHeights = new int[5];
    
    private final int[] cloudX = new int[8];
    private final int[] cloudY = new int[8];
    private final int[] cloudW = new int[8];
    private final int[] cloudH = new int[8];
    
    private final int[] lightX = new int[40];
    private final int[] lightY = new int[40];
    
    private final int[] starX = new int[60];
    private final int[] starY = new int[60];
    
    public CityBackground(int gameWidth, int gameHeight, int groundLevel) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.groundLevel = groundLevel;
        
        Random random = new Random(42); // Seeded for deterministic background layout
        initializeStaticLayout(random);
    }
    
    private void initializeStaticLayout(Random random) {
        for (int i = 0; i < smallBuildingHeights.length; i++) {
            smallBuildingHeights[i] = 40 + random.nextInt(35);
        }
        for (int i = 0; i < mediumBuildingHeights.length; i++) {
            mediumBuildingHeights[i] = 80 + random.nextInt(55);
        }
        for (int i = 0; i < tallBuildingHeights.length; i++) {
            tallBuildingHeights[i] = 120 + random.nextInt(90);
        }
        for (int i = 0; i < skyscraperHeights.length; i++) {
            skyscraperHeights[i] = 200 + random.nextInt(140);
        }
        for (int i = 0; i < cloudX.length; i++) {
            cloudX[i] = (i * gameWidth / cloudX.length) + random.nextInt(30);
            cloudY[i] = 60 + random.nextInt(200);
            cloudW[i] = 90 + random.nextInt(40);
            cloudH[i] = 30 + random.nextInt(15);
        }
        for (int i = 0; i < lightX.length; i++) {
            lightX[i] = random.nextInt(gameWidth);
            lightY[i] = random.nextInt(gameHeight);
        }
        for (int i = 0; i < starX.length; i++) {
            starX[i] = random.nextInt(gameWidth);
            starY[i] = random.nextInt(gameHeight);
        }
    }
    
    /**
     * Renders screen-space parallax background with seamless gradient & infinite scrolling
     */
    public void render(Graphics2D g2d, int towerHeight, double cameraY) {
        // 1. Sky Gradient based on tower height (Day -> Sunset -> Night -> Stratosphere -> Space)
        renderDynamicSky(g2d, towerHeight);
        
        // 2. Parallax background layers
        double slowParallax = cameraY * 0.15;
        double medParallax = cameraY * 0.35;
        double fastParallax = cameraY * 0.85;
        
        // Stars in higher altitude / night
        if (towerHeight > 25) {
            renderStars(g2d, towerHeight, slowParallax);
        }
        
        // Distant Skyscrapers / Buildings
        if (towerHeight > 10) {
            renderSkyscrapers(g2d, medParallax);
        }
        
        // Mid-rise Buildings
        renderMediumBuildings(g2d, fastParallax);
        
        // Clouds scrolling seamlessly
        renderClouds(g2d, slowParallax);
        
        // Ground Level (scrolls off screen as camera rises)
        double screenGroundY = groundLevel + cameraY;
        if (screenGroundY < gameHeight + 200) {
            g2d.setColor(new Color(34, 139, 34)); // Grass green
            g2d.fillRect(0, (int)screenGroundY, gameWidth, Math.max(200, gameHeight - (int)screenGroundY));
            renderSmallBuildings(g2d, screenGroundY);
        }
    }
    
    private void renderDynamicSky(Graphics2D g2d, int towerHeight) {
        Color topColor;
        Color bottomColor;
        
        if (towerHeight <= 10) {
            // Day Sky
            topColor = new Color(135, 206, 250);
            bottomColor = new Color(245, 250, 255);
        } else if (towerHeight <= 25) {
            // Sunset Sky
            float t = (towerHeight - 10) / 15.0f;
            topColor = interpolateColor(new Color(135, 206, 250), new Color(70, 130, 180), t);
            bottomColor = interpolateColor(new Color(245, 250, 255), new Color(255, 140, 0), t);
        } else if (towerHeight <= 50) {
            // Twilight / Night Sky
            float t = (towerHeight - 25) / 25.0f;
            topColor = interpolateColor(new Color(70, 130, 180), new Color(25, 25, 112), t);
            bottomColor = interpolateColor(new Color(255, 140, 0), new Color(72, 61, 139), t);
        } else {
            // Stratosphere / Deep Space
            float t = Math.min(1.0f, (towerHeight - 50) / 50.0f);
            topColor = interpolateColor(new Color(25, 25, 112), new Color(5, 5, 20), t);
            bottomColor = interpolateColor(new Color(72, 61, 139), new Color(15, 15, 45), t);
        }
        
        GradientPaint skyGradient = new GradientPaint(0, 0, topColor, 0, gameHeight, bottomColor);
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, gameWidth, gameHeight);
    }
    
    private Color interpolateColor(Color c1, Color c2, float ratio) {
        float r = c1.getRed() + ratio * (c2.getRed() - c1.getRed());
        float g = c1.getGreen() + ratio * (c2.getGreen() - c1.getGreen());
        float b = c1.getBlue() + ratio * (c2.getBlue() - c1.getBlue());
        return new Color(Math.max(0, Math.min(255, (int)r)),
                         Math.max(0, Math.min(255, (int)g)),
                         Math.max(0, Math.min(255, (int)b)));
    }
    
    private void renderSmallBuildings(Graphics2D g2d, double screenGroundY) {
        g2d.setColor(new Color(150, 150, 160));
        int count = smallBuildingHeights.length;
        for (int i = 0; i < count; i++) {
            int x = i * (gameWidth / count);
            int height = smallBuildingHeights[i];
            g2d.fillRect(x, (int)screenGroundY - height, gameWidth / count - 6, height);
        }
    }
    
    private void renderMediumBuildings(Graphics2D g2d, double parallaxY) {
        g2d.setColor(new Color(100, 110, 125, 180));
        int count = mediumBuildingHeights.length;
        for (int i = 0; i < count; i++) {
            int x = i * (gameWidth / count);
            int height = mediumBuildingHeights[i];
            int y = (int)(groundLevel + parallaxY - height);
            if (y < gameHeight) {
                g2d.fillRect(x, y, gameWidth / count - 8, height);
            }
        }
    }
    
    private void renderSkyscrapers(Graphics2D g2d, double parallaxY) {
        g2d.setColor(new Color(50, 60, 80, 150));
        int count = skyscraperHeights.length;
        for (int i = 0; i < count; i++) {
            int x = i * (gameWidth / count);
            int height = skyscraperHeights[i];
            int y = (int)(groundLevel + parallaxY - height);
            if (y < gameHeight) {
                g2d.fillRect(x, y, gameWidth / count - 10, height);
            }
        }
    }
    
    private void renderClouds(Graphics2D g2d, double parallaxY) {
        g2d.setColor(new Color(255, 255, 255, 150));
        for (int i = 0; i < cloudX.length; i++) {
            int y = (int)((cloudY[i] + parallaxY) % (gameHeight + 100)) - 50;
            g2d.fillOval(cloudX[i], y, cloudW[i], cloudH[i]);
        }
    }
    
    private void renderStars(Graphics2D g2d, int towerHeight, double parallaxY) {
        int alpha = Math.min(255, Math.max(0, (towerHeight - 20) * 8));
        g2d.setColor(new Color(255, 255, 255, alpha));
        for (int i = 0; i < starX.length; i++) {
            int y = (int)((starY[i] + parallaxY) % gameHeight);
            if (y < 0) y += gameHeight;
            g2d.fillOval(starX[i], y, 2, 2);
        }
    }
    
    public void update() {
        // Deterministic parallax update
    }
}
package com.skillparty.towerblox.ui.components;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.BasicStroke;
import java.util.Random;

/**
 * Dynamic city background that changes based on tower height - ENHANCED
 */
public class CityBackground {
    private int gameWidth;
    private int gameHeight;
    private int groundLevel;
    private Random random;
    
    // Background transition levels
    private static final int GROUND_LEVEL_MAX = 10;      // 0-10: Ground level
    private static final int MID_RISE_MAX = 30;          // 11-30: Mid-rise
    private static final int HIGH_RISE_MAX = 50;         // 31-50: High-rise
    private static final int SKYSCRAPER_MAX = 100;       // 51-100: Skyscraper
    // 101+: Space/Stratosphere
    
    public CityBackground(int gameWidth, int gameHeight, int groundLevel) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.groundLevel = groundLevel;
        this.random = new Random();
    }
    
    /**
     * Renders dynamic background based on tower height - FIXED
     */
    public void render(Graphics2D g2d, int towerHeight, double cameraY) {
        // Determine background type based on tower height
        if (towerHeight <= GROUND_LEVEL_MAX) {
            renderGroundLevel(g2d, towerHeight, cameraY);
        } else if (towerHeight <= MID_RISE_MAX) {
            renderMidRise(g2d, towerHeight, cameraY);
        } else if (towerHeight <= HIGH_RISE_MAX) {
            renderHighRise(g2d, towerHeight, cameraY);
        } else if (towerHeight <= SKYSCRAPER_MAX) {
            renderSkyscraper(g2d, towerHeight, cameraY);
        } else {
            renderStratosphere(g2d, towerHeight, cameraY);
        }
    }
    
    /**
     * Ground level background (0-10 floors)
     */
    private void renderGroundLevel(Graphics2D g2d, int towerHeight, double cameraY) {
        // Bright day sky
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(135, 206, 250),      // Light blue
            0, gameHeight, new Color(255, 255, 255)  // White
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, gameWidth, gameHeight);
        
        // Green ground
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, groundLevel, gameWidth, gameHeight - groundLevel);
        
        // Small buildings in distance
        renderSmallBuildings(g2d);
        
        // Sun
        g2d.setColor(new Color(255, 255, 0));
        g2d.fillOval(gameWidth - 100, 50, 60, 60);
    }
    
    /**
     * Mid-rise background (11-30 floors)
     */
    private void renderMidRise(Graphics2D g2d, int towerHeight, double cameraY) {
        // Afternoon sky
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(100, 149, 237),      // Cornflower blue
            0, gameHeight, new Color(255, 218, 185)  // Peach
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, gameWidth, gameHeight);
        
        // Urban ground
        g2d.setColor(new Color(105, 105, 105));
        g2d.fillRect(0, groundLevel, gameWidth, gameHeight - groundLevel);
        
        // Medium buildings
        renderMediumBuildings(g2d);
        
        // Clouds
        renderClouds(g2d, 3);
    }
    
    /**
     * High-rise background (31-50 floors)
     */
    private void renderHighRise(Graphics2D g2d, int towerHeight, double cameraY) {
        // Evening sky
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(70, 130, 180),       // Steel blue
            0, gameHeight, new Color(255, 140, 0)    // Dark orange
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, gameWidth, gameHeight);
        
        // City ground
        g2d.setColor(new Color(64, 64, 64));
        g2d.fillRect(0, groundLevel, gameWidth, gameHeight - groundLevel);
        
        // Tall buildings
        renderTallBuildings(g2d);
        
        // More clouds
        renderClouds(g2d, 5);
        
        // City lights starting to appear
        renderCityLights(g2d);
    }
    
    /**
     * Skyscraper background (51-100 floors)
     */
    private void renderSkyscraper(Graphics2D g2d, int towerHeight, double cameraY) {
        // Night sky
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(25, 25, 112),        // Midnight blue
            0, gameHeight, new Color(72, 61, 139)    // Dark slate blue
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, gameWidth, gameHeight);
        
        // Dark city ground
        g2d.setColor(new Color(32, 32, 32));
        g2d.fillRect(0, groundLevel, gameWidth, gameHeight - groundLevel);
        
        // Skyscrapers
        renderSkyscrapers(g2d);
        
        // Dense clouds
        renderClouds(g2d, 7);
        
        // Bright city lights
        renderNightCityLights(g2d);
        
        // Stars
        renderStars(g2d, 20);
    }
    
    /**
     * Stratosphere background (101+ floors)
     */
    private void renderStratosphere(Graphics2D g2d, int towerHeight, double cameraY) {
        // Space-like sky
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(0, 0, 0),            // Black
            0, gameHeight, new Color(25, 25, 112)    // Midnight blue
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, gameWidth, gameHeight);
        
        // No visible ground at this height
        
        // Earth curvature in distance
        renderEarthCurvature(g2d);
        
        // Many stars
        renderStars(g2d, 50);
        
        // Space atmosphere effect
        renderSpaceAtmosphere(g2d);
    }
    
    /**
     * Renders small buildings for ground level
     */
    private void renderSmallBuildings(Graphics2D g2d) {
        g2d.setColor(new Color(169, 169, 169));
        for (int i = 0; i < 8; i++) {
            int x = i * (gameWidth / 8);
            int height = 40 + random.nextInt(30);
            g2d.fillRect(x, groundLevel - height, gameWidth / 8 - 5, height);
        }
    }
    
    /**
     * Renders medium buildings for mid-rise
     */
    private void renderMediumBuildings(Graphics2D g2d) {
        g2d.setColor(new Color(128, 128, 128));
        for (int i = 0; i < 6; i++) {
            int x = i * (gameWidth / 6);
            int height = 80 + random.nextInt(60);
            g2d.fillRect(x, groundLevel - height, gameWidth / 6 - 5, height);
        }
    }
    
    /**
     * Renders tall buildings for high-rise
     */
    private void renderTallBuildings(Graphics2D g2d) {
        g2d.setColor(new Color(105, 105, 105));
        for (int i = 0; i < 5; i++) {
            int x = i * (gameWidth / 5);
            int height = 120 + random.nextInt(100);
            g2d.fillRect(x, groundLevel - height, gameWidth / 5 - 5, height);
        }
    }
    
    /**
     * Renders skyscrapers for night view
     */
    private void renderSkyscrapers(Graphics2D g2d) {
        g2d.setColor(new Color(64, 64, 64));
        for (int i = 0; i < 4; i++) {
            int x = i * (gameWidth / 4);
            int height = 200 + random.nextInt(150);
            g2d.fillRect(x, groundLevel - height, gameWidth / 4 - 5, height);
        }
    }
    
    /**
     * Renders clouds
     */
    private void renderClouds(Graphics2D g2d, int count) {
        g2d.setColor(new Color(255, 255, 255, 180));
        for (int i = 0; i < count; i++) {
            int x = (i * gameWidth / count) + random.nextInt(50);
            int y = 100 + random.nextInt(200);
            g2d.fillOval(x, y, 80 + random.nextInt(40), 30 + random.nextInt(20));
        }
    }
    
    /**
     * Renders city lights for evening
     */
    private void renderCityLights(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 0, 150));
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(gameWidth);
            int y = groundLevel - random.nextInt(200);
            g2d.fillRect(x, y, 3, 3);
        }
    }
    
    /**
     * Renders bright city lights for night
     */
    private void renderNightCityLights(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 0));
        for (int i = 0; i < 40; i++) {
            int x = random.nextInt(gameWidth);
            int y = groundLevel - random.nextInt(300);
            g2d.fillRect(x, y, 4, 4);
        }
    }
    
    /**
     * Renders stars
     */
    private void renderStars(Graphics2D g2d, int count) {
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(gameWidth);
            int y = random.nextInt(gameHeight / 2);
            g2d.fillOval(x, y, 2, 2);
        }
    }
    
    /**
     * Renders Earth curvature for space view
     */
    private void renderEarthCurvature(Graphics2D g2d) {
        GradientPaint earthGradient = new GradientPaint(
            0, gameHeight - 100, new Color(0, 100, 0),
            0, gameHeight, new Color(0, 50, 100)
        );
        g2d.setPaint(earthGradient);
        g2d.fillArc(-200, gameHeight - 50, gameWidth + 400, 200, 0, 180);
    }
    
    /**
     * Renders space atmosphere effect
     */
    private void renderSpaceAtmosphere(Graphics2D g2d) {
        GradientPaint atmosphereGradient = new GradientPaint(
            0, gameHeight - 150, new Color(0, 100, 255, 50),
            0, gameHeight - 50, new Color(0, 0, 0, 0)
        );
        g2d.setPaint(atmosphereGradient);
        g2d.fillRect(0, gameHeight - 150, gameWidth, 100);
    }
    
    /**
     * Updates background animations
     */
    public void update() {
        // Could add animated elements here in the future
    }
}
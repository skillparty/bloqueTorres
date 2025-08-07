package com.skillparty.towerblox.ui.components;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;

/**
 * Simple city background for the game
 */
public class CityBackground {
    private int gameWidth;
    private int gameHeight;
    private int groundLevel;
    
    public CityBackground(int gameWidth, int gameHeight, int groundLevel) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.groundLevel = groundLevel;
    }
    
    /**
     * Renders a simple city background
     */
    public void render(Graphics2D g2d, int towerHeight, double cameraY) {
        // Simple gradient sky background
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(135, 206, 250),  // Light blue
            0, gameHeight, new Color(255, 218, 185)  // Light orange
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, gameWidth, gameHeight);
        
        // Simple ground
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, groundLevel, gameWidth, gameHeight - groundLevel);
    }
    
    /**
     * Updates background animations
     */
    public void update() {
        // Simple update - no animations for now
    }
}
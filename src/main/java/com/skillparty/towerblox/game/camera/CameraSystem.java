package com.skillparty.towerblox.game.camera;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Advanced camera system for Tower Bloxx 2025
 * Provides smooth tracking, zoom capabilities, and cinematic effects
 * 
 * @author joseAlejandro
 */
public class CameraSystem {
    private double x, y;
    private double targetX, targetY;
    private double zoom = 1.0;
    private double targetZoom = 1.0;
    
    // Smooth following parameters
    private static final double FOLLOW_SPEED = 0.08;
    private static final double ZOOM_SPEED = 0.05;
    private static final double MIN_ZOOM = 0.3;
    private static final double MAX_ZOOM = 2.0;
    
    // Screen dimensions
    private int screenWidth;
    private int screenHeight;
    
    // Camera shake effect
    private double shakeX = 0, shakeY = 0;
    private double shakeIntensity = 0;
    private long shakeStartTime = 0;
    private long shakeDuration = 0;
    
    // Cinematic effects
    private boolean cinematicMode = false;
    private double cinematicTargetY = 0;
    
    public CameraSystem(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = 0;
        this.y = 0;
    }
    
    /**
     * Update camera position and effects
     */
    public void update() {
        // Smooth camera following
        x += (targetX - x) * FOLLOW_SPEED;
        y += (targetY - y) * FOLLOW_SPEED;
        zoom += (targetZoom - zoom) * ZOOM_SPEED;
        
        // Clamp zoom
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        
        // Update shake effect
        updateShake();
        
        // Cinematic mode handling
        if (cinematicMode) {
            targetY = cinematicTargetY;
        }
    }
    
    /**
     * Set camera target position (what the camera should follow)
     */
    public void setTarget(double targetX, double targetY) {
        this.targetX = targetX - screenWidth / 2.0;
        this.targetY = targetY - screenHeight / 2.0;
    }
    
    /**
     * Follow a tower with intelligent positioning - FIXED VERSION
     */
    public void followTower(double towerBaseX, double towerHeight) {
        // Always center horizontally on the screen center, not tower base
        double followX = 0; // Keep centered
        
        // Vertical positioning - show ground for first blocks, then follow tower
        double followY;
        if (towerHeight <= 50) {
            // For first few blocks, stay low to show ground
            followY = -100;
        } else if (towerHeight < 400) {
            // For growing towers, gradually move up but show ground
            followY = -50 - (towerHeight - 50) * 0.3;
        } else {
            // For tall towers, follow more closely but keep some offset
            followY = -200 - (towerHeight - 400) * 0.5;
        }
        
        setTarget(followX, followY);
        
        // Keep zoom stable for better gameplay
        setTargetZoom(1.0);
    }
    
    /**
     * Set target zoom level
     */
    public void setTargetZoom(double zoom) {
        this.targetZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
    }
    
    /**
     * Apply camera shake effect
     */
    public void shake(double intensity, long duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.shakeStartTime = System.currentTimeMillis();
    }
    
    /**
     * Update shake effect
     */
    private void updateShake() {
        if (shakeIntensity > 0) {
            long elapsed = System.currentTimeMillis() - shakeStartTime;
            if (elapsed < shakeDuration) {
                // Decrease intensity over time
                double progress = (double) elapsed / shakeDuration;
                double currentIntensity = shakeIntensity * (1.0 - progress);
                
                // Random shake offset
                shakeX = (Math.random() - 0.5) * currentIntensity * 2;
                shakeY = (Math.random() - 0.5) * currentIntensity * 2;
            } else {
                // Shake finished
                shakeIntensity = 0;
                shakeX = 0;
                shakeY = 0;
            }
        }
    }
    
    /**
     * Apply camera transformation to graphics
     */
    public void applyTransform(Graphics2D g2d) {
        AffineTransform transform = new AffineTransform();
        
        // Apply zoom (scale from center)
        transform.translate(screenWidth / 2.0, screenHeight / 2.0);
        transform.scale(zoom, zoom);
        transform.translate(-screenWidth / 2.0, -screenHeight / 2.0);
        
        // Apply camera position and shake
        transform.translate(-x + shakeX, -y + shakeY);
        
        g2d.transform(transform);
    }
    
    /**
     * Remove camera transformation
     */
    public void resetTransform(Graphics2D g2d, AffineTransform originalTransform) {
        g2d.setTransform(originalTransform);
    }
    
    /**
     * Convert screen coordinates to world coordinates
     */
    public double screenToWorldX(int screenX) {
        return (screenX / zoom) + x;
    }
    
    public double screenToWorldY(int screenY) {
        return (screenY / zoom) + y;
    }
    
    /**
     * Convert world coordinates to screen coordinates
     */
    public int worldToScreenX(double worldX) {
        return (int) ((worldX - x) * zoom);
    }
    
    public int worldToScreenY(double worldY) {
        return (int) ((worldY - y) * zoom);
    }
    
    /**
     * Enable cinematic mode for dramatic camera movements
     */
    public void setCinematicMode(boolean enabled, double targetY) {
        this.cinematicMode = enabled;
        this.cinematicTargetY = targetY;
    }
    
    /**
     * Instant camera positioning (no smooth movement)
     */
    public void setPositionImmediate(double x, double y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
    }
    
    /**
     * Instant zoom (no smooth transition)
     */
    public void setZoomImmediate(double zoom) {
        this.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        this.targetZoom = this.zoom;
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZoom() { return zoom; }
    public boolean isShaking() { return shakeIntensity > 0; }
    public boolean isCinematicMode() { return cinematicMode; }
}

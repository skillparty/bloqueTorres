package com.skillparty.towerblox.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Professional game feedback system for enhanced player experience
 * Provides immediate visual and contextual feedback for all game actions
 */
public class GameFeedbackSystem {
    
    private List<FeedbackEffect> activeEffects;
    private ScreenShake screenShake;
    private ComboDisplay comboDisplay;
    private PerfectionMeter perfectionMeter;
    
    public GameFeedbackSystem() {
        this.activeEffects = new ArrayList<>();
        this.screenShake = new ScreenShake();
        this.comboDisplay = new ComboDisplay();
        this.perfectionMeter = new PerfectionMeter();
    }
    
    /**
     * Base class for all feedback effects
     */
    private abstract static class FeedbackEffect {
        protected double x, y;
        protected long startTime;
        protected long duration;
        protected boolean expired;
        
        public FeedbackEffect(double x, double y, long duration) {
            this.x = x;
            this.y = y;
            this.startTime = System.currentTimeMillis();
            this.duration = duration;
            this.expired = false;
        }
        
        public abstract void update(long deltaTime);
        public abstract void render(Graphics2D g2d);
        
        public boolean isExpired() {
            return expired || (System.currentTimeMillis() - startTime) > duration;
        }
        
        protected double getProgress() {
            return Math.min(1.0, (System.currentTimeMillis() - startTime) / (double)duration);
        }
    }
    
    /**
     * Professional placement feedback effect
     */
    private static class PlacementFeedback extends FeedbackEffect {
        private String message;
        private Color color;
        private Font font;
        private boolean isPerfect;
        
        public PlacementFeedback(double x, double y, String message, Color color, boolean isPerfect) {
            super(x, y, isPerfect ? 2500 : 2000);
            this.message = message;
            this.color = color;
            this.isPerfect = isPerfect;
            this.font = new Font("Arial", Font.BOLD, isPerfect ? 24 : 18);
        }
        
        @Override
        public void update(long deltaTime) {
            // Move upward over time
            double progress = getProgress();
            y -= (deltaTime / 1000.0) * 60; // Move up 60 pixels per second
        }
        
        @Override
        public void render(Graphics2D g2d) {
            double progress = getProgress();
            float alpha = (float)(1.0 - progress);
            
            // Scale effect for perfect placements
            double scale = isPerfect ? (1.0 + Math.sin(progress * Math.PI * 4) * 0.1) : 1.0;
            
            AffineTransform oldTransform = g2d.getTransform();
            g2d.translate(x, y);
            g2d.scale(scale, scale);
            
            // Shadow
            g2d.setColor(new Color(0, 0, 0, (int)(alpha * 150)));
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(message);
            g2d.drawString(message, -textWidth/2 + 2, 2);
            
            // Main text
            Color textColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255));
            g2d.setColor(textColor);
            g2d.drawString(message, -textWidth/2, 0);
            
            g2d.setTransform(oldTransform);
        }
    }
    
    /**
     * Screen shake effect for impactful moments
     */
    private static class ScreenShake {
        private boolean active;
        private long startTime;
        private long duration;
        private double intensity;
        private double currentOffsetX, currentOffsetY;
        
        public void trigger(double intensity, long duration) {
            this.active = true;
            this.intensity = intensity;
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
        }
        
        public void update(long deltaTime) {
            if (!active) return;
            
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= duration) {
                active = false;
                currentOffsetX = 0;
                currentOffsetY = 0;
                return;
            }
            
            // Decay intensity over time
            double progress = elapsed / (double)duration;
            double currentIntensity = intensity * (1.0 - progress);
            
            // Generate random shake
            currentOffsetX = (Math.random() - 0.5) * 2 * currentIntensity;
            currentOffsetY = (Math.random() - 0.5) * 2 * currentIntensity;
        }
        
        public void applyShake(Graphics2D g2d) {
            if (active) {
                g2d.translate(currentOffsetX, currentOffsetY);
            }
        }
        
        public void removeShake(Graphics2D g2d) {
            if (active) {
                g2d.translate(-currentOffsetX, -currentOffsetY);
            }
        }
        
        public boolean isActive() { return active; }
    }
    
    /**
     * Combo display system
     */
    private static class ComboDisplay {
        private int currentCombo;
        private long comboStartTime;
        private boolean visible;
        private static final long COMBO_DISPLAY_DURATION = 3000;
        
        public void updateCombo(int combo) {
            if (combo > 1) {
                this.currentCombo = combo;
                this.comboStartTime = System.currentTimeMillis();
                this.visible = true;
            } else {
                this.visible = false;
            }
        }
        
        public void render(Graphics2D g2d, int screenWidth, int screenHeight) {
            if (!visible) return;
            
            long elapsed = System.currentTimeMillis() - comboStartTime;
            if (elapsed > COMBO_DISPLAY_DURATION) {
                visible = false;
                return;
            }
            
            float alpha = Math.min(1.0f, (COMBO_DISPLAY_DURATION - elapsed) / 1000.0f);
            double pulse = 1.0 + Math.sin(elapsed * 0.01) * 0.1;
            
            // Position in top-right corner
            int x = screenWidth - 200;
            int y = 80;
            
            AffineTransform oldTransform = g2d.getTransform();
            g2d.translate(x, y);
            g2d.scale(pulse, pulse);
            
            // Background
            g2d.setColor(new Color(255, 215, 0, (int)(alpha * 180)));
            g2d.fillRoundRect(-80, -25, 160, 50, 25, 25);
            
            // Border
            g2d.setColor(new Color(255, 165, 0, (int)(alpha * 255)));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(-80, -25, 160, 50, 25, 25);
            
            // Text
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.setColor(new Color(0, 0, 0, (int)(alpha * 255)));
            String comboText = "COMBO x" + currentCombo;
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(comboText);
            g2d.drawString(comboText, -textWidth/2, 8);
            
            g2d.setTransform(oldTransform);
        }
    }
    
    /**
     * Perfection meter for skill tracking
     */
    private static class PerfectionMeter {
        private double perfectionLevel;
        private List<Double> recentPlacements;
        private static final int PLACEMENT_HISTORY_SIZE = 10;
        
        public PerfectionMeter() {
            this.recentPlacements = new ArrayList<>();
            this.perfectionLevel = 0.5;
        }
        
        public void addPlacement(double alignmentPercentage) {
            recentPlacements.add(alignmentPercentage);
            if (recentPlacements.size() > PLACEMENT_HISTORY_SIZE) {
                recentPlacements.remove(0);
            }
            
            // Calculate average perfection
            double sum = recentPlacements.stream().mapToDouble(Double::doubleValue).sum();
            perfectionLevel = sum / recentPlacements.size();
        }
        
        public void render(Graphics2D g2d, int screenWidth, int screenHeight) {
            if (recentPlacements.size() < 3) return; // Need some data first
            
            // Position in top-left corner
            int x = 20;
            int y = 120;
            int width = 200;
            int height = 20;
            
            // Background
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(x - 5, y - 25, width + 10, height + 30, 10, 10);
            
            // Label
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("PRECISION", x, y - 8);
            
            // Meter background
            g2d.setColor(new Color(60, 60, 60));
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            
            // Meter fill
            int fillWidth = (int)(width * perfectionLevel);
            Color meterColor;
            if (perfectionLevel >= 0.8) {
                meterColor = Color.GREEN;
            } else if (perfectionLevel >= 0.6) {
                meterColor = Color.YELLOW;
            } else {
                meterColor = Color.RED;
            }
            
            g2d.setColor(meterColor);
            g2d.fillRoundRect(x, y, fillWidth, height, 10, 10);
            
            // Border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, width, height, 10, 10);
            
            // Percentage text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            String percentText = String.format("%.0f%%", perfectionLevel * 100);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(percentText);
            g2d.drawString(percentText, x + width - textWidth - 5, y + 14);
        }
        
        public double getPerfectionLevel() { return perfectionLevel; }
    }
    
    /**
     * Adds placement feedback effect
     */
    public void addPlacementFeedback(double x, double y, GameplayEnhancer.GameplayFeedback feedback) {
        String message = feedback.quality.displayText;
        if (feedback.perfectTiming) {
            message += " + TIMING!";
        }
        
        boolean isPerfect = feedback.quality == GameplayEnhancer.PlacementQuality.PERFECT;
        activeEffects.add(new PlacementFeedback(x, y, message, feedback.quality.color, isPerfect));
        
        // Update systems
        comboDisplay.updateCombo(feedback.comboCount);
        perfectionMeter.addPlacement(feedback.alignmentPercentage);
        
        // Trigger screen shake for perfect placements
        if (isPerfect || (feedback.isCombo && feedback.comboCount >= 5)) {
            screenShake.trigger(isPerfect ? 4.0 : 6.0, isPerfect ? 200 : 300);
        }
    }
    
    /**
     * Updates all feedback systems
     */
    public void update(long deltaTime) {
        // Update screen shake
        screenShake.update(deltaTime);
        
        // Update effects
        Iterator<FeedbackEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            FeedbackEffect effect = iterator.next();
            effect.update(deltaTime);
            if (effect.isExpired()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Renders all feedback elements
     */
    public void render(Graphics2D g2d, int screenWidth, int screenHeight) {
        // Apply screen shake
        AffineTransform originalTransform = g2d.getTransform();
        screenShake.applyShake(g2d);
        
        // Render effects
        for (FeedbackEffect effect : activeEffects) {
            effect.render(g2d);
        }
        
        // Remove screen shake for UI elements
        g2d.setTransform(originalTransform);
        
        // Render UI elements (not affected by shake)
        comboDisplay.render(g2d, screenWidth, screenHeight);
        perfectionMeter.render(g2d, screenWidth, screenHeight);
    }
    
    /**
     * Resets all feedback systems
     */
    public void reset() {
        activeEffects.clear();
        screenShake.active = false;
        comboDisplay.visible = false;
        perfectionMeter.recentPlacements.clear();
        perfectionMeter.perfectionLevel = 0.5;
    }
    
    /**
     * Gets current perfection level for adaptive difficulty
     */
    public double getCurrentPerfectionLevel() {
        return perfectionMeter.getPerfectionLevel();
    }
    
    /**
     * Checks if screen shake is active (for camera adjustments)
     */
    public boolean isScreenShakeActive() {
        return screenShake.isActive();
    }
}
package com.skillparty.towerblox.game.physics;

import com.skillparty.towerblox.utils.Constants;
import com.skillparty.towerblox.game.MovementRecorder;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.RenderingHints;

/**
 * SIMPLE WORKING CRANE - NO MORE COMPLEX SYSTEMS
 */
public class Crane {
    // Basic position and movement
    private double x, y;
    private double speed;
    private double baseSpeed;
    private int gameWidth;
    private boolean movingRight;
    
    // Simple movement bounds
    private double centerX;
    private double swingRange;
    private double minX, maxX;
    
    // Current block being carried
    private Block currentBlock;
    
    // Simple visual properties
    private int craneHeight = 80;
    private int hookLength = 40;
    
    // Movement recorder (keep for compatibility)
    private MovementRecorder movementRecorder;
    
    // Current tower height for calculations
    private int currentTowerHeight = 0;
    
    public Crane(double startX, double startY, int gameWidth) {
        this.gameWidth = gameWidth;
        this.centerX = gameWidth / 2.0;
        this.x = centerX;
        this.y = startY;
        
        // Optimized movement setup - FASTER AND SHORTER RANGE
        this.baseSpeed = 4.0; // Much faster for better gameplay
        this.speed = baseSpeed;
        this.movingRight = true;
        
        // CENTERED swing range - stays away from edges
        this.swingRange = gameWidth * 0.15; // Reduced to 15% of screen width for centered movement
        this.minX = centerX - swingRange;
        this.maxX = centerX + swingRange;
        
        System.out.println("🏗️ SIMPLE Crane created - Speed: " + speed + ", Range: " + swingRange);
    }
    
    /**
     * SIMPLE UPDATE - JUST MOVE LEFT AND RIGHT
     */
    public void update(long deltaTime) {
        // Convert to seconds
        double dt = deltaTime / 1000.0;
        
        // Optimized pendulum movement - FASTER AND SMOOTHER
        double movement = speed * dt * 100; // Increased to 100 pixels per second base
        
        if (movingRight) {
            x += movement;
            if (x >= maxX) {
                x = maxX;
                movingRight = false;
                System.out.println("🏗️ Crane reached RIGHT limit: " + x);
            }
        } else {
            x -= movement;
            if (x <= minX) {
                x = minX;
                movingRight = true;
                System.out.println("🏗️ Crane reached LEFT limit: " + x);
            }
        }
        
        // Update current block position if carrying one
        if (currentBlock != null && !currentBlock.isDropped()) {
            currentBlock.setX(x - currentBlock.getWidth() / 2);
            currentBlock.setY(y + craneHeight + hookLength);
        }
        
        // Debug output every 60 frames (1 second)
        if (System.currentTimeMillis() % 1000 < 50) {
            System.out.println("🏗️ Crane position: " + String.format("%.1f", x) + 
                             " | Moving: " + (movingRight ? "RIGHT" : "LEFT") + 
                             " | Speed: " + String.format("%.1f", speed));
        }
    }
    
    /**
     * PROFESSIONAL CRANE RENDER - CLEAN AND POLISHED
     */
    public void render(Graphics2D g2d) {
        // Enable antialiasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // Save original stroke
        BasicStroke originalStroke = (BasicStroke) g2d.getStroke();
        
        // Calculate positions
        int armStartX = (int)(centerX - swingRange * 1.1);
        int armEndX = (int)(centerX + swingRange * 1.1);
        
        // 1. CRANE ARM - Professional construction yellow with gradient effect
        renderCraneArm(g2d, armStartX, armEndX);
        
        // 2. CRANE MAST - Sturdy vertical support
        renderCraneMast(g2d);
        
        // 3. TROLLEY - Moving part with smooth animation
        renderTrolley(g2d);
        
        // 4. CABLE AND HOOK - Realistic physics
        renderCableAndHook(g2d);
        
        // 5. COUNTERWEIGHT - Realistic balance
        renderCounterweight(g2d, armStartX);
        
        // Restore original stroke
        g2d.setStroke(originalStroke);
        
        // Render current block if it exists and hasn't been dropped
        if (currentBlock != null && !currentBlock.isDropped()) {
            currentBlock.render(g2d);
        }
    }
    
    private void renderCraneArm(Graphics2D g2d, int armStartX, int armEndX) {
        // Main arm with gradient
        GradientPaint armGradient = new GradientPaint(
            0, (int)y - 5, new Color(255, 215, 0),  // Gold
            0, (int)y + 5, new Color(218, 165, 32)  // Darker gold
        );
        g2d.setPaint(armGradient);
        g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(armStartX, (int)y, armEndX, (int)y);
        
        // Arm outline for definition
        g2d.setColor(new Color(184, 134, 11)); // Dark gold outline
        g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(armStartX, (int)y, armEndX, (int)y);
        
        // Redraw main arm on top
        g2d.setPaint(armGradient);
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(armStartX, (int)y, armEndX, (int)y);
    }
    
    private void renderCraneMast(Graphics2D g2d) {
        // Mast with gradient
        GradientPaint mastGradient = new GradientPaint(
            (int)centerX - 5, 0, new Color(255, 140, 0),  // Dark orange
            (int)centerX + 5, 0, new Color(205, 92, 92)   // Indian red
        );
        g2d.setPaint(mastGradient);
        g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int)centerX, (int)y, (int)centerX, (int)(y - 60));
        
        // Mast outline
        g2d.setColor(new Color(139, 69, 19)); // Saddle brown outline
        g2d.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int)centerX, (int)y, (int)centerX, (int)(y - 60));
        
        // Redraw main mast
        g2d.setPaint(mastGradient);
        g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int)centerX, (int)y, (int)centerX, (int)(y - 60));
    }
    
    private void renderTrolley(Graphics2D g2d) {
        // Trolley with 3D effect
        int trolleyX = (int)x - 12;
        int trolleyY = (int)y - 8;
        int trolleyW = 24;
        int trolleyH = 16;
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(trolleyX + 2, trolleyY + 2, trolleyW, trolleyH, 6, 6);
        
        // Main trolley body
        GradientPaint trolleyGradient = new GradientPaint(
            trolleyX, trolleyY, new Color(220, 20, 60),     // Crimson
            trolleyX, trolleyY + trolleyH, new Color(139, 0, 0)  // Dark red
        );
        g2d.setPaint(trolleyGradient);
        g2d.fillRoundRect(trolleyX, trolleyY, trolleyW, trolleyH, 6, 6);
        
        // Trolley outline
        g2d.setColor(new Color(105, 0, 0)); // Dark red outline
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(trolleyX, trolleyY, trolleyW, trolleyH, 6, 6);
        
        // Trolley highlight
        g2d.setColor(new Color(255, 182, 193, 150)); // Light pink highlight
        g2d.fillRoundRect(trolleyX + 2, trolleyY + 2, trolleyW - 4, 4, 3, 3);
    }
    
    private void renderCableAndHook(Graphics2D g2d) {
        // Calculate cable swing with smooth animation
        double swingAmount = Math.sin(System.currentTimeMillis() * 0.003) * 3; // Subtle oscillation
        if (Math.abs(x - centerX) > swingRange * 0.8) {
            swingAmount += movingRight ? 2 : -2; // Extra swing at extremes
        }
        
        double hookX = x + swingAmount;
        double hookY = y + hookLength + 25;
        
        // Cable shadow
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int)x + 1, (int)(y + 8) + 1, (int)hookX + 1, (int)hookY + 1);
        
        // Main cable
        g2d.setColor(new Color(64, 64, 64)); // Dark gray
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int)x, (int)(y + 8), (int)hookX, (int)hookY);
        
        // Cable highlight
        g2d.setColor(new Color(128, 128, 128)); // Light gray
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine((int)x - 1, (int)(y + 8), (int)hookX - 1, (int)hookY);
        
        // Hook with 3D effect
        int hookW = 16, hookH = 10;
        int hookXPos = (int)hookX - hookW/2;
        int hookYPos = (int)hookY - hookH/2;
        
        // Hook shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(hookXPos + 1, hookYPos + 1, hookW, hookH, 4, 4);
        
        // Main hook
        GradientPaint hookGradient = new GradientPaint(
            hookXPos, hookYPos, new Color(169, 169, 169),     // Dark gray
            hookXPos, hookYPos + hookH, new Color(105, 105, 105)  // Dim gray
        );
        g2d.setPaint(hookGradient);
        g2d.fillRoundRect(hookXPos, hookYPos, hookW, hookH, 4, 4);
        
        // Hook outline
        g2d.setColor(new Color(64, 64, 64));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(hookXPos, hookYPos, hookW, hookH, 4, 4);
    }
    
    private void renderCounterweight(Graphics2D g2d, int armStartX) {
        int weightX = armStartX - 25;
        int weightY = (int)y - 12;
        int weightW = 30;
        int weightH = 24;
        
        // Counterweight shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRect(weightX + 2, weightY + 2, weightW, weightH);
        
        // Main counterweight
        GradientPaint weightGradient = new GradientPaint(
            weightX, weightY, new Color(105, 105, 105),     // Dim gray
            weightX, weightY + weightH, new Color(47, 79, 79)   // Dark slate gray
        );
        g2d.setPaint(weightGradient);
        g2d.fillRect(weightX, weightY, weightW, weightH);
        
        // Weight outline
        g2d.setColor(new Color(25, 25, 25)); // Very dark gray
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(weightX, weightY, weightW, weightH);
        
        // Weight highlight
        g2d.setColor(new Color(192, 192, 192, 100)); // Silver highlight
        g2d.fillRect(weightX + 2, weightY + 2, weightW - 4, 4);
    }
    
    /**
     * Drop the current block
     */
    public void dropBlock() {
        if (currentBlock == null || currentBlock.isDropped()) {
            return;
        }
        
        currentBlock.drop();
        System.out.println("🎯 Block dropped at position: " + x);
    }
    
    /**
     * Set the current block being carried
     */
    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
        if (block != null) {
            // Position block at crane location
            block.setX(x - block.getWidth() / 2);
            block.setY(y + craneHeight + hookLength);
            System.out.println("🔗 New block attached to crane at: " + x);
        }
    }
    
    /**
     * Reset the crane to initial state
     */
    public void reset() {
        this.x = centerX;
        this.speed = baseSpeed;
        this.movingRight = true;
        this.currentBlock = null;
        System.out.println("🔄 Crane reset to center: " + x);
    }
    
    /**
     * Set crane speed - SIMPLE AND DIRECT
     */
    public void setSpeed(double speed) {
        this.speed = Math.max(1.0, Math.min(10.0, speed)); // Clamp between 1 and 10
        System.out.println("⚡ Crane speed set to: " + this.speed);
    }
    
    /**
     * Update swing range based on tower height - CENTERED MOVEMENT
     */
    public void updateSwingRange(int towerHeight) {
        this.currentTowerHeight = towerHeight;
        
        // CENTERED range reduction - starts smaller and stays centered
        double baseRange = gameWidth * 0.15; // Start with smaller base range
        double reductionFactor = towerHeight * 0.005; // Slower reduction (0.5% per level)
        reductionFactor = Math.min(reductionFactor, 0.5); // Max 50% reduction
        
        this.swingRange = baseRange * (1.0 - reductionFactor);
        this.swingRange = Math.max(this.swingRange, gameWidth * 0.08); // Min 8% of screen
        
        // ALWAYS CENTERED - never touches edges
        this.minX = centerX - swingRange;
        this.maxX = centerX + swingRange;
        
        // Adjust position if outside new bounds
        if (x < minX) {
            x = minX;
            movingRight = true;
        } else if (x > maxX) {
            x = maxX;
            movingRight = false;
        }
        
        System.out.println("🎯 Swing range updated for height " + towerHeight + ": " + String.format("%.0f", swingRange));
    }
    
    // SIMPLE GETTERS
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSpeed() { return speed; }
    public double getBaseSpeed() { return baseSpeed; }
    public double getSwingRange() { return swingRange; }
    public Block getCurrentBlock() { return currentBlock; }
    public boolean isMovingRight() { return movingRight; }
    
    // SIMPLE SETTERS
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setMovingRight(boolean movingRight) { this.movingRight = movingRight; }
    public void setMovementRecorder(MovementRecorder recorder) { this.movementRecorder = recorder; }
    
    // COMPATIBILITY METHODS FOR OTHER CLASSES
    public boolean isAnimating() { return false; } // Simple - no animation
    public void setManualControl(boolean manual) { /* Simple - ignore */ }
    public void moveLeft(int pixels) { this.x = Math.max(minX, x - pixels); }
    public void moveRight(int pixels) { this.x = Math.min(maxX, x + pixels); }
    public void moveUp(int pixels) { this.y = Math.max(50, y - pixels); }
    public void moveDown(int pixels) { this.y = Math.min(200, y + pixels); }
}
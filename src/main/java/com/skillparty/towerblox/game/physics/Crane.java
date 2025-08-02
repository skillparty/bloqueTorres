package com.skillparty.towerblox.game.physics;

import com.skillparty.towerblox.utils.Constants;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;

/**
 * Represents the crane that carries and drops blocks with realistic animation
 */
public class Crane {
    private double x, y;
    private double speed;
    private double baseSpeed;
    private int gameWidth;
    private boolean movingRight;
    
    // Current block being carried
    private Block currentBlock;
    
    // Crane animation
    private CraneAnimation craneAnimation;
    
    // Crane visual properties
    private int craneWidth;
    private int craneHeight;
    private int hookLength;
    

    
    public Crane(double startX, double startY, int gameWidth) {
        this.x = startX;
        this.y = startY;
        this.gameWidth = gameWidth;
        this.baseSpeed = Constants.CRANE_BASE_SPEED;
        this.speed = baseSpeed;
        this.movingRight = true;
        
        this.craneWidth = 60;
        this.craneHeight = Constants.CRANE_HEIGHT;
        this.hookLength = Constants.CRANE_HOOK_LENGTH;
        
        this.craneAnimation = new CraneAnimation();
    }
    
    /**
     * Updates the crane position and animation
     */
    public void update(long deltaTime) {
        // Update horizontal movement
        updateMovement(deltaTime);
        
        // Update claw animation
        craneAnimation.update(deltaTime);
        
        // Update current block position if carrying one
        if (currentBlock != null && !currentBlock.isDropped()) {
            currentBlock.setX(x - currentBlock.getWidth() / 2);
            currentBlock.setY(y + craneHeight + hookLength);
        }
    }
    
    /**
     * Updates crane horizontal movement
     */
    private void updateMovement(long deltaTime) {
        if (craneAnimation.isAnimating()) {
            return; // Don't move while animating
        }
        
        // Calculate movement
        double movement = speed * (deltaTime / 16.0); // Normalize to ~60fps
        
        if (movingRight) {
            x += movement;
            if (x + craneWidth / 2 >= gameWidth - 20) {
                movingRight = false;
            }
        } else {
            x -= movement;
            if (x - craneWidth / 2 <= 20) {
                movingRight = true;
            }
        }
    }
    

    
    /**
     * Renders the crane with realistic claw animation
     */
    public void render(Graphics2D g2d) {
        // Save original stroke
        BasicStroke originalStroke = (BasicStroke) g2d.getStroke();
        
        // Set crane color
        g2d.setColor(new Color(169, 169, 169)); // Dark gray
        
        // Draw crane arm (horizontal beam)
        g2d.setStroke(new BasicStroke(8));
        g2d.drawLine(0, (int)y, gameWidth, (int)y);
        
        // Draw crane support (vertical beam at current position)
        g2d.setStroke(new BasicStroke(6));
        g2d.drawLine((int)x, (int)y, (int)x, (int)(y + craneHeight));
        
        // Draw hook cable
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        g2d.drawLine((int)x, (int)(y + craneHeight), (int)x, (int)(y + craneHeight + hookLength));
        
        // Draw claw with realistic animation
        craneAnimation.renderClaw(g2d, x, y + craneHeight + hookLength);
        
        // Render current block if it exists and hasn't been dropped
        if (currentBlock != null && !currentBlock.isDropped()) {
            currentBlock.render(g2d);
        }
        
        // Restore original stroke
        g2d.setStroke(originalStroke);
        
        // Draw crane position indicator
        renderPositionIndicator(g2d);
    }
    

    
    /**
     * Renders position indicator and movement direction
     */
    private void renderPositionIndicator(Graphics2D g2d) {
        // Direction arrow
        g2d.setColor(Color.WHITE);
        int arrowY = (int)y - 15;
        int arrowSize = 8;
        
        if (movingRight) {
            // Right arrow
            g2d.drawLine((int)x, arrowY, (int)x + arrowSize, arrowY);
            g2d.drawLine((int)x + arrowSize, arrowY, (int)x + arrowSize - 3, arrowY - 3);
            g2d.drawLine((int)x + arrowSize, arrowY, (int)x + arrowSize - 3, arrowY + 3);
        } else {
            // Left arrow
            g2d.drawLine((int)x, arrowY, (int)x - arrowSize, arrowY);
            g2d.drawLine((int)x - arrowSize, arrowY, (int)x - arrowSize + 3, arrowY - 3);
            g2d.drawLine((int)x - arrowSize, arrowY, (int)x - arrowSize + 3, arrowY + 3);
        }
        
        // Speed indicator
        g2d.drawString(String.format("Speed: %.1fx", speed / baseSpeed), (int)x + 20, arrowY);
    }
    
    /**
     * Drops the current block and triggers claw animation
     */
    public void dropBlock() {
        if (currentBlock == null || currentBlock.isDropped() || craneAnimation.isAnimating()) {
            return;
        }
        
        // Start claw opening animation
        craneAnimation.startReleaseAnimation();
        
        // Drop the block
        currentBlock.drop();
        
        System.out.println("Block dropped at position: " + x + " | Animation started");
    }
    

    
    /**
     * Sets the current block being carried
     */
    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
        if (block != null) {
            // Position block at crane location
            block.setX(x - block.getWidth() / 2);
            block.setY(y + craneHeight + hookLength);
            
            // Start grab animation when picking up a new block
            craneAnimation.startGrabAnimation();
        }
    }
    
    /**
     * Resets the crane to initial state
     */
    public void reset() {
        this.x = gameWidth / 2.0;
        this.speed = baseSpeed;
        this.movingRight = true;
        this.currentBlock = null;
        this.craneAnimation.reset();
    }
    
    /**
     * Sets the crane speed (used for difficulty adjustment)
     */
    public void setSpeed(double speed) {
        this.speed = Math.max(0.5, Math.min(10.0, speed)); // Clamp between 0.5 and 10
    }
    
    /**
     * Gets crane status information
     */
    public String getStatus() {
        String blockStatus = currentBlock != null ? 
            (currentBlock.isDropped() ? "Dropped" : "Carrying") : "No Block";
        String animationStatus = craneAnimation.getCurrentState().toString();
        
        return String.format("Pos: %.0f | Speed: %.1f | Block: %s | Animation: %s", 
                           x, speed, blockStatus, animationStatus);
    }
    
    /**
     * Checks if crane is ready to accept a new block
     */
    public boolean isReadyForNewBlock() {
        return currentBlock == null || currentBlock.isDropped();
    }
    
    /**
     * Gets the drop zone bounds (where blocks will land)
     */
    public double getDropZoneLeft() {
        return x - (currentBlock != null ? currentBlock.getWidth() / 2 : 25);
    }
    
    public double getDropZoneRight() {
        return x + (currentBlock != null ? currentBlock.getWidth() / 2 : 25);
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSpeed() { return speed; }
    public double getBaseSpeed() { return baseSpeed; }
    public Block getCurrentBlock() { return currentBlock; }
    public boolean isMovingRight() { return movingRight; }
    public boolean isAnimating() { return craneAnimation.isAnimating(); }
    public int getAnimationFrame() { return craneAnimation.getCurrentFrame(); }
    public boolean isOpening() { return craneAnimation.isOpening(); }
    public boolean isClosing() { return craneAnimation.isClosing(); }
    
    // Setters
    public void setX(double x) { 
        this.x = Math.max(craneWidth/2, Math.min(gameWidth - craneWidth/2, x)); 
    }
    
    public void setMovingRight(boolean movingRight) { 
        this.movingRight = movingRight; 
    }
}
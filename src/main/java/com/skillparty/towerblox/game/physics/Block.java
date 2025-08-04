package com.skillparty.towerblox.game.physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.RenderingHints;

/**
 * Represents a single block in the Tower Bloxx game
 * Contains physics properties and collision detection
 */
public class Block {
    private double x, y;
    private double width, height;
    private double velocityX, velocityY;
    private Color color;
    private boolean isStable;
    private boolean isDropped;
    
    // Physics constants
    private static final double GRAVITY = 0.5;
    private static final double FRICTION = 0.98;
    private static final double MIN_VELOCITY = 0.1;

    /**
     * Creates a new block with specified position and dimensions
     */
    public Block(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.velocityX = 0;
        this.velocityY = 0;
        this.isStable = false;
        this.isDropped = false;
    }

    /**
     * Updates the block's physics (gravity, velocity, position)
     */
    public void update() {
        if (isDropped && !isStable) {
            // Apply gravity
            velocityY += GRAVITY;
            
            // Apply friction to horizontal movement
            velocityX *= FRICTION;
            
            // Update position
            x += velocityX;
            y += velocityY;
            
            // Stop very small movements
            if (Math.abs(velocityX) < MIN_VELOCITY) {
                velocityX = 0;
            }
            if (Math.abs(velocityY) < MIN_VELOCITY && velocityY > 0) {
                velocityY = 0;
            }
        }
    }

    /**
     * Renders the block on the screen with improved 3D design
     */
    public void render(Graphics2D g2d) {
        int blockX = (int)x;
        int blockY = (int)y;
        int blockWidth = (int)width;
        int blockHeight = (int)height;
        
        // Enable antialiasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create gradient for 3D effect
        GradientPaint gradient = new GradientPaint(
            blockX, blockY, color.brighter().brighter(),
            blockX + blockWidth, blockY + blockHeight, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(blockX, blockY, blockWidth, blockHeight);
        
        // Draw 3D highlight on top and left edges
        g2d.setColor(color.brighter().brighter());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(blockX, blockY, blockX + blockWidth - 1, blockY); // Top edge
        g2d.drawLine(blockX, blockY, blockX, blockY + blockHeight - 1); // Left edge
        
        // Draw 3D shadow on bottom and right edges
        g2d.setColor(color.darker().darker());
        g2d.drawLine(blockX + blockWidth - 1, blockY + 1, blockX + blockWidth - 1, blockY + blockHeight - 1); // Right edge
        g2d.drawLine(blockX + 1, blockY + blockHeight - 1, blockX + blockWidth - 1, blockY + blockHeight - 1); // Bottom edge
        
        // Draw main border
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(blockX, blockY, blockWidth, blockHeight);
        
        // Add inner highlight for more depth
        g2d.setColor(new Color(255, 255, 255, 80)); // Semi-transparent white
        g2d.fillRect(blockX + 2, blockY + 2, blockWidth - 4, blockHeight / 3);
        
        // Add texture pattern
        g2d.setColor(new Color(0, 0, 0, 30)); // Semi-transparent black
        for (int i = blockX + 4; i < blockX + blockWidth - 4; i += 8) {
            for (int j = blockY + 4; j < blockY + blockHeight - 4; j += 8) {
                g2d.fillRect(i, j, 2, 2);
            }
        }
        
        // Reset stroke
        g2d.setStroke(new BasicStroke(1));
    }

    /**
     * Checks if this block collides with another block
     */
    public boolean collidesWith(Block other) {
        Rectangle thisRect = new Rectangle((int)x, (int)y, (int)width, (int)height);
        Rectangle otherRect = new Rectangle((int)other.x, (int)other.y, (int)other.width, (int)other.height);
        return thisRect.intersects(otherRect);
    }

    /**
     * Checks if this block collides with the ground (bottom boundary)
     */
    public boolean collidesWithGround(int groundLevel) {
        return (y + height) >= groundLevel && isDropped;
    }

    /**
     * Stops the block's movement and marks it as stable
     */
    public void makeStable() {
        isStable = true;
        velocityX = 0;
        velocityY = 0;
    }

    /**
     * Drops the block, enabling physics
     */
    public void drop() {
        isDropped = true;
    }

    /**
     * Calculates alignment score with another block (0-100)
     */
    public int getAlignmentScore(Block below) {
        if (below == null) return 100; // Perfect if it's the first block
        
        double centerThis = x + width / 2;
        double centerBelow = below.x + below.width / 2;
        double maxOffset = (width + below.width) / 2;
        double offset = Math.abs(centerThis - centerBelow);
        
        if (offset >= maxOffset) return 0;
        
        return (int)(100 * (1 - offset / maxOffset));
    }

    // Getters and setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    
    public double getVelocityX() { return velocityX; }
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }
    
    public double getVelocityY() { return velocityY; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }
    
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    
    public boolean isStable() { return isStable; }
    public boolean isDropped() { return isDropped; }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, (int)width, (int)height);
    }
}
package com.skillparty.towerblox.game.physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a building floor block in the Tower Bloxx game
 * Contains physics properties, collision detection, and building-like appearance
 */
public class Block {
    private double x, y;
    private double width, height;
    private double velocityX, velocityY;
    private Color color;
    private boolean isStable;
    private boolean isDropped;
    private BlockType blockType;
    private List<Window> windows;
    private boolean hasBalcony;
    private boolean hasAntenna;
    
    // Physics constants
    private static final double GRAVITY = 0.5;
    private static final double FRICTION = 0.98;
    private static final double MIN_VELOCITY = 0.1;
    
    // Block types for different building floors
    public enum BlockType {
        FOUNDATION,    // Special base block
        RESIDENTIAL,   // Apartment floors
        OFFICE,        // Office floors
        COMMERCIAL,    // Shop/restaurant floors
        PENTHOUSE     // Top luxury floors
    }
    
    // Window class for building floors
    private static class Window {
        int x, y, width, height;
        boolean isLit;
        Color lightColor;
        
        Window(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.isLit = Math.random() > 0.4; // 60% chance of being lit
            this.lightColor = new Color(255, 255, 150, 200);
        }
        
        void render(Graphics2D g2d, int blockX, int blockY) {
            // Calculate absolute position from relative position
            int absoluteX = blockX + x;
            int absoluteY = blockY + y;
            
            // Window frame
            g2d.setColor(new Color(40, 40, 40));
            g2d.fillRect(absoluteX, absoluteY, width, height);
            
            // Window light/reflection
            if (isLit) {
                g2d.setColor(lightColor);
                g2d.fillRect(absoluteX + 1, absoluteY + 1, width - 2, height - 2);
            } else {
                // Reflection
                g2d.setColor(new Color(150, 200, 255, 100));
                g2d.fillRect(absoluteX + 1, absoluteY + 1, width - 2, height - 2);
            }
            
            // Window cross
            g2d.setColor(new Color(60, 60, 60));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(absoluteX + width/2, absoluteY, absoluteX + width/2, absoluteY + height);
            g2d.drawLine(absoluteX, absoluteY + height/2, absoluteX + width, absoluteY + height/2);
        }
    }

    /**
     * Creates a new block with specified position and dimensions
     */
    public Block(double x, double y, double width, double height, Color color) {
        this(x, y, width, height, color, BlockType.RESIDENTIAL);
    }
    
    /**
     * Creates a new block with specified type
     */
    public Block(double x, double y, double width, double height, Color color, BlockType blockType) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.blockType = blockType;
        this.velocityX = 0;
        this.velocityY = 0;
        this.isStable = false;
        this.isDropped = false;
        this.windows = new ArrayList<>();
        this.hasBalcony = Math.random() > 0.7; // 30% chance
        this.hasAntenna = blockType == BlockType.PENTHOUSE && Math.random() > 0.5;
        
        generateWindows();
    }
    
    /**
     * Generates windows based on block type and size
     */
    private void generateWindows() {
        if (blockType == BlockType.FOUNDATION) {
            return; // Foundation has no windows
        }
        
        int windowWidth = 8;
        int windowHeight = 10;
        int spacing = 4;
        
        // Different window patterns based on block type
        switch (blockType) {
            case RESIDENTIAL:
                generateResidentialWindows(windowWidth, windowHeight, spacing);
                break;
            case OFFICE:
                generateOfficeWindows(windowWidth, windowHeight, spacing);
                break;
            case COMMERCIAL:
                generateCommercialWindows(windowWidth, windowHeight, spacing);
                break;
            case PENTHOUSE:
                generatePenthouseWindows(windowWidth, windowHeight, spacing);
                break;
        }
    }
    
    private void generateResidentialWindows(int windowWidth, int windowHeight, int spacing) {
        // Residential: scattered windows with some variety
        int cols = (int)width / (windowWidth + spacing);
        int rows = Math.max(1, (int)height / (windowHeight + spacing));
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (Math.random() > 0.2) { // 80% chance of window
                    // Store RELATIVE positions, not absolute
                    int wx = col * (windowWidth + spacing) + spacing;
                    int wy = row * (windowHeight + spacing) + spacing;
                    windows.add(new Window(wx, wy, windowWidth, windowHeight));
                }
            }
        }
    }
    
    private void generateOfficeWindows(int windowWidth, int windowHeight, int spacing) {
        // Office: uniform grid of windows
        int cols = (int)width / (windowWidth + spacing);
        int rows = Math.max(1, (int)height / (windowHeight + spacing));
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Store RELATIVE positions, not absolute
                int wx = col * (windowWidth + spacing) + spacing;
                int wy = row * (windowHeight + spacing) + spacing;
                windows.add(new Window(wx, wy, windowWidth, windowHeight));
            }
        }
    }
    
    private void generateCommercialWindows(int windowWidth, int windowHeight, int spacing) {
        // Commercial: large windows, fewer rows
        int largeWindowWidth = windowWidth * 2;
        int largeWindowHeight = windowHeight + 4;
        int cols = (int)width / (largeWindowWidth + spacing);
        
        for (int col = 0; col < cols; col++) {
            // Store RELATIVE positions, not absolute
            int wx = col * (largeWindowWidth + spacing) + spacing;
            int wy = spacing;
            windows.add(new Window(wx, wy, largeWindowWidth, largeWindowHeight));
        }
    }
    
    private void generatePenthouseWindows(int windowWidth, int windowHeight, int spacing) {
        // Penthouse: large, luxurious windows
        int luxuryWidth = windowWidth + 4;
        int luxuryHeight = windowHeight + 6;
        int cols = (int)width / (luxuryWidth + spacing);
        
        for (int col = 0; col < cols; col++) {
            // Store RELATIVE positions, not absolute
            int wx = col * (luxuryWidth + spacing) + spacing;
            int wy = spacing;
            Window window = new Window(wx, wy, luxuryWidth, luxuryHeight);
            window.lightColor = new Color(255, 215, 0, 180); // Golden light
            windows.add(window);
        }
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
     * Renders the block as a building floor with appropriate details
     */
    public void render(Graphics2D g2d) {
        int blockX = (int)x;
        int blockY = (int)y;
        int blockWidth = (int)width;
        int blockHeight = (int)height;
        
        // Enable antialiasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Render based on block type
        switch (blockType) {
            case FOUNDATION:
                renderFoundation(g2d, blockX, blockY, blockWidth, blockHeight);
                break;
            case RESIDENTIAL:
                renderResidentialFloor(g2d, blockX, blockY, blockWidth, blockHeight);
                break;
            case OFFICE:
                renderOfficeFloor(g2d, blockX, blockY, blockWidth, blockHeight);
                break;
            case COMMERCIAL:
                renderCommercialFloor(g2d, blockX, blockY, blockWidth, blockHeight);
                break;
            case PENTHOUSE:
                renderPenthouseFloor(g2d, blockX, blockY, blockWidth, blockHeight);
                break;
        }
        
        // Render windows with correct positioning
        for (Window window : windows) {
            window.render(g2d, blockX, blockY);
        }
        
        // Render additional features
        if (hasBalcony && blockType != BlockType.FOUNDATION) {
            renderBalcony(g2d, blockX, blockY, blockWidth, blockHeight);
        }
        
        if (hasAntenna) {
            renderAntenna(g2d, blockX, blockY, blockWidth);
        }
    }
    
    private void renderFoundation(Graphics2D g2d, int x, int y, int width, int height) {
        // Foundation: solid, concrete-like base
        Color foundationColor = new Color(120, 120, 120);
        
        // Main foundation body
        GradientPaint gradient = new GradientPaint(
            x, y, foundationColor.brighter(),
            x + width, y + height, foundationColor.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Foundation details
        g2d.setColor(foundationColor.darker());
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x, y, width, height);
        
        // Concrete texture
        g2d.setColor(new Color(100, 100, 100, 100));
        for (int i = x + 5; i < x + width - 5; i += 10) {
            for (int j = y + 5; j < y + height - 5; j += 10) {
                g2d.fillRect(i, j, 3, 3);
            }
        }
        
        // Foundation label
        g2d.setColor(Color.WHITE);
        g2d.drawString("BASE", x + width/2 - 15, y + height/2 + 3);
    }
    
    private void renderResidentialFloor(Graphics2D g2d, int x, int y, int width, int height) {
        // Residential: warm, homey colors
        GradientPaint gradient = new GradientPaint(
            x, y, color.brighter(),
            x + width, y + height, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Building outline
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);
        
        // Residential details - brick pattern
        g2d.setColor(color.darker().darker());
        g2d.setStroke(new BasicStroke(1));
        for (int i = y + 5; i < y + height; i += 8) {
            g2d.drawLine(x, i, x + width, i);
        }
    }
    
    private void renderOfficeFloor(Graphics2D g2d, int x, int y, int width, int height) {
        // Office: clean, modern appearance
        GradientPaint gradient = new GradientPaint(
            x, y, color.brighter(),
            x + width, y + height, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Clean modern outline
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);
        
        // Modern glass effect
        g2d.setColor(new Color(255, 255, 255, 60));
        g2d.fillRect(x + 2, y + 2, width - 4, height/4);
    }
    
    private void renderCommercialFloor(Graphics2D g2d, int x, int y, int width, int height) {
        // Commercial: bright, inviting colors
        GradientPaint gradient = new GradientPaint(
            x, y, color.brighter().brighter(),
            x + width, y + height, color
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Commercial storefront look
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x, y, width, height);
        
        // Awning effect
        g2d.setColor(color.darker());
        g2d.fillRect(x - 2, y + height - 8, width + 4, 6);
    }
    
    private void renderPenthouseFloor(Graphics2D g2d, int x, int y, int width, int height) {
        // Penthouse: luxurious, golden accents
        Color luxuryColor = new Color(
            Math.min(255, color.getRed() + 30),
            Math.min(255, color.getGreen() + 20),
            Math.min(255, color.getBlue() + 10)
        );
        
        GradientPaint gradient = new GradientPaint(
            x, y, luxuryColor.brighter(),
            x + width, y + height, luxuryColor.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Luxury border
        g2d.setColor(new Color(255, 215, 0)); // Gold
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x, y, width, height);
        
        // Luxury details
        g2d.setColor(new Color(255, 215, 0, 100));
        g2d.fillRect(x + 2, y + 2, width - 4, 4);
    }
    
    private void renderBalcony(Graphics2D g2d, int x, int y, int width, int height) {
        // Small balcony on the side
        g2d.setColor(new Color(100, 100, 100));
        g2d.fillRect(x + width - 8, y + height/2, 6, height/3);
        
        // Balcony railing
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(x + width - 8, y + height/2, x + width - 2, y + height/2);
        for (int i = 0; i < 3; i++) {
            g2d.drawLine(x + width - 7 + i*2, y + height/2, x + width - 7 + i*2, y + height/2 + 8);
        }
    }
    
    private void renderAntenna(Graphics2D g2d, int x, int y, int width) {
        // Antenna on top of penthouse
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x + width/2, y, x + width/2, y - 15);
        g2d.fillOval(x + width/2 - 2, y - 18, 4, 4);
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
    
    public BlockType getBlockType() { return blockType; }
    public void setBlockType(BlockType blockType) { this.blockType = blockType; }
}
package com.skillparty.towerblox.game.physics;

import com.skillparty.towerblox.utils.Constants;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the tower of blocks with stability calculations and collision detection
 */
public class Tower {
    private List<Block> blocks;
    private int gameWidth;
    private int groundLevel;
    private double instabilityScore;
    private double tiltAngle;
    private boolean isStable;
    
    // Tower statistics
    private int maxHeight;
    private double averageAlignment;
    private int perfectAlignments;
    
    public Tower(int gameWidth, int groundLevel) {
        this.gameWidth = gameWidth;
        this.groundLevel = groundLevel;
        this.blocks = new ArrayList<>();
        this.isStable = true;
        this.instabilityScore = 0.0;
        this.tiltAngle = 0.0;
        this.maxHeight = 0;
        this.averageAlignment = 0.0;
        this.perfectAlignments = 0;
    }
    
    /**
     * Adds a block to the tower
     */
    public void addBlock(Block block) {
        if (block == null) return;
        
        blocks.add(block);
        updateTowerStatistics();
        calculateStability();
        
        System.out.println("Block added to tower. Height: " + blocks.size() + 
                          " | Stability: " + String.format("%.1f%%", (1.0 - instabilityScore) * 100));
    }
    
    /**
     * Updates the tower physics and stability
     */
    public void update(long deltaTime) {
        // Update all blocks
        for (Block block : blocks) {
            block.update();
        }
        
        // Recalculate stability periodically
        calculateStability();
        
        // Check for blocks that have fallen too far
        removeFailedBlocks();
    }
    
    /**
     * Renders the tower
     */
    public void render(Graphics2D g2d) {
        // Render all blocks
        for (Block block : blocks) {
            block.render(g2d);
        }
        
        // Render stability indicator
        renderStabilityIndicator(g2d);
        
        // Render tower center line (for debugging)
        if (blocks.size() > 1) {
            renderCenterLine(g2d);
        }
    }
    
    /**
     * Renders a visual stability indicator
     */
    private void renderStabilityIndicator(Graphics2D g2d) {
        if (blocks.isEmpty()) return;
        
        // Stability bar
        int barWidth = 100;
        int barHeight = 10;
        int barX = gameWidth - barWidth - 20;
        int barY = 150;
        
        // Background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Stability fill
        double stability = 1.0 - instabilityScore;
        int fillWidth = (int)(barWidth * Math.max(0, Math.min(1, stability)));
        
        if (stability > 0.7) {
            g2d.setColor(Color.GREEN);
        } else if (stability > 0.4) {
            g2d.setColor(Color.YELLOW);
        } else {
            g2d.setColor(Color.RED);
        }
        
        g2d.fillRect(barX, barY, fillWidth, barHeight);
        
        // Border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);
        
        // Label
        g2d.drawString("Stability", barX, barY - 5);
        g2d.drawString(String.format("%.0f%%", stability * 100), barX + barWidth + 5, barY + 8);
    }
    
    /**
     * Renders the tower center line for visual reference
     */
    private void renderCenterLine(Graphics2D g2d) {
        if (blocks.size() < 2) return;
        
        double centerX = getTowerCenterX();
        
        g2d.setColor(new Color(255, 255, 255, 100)); // Semi-transparent white
        g2d.drawLine((int)centerX, 0, (int)centerX, groundLevel);
        
        // Show tilt angle
        if (Math.abs(tiltAngle) > 0.1) {
            g2d.setColor(Color.ORANGE);
            g2d.drawString(String.format("Tilt: %.1f°", Math.toDegrees(tiltAngle)), 
                          (int)centerX + 10, 30);
        }
    }
    
    /**
     * Calculates tower stability based on block alignment and physics
     */
    private void calculateStability() {
        if (blocks.size() < 2) {
            isStable = true;
            instabilityScore = 0.0;
            tiltAngle = 0.0;
            return;
        }
        
        double totalInstability = 0.0;
        int comparisons = 0;
        
        // Calculate alignment-based instability
        for (int i = 1; i < blocks.size(); i++) {
            Block current = blocks.get(i);
            Block below = blocks.get(i - 1);
            
            double alignmentScore = current.getAlignmentScore(below) / 100.0; // Normalize to 0-1
            double misalignment = 1.0 - alignmentScore;
            
            // Weight recent blocks more heavily
            double weight = 1.0 + (i / (double)blocks.size()) * 0.5;
            totalInstability += misalignment * weight;
            comparisons++;
        }
        
        if (comparisons > 0) {
            instabilityScore = totalInstability / comparisons;
        }
        
        // Calculate tilt angle
        calculateTiltAngle();
        
        // Add tilt to instability
        double tiltContribution = Math.abs(tiltAngle) / (Math.PI / 6); // Normalize to 0-1 (30 degrees = 1)
        instabilityScore = Math.min(1.0, instabilityScore + tiltContribution * 0.3);
        
        // Determine if tower is stable
        isStable = instabilityScore < Constants.TOWER_INSTABILITY_THRESHOLD;
    }
    
    /**
     * Calculates the tilt angle of the tower
     */
    private void calculateTiltAngle() {
        if (blocks.size() < 3) {
            tiltAngle = 0.0;
            return;
        }
        
        // Calculate center of mass progression
        double bottomCenterX = blocks.get(0).getX() + blocks.get(0).getWidth() / 2;
        double topCenterX = blocks.get(blocks.size() - 1).getX() + blocks.get(blocks.size() - 1).getWidth() / 2;
        
        double horizontalOffset = topCenterX - bottomCenterX;
        double verticalDistance = blocks.get(blocks.size() - 1).getY() - blocks.get(0).getY();
        
        if (verticalDistance != 0) {
            tiltAngle = Math.atan(horizontalOffset / Math.abs(verticalDistance));
        } else {
            tiltAngle = 0.0;
        }
    }
    
    /**
     * Updates tower statistics
     */
    private void updateTowerStatistics() {
        maxHeight = Math.max(maxHeight, blocks.size());
        
        if (blocks.size() > 1) {
            // Calculate average alignment
            double totalAlignment = 0.0;
            int perfectCount = 0;
            
            for (int i = 1; i < blocks.size(); i++) {
                Block current = blocks.get(i);
                Block below = blocks.get(i - 1);
                int alignment = current.getAlignmentScore(below);
                
                totalAlignment += alignment;
                if (alignment >= Constants.PERFECT_ALIGNMENT_THRESHOLD) {
                    perfectCount++;
                }
            }
            
            averageAlignment = totalAlignment / (blocks.size() - 1);
            perfectAlignments = perfectCount;
        }
    }
    
    /**
     * Removes blocks that have fallen too far or are no longer part of the tower
     */
    private void removeFailedBlocks() {
        blocks.removeIf(block -> 
            block.getY() > groundLevel + 200 || // Fallen too far below ground
            block.getX() + block.getWidth() < 0 || // Off screen left
            block.getX() > gameWidth // Off screen right
        );
    }
    
    /**
     * Checks if a block collides with any block in the tower
     */
    public boolean hasCollision(Block testBlock) {
        if (testBlock == null) return false;
        
        for (Block block : blocks) {
            if (block != testBlock && block.collidesWith(testBlock)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the topmost block in the tower
     */
    public Block getTopBlock() {
        if (blocks.isEmpty()) return null;
        
        Block topBlock = blocks.get(0);
        for (Block block : blocks) {
            if (block.getY() < topBlock.getY()) {
                topBlock = block;
            }
        }
        return topBlock;
    }
    
    /**
     * Gets the center X coordinate of the tower
     */
    public double getTowerCenterX() {
        if (blocks.isEmpty()) return gameWidth / 2.0;
        
        double totalX = 0.0;
        double totalWeight = 0.0;
        
        for (Block block : blocks) {
            double blockCenter = block.getX() + block.getWidth() / 2;
            double weight = block.getWidth() * block.getHeight(); // Use area as weight
            
            totalX += blockCenter * weight;
            totalWeight += weight;
        }
        
        return totalWeight > 0 ? totalX / totalWeight : gameWidth / 2.0;
    }
    
    /**
     * Gets the height of the tower in pixels
     */
    public int getTowerHeightPixels() {
        if (blocks.isEmpty()) return 0;
        
        Block topBlock = getTopBlock();
        Block bottomBlock = blocks.get(0);
        
        for (Block block : blocks) {
            if (block.getY() + block.getHeight() > bottomBlock.getY() + bottomBlock.getHeight()) {
                bottomBlock = block;
            }
        }
        
        return (int)((bottomBlock.getY() + bottomBlock.getHeight()) - topBlock.getY());
    }
    
    /**
     * Resets the tower for a new game
     */
    public void reset() {
        blocks.clear();
        isStable = true;
        instabilityScore = 0.0;
        tiltAngle = 0.0;
        maxHeight = 0;
        averageAlignment = 0.0;
        perfectAlignments = 0;
    }
    
    /**
     * Gets tower statistics as a formatted string
     */
    public String getStatistics() {
        return String.format(
            "Height: %d blocks | Max: %d | Stability: %.1f%% | Avg Alignment: %.1f%% | Perfect: %d | Tilt: %.1f°",
            blocks.size(), maxHeight, (1.0 - instabilityScore) * 100, 
            averageAlignment, perfectAlignments, Math.toDegrees(tiltAngle)
        );
    }
    
    // Getters
    public List<Block> getBlocks() {
        return new ArrayList<>(blocks); // Return copy to prevent external modification
    }
    
    public int getHeight() {
        return blocks.size();
    }
    
    public int getMaxHeight() {
        return maxHeight;
    }
    
    public boolean isStable() {
        return isStable;
    }
    
    public double getInstabilityScore() {
        return instabilityScore;
    }
    
    public double getTiltAngle() {
        return tiltAngle;
    }
    
    public double getAverageAlignment() {
        return averageAlignment;
    }
    
    public int getPerfectAlignments() {
        return perfectAlignments;
    }
    
    public boolean isEmpty() {
        return blocks.isEmpty();
    }
}
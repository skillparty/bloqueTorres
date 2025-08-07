package com.skillparty.towerblox.utils;

import com.skillparty.towerblox.game.physics.Block;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Object pool for Block instances to reduce garbage collection pressure
 * and improve performance in the game
 * 
 * @author joseAlejandro
 */
public class BlockPool {
    private final List<Block> availableBlocks;
    private final List<Block> usedBlocks;
    private final int maxSize;
    
    // Default values for pooled blocks
    private static final double DEFAULT_WIDTH = 80.0;
    private static final double DEFAULT_HEIGHT = 30.0;
    private static final Color DEFAULT_COLOR = new Color(100, 150, 200);
    
    public BlockPool(int initialSize, int maxSize) {
        this.maxSize = maxSize;
        this.availableBlocks = new ArrayList<>(initialSize);
        this.usedBlocks = new ArrayList<>();
        
        // Pre-fill pool with blocks using proper constructor
        for (int i = 0; i < initialSize; i++) {
            availableBlocks.add(new Block(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_COLOR));
        }
    }
    
    /**
     * Acquire a block from the pool
     */
    public Block acquire(double x, double y, double width, double height, Color color) {
        Block block;
        
        if (!availableBlocks.isEmpty()) {
            block = availableBlocks.remove(availableBlocks.size() - 1);
            // Reset block properties
            resetBlock(block, x, y, width, height, color);
        } else if (usedBlocks.size() < maxSize) {
            // Create new block with proper constructor
            block = new Block(x, y, width, height, color);
        } else {
            // Pool is full, reuse oldest block
            block = usedBlocks.remove(0);
            resetBlock(block, x, y, width, height, color);
        }
        
        usedBlocks.add(block);
        return block;
    }
    
    /**
     * Acquire a block with block type specified
     */
    public Block acquire(double x, double y, double width, double height, Color color, Block.BlockType blockType) {
        Block block;
        
        if (!availableBlocks.isEmpty()) {
            block = availableBlocks.remove(availableBlocks.size() - 1);
            // Reset block properties
            resetBlock(block, x, y, width, height, color);
            block.setBlockType(blockType);
        } else if (usedBlocks.size() < maxSize) {
            // Create new block with proper constructor
            block = new Block(x, y, width, height, color, blockType);
        } else {
            // Pool is full, reuse oldest block
            block = usedBlocks.remove(0);
            resetBlock(block, x, y, width, height, color);
            block.setBlockType(blockType);
        }
        
        usedBlocks.add(block);
        return block;
    }
    
    /**
     * Reset a block's properties for reuse
     */
    private void resetBlock(Block block, double x, double y, double width, double height, Color color) {
        block.setX(x);
        block.setY(y);
        block.setColor(color);
        // Reset velocities to zero
        block.setVelocityX(0);
        block.setVelocityY(0);
        // Note: width and height don't have setters in Block class, so we work with current dimensions
    }
    
    /**
     * Release a block back to the pool
     */
    public void release(Block block) {
        if (block != null && usedBlocks.remove(block)) {
            // Clean up the block state
            cleanupBlock(block);
            
            if (availableBlocks.size() < maxSize) {
                availableBlocks.add(block);
            }
        }
    }
    
    /**
     * Clean up a block's state when returning to pool
     */
    private void cleanupBlock(Block block) {
        // Reset to default state
        block.setVelocityX(0);
        block.setVelocityY(0);
        block.setX(0);
        block.setY(0);
        block.setColor(DEFAULT_COLOR);
    }
    
    /**
     * Clear all blocks from the pool
     */
    public void clear() {
        for (Block block : usedBlocks) {
            cleanupBlock(block);
        }
        usedBlocks.clear();
        availableBlocks.clear();
    }
    
    /**
     * Get pool statistics
     */
    public int getAvailableCount() {
        return availableBlocks.size();
    }
    
    public int getUsedCount() {
        return usedBlocks.size();
    }
    
    public int getTotalCount() {
        return availableBlocks.size() + usedBlocks.size();
    }
    
    /**
     * Check if pool is running low on available blocks
     */
    public boolean isLow() {
        return availableBlocks.size() < maxSize * 0.2; // Less than 20% available
    }
}

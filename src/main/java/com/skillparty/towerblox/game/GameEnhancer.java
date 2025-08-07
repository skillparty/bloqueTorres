package com.skillparty.towerblox.game;

import com.skillparty.towerblox.performance.PerformanceMonitor;
import com.skillparty.towerblox.utils.BlockPool;
import com.skillparty.towerblox.game.physics.Block;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Game enhancer that adds performance monitoring and optimizations
 * to the Tower Bloxx game without modifying the core GameEngine
 * 
 * @author joseAlejandro
 */
public class GameEnhancer {
    private PerformanceMonitor performanceMonitor;
    private BlockPool blockPool;
    private boolean showPerformanceInfo;
    
    // Performance thresholds
    private static final double LOW_FPS_THRESHOLD = 30.0;
    private static final int POOL_SIZE = 100;
    private static final int MAX_POOL_SIZE = 200;
    
    public GameEnhancer() {
        this.performanceMonitor = new PerformanceMonitor();
        this.blockPool = new BlockPool(POOL_SIZE, MAX_POOL_SIZE);
        this.showPerformanceInfo = true;
        
        System.out.println("🎮 GameEnhancer initialized - Performance monitoring active");
    }
    
    /**
     * Called each frame to update performance metrics
     */
    public void updatePerformance() {
        performanceMonitor.update();
        
        // Check for performance issues
        if (performanceMonitor.getCurrentFPS() < LOW_FPS_THRESHOLD) {
            suggestOptimizations();
        }
    }
    
    /**
     * Enhanced block creation using object pooling
     */
    public Block createOptimizedBlock(double x, double y, double width, double height, Color color) {
        return blockPool.acquire(x, y, width, height, color);
    }
    
    /**
     * Enhanced block creation with specific type
     */
    public Block createOptimizedBlock(double x, double y, double width, double height, 
                                    Color color, Block.BlockType blockType) {
        return blockPool.acquire(x, y, width, height, color, blockType);
    }
    
    /**
     * Release block back to pool when no longer needed
     */
    public void releaseBlock(Block block) {
        blockPool.release(block);
    }
    
    /**
     * Render performance information overlay
     */
    public void renderPerformanceOverlay(Graphics2D g2d) {
        if (!showPerformanceInfo) {
            return;
        }
        
        // Save original state
        Color originalColor = g2d.getColor();
        
        // Performance info background
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(10, 10, 220, 100);
        
        // Performance text
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("FPS: %.1f", performanceMonitor.getCurrentFPS()), 20, 30);
        g2d.drawString(String.format("Avg FPS: %.1f", performanceMonitor.getAverageFPS()), 20, 45);
        g2d.drawString(String.format("Frames: %d", performanceMonitor.getFrameCount()), 20, 60);
        g2d.drawString(String.format("Pool Used: %d/%d", 
                      blockPool.getUsedCount(), blockPool.getTotalCount()), 20, 75);
        
        // Performance indicator
        double fps = performanceMonitor.getCurrentFPS();
        if (fps < LOW_FPS_THRESHOLD) {
            g2d.setColor(Color.RED);
            g2d.drawString("⚠️ Low Performance", 20, 90);
        } else if (fps > 55) {
            g2d.setColor(Color.GREEN);
            g2d.drawString("✅ Good Performance", 20, 90);
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("⚡ Normal Performance", 20, 90);
        }
        
        // Enhancement info
        g2d.setColor(Color.CYAN);
        g2d.drawString("F1: Toggle Info | F2: Stats", 20, 105);
        
        // Restore original state
        g2d.setColor(originalColor);
    }
    
    /**
     * Suggest performance optimizations
     */
    private void suggestOptimizations() {
        double fps = performanceMonitor.getCurrentFPS();
        long frameCount = performanceMonitor.getFrameCount();
        
        // Only show suggestions periodically to avoid spam
        if (frameCount % 180 == 0) { // Every 3 seconds at 60 FPS
            if (fps < 20) {
                System.out.println("⚠️ Performance Alert: Very low FPS (" + String.format("%.1f", fps) + 
                                 ") - Consider reducing visual effects");
            } else if (fps < LOW_FPS_THRESHOLD) {
                System.out.println("⚡ Performance Alert: Low FPS (" + String.format("%.1f", fps) + 
                                 ") - Using object pooling: " + blockPool.getUsedCount() + " blocks in pool");
            }
        }
    }
    
    /**
     * Handle keyboard input for enhanced features
     */
    public boolean handleKeyPress(int keyCode) {
        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_F1:
                showPerformanceInfo = !showPerformanceInfo;
                System.out.println("🎮 Performance info " + (showPerformanceInfo ? "enabled" : "disabled"));
                return true;
                
            case java.awt.event.KeyEvent.VK_F2:
                printDetailedStats();
                return true;
                
            case java.awt.event.KeyEvent.VK_F3:
                resetPerformanceStats();
                return true;
                
            case java.awt.event.KeyEvent.VK_F4:
                System.out.println(performanceMonitor.getPerformanceReport());
                return true;
                
            default:
                return false;
        }
    }
    
    /**
     * Print detailed performance statistics
     */
    public void printDetailedStats() {
        System.out.println("\n📊 DETAILED PERFORMANCE STATS:");
        System.out.println("├─ Current FPS: " + String.format("%.2f", performanceMonitor.getCurrentFPS()));
        System.out.println("├─ Average FPS: " + String.format("%.2f", performanceMonitor.getAverageFPS()));
        System.out.println("├─ Frame Count: " + performanceMonitor.getFrameCount());
        System.out.println("├─ Performance Status: " + (performanceMonitor.isPerformanceGood() ? "✅ Good" : "⚠️ Poor"));
        System.out.println("├─ Block Pool Stats:");
        System.out.println("│  ├─ Used: " + blockPool.getUsedCount());
        System.out.println("│  ├─ Available: " + blockPool.getAvailableCount());
        System.out.println("│  └─ Total: " + blockPool.getTotalCount());
        System.out.println("└─ Pool Status: " + (blockPool.isLow() ? "⚠️ Running Low" : "✅ Healthy"));
    }
    
    /**
     * Reset performance statistics
     */
    public void resetPerformanceStats() {
        performanceMonitor.reset();
        System.out.println("🔄 Performance stats reset");
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        blockPool.clear();
        System.out.println("🧹 GameEnhancer cleaned up");
    }
    
    // Getters
    public PerformanceMonitor getPerformanceMonitor() { return performanceMonitor; }
    public BlockPool getBlockPool() { return blockPool; }
    public boolean isShowingPerformanceInfo() { return showPerformanceInfo; }
    
    // Setters
    public void setShowPerformanceInfo(boolean show) { this.showPerformanceInfo = show; }
}

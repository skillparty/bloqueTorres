package com.skillparty.towerblox.game;

import com.skillparty.towerblox.game.GameEnhancer;
import com.skillparty.towerblox.game.physics.Block;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Demonstration of GameEnhancer integration
 * Shows how to use the enhanced features in the game
 * 
 * @author joseAlejandro
 */
public class EnhancedGameDemo extends JPanel implements KeyListener {
    private GameEnhancer gameEnhancer;
    private Timer gameTimer;
    private boolean running;
    
    // Sample game objects for demonstration
    private java.util.List<Block> activeBlocks;
    
    public EnhancedGameDemo() {
        this.gameEnhancer = new GameEnhancer();
        this.activeBlocks = new java.util.ArrayList<>();
        this.running = false;
        
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        // Game loop timer (60 FPS target)
        gameTimer = new Timer(16, e -> {
            update();
            repaint();
        });
        
        System.out.println("🎮 Enhanced Game Demo initialized");
        System.out.println("📋 Controls:");
        System.out.println("   SPACE: Start/Stop demo");
        System.out.println("   B: Create optimized block");
        System.out.println("   C: Clear blocks");
        System.out.println("   F1: Toggle performance info");
        System.out.println("   F2: Show detailed stats");
        System.out.println("   F3: Reset performance stats");
        System.out.println("   F4: Show performance report");
    }
    
    private void update() {
        if (!running) return;
        
        // Update performance monitoring
        gameEnhancer.updatePerformance();
        
        // Update blocks (simple physics simulation)
        for (Block block : activeBlocks) {
            block.update();
        }
        
        // Clean up blocks that are off screen (demonstration of block pooling)
        activeBlocks.removeIf(block -> {
            if (block.getY() > getHeight() + 100) {
                gameEnhancer.releaseBlock(block);
                System.out.println("🔄 Block returned to pool");
                return true;
            }
            return false;
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable anti-aliasing for better visuals
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Clear background
        g2d.setColor(new Color(25, 25, 50));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw demo info
        g2d.setColor(Color.WHITE);
        g2d.drawString("🏗️ Enhanced Tower Bloxx Demo by joseAlejandro", 10, 30);
        g2d.drawString("Status: " + (running ? "Running" : "Stopped"), 10, 50);
        g2d.drawString("Active Blocks: " + activeBlocks.size(), 10, 70);
        
        // Render blocks
        for (Block block : activeBlocks) {
            block.render(g2d);
        }
        
        // Render performance overlay using GameEnhancer
        gameEnhancer.renderPerformanceOverlay(g2d);
        
        // Draw controls info
        if (!running) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("Press SPACE to start the demo", getWidth()/2 - 100, getHeight()/2);
            g2d.drawString("Press B to create blocks, C to clear", getWidth()/2 - 110, getHeight()/2 + 20);
        }
        
        g2d.dispose();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Let GameEnhancer handle its keys first
        if (gameEnhancer.handleKeyPress(e.getKeyCode())) {
            return;
        }
        
        // Handle demo-specific keys
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                toggleDemo();
                break;
                
            case KeyEvent.VK_B:
                createOptimizedBlock();
                break;
                
            case KeyEvent.VK_C:
                clearBlocks();
                break;
        }
    }
    
    private void toggleDemo() {
        running = !running;
        if (running) {
            gameTimer.start();
            System.out.println("▶️ Demo started");
        } else {
            gameTimer.stop();
            System.out.println("⏸️ Demo stopped");
        }
    }
    
    private void createOptimizedBlock() {
        // Create block using GameEnhancer's optimized method
        double x = Math.random() * (getWidth() - 60);
        double y = -30;
        double width = 60 + Math.random() * 40;
        double height = 25 + Math.random() * 15;
        Color color = new Color(
            (int)(Math.random() * 255),
            (int)(Math.random() * 255), 
            (int)(Math.random() * 255)
        );
        
        Block block = gameEnhancer.createOptimizedBlock(x, y, width, height, color);
        
        // Add some downward velocity
        block.setVelocityY(2 + Math.random() * 3);
        
        activeBlocks.add(block);
        System.out.println("🧱 Optimized block created (Pool usage: " + 
                          gameEnhancer.getBlockPool().getUsedCount() + "/" + 
                          gameEnhancer.getBlockPool().getTotalCount() + ")");
    }
    
    private void clearBlocks() {
        for (Block block : activeBlocks) {
            gameEnhancer.releaseBlock(block);
        }
        activeBlocks.clear();
        System.out.println("🧹 All blocks cleared and returned to pool");
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    // Create and show the demo window
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Enhanced Tower Bloxx Demo");
            EnhancedGameDemo demo = new EnhancedGameDemo();
            
            frame.add(demo);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Request focus for key events
            demo.requestFocusInWindow();
        });
    }
}

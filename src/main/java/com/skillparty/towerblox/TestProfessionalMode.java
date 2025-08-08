package com.skillparty.towerblox;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.game.GameEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Test launcher specifically for Professional Mode debugging
 */
public class TestProfessionalMode {
    public static void main(String[] args) {
        System.out.println("🔧 DEBUG: Professional Mode Block Generation Test");
        System.out.println("================================================");
        
        SwingUtilities.invokeLater(() -> {
            
            // Create game engine
            GameEngine engine = new GameEngine();
            
            // Force professional mode immediately
            System.out.println("🏆 FORCING Professional Mode for DEBUG...");
            engine.startNewGame(DifficultyLevel.PROFESSIONAL);
            
            // Create a simple test window
            JFrame frame = new JFrame("Professional Mode Debug Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
            
            // Create simple game panel
            JPanel gamePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Clear background
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Render game
                    engine.render(g2d);
                    
                    // Show debug info
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Monospace", Font.BOLD, 16));
                    g2d.drawString("Professional Mode Debug Test", 20, 30);
                    g2d.drawString("Press SPACE to drop blocks", 20, 60);
                    g2d.drawString("Watch console for debug logs", 20, 90);
                }
            };
            
            // Add key listener for space bar
            gamePanel.addKeyListener(engine);
            gamePanel.setFocusable(true);
            
            frame.add(gamePanel);
            frame.setVisible(true);
            gamePanel.requestFocusInWindow();
            
            // Start game loop
            Timer gameTimer = new Timer(16, e -> {
                engine.gameLoop();
                gamePanel.repaint();
            });
            gameTimer.start();
            
            System.out.println("✅ Test started. Game should be in Professional Mode.");
            System.out.println("📋 Instructions:");
            System.out.println("   - Watch console for debug logs");
            System.out.println("   - Press SPACE to drop blocks");
            System.out.println("   - Check if new blocks are generated after dropping");
        });
    }
}

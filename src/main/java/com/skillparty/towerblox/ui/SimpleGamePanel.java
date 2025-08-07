package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.GameEngine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * SIMPLIFIED GAME PANEL - CLEAN AND FOCUSED
 * Shows only essential information without clutter
 */
public class SimpleGamePanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    
    private GameWindow parentWindow;
    private GameEngine gameEngine;
    
    // Essential game state
    private int currentScore = 0;
    private boolean gameOverShown = false;
    private String gameOverReason = "";
    private int finalScore = 0;
    
    public SimpleGamePanel(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        initializePanel();
        setupTimer();
    }
    
    private void initializePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        addKeyListener(this);
    }
    
    private void setupTimer() {
        Timer renderTimer = new Timer(16, e -> { // ~60 FPS
            if (gameEngine != null) {
                gameEngine.gameLoop();
            }
            repaint();
        });
        renderTimer.start();
    }
    
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    public void updateScore(int newScore) {
        this.currentScore = newScore;
    }
    
    public void showGameOver(String reason, int finalScore) {
        this.gameOverShown = true;
        this.gameOverReason = reason;
        this.finalScore = finalScore;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (gameEngine != null) {
            // Render game world
            gameEngine.render(g2d);
            
            // Render ONLY essential UI
            renderEssentialUI(g2d);
            
            if (gameOverShown) {
                renderGameOverOverlay(g2d);
            }
        } else {
            renderLoadingScreen(g2d);
        }
        
        g2d.dispose();
    }
    
    /**
     * Renders ONLY the most essential UI elements
     */
    private void renderEssentialUI(Graphics2D g2d) {
        // Clean, minimal HUD in top-left corner
        renderMinimalHUD(g2d);
        
        // Simple controls reminder at bottom
        renderSimpleControls(g2d);
    }
    
    private void renderMinimalHUD(Graphics2D g2d) {
        // Semi-transparent background
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(10, 10, 200, 80, 10, 10);
        
        // Clean border
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(10, 10, 200, 80, 10, 10);
        
        // Score - most important info
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + String.format("%,d", currentScore), 20, 35);
        
        // Tower height - second most important
        if (gameEngine != null && gameEngine.getTower() != null) {
            int height = gameEngine.getTower().getHeight();
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.setColor(Color.CYAN);
            g2d.drawString("Height: " + height + " floors", 20, 55);
            
            // Simple progress bar
            int barWidth = 160;
            int barHeight = 6;
            int barX = 20;
            int barY = 65;
            
            // Background
            g2d.setColor(new Color(60, 60, 60));
            g2d.fillRoundRect(barX, barY, barWidth, barHeight, 3, 3);
            
            // Progress (assuming max 100 floors for visualization)
            double progress = Math.min(1.0, height / 100.0);
            int progressWidth = (int)(barWidth * progress);
            
            Color progressColor = height < 25 ? Color.GREEN : 
                                 height < 50 ? Color.YELLOW : 
                                 height < 75 ? Color.ORANGE : Color.RED;
            
            g2d.setColor(progressColor);
            g2d.fillRoundRect(barX, barY, progressWidth, barHeight, 3, 3);
        }
    }
    
    private void renderSimpleControls(Graphics2D g2d) {
        // Simple control reminder at bottom center
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(getWidth()/2 - 100, getHeight() - 35, 200, 25, 10, 10);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.WHITE);
        String controls = "SPACE: Drop Block  |  ESC: Menu";
        FontMetrics fm = g2d.getFontMetrics();
        int controlsWidth = fm.stringWidth(controls);
        g2d.drawString(controls, (getWidth() - controlsWidth) / 2, getHeight() - 15);
    }
    
    private void renderGameOverOverlay(Graphics2D g2d) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Game Over text
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.setColor(Color.RED);
        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        g2d.drawString(gameOverText, (getWidth() - textWidth) / 2, getHeight() / 2 - 50);
        
        // Final score
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(Color.WHITE);
        String scoreText = "Final Score: " + String.format("%,d", finalScore);
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(scoreText);
        g2d.drawString(scoreText, (getWidth() - textWidth) / 2, getHeight() / 2);
        
        // Continue instruction
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String continueText = "Press ESC to continue";
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(continueText);
        g2d.drawString(continueText, (getWidth() - textWidth) / 2, getHeight() / 2 + 40);
    }
    
    private void renderLoadingScreen(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(Color.WHITE);
        String loadingText = "Loading...";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(loadingText);
        g2d.drawString(loadingText, (getWidth() - textWidth) / 2, getHeight() / 2);
    }
    
    // KeyListener implementation - SIMPLIFIED
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameEngine == null) return;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (!gameOverShown) {
                    gameEngine.keyPressed(e);
                }
                break;
                
            case KeyEvent.VK_ESCAPE:
                if (gameOverShown) {
                    gameOverShown = false;
                    parentWindow.returnToMenu();
                } else {
                    parentWindow.returnToMenu();
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed for this simple implementation
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for this simple implementation
    }
    
    // Compatibility methods for existing code
    public void showBlockPlacedEffect(int score, int combo) {
        // Simple implementation - just update score
        updateScore(currentScore + score);
    }
    
    public void showMilestoneEffect(int milestone) {
        // Simple implementation - no complex effects
    }
}
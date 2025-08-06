package com.skillparty.towerblox.integration;

import com.skillparty.towerblox.core.TowerBloxxGame;
import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.ui.GameWindow;
import com.skillparty.towerblox.ui.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * Adapter to integrate the refactored TowerBloxxGame with existing UI
 * Bridges the new architecture with legacy GameWindow/GamePanel
 * 
 * @author joseAlejandro
 * @version 1.0
 */
public class RefactoredGameAdapter extends JPanel {
    
    // Refactored game instance
    private TowerBloxxGame refactoredGame;
    
    // Legacy compatibility
    private GameWindow parentWindow;
    private boolean useRefactored = true;
    private GameEngine legacyEngine;
    
    // Thread management
    private Thread gameThread;
    private volatile boolean running = false;
    
    /**
     * Creates the adapter
     */
    public RefactoredGameAdapter(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        
        // Check if we should use refactored version
        String useRefactoredProp = System.getProperty("use.refactored", "true");
        useRefactored = Boolean.parseBoolean(useRefactoredProp);
        
        if (useRefactored) {
            initializeRefactoredGame();
        } else {
            initializeLegacyGame();
        }
    }
    
    /**
     * Initializes the refactored game
     */
    private void initializeRefactoredGame() {
        System.out.println("🚀 Initializing Refactored javaBloxx 2.0");
        
        // Create refactored game instance
        refactoredGame = new TowerBloxxGame();
        
        // Add to panel
        setLayout(new BorderLayout());
        add(refactoredGame, BorderLayout.CENTER);
        
        // Transfer key listeners
        for (KeyListener kl : refactoredGame.getKeyListeners()) {
            addKeyListener(kl);
        }
    }
    
    /**
     * Initializes legacy game for fallback
     */
    private void initializeLegacyGame() {
        System.out.println("📦 Using Legacy Game Engine");
        
        // Create legacy game panel
        GamePanel legacyPanel = new GamePanel(parentWindow);
        setLayout(new BorderLayout());
        add(legacyPanel, BorderLayout.CENTER);
    }
    
    /**
     * Starts a new game
     */
    public void startNewGame(DifficultyLevel difficulty) {
        if (useRefactored) {
            System.out.println("🎮 Starting Refactored Game - Difficulty: " + difficulty);
            // Refactored game handles its own thread
            SwingUtilities.invokeLater(() -> {
                refactoredGame.requestFocusInWindow();
            });
        } else {
            System.out.println("🎮 Starting Legacy Game - Difficulty: " + difficulty);
            // Legacy game handling
            if (parentWindow != null) {
                parentWindow.startNewGame(difficulty);
            }
        }
    }
    
    /**
     * Pauses the game
     */
    public void pauseGame() {
        if (useRefactored && refactoredGame != null) {
            // Send pause key event
            refactoredGame.dispatchEvent(new java.awt.event.KeyEvent(
                refactoredGame, 
                java.awt.event.KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                java.awt.event.KeyEvent.VK_P,
                'P'
            ));
        }
    }
    
    /**
     * Resumes the game
     */
    public void resumeGame() {
        pauseGame(); // Toggle pause
    }
    
    /**
     * Stops the game
     */
    public void stopGame() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Gets current score
     */
    public int getScore() {
        // Would need to expose from refactored game
        return 0;
    }
    
    /**
     * Gets tower height
     */
    public int getTowerHeight() {
        // Would need to expose from refactored game
        return 0;
    }
    
    /**
     * Checks if using refactored version
     */
    public boolean isUsingRefactored() {
        return useRefactored;
    }
    
    /**
     * Switches between refactored and legacy
     */
    public void toggleGameVersion() {
        useRefactored = !useRefactored;
        removeAll();
        
        if (useRefactored) {
            initializeRefactoredGame();
        } else {
            initializeLegacyGame();
        }
        
        revalidate();
        repaint();
        
        System.out.println("Switched to: " + (useRefactored ? "Refactored 2.0" : "Legacy") + " version");
    }
}

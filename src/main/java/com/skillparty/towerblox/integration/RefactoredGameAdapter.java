package com.skillparty.towerblox.integration;

import com.skillparty.towerblox.ui.GameWindow;
import com.skillparty.towerblox.ui.MenuPanel;
import com.skillparty.towerblox.ui.GamePanel;
import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.core.TowerBloxxGame;

import javax.swing.*;
import java.awt.*;

/**
 * Enhanced adapter with Professional Mode integration using your CraneSystem
 * 
 * @author joseAlejandro
 * @version 2.0 - Professional Mode Enhanced
 */
public class RefactoredGameAdapter extends JPanel {
    private static final String MENU_CARD = "MENU";
    private static final String GAME_CARD = "GAME";
    private static final String PROFESSIONAL_CARD = "PROFESSIONAL";
    
    private CardLayout cardLayout;
    private GameWindow parentWindow;
    private GameWindow gameWindowProxy;
    
    // UI Components
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private TowerBloxxGame professionalGame; // Your advanced system
    
    /**
     * Constructor
     */
    public RefactoredGameAdapter(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        
        // Create a proxy GameWindow that delegates returnToMenu to this adapter
        this.gameWindowProxy = new GameWindowProxy();
        
        // Setup card layout
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        
        initializeComponents();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        System.out.println("🚀 Initializing Enhanced RefactoredGameAdapter with Professional Mode");
        
        // Create menu panel using the proxy
        menuPanel = new MenuPanel(gameWindowProxy);
        
        // Create game panel using the proxy (initially hidden)
        gamePanel = new GamePanel(gameWindowProxy);
        
        // Add panels to card layout
        add(menuPanel, MENU_CARD);
        add(gamePanel, GAME_CARD);
        
        // Start with menu
        cardLayout.show(this, MENU_CARD);
        
        // Ensure menu panel gets focus for mouse events
        SwingUtilities.invokeLater(() -> {
            menuPanel.requestFocusInWindow();
            menuPanel.repaint();
            System.out.println("🎯 Menu panel focused and ready for clicks!");
        });
    }
    
    /**
     * Starts a new game - detects PROFESSIONAL mode for your CraneSystem
     */
    public void startNewGame(DifficultyLevel difficulty) {
        System.out.println("🎮 Starting new game with difficulty: " + difficulty.getDisplayName());
        
        // Check if it's PROFESSIONAL mode - use your advanced system
        if (difficulty == DifficultyLevel.PROFESSIONAL) {
            System.out.println("🏆 ACTIVATING YOUR PROFESSIONAL MODE - Advanced CraneSystem!");
            startProfessionalMode();
            return;
        }
        
        // For other modes, use standard GameEngine
        System.out.println("🎮 Using standard GameEngine for " + difficulty.getDisplayName());
        GameEngine gameEngine = new GameEngine();
        
        // Start the game with the selected difficulty
        gameEngine.startNewGame(difficulty);
        
        // Set the GameEngine to the GamePanel
        gamePanel.setGameEngine(gameEngine);
        
        // Show the game panel
        showGamePanel();
    }
    
    /**
     * Starts your advanced professional mode with CraneSystem
     */
    private void startProfessionalMode() {
        System.out.println("🚀 Launching YOUR TowerBloxxGame with advanced physics...");
        
        // Remove existing professional game if any
        if (professionalGame != null) {
            remove(professionalGame);
        }
        
        // Create new professional game instance
        professionalGame = new TowerBloxxGame();
        
        // Add to card layout
        add(professionalGame, PROFESSIONAL_CARD);
        
        // Show professional mode
        cardLayout.show(this, PROFESSIONAL_CARD);
        
        // Ensure it gets focus
        SwingUtilities.invokeLater(() -> {
            professionalGame.requestFocusInWindow();
        });
        
        System.out.println("✨ YOUR Professional mode activated!");
        System.out.println("🏗️ CraneSystem with realistic pendulum physics");
        System.out.println("🎪 Animated hydraulic claw system");
        System.out.println("⚡ Advanced PhysicsEngine");
        System.out.println("🔄 Continuous block generation");
    }
    
    /**
     * Shows the game panel (called from menu)
     */
    public void showGamePanel() {
        cardLayout.show(this, GAME_CARD);
        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
        });
    }
    
    /**
     * Shows the menu panel (called from menu)
     */
    public void showMenuPanel() {
        cardLayout.show(this, MENU_CARD);
        SwingUtilities.invokeLater(() -> {
            menuPanel.requestFocusInWindow();
        });
    }
    
    /**
     * Returns to the main menu from game (ESC key functionality)
     */
    public void returnToMenu() {
        System.out.println("🔙 Returning to main menu...");
        
        // Stop professional game if running
        if (professionalGame != null) {
            System.out.println("🏆 Stopping your professional mode...");
        }
        
        showMenuPanel();
    }
    
    /**
     * Gets the current game panel
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }
    
    /**
     * Gets the current menu panel
     */
    public MenuPanel getMenuPanel() {
        return menuPanel;
    }
    
    /**
     * Proxy class to delegate method calls between GameWindow and RefactoredGameAdapter
     */
    private class GameWindowProxy extends GameWindow {
        @Override
        public void returnToMenu() {
            RefactoredGameAdapter.this.returnToMenu();
        }
        
        @Override
        public void showMovements() {
            parentWindow.showMovements();
        }
        
        @Override
        public void showHighScores() {
            parentWindow.showHighScores();
        }
        
        @Override
        public void exitGame() {
            parentWindow.exitGame();
        }
        
        @Override
        public void startNewGame(DifficultyLevel difficulty) {
            RefactoredGameAdapter.this.startNewGame(difficulty);
        }
    }
}

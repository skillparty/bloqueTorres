package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.game.GameState;
import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.game.MovementRecorder;
import com.skillparty.towerblox.ui.components.FontManager;
import com.skillparty.towerblox.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main game window that manages all UI panels and game state
 */
public class GameWindow extends JFrame implements GameEngine.GameStateListener {
    private static final long serialVersionUID = 1L;
    
    // UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private ScorePanel scorePanel;
    private HighScorePanel highScorePanel;
    private MovementPanel movementPanel;
    
    // Game engine
    private GameEngine gameEngine;
    private Thread gameThread;
    
    // Font manager
    private FontManager fontManager;
    
    // Window properties
    private static final int WINDOW_WIDTH = Constants.GAME_WIDTH;
    private static final int WINDOW_HEIGHT = Constants.GAME_HEIGHT + 50; // Extra space for title bar
    
    public GameWindow() {
        initializeWindow();
        initializeComponents();
        setupLayout();
        setupGameEngine();
        
        // Show menu initially
        showPanel("MENU");
        
        System.out.println("Tower Bloxx game window initialized");
    }
    
    /**
     * Initializes the main window properties
     */
    private void initializeWindow() {
        setTitle("Tower Bloxx - Java Edition");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        
        // Set window icon (if available)
        try {
            // You could add an icon here if you have one
            // setIconImage(ImageIO.read(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon not available, continue without it
        }
        
        // Initialize font manager
        fontManager = FontManager.getInstance();
        
        // Set global font
        setUIFont(fontManager.getGameFont());
        
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }
    
    /**
     * Initializes all UI components
     */
    private void initializeComponents() {
        // Create main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.BLACK);
        
        // Create individual panels
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        scorePanel = new ScorePanel(this);
        highScorePanel = new HighScorePanel(this);
        movementPanel = new MovementPanel(this);
        
        // Add panels to card layout
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(scorePanel, "SCORE");
        mainPanel.add(highScorePanel, "HIGH_SCORES");
        mainPanel.add(movementPanel, "MOVEMENTS");
    }
    
    /**
     * Sets up the main layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        // Create status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the status bar
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(Color.DARK_GRAY);
        statusBar.setPreferredSize(new Dimension(WINDOW_WIDTH, 25));
        
        JLabel statusLabel = new JLabel("Ready to play Tower Bloxx!");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(fontManager.getSmallFont());
        
        statusBar.add(statusLabel);
        
        return statusBar;
    }
    
    /**
     * Sets up the game engine
     */
    private void setupGameEngine() {
        gameEngine = new GameEngine();
        gameEngine.setStateListener(this);
        
        // Pass game engine to panels that need it
        gamePanel.setGameEngine(gameEngine);
        scorePanel.setGameEngine(gameEngine);
        highScorePanel.setGameEngine(gameEngine);
    }
    
    /**
     * Shows a specific panel
     */
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        
        // Update focus and key listeners
        switch (panelName) {
            case "GAME":
                gamePanel.requestFocusInWindow();
                break;
            case "MENU":
                menuPanel.requestFocusInWindow();
                break;
            case "SCORE":
                scorePanel.requestFocusInWindow();
                break;
            case "HIGH_SCORES":
                highScorePanel.requestFocusInWindow();
                break;
        }
        
        repaint();
    }
    
    /**
     * Starts a new game with the specified difficulty
     */
    public void startNewGame(DifficultyLevel difficulty) {
        // Stop any existing game thread
        stopGameThread();
        
        // Start new game
        gameEngine.startNewGame(difficulty);
        showPanel("GAME");
        
        // Game loop is handled by GamePanel timer
        
        System.out.println("New game started with difficulty: " + difficulty.getDisplayName());
    }
    
    /**
     * Shows the high scores
     */
    public void showHighScores() {
        highScorePanel.refreshScores();
        showPanel("HIGH_SCORES");
    }
    
    /**
     * Shows the movement recorder
     */
    public void showMovements() {
        // Conectar el MovementRecorder del GameEngine con el MovementPanel
        if (gameEngine != null && movementPanel != null) {
            System.out.println("🔗 Conectando MovementRecorder...");
            MovementRecorder recorder = gameEngine.getMovementRecorder();
            if (recorder != null) {
                movementPanel.setMovementRecorder(recorder);
                System.out.println("✅ MovementRecorder conectado exitosamente");
            } else {
                System.out.println("❌ GameEngine.getMovementRecorder() devolvió null");
            }
        } else {
            System.out.println("❌ GameEngine o MovementPanel son null");
        }
        showPanel("MOVEMENTS");
        
        // Asegurar que el MovementPanel tenga el foco para recibir eventos de teclado
        SwingUtilities.invokeLater(() -> {
            if (movementPanel != null) {
                movementPanel.requestFocusInWindow();
                System.out.println("🎯 Foco solicitado para MovementPanel");
            }
        });
    }
    
    /**
     * Shows the main menu
     */
    public void showMenu() {
        showPanel("MENU");
    }
    
    /**
     * Returns to the main menu
     */
    public void returnToMenu() {
        stopGameThread();
        gameEngine.returnToMenu();
        showPanel("MENU");
    }
    
    /**
     * Starts the game thread
     */
    private void startGameThread() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(() -> {
                gameEngine.gameLoop();
            }, "GameThread");
            gameThread.setDaemon(true);
            gameThread.start();
        }
    }
    
    /**
     * Stops the game thread
     */
    private void stopGameThread() {
        if (gameThread != null && gameThread.isAlive()) {
            gameEngine.stopEngine();
            try {
                gameThread.join(1000); // Wait up to 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Handles window closing event
     */
    private void handleWindowClosing() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit Tower Bloxx?",
            "Exit Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            stopGameThread();
            System.exit(0);
        }
    }
    
    /**
     * Sets the UI font globally
     */
    private void setUIFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("List.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
    }
    
    // GameEngine.GameStateListener implementation
    @Override
    public void onStateChanged(GameState newState) {
        SwingUtilities.invokeLater(() -> {
            switch (newState) {
                case MENU:
                    showPanel("MENU");
                    break;
                case PLAYING:
                    showPanel("GAME");
                    break;
                case GAME_OVER:
                    // Check if score qualifies for high score table
                    if (gameEngine.getScoreStorage().qualifiesForHighScore(
                            gameEngine.getScoreManager().calculateFinalScore())) {
                        showPanel("SCORE");
                    } else {
                        // Show game over dialog and return to menu
                        showGameOverDialog();
                    }
                    break;
                case HIGH_SCORES:
                    showHighScores();
                    break;
            }
        });
    }
    
    @Override
    public void onScoreChanged(int newScore) {
        // Update score display in game panel
        SwingUtilities.invokeLater(() -> {
            gamePanel.updateScore(newScore);
        });
    }
    
    @Override
    public void onGameOver(String reason, int finalScore) {
        SwingUtilities.invokeLater(() -> {
            gamePanel.showGameOver(reason, finalScore);
        });
    }
    
    @Override
    public void onBlockPlaced(int score, int combo) {
        SwingUtilities.invokeLater(() -> {
            gamePanel.showBlockPlacedEffect(score, combo);
        });
    }
    
    /**
     * Shows game over dialog for non-qualifying scores
     */
    private void showGameOverDialog() {
        int finalScore = gameEngine.getScoreManager().calculateFinalScore();
        String message = String.format(
            "Game Over!\n\nFinal Score: %d\nDifficulty: %s\n%s\n\nTry again to beat your score!",
            finalScore,
            gameEngine.getCurrentDifficulty().getDisplayName(),
            gameEngine.getScoreManager().getGameSummary()
        );
        
        int option = JOptionPane.showOptionDialog(
            this,
            message,
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Play Again", "Main Menu"},
            "Play Again"
        );
        
        if (option == 0) {
            // Play again with same difficulty
            startNewGame(gameEngine.getCurrentDifficulty());
        } else {
            // Return to menu
            returnToMenu();
        }
    }
    
    // Getters for panels (used by child panels)
    public GameEngine getGameEngine() { return gameEngine; }
    public FontManager getFontManager() { return fontManager; }
    
    // Utility methods for child panels
    public void exitGame() {
        handleWindowClosing();
    }
    
    public void pauseGame() {
        if (gameEngine != null) {
            gameEngine.pauseGame();
        }
    }
    
    public void resumeGame() {
        if (gameEngine != null) {
            gameEngine.resumeGame();
        }
    }
}
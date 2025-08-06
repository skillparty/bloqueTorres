package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.ui.components.TowerVisualizationPanel;
// import com.skillparty.towerblox.ui.components.FontManager;
// import com.skillparty.towerblox.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Game panel that renders the actual gameplay and handles input
 */
public class GamePanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    
    private GameWindow parentWindow;
    // private FontManager fontManager;
    private GameEngine gameEngine;
    private TowerVisualizationPanel towerVisualizationPanel;
    
    // Game state display
    private int currentScore = 0;
    private boolean gameOverShown = false;
    private String gameOverReason = "";
    private int finalScore = 0;
    
    // Visual effects
    private List<ScoreEffect> scoreEffects;
    private List<ComboEffect> comboEffects;
    
    // Pause overlay
    private boolean showPauseOverlay = false;
    
    // Performance monitoring
    private long lastRenderTime = 0;
    private int frameCount = 0;
    private double renderFPS = 0;
    
    /**
     * Visual effect for score display
     */
    private static class ScoreEffect {
        int x, y;
        int score;
        long startTime;
        Color color;
        
        ScoreEffect(int x, int y, int score, Color color) {
            this.x = x;
            this.y = y;
            this.score = score;
            this.color = color;
            this.startTime = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - startTime > 2000; // 2 seconds
        }
        
        float getAlpha() {
            long elapsed = System.currentTimeMillis() - startTime;
            return Math.max(0, 1.0f - elapsed / 2000.0f);
        }
        
        int getCurrentY() {
            long elapsed = System.currentTimeMillis() - startTime;
            return y - (int)(elapsed / 10); // Move up over time
        }
    }
    
    /**
     * Visual effect for combo display
     */
    private static class ComboEffect {
        int x, y;
        int combo;
        long startTime;
        
        ComboEffect(int x, int y, int combo) {
            this.x = x;
            this.y = y;
            this.combo = combo;
            this.startTime = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - startTime > 1500; // 1.5 seconds
        }
        
        float getScale() {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed < 300) {
                return 1.0f + (elapsed / 300.0f) * 0.5f; // Scale up
            } else {
                return 1.5f - ((elapsed - 300) / 1200.0f) * 0.5f; // Scale down
            }
        }
        
        float getAlpha() {
            long elapsed = System.currentTimeMillis() - startTime;
            return Math.max(0, 1.0f - elapsed / 1500.0f);
        }
    }
    
    public GamePanel(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        // this.fontManager = FontManager.getInstance();
        this.scoreEffects = new ArrayList<>();
        this.comboEffects = new ArrayList<>();
        
        initializePanel();
        setupTimer();
    }
    
    /**
     * Initializes the panel properties
     */
    private void initializePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800 + 200, 600)); // Increased width for stats panel
        setFocusable(true);
        addKeyListener(this);
        
        // Use BorderLayout to position the stats panel
        setLayout(new BorderLayout());
    }
    
    /**
     * Sets up the rendering timer
     */
    private void setupTimer() {
        Timer renderTimer = new Timer(16, e -> { // ~60 FPS
            if (gameEngine != null) {
                gameEngine.gameLoop(); // Update game logic
            }
            repaint(); // Render
            updateEffects();
            updatePerformanceStats();
        });
        renderTimer.start();
    }
    
    /**
     * Sets the game engine reference
     */
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        
        // Initialize tower stats panel when game engine is set
        if (gameEngine != null) {
            initializeTowerStatsPanel();
        }
    }
    
    /**
     * Initializes the tower visualization panel
     */
    private void initializeTowerStatsPanel() {
        // Remove existing visualization panel if any
        if (towerVisualizationPanel != null) {
            remove(towerVisualizationPanel);
        }
        
        // Create new visualization panel
        towerVisualizationPanel = new TowerVisualizationPanel(gameEngine);
        
        // Add to the left side
        add(towerVisualizationPanel, BorderLayout.WEST);
        
        // Revalidate to update layout
        revalidate();
    }
    
    /**
     * Updates the current score display
     */
    public void updateScore(int newScore) {
        this.currentScore = newScore;
    }
    
    /**
     * Shows game over state
     */
    public void showGameOver(String reason, int finalScore) {
        this.gameOverShown = true;
        this.gameOverReason = reason;
        this.finalScore = finalScore;
    }
    
    /**
     * Shows visual effect when a block is placed
     */
    public void showBlockPlacedEffect(int score, int combo) {
        if (gameEngine != null && gameEngine.getCrane() != null) {
            int x = (int) gameEngine.getCrane().getX();
            int y = (int) gameEngine.getCrane().getY() + 100;
            
            // Add score effect
            Color scoreColor = score > 500 ? Color.YELLOW : Color.WHITE;
            scoreEffects.add(new ScoreEffect(x, y, score, scoreColor));
            
            // Add combo effect if combo > 1
            if (combo > 1) {
                comboEffects.add(new ComboEffect(x + 50, y - 20, combo));
            }
        }
    }
    
    /**
     * Updates visual effects
     */
    private void updateEffects() {
        // Remove expired effects
        scoreEffects.removeIf(ScoreEffect::isExpired);
        comboEffects.removeIf(ComboEffect::isExpired);
    }
    
    /**
     * Updates performance statistics
     */
    private void updatePerformanceStats() {
        long currentTime = System.currentTimeMillis();
        frameCount++;
        
        if (currentTime - lastRenderTime >= 1000) { // Update every second
            renderFPS = frameCount * 1000.0 / (currentTime - lastRenderTime);
            frameCount = 0;
            lastRenderTime = currentTime;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (gameEngine != null) {
            // Render game
            gameEngine.render(g2d);
            
            // Render UI overlays
            renderGameUI(g2d);
            renderEffects(g2d);
            
            if (gameEngine.isPaused() || showPauseOverlay) {
                renderPauseOverlay(g2d);
            }
            
            if (gameOverShown) {
                renderGameOverOverlay(g2d);
            }
        } else {
            // No game engine - show loading
            renderLoadingScreen(g2d);
        }
        
        g2d.dispose();
    }
    
    /**
     * Renders the simplified game UI
     */
    private void renderGameUI(Graphics2D g2d) {
        // Controls help (bottom)
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g2d.setColor(Color.LIGHT_GRAY);
        String controls = "ESPACIO: Soltar | P: Pausa | ESC: Menú";
        FontMetrics fm = g2d.getFontMetrics();
        int controlsWidth = fm.stringWidth(controls);
        g2d.drawString(controls, (getWidth() - controlsWidth) / 2, getHeight() - 10);
    }
    

    
    /**
     * Renders visual effects
     */
    private void renderEffects(Graphics2D g2d) {
        // Render score effects
        for (ScoreEffect effect : scoreEffects) {
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            
            // Set color with alpha
            Color color = effect.color;
            float alpha = effect.getAlpha();
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 
                                 (int)(alpha * 255)));
            
            String scoreText = "+" + effect.score;
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(scoreText);
            
            g2d.drawString(scoreText, effect.x - textWidth / 2, effect.getCurrentY());
        }
        
        // Render combo effects
        for (ComboEffect effect : comboEffects) {
            float scale = effect.getScale();
            float alpha = effect.getAlpha();
            
            Font originalFont = g2d.getFont();
            Font scaledFont = originalFont.deriveFont(originalFont.getSize2D() * scale);
            g2d.setFont(scaledFont);
            
            g2d.setColor(new Color(255, 215, 0, (int)(alpha * 255))); // Gold color
            
            String comboText = "COMBO x" + effect.combo + "!";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(comboText);
            
            g2d.drawString(comboText, effect.x - textWidth / 2, effect.y);
            
            g2d.setFont(originalFont);
        }
    }
    
    /**
     * Renders pause overlay
     */
    private void renderPauseOverlay(Graphics2D g2d) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Pause text
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g2d.setColor(Color.WHITE);
        
        String pauseText = "JUEGO PAUSADO";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(pauseText);
        int textHeight = fm.getHeight();
        
        g2d.drawString(pauseText, (getWidth() - textWidth) / 2, 
                      (getHeight() - textHeight) / 2);
        
        // Instructions
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        g2d.setColor(Color.LIGHT_GRAY);
        
        String[] instructions = {
            "Presiona P para continuar",
            "Presiona ESC para volver al menú"
        };
        
        int y = getHeight() / 2 + 50;
        for (String instruction : instructions) {
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(instruction);
            g2d.drawString(instruction, (getWidth() - textWidth) / 2, y);
            y += fm.getHeight() + 5;
        }
    }
    
    /**
     * Renders game over overlay
     */
    private void renderGameOverOverlay(Graphics2D g2d) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Game Over text
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        g2d.setColor(Color.RED);
        
        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        
        g2d.drawString(gameOverText, (getWidth() - textWidth) / 2, getHeight() / 2 - 100);
        
        // Reason and score
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        g2d.setColor(Color.WHITE);
        
        String[] info = {
            gameOverReason,
            "",
            "Puntuación Final: " + finalScore,
            gameEngine != null ? gameEngine.getScoreManager().getGameSummary() : "",
            "",
            "Presiona ESC para continuar"
        };
        
        int y = getHeight() / 2 - 50;
        for (String line : info) {
            if (!line.isEmpty()) {
                fm = g2d.getFontMetrics();
                textWidth = fm.stringWidth(line);
                g2d.drawString(line, (getWidth() - textWidth) / 2, y);
            }
            y += fm.getHeight() + 5;
        }
    }
    
    /**
     * Renders loading screen
     */
    private void renderLoadingScreen(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        g2d.setColor(Color.WHITE);
        
        String loadingText = "Cargando juego...";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(loadingText);
        
        g2d.drawString(loadingText, (getWidth() - textWidth) / 2, getHeight() / 2);
    }
    
    // KeyListener implementation
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameEngine == null) return;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (!gameEngine.isPaused() && !gameOverShown) {
                    gameEngine.keyPressed(e);
                }
                break;
                
            case KeyEvent.VK_P:
                if (!gameOverShown) {
                    gameEngine.togglePause();
                    showPauseOverlay = gameEngine.isPaused();
                }
                break;
                
            case KeyEvent.VK_ESCAPE:
                if (gameOverShown) {
                    // Reset game over state and return to menu
                    gameOverShown = false;
                    parentWindow.returnToMenu();
                } else if (gameEngine.isPaused()) {
                    // Unpause and return to menu
                    gameEngine.resumeGame();
                    showPauseOverlay = false;
                    parentWindow.returnToMenu();
                } else {
                    // Pause and show pause overlay
                    gameEngine.pauseGame();
                    showPauseOverlay = true;
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (gameEngine != null) {
            gameEngine.keyReleased(e);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        if (gameEngine != null) {
            gameEngine.keyTyped(e);
        }
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
        requestFocusInWindow();
    }
    
    /**
     * Resets the panel for a new game
     */
    public void resetForNewGame() {
        gameOverShown = false;
        gameOverReason = "";
        finalScore = 0;
        currentScore = 0;
        showPauseOverlay = false;
        scoreEffects.clear();
        comboEffects.clear();
    }
}
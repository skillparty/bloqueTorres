package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.ui.components.TowerVisualizationPanel;
import com.skillparty.towerblox.effects.ProfessionalEffects;
import com.skillparty.towerblox.game.physics.Crane;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.game.physics.Block;
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
    private ProfessionalEffects professionalEffects;
    
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
        this.professionalEffects = new ProfessionalEffects();
        
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
        Timer renderTimer = new Timer(33, e -> { // ~30 FPS - Smoother, less flickering
            // Game update and render cycle
            gameEngine.gameLoop(); // Update game logic
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
            
            // Add legacy effects for compatibility
            Color scoreColor = score > 500 ? Color.YELLOW : Color.WHITE;
            scoreEffects.add(new ScoreEffect(x, y, score, scoreColor));
            
            if (combo > 1) {
                comboEffects.add(new ComboEffect(x + 50, y - 20, combo));
            }
            
            // Add professional effects
            if (professionalEffects != null) {
                professionalEffects.addScoreEffect(x, y, score);
                
                if (score > 1500) {
                    professionalEffects.addPerfectPlacementEffect(x, y);
                } else if (score > 500) {
                    professionalEffects.addGoodPlacementEffect(x, y);
                }
                
                if (combo > 1) {
                    professionalEffects.addComboEffect(x + 50, y - 20, combo);
                }
            }
        }
    }
    
    /**
     * Shows milestone effect when reaching certain heights
     */
    public void showMilestoneEffect(int milestone) {
        if (gameEngine != null && gameEngine.getCrane() != null && professionalEffects != null) {
            int x = (int) gameEngine.getCrane().getX();
            int y = (int) gameEngine.getCrane().getY() + 50;
            
            professionalEffects.addMilestoneEffect(x, y, milestone);
        }
    }
    
    /**
     * Updates visual effects
     */
    private void updateEffects() {
        // Remove expired effects
        scoreEffects.removeIf(ScoreEffect::isExpired);
        comboEffects.removeIf(ComboEffect::isExpired);
        
        // Update professional effects
        if (professionalEffects != null) {
            professionalEffects.update(33); // 33ms delta time for 30 FPS - smoother effects
        }
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
            
            // Render professional effects (with camera transformation)
            if (professionalEffects != null) {
                var originalTransform = g2d.getTransform();
                if (gameEngine.getTower() != null) {
                    // Apply same camera transformation as game objects
                    double cameraY = calculateCameraOffset();
                    g2d.translate(0, cameraY);
                }
                professionalEffects.render(g2d);
                g2d.setTransform(originalTransform);
            }
            
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
     * Renders the professional game UI with enhanced information
     */
    private void renderGameUI(Graphics2D g2d) {
        // Professional HUD panel (top-left)
        renderMainHUD(g2d);
        
        // Crane status panel (top-right)
        renderCraneStatus(g2d);
        
        // ELIMINADO: Torre lateral derecha con luces parpadeantes
        // Ahora se usa sistema unificado sin parpadeos en TowerVisualizationPanel
        
        // Performance indicators (bottom-left)
        renderPerformanceInfo(g2d);
        
        // Controls help (bottom-center)
        renderControlsHelp(g2d);
        
        // Game mode indicator (top-center)
        renderGameModeInfo(g2d);
    }
    
    /**
     * Renders the main HUD with score, lives, and essential info
     */
    private void renderMainHUD(Graphics2D g2d) {
        // Professional HUD background
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(10, 10, 280, 140, 15, 15);
        
        // HUD border with gradient effect
        g2d.setColor(new Color(59, 130, 246));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(10, 10, 280, 140, 15, 15);
        
        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(new Color(248, 250, 252));
        g2d.drawString("TOWER BLOXX", 20, 30);
        
        // Score with formatting
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.YELLOW);
        String scoreText = String.format("Score: %,d", currentScore);
        g2d.drawString(scoreText, 20, 55);
        
        // Tower height with progress
        if (gameEngine != null && gameEngine.getTower() != null) {
            int height = gameEngine.getTower().getHeight();
            g2d.setColor(Color.CYAN);
            String heightText = String.format("Height: %d/163 floors", height);
            g2d.drawString(heightText, 20, 75);
            
            // Progress bar for height
            int barWidth = 200;
            int barHeight = 8;
            int barX = 20;
            int barY = 80;
            
            // Background
            g2d.setColor(new Color(60, 60, 60));
            g2d.fillRoundRect(barX, barY, barWidth, barHeight, 4, 4);
            
            // Progress
            double progress = Math.min(1.0, height / 163.0);
            int progressWidth = (int)(barWidth * progress);
            
            // Color based on progress
            Color progressColor;
            if (progress < 0.3) progressColor = Color.GREEN;
            else if (progress < 0.6) progressColor = Color.YELLOW;
            else if (progress < 0.9) progressColor = Color.ORANGE;
            else progressColor = Color.RED;
            
            g2d.setColor(progressColor);
            g2d.fillRoundRect(barX, barY, progressWidth, barHeight, 4, 4);
            
            // Border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(barX, barY, barWidth, barHeight, 4, 4);
        }
        
        // Lives with heart icons
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Lives:", 20, 105);
        
        for (int i = 0; i < 3; i++) {
            if (gameEngine != null) {
                // Use actual lives from game engine if available
                boolean hasLife = i < gameEngine.getLives();
                g2d.setColor(hasLife ? Color.RED : new Color(60, 60, 60));
            } else {
                g2d.setColor(Color.RED);
            }
            
            // Draw heart shape
            int heartX = 70 + i * 25;
            int heartY = 95;
            drawHeart(g2d, heartX, heartY, 8);
        }
        
        // Combo indicator
        if (gameEngine != null && gameEngine.getScoreManager() != null) {
            int combo = gameEngine.getScoreManager().getCurrentCombo();
            if (combo > 1) {
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.setColor(Color.ORANGE);
                g2d.drawString("Combo: x" + combo, 20, 125);
            }
        }
        
        // Difficulty indicator
        if (gameEngine != null) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.setColor(Color.LIGHT_GRAY);
            // String difficulty = gameEngine.getCurrentDifficulty().getDisplayName();
            // g2d.drawString("Difficulty: " + difficulty, 20, 140);
            g2d.drawString("Tower Bloxx - Enhanced", 20, 140);
        }
    }
    
    /**
     * Draws a heart shape for life indicators
     */
    private void drawHeart(Graphics2D g2d, int x, int y, int size) {
        // Simple heart approximation using circles and triangle
        g2d.fillOval(x - size/2, y - size/2, size, size/2);
        g2d.fillOval(x, y - size/2, size, size/2);
        
        int[] xPoints = {x, x + size, x + size/2};
        int[] yPoints = {y, y, y + size};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Renders crane status and movement information
     */
    private void renderCraneStatus(Graphics2D g2d) {
        if (gameEngine == null || gameEngine.getCrane() == null) return;
        
        int panelX = getWidth() - 300;
        int panelY = 10;
        int panelWidth = 280;
        int panelHeight = 120;
        
        // Background
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        // Border
        g2d.setColor(new Color(16, 185, 129));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString("CRANE STATUS", panelX + 10, panelY + 20);
        
        Crane crane = gameEngine.getCrane();
        
        // Movement pattern
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.CYAN);
        String pattern = getMovementPatternName(gameEngine.getTower().getHeight());
        g2d.drawString("Pattern: " + pattern, panelX + 10, panelY + 40);
        
        // Speed indicator
        double speedRatio = crane.getSpeed() / crane.getBaseSpeed();
        Color speedColor = speedRatio < 1.5 ? Color.GREEN : 
                          speedRatio < 2.5 ? Color.YELLOW : Color.RED;
        g2d.setColor(speedColor);
        g2d.drawString(String.format("Speed: %.1fx", speedRatio), panelX + 10, panelY + 55);
        
        // Range indicator
        double rangePercent = (crane.getSwingRange() / (800 * 0.3)) * 100;
        g2d.setColor(Color.ORANGE);
        g2d.drawString(String.format("Range: %.0f%%", rangePercent), panelX + 10, panelY + 70);
        
        // Position indicator
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("Position: %.0f", crane.getX()), panelX + 10, panelY + 85);
        
        // Direction indicator
        String direction = crane.isMovingRight() ? "→ RIGHT" : "← LEFT";
        g2d.setColor(crane.isMovingRight() ? Color.GREEN : Color.BLUE);
        g2d.drawString(direction, panelX + 10, panelY + 100);
        
        // Precision zone indicator
        renderPrecisionIndicator(g2d, panelX + 150, panelY + 40, crane);
    }
    
    /**
     * Renders precision timing indicator
     */
    private void renderPrecisionIndicator(Graphics2D g2d, int x, int y, Crane crane) {
        // Calculate precision
        double centerX = 400; // Game center
        double distance = Math.abs(crane.getX() - centerX);
        double maxDistance = crane.getSwingRange();
        boolean inPerfectZone = distance < (maxDistance * 0.2);
        
        // Precision circle
        g2d.setColor(inPerfectZone ? Color.GREEN : Color.RED);
        g2d.fillOval(x, y, 20, 20);
        
        // Label
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString(inPerfectZone ? "PERFECT" : "TIMING", x + 25, y + 12);
        
        // Precision bar
        int barWidth = 80;
        int barX = x;
        int barY = y + 25;
        
        // Background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, 6);
        
        // Perfect zone
        int perfectStart = barWidth * 2 / 5;
        int perfectWidth = barWidth / 5;
        g2d.setColor(new Color(0, 255, 0, 100));
        g2d.fillRect(barX + perfectStart, barY, perfectWidth, 6);
        
        // Current position
        int currentPos = (int)((distance / maxDistance) * (barWidth / 2));
        if (crane.getX() < centerX) {
            currentPos = barWidth / 2 - currentPos;
        } else {
            currentPos = barWidth / 2 + currentPos;
        }
        
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(barX + currentPos - 1, barY - 2, 2, 10);
    }

    /**
     * ELIMINADO: renderTowerProgress - Reemplazado por sistema unificado sin parpadeos
     * El TowerVisualizationPanel ahora maneja toda la visualización de torre de forma segura
     */

    /**
     * Renders performance information
     */
    private void renderPerformanceInfo(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(new Color(150, 150, 150));
        
        // FPS counter
        g2d.drawString(String.format("FPS: %.1f", renderFPS), 10, getHeight() - 40);
        
        // Game time
        if (gameEngine != null) {
            long gameTime = gameEngine.getGameTime();
            int minutes = (int)(gameTime / 60000);
            int seconds = (int)((gameTime % 60000) / 1000);
            g2d.drawString(String.format("Time: %02d:%02d", minutes, seconds), 10, getHeight() - 25);
        }
    }
    
    /**
     * Renders enhanced controls help
     */
    private void renderControlsHelp(Graphics2D g2d) {
        // Background for controls
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(getWidth()/2 - 200, getHeight() - 35, 400, 25, 10, 10);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        String controls = "SPACE: Drop Block  |  P: Pause  |  ESC: Menu  |  M: Movement Recorder";
        FontMetrics fm = g2d.getFontMetrics();
        int controlsWidth = fm.stringWidth(controls);
        g2d.drawString(controls, (getWidth() - controlsWidth) / 2, getHeight() - 15);
    }
    
    /**
     * Renders game mode information
     */
    private void renderGameModeInfo(Graphics2D g2d) {
        if (gameEngine == null) return;
        
        // Game mode banner
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(getWidth()/2 - 100, 10, 200, 30, 15, 15);
        
        g2d.setColor(new Color(59, 130, 246));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(getWidth()/2 - 100, 10, 200, 30, 15, 15);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        String modeText = "TOWER BLOXX 2005 MODE";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(modeText);
        g2d.drawString(modeText, (getWidth() - textWidth) / 2, 30);
    }
    
    /**
     * Gets movement pattern name for display
     */
    private String getMovementPatternName(int towerHeight) {
        if (towerHeight <= 10) return "STEADY";
        if (towerHeight <= 25) return "ACCELERATING";
        if (towerHeight <= 50) return "ERRATIC";
        if (towerHeight <= 75) return "PRECISION";
        if (towerHeight <= 100) return "CHAOTIC";
        return "EXTREME";
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
                
            case KeyEvent.VK_M:
                // Open movement recorder (if available)
                if (!gameOverShown && gameEngine != null) {
                    gameEngine.pauseGame();
                    // TODO: Implement movement recorder access
                    System.out.println("Movement recorder access - to be implemented");
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
     * Calculates camera offset for effects rendering
     */
    private double calculateCameraOffset() {
        if (gameEngine == null || gameEngine.getTower() == null) return 0;
        
        int towerHeight = gameEngine.getTower().getHeight();
        if (towerHeight <= 7) return 0; // No camera movement for low towers
        
        // Simple camera calculation - move up as tower grows
        return -(towerHeight - 7) * 35; // 35 pixels per floor above floor 7
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
        
        if (professionalEffects != null) {
            professionalEffects.clear();
        }
    }
}
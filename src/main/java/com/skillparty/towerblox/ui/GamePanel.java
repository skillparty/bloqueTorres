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
            // Game update and render cycle - only if gameEngine is set
            if (gameEngine != null) {
                gameEngine.gameLoop(); // Update game logic
                repaint(); // Render
                updateEffects();
                updatePerformanceStats();
            }
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
        
        // ELIMINADO: Crane status panel integrado en tower progress card
        
        // Performance indicators (bottom-left)
        renderPerformanceInfo(g2d);
        
        // Controls help (bottom-center)
        renderControlsHelp(g2d);
        
        // Game mode indicator (top-center)
        renderGameModeInfo(g2d);
    }
    
    /**
     * Renders the main HUD with professional card-based design
     */
    private void renderMainHUD(Graphics2D g2d) {
        // Main stats card (top-left)
        renderStatsCard(g2d);
        
        // Tower progress card (top-right)
        renderTowerProgressCard(g2d);
        
        // Lives and combo card (bottom-left)
        renderGameStatusCard(g2d);
    }
    
    /**
     * Professional stats card with modern design
     */
    private void renderStatsCard(Graphics2D g2d) {
        int cardX = 15, cardY = 15;
        int cardWidth = 280, cardHeight = 120;
        
        // Card background with subtle shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(cardX + 3, cardY + 3, cardWidth, cardHeight, 20, 20);
        
        // Main card background with gradient effect
        GradientPaint cardGradient = new GradientPaint(
            cardX, cardY, new Color(20, 30, 60, 240),
            cardX, cardY + cardHeight, new Color(10, 15, 30, 240)
        );
        g2d.setPaint(cardGradient);
        g2d.fillRoundRect(cardX, cardY, cardWidth, cardHeight, 20, 20);
        
        // Premium border with glowing effect
        g2d.setPaint(new GradientPaint(
            cardX, cardY, new Color(59, 130, 246, 255),
            cardX + cardWidth, cardY, new Color(147, 51, 234, 255)
        ));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, 20, 20);
        
        // Title with modern typography
        g2d.setColor(new Color(248, 250, 252));
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        g2d.drawString("🏗️ TOWER PROGRESS", cardX + 20, cardY + 30);
        
        // Score section with enhanced formatting
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        g2d.setColor(new Color(255, 215, 0)); // Gold
        String scoreText = String.format("💰 Score: %,d pts", currentScore);
        g2d.drawString(scoreText, cardX + 20, cardY + 55);
        
        // Tower height with enhanced visual indicator
        if (gameEngine != null && gameEngine.getTower() != null) {
            int height = gameEngine.getTower().getHeight();
            g2d.setColor(new Color(34, 197, 94)); // Emerald
            String heightText = String.format("🏢 Height: %d floors", height);
            g2d.drawString(heightText, cardX + 20, cardY + 80);
            
            // Professional progress bar
            renderEnhancedProgressBar(g2d, cardX + 20, cardY + 90, 240, height, 163);
        }
    }
    
    /**
     * Enhanced progress bar with professional styling
     */
    private void renderEnhancedProgressBar(Graphics2D g2d, int x, int y, int width, int current, int max) {
        int height = 12;
        
        // Background track
        g2d.setColor(new Color(30, 41, 59, 200));
        g2d.fillRoundRect(x, y, width, height, height/2, height/2);
        
        // Progress calculation
        double progress = Math.min(1.0, (double)current / max);
        int progressWidth = (int)(width * progress);
        
        // Dynamic color based on progress
        Color progressColor;
        if (progress < 0.25) progressColor = new Color(34, 197, 94);    // Green
        else if (progress < 0.5) progressColor = new Color(59, 130, 246); // Blue  
        else if (progress < 0.75) progressColor = new Color(251, 191, 36); // Yellow
        else progressColor = new Color(239, 68, 68);                      // Red
        
        // Progress fill with gradient
        if (progressWidth > 0) {
            GradientPaint progressGradient = new GradientPaint(
                x, y, progressColor,
                x, y + height, progressColor.darker()
            );
            g2d.setPaint(progressGradient);
            g2d.fillRoundRect(x, y, progressWidth, height, height/2, height/2);
            
            // Shine effect
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.fillRoundRect(x + 1, y + 1, progressWidth - 2, height/3, height/4, height/4);
        }
        
        // Border
        g2d.setColor(new Color(100, 116, 139, 150));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y, width, height, height/2, height/2);
        
        // Progress text
        g2d.setFont(new Font("SF Pro Display", Font.PLAIN, 10));
        g2d.setColor(Color.WHITE);
        String progressText = String.format("%.0f%%", progress * 100);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (width - fm.stringWidth(progressText)) / 2;
        g2d.drawString(progressText, textX, y + height - 2);
    }
    
    /**
     * Professional tower stability and progress card
     */
    private void renderTowerProgressCard(Graphics2D g2d) {
        int cardX = getWidth() - 320, cardY = 15;
        int cardWidth = 300, cardHeight = 140;
        
        // Card shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(cardX + 3, cardY + 3, cardWidth, cardHeight, 20, 20);
        
        // Card background
        GradientPaint cardGradient = new GradientPaint(
            cardX, cardY, new Color(30, 20, 60, 240),
            cardX, cardY + cardHeight, new Color(15, 10, 30, 240)
        );
        g2d.setPaint(cardGradient);
        g2d.fillRoundRect(cardX, cardY, cardWidth, cardHeight, 20, 20);
        
        // Premium border
        g2d.setPaint(new GradientPaint(
            cardX, cardY, new Color(168, 85, 247),
            cardX + cardWidth, cardY, new Color(59, 130, 246)
        ));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, 20, 20);
        
        if (gameEngine == null || gameEngine.getTower() == null) return;
        
        // Title
        g2d.setColor(new Color(248, 250, 252));
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        g2d.drawString("🏗️ TOWER STATUS", cardX + 20, cardY + 30);
        
        Tower tower = gameEngine.getTower();
        
        // Overall stability indicator
        double avgStability = calculateAverageStability(tower);
        renderStabilityIndicator(g2d, cardX + 20, cardY + 45, avgStability);
        
        // Last block stability (most recent)
        if (tower.getHeight() > 0) {
            Block lastBlock = tower.getBlocks().get(tower.getHeight() - 1);
            renderLastBlockStatus(g2d, cardX + 20, cardY + 85, lastBlock);
        }
        
        // Mini tower visualization
        renderMiniTowerVisualization(g2d, cardX + 220, cardY + 40, 60, 80);
    }
    
    /**
     * Calculate average stability of the tower
     */
    private double calculateAverageStability(Tower tower) {
        if (tower.getHeight() == 0) return 1.0;
        
        double totalStability = 0;
        for (Block block : tower.getBlocks()) {
            totalStability += block.getStability();
        }
        return totalStability / tower.getHeight();
    }
    
    /**
     * Professional stability indicator
     */
    private void renderStabilityIndicator(Graphics2D g2d, int x, int y, double stability) {
        // Label
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        g2d.setColor(new Color(156, 163, 175));
        g2d.drawString("Overall Stability:", x, y);
        
        // Stability bar
        int barWidth = 180, barHeight = 8;
        int barX = x, barY = y + 5;
        
        // Background
        g2d.setColor(new Color(55, 65, 81));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, barHeight/2, barHeight/2);
        
        // Stability fill
        int fillWidth = (int)(barWidth * stability);
        Color stabilityColor = getStabilityColor(stability);
        
        GradientPaint stabilityGradient = new GradientPaint(
            barX, barY, stabilityColor,
            barX, barY + barHeight, stabilityColor.darker()
        );
        g2d.setPaint(stabilityGradient);
        g2d.fillRoundRect(barX, barY, fillWidth, barHeight, barHeight/2, barHeight/2);
        
        // Stability text
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        String stabilityText = String.format("%.1f%% %s", stability * 100, getStabilityLabel(stability));
        g2d.drawString(stabilityText, x + 185, y);
    }
    
    /**
     * Get color based on stability value
     */
    private Color getStabilityColor(double stability) {
        if (stability >= 0.8) return new Color(34, 197, 94);    // Green - Excellent
        if (stability >= 0.6) return new Color(59, 130, 246);   // Blue - Good
        if (stability >= 0.4) return new Color(251, 191, 36);   // Yellow - Fair
        if (stability >= 0.2) return new Color(249, 115, 22);   // Orange - Poor
        return new Color(239, 68, 68);                          // Red - Critical
    }
    
    /**
     * Get stability label
     */
    private String getStabilityLabel(double stability) {
        if (stability >= 0.8) return "EXCELLENT";
        if (stability >= 0.6) return "GOOD";
        if (stability >= 0.4) return "FAIR";
        if (stability >= 0.2) return "POOR";
        return "CRITICAL";
    }
    
    /**
     * Show status of the last placed block
     */
    private void renderLastBlockStatus(Graphics2D g2d, int x, int y, Block block) {
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 12));
        g2d.setColor(new Color(156, 163, 175));
        g2d.drawString("Last Block:", x, y);
        
        // Block color indicator
        g2d.setColor(block.getColor());
        g2d.fillRoundRect(x + 80, y - 10, 12, 12, 3, 3);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(x + 80, y - 10, 12, 12, 3, 3);
        
        // Block stability
        double blockStability = block.getStability();
        Color statusColor = getStabilityColor(blockStability);
        g2d.setColor(statusColor);
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 12));
        String statusText = String.format("%.0f%% %s", blockStability * 100, getStabilityLabel(blockStability));
        g2d.drawString(statusText, x + 100, y);
    }
    
    /**
     * Mini tower visualization for quick reference
     */
    private void renderMiniTowerVisualization(Graphics2D g2d, int x, int y, int width, int height) {
        if (gameEngine == null || gameEngine.getTower() == null) return;
        
        Tower tower = gameEngine.getTower();
        int towerHeight = tower.getHeight();
        if (towerHeight == 0) return;
        
        // Background
        g2d.setColor(new Color(17, 24, 39, 150));
        g2d.fillRoundRect(x - 5, y - 5, width + 10, height + 10, 8, 8);
        
        // Show last 10 blocks
        int maxBlocks = Math.min(10, towerHeight);
        int blockHeight = height / maxBlocks;
        
        for (int i = 0; i < maxBlocks; i++) {
            int blockIndex = towerHeight - maxBlocks + i;
            Block block = tower.getBlocks().get(blockIndex);
            
            int blockY = y + height - (i + 1) * blockHeight;
            int blockWidth = (int)(width * block.getStability() * 0.8) + (width / 5);
            int blockX = x + (width - blockWidth) / 2;
            
            // Block with stability-based width
            g2d.setColor(block.getColor());
            g2d.fillRect(blockX, blockY, blockWidth, blockHeight - 1);
            
            // Block outline
            g2d.setColor(block.getColor().brighter());
            g2d.drawRect(blockX, blockY, blockWidth, blockHeight - 1);
        }
        
        // Base
        g2d.setColor(new Color(107, 114, 128));
        g2d.fillRect(x, y + height, width, 3);
    }
    
    /**
     * Professional game status card (lives, combo, etc)
     */
    private void renderGameStatusCard(Graphics2D g2d) {
        int cardX = 15, cardY = getHeight() - 120;
        int cardWidth = 280, cardHeight = 100;
        
        // Card shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(cardX + 3, cardY + 3, cardWidth, cardHeight, 20, 20);
        
        // Card background
        GradientPaint cardGradient = new GradientPaint(
            cardX, cardY, new Color(60, 20, 30, 240),
            cardX, cardY + cardHeight, new Color(30, 10, 15, 240)
        );
        g2d.setPaint(cardGradient);
        g2d.fillRoundRect(cardX, cardY, cardWidth, cardHeight, 20, 20);
        
        // Border
        g2d.setPaint(new GradientPaint(
            cardX, cardY, new Color(239, 68, 68),
            cardX + cardWidth, cardY, new Color(251, 146, 60)
        ));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, 20, 20);
        
        // Title
        g2d.setColor(new Color(248, 250, 252));
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        g2d.drawString("❤️ GAME STATUS", cardX + 20, cardY + 25);
        
        // Lives with enhanced hearts
        g2d.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        g2d.setColor(new Color(156, 163, 175));
        g2d.drawString("Lives:", cardX + 20, cardY + 50);
        
        for (int i = 0; i < 3; i++) {
            boolean hasLife = gameEngine != null && i < gameEngine.getLives();
            renderEnhancedHeart(g2d, cardX + 80 + i * 30, cardY + 40, hasLife);
        }
        
        // Combo section
        if (gameEngine != null && gameEngine.getScoreManager() != null) {
            int combo = gameEngine.getScoreManager().getCurrentCombo();
            renderComboIndicator(g2d, cardX + 20, cardY + 65, combo);
        }
        
        // Performance indicator
        g2d.setFont(new Font("SF Pro Display", Font.PLAIN, 11));
        g2d.setColor(new Color(107, 114, 128));
        String perfText = String.format("FPS: %.1f | Enhanced UI", renderFPS);
        g2d.drawString(perfText, cardX + 20, cardY + 85);
    }
    
    /**
     * Enhanced heart with professional styling
     */
    private void renderEnhancedHeart(Graphics2D g2d, int centerX, int centerY, boolean hasLife) {
        int size = 12;
        
        if (hasLife) {
            // Active heart with gradient
            GradientPaint heartGradient = new GradientPaint(
                centerX - size/2, centerY - size/2, new Color(239, 68, 68),
                centerX - size/2, centerY + size/2, new Color(185, 28, 28)
            );
            g2d.setPaint(heartGradient);
            
            // Heart shape using path
            drawEnhancedHeart(g2d, centerX, centerY, size);
            
            // Highlight
            g2d.setColor(new Color(255, 255, 255, 100));
            drawEnhancedHeart(g2d, centerX - 1, centerY - 1, size - 2);
            
        } else {
            // Inactive heart
            g2d.setColor(new Color(75, 85, 99));
            drawEnhancedHeart(g2d, centerX, centerY, size);
            g2d.setColor(new Color(55, 65, 81));
            g2d.setStroke(new BasicStroke(1));
            drawEnhancedHeartOutline(g2d, centerX, centerY, size);
        }
    }
    
    /**
     * Draw enhanced heart shape
     */
    private void drawEnhancedHeart(Graphics2D g2d, int centerX, int centerY, int size) {
        // Left circle
        g2d.fillOval(centerX - size * 3/4, centerY - size/2, size/2, size/2);
        // Right circle  
        g2d.fillOval(centerX - size/4, centerY - size/2, size/2, size/2);
        // Bottom triangle
        int[] xPoints = {centerX - size/2, centerX + size/2, centerX};
        int[] yPoints = {centerY - size/4, centerY - size/4, centerY + size/2};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Draw heart outline
     */
    private void drawEnhancedHeartOutline(Graphics2D g2d, int centerX, int centerY, int size) {
        g2d.drawOval(centerX - size * 3/4, centerY - size/2, size/2, size/2);
        g2d.drawOval(centerX - size/4, centerY - size/2, size/2, size/2);
        int[] xPoints = {centerX - size/2, centerX + size/2, centerX};
        int[] yPoints = {centerY - size/4, centerY - size/4, centerY + size/2};
        g2d.drawPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Professional combo indicator
     */
    private void renderComboIndicator(Graphics2D g2d, int x, int y, int combo) {
        if (combo <= 1) {
            // No combo - show potential
            g2d.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
            g2d.setColor(new Color(107, 114, 128));
            g2d.drawString("Perfect placement for combo!", x, y);
        } else {
            // Active combo with flash effect (static, epilepsy-safe)
            g2d.setFont(new Font("SF Pro Display", Font.BOLD, 14));
            
            // Combo color based on value
            Color comboColor;
            if (combo >= 5) comboColor = new Color(168, 85, 247);      // Purple - Amazing
            else if (combo >= 3) comboColor = new Color(59, 130, 246);  // Blue - Great
            else comboColor = new Color(34, 197, 94);                   // Green - Good
            
            g2d.setColor(comboColor);
            String comboText = String.format("🔥 COMBO x%d", combo);
            g2d.drawString(comboText, x, y);
            
            // Combo multiplier indicator
            g2d.setFont(new Font("SF Pro Display", Font.PLAIN, 11));
            g2d.setColor(new Color(156, 163, 175));
            String multiplierText = String.format("+%.0f%% Score Bonus", (combo - 1) * 50.0);
            g2d.drawString(multiplierText, x + 120, y);
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
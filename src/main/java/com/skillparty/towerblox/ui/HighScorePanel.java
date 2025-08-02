package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.score.HighScore;
import com.skillparty.towerblox.ui.components.FontManager;
import com.skillparty.towerblox.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * Panel for displaying high scores leaderboard
 */
public class HighScorePanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    
    private GameWindow parentWindow;
    private FontManager fontManager;
    private GameEngine gameEngine;
    
    // UI Components
    private JButton backButton;
    private JButton clearButton;
    private List<HighScore> highScores;
    
    public HighScorePanel(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        this.fontManager = FontManager.getInstance();
        
        initializePanel();
        createComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Initializes the panel properties
     */
    private void initializePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(Constants.GAME_WIDTH, Constants.GAME_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
    }
    
    /**
     * Creates all UI components
     */
    private void createComponents() {
        backButton = createButton("Volver al Menú", new Color(0, 100, 200));
        clearButton = createButton("Limpiar Puntuaciones", new Color(200, 50, 50));
    }
    
    /**
     * Creates a styled button
     */
    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(fontManager.getMenuFont());
        button.setPreferredSize(new Dimension(200, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBorderBorder());
        button.setFocusPainted(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    /**
     * Sets up the panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Scores panel (will be drawn in paintComponent)
        // We don't add components here since we're custom drawing
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(800, 80));
        
        JLabel titleLabel = new JLabel("MEJORES PUNTUACIONES");
        titleLabel.setFont(fontManager.getFont(FontManager.LOGO_SIZE, Font.BOLD));
        titleLabel.setForeground(new Color(0, 255, 0)); // Terminal green
        
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(800, 60));
        
        panel.add(backButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(clearButton);
        
        return panel;
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        backButton.addActionListener(e -> parentWindow.returnToMenu());
        
        clearButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que quieres borrar todas las puntuaciones?\n\nEsta acción no se puede deshacer.",
                "Confirmar Borrado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                clearHighScores();
            }
        });
    }
    
    /**
     * Sets the game engine reference
     */
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    /**
     * Refreshes the high scores from storage
     */
    public void refreshScores() {
        if (gameEngine != null && gameEngine.getScoreStorage() != null) {
            highScores = gameEngine.getScoreStorage().getHighScores();
        }
        repaint();
    }
    
    /**
     * Clears all high scores
     */
    private void clearHighScores() {
        if (gameEngine != null && gameEngine.getScoreStorage() != null) {
            gameEngine.getScoreStorage().clearScores();
            refreshScores();
            
            JOptionPane.showMessageDialog(
                this,
                "Todas las puntuaciones han sido borradas.",
                "Puntuaciones Borradas",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw background pattern
        drawBackgroundPattern(g2d);
        
        // Draw high scores table
        drawHighScoresTable(g2d);
        
        // Draw statistics
        drawStatistics(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Draws a subtle background pattern
     */
    private void drawBackgroundPattern(Graphics2D g2d) {
        g2d.setColor(new Color(0, 32, 0, 50)); // Very dark green, semi-transparent
        
        // Draw diagonal lines
        for (int i = -getHeight(); i < getWidth() + getHeight(); i += 30) {
            g2d.drawLine(i, 0, i + getHeight(), getHeight());
        }
    }
    
    /**
     * Draws the high scores table
     */
    private void drawHighScoresTable(Graphics2D g2d) {
        if (highScores == null || highScores.isEmpty()) {
            drawEmptyScoresMessage(g2d);
            return;
        }
        
        // Table dimensions
        int tableX = 50;
        int tableY = 120;
        int tableWidth = getWidth() - 100;
        int rowHeight = 35;
        
        // Draw table header
        drawTableHeader(g2d, tableX, tableY, tableWidth, rowHeight);
        
        // Draw score entries
        for (int i = 0; i < Math.min(highScores.size(), 10); i++) {
            HighScore score = highScores.get(i);
            int rowY = tableY + (i + 1) * rowHeight;
            
            drawScoreRow(g2d, score, i + 1, tableX, rowY, tableWidth, rowHeight);
        }
        
        // Draw table border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(tableX, tableY, tableWidth, (Math.min(highScores.size(), 10) + 1) * rowHeight);
    }
    
    /**
     * Draws the table header
     */
    private void drawTableHeader(Graphics2D g2d, int x, int y, int width, int height) {
        // Header background
        g2d.setColor(new Color(0, 100, 0));
        g2d.fillRect(x, y, width, height);
        
        // Header text
        g2d.setFont(fontManager.getFont(FontManager.GAME_SIZE, Font.BOLD));
        g2d.setColor(Color.WHITE);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textY = y + (height + fm.getAscent()) / 2;
        
        // Column headers
        g2d.drawString("Pos", x + 10, textY);
        g2d.drawString("Nick", x + 60, textY);
        g2d.drawString("Puntuación", x + 150, textY);
        g2d.drawString("Dificultad", x + 280, textY);
        g2d.drawString("Fecha", x + 400, textY);
        
        // Header border
        g2d.setColor(Color.WHITE);
        g2d.drawLine(x, y + height, x + width, y + height);
    }
    
    /**
     * Draws a single score row
     */
    private void drawScoreRow(Graphics2D g2d, HighScore score, int position, 
                             int x, int y, int width, int height) {
        // Alternate row colors
        if (position % 2 == 0) {
            g2d.setColor(new Color(20, 20, 20));
            g2d.fillRect(x, y, width, height);
        }
        
        // Highlight top 3
        Color textColor = Color.WHITE;
        if (position == 1) {
            textColor = Color.YELLOW; // Gold
        } else if (position == 2) {
            textColor = Color.LIGHT_GRAY; // Silver
        } else if (position == 3) {
            textColor = new Color(205, 127, 50); // Bronze
        }
        
        g2d.setFont(fontManager.getGameFont());
        g2d.setColor(textColor);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textY = y + (height + fm.getAscent()) / 2;
        
        // Draw score data
        g2d.drawString(String.valueOf(position), x + 15, textY);
        g2d.drawString(score.getDisplayNickname(), x + 60, textY);
        g2d.drawString(String.format("%,d", score.getScore()), x + 150, textY);
        g2d.drawString(score.getDifficultyName(), x + 280, textY);
        g2d.drawString(score.getFormattedDate(), x + 400, textY);
        
        // Draw position medal for top 3
        if (position <= 3) {
            drawPositionMedal(g2d, position, x + 25, y + height/2);
        }
    }
    
    /**
     * Draws a medal icon for top 3 positions
     */
    private void drawPositionMedal(Graphics2D g2d, int position, int x, int y) {
        Color medalColor;
        switch (position) {
            case 1: medalColor = Color.YELLOW; break;
            case 2: medalColor = Color.LIGHT_GRAY; break;
            case 3: medalColor = new Color(205, 127, 50); break;
            default: return;
        }
        
        // Draw medal circle
        g2d.setColor(medalColor);
        g2d.fillOval(x - 8, y - 8, 16, 16);
        
        // Draw medal border
        g2d.setColor(medalColor.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - 8, y - 8, 16, 16);
        
        // Draw position number
        g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE, Font.BOLD));
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        String posText = String.valueOf(position);
        int textWidth = fm.stringWidth(posText);
        g2d.drawString(posText, x - textWidth/2, y + fm.getAscent()/2);
    }
    
    /**
     * Draws message when no scores are available
     */
    private void drawEmptyScoresMessage(Graphics2D g2d) {
        g2d.setFont(fontManager.getMenuFont());
        g2d.setColor(Color.GRAY);
        
        String message = "No hay puntuaciones registradas aún";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        
        g2d.drawString(message, (getWidth() - textWidth) / 2, getHeight() / 2);
        
        // Draw encouragement message
        g2d.setFont(fontManager.getGameFont());
        String encouragement = "¡Juega para ser el primero en la tabla!";
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(encouragement);
        
        g2d.drawString(encouragement, (getWidth() - textWidth) / 2, getHeight() / 2 + 30);
    }
    
    /**
     * Draws statistics about the high scores
     */
    private void drawStatistics(Graphics2D g2d) {
        if (gameEngine == null || gameEngine.getScoreStorage() == null || 
            highScores == null || highScores.isEmpty()) {
            return;
        }
        
        String stats = gameEngine.getScoreStorage().getStatistics();
        
        g2d.setFont(fontManager.getSmallFont());
        g2d.setColor(Color.LIGHT_GRAY);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(stats);
        
        g2d.drawString(stats, (getWidth() - textWidth) / 2, getHeight() - 80);
    }
    
    // KeyListener implementation
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_ENTER:
                parentWindow.returnToMenu();
                break;
                
            case KeyEvent.VK_R:
                refreshScores();
                break;
                
            case KeyEvent.VK_C:
                // Ctrl+C to clear (with confirmation)
                if (e.isControlDown()) {
                    clearButton.doClick();
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
        requestFocusInWindow();
    }
}
package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.score.HighScore;
import com.skillparty.towerblox.ui.components.FontManager;
import com.skillparty.towerblox.utils.Constants;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Panel for entering high score nickname (3 characters max)
 */
public class ScorePanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    
    private GameWindow parentWindow;
    private FontManager fontManager;
    private GameEngine gameEngine;
    
    // UI Components
    private JTextField nicknameField;
    private JButton submitButton;
    private JButton skipButton;
    private JLabel scoreLabel;
    private JLabel instructionLabel;
    private JLabel errorLabel;
    
    // Game data
    private int finalScore = 0;
    private String gameStats = "";
    
    // Input validation
    private boolean isValidInput = false;
    
    public ScorePanel(GameWindow parentWindow) {
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
        // Score display
        scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setFont(fontManager.getFont(FontManager.LOGO_SIZE, Font.BOLD));
        scoreLabel.setForeground(Color.YELLOW);
        
        // Instructions
        instructionLabel = new JLabel("", SwingConstants.CENTER);
        instructionLabel.setFont(fontManager.getMenuFont());
        instructionLabel.setForeground(Color.WHITE);
        
        // Nickname input field
        nicknameField = createNicknameField();
        
        // Buttons
        submitButton = createButton("Guardar Puntuación", Color.GREEN);
        skipButton = createButton("Saltar", Color.GRAY);
        
        // Error label
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(fontManager.getGameFont());
        errorLabel.setForeground(Color.RED);
        
        // Initially disable submit button
        submitButton.setEnabled(false);
    }
    
    /**
     * Creates the nickname input field with validation
     */
    private JTextField createNicknameField() {
        JTextField field = new JTextField(3);
        field.setFont(fontManager.getFont(FontManager.MENU_SIZE + 4, Font.BOLD));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setPreferredSize(new Dimension(100, 50));
        field.setBackground(Color.DARK_GRAY);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Add document filter for 3-character limit and validation
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                
                // Filter to alphanumeric only and convert to uppercase
                String filtered = string.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
                
                if (fb.getDocument().getLength() + filtered.length() <= 3) {
                    super.insertString(fb, offset, filtered, attr);
                    validateInput();
                }
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) return;
                
                // Filter to alphanumeric only and convert to uppercase
                String filtered = text.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
                
                if (fb.getDocument().getLength() - length + filtered.length() <= 3) {
                    super.replace(fb, offset, length, filtered, attrs);
                    validateInput();
                }
            }
            
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                validateInput();
            }
        });
        
        return field;
    }
    
    /**
     * Creates a styled button
     */
    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(fontManager.getMenuFont());
        button.setPreferredSize(new Dimension(180, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
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
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Title
        JLabel titleLabel = new JLabel("¡NUEVA PUNTUACIÓN ALTA!");
        titleLabel.setFont(fontManager.getFont(FontManager.LOGO_SIZE + 4, Font.BOLD));
        titleLabel.setForeground(new Color(0, 255, 0)); // Terminal green
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        
        // Score display
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(scoreLabel, gbc);
        
        // Game stats
        JLabel statsLabel = new JLabel("", SwingConstants.CENTER);
        statsLabel.setFont(fontManager.getGameFont());
        statsLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 2;
        add(statsLabel, gbc);
        
        // Instructions
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 20, 10, 20);
        add(instructionLabel, gbc);
        
        // Nickname input
        JPanel inputPanel = createInputPanel();
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(inputPanel, gbc);
        
        // Error message
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 20, 20, 20);
        add(errorLabel, gbc);
        
        // Buttons
        JPanel buttonPanel = createButtonPanel();
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(buttonPanel, gbc);
        
        // Help text
        JLabel helpLabel = new JLabel("Solo letras y números (máximo 3 caracteres)");
        helpLabel.setFont(fontManager.getSmallFont());
        helpLabel.setForeground(Color.GRAY);
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 20, 20, 20);
        add(helpLabel, gbc);
    }
    
    /**
     * Creates the input panel with nickname field
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.BLACK);
        
        JLabel label = new JLabel("Ingresa tu nickname:");
        label.setFont(fontManager.getMenuFont());
        label.setForeground(Color.WHITE);
        
        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(nicknameField);
        
        return panel;
    }
    
    /**
     * Creates the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.BLACK);
        
        panel.add(submitButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(skipButton);
        
        return panel;
    }
    
    /**
     * Sets up event handlers
     */
    private void setupEventHandlers() {
        submitButton.addActionListener(e -> submitScore());
        skipButton.addActionListener(e -> skipScore());
        
        // Enter key submits if valid
        nicknameField.addActionListener(e -> {
            if (isValidInput) {
                submitScore();
            }
        });
        
        // Focus on nickname field when panel is shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    nicknameField.requestFocusInWindow();
                    nicknameField.selectAll();
                });
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
     * Updates the panel with current game data
     */
    public void updateGameData() {
        if (gameEngine != null) {
            finalScore = gameEngine.getScoreManager().calculateFinalScore();
            gameStats = gameEngine.getScoreManager().getGameSummary();
            
            scoreLabel.setText("Puntuación: " + finalScore);
            instructionLabel.setText("Tu puntuación califica para el top 10!");
            
            // Update stats display
            Component statsComponent = getComponent(2); // Stats label is at index 2
            if (statsComponent instanceof JLabel) {
                ((JLabel) statsComponent).setText("<html><center>" + 
                    gameStats.replace(" | ", "<br>") + "</center></html>");
            }
        }
    }
    
    /**
     * Validates the nickname input
     */
    private void validateInput() {
        String nickname = nicknameField.getText().trim();
        
        if (nickname.isEmpty()) {
            isValidInput = false;
            errorLabel.setText("Ingresa al menos 1 carácter");
            submitButton.setEnabled(false);
        } else if (!HighScore.isValidNickname(nickname)) {
            isValidInput = false;
            errorLabel.setText("Solo letras y números permitidos");
            submitButton.setEnabled(false);
        } else {
            isValidInput = true;
            errorLabel.setText("");
            submitButton.setEnabled(true);
        }
        
        // Update field border color
        if (isValidInput) {
            nicknameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GREEN, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else if (!nickname.isEmpty()) {
            nicknameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else {
            nicknameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
    }
    
    /**
     * Submits the high score
     */
    private void submitScore() {
        if (!isValidInput || gameEngine == null) {
            return;
        }
        
        String nickname = nicknameField.getText().trim();
        boolean success = gameEngine.addHighScore(nickname);
        
        if (success) {
            // Show success message
            JOptionPane.showMessageDialog(
                this,
                "¡Puntuación guardada exitosamente!\n\nNickname: " + nickname + 
                "\nPuntuación: " + finalScore,
                "Puntuación Guardada",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Go to high scores to show the new entry
            parentWindow.showHighScores();
        } else {
            // Show error message
            JOptionPane.showMessageDialog(
                this,
                "Error al guardar la puntuación.\nIntenta nuevamente.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Skips saving the score
     */
    private void skipScore() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que no quieres guardar tu puntuación?\n\nPuntuación: " + finalScore,
            "Confirmar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            parentWindow.returnToMenu();
        }
    }
    
    /**
     * Resets the panel for a new score entry
     */
    public void resetForNewScore() {
        nicknameField.setText("");
        errorLabel.setText("");
        isValidInput = false;
        submitButton.setEnabled(false);
        
        // Reset field border
        nicknameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        updateGameData();
    }
    
    // KeyListener implementation
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                skipScore();
                break;
                
            case KeyEvent.VK_ENTER:
                if (isValidInput) {
                    submitScore();
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Add background effects
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw celebration particles effect
        drawCelebrationEffect(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Draws a simple celebration effect
     */
    private void drawCelebrationEffect(Graphics2D g2d) {
        long time = System.currentTimeMillis();
        
        // Draw floating particles
        for (int i = 0; i < 20; i++) {
            double angle = (time / 1000.0 + i * 0.3) % (2 * Math.PI);
            int x = (int) (getWidth() / 2 + Math.cos(angle) * (100 + i * 10));
            int y = (int) (getHeight() / 2 + Math.sin(angle * 1.5) * (50 + i * 5));
            
            // Vary colors
            Color[] colors = {Color.YELLOW, Color.ORANGE, Color.CYAN, Color.MAGENTA};
            g2d.setColor(colors[i % colors.length]);
            
            int size = 3 + (i % 3);
            g2d.fillOval(x - size/2, y - size/2, size, size);
        }
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
        SwingUtilities.invokeLater(() -> {
            nicknameField.requestFocusInWindow();
            nicknameField.selectAll();
        });
    }
}
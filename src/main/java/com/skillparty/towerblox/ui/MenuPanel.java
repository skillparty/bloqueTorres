package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.ui.components.ASCIILogo;
import com.skillparty.towerblox.ui.components.FontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Main menu panel with difficulty selection and ASCII logo
 */
public class MenuPanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    
    private GameWindow parentWindow;
    private FontManager fontManager;
    private ASCIILogo asciiLogo;
    
    // UI Components
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;
    private JButton highScoresButton;
    private JButton exitButton;
    
    // Selected difficulty (for keyboard navigation)
    private int selectedDifficulty = 1; // 0=Easy, 1=Normal, 2=Hard
    private boolean keyboardMode = false;
    
    public MenuPanel(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        this.fontManager = FontManager.getInstance();
        this.asciiLogo = new ASCIILogo();
        
        initializePanel();
        createComponents();
        setupLayout();
        setupEventHandlers();
        
        // Make panel focusable for keyboard input
        setFocusable(true);
        addKeyListener(this);
    }
    
    /**
     * Initializes the panel properties
     */
    private void initializePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
    }
    
    /**
     * Creates all UI components
     */
    private void createComponents() {
        // Create difficulty buttons
        easyButton = createDifficultyButton("Fácil", DifficultyLevel.EASY, 
            "Velocidad reducida - Perfecto para principiantes");
        normalButton = createDifficultyButton("Normal", DifficultyLevel.NORMAL, 
            "Velocidad estándar - Experiencia equilibrada");
        hardButton = createDifficultyButton("Difícil", DifficultyLevel.HARD, 
            "Velocidad alta - Solo para expertos");
        
        // Create other buttons
        highScoresButton = createMenuButton("Ver Puntuaciones", 
            "Consulta las mejores puntuaciones");
        exitButton = createMenuButton("Salir", 
            "Cerrar el juego");
        
        // Set initial selection
        updateButtonSelection();
    }
    
    /**
     * Creates a difficulty selection button
     */
    private JButton createDifficultyButton(String text, DifficultyLevel difficulty, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(fontManager.getMenuFont());
        button.setPreferredSize(new Dimension(200, 50));
        button.setToolTipText(tooltip);
        
        // Style the button
        styleButton(button, difficulty);
        
        return button;
    }
    
    /**
     * Creates a regular menu button
     */
    private JButton createMenuButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(fontManager.getMenuFont());
        button.setPreferredSize(new Dimension(200, 40));
        button.setToolTipText(tooltip);
        
        // Style the button
        button.setBackground(new Color(64, 64, 64));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBorderBorder());
        button.setFocusPainted(false);
        
        return button;
    }
    
    /**
     * Styles difficulty buttons with appropriate colors
     */
    private void styleButton(JButton button, DifficultyLevel difficulty) {
        Color backgroundColor;
        Color foregroundColor = Color.WHITE;
        
        switch (difficulty) {
            case EASY:
                backgroundColor = new Color(34, 139, 34); // Forest Green
                break;
            case NORMAL:
                backgroundColor = new Color(255, 140, 0); // Dark Orange
                break;
            case HARD:
                backgroundColor = new Color(220, 20, 60); // Crimson
                break;
            default:
                backgroundColor = Color.GRAY;
        }
        
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
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
    }
    
    /**
     * Sets up the panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Logo panel (top)
        JPanel logoPanel = createLogoPanel();
        add(logoPanel, BorderLayout.NORTH);
        
        // Menu panel (center)
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.CENTER);
        
        // Info panel (bottom)
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the logo panel
     */
    private JPanel createLogoPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Calculate centered position
                int logoX = (getWidth() - asciiLogo.getWidth()) / 2;
                int logoY = 30;
                
                // Render logo with terminal background
                asciiLogo.renderWithBackground(g2d, logoX, logoY, true);
                
                g2d.dispose();
            }
        };
        
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(800, asciiLogo.getHeight() + 100));
        
        return panel;
    }
    
    /**
     * Creates the main menu panel
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Title
        JLabel titleLabel = new JLabel("Selecciona la Dificultad");
        titleLabel.setFont(fontManager.getFont(FontManager.MENU_SIZE + 4, Font.BOLD));
        titleLabel.setForeground(new Color(0, 255, 0)); // Terminal green
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(titleLabel, gbc);
        
        // Difficulty buttons
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        
        gbc.gridx = 0;
        panel.add(easyButton, gbc);
        
        gbc.gridx = 1;
        panel.add(normalButton, gbc);
        
        gbc.gridx = 2;
        panel.add(hardButton, gbc);
        
        // Other buttons
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(highScoresButton, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(exitButton, gbc);
        
        return panel;
    }
    
    /**
     * Creates the info panel
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.BLACK);
        
        JLabel infoLabel = new JLabel("Usa las teclas de flecha y ENTER para navegar | ESC para salir");
        infoLabel.setFont(fontManager.getSmallFont());
        infoLabel.setForeground(Color.LIGHT_GRAY);
        
        panel.add(infoLabel);
        
        return panel;
    }
    
    /**
     * Sets up event handlers for all buttons
     */
    private void setupEventHandlers() {
        easyButton.addActionListener(e -> startGame(DifficultyLevel.EASY));
        normalButton.addActionListener(e -> startGame(DifficultyLevel.NORMAL));
        hardButton.addActionListener(e -> startGame(DifficultyLevel.HARD));
        
        highScoresButton.addActionListener(e -> parentWindow.showHighScores());
        exitButton.addActionListener(e -> parentWindow.exitGame());
    }
    
    /**
     * Starts a new game with the specified difficulty
     */
    private void startGame(DifficultyLevel difficulty) {
        System.out.println("Starting game with difficulty: " + difficulty.getDisplayName());
        parentWindow.startNewGame(difficulty);
    }
    
    /**
     * Updates button selection for keyboard navigation
     */
    private void updateButtonSelection() {
        // Reset all buttons
        resetButtonStyles();
        
        if (keyboardMode) {
            JButton selectedButton = getSelectedButton();
            if (selectedButton != null) {
                selectedButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.YELLOW, 3),
                    BorderFactory.createRaisedBorderBorder()
                ));
            }
        }
    }
    
    /**
     * Gets the currently selected button
     */
    private JButton getSelectedButton() {
        switch (selectedDifficulty) {
            case 0: return easyButton;
            case 1: return normalButton;
            case 2: return hardButton;
            case 3: return highScoresButton;
            case 4: return exitButton;
            default: return normalButton;
        }
    }
    
    /**
     * Resets all button styles
     */
    private void resetButtonStyles() {
        easyButton.setBorder(BorderFactory.createRaisedBorderBorder());
        normalButton.setBorder(BorderFactory.createRaisedBorderBorder());
        hardButton.setBorder(BorderFactory.createRaisedBorderBorder());
        highScoresButton.setBorder(BorderFactory.createRaisedBorderBorder());
        exitButton.setBorder(BorderFactory.createRaisedBorderBorder());
    }
    
    /**
     * Activates the currently selected button
     */
    private void activateSelectedButton() {
        JButton selectedButton = getSelectedButton();
        if (selectedButton != null) {
            selectedButton.doClick();
        }
    }
    
    // KeyListener implementation
    @Override
    public void keyPressed(KeyEvent e) {
        keyboardMode = true;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (selectedDifficulty > 0) {
                    selectedDifficulty--;
                    updateButtonSelection();
                }
                break;
                
            case KeyEvent.VK_RIGHT:
                if (selectedDifficulty < 4) {
                    selectedDifficulty++;
                    updateButtonSelection();
                }
                break;
                
            case KeyEvent.VK_UP:
                if (selectedDifficulty > 2) {
                    selectedDifficulty = Math.max(0, selectedDifficulty - 3);
                    updateButtonSelection();
                }
                break;
                
            case KeyEvent.VK_DOWN:
                if (selectedDifficulty < 3) {
                    selectedDifficulty = Math.min(4, selectedDifficulty + 3);
                    updateButtonSelection();
                }
                break;
                
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                activateSelectedButton();
                break;
                
            case KeyEvent.VK_ESCAPE:
                parentWindow.exitGame();
                break;
                
            case KeyEvent.VK_1:
                startGame(DifficultyLevel.EASY);
                break;
                
            case KeyEvent.VK_2:
                startGame(DifficultyLevel.NORMAL);
                break;
                
            case KeyEvent.VK_3:
                startGame(DifficultyLevel.HARD);
                break;
                
            case KeyEvent.VK_H:
                parentWindow.showHighScores();
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
        
        // Add some visual effects
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw subtle grid pattern
        g2d.setColor(new Color(0, 64, 0, 30)); // Very dark green, semi-transparent
        for (int x = 0; x < getWidth(); x += 50) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += 50) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        
        g2d.dispose();
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
        requestFocusInWindow();
    }
}
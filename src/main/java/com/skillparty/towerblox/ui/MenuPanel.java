package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.ui.components.FontManager;
import com.skillparty.towerblox.ui.components.ASCIILogo;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Enhanced menu panel with background image and modern styling
 */
public class MenuPanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    
    private GameWindow parentWindow;
    private FontManager fontManager;
    private BufferedImage backgroundImage;
    private ASCIILogo asciiLogo;
    
    // Professional corporate color palette - elegant and modern
    private static final Color BUTTON_PRIMARY = new Color(59, 130, 246);      // Professional Blue
    private static final Color BUTTON_SECONDARY = new Color(16, 185, 129);    // Success Green
    private static final Color BUTTON_HOVER = new Color(99, 102, 241);        // Indigo
    private static final Color BUTTON_ACCENT = new Color(239, 68, 68);        // Warning Red
    private static final Color BUTTON_SPECIAL = new Color(245, 158, 11);      // Amber
    private static final Color BUTTON_EXIT = new Color(107, 114, 128);        // Neutral Gray
    private static final Color BUTTON_TEXT = Color.WHITE;                     // White text for contrast
    private static final Color TITLE_COLOR = new Color(248, 250, 252);        // Almost White
    private static final Color TITLE_SHADOW = new Color(30, 41, 59);          // Dark Slate
    private static final Color OVERLAY = new Color(0, 0, 0, 35);              // Subtle overlay
    
    // Simple button system
    private int selectedButton = 1; // 0=Easy, 1=Normal, 2=Hard, 3=Movements, 4=HighScores, 5=Exit
    private Rectangle[] buttonBounds;
    private String[] buttonTexts = {"FÁCIL", "NORMAL", "DIFÍCIL", "MOVIMIENTOS", "PUNTUACIONES", "SALIR"};
    private DifficultyLevel[] difficulties = {DifficultyLevel.EASY, DifficultyLevel.NORMAL, DifficultyLevel.HARD, null, null, null};

    public MenuPanel(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        this.fontManager = FontManager.getInstance();
        this.asciiLogo = new ASCIILogo();
        
        loadBackgroundImage();
        initializePanel();
        createButtons();
        setupEventHandlers();
        
        setFocusable(true);
        addKeyListener(this);
    }
    
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("public/img/logo_app.png"));
            System.out.println("Menu background image loaded successfully");
        } catch (IOException e) {
            System.err.println("Could not load background image: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void initializePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600));
        setDoubleBuffered(true);
    }
    
    private void createButtons() {
        buttonBounds = new Rectangle[6];
        
        // Difficulty buttons in a row - positioned lower to avoid covering the image title
        int buttonWidth = 140;
        int buttonHeight = 55;
        int spacing = 25;
        int startY = 420; // Moved down to avoid image title
        int totalWidth = 3 * buttonWidth + 2 * spacing;
        int startX = (800 - totalWidth) / 2;
        
        for (int i = 0; i < 3; i++) {
            buttonBounds[i] = new Rectangle(startX + i * (buttonWidth + spacing), startY, buttonWidth, buttonHeight);
        }
        
        // Special buttons below - now 3 buttons in a row
        int specialY = startY + buttonHeight + 35;
        int specialWidth = 150;
        int specialHeight = 45;
        int specialTotalWidth = 3 * specialWidth + 2 * spacing;
        int specialStartX = (800 - specialTotalWidth) / 2;
        
        buttonBounds[3] = new Rectangle(specialStartX, specialY, specialWidth, specialHeight);
        buttonBounds[4] = new Rectangle(specialStartX + specialWidth + spacing, specialY, specialWidth, specialHeight);
        buttonBounds[5] = new Rectangle(specialStartX + 2 * (specialWidth + spacing), specialY, specialWidth, specialHeight);
    }

    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < buttonBounds.length; i++) {
                    if (buttonBounds[i].contains(e.getPoint())) {
                        activateButton(i);
                        break;
                    }
                }
            }
        });
    }
    
    private void activateButton(int index) {
        if (index < 3) {
            // Difficulty buttons
            startGame(difficulties[index]);
        } else if (index == 3) {
            // Movements
            parentWindow.showMovements();
        } else if (index == 4) {
            // High scores
            parentWindow.showHighScores();
        } else if (index == 5) {
            // Exit
            parentWindow.exitGame();
        }
    }
    
    private void startGame(DifficultyLevel difficulty) {
        System.out.println("Starting game with difficulty: " + difficulty.getDisplayName());
        parentWindow.startNewGame(difficulty);
    }

    // KeyListener implementation
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (selectedButton > 0) {
                    selectedButton--;
                }
                break;
                
            case KeyEvent.VK_RIGHT:
                if (selectedButton < buttonTexts.length - 1) {
                    selectedButton++;
                }
                break;
                
            case KeyEvent.VK_UP:
                if (selectedButton > 2) {
                    selectedButton = Math.max(0, selectedButton - 3);
                }
                break;
                
            case KeyEvent.VK_DOWN:
                if (selectedButton < 3) {
                    selectedButton = Math.min(buttonTexts.length - 1, selectedButton + 3);
                }
                break;
                
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                activateButton(selectedButton);
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
        }
        
        repaint();
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
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw background image
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback gradient background with modern colors
            GradientPaint gradient = new GradientPaint(0, 0, new Color(44, 62, 80), 0, getHeight(), new Color(52, 73, 94));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Draw subtle overlay for better contrast without covering the image completely
        g2d.setColor(OVERLAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw ASCII logo positioned above the image title
        drawASCIILogo(g2d);
        
        // Title removed to avoid overlapping with buttons
        
        // Draw buttons
        for (int i = 0; i < buttonBounds.length; i++) {
            drawButton(g2d, i);
        }
        
        // Draw modern instructions with better styling
        g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE));
        String instructions = "Usa las flechas y ENTER para navegar, o haz clic con el ratón";
        FontMetrics fm = g2d.getFontMetrics();
        int instrX = (getWidth() - fm.stringWidth(instructions)) / 2;
        int instrY = getHeight() - 25;
        
        // Background for instructions
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(instrX - 10, instrY - fm.getAscent() - 3, 
                         fm.stringWidth(instructions) + 20, fm.getHeight() + 6, 8, 8);
        
        // Instructions text with modern color
        g2d.setColor(new Color(BUTTON_PRIMARY.getRed(), BUTTON_PRIMARY.getGreen(), BUTTON_PRIMARY.getBlue(), 255));
        g2d.drawString(instructions, instrX, instrY);
        
        g2d.dispose();
    }
    
    private void drawASCIILogo(Graphics2D g2d) {
        // Position the ASCII logo in the upper area, above everything else
        int logoX = (getWidth() - asciiLogo.getWidth()) / 2;
        int logoY = 40; // Positioned high to avoid covering the image title
        
        // Create a semi-transparent background with blur effect
        int padding = 20;
        int bgWidth = asciiLogo.getWidth() + (padding * 2);
        int bgHeight = asciiLogo.getHeight() + (padding * 2);
        
        // Draw blurred background
        g2d.setColor(new Color(0, 0, 0, 100)); // Semi-transparent black
        g2d.fillRoundRect(logoX - padding, logoY - padding - 30, bgWidth, bgHeight, 15, 15);
        
        // Draw professional border with gradient effect
        GradientPaint borderGradient = new GradientPaint(
            logoX - padding, logoY - padding - 30,
            new Color(BUTTON_PRIMARY.getRed(), BUTTON_PRIMARY.getGreen(), BUTTON_PRIMARY.getBlue(), 200),
            logoX - padding, logoY - padding - 30 + bgHeight,
            new Color(BUTTON_SECONDARY.getRed(), BUTTON_SECONDARY.getGreen(), BUTTON_SECONDARY.getBlue(), 150)
        );
        g2d.setPaint(borderGradient);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(logoX - padding, logoY - padding - 30, bgWidth, bgHeight, 15, 15);
        
        // Draw the ASCII logo without terminal background (we created our own)
        asciiLogo.render(g2d, logoX, logoY);
    }
    
    private void drawTitle(Graphics2D g2d) {
        // Position the title below the ASCII logo but above the image title
        String title = "javaTower";
        g2d.setFont(fontManager.getFont(FontManager.LOGO_SIZE + 4, Font.BOLD));
        FontMetrics fm = g2d.getFontMetrics();
        
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 40 + asciiLogo.getHeight() + 50; // Below ASCII logo with some spacing
        
        // Modern glass effect background for title
        int titlePadding = 20;
        GradientPaint titleBg = new GradientPaint(
            titleX - titlePadding, titleY - fm.getAscent() - 5,
            new Color(BUTTON_PRIMARY.getRed(), BUTTON_PRIMARY.getGreen(), BUTTON_PRIMARY.getBlue(), 80),
            titleX - titlePadding, titleY + 10,
            new Color(BUTTON_SECONDARY.getRed(), BUTTON_SECONDARY.getGreen(), BUTTON_SECONDARY.getBlue(), 60)
        );
        g2d.setPaint(titleBg);
        g2d.fillRoundRect(titleX - titlePadding, titleY - fm.getAscent() - 5, 
                         fm.stringWidth(title) + (titlePadding * 2), fm.getHeight() + 10, 15, 15);
        
        // Modern border for title
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(titleX - titlePadding, titleY - fm.getAscent() - 5, 
                         fm.stringWidth(title) + (titlePadding * 2), fm.getHeight() + 10, 15, 15);
        
        // Enhanced shadow with modern colors
        g2d.setColor(new Color(TITLE_SHADOW.getRed(), TITLE_SHADOW.getGreen(), TITLE_SHADOW.getBlue(), 200));
        g2d.drawString(title, titleX + 2, titleY + 2);
        
        // Main title with modern color
        g2d.setColor(TITLE_COLOR);
        g2d.drawString(title, titleX, titleY);
    }
    
    private void drawButton(Graphics2D g2d, int index) {
        Rectangle bounds = buttonBounds[index];
        boolean isSelected = (index == selectedButton);
        
        // Modern button design with different colors for each button type
        Color baseColor = getButtonColor(index);
        Color hoverColor = isSelected ? BUTTON_HOVER : baseColor;
        
        // Create modern glass effect background
        g2d.setColor(new Color(255, 255, 255, 20)); // Light glass effect
        g2d.fillRoundRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, 20, 20);
        
        // Main button gradient with modern colors
        GradientPaint buttonGradient = new GradientPaint(
            bounds.x, bounds.y, new Color(hoverColor.getRed(), hoverColor.getGreen(), hoverColor.getBlue(), 200),
            bounds.x, bounds.y + bounds.height, new Color(hoverColor.getRed(), hoverColor.getGreen(), hoverColor.getBlue(), 160)
        );
        
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 18, 18);
        
        // Modern border with glow effect
        if (isSelected) {
            // Outer glow for selected button
            g2d.setColor(new Color(hoverColor.getRed(), hoverColor.getGreen(), hoverColor.getBlue(), 100));
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRoundRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, 22, 22);
            
            // Inner bright border
            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2, 16, 16);
        } else {
            // Subtle border for unselected buttons
            g2d.setColor(new Color(255, 255, 255, 120));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 18, 18);
        }
        
        // Modern button text with shadow
        g2d.setFont(fontManager.getFont(FontManager.MENU_SIZE, Font.BOLD));
        FontMetrics fm = g2d.getFontMetrics();
        
        String text = buttonTexts[index];
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + (bounds.height + fm.getAscent()) / 2 - 2;
        
        // Text shadow for depth
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(text, textX + 1, textY + 1);
        
        // Main text
        g2d.setColor(BUTTON_TEXT);
        g2d.drawString(text, textX, textY);
    }
    
    /**
     * Gets the appropriate color for each button type with professional palette
     */
    private Color getButtonColor(int index) {
        switch (index) {
            case 0: return BUTTON_SECONDARY;  // Easy - Success Green
            case 1: return BUTTON_PRIMARY;   // Normal - Professional Blue  
            case 2: return BUTTON_ACCENT;    // Hard - Warning Red
            case 3: return new Color(147, 51, 234);  // Movements - Purple
            case 4: return BUTTON_SPECIAL;   // High Scores - Amber
            case 5: return BUTTON_EXIT;      // Exit - Neutral Gray
            default: return BUTTON_PRIMARY;
        }
    }
}
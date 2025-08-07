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
        
        // Block-style buttons positioned to work with background image
        int blockWidth = 150;
        int blockHeight = 55;
        int spacing = 25;
        
        // First row - Difficulty buttons positioned below ASCII logo
        int startY = 320; // Positioned below the ASCII logo
        int totalWidth = 3 * blockWidth + 2 * spacing;
        int startX = (800 - totalWidth) / 2;
        
        for (int i = 0; i < 3; i++) {
            buttonBounds[i] = new Rectangle(startX + i * (blockWidth + spacing), startY, blockWidth, blockHeight);
        }
        
        // Second row - Special buttons stacked below
        int secondRowY = startY + blockHeight + 20;
        int secondRowWidth = 3 * blockWidth + 2 * spacing;
        int secondRowX = (800 - secondRowWidth) / 2;
        
        buttonBounds[3] = new Rectangle(secondRowX, secondRowY, blockWidth, blockHeight);
        buttonBounds[4] = new Rectangle(secondRowX + blockWidth + spacing, secondRowY, blockWidth, blockHeight);
        buttonBounds[5] = new Rectangle(secondRowX + 2 * (blockWidth + spacing), secondRowY, blockWidth, blockHeight);
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
        
        // Draw professional construction site background
        drawConstructionBackground(g2d);
        
        // Draw ASCII logo with terminal effect only
        drawASCIILogo(g2d);
        
        // Draw block-style buttons
        for (int i = 0; i < buttonBounds.length; i++) {
            drawBlockButton(g2d, i);
        }
        
        // Draw construction-themed instructions
        drawInstructions(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Draws background using the PNG image with overlay for block buttons
     */
    private void drawConstructionBackground(Graphics2D g2d) {
        // Draw the PNG background image
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback construction site background if image fails to load
            GradientPaint skyGradient = new GradientPaint(
                0, 0, new Color(135, 206, 235), // Sky blue
                0, getHeight() * 0.6f, new Color(176, 224, 230) // Light blue
            );
            g2d.setPaint(skyGradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Ground
            g2d.setColor(new Color(139, 69, 19)); // Saddle brown
            g2d.fillRect(0, (int)(getHeight() * 0.85), getWidth(), getHeight());
            
            // Construction site elements
            drawConstructionElements(g2d);
        }
        
        // Subtle overlay for better contrast with block buttons
        g2d.setColor(new Color(0, 0, 0, 25));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Draws construction site elements in the background
     */
    private void drawConstructionElements(Graphics2D g2d) {
        // Simple building silhouettes in the background
        g2d.setColor(new Color(70, 70, 70, 100));
        
        // Building 1
        g2d.fillRect(50, (int)(getHeight() * 0.7), 80, (int)(getHeight() * 0.15));
        
        // Building 2
        g2d.fillRect(200, (int)(getHeight() * 0.6), 60, (int)(getHeight() * 0.25));
        
        // Building 3
        g2d.fillRect(getWidth() - 150, (int)(getHeight() * 0.65), 100, (int)(getHeight() * 0.2));
        
        // Construction crane silhouette
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(100, 100, 100, 150));
        
        // Crane mast
        int craneX = getWidth() - 100;
        int craneBase = (int)(getHeight() * 0.85);
        int craneTop = (int)(getHeight() * 0.3);
        g2d.drawLine(craneX, craneBase, craneX, craneTop);
        
        // Crane arm
        g2d.drawLine(craneX - 80, craneTop, craneX + 60, craneTop);
        
        // Crane hook
        g2d.drawLine(craneX + 20, craneTop, craneX + 20, craneTop + 30);
    }
    
    /**
     * Draws ASCII logo with professional styling and blur effect
     */
    private void drawASCIILogo(Graphics2D g2d) {
        // Position the ASCII logo at the top
        int logoX = (getWidth() - asciiLogo.getWidth()) / 2;
        int logoY = 50;
        
        // Draw professional ASCII logo with blur effect
        drawProfessionalASCIILogo(g2d, logoX, logoY);
    }
    
    /**
     * Draws the ASCII logo with professional styling, blur effect and transparency
     */
    private void drawProfessionalASCIILogo(Graphics2D g2d, int x, int y) {
        // Professional terminal window dimensions
        int padding = 25;
        int bgWidth = asciiLogo.getWidth() + (padding * 2);
        int bgHeight = asciiLogo.getHeight() + (padding * 2);
        int titleBarHeight = 30;
        
        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw blur effect background (multiple layers for better blur)
        for (int i = 8; i >= 0; i--) {
            int alpha = 15 - i;
            g2d.setColor(new Color(0, 0, 0, alpha));
            g2d.fillRoundRect(x - padding - i, y - padding - titleBarHeight - i, 
                            bgWidth + (i * 2), bgHeight + titleBarHeight + (i * 2), 15 + i, 15 + i);
        }
        
        // Main terminal background with professional transparency
        GradientPaint terminalBg = new GradientPaint(
            x - padding, y - padding - titleBarHeight,
            new Color(30, 30, 30, 200), // Dark professional background
            x - padding, y - padding - titleBarHeight + bgHeight + titleBarHeight,
            new Color(20, 20, 20, 180)  // Slightly lighter at bottom
        );
        g2d.setPaint(terminalBg);
        g2d.fillRoundRect(x - padding, y - padding - titleBarHeight, bgWidth, bgHeight + titleBarHeight, 12, 12);
        
        // Professional title bar with gradient
        GradientPaint titleBarGradient = new GradientPaint(
            x - padding, y - padding - titleBarHeight,
            new Color(60, 60, 60, 220), // Professional gray
            x - padding, y - padding,
            new Color(45, 45, 45, 200)  // Darker gray
        );
        g2d.setPaint(titleBarGradient);
        g2d.fillRoundRect(x - padding, y - padding - titleBarHeight, bgWidth, titleBarHeight, 12, 12);
        
        // Terminal window controls (macOS style for professional look)
        int controlY = y - padding - titleBarHeight + 8;
        int controlSize = 12;
        int controlSpacing = 20;
        
        // Close button (red)
        GradientPaint redGradient = new GradientPaint(
            x - padding + 15, controlY,
            new Color(255, 95, 87),
            x - padding + 15, controlY + controlSize,
            new Color(255, 70, 61)
        );
        g2d.setPaint(redGradient);
        g2d.fillOval(x - padding + 15, controlY, controlSize, controlSize);
        
        // Minimize button (yellow)
        GradientPaint yellowGradient = new GradientPaint(
            x - padding + 15 + controlSpacing, controlY,
            new Color(255, 189, 46),
            x - padding + 15 + controlSpacing, controlY + controlSize,
            new Color(255, 159, 10)
        );
        g2d.setPaint(yellowGradient);
        g2d.fillOval(x - padding + 15 + controlSpacing, controlY, controlSize, controlSize);
        
        // Maximize button (green)
        GradientPaint greenGradient = new GradientPaint(
            x - padding + 15 + (controlSpacing * 2), controlY,
            new Color(39, 201, 63),
            x - padding + 15 + (controlSpacing * 2), controlY + controlSize,
            new Color(25, 185, 45)
        );
        g2d.setPaint(greenGradient);
        g2d.fillOval(x - padding + 15 + (controlSpacing * 2), controlY, controlSize, controlSize);
        
        // Terminal title text
        g2d.setColor(new Color(200, 200, 200, 180));
        g2d.setFont(new Font("SF Pro Display", Font.PLAIN, 11));
        String terminalTitle = "javaTower@terminal: ~/development";
        g2d.drawString(terminalTitle, x - padding + 80, controlY + 9);
        
        // Professional border with subtle glow
        g2d.setColor(new Color(100, 100, 100, 120));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x - padding, y - padding - titleBarHeight, bgWidth, bgHeight + titleBarHeight, 12, 12);
        
        // Inner glow effect
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.drawRoundRect(x - padding + 1, y - padding - titleBarHeight + 1, 
                         bgWidth - 2, bgHeight + titleBarHeight - 2, 11, 11);
        
        // Draw the ASCII logo with professional colors
        drawProfessionalASCIIText(g2d, x, y);
    }
    
    /**
     * Draws the ASCII text with professional color scheme
     */
    private void drawProfessionalASCIIText(Graphics2D g2d, int x, int y) {
        // Get the ASCII logo lines
        String[] logoLines = {
            "     ██╗ █████╗ ██╗   ██╗ █████╗ ████████╗ ██████╗ ██╗    ██╗███████╗██████╗ ",
            "     ██║██╔══██╗██║   ██║██╔══██╗╚══██╔══╝██╔═══██╗██║    ██║██╔════╝██╔══██╗",
            "     ██║███████║██║   ██║███████║   ██║   ██║   ██║██║ █╗ ██║█████╗  ██████╔╝",
            "██   ██║██╔══██║╚██╗ ██╔╝██╔══██║   ██║   ██║   ██║██║███╗██║██╔══╝  ██╔══██╗",
            "╚█████╔╝██║  ██║ ╚████╔╝ ██║  ██║   ██║   ╚██████╔╝╚███╔███╔╝███████╗██║  ██║",
            " ╚════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝   ╚═╝    ╚═════╝  ╚══╝╚══╝ ╚══════╝╚═╝  ╚═╝"
        };
        
        String subtitle = "development by joseAlejandro";
        
        // Professional monospace font
        g2d.setFont(new Font("Monaco", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        
        int currentY = y;
        
        // Draw main logo with professional gradient colors
        for (int i = 0; i < logoLines.length; i++) {
            String line = logoLines[i];
            
            // Professional color scheme with subtle gradients
            Color lineColor;
            if (i < 3) {
                // Top part - Professional blue to cyan gradient
                float ratio = (float) i / 2;
                lineColor = blendColors(new Color(52, 152, 219), new Color(46, 204, 113), ratio);
            } else {
                // Bottom part - Cyan to blue gradient
                float ratio = (float) (i - 3) / 2;
                lineColor = blendColors(new Color(46, 204, 113), new Color(155, 89, 182), ratio);
            }
            
            // Add transparency for professional look
            lineColor = new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 220);
            
            int lineWidth = fm.stringWidth(line);
            int lineX = x + (asciiLogo.getWidth() - lineWidth) / 2;
            
            // Subtle shadow for depth
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(line, lineX + 1, currentY + 1);
            
            // Main text with professional color
            g2d.setColor(lineColor);
            g2d.drawString(line, lineX, currentY);
            
            currentY += fm.getHeight() + 2;
        }
        
        // Add spacing before subtitle
        currentY += 10;
        
        // Draw subtitle with professional styling
        g2d.setFont(new Font("Monaco", Font.ITALIC, 12));
        FontMetrics subFm = g2d.getFontMetrics();
        int subWidth = subFm.stringWidth(subtitle);
        int subX = x + (asciiLogo.getWidth() - subWidth) / 2;
        
        // Subtitle shadow
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.drawString(subtitle, subX + 1, currentY + 1);
        
        // Subtitle with professional amber color
        g2d.setColor(new Color(241, 196, 15, 200));
        g2d.drawString(subtitle, subX, currentY);
    }
    
    /**
     * Blends two colors for gradient effects
     */
    private Color blendColors(Color color1, Color color2, float ratio) {
        ratio = Math.max(0.0f, Math.min(1.0f, ratio));
        
        int red = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int blue = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        
        return new Color(red, green, blue);
    }
    
    /**
     * Draws ASCII subtitle below the main title
     */
    private void drawASCIISubtitle(Graphics2D g2d) {
        // ASCII art for "development by joseAlejandro"
        String[] asciiLines = {
            "█▀▄ █▀▀ █ █ █▀▀ █   █▀█ █▀█ █▄█ █▀▀ █▄█ ▀█▀   █▀▄ █ █",
            "█ █ █▀▀ ▀▄▀ █▀▀ █   █ █ █▀▀ █▀█ █▀▀ █ █  █    █▀▄ ▀█▀",
            "█▄▀ █▄▄  █  █▄▄ █▄▄ █▄█ █   █ █ █▄▄ █ █  █    █▄▀  █ ",
            "",
            "  ▄▄█ █▀█ █▀▀ █▀▀ ▄▀█ █   █▀▀ ▄▄█ ▄▀█ █▄█ █▀▄ █▀█ █▀█",
            "  █▄█ █▄█ ▄▄█ █▄▄ █▀█ █▄▄ █▄▄ █▄█ █▀█ █ █ █▀▄ █▄█ █▄█"
        };
        
        // Use monospace font for ASCII art
        g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        
        int startY = 280; // Position below ASCII logo and main title
        
        for (int i = 0; i < asciiLines.length; i++) {
            String line = asciiLines[i];
            if (!line.isEmpty()) {
                int lineX = (getWidth() - fm.stringWidth(line)) / 2;
                int lineY = startY + (i * (fm.getHeight() + 2));
                
                // Shadow for visibility
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.drawString(line, lineX + 1, lineY + 1);
                
                // Main ASCII text in complementary color
                g2d.setColor(new Color(46, 204, 113)); // Green that complements the blue title
                g2d.drawString(line, lineX, lineY);
            }
        }
    }
    
    /**
     * Draws the main javaTower title with new color scheme
     */
    private void drawMainTitle(Graphics2D g2d) {
        String title = "javaTower";
        g2d.setFont(fontManager.getFont(FontManager.LOGO_SIZE + 10, Font.BOLD));
        FontMetrics fm = g2d.getFontMetrics();
        
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 200; // Position below ASCII logo
        
        // Strong shadow for visibility over image
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.drawString(title, titleX + 3, titleY + 3);
        
        // Main title with new color scheme - professional blue/teal
        GradientPaint titleGradient = new GradientPaint(
            titleX, titleY - fm.getAscent(),
            new Color(41, 128, 185), // Professional blue
            titleX, titleY,
            new Color(52, 152, 219)  // Lighter blue
        );
        g2d.setPaint(titleGradient);
        g2d.drawString(title, titleX, titleY);
    }
    
    /**
     * Draws buttons that look like construction blocks
     */
    private void drawBlockButton(Graphics2D g2d, int index) {
        Rectangle bounds = buttonBounds[index];
        boolean isSelected = (index == selectedButton);
        
        Color blockColor = getBlockColor(index);
        
        // Draw 3D block effect
        draw3DBlock(g2d, bounds, blockColor, isSelected);
        
        // Draw button text
        drawBlockText(g2d, bounds, buttonTexts[index], isSelected);
        
        // Add construction details
        drawBlockDetails(g2d, bounds, blockColor, isSelected);
    }
    
    /**
     * Draws a 3D construction block
     */
    private void draw3DBlock(Graphics2D g2d, Rectangle bounds, Color baseColor, boolean isSelected) {
        // Calculate 3D offset
        int offset = isSelected ? 8 : 6;
        
        // Draw shadow (bottom-right face)
        Color shadowColor = new Color(
            Math.max(0, baseColor.getRed() - 80),
            Math.max(0, baseColor.getGreen() - 80),
            Math.max(0, baseColor.getBlue() - 80)
        );
        g2d.setColor(shadowColor);
        
        // Right face
        int[] rightX = {bounds.x + bounds.width, bounds.x + bounds.width + offset, 
                       bounds.x + bounds.width + offset, bounds.x + bounds.width};
        int[] rightY = {bounds.y, bounds.y - offset, 
                       bounds.y + bounds.height - offset, bounds.y + bounds.height};
        g2d.fillPolygon(rightX, rightY, 4);
        
        // Top face
        int[] topX = {bounds.x, bounds.x + offset, 
                     bounds.x + bounds.width + offset, bounds.x + bounds.width};
        int[] topY = {bounds.y, bounds.y - offset, 
                     bounds.y - offset, bounds.y};
        g2d.fillPolygon(topX, topY, 4);
        
        // Main face (front)
        GradientPaint blockGradient = new GradientPaint(
            bounds.x, bounds.y,
            isSelected ? brightenColor(baseColor, 30) : baseColor,
            bounds.x, bounds.y + bounds.height,
            isSelected ? baseColor : darkenColor(baseColor, 20)
        );
        g2d.setPaint(blockGradient);
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Block outline
        g2d.setColor(darkenColor(baseColor, 40));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // 3D edges
        g2d.setColor(darkenColor(shadowColor, 20));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawPolygon(rightX, rightY, 4);
        g2d.drawPolygon(topX, topY, 4);
        
        // Selection glow
        if (isSelected) {
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
        }
    }
    
    /**
     * Draws text on the block
     */
    private void drawBlockText(Graphics2D g2d, Rectangle bounds, String text, boolean isSelected) {
        g2d.setFont(fontManager.getFont(FontManager.MENU_SIZE, Font.BOLD));
        FontMetrics fm = g2d.getFontMetrics();
        
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + (bounds.height + fm.getAscent()) / 2 - 2;
        
        // Text shadow for depth
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.drawString(text, textX + 2, textY + 2);
        
        // Main text
        g2d.setColor(isSelected ? Color.WHITE : new Color(240, 240, 240));
        g2d.drawString(text, textX, textY);
    }
    
    /**
     * Draws construction block details (rivets, texture)
     */
    private void drawBlockDetails(Graphics2D g2d, Rectangle bounds, Color baseColor, boolean isSelected) {
        // Draw rivets/bolts on the corners
        Color rivetColor = darkenColor(baseColor, 60);
        g2d.setColor(rivetColor);
        
        int rivetSize = 4;
        // Top-left rivet
        g2d.fillOval(bounds.x + 8, bounds.y + 8, rivetSize, rivetSize);
        // Top-right rivet
        g2d.fillOval(bounds.x + bounds.width - 12, bounds.y + 8, rivetSize, rivetSize);
        // Bottom-left rivet
        g2d.fillOval(bounds.x + 8, bounds.y + bounds.height - 12, rivetSize, rivetSize);
        // Bottom-right rivet
        g2d.fillOval(bounds.x + bounds.width - 12, bounds.y + bounds.height - 12, rivetSize, rivetSize);
        
        // Add highlight to rivets
        g2d.setColor(brightenColor(rivetColor, 40));
        g2d.fillOval(bounds.x + 8, bounds.y + 8, rivetSize/2, rivetSize/2);
        g2d.fillOval(bounds.x + bounds.width - 12, bounds.y + 8, rivetSize/2, rivetSize/2);
        g2d.fillOval(bounds.x + 8, bounds.y + bounds.height - 12, rivetSize/2, rivetSize/2);
        g2d.fillOval(bounds.x + bounds.width - 12, bounds.y + bounds.height - 12, rivetSize/2, rivetSize/2);
        
        // Add subtle texture lines
        g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 50));
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < 3; i++) {
            int lineY = bounds.y + (bounds.height / 4) * (i + 1);
            g2d.drawLine(bounds.x + 15, lineY, bounds.x + bounds.width - 15, lineY);
        }
    }
    
    /**
     * Draws construction-themed instructions
     */
    private void drawInstructions(Graphics2D g2d) {
        g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE));
        String instructions = "Usa las flechas y ENTER para navegar, o haz clic con el ratón";
        FontMetrics fm = g2d.getFontMetrics();
        int instrX = (getWidth() - fm.stringWidth(instructions)) / 2;
        int instrY = getHeight() - 25;
        
        // Construction sign background
        g2d.setColor(new Color(255, 165, 0)); // Orange construction color
        g2d.fillRoundRect(instrX - 15, instrY - fm.getAscent() - 5, 
                         fm.stringWidth(instructions) + 30, fm.getHeight() + 10, 10, 10);
        
        // Black border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(instrX - 15, instrY - fm.getAscent() - 5, 
                         fm.stringWidth(instructions) + 30, fm.getHeight() + 10, 10, 10);
        
        // Instructions text
        g2d.setColor(Color.BLACK);
        g2d.drawString(instructions, instrX, instrY);
    }
    
    /**
     * Gets construction block colors that complement the background image
     */
    private Color getBlockColor(int index) {
        switch (index) {
            case 0: return new Color(39, 174, 96);    // Easy - Forest Green (professional)
            case 1: return new Color(41, 128, 185);   // Normal - Ocean Blue (matches image tones)  
            case 2: return new Color(192, 57, 43);    // Hard - Deep Red (strong contrast)
            case 3: return new Color(142, 68, 173);   // Movements - Royal Purple
            case 4: return new Color(211, 84, 0);     // High Scores - Construction Orange
            case 5: return new Color(127, 140, 141);  // Exit - Professional Gray
            default: return new Color(41, 128, 185);
        }
    }
    
    /**
     * Brightens a color by the specified amount
     */
    private Color brightenColor(Color color, int amount) {
        return new Color(
            Math.min(255, color.getRed() + amount),
            Math.min(255, color.getGreen() + amount),
            Math.min(255, color.getBlue() + amount)
        );
    }
    
    /**
     * Darkens a color by the specified amount
     */
    private Color darkenColor(Color color, int amount) {
        return new Color(
            Math.max(0, color.getRed() - amount),
            Math.max(0, color.getGreen() - amount),
            Math.max(0, color.getBlue() - amount)
        );
    }
}
package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.ui.GamePanel;
import com.skillparty.towerblox.ui.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Main menu screen with Tower Bloxx background image
 */
public class MainMenu extends JPanel {
    private static final int MENU_WIDTH = 1280;
    private static final int MENU_HEIGHT = 720;
    
    private BufferedImage backgroundImage;
    private ImageManager imageManager;
    private JFrame parentFrame;
    private boolean isHoveringPlay = false;
    private boolean isHoveringQuit = false;
    
    // Button areas
    private Rectangle playButtonArea;
    private Rectangle quitButtonArea;
    
    public MainMenu(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.imageManager = ImageManager.getInstance();
        
        setPreferredSize(new Dimension(MENU_WIDTH, MENU_HEIGHT));
        setBackground(Color.BLACK);
        
        // Load the background image
        loadBackgroundImage();
        
        // Initialize button areas
        initializeButtons();
        
        // Add mouse listeners for interactive buttons
        addMouseListeners();
        
        // Set focusable for keyboard input
        setFocusable(true);
    }
    
    private void loadBackgroundImage() {
        try {
            // Load the specific background image from public/img folder
            BufferedImage originalBg = imageManager.loadImage("ChatGPT Image Aug 5, 2025 at 10_09_33 PM-2.png");
            if (originalBg != null) {
                backgroundImage = imageManager.scaleImage(originalBg, MENU_WIDTH, MENU_HEIGHT);
            } else {
                // Create a fallback image if none found
                backgroundImage = createFallbackBackground();
            }
        } catch (Exception e) {
            System.err.println("Error loading menu background: " + e.getMessage());
            backgroundImage = null;
        }
    }
    
    private void initializeButtons() {
        // Calculate button positions based on screen size
        int buttonWidth = 400;
        int buttonHeight = 80;
        int centerX = MENU_WIDTH / 2;
        int centerY = MENU_HEIGHT / 2;
        
        // Play button
        playButtonArea = new Rectangle(
            centerX - buttonWidth / 2,
            centerY - 50,
            buttonWidth,
            buttonHeight
        );
        
        // Quit button
        quitButtonArea = new Rectangle(
            centerX - buttonWidth / 2,
            centerY + 50,
            buttonWidth,
            buttonHeight
        );
    }
    
    private void addMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (playButtonArea.contains(e.getPoint())) {
                    startGame();
                } else if (quitButtonArea.contains(e.getPoint())) {
                    quitGame();
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean wasHoveringPlay = isHoveringPlay;
                boolean wasHoveringQuit = isHoveringQuit;
                
                isHoveringPlay = playButtonArea.contains(e.getPoint());
                isHoveringQuit = quitButtonArea.contains(e.getPoint());
                
                // Repaint if hover state changed
                if (wasHoveringPlay != isHoveringPlay || wasHoveringQuit != isHoveringQuit) {
                    repaint();
                }
                
                // Change cursor for buttons
                if (isHoveringPlay || isHoveringQuit) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }
    
    private void startGame() {
        System.out.println("Starting Tower Bloxx game...");
        
        // Close current menu frame
        parentFrame.setVisible(false);
        
        // Create and start the game
        SwingUtilities.invokeLater(() -> {
            try {
                GameWindow gameWindow = new GameWindow();
                gameWindow.setVisible(true);
                
                System.out.println("Game started successfully!");
            } catch (Exception e) {
                System.err.println("Error starting game: " + e.getMessage());
                e.printStackTrace();
                // Show menu again if game fails to start
                parentFrame.setVisible(true);
            }
        });
    }
    
    private void quitGame() {
        System.out.println("Quitting Tower Bloxx...");
        System.exit(0);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable antialiasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw background image or gradient fallback
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, MENU_WIDTH, MENU_HEIGHT, this);
        } else {
            // Fallback gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(135, 206, 250),
                0, MENU_HEIGHT, new Color(255, 218, 185)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, MENU_WIDTH, MENU_HEIGHT);
        }
        
        // Add semi-transparent overlay for better text readability
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, MENU_WIDTH, MENU_HEIGHT);
        
        // Draw game title
        drawTitle(g2d);
        
        // Draw menu buttons
        drawButtons(g2d);
        
        // Draw version info
        drawVersionInfo(g2d);
        
        g2d.dispose();
    }
    
    private void drawTitle(Graphics2D g2d) {
        // Main title
        Font titleFont = new Font("Arial", Font.BOLD, 120);
        g2d.setFont(titleFont);
        
        String title = "TOWER BLOXX";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = (MENU_WIDTH - titleWidth) / 2;
        int titleY = 400;
        
        // Title shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(title, titleX + 5, titleY + 5);
        
        // Title main text with gradient effect
        GradientPaint titleGradient = new GradientPaint(
            titleX, titleY - 60, new Color(255, 255, 255),
            titleX, titleY + 20, new Color(255, 215, 0)
        );
        g2d.setPaint(titleGradient);
        g2d.drawString(title, titleX, titleY);
        
        // Subtitle
        Font subtitleFont = new Font("Arial", Font.ITALIC, 40);
        g2d.setFont(subtitleFont);
        g2d.setColor(new Color(255, 255, 255, 200));
        
        String subtitle = "Digital Chocolate Edition";
        FontMetrics subtitleFm = g2d.getFontMetrics();
        int subtitleWidth = subtitleFm.stringWidth(subtitle);
        int subtitleX = (MENU_WIDTH - subtitleWidth) / 2;
        g2d.drawString(subtitle, subtitleX, titleY + 80);
    }
    
    private void drawButtons(Graphics2D g2d) {
        Font buttonFont = new Font("Arial", Font.BOLD, 48);
        g2d.setFont(buttonFont);
        
        // Play button
        drawButton(g2d, playButtonArea, "PLAY", isHoveringPlay);
        
        // Quit button
        drawButton(g2d, quitButtonArea, "QUIT", isHoveringQuit);
    }
    
    private void drawButton(Graphics2D g2d, Rectangle buttonArea, String text, boolean isHovering) {
        // Button background
        if (isHovering) {
            g2d.setColor(new Color(255, 215, 0, 200)); // Gold when hovering
        } else {
            g2d.setColor(new Color(70, 130, 180, 150)); // Steel blue normally
        }
        
        g2d.fillRoundRect(buttonArea.x, buttonArea.y, buttonArea.width, buttonArea.height, 20, 20);
        
        // Button border
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(buttonArea.x, buttonArea.y, buttonArea.width, buttonArea.height, 20, 20);
        
        // Button text
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        
        int textX = buttonArea.x + (buttonArea.width - textWidth) / 2;
        int textY = buttonArea.y + (buttonArea.height + textHeight) / 2 - 5;
        
        // Text shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(text, textX + 2, textY + 2);
        
        // Text main
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }
    
    private void drawVersionInfo(Graphics2D g2d) {
        Font versionFont = new Font("Arial", Font.PLAIN, 24);
        g2d.setFont(versionFont);
        g2d.setColor(new Color(255, 255, 255, 150));
        
        String versionText = "v1.0 - Enhanced Edition | Resolution: " + MENU_WIDTH + "x" + MENU_HEIGHT + " (Fixed & Visible!)";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(versionText);
        
        g2d.drawString(versionText, MENU_WIDTH - textWidth - 30, MENU_HEIGHT - 30);
    }
    
    /**
     * Creates and shows the main menu window
     */
    public static void showMainMenu() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Warning: Could not set system look and feel");
            }
            
            JFrame frame = new JFrame("Tower Bloxx - Main Menu");
            MainMenu menu = new MainMenu(frame);
            
            frame.add(menu);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            System.out.println("Main menu displayed successfully!");
        });
    }
}

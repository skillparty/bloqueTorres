package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.ui.components.ASCIILogo;
import com.skillparty.towerblox.ui.components.FontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Professional animated main menu panel with modern UI design
 */
public class MenuPanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    
    private GameWindow parentWindow;
    private FontManager fontManager;
    private ASCIILogo asciiLogo;
    
    // Animation and effects
    private Timer animationTimer;
    private List<Particle> particles;
    private Random random;
    private long startTime;
    private float logoGlow = 0.0f;
    private boolean glowIncreasing = true;
    
    // UI Components
    private List<MenuButton> menuButtons;
    private int selectedButton = 1; // 0=Easy, 1=Normal, 2=Hard, 3=HighScores, 4=Exit
    private boolean keyboardMode = false;
    
    // Colors and styling
    private static final Color BACKGROUND_START = new Color(10, 15, 35);
    private static final Color BACKGROUND_END = new Color(25, 35, 60);
    private static final Color ACCENT_COLOR = new Color(0, 255, 150);
    private static final Color SECONDARY_COLOR = new Color(100, 200, 255);
    private static final Color GLASS_COLOR = new Color(255, 255, 255, 30);
    
    // Particle system for background animation
    private static class Particle {
        float x, y, vx, vy, size, alpha, life;
        Color color;
        
        Particle(float x, float y) {
            this.x = x;
            this.y = y;
            this.vx = (float)(Math.random() - 0.5) * 2;
            this.vy = (float)(Math.random() - 0.5) * 2;
            this.size = (float)Math.random() * 3 + 1;
            this.alpha = (float)Math.random() * 0.5f + 0.2f;
            this.life = 1.0f;
            this.color = Math.random() > 0.5 ? ACCENT_COLOR : SECONDARY_COLOR;
        }
        
        void update() {
            x += vx;
            y += vy;
            life -= 0.003f;
            alpha = life * 0.7f;
            
            // Add some gravitational pull towards center
            float centerX = 400;
            float centerY = 300;
            float dx = centerX - x;
            float dy = centerY - y;
            float distance = (float)Math.sqrt(dx*dx + dy*dy);
            
            if (distance > 0) {
                vx += (dx / distance) * 0.01f;
                vy += (dy / distance) * 0.01f;
            }
            
            // Limit velocity
            float maxVel = 3.0f;
            if (Math.abs(vx) > maxVel) vx = vx > 0 ? maxVel : -maxVel;
            if (Math.abs(vy) > maxVel) vy = vy > 0 ? maxVel : -maxVel;
            
            if (life <= 0 || x < -50 || x > 850 || y < -50 || y > 650) {
                life = 1.0f;
                x = (float)(Math.random() * 800);
                y = (float)(Math.random() * 600);
                vx = (float)(Math.random() - 0.5) * 2;
                vy = (float)(Math.random() - 0.5) * 2;
                size = (float)Math.random() * 4 + 1;
                color = Math.random() > 0.5 ? ACCENT_COLOR : SECONDARY_COLOR;
            }
        }
        
        void render(Graphics2D g2d) {
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 
                                 Math.max(0, Math.min(255, (int)(alpha * 255)))));
            g2d.fillOval((int)x, (int)y, (int)size, (int)size);
        }
    }
    
    // Custom menu button class
    private static class MenuButton {
        String text;
        String description;
        Rectangle bounds;
        DifficultyLevel difficulty;
        boolean isSpecial; // for high scores and exit
        float hoverAnimation = 0.0f;
        Color baseColor;
        Color hoverColor;
        
        MenuButton(String text, String description, DifficultyLevel difficulty, Color baseColor) {
            this.text = text;
            this.description = description;
            this.difficulty = difficulty;
            this.isSpecial = false;
            this.baseColor = baseColor;
            this.hoverColor = baseColor.brighter();
        }
        
        MenuButton(String text, String description, Color baseColor) {
            this.text = text;
            this.description = description;
            this.isSpecial = true;
            this.baseColor = baseColor;
            this.hoverColor = baseColor.brighter();
        }
        
        void updateHover(boolean isHovered, boolean isSelected) {
            float target = (isHovered || isSelected) ? 1.0f : 0.0f;
            hoverAnimation += (target - hoverAnimation) * 0.15f;
        }
        
        void render(Graphics2D g2d, FontManager fontManager, boolean isSelected) {
            // Create glass effect background
            Color bgColor = new Color(
                (int)(baseColor.getRed() * (0.3f + hoverAnimation * 0.7f)),
                (int)(baseColor.getGreen() * (0.3f + hoverAnimation * 0.7f)),
                (int)(baseColor.getBlue() * (0.3f + hoverAnimation * 0.7f)),
                (int)(100 + hoverAnimation * 100)
            );
            
            // Draw background with rounded corners
            g2d.setColor(bgColor);
            g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);
            
            // Draw glass overlay
            g2d.setColor(new Color(255, 255, 255, (int)(30 + hoverAnimation * 50)));
            g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);
            
            // Draw border
            if (isSelected) {
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(ACCENT_COLOR);
            } else {
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(new Color(255, 255, 255, (int)(100 + hoverAnimation * 100)));
            }
            g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);
            
            // Draw text
            g2d.setFont(fontManager.getFont(FontManager.MENU_SIZE, Font.BOLD));
            FontMetrics fm = g2d.getFontMetrics();
            
            // Main text
            g2d.setColor(Color.WHITE);
            int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
            int textY = bounds.y + bounds.height / 2 - 5;
            g2d.drawString(text, textX, textY);
            
            // Description text
            g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE));
            fm = g2d.getFontMetrics();
            g2d.setColor(new Color(200, 200, 200));
            int descX = bounds.x + (bounds.width - fm.stringWidth(description)) / 2;
            int descY = textY + 20;
            g2d.drawString(description, descX, descY);
        }
    }
    
    public MenuPanel(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        this.fontManager = FontManager.getInstance();
        this.asciiLogo = new ASCIILogo();
        this.random = new Random();
        this.particles = new ArrayList<>();
        this.menuButtons = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
        
        initializePanel();
        createParticles();
        createMenuButtons();
        setupEventHandlers();
        startAnimations();
        
        // Make panel focusable for keyboard input
        setFocusable(true);
        addKeyListener(this);
    }
    
    /**
     * Initializes the panel properties
     */
    private void initializePanel() {
        setBackground(BACKGROUND_START);
        setPreferredSize(new Dimension(800, 600));
        setDoubleBuffered(true);
    }
    
    /**
     * Creates the particle system for background animation
     */
    private void createParticles() {
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle(
                (float)(Math.random() * 800),
                (float)(Math.random() * 600)
            ));
        }
    }
    
    /**
     * Creates all menu buttons with modern styling
     */
    private void createMenuButtons() {
        // Difficulty buttons
        menuButtons.add(new MenuButton("FÁCIL", "Velocidad reducida", 
            DifficultyLevel.EASY, new Color(46, 204, 113))); // Emerald
        menuButtons.add(new MenuButton("NORMAL", "Velocidad estándar", 
            DifficultyLevel.NORMAL, new Color(52, 152, 219))); // Blue
        menuButtons.add(new MenuButton("DIFÍCIL", "Velocidad alta", 
            DifficultyLevel.HARD, new Color(231, 76, 60))); // Red
        
        // Special buttons
        menuButtons.add(new MenuButton("PUNTUACIONES", "Ver mejores scores", 
            new Color(155, 89, 182))); // Purple
        menuButtons.add(new MenuButton("SALIR", "Cerrar el juego", 
            new Color(149, 165, 166))); // Gray
        
        // Set button positions
        updateButtonPositions();
    }
    
    /**
     * Updates button positions based on panel size
     */
    private void updateButtonPositions() {
        int buttonWidth = 200;
        int buttonHeight = 80;
        int spacing = 20;
        int startY = 300;
        
        // Difficulty buttons in a row
        for (int i = 0; i < 3; i++) {
            int x = (800 - (3 * buttonWidth + 2 * spacing)) / 2 + i * (buttonWidth + spacing);
            menuButtons.get(i).bounds = new Rectangle(x, startY, buttonWidth, buttonHeight);
        }
        
        // Special buttons below
        int specialButtonWidth = 180;
        int specialStartX = (800 - (2 * specialButtonWidth + spacing)) / 2;
        menuButtons.get(3).bounds = new Rectangle(specialStartX, startY + buttonHeight + 30, 
                                                specialButtonWidth, 60);
        menuButtons.get(4).bounds = new Rectangle(specialStartX + specialButtonWidth + spacing, 
                                                startY + buttonHeight + 30, specialButtonWidth, 60);
    }
    
    /**
     * Starts all animations
     */
    private void startAnimations() {
        animationTimer = new Timer(16, e -> {
            // Update particles
            for (Particle particle : particles) {
                particle.update();
            }
            
            // Update logo glow
            if (glowIncreasing) {
                logoGlow += 0.02f;
                if (logoGlow >= 1.0f) {
                    logoGlow = 1.0f;
                    glowIncreasing = false;
                }
            } else {
                logoGlow -= 0.02f;
                if (logoGlow <= 0.0f) {
                    logoGlow = 0.0f;
                    glowIncreasing = true;
                }
            }
            
            // Update button hover animations
            Point mousePos = getMousePosition();
            for (int i = 0; i < menuButtons.size(); i++) {
                MenuButton button = menuButtons.get(i);
                boolean isHovered = mousePos != null && button.bounds.contains(mousePos);
                boolean isSelected = (i == selectedButton && keyboardMode);
                button.updateHover(isHovered, isSelected);
            }
            
            repaint();
        });
        animationTimer.start();
    }
    

    
    /**
     * Sets up event handlers for mouse interaction
     */
    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < menuButtons.size(); i++) {
                    if (menuButtons.get(i).bounds.contains(e.getPoint())) {
                        activateButton(i);
                        break;
                    }
                }
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                keyboardMode = false;
                repaint();
            }
        });
    }
    
    /**
     * Activates a button by index with visual feedback
     */
    private void activateButton(int index) {
        if (index < 0 || index >= menuButtons.size()) return;
        
        MenuButton button = menuButtons.get(index);
        
        // Visual feedback - flash the button
        Timer flashTimer = new Timer(50, null);
        final int[] flashCount = {0};
        flashTimer.addActionListener(e -> {
            button.hoverAnimation = (flashCount[0] % 2 == 0) ? 1.0f : 0.5f;
            flashCount[0]++;
            repaint();
            
            if (flashCount[0] >= 6) {
                flashTimer.stop();
                
                // Execute action after flash
                if (index < 3) {
                    // Difficulty buttons
                    startGame(button.difficulty);
                } else if (index == 3) {
                    // High scores
                    parentWindow.showHighScores();
                } else if (index == 4) {
                    // Exit
                    parentWindow.exitGame();
                }
            }
        });
        flashTimer.start();
    }
    
    /**
     * Starts a new game with the specified difficulty
     */
    private void startGame(DifficultyLevel difficulty) {
        System.out.println("Starting game with difficulty: " + difficulty.getDisplayName());
        if (animationTimer != null) {
            animationTimer.stop();
        }
        parentWindow.startNewGame(difficulty);
    }
    
    // KeyListener implementation
    @Override
    public void keyPressed(KeyEvent e) {
        keyboardMode = true;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (selectedButton > 0) {
                    selectedButton--;
                }
                break;
                
            case KeyEvent.VK_RIGHT:
                if (selectedButton < menuButtons.size() - 1) {
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
                    selectedButton = Math.min(menuButtons.size() - 1, selectedButton + 3);
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
                
            case KeyEvent.VK_H:
                parentWindow.showHighScores();
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
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw animated gradient background
        drawAnimatedBackground(g2d);
        
        // Draw particles
        for (Particle particle : particles) {
            particle.render(g2d);
        }
        
        // Draw logo with glow effect
        drawLogoWithGlow(g2d);
        
        // Draw title
        drawTitle(g2d);
        
        // Draw menu buttons
        for (int i = 0; i < menuButtons.size(); i++) {
            menuButtons.get(i).render(g2d, fontManager, i == selectedButton && keyboardMode);
        }
        
        // Draw instructions
        drawInstructions(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Draws animated gradient background
     */
    private void drawAnimatedBackground(Graphics2D g2d) {
        long time = System.currentTimeMillis() - startTime;
        float wave = (float)Math.sin(time * 0.001) * 0.3f + 0.7f;
        
        Color color1 = new Color(
            (int)(BACKGROUND_START.getRed() * wave),
            (int)(BACKGROUND_START.getGreen() * wave),
            (int)(BACKGROUND_START.getBlue() * wave)
        );
        Color color2 = new Color(
            (int)(BACKGROUND_END.getRed() * wave),
            (int)(BACKGROUND_END.getGreen() * wave),
            (int)(BACKGROUND_END.getBlue() * wave)
        );
        
        GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Add subtle pattern overlay
        g2d.setColor(new Color(255, 255, 255, 5));
        for (int x = 0; x < getWidth(); x += 100) {
            for (int y = 0; y < getHeight(); y += 100) {
                g2d.drawLine(x, y, x + 50, y + 50);
            }
        }
    }
    
    /**
     * Draws the logo with animated glow effect
     */
    private void drawLogoWithGlow(Graphics2D g2d) {
        // Create glow effect
        int glowSize = (int)(20 * logoGlow);
        Color glowColor = new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), 
                                  ACCENT_COLOR.getBlue(), (int)(50 * logoGlow));
        
        // Draw multiple glow layers
        for (int i = glowSize; i > 0; i--) {
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), 
                                 glowColor.getBlue(), glowColor.getAlpha() / i));
            g2d.fillRoundRect(getWidth()/2 - asciiLogo.getWidth()/2 - i, 
                            50 - i, asciiLogo.getWidth() + 2*i, 
                            asciiLogo.getHeight() + 2*i, 20, 20);
        }
        
        // Draw logo
        asciiLogo.render(g2d, getWidth()/2 - asciiLogo.getWidth()/2, 80);
    }
    
    /**
     * Draws the main title
     */
    private void drawTitle(Graphics2D g2d) {
        String title = "SELECCIONA LA DIFICULTAD";
        g2d.setFont(fontManager.getFont(FontManager.MENU_SIZE, Font.BOLD));
        FontMetrics fm = g2d.getFontMetrics();
        
        // Draw title with shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(title, (getWidth() - fm.stringWidth(title)) / 2 + 2, 252);
        
        g2d.setColor(ACCENT_COLOR);
        g2d.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 250);
    }
    
    /**
     * Draws instruction text
     */
    private void drawInstructions(Graphics2D g2d) {
        String instructions = "Usa las teclas de flecha y ENTER para navegar | ESC para salir | 1-3 para dificultad";
        g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE));
        FontMetrics fm = g2d.getFontMetrics();
        
        g2d.setColor(new Color(200, 200, 200, 150));
        g2d.drawString(instructions, (getWidth() - fm.stringWidth(instructions)) / 2, 
                      getHeight() - 20);
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
        requestFocusInWindow();
    }
    
    /**
     * Cleanup method to stop animations when panel is no longer visible
     */
    public void cleanup() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}
package com.skillparty.towerblox.integration;

import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.game.EnhancedGameEngine;
import com.skillparty.towerblox.core.GameLoop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * Main integration class that seamlessly connects all 2025 improvements
 * with the existing game engine for a professional gaming experience
 * 
 * @author joseAlejandro - Tower Bloxx 2025 Professional Edition
 */
public class GameIntegration2025 extends JPanel implements KeyListener {
    
    // Core components
    private GameEngine originalEngine;
    private EnhancedGameEngine enhancedEngine;
    private GameLoop gameLoop;
    
    // Game state
    private boolean initialized = false;
    private BufferedImage backBuffer;
    private Graphics2D backGraphics;
    
    // Window settings
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final String GAME_TITLE = "Tower Bloxx 2025 - Professional Edition";
    
    public GameIntegration2025() {
        initializeIntegration();
        setupGameWindow();
    }
    
    private void initializeIntegration() {
        System.out.println("🚀 ==========================================");
        System.out.println("🚀 TOWER BLOXX 2025 PROFESSIONAL EDITION");
        System.out.println("🚀 ==========================================");
        System.out.println("🎮 Starting advanced game integration...");
        
        try {
            // Initialize original game engine
            originalEngine = new GameEngine();
            System.out.println("✅ Original game engine initialized");
            
            // Initialize enhanced wrapper
            enhancedEngine = new EnhancedGameEngine(originalEngine);
            System.out.println("✅ Enhanced engine wrapper created");
            
            // Setup double buffering
            setupDoubleBuffering();
            System.out.println("✅ Double buffering enabled");
            
            // Initialize game loop with callbacks
            setupGameLoopCallbacks();
            System.out.println("✅ Professional game loop configured");
            
            initialized = true;
            System.out.println("🎉 Game integration completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error during game initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupGameWindow() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);
        addKeyListener(enhancedEngine); // Add enhanced input handling
    }
    
    private void setupDoubleBuffering() {
        backBuffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        backGraphics = backBuffer.createGraphics();
        
        // Enable high-quality rendering
        backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        backGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        backGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        backGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
    
    private void setupGameLoopCallbacks() {
        // Create update callback
        GameLoop.UpdateCallback updateCallback = (deltaSeconds) -> {
            if (initialized && enhancedEngine != null) {
                enhancedEngine.update();
            }
        };
        
        // Create render callback
        GameLoop.RenderCallback renderCallback = (interpolation) -> {
            if (initialized && backGraphics != null) {
                // Clear back buffer
                backGraphics.setColor(new Color(135, 206, 250)); // Sky blue
                backGraphics.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
                
                // Render through enhanced engine
                enhancedEngine.render(backGraphics);
                
                // Repaint to screen
                repaint();
            }
        };
        
        // Initialize game loop with callbacks
        gameLoop = new GameLoop(updateCallback, renderCallback);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backBuffer != null) {
            // Draw the back buffer to screen
            g.drawImage(backBuffer, 0, 0, null);
            
            // Add 2025 branding
            renderProfessionalBranding(g);
        }
    }
    
    private void renderProfessionalBranding(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Professional branding in corner
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Tower Bloxx 2025 Pro", WINDOW_WIDTH - 150, WINDOW_HEIGHT - 10);
        
        g2d.dispose();
    }
    
    public void startGame() {
        if (!initialized) {
            System.err.println("❌ Cannot start game - initialization failed!");
            return;
        }
        
        System.out.println("🎮 Starting Tower Bloxx 2025 Professional Edition...");
        
        // Start the enhanced game loop
        gameLoop.start();
        
        System.out.println("✅ Game started successfully!");
        System.out.println("📋 Controls:");
        System.out.println("   SPACE: Drop Block");
        System.out.println("   ESC: Pause/Resume");
        System.out.println("   F1: Performance Overlay");
        System.out.println("   F2: Reset Camera");
        System.out.println("   F3: Toggle Advanced Physics");
        System.out.println("   F4: Add Visual Effects");
        System.out.println("   F5: Print Stats");
        System.out.println("   F6: Camera Shake Test");
        System.out.println("🎯 Ready to play!");
    }
    
    public void stopGame() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.stop();
            System.out.println("🛑 Game stopped");
        }
    }
    
    // =================== INPUT HANDLING ===================
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (enhancedEngine != null) {
            enhancedEngine.keyPressed(e);
        }
        
        // Handle integration-specific keys
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F12:
                printSystemStatus();
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (enhancedEngine != null) {
            enhancedEngine.keyReleased(e);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        if (enhancedEngine != null) {
            enhancedEngine.keyTyped(e);
        }
    }
    
    private void printSystemStatus() {
        System.out.println("\n🔧 ===== SYSTEM STATUS =====");
        System.out.println("Game Loop Running: " + (gameLoop != null && gameLoop.isRunning()));
        System.out.println("Enhanced Engine: " + (enhancedEngine != null ? "ACTIVE" : "INACTIVE"));
        System.out.println("Original Engine: " + (originalEngine != null ? "ACTIVE" : "INACTIVE"));
        System.out.println("Initialization: " + (initialized ? "COMPLETE" : "INCOMPLETE"));
        System.out.println("Back Buffer: " + (backBuffer != null ? "READY" : "NULL"));
        
        if (enhancedEngine != null) {
            System.out.println("Performance Monitor: ACTIVE");
            System.out.println("Camera System: ACTIVE");
            System.out.println("Block Pool: ACTIVE");
        }
        System.out.println("==============================\n");
    }
    
    // =================== MAIN ENTRY POINT ===================
    
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true"); // Enable OpenGL acceleration
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Create main window
                JFrame frame = new JFrame(GAME_TITLE);
                GameIntegration2025 gameIntegration = new GameIntegration2025();
                
                // Setup window
                frame.add(gameIntegration);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.pack();
                frame.setLocationRelativeTo(null);
                
                // Add window listener for cleanup
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        gameIntegration.stopGame();
                        System.out.println("👋 Thank you for playing Tower Bloxx 2025!");
                        System.exit(0);
                    }
                });
                
                // Show window and start game
                frame.setVisible(true);
                gameIntegration.requestFocusInWindow();
                
                // Start the game with a small delay for proper initialization
                Timer timer = new Timer(500, e -> {
                    gameIntegration.startGame();
                    ((Timer) e.getSource()).stop();
                });
                timer.setRepeats(false);
                timer.start();
                
            } catch (Exception e) {
                System.err.println("❌ Failed to launch Tower Bloxx 2025: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
    
    // =================== UTILITY METHODS ===================
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public EnhancedGameEngine getEnhancedEngine() {
        return enhancedEngine;
    }
    
    public GameEngine getOriginalEngine() {
        return originalEngine;
    }
    
    public GameLoop getGameLoop() {
        return gameLoop;
    }
}

package com.skillparty.towerblox;

import com.skillparty.towerblox.core.TowerBloxxGame;
import com.skillparty.towerblox.ui.components.ASCIILogo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Simple integration launcher for javaBloxx 2.0
 * Launches the refactored game with menu options
 * 
 * @author joseAlejandro
 * @version 2.0
 */
public class MainSimpleIntegration {
    
    private static JFrame mainFrame;
    private static TowerBloxxGame gamePanel;
    private static JLabel statusLabel;
    private static JLabel scoreLabel;
    
    public static void main(String[] args) {
        // Show ASCII intro
        showASCIIIntro();
        
        // Launch game in EDT
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    /**
     * Shows ASCII intro
     */
    private static void showASCIIIntro() {
        clearConsole();
        
        System.out.println("\n");
        ASCIILogo.displayLogo();
        System.out.println("\n");
        
        System.out.println("🎮 javaBloxx 2.0 - Professional Refactor");
        System.out.println("📊 Resolution: 1280x720");
        System.out.println("🚀 Professional Game Engine");
        System.out.println("✨ Enhanced Animations");
        System.out.println("⚡ 60 FPS Target");
        System.out.println();
        
        // Show loading progress
        System.out.print("Loading");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(" Ready!");
        System.out.println();
    }
    
    /**
     * Creates and shows the GUI
     */
    private static void createAndShowGUI() {
        // Create main frame
        mainFrame = new JFrame("javaBloxx 2.0 - Professional Edition");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 20, 25));
        
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        mainFrame.setJMenuBar(menuBar);
        
        // Create game panel
        gamePanel = new TowerBloxxGame();
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        
        // Create status panel
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Create side panel with info
        JPanel sidePanel = createSidePanel();
        mainPanel.add(sidePanel, BorderLayout.EAST);
        
        // Setup frame
        mainFrame.setContentPane(mainPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        
        // Add global key listener
        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleGlobalKeys(e);
            }
        });
        
        // Transfer focus to game
        gamePanel.requestFocusInWindow();
        
        // Show frame
        mainFrame.setVisible(true);
        
        System.out.println("✅ javaBloxx 2.0 Integrated - Professional Edition Running");
    }
    
    /**
     * Creates menu bar
     */
    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 30, 35));
        
        // Game menu
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setForeground(Color.WHITE);
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newGameItem.addActionListener(e -> restartGame());
        
        JMenuItem pauseItem = new JMenuItem("Pause/Resume");
        pauseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
        pauseItem.addActionListener(e -> pauseGame());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGameItem);
        gameMenu.add(pauseItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(Color.WHITE);
        
        JMenuItem debugItem = new JMenuItem("Toggle Debug");
        debugItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0));
        debugItem.addActionListener(e -> toggleDebug());
        
        JMenuItem fullscreenItem = new JMenuItem("Fullscreen");
        fullscreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        fullscreenItem.addActionListener(e -> toggleFullscreen());
        
        viewMenu.add(debugItem);
        viewMenu.add(fullscreenItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        
        JMenuItem controlsItem = new JMenuItem("Controls");
        controlsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        controlsItem.addActionListener(e -> showControls());
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        
        helpMenu.add(controlsItem);
        helpMenu.add(aboutItem);
        
        menuBar.add(gameMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Creates status panel
     */
    private static JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(30, 30, 35));
        panel.setPreferredSize(new Dimension(1280, 30));
        
        // Status label
        statusLabel = new JLabel("Press SPACE to drop block");
        statusLabel.setForeground(new Color(100, 200, 255));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Score label
        scoreLabel = new JLabel("Score: 0 | Height: 0");
        scoreLabel.setForeground(new Color(255, 200, 100));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        panel.add(Box.createHorizontalStrut(10));
        panel.add(statusLabel);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(scoreLabel);
        
        return panel;
    }
    
    /**
     * Creates side panel
     */
    private static JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(25, 25, 30));
        panel.setPreferredSize(new Dimension(200, 720));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(50, 50, 55)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("javaBloxx 2.0");
        titleLabel.setForeground(new Color(100, 200, 255));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Separator
        JSeparator sep1 = new JSeparator();
        sep1.setMaximumSize(new Dimension(180, 2));
        
        // Controls info
        JLabel controlsTitle = new JLabel("Controls:");
        controlsTitle.setForeground(Color.WHITE);
        controlsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        controlsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] controls = {
            "SPACE - Drop Block",
            "P - Pause/Resume",
            "D - Debug Info",
            "R - Restart"
        };
        
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(new Color(25, 25, 30));
        controlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        for (String control : controls) {
            JLabel label = new JLabel(control);
            label.setForeground(new Color(180, 180, 185));
            label.setFont(new Font("Monospaced", Font.PLAIN, 11));
            controlsPanel.add(label);
            controlsPanel.add(Box.createVerticalStrut(3));
        }
        
        // Separator
        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(180, 2));
        
        // Stats
        JLabel statsTitle = new JLabel("Statistics:");
        statsTitle.setForeground(Color.WHITE);
        statsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        statsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(25, 25, 30));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Developer credit
        JLabel devLabel = new JLabel("by joseAlejandro");
        devLabel.setForeground(new Color(100, 150, 200));
        devLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        devLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sep1);
        panel.add(Box.createVerticalStrut(10));
        panel.add(controlsTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(controlsPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sep2);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statsTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(statsPanel);
        panel.add(Box.createVerticalGlue());
        panel.add(devLabel);
        
        return panel;
    }
    
    /**
     * Handles global key events
     */
    private static void handleGlobalKeys(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F11:
                toggleFullscreen();
                break;
            case KeyEvent.VK_ESCAPE:
                if (isFullscreen()) {
                    toggleFullscreen();
                }
                break;
        }
    }
    
    /**
     * Restarts the game
     */
    private static void restartGame() {
        // Send R key to game
        if (gamePanel != null) {
            gamePanel.dispatchEvent(new KeyEvent(
                gamePanel, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0,
                KeyEvent.VK_R, 'R'
            ));
        }
        updateStatus("New game started");
    }
    
    /**
     * Pauses the game
     */
    private static void pauseGame() {
        // Send P key to game
        if (gamePanel != null) {
            gamePanel.dispatchEvent(new KeyEvent(
                gamePanel, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0,
                KeyEvent.VK_P, 'P'
            ));
        }
    }
    
    /**
     * Toggles debug mode
     */
    private static void toggleDebug() {
        // Send D key to game
        if (gamePanel != null) {
            gamePanel.dispatchEvent(new KeyEvent(
                gamePanel, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0,
                KeyEvent.VK_D, 'D'
            ));
        }
    }
    
    /**
     * Toggles fullscreen
     */
    private static void toggleFullscreen() {
        GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
            
        if (isFullscreen()) {
            device.setFullScreenWindow(null);
            mainFrame.setVisible(true);
        } else {
            device.setFullScreenWindow(mainFrame);
        }
    }
    
    /**
     * Checks if fullscreen
     */
    private static boolean isFullscreen() {
        GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
        return device.getFullScreenWindow() == mainFrame;
    }
    
    /**
     * Updates status
     */
    private static void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Shows controls dialog
     */
    private static void showControls() {
        String controls = """
            javaBloxx 2.0 Controls:
            
            Game Controls:
            SPACE    - Drop block
            P        - Pause/Resume game
            D        - Toggle debug information
            R        - Restart game
            
            Menu Controls:
            Ctrl+N   - New game
            Ctrl+Q   - Exit game
            F1       - Show this help
            F11      - Toggle fullscreen
            ESC      - Exit fullscreen
            
            Tips:
            • Align blocks carefully for bonus points
            • Perfect drops give 2x score
            • Tower stability affects game difficulty
            • Watch the crane movement pattern
            """;
        
        JOptionPane.showMessageDialog(mainFrame, controls, "Game Controls", 
                                      JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows about dialog
     */
    private static void showAbout() {
        String about = """
            javaBloxx 2.0
            Professional Tower Building Experience
            
            Developer: joseAlejandro
            Version: 2.0 Professional Refactor
            
            Core Features:
            • Professional 60 FPS game loop
            • Advanced physics engine
            • Scalable rendering system
            • Realistic crane animations
            • Tower stability calculations
            • Support for 9999+ block towers
            
            Technical Stack:
            • Java Swing for UI
            • Custom physics engine
            • Modular architecture
            • Performance optimized
            
            © 2025 joseAlejandro
            All rights reserved
            """;
        
        JOptionPane.showMessageDialog(mainFrame, about, "About javaBloxx", 
                                      JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Clears console
     */
    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing fails, just add some blank lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}

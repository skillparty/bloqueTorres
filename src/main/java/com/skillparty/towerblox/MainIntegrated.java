package com.skillparty.towerblox;

import com.skillparty.towerblox.integration.RefactoredGameAdapter;
import com.skillparty.towerblox.ui.GameWindow;
import com.skillparty.towerblox.ui.components.ASCIILogo;
import com.skillparty.towerblox.game.DifficultyLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Integrated main launcher for javaBloxx
 * Supports both refactored and legacy versions
 * 
 * @author joseAlejandro
 * @version 2.0
 */
public class MainIntegrated {
    
    private static boolean USE_REFACTORED = true;
    private static JFrame mainFrame;
    private static RefactoredGameAdapter gameAdapter;
    
    public static void main(String[] args) {
        // Check command line arguments
        for (String arg : args) {
            if ("--legacy".equals(arg)) {
                USE_REFACTORED = false;
            } else if ("--refactored".equals(arg)) {
                USE_REFACTORED = true;
            } else if ("--help".equals(arg)) {
                showHelp();
                return;
            }
        }
        
        // Set system property for adapter
        System.setProperty("use.refactored", String.valueOf(USE_REFACTORED));
        
        // Show ASCII intro
        showASCIIIntro();
        
        // Launch game in EDT
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    /**
     * Shows help information
     */
    private static void showHelp() {
        System.out.println("javaBloxx 2.0 - Professional Tower Building Experience");
        System.out.println("by joseAlejandro");
        System.out.println();
        System.out.println("Usage: java MainIntegrated [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --refactored  Use the refactored 2.0 version (default)");
        System.out.println("  --legacy      Use the legacy version");
        System.out.println("  --help        Show this help message");
        System.out.println();
        System.out.println("In-game keys:");
        System.out.println("  SPACE    Drop block");
        System.out.println("  P        Pause/Resume");
        System.out.println("  D        Toggle debug info");
        System.out.println("  R        Restart (after game over)");
        System.out.println("  V        Toggle version (refactored/legacy)");
        System.out.println("  ESC      Return to menu");
    }
    
    /**
     * Shows ASCII intro
     */
    private static void showASCIIIntro() {
        clearConsole();
        
        System.out.println("\n");
        new ASCIILogo().displayLogo();
        System.out.println("\n");
        
        String version = USE_REFACTORED ? "REFACTORED 2.0" : "LEGACY";
        System.out.println("🎮 javaBloxx - " + version + " Version");
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
        mainFrame = new JFrame("javaBloxx 2.0 - Professional Edition");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        
        // Create main panel with menu bar
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        mainFrame.setJMenuBar(menuBar);
        
        // Create game adapter
        if (USE_REFACTORED) {
            // Use adapter with refactored game
            GameWindow dummyWindow = new GameWindow();
            gameAdapter = new RefactoredGameAdapter(dummyWindow);
            mainPanel.add(gameAdapter, BorderLayout.CENTER);
            
            // Create control panel
            JPanel controlPanel = createControlPanel();
            mainPanel.add(controlPanel, BorderLayout.SOUTH);
            
        } else {
            // Use legacy GameWindow
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);
            gameWindow.startGameDirectly();
            return; // Legacy handles its own window
        }
        
        // Setup frame
        mainFrame.setContentPane(mainPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        
        // Add key listener for version toggle
        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_V) {
                    toggleVersion();
                }
            }
        });
        
        // Show frame
        mainFrame.setVisible(true);
        
        // Start game
        if (gameAdapter != null) {
            gameAdapter.startNewGame(DifficultyLevel.NORMAL);
        }
        
        System.out.println("✅ javaBloxx 2.0 Integrated - " + 
                          (USE_REFACTORED ? "Refactored" : "Legacy") + " Version Running");
    }
    
    /**
     * Creates menu bar
     */
    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Game menu
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newGameItem.addActionListener(e -> startNewGame());
        
        JMenuItem pauseItem = new JMenuItem("Pause/Resume");
        pauseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
        pauseItem.addActionListener(e -> togglePause());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGameItem);
        gameMenu.add(pauseItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        // Options menu
        JMenu optionsMenu = new JMenu("Options");
        
        JMenuItem toggleVersionItem = new JMenuItem("Toggle Version");
        toggleVersionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0));
        toggleVersionItem.addActionListener(e -> toggleVersion());
        
        JMenuItem debugItem = new JMenuItem("Toggle Debug");
        debugItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0));
        
        optionsMenu.add(toggleVersionItem);
        optionsMenu.add(debugItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem controlsItem = new JMenuItem("Controls");
        controlsItem.addActionListener(e -> showControls());
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        
        helpMenu.add(controlsItem);
        helpMenu.add(aboutItem);
        
        menuBar.add(gameMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Creates control panel
     */
    private static JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(30, 30, 35));
        
        // Version label
        JLabel versionLabel = new JLabel("Version: " + (USE_REFACTORED ? "Refactored 2.0" : "Legacy"));
        versionLabel.setForeground(Color.WHITE);
        
        // Status label
        JLabel statusLabel = new JLabel("Press SPACE to drop block");
        statusLabel.setForeground(new Color(100, 200, 255));
        
        panel.add(versionLabel);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(statusLabel);
        
        return panel;
    }
    
    /**
     * Starts a new game
     */
    private static void startNewGame() {
        if (gameAdapter != null) {
            gameAdapter.startNewGame(DifficultyLevel.NORMAL);
        }
    }
    
    /**
     * Toggles pause
     */
    private static void togglePause() {
        if (gameAdapter != null) {
            gameAdapter.pauseGame();
        }
    }
    
    /**
     * Toggles between versions
     */
    private static void toggleVersion() {
        if (gameAdapter != null) {
            gameAdapter.toggleGameVersion();
            USE_REFACTORED = gameAdapter.isUsingRefactored();
            
            // Update UI
            if (mainFrame != null) {
                mainFrame.setTitle("javaBloxx 2.0 - " + 
                                  (USE_REFACTORED ? "Refactored Edition" : "Legacy Edition"));
            }
            
            System.out.println("Switched to " + (USE_REFACTORED ? "Refactored 2.0" : "Legacy") + " version");
        }
    }
    
    /**
     * Shows controls dialog
     */
    private static void showControls() {
        String controls = """
            javaBloxx Controls:
            
            SPACE    - Drop block
            P        - Pause/Resume game
            D        - Toggle debug information
            R        - Restart (after game over)
            V        - Toggle version (Refactored/Legacy)
            ESC      - Return to menu
            
            Ctrl+N   - New game
            Ctrl+Q   - Exit game
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
            
            Features:
            • Professional 60 FPS game loop
            • Advanced physics engine
            • Scalable rendering system
            • Realistic crane animations
            • Tower stability calculations
            • Support for 9999+ block towers
            
            © 2025 joseAlejandro
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

package com.skillparty.towerblox;

import com.skillparty.towerblox.core.TowerBloxxGame;
import com.skillparty.towerblox.ui.GameWindow;
import com.skillparty.towerblox.ui.MenuPanel;
import com.skillparty.towerblox.game.DifficultyLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Professional Mode Launcher - Includes your advanced CraneSystem
 * 
 * @author joseAlejandro
 * @version 2.0 Professional
 */
public class ProfessionalModeLauncher {
    
    private static JFrame mainFrame;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    
    public static void main(String[] args) {
        showASCIIIntro();
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
    
    private static void showASCIIIntro() {
        System.out.println();
        // Simple ASCII logo
        System.out.println("     ██╗ █████╗ ██╗   ██╗ █████╗ ████████╗ ██████╗ ██╗    ██╗███████╗██████╗ ");
        System.out.println("     ██║██╔══██╗██║   ██║██╔══██╗╚══██╔══╝██╔═══██╗██║    ██║██╔════╝██╔══██╗");
        System.out.println("     ██║███████║██║   ██║███████║   ██║   ██║   ██║██║ █╗ ██║█████╗  ██████╔╝");
        System.out.println("██   ██║██╔══██║╚██╗ ██╔╝██╔══██║   ██║   ██║   ██║██║███╗██║██╔══╝  ██╔══██╗");
        System.out.println("╚█████╔╝██║  ██║ ╚████╔╝ ██║  ██║   ██║   ╚██████╔╝╚███╔███╔╝███████╗██║  ██║");
        System.out.println(" ╚════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝   ╚═╝    ╚═════╝  ╚══╝╚══╝ ╚══════╝╚═╝  ╚═╝");
        System.out.println();
        System.out.println("🎮 javaBloxx 2.0 - Professional Mode");
        System.out.println("🏆 With Advanced CraneSystem Physics!");
        System.out.println("🚀 Launching...");
        System.out.println();
    }
    
    private static void createAndShowGUI() {
        try {
            // Set system look and feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("System".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default look and feel
        }
        
        // Create main frame
        mainFrame = new JFrame("javaBloxx 2.0 - Professional Mode");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setResizable(false);
        
        // Create card layout for switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create menu
        GameWindow gameWindow = createGameWindow();
        MenuPanel menuPanel = new MenuPanel(gameWindow);
        mainPanel.add(menuPanel, "MENU");
        
        // Start with menu
        cardLayout.show(mainPanel, "MENU");
        
        // Setup frame
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("\n🎮 Thanks for playing javaBloxx 2.0 Professional!");
                System.exit(0);
            }
        });
        
        mainFrame.setVisible(true);
        
        System.out.println("✅ Professional Mode launched!");
        System.out.println("🏗️ Advanced CraneSystem available in Professional difficulty!");
    }
    
    private static GameWindow createGameWindow() {
        return new GameWindow() {
            @Override
            public void returnToMenu() {
                cardLayout.show(mainPanel, "MENU");
            }
            
            @Override
            public void showMovements() {
                JOptionPane.showMessageDialog(mainFrame,
                    "🎮 CONTROLES:\n\n" +
                    "ESPACIO - Soltar bloque\n" +
                    "P - Pausar juego\n" +
                    "ESC - Regresar al menú\n\n" +
                    "🏆 MODO PROFESIONAL:\n" +
                    "• CraneSystem con física realista\n" +
                    "• Garra con animación hidráulica\n" +
                    "• Péndulo con gravedad real\n" +
                    "• Generación continua de bloques",
                    "Controles", JOptionPane.INFORMATION_MESSAGE);
            }
            
            @Override
            public void showHighScores() {
                JOptionPane.showMessageDialog(mainFrame,
                    "🏆 HIGH SCORES\n\n" +
                    "🏆 PROFESIONAL: Tu sistema avanzado\n" +
                    "   con CraneSystem y físicas realistas!\n\n" +
                    "¡Selecciona PROFESIONAL para la\n" +
                    "experiencia más avanzada!",
                    "Puntajes", JOptionPane.INFORMATION_MESSAGE);
            }
            
            @Override
            public void exitGame() {
                mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
            }
            
            @Override
            public void startNewGame(DifficultyLevel difficulty) {
                if (difficulty == DifficultyLevel.PROFESSIONAL) {
                    // Launch your advanced system!
                    System.out.println("🚀 Launching PROFESSIONAL mode with advanced CraneSystem!");
                    launchProfessionalMode();
                } else {
                    // For other modes, show info
                    JOptionPane.showMessageDialog(mainFrame,
                        "🎮 Para el modo PROFESIONAL completo:\n\n" +
                        "Selecciona 'PROFESIONAL' para activar:\n" +
                        "• Tu CraneSystem avanzado\n" +
                        "• Físicas de péndulo realistas\n" +
                        "• Garra con animación hidráulica\n" +
                        "• PhysicsEngine completo\n\n" +
                        "Los otros modos usan el sistema básico.",
                        "Modo Disponible", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
    }
    
    private static void launchProfessionalMode() {
        // Remove old content
        mainPanel.removeAll();
        
        // Create your professional game
        TowerBloxxGame professionalGame = new TowerBloxxGame();
        mainPanel.add(professionalGame, "PROFESSIONAL");
        
        // Show professional mode
        cardLayout.show(mainPanel, "PROFESSIONAL");
        
        // Focus
        SwingUtilities.invokeLater(() -> professionalGame.requestFocusInWindow());
        
        System.out.println("✨ PROFESSIONAL MODE ACTIVATED!");
        System.out.println("🏗️ Advanced CraneSystem with realistic pendulum physics");
        System.out.println("🎪 Animated hydraulic claw system");
        System.out.println("⚡ Advanced PhysicsEngine");
        System.out.println("🔄 Continuous block generation");
    }
}

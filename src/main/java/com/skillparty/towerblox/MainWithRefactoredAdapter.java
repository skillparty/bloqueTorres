package com.skillparty.towerblox;

import com.skillparty.towerblox.integration.RefactoredGameAdapter;
import com.skillparty.towerblox.ui.GameWindow;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main launcher using your enhanced RefactoredGameAdapter with Professional Mode
 * 
 * @author joseAlejandro
 * @version 2.0 Professional Enhanced
 */
public class MainWithRefactoredAdapter {
    
    private static JFrame mainFrame;
    private static RefactoredGameAdapter gameAdapter;
    
    public static void main(String[] args) {
        showIntro();
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
    
    private static void showIntro() {
        System.out.println();
        System.out.println("     ██╗ █████╗ ██╗   ██╗ █████╗ ████████╗ ██████╗ ██╗    ██╗███████╗██████╗ ");
        System.out.println("     ██║██╔══██╗██║   ██║██╔══██╗╚══██╔══╝██╔═══██╗██║    ██║██╔════╝██╔══██╗");
        System.out.println("     ██║███████║██║   ██║███████║   ██║   ██║   ██║██║ █╗ ██║█████╗  ██████╔╝");
        System.out.println("██   ██║██╔══██║╚██╗ ██╔╝██╔══██║   ██║   ██║   ██║██║███╗██║██╔══╝  ██╔══██╗");
        System.out.println("╚█████╔╝██║  ██║ ╚████╔╝ ██║  ██║   ██║   ╚██████╔╝╚███╔███╔╝███████╗██║  ██║");
        System.out.println(" ╚════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝   ╚═╝    ╚═════╝  ╚══╝╚══╝ ╚══════╝╚═╝  ╚═╝");
        System.out.println();
        System.out.println("🎮 javaBloxx 2.0 - Professional Edition");
        System.out.println("🏆 Using Enhanced RefactoredGameAdapter with CraneSystem!");
        System.out.println("🚀 Launching with working menu buttons...");
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
            // Use default
        }
        
        // Create main frame
        mainFrame = new JFrame("javaBloxx 2.0 - Professional Edition");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setResizable(false);
        
        // Create GameWindow proxy for the adapter
        GameWindow gameWindow = new GameWindow() {
            @Override
            public void returnToMenu() {
                if (gameAdapter != null) {
                    gameAdapter.returnToMenu();
                }
            }
            
            @Override
            public void showMovements() {
                JOptionPane.showMessageDialog(mainFrame,
                    "🎮 CONTROLES:\n\n" +
                    "ESPACIO - Soltar bloque\n" +
                    "P - Pausar juego\n" +
                    "ESC - Regresar al menú\n\n" +
                    "🏆 MODO PROFESIONAL:\n" +
                    "• CraneSystem con físicas realistas\n" +
                    "• Garra con animación hidráulica\n" +
                    "• Péndulo con gravedad real (9.8)\n" +
                    "• Generación continua de bloques\n" +
                    "• PhysicsEngine avanzado",
                    "Controles del Juego", JOptionPane.INFORMATION_MESSAGE);
            }
            
            @Override
            public void showHighScores() {
                JOptionPane.showMessageDialog(mainFrame,
                    "🏆 HIGH SCORES\n\n" +
                    "Los puntajes se guardan por dificultad:\n\n" +
                    "🟢 FÁCIL: Velocidad 0.7x\n" +
                    "🟡 NORMAL: Velocidad 1.0x  \n" +
                    "🔴 DIFÍCIL: Velocidad 1.3x\n" +
                    "⭐ PROFESIONAL: Velocidad 1.5x\n" +
                    "   ¡Con tu CraneSystem avanzado!\n\n" +
                    "¡Haz clic en PROFESIONAL para\n" +
                    "activar tu sistema completo!",
                    "Puntuaciones Altas", JOptionPane.INFORMATION_MESSAGE);
            }
            
            @Override
            public void exitGame() {
                exitApplication();
            }
        };
        
        // Create your enhanced RefactoredGameAdapter
        gameAdapter = new RefactoredGameAdapter(gameWindow);
        
        // Setup frame
        mainFrame.add(gameAdapter);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        
        // Add window listener
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // Show frame
        mainFrame.setVisible(true);
        
        System.out.println("✅ javaBloxx 2.0 launched with RefactoredGameAdapter!");
        System.out.println("🎯 Menu buttons should work now!");
        System.out.println("🏆 Professional Mode available with your CraneSystem!");
    }
    
    private static void exitApplication() {
        System.out.println("\n🎮 Thanks for playing javaBloxx 2.0!");
        System.out.println("🏆 RefactoredGameAdapter with Professional Mode integration!");
        System.exit(0);
    }
}

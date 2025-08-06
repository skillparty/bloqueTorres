package com.skillparty.towerblox;

import com.skillparty.towerblox.ui.GameWindow;
import com.skillparty.towerblox.ui.components.ASCIILogo;
import javax.swing.SwingUtilities;

/**
 * Main entry point for Tower Bloxx Java game with ASCII logo intro
 */
public class Main {
    public static void main(String[] args) {
        // Clear console and show professional ASCII logo
        clearConsole();
        showASCIIIntro();
        
        // Wait a moment for the logo to be appreciated
        try {
            Thread.sleep(3000); // 3 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\n🎮 Starting javaBloxx Game...");
        System.out.println("📱 Resolution: 1280x720 (Professional Edition)");
        System.out.println("🏗️ Loading enhanced animations and professional movement...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Create and show the game window - skip menu, go directly to game
                GameWindow gameWindow = new GameWindow();
                gameWindow.setVisible(true);
                
                // Auto-start the game after a brief moment
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(500);
                        gameWindow.startGameDirectly();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                
            } catch (Exception e) {
                System.err.println("Error starting Tower Bloxx: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private static void clearConsole() {
        try {
            // Try to clear console (works on most terminals)
            System.out.print("\033[2J\033[H");
            System.out.flush();
        } catch (Exception e) {
            // If clearing fails, just add some space
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private static void showASCIIIntro() {
        ASCIILogo logo = new ASCIILogo();
        logo.printToConsole();
        
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println("                    🏗️  PROFESSIONAL TOWER BUILDING EXPERIENCE  🏗️");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println();
    }
}
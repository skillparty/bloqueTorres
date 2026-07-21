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

        System.out.println("\nStarting javaBloxx Game...");
        System.out.println("Resolution: 1280x720");
        System.out.println("Loading game assets...");

        SwingUtilities.invokeLater(() -> {
            try {
                // GameWindow shows MENU on construction; game starts on a real Play click
                GameWindow gameWindow = new GameWindow();
                gameWindow.setVisible(true);
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
        System.out.println("                     TOWER BUILDING EXPERIENCE");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println();
    }
}
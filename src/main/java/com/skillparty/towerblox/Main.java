package com.skillparty.towerblox;

import com.skillparty.towerblox.ui.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Main entry point for Tower Bloxx Java game
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("    TOWER BLOXX - javaTower Edition");
        System.out.println("    Enhanced Menu with Background Image");
        System.out.println("===========================================");

        SwingUtilities.invokeLater(() -> {
            try {
                // Create and show the game window which will start with the menu
                GameWindow gameWindow = new GameWindow();
                gameWindow.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting Tower Bloxx: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
package com.skillparty.towerblox;

import com.skillparty.towerblox.ui.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Main entry point for Tower Bloxx Java game
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameWindow().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting Tower Bloxx: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
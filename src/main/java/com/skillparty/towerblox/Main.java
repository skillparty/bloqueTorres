package com.skillparty.towerblox;

import com.skillparty.towerblox.ui.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Main entry point for Tower Bloxx
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("🏗️ Tower Bloxx - Starting Game...");
        
        SwingUtilities.invokeLater(() -> {
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);
            gameWindow.startGameDirectly();
        });
    }
}

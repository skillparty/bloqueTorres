package com.skillparty.towerblox;

import com.skillparty.towerblox.ui.MainMenu;

import javax.swing.*;

/**
 * Main entry point for Tower Bloxx game
 * Launches the main menu with the enhanced background system
 */
public class TowerBloxxMain {
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("    TOWER BLOXX - Digital Chocolate Edition");
        System.out.println("    Enhanced with Background Image System");
        System.out.println("    Resolution: 1280 x 720");
        System.out.println("===========================================");
        
        // Set system properties for better performance
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
        
        // Enable anti-aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        try {
            // Show the main menu
            MainMenu.showMainMenu();
            
        } catch (Exception e) {
            System.err.println("Error starting Tower Bloxx: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog
            JOptionPane.showMessageDialog(null, 
                "Error starting Tower Bloxx:\n" + e.getMessage(), 
                "Tower Bloxx Error", 
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }
    }
}

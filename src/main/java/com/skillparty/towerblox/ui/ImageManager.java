package com.skillparty.towerblox.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and caching of game images
 */
public class ImageManager {
    private static ImageManager instance;
    private Map<String, BufferedImage> imageCache;
    private final String basePath;
    
    private ImageManager() {
        this.imageCache = new HashMap<>();
        // Get the correct path - try multiple locations
        String userDir = System.getProperty("user.dir");
        this.basePath = userDir + "/public/img/";
    }
    
    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }
    
    /**
     * Loads and caches an image
     */
    public BufferedImage loadImage(String filename) {
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }
        
        // Try multiple possible locations
        String[] possiblePaths = {
            basePath + filename,
            System.getProperty("user.dir") + "/public/img/" + filename,
            "./public/img/" + filename,
            "public/img/" + filename,
            filename
        };
        
        for (String path : possiblePaths) {
            try {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    BufferedImage image = ImageIO.read(imageFile);
                    imageCache.put(filename, image);
                    System.out.println("✅ Successfully loaded image from: " + path);
                    return image;
                }
            } catch (IOException e) {
                // Continue to next path
            }
        }
        
        System.err.println("❌ Image not found in any location: " + filename);
        for (String path : possiblePaths) {
            System.err.println("   Tried: " + path);
        }
        return createPlaceholderImage(800, 600);
    }
    
    /**
     * Loads the menu background image
     */
    public BufferedImage getMenuBackground() {
        // Try to load the specific image file
        String[] possibleNames = {
            "ChatGPT Image Aug 5, 2025 at 10_09_33 PM-2.png",
            "menu_background.png",
            "background.png"
        };
        
        for (String filename : possibleNames) {
            BufferedImage image = loadImage(filename);
            if (image != null && image.getWidth() > 100) { // Valid image
                return image;
            }
        }
        
        // Return a styled placeholder if no image found
        return createTowerBloxxMenuBackground();
    }
    
    /**
     * Creates a Tower Bloxx style menu background
     */
    private BufferedImage createTowerBloxxMenuBackground() {
        BufferedImage image = new BufferedImage(3456, 2234, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Sky gradient (Tower Bloxx style)
        java.awt.GradientPaint skyGradient = new java.awt.GradientPaint(
            0, 0, new Color(135, 206, 250),
            0, 2234, new Color(255, 218, 185)
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, 3456, 2234);
        
        // City silhouette
        g2d.setColor(new Color(50, 50, 50, 180));
        for (int i = 0; i < 20; i++) {
            int buildingWidth = 100 + (int)(Math.random() * 150);
            int buildingHeight = 200 + (int)(Math.random() * 400);
            int x = i * (3456 / 20);
            int y = 2234 - buildingHeight;
            g2d.fillRect(x, y, buildingWidth, buildingHeight);
            
            // Windows
            g2d.setColor(new Color(255, 255, 100, 150));
            for (int row = 0; row < buildingHeight / 30; row++) {
                for (int col = 0; col < buildingWidth / 25; col++) {
                    if (Math.random() > 0.3) {
                        g2d.fillRect(x + col * 25 + 5, y + row * 30 + 5, 15, 20);
                    }
                }
            }
            g2d.setColor(new Color(50, 50, 50, 180));
        }
        
        // Tower Bloxx title styling
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 120));
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        String title = "TOWER BLOXX";
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (3456 - titleWidth) / 2, 400);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Creates a placeholder image
     */
    private BufferedImage createPlaceholderImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(100, 150, 200));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Image Not Found", width / 2 - 50, height / 2);
        g2d.dispose();
        return image;
    }
    
    /**
     * Scales an image to fit the specified dimensions while maintaining aspect ratio
     */
    public BufferedImage scaleImage(BufferedImage original, int targetWidth, int targetHeight) {
        double scaleX = (double) targetWidth / original.getWidth();
        double scaleY = (double) targetHeight / original.getHeight();
        double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
        
        int scaledWidth = (int) (original.getWidth() * scale);
        int scaledHeight = (int) (original.getHeight() * scale);
        
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                           java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Center the image
        int x = (targetWidth - scaledWidth) / 2;
        int y = (targetHeight - scaledHeight) / 2;
        
        g2d.drawImage(original, x, y, scaledWidth, scaledHeight, null);
        g2d.dispose();
        
        return scaled;
    }
}

package com.skillparty.towerblox.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Professional image loading and caching system for Tower Bloxx
 * Handles background images and creates fallback graphics
 */
public class ImageManager {
    private static final String BACKGROUND_PATH = "public/img/";
    private static final String FALLBACK_IMAGE_NAME = "tower_bloxx_bg.png";
    
    // Image cache for performance
    private Map<String, BufferedImage> imageCache;
    
    public ImageManager() {
        this.imageCache = new HashMap<>();
    }
    
    /**
     * Loads and caches an image from the background directory
     */
    public BufferedImage loadBackgroundImage(String filename) {
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }
        
        try {
            File imageFile = new File(BACKGROUND_PATH + filename);
            if (imageFile.exists()) {
                BufferedImage image = ImageIO.read(imageFile);
                imageCache.put(filename, image);
                return image;
            }
        } catch (IOException e) {
            System.err.println("Failed to load image: " + filename);
        }
        
        // Return fallback image if loading fails
        return createFallbackBackground();
    }
    
    /**
     * Creates a beautiful Tower Bloxx-style fallback background
     */
    public BufferedImage createFallbackBackground() {
        if (imageCache.containsKey(FALLBACK_IMAGE_NAME)) {
            return imageCache.get(FALLBACK_IMAGE_NAME);
        }
        
        BufferedImage fallback = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = fallback.createGraphics();
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create gradient sky
        GradientPaint skyGradient = new GradientPaint(
            0, 0, new Color(135, 206, 235),
            0, 720, new Color(255, 255, 255)
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, 1280, 720);
        
        // Add city silhouette
        createCitySilhouette(g2d);
        
        g2d.dispose();
        imageCache.put(FALLBACK_IMAGE_NAME, fallback);
        return fallback;
    }
    
    /**
     * Creates a professional city silhouette for the background
     */
    private void createCitySilhouette(Graphics2D g2d) {
        g2d.setColor(new Color(40, 40, 60, 180));
        
        // Create various building shapes
        int[] buildingHeights = {120, 80, 150, 100, 180, 90, 160, 110, 140, 85, 170, 95};
        int buildingWidth = 1280 / buildingHeights.length;
        
        for (int i = 0; i < buildingHeights.length; i++) {
            int x = i * buildingWidth;
            int height = buildingHeights[i];
            int y = 720 - height;
            
            // Building body
            g2d.fillRect(x, y, buildingWidth, height);
            
            // Add windows
            g2d.setColor(new Color(255, 255, 150, 100));
            for (int row = 0; row < height / 15; row++) {
                for (int col = 0; col < buildingWidth / 12; col++) {
                    if (Math.random() > 0.6) {
                        int windowX = x + col * 12 + 2;
                        int windowY = y + row * 15 + 2;
                        g2d.fillRect(windowX, windowY, 8, 10);
                    }
                }
            }
            
            g2d.setColor(new Color(40, 40, 60, 180));
        }
    }
    
    /**
     * Scales an image to the specified dimensions
     */
    public BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        return scaled;
    }
    
    /**
     * Clears the image cache to free memory
     */
    public void clearCache() {
        imageCache.clear();
    }
}

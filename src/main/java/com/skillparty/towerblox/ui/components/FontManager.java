package com.skillparty.towerblox.ui.components;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages font loading and provides Ubuntu Monospace font with fallback
 */
public class FontManager {
    private static FontManager instance;
    private Font ubuntuMonoFont;
    private Map<Float, Font> fontCache;
    private boolean ubuntuMonoLoaded = false;

    // Font sizes for different UI elements
    public static final float LOGO_SIZE = 24f;
    public static final float MENU_SIZE = 18f;
    public static final float GAME_SIZE = 14f;
    public static final float SCORE_SIZE = 16f;
    public static final float SMALL_SIZE = 12f;

    private FontManager() {
        fontCache = new HashMap<>();
        loadUbuntuMonoFont();
    }

    /**
     * Gets the singleton instance of FontManager
     */
    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    /**
     * Loads Ubuntu Monospace font from resources
     */
    private void loadUbuntuMonoFont() {
        try {
            InputStream fontStream = getClass().getResourceAsStream("/fonts/UbuntuMono-Regular.ttf");
            if (fontStream != null) {
                ubuntuMonoFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(ubuntuMonoFont);
                ubuntuMonoLoaded = true;
                System.out.println("Ubuntu Monospace font loaded successfully");
            } else {
                System.out.println("Ubuntu Monospace font not found, using system fallback");
                createFallbackFont();
            }
        } catch (FontFormatException | IOException e) {
            System.err.println("Error loading Ubuntu Monospace font: " + e.getMessage());
            createFallbackFont();
        }
    }

    /**
     * Creates fallback monospace font when Ubuntu Mono is not available
     */
    private void createFallbackFont() {
        ubuntuMonoFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);
        ubuntuMonoLoaded = false;
    }

    /**
     * Gets Ubuntu Monospace font with specified size
     * @param size Font size
     * @return Font instance with specified size
     */
    public Font getFont(float size) {
        Font cachedFont = fontCache.get(size);
        if (cachedFont != null) {
            return cachedFont;
        }

        Font derivedFont = ubuntuMonoFont.deriveFont(size);
        fontCache.put(size, derivedFont);
        return derivedFont;
    }

    /**
     * Gets Ubuntu Monospace font with specified size and style
     * @param size Font size
     * @param style Font style (Font.PLAIN, Font.BOLD, Font.ITALIC)
     * @return Font instance with specified size and style
     */
    public Font getFont(float size, int style) {
        return ubuntuMonoFont.deriveFont(style, size);
    }

    /**
     * Gets logo font (Ubuntu Mono 24pt bold)
     */
    public Font getLogoFont() {
        return getFont(LOGO_SIZE, Font.BOLD);
    }

    /**
     * Gets menu font (Ubuntu Mono 18pt)
     */
    public Font getMenuFont() {
        return getFont(MENU_SIZE);
    }

    /**
     * Gets game font (Ubuntu Mono 14pt)
     */
    public Font getGameFont() {
        return getFont(GAME_SIZE);
    }

    /**
     * Gets score font (Ubuntu Mono 16pt)
     */
    public Font getScoreFont() {
        return getFont(SCORE_SIZE);
    }

    /**
     * Gets small font (Ubuntu Mono 12pt)
     */
    public Font getSmallFont() {
        return getFont(SMALL_SIZE);
    }

    /**
     * Checks if Ubuntu Monospace font was loaded successfully
     */
    public boolean isUbuntuMonoLoaded() {
        return ubuntuMonoLoaded;
    }

    /**
     * Gets the base font name
     */
    public String getFontName() {
        return ubuntuMonoLoaded ? "Ubuntu Mono" : "Monospaced";
    }

    /**
     * Clears the font cache
     */
    public void clearCache() {
        fontCache.clear();
    }
}
package com.skillparty.towerblox;

import com.skillparty.towerblox.ui.components.FontManager;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.Font;

/**
 * Unit tests for FontManager class
 */
public class FontManagerTest {
    private FontManager fontManager;

    @Before
    public void setUp() {
        fontManager = FontManager.getInstance();
    }

    @Test
    public void testSingletonInstance() {
        FontManager instance1 = FontManager.getInstance();
        FontManager instance2 = FontManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testFontLoading() {
        Font logoFont = fontManager.getLogoFont();
        assertNotNull(logoFont);
        assertEquals(FontManager.LOGO_SIZE, logoFont.getSize2D(), 0.1f);
        assertTrue(logoFont.isBold());
    }

    @Test
    public void testDifferentFontSizes() {
        Font menuFont = fontManager.getMenuFont();
        Font gameFont = fontManager.getGameFont();
        Font scoreFont = fontManager.getScoreFont();
        Font smallFont = fontManager.getSmallFont();

        assertNotNull(menuFont);
        assertNotNull(gameFont);
        assertNotNull(scoreFont);
        assertNotNull(smallFont);

        assertEquals(FontManager.MENU_SIZE, menuFont.getSize2D(), 0.1f);
        assertEquals(FontManager.GAME_SIZE, gameFont.getSize2D(), 0.1f);
        assertEquals(FontManager.SCORE_SIZE, scoreFont.getSize2D(), 0.1f);
        assertEquals(FontManager.SMALL_SIZE, smallFont.getSize2D(), 0.1f);
    }

    @Test
    public void testCustomFontSize() {
        float customSize = 20f;
        Font customFont = fontManager.getFont(customSize);
        assertNotNull(customFont);
        assertEquals(customSize, customFont.getSize2D(), 0.1f);
    }

    @Test
    public void testFontCaching() {
        Font font1 = fontManager.getFont(16f);
        Font font2 = fontManager.getFont(16f);
        assertSame(font1, font2); // Should be the same cached instance
    }

    @Test
    public void testFontName() {
        String fontName = fontManager.getFontName();
        assertNotNull(fontName);
        assertTrue(fontName.equals("Ubuntu Mono") || fontName.equals("Monospaced"));
    }

    @Test
    public void testClearCache() {
        fontManager.getFont(16f); // Add to cache
        fontManager.clearCache();
        
        Font font1 = fontManager.getFont(16f);
        Font font2 = fontManager.getFont(16f);
        assertSame(font1, font2); // Should still cache after clearing
    }
}
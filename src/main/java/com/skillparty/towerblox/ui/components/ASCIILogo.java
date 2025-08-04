package com.skillparty.towerblox.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * ASCII art logo component for Tower Bloxx in Linux terminal style
 */
public class ASCIILogo {
    private static final String[] LOGO_LINES = {
        "     ██╗ █████╗ ██╗   ██╗ █████╗ ",
        "     ██║██╔══██╗██║   ██║██╔══██╗",
        "     ██║███████║██║   ██║███████║",
        "██   ██║██╔══██║╚██╗ ██╔╝██╔══██║",
        "╚█████╔╝██║  ██║ ╚████╔╝ ██║  ██║",
        " ╚════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝",
        "",
        "████████╗ ██████╗ ██╗    ██╗███████╗██████╗ ",
        "╚══██╔══╝██╔═══██╗██║    ██║██╔════╝██╔══██╗",
        "   ██║   ██║   ██║██║ █╗ ██║█████╗  ██████╔╝",
        "   ██║   ██║   ██║██║███╗██║██╔══╝  ██╔══██╗",
        "   ██║   ╚██████╔╝╚███╔███╔╝███████╗██║  ██║",
        "   ╚═╝    ╚═════╝  ╚══╝╚══╝ ╚══════╝╚═╝  ╚═╝"
    };

    private static final String VERSION_TEXT = "by joseAlejandro";
    private static final String SUBTITLE = "Build the highest tower!";

    // Linux terminal colors
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0);        // Black
    private static final Color PRIMARY_COLOR = new Color(0, 255, 0);         // Bright Green
    private static final Color SECONDARY_COLOR = new Color(0, 200, 0);       // Dark Green
    private static final Color VERSION_COLOR = new Color(128, 128, 128);     // Gray
    private static final Color SUBTITLE_COLOR = new Color(255, 255, 0);      // Yellow

    private FontManager fontManager;
    private int width;
    private int height;

    public ASCIILogo() {
        this.fontManager = FontManager.getInstance();
        calculateDimensions();
    }

    /**
     * Calculates the dimensions needed to render the logo
     */
    private void calculateDimensions() {
        Font logoFont = fontManager.getFont(FontManager.LOGO_SIZE - 8); // Slightly smaller for ASCII art
        
        // Calculate width based on longest line
        int maxLineLength = 0;
        for (String line : LOGO_LINES) {
            maxLineLength = Math.max(maxLineLength, line.length());
        }
        
        // Estimate character width for monospace font
        this.width = maxLineLength * 12; // Approximate character width
        this.height = (LOGO_LINES.length + 4) * 20; // Line height + extra space for version and subtitle
    }

    /**
     * Renders the ASCII logo with Linux terminal styling
     * @param g2d Graphics2D context
     * @param x X position to render at
     * @param y Y position to render at
     */
    public void render(Graphics2D g2d, int x, int y) {
        // Enable antialiasing for smoother text
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font logoFont = fontManager.getFont(FontManager.LOGO_SIZE - 8);
        Font versionFont = fontManager.getFont(FontManager.SMALL_SIZE);
        Font subtitleFont = fontManager.getFont(FontManager.MENU_SIZE - 2);

        FontMetrics logoMetrics = g2d.getFontMetrics(logoFont);
        FontMetrics versionMetrics = g2d.getFontMetrics(versionFont);
        FontMetrics subtitleMetrics = g2d.getFontMetrics(subtitleFont);

        int currentY = y;

        // Render main logo lines
        g2d.setFont(logoFont);
        for (int i = 0; i < LOGO_LINES.length; i++) {
            String line = LOGO_LINES[i];
            
            // Alternate colors for depth effect
            if (i < 6) {
                g2d.setColor(PRIMARY_COLOR);
            } else {
                g2d.setColor(SECONDARY_COLOR);
            }
            
            // Center the line
            int lineWidth = logoMetrics.stringWidth(line);
            int lineX = x + (width - lineWidth) / 2;
            
            g2d.drawString(line, lineX, currentY);
            currentY += logoMetrics.getHeight();
        }

        // Add some spacing
        currentY += 10;

        // Render subtitle
        g2d.setFont(subtitleFont);
        g2d.setColor(SUBTITLE_COLOR);
        int subtitleWidth = subtitleMetrics.stringWidth(SUBTITLE);
        int subtitleX = x + (width - subtitleWidth) / 2;
        g2d.drawString(SUBTITLE, subtitleX, currentY);
        currentY += subtitleMetrics.getHeight() + 5;

        // Render version
        g2d.setFont(versionFont);
        g2d.setColor(VERSION_COLOR);
        int versionWidth = versionMetrics.stringWidth(VERSION_TEXT);
        int versionX = x + (width - versionWidth) / 2;
        g2d.drawString(VERSION_TEXT, versionX, currentY);
    }

    /**
     * Renders the logo with a terminal-style background
     * @param g2d Graphics2D context
     * @param x X position
     * @param y Y position
     * @param drawBackground Whether to draw the terminal background
     */
    public void renderWithBackground(Graphics2D g2d, int x, int y, boolean drawBackground) {
        if (drawBackground) {
            // Draw terminal-style background
            g2d.setColor(BACKGROUND_COLOR);
            g2d.fillRect(x - 20, y - 30, width + 40, height + 40);
            
            // Draw border like a terminal window
            g2d.setColor(PRIMARY_COLOR);
            g2d.drawRect(x - 20, y - 30, width + 40, height + 40);
            
            // Draw terminal title bar
            g2d.setColor(SECONDARY_COLOR);
            g2d.fillRect(x - 20, y - 30, width + 40, 25);
            
            // Terminal window controls (fake)
            g2d.setColor(Color.RED);
            g2d.fillOval(x - 15, y - 25, 8, 8);
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x - 5, y - 25, 8, 8);
            g2d.setColor(PRIMARY_COLOR);
            g2d.fillOval(x + 5, y - 25, 8, 8);
            
            // Terminal title
            g2d.setColor(Color.BLACK);
            g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE));
            g2d.drawString("tower-bloxx@terminal:~$", x + 20, y - 12);
        }
        
        render(g2d, x, y);
    }

    /**
     * Gets the width needed to render the logo
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height needed to render the logo
     */
    public int getHeight() {
        return height;
    }

    /**
     * Creates a simple ASCII version for console output
     */
    public void printToConsole() {
        System.out.println();
        for (String line : LOGO_LINES) {
            System.out.println(line);
        }
        System.out.println();
        System.out.println("    " + SUBTITLE);
        System.out.println("    " + VERSION_TEXT);
        System.out.println();
    }
}
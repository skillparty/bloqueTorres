package com.skillparty.towerblox.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Professional ASCII logo for Tower Bloxx with developer credit
 */
public class ASCIILogo {
    private static final String[] LOGO_LINES = {
        "     ██╗ █████╗ ██╗   ██╗ █████╗ ██████╗ ██╗      ██████╗ ██╗  ██╗██╗  ██╗",
        "     ██║██╔══██╗██║   ██║██╔══██╗██╔══██╗██║     ██╔═══██╗╚██╗██╔╝╚██╗██╔╝",
        "     ██║███████║██║   ██║███████║██████╔╝██║     ██║   ██║ ╚███╔╝  ╚███╔╝ ",
        "██   ██║██╔══██║╚██╗ ██╔╝██╔══██║██╔══██╗██║     ██║   ██║ ██╔██╗  ██╔██╗ ",
        "╚█████╔╝██║  ██║ ╚████╔╝ ██║  ██║██████╔╝███████╗╚██████╔╝██╔╝ ██╗██╔╝ ██╗",
        " ╚════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝╚═════╝ ╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝",
        "",
        "                    ██████╗ ██╗   ██╗    ",
        "                    ██╔══██╗╚██╗ ██╔╝    ",
        "                    ██████╔╝ ╚████╔╝     ",
        "                    ██╔══██╗  ╚██╔╝      ",
        "                    ██████╔╝   ██║       ",
        "                    ╚═════╝    ╚═╝       ",
        "",
        "     ██╗ ██████╗ ███████╗███████╗ █████╗ ██╗     ███████╗     ██╗ █████╗ ███╗   ██╗██████╗ ██████╗  ██████╗ ",
        "     ██║██╔═══██╗██╔════╝██╔════╝██╔══██╗██║     ██╔════╝     ██║██╔══██╗████╗  ██║██╔══██╗██╔══██╗██╔═══██╗",
        "     ██║██║   ██║███████╗█████╗  ███████║██║     █████╗       ██║███████║██╔██╗ ██║██║  ██║██████╔╝██║   ██║",
        "██   ██║██║   ██║╚════██║██╔══╝  ██╔══██║██║     ██╔══╝  ██   ██║██╔══██║██║╚██╗██║██║  ██║██╔══██╗██║   ██║",
        "╚█████╔╝╚██████╔╝███████║███████╗██║  ██║███████╗███████╗╚█████╔╝██║  ██║██║ ╚████║██████╔╝██║  ██║╚██████╔╝",
        " ╚════╝  ╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝ ╚════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝ ╚═╝  ╚═╝ ╚═════╝ "
    };

    private static final String DEV_TEXT = "by joseAlejandro";
    private static final String SUBTITLE = "Professional Tower Building Experience";

    // Professional colors
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42);     // Dark Slate
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);      // Professional Blue
    private static final Color SECONDARY_COLOR = new Color(16, 185, 129);    // Success Green
    private static final Color ACCENT_COLOR = new Color(99, 102, 241);       // Indigo
    private static final Color DEV_COLOR = new Color(245, 158, 11);          // Amber for developer credit
    private static final Color SUBTITLE_COLOR = new Color(148, 163, 184);    // Light Gray

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

        // Render main logo lines with professional gradient effect
        g2d.setFont(logoFont);
        for (int i = 0; i < LOGO_LINES.length; i++) {
            String line = LOGO_LINES[i];
            
            // Professional color scheme with gradient effect
            Color lineColor;
            if (i < 6) {
                // "JAVA" part - Blue to Indigo gradient
                float ratio = (float) i / 5;
                lineColor = blendColors(PRIMARY_COLOR, ACCENT_COLOR, ratio);
            } else {
                // "TOWER" part - Green to Blue gradient
                float ratio = (float) (i - 6) / 6;
                lineColor = blendColors(SECONDARY_COLOR, PRIMARY_COLOR, ratio);
            }
            
            g2d.setColor(lineColor);
            
            // Center the line
            int lineWidth = logoMetrics.stringWidth(line);
            int lineX = x + (width - lineWidth) / 2;
            
            // Add subtle shadow for depth
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(line, lineX + 1, currentY + 1);
            
            // Draw main text
            g2d.setColor(lineColor);
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

        // Render developer credit
        g2d.setFont(versionFont);
        g2d.setColor(DEV_COLOR);
        int devWidth = versionMetrics.stringWidth(DEV_TEXT);
        int devX = x + (width - devWidth) / 2;
        g2d.drawString(DEV_TEXT, devX, currentY);
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
            
            // Draw professional border with gradient
            g2d.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 180));
            g2d.setStroke(new java.awt.BasicStroke(2));
            g2d.drawRoundRect(x - 20, y - 30, width + 40, height + 40, 10, 10);
            
            // Draw modern title bar with gradient
            java.awt.GradientPaint titleBarGradient = new java.awt.GradientPaint(
                x - 20, y - 30, SECONDARY_COLOR,
                x - 20, y - 5, PRIMARY_COLOR
            );
            g2d.setPaint(titleBarGradient);
            g2d.fillRoundRect(x - 20, y - 30, width + 40, 25, 10, 10);
            
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
     * Blends two colors based on a ratio for gradient effects
     * @param color1 First color
     * @param color2 Second color
     * @param ratio Blend ratio (0.0 = color1, 1.0 = color2)
     * @return Blended color
     */
    private Color blendColors(Color color1, Color color2, float ratio) {
        ratio = Math.max(0.0f, Math.min(1.0f, ratio)); // Clamp ratio between 0 and 1
        
        int red = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int blue = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        
        return new Color(red, green, blue);
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
        System.out.println("    " + DEV_TEXT);
        System.out.println();
    }
    
    /**
     * Displays the logo to console (alias for printToConsole)
     */
    public void displayLogo() {
        printToConsole();
    }
}
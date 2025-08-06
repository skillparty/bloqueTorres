package com.skillparty.towerblox.game.physics;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Professional base design system for Tower Bloxx
 * Creates beautiful, realistic foundation blocks
 */
public class ProfessionalBaseDesign {
    
    public enum BaseType {
        CONCRETE_FOUNDATION,
        STEEL_REINFORCED,
        MARBLE_LUXURY,
        MODERN_GLASS,
        INDUSTRIAL_METAL
    }
    
    /**
     * Renders a professional foundation base
     */
    public static void renderProfessionalBase(Graphics2D g2d, int x, int y, int width, int height, BaseType baseType) {
        // Enable antialiasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        switch (baseType) {
            case CONCRETE_FOUNDATION:
                renderConcreteFoundation(g2d, x, y, width, height);
                break;
            case STEEL_REINFORCED:
                renderSteelReinforced(g2d, x, y, width, height);
                break;
            case MARBLE_LUXURY:
                renderMarbleLuxury(g2d, x, y, width, height);
                break;
            case MODERN_GLASS:
                renderModernGlass(g2d, x, y, width, height);
                break;
            case INDUSTRIAL_METAL:
                renderIndustrialMetal(g2d, x, y, width, height);
                break;
        }
        
        // Add professional label
        renderBaseLabel(g2d, x, y, width, height, baseType);
    }
    
    private static void renderConcreteFoundation(Graphics2D g2d, int x, int y, int width, int height) {
        // Main concrete body with gradient
        GradientPaint gradient = new GradientPaint(
            x, y, new Color(140, 140, 140),
            x + width, y + height, new Color(100, 100, 100)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Concrete texture
        g2d.setColor(new Color(120, 120, 120, 150));
        for (int i = x + 3; i < x + width - 3; i += 8) {
            for (int j = y + 3; j < y + height - 3; j += 8) {
                if (Math.random() > 0.6) {
                    g2d.fillRect(i, j, 2, 2);
                }
            }
        }
        
        // Reinforcement bars
        g2d.setColor(new Color(80, 80, 80));
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < 3; i++) {
            int barY = y + height/4 + i * height/4;
            g2d.drawLine(x + 5, barY, x + width - 5, barY);
        }
        
        // Border
        g2d.setColor(new Color(80, 80, 80));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x, y, width, height);
    }
    
    private static void renderSteelReinforced(Graphics2D g2d, int x, int y, int width, int height) {
        // Steel base
        GradientPaint gradient = new GradientPaint(
            x, y, new Color(160, 160, 180),
            x + width, y + height, new Color(120, 120, 140)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Steel beams
        g2d.setColor(new Color(90, 90, 110));
        g2d.setStroke(new BasicStroke(4));
        // Horizontal beams
        g2d.drawLine(x, y + height/3, x + width, y + height/3);
        g2d.drawLine(x, y + 2*height/3, x + width, y + 2*height/3);
        // Vertical beams
        g2d.drawLine(x + width/3, y, x + width/3, y + height);
        g2d.drawLine(x + 2*width/3, y, x + 2*width/3, y + height);
        
        // Rivets
        g2d.setColor(new Color(70, 70, 90));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                int rivetX = x + (i + 1) * width/5;
                int rivetY = y + (j + 1) * height/4;
                g2d.fillOval(rivetX - 2, rivetY - 2, 4, 4);
            }
        }
        
        // Metallic shine
        g2d.setColor(new Color(200, 200, 220, 100));
        g2d.fillRect(x + 2, y + 2, width/3, height/4);
    }
    
    private static void renderMarbleLuxury(Graphics2D g2d, int x, int y, int width, int height) {
        // Marble base with elegant gradient
        GradientPaint gradient = new GradientPaint(
            x, y, new Color(240, 240, 245),
            x + width, y + height, new Color(200, 200, 210)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Marble veining
        g2d.setColor(new Color(180, 180, 190, 120));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 5; i++) {
            int startX = x + (int)(Math.random() * width);
            int startY = y + (int)(Math.random() * height);
            int endX = startX + (int)(Math.random() * 30 - 15);
            int endY = startY + (int)(Math.random() * 20 - 10);
            g2d.drawLine(startX, startY, endX, endY);
        }
        
        // Gold trim
        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x + 2, y + 2, width - 4, height - 4);
        
        // Elegant border
        g2d.setColor(new Color(160, 160, 170));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, width, height);
    }
    
    private static void renderModernGlass(Graphics2D g2d, int x, int y, int width, int height) {
        // Glass base with transparency effect
        GradientPaint gradient = new GradientPaint(
            x, y, new Color(200, 220, 255, 180),
            x + width, y + height, new Color(150, 180, 220, 180)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Glass reflection
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillRect(x + 2, y + 2, width/3, height/2);
        
        // Modern frame
        g2d.setColor(new Color(100, 100, 120));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);
        
        // LED strip effect
        g2d.setColor(new Color(0, 150, 255, 150));
        g2d.fillRect(x, y + height - 4, width, 4);
    }
    
    private static void renderIndustrialMetal(Graphics2D g2d, int x, int y, int width, int height) {
        // Industrial metal base
        GradientPaint gradient = new GradientPaint(
            x, y, new Color(120, 120, 130),
            x + width, y + height, new Color(80, 80, 90)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        
        // Diamond plate pattern
        g2d.setColor(new Color(100, 100, 110));
        for (int i = x + 5; i < x + width - 5; i += 10) {
            for (int j = y + 5; j < y + height - 5; j += 10) {
                int[] xPoints = {i, i + 3, i, i - 3};
                int[] yPoints = {j - 3, j, j + 3, j};
                g2d.fillPolygon(xPoints, yPoints, 4);
            }
        }
        
        // Warning stripes
        g2d.setColor(new Color(255, 255, 0));
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < 3; i++) {
            int stripeY = y + height - 8 + i * 3;
            g2d.drawLine(x, stripeY, x + width, stripeY);
        }
        
        // Heavy border
        g2d.setColor(new Color(60, 60, 70));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(x, y, width, height);
    }
    
    private static void renderBaseLabel(Graphics2D g2d, int x, int y, int width, int height, BaseType baseType) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        
        String label = "FOUNDATION";
        int labelWidth = fm.stringWidth(label);
        int labelX = x + (width - labelWidth) / 2;
        int labelY = y + height / 2 + fm.getAscent() / 2;
        
        // Shadow effect
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(label, labelX + 1, labelY + 1);
        
        // Main text
        g2d.setColor(Color.WHITE);
        g2d.drawString(label, labelX, labelY);
    }
    
    /**
     * Gets the appropriate base type based on tower height and difficulty
     */
    public static BaseType getBaseTypeForTower(int towerHeight, String difficulty) {
        if (towerHeight < 5) return BaseType.CONCRETE_FOUNDATION;
        if (towerHeight < 15) return BaseType.STEEL_REINFORCED;
        if (towerHeight < 30) return BaseType.MODERN_GLASS;
        if (towerHeight < 50) return BaseType.MARBLE_LUXURY;
        return BaseType.INDUSTRIAL_METAL;
    }
}

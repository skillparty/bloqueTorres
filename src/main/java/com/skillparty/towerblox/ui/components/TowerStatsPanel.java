package com.skillparty.towerblox.ui.components;

import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.game.physics.Block;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Panel lateral que muestra estadísticas detalladas de la torre
 */
public class TowerStatsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private GameEngine gameEngine;
    private Font titleFont;
    private Font statsFont;
    private Font smallFont;
    
    // Colores del tema
    private static final Color PANEL_BACKGROUND = new Color(0, 0, 0, 180);
    private static final Color BORDER_COLOR = new Color(64, 224, 255, 200);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 215, 0);
    private static final Color SUCCESS_COLOR = new Color(0, 255, 127);
    private static final Color WARNING_COLOR = new Color(255, 140, 0);
    
    public TowerStatsPanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        
        setPreferredSize(new Dimension(200, 600));
        setOpaque(false);
        
        initializeFonts();
    }
    
    private void initializeFonts() {
        try {
            titleFont = new Font("Arial", Font.BOLD, 16);
            statsFont = new Font("Arial", Font.BOLD, 14);
            smallFont = new Font("Arial", Font.PLAIN, 12);
        } catch (Exception e) {
            // Fallback fonts
            titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
            statsFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
            smallFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (gameEngine == null || gameEngine.getTower() == null) {
            g2d.dispose();
            return;
        }
        
        Tower tower = gameEngine.getTower();
        
        // Panel de fondo con bordes redondeados
        int panelWidth = getWidth() - 20;
        int panelHeight = getHeight() - 20;
        
        RoundRectangle2D panelBounds = new RoundRectangle2D.Float(10, 10, panelWidth, panelHeight, 15, 15);
        g2d.setColor(PANEL_BACKGROUND);
        g2d.fill(panelBounds);
        
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(panelBounds);
        
        // Título del panel
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.setFont(titleFont);
        String title = "ESTADÍSTICAS DE TORRE";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 35);
        
        // Línea separadora
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(20, 45, getWidth() - 20, 45);
        
        int y = 70;
        
        // Estadísticas principales
        y = drawMainStats(g2d, tower, y);
        
        // Separador
        y += 10;
        g2d.setColor(BORDER_COLOR);
        g2d.drawLine(20, y, getWidth() - 20, y);
        y += 20;
        
        // Estadísticas detalladas
        y = drawDetailedStats(g2d, tower, y);
        
        // Separador
        y += 10;
        g2d.setColor(BORDER_COLOR);
        g2d.drawLine(20, y, getWidth() - 20, y);
        y += 20;
        
        // Información del último bloque
        y = drawLastBlockInfo(g2d, tower, y);
        
        // Separador
        y += 10;
        g2d.setColor(BORDER_COLOR);
        g2d.drawLine(20, y, getWidth() - 20, y);
        y += 20;
        
        // Progreso visual de la torre
        drawTowerProgress(g2d, tower, y);
        
        g2d.dispose();
    }
    
    private int drawMainStats(Graphics2D g2d, Tower tower, int y) {
        g2d.setFont(statsFont);
        
        // Altura de la torre
        g2d.setColor(SUCCESS_COLOR);
        g2d.drawString("ALTURA:", 25, y);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(String.valueOf(tower.getHeight()), 120, y);
        g2d.drawString("bloques", 145, y);
        y += 25;
        
        // Puntuación actual
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.drawString("PUNTOS:", 25, y);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(String.valueOf(gameEngine.getScoreManager().getCurrentScore()), 120, y);
        y += 25;
        
        // Vidas restantes
        g2d.setColor(tower.getHeight() > 15 ? SUCCESS_COLOR : WARNING_COLOR);
        g2d.drawString("VIDAS:", 25, y);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(String.valueOf(gameEngine.getLives()), 120, y);
        y += 25;
        
        return y;
    }
    
    private int drawDetailedStats(Graphics2D g2d, Tower tower, int y) {
        g2d.setFont(smallFont);
        
        List<Block> blocks = tower.getBlocks();
        if (blocks.isEmpty()) return y;
        
        // Estabilidad promedio
        double avgStability = blocks.stream()
            .mapToDouble(Block::getStability)
            .average()
            .orElse(0.0);
            
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Estabilidad promedio:", 25, y);
        
        Color stabilityColor = avgStability > 0.8 ? SUCCESS_COLOR : 
                              avgStability > 0.5 ? WARNING_COLOR : Color.RED;
        g2d.setColor(stabilityColor);
        g2d.drawString(String.format("%.1f%%", avgStability * 100), 140, y);
        y += 20;
        
        // Ancho promedio
        double avgWidth = blocks.stream()
            .mapToDouble(Block::getWidth)
            .average()
            .orElse(0.0);
            
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Ancho promedio:", 25, y);
        g2d.drawString(String.format("%.0f px", avgWidth), 140, y);
        y += 20;
        
        // Tipo de bloques más común
        String mostCommonType = getMostCommonBlockType(blocks);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Tipo más común:", 25, y);
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.drawString(mostCommonType, 25, y + 15);
        y += 35;
        
        return y;
    }
    
    private int drawLastBlockInfo(Graphics2D g2d, Tower tower, int y) {
        g2d.setFont(smallFont);
        
        if (tower.isEmpty()) {
            g2d.setColor(TEXT_COLOR);
            g2d.drawString("No hay bloques aún", 25, y);
            return y + 20;
        }
        
        Block lastBlock = tower.getTopBlock();
        
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.drawString("ÚLTIMO BLOQUE:", 25, y);
        y += 20;
        
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Tipo: " + lastBlock.getType().toString(), 25, y);
        y += 15;
        
        g2d.drawString(String.format("Posición: (%.0f, %.0f)", lastBlock.getX(), lastBlock.getY()), 25, y);
        y += 15;
        
        g2d.drawString(String.format("Estabilidad: %.1f%%", lastBlock.getStability() * 100), 25, y);
        y += 15;
        
        Color stabilityColor = lastBlock.getStability() > 0.8 ? SUCCESS_COLOR : 
                              lastBlock.getStability() > 0.5 ? WARNING_COLOR : Color.RED;
        g2d.setColor(stabilityColor);
        g2d.drawString(getStabilityText(lastBlock.getStability()), 25, y);
        y += 20;
        
        return y;
    }
    
    private void drawTowerProgress(Graphics2D g2d, Tower tower, int y) {
        g2d.setFont(smallFont);
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.drawString("PROGRESO VISUAL:", 25, y);
        y += 20;
        
        // Barra de progreso basada en altura
        int barWidth = 150;
        int barHeight = 20;
        int barX = 25;
        
        // Fondo de la barra
        g2d.setColor(new Color(64, 64, 64));
        g2d.fillRoundRect(barX, y, barWidth, barHeight, 10, 10);
        
        // Progreso (cada 10 bloques = 100% de una sección)
        int currentHeight = tower.getHeight();
        int sectionHeight = ((currentHeight - 1) / 10 + 1) * 10; // Siguiente múltiplo de 10
        double progress = (double) currentHeight / sectionHeight;
        
        int progressWidth = (int) (barWidth * progress);
        
        // Gradiente de color basado en la altura
        Color progressColor = getHeightColor(currentHeight);
        g2d.setColor(progressColor);
        g2d.fillRoundRect(barX, y, progressWidth, barHeight, 10, 10);
        
        // Borde de la barra
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(barX, y, barWidth, barHeight, 10, 10);
        
        // Texto de progreso
        g2d.setColor(TEXT_COLOR);
        String progressText = String.format("Sección %d: %d/%d", (currentHeight - 1) / 10 + 1, 
                                           (currentHeight - 1) % 10 + 1, 10);
        g2d.drawString(progressText, barX, y + 35);
    }
    
    private String getMostCommonBlockType(List<Block> blocks) {
        return blocks.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                block -> block.getType(),
                java.util.stream.Collectors.counting()))
            .entrySet()
            .stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(entry -> entry.getKey().toString())
            .orElse("N/A");
    }
    
    private String getStabilityText(double stability) {
        if (stability > 0.9) return "EXCELENTE";
        if (stability > 0.8) return "MUY BUENO";
        if (stability > 0.6) return "BUENO";
        if (stability > 0.4) return "REGULAR";
        return "PELIGROSO";
    }
    
    private Color getHeightColor(int height) {
        if (height < 5) return new Color(0, 255, 127); // Verde
        if (height < 10) return new Color(127, 255, 0); // Verde-amarillo
        if (height < 20) return new Color(255, 215, 0); // Dorado
        if (height < 30) return new Color(255, 140, 0); // Naranja
        if (height < 50) return new Color(255, 69, 0); // Rojo-naranja
        return new Color(255, 20, 147); // Rosa fuerte para torres muy altas
    }
}

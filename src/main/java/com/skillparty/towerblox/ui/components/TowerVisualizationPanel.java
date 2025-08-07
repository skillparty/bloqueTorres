package com.skillparty.towerblox.ui.components;

import com.skillparty.towerblox.game.GameEngine;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.game.physics.Block;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Random;

/**
 * Panel épico que muestra una torre desde la base hasta la atmósfera (altura 163)
 * con animaciones atmosféricas y efectos visuales dinámicos
 */
public class TowerVisualizationPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private GameEngine gameEngine;
    private Font titleFont;
    private Font statsFont;
    private Font atmosphereFont;
    
    // Configuración épica de la torre
    private static final int MAX_TOWER_HEIGHT = 163;  // Límite atmosférico
    private static final int VISIBLE_SEGMENTS = 25;   // Segmentos visibles en pantalla
    private static final int SEGMENT_HEIGHT = 8;      // Altura de cada segmento
    private static final int TOWER_BASE_WIDTH = 90;   // Ancho base
    private static final int TOWER_TOP_WIDTH = 20;    // Ancho en la cima
    
    // Zonas atmosféricas
    private static final int GROUND_LEVEL = 0;
    private static final int CITY_LEVEL = 20;
    private static final int CLOUD_LEVEL = 50;
    private static final int SKY_LEVEL = 100;
    private static final int SPACE_LEVEL = 150;
    private static final int ATMOSPHERE_LEVEL = 163;
    
    // Colores por zonas atmosféricas
    private static final Color GROUND_COLOR = new Color(139, 69, 19);           // Marrón tierra
    private static final Color CITY_COLOR = new Color(64, 64, 64);              // Gris ciudad
    private static final Color CLOUD_COLOR = new Color(176, 196, 222);          // Azul claro nubes
    private static final Color SKY_COLOR = new Color(135, 206, 235);            // Azul cielo
    private static final Color SPACE_COLOR = new Color(25, 25, 112);            // Azul marino espacio
    private static final Color ATMOSPHERE_COLOR = new Color(72, 61, 139);       // Púrpura atmósfera
    
    // Colores de segmentos por estabilidad
    private static final Color SEGMENT_INACTIVE = new Color(71, 85, 105, 80);   // Gris inactivo
    private static final Color SEGMENT_PERFECT = new Color(255, 215, 0);        // Oro perfecto
    private static final Color SEGMENT_EXCELLENT = new Color(50, 205, 50);      // Verde excelente
    private static final Color SEGMENT_GOOD = new Color(255, 165, 0);           // Naranja bueno
    private static final Color SEGMENT_WARNING = new Color(255, 69, 0);         // Rojo advertencia
    private static final Color SEGMENT_DANGER = new Color(220, 20, 60);         // Rojo peligro
    
    // Colores de interfaz
    private static final Color PANEL_BACKGROUND = new Color(15, 23, 42, 220);
    private static final Color BORDER_COLOR = new Color(59, 130, 246, 200);
    private static final Color TEXT_COLOR = new Color(248, 250, 252);
    
    // Sistema ESTÁTICO sin animaciones - EPILEPSY SAFE
    private int lastBlockHeight = 0;
    private int currentViewOffset = 0;  // Para scroll automático
    private Random random = new Random();
    
    // Partículas atmosféricas
    private java.util.List<AtmosphereParticle> particles;
    
    /**
     * Partícula atmosférica para efectos visuales
     */
    private static class AtmosphereParticle {
        float x, y, vx, vy;
        Color color;
        float alpha;
        int size;
        long birthTime;
        
        AtmosphereParticle(float x, float y, Color color) {
            this.x = x;
            this.y = y;
            this.vx = (float)(Math.random() - 0.5) * 0.5f;
            this.vy = (float)Math.random() * -0.3f;
            this.color = color;
            this.alpha = 0.7f;
            this.size = (int)(Math.random() * 3) + 1;
            this.birthTime = System.currentTimeMillis();
        }
        
        void update() {
            x += vx;
            y += vy;
            long age = System.currentTimeMillis() - birthTime;
            alpha = Math.max(0, 0.7f - age / 5000.0f);
        }
        
        boolean isAlive() {
            return alpha > 0;
        }
    }

    public TowerVisualizationPanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        
        setPreferredSize(new Dimension(200, 600));
        setOpaque(false);
        
        initializeFonts();
        initializeParticles();
        // ELIMINADO: setupAnimationTimer() - NO MÁS ANIMACIONES
        System.out.println("🎮 Torre de visualización creada - SIN ANIMACIONES PARA ACCESIBILIDAD");
    }
    
    /**
     * ELIMINADO - Timer de animaciones removido para EPILEPSY SAFETY
     */
    private void setupAnimationTimer() {
        // SISTEMA COMPLETAMENTE ESTÁTICO - NO ANIMACIONES
        // Mensaje informativo para desarrolladores
        System.out.println("TOWER VISUALIZATION: Usando sistema estático sin animaciones (epilepsy-safe)");
    }
    
    private void initializeFonts() {
        try {
            titleFont = new Font("Arial", Font.BOLD, 12);
            statsFont = new Font("Arial", Font.BOLD, 10);
            atmosphereFont = new Font("Arial", Font.PLAIN, 8);
        } catch (Exception e) {
            titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
            statsFont = new Font(Font.SANS_SERIF, Font.BOLD, 10);
            atmosphereFont = new Font(Font.SANS_SERIF, Font.PLAIN, 8);
        }
    }
    
    /**
     * Inicializa las partículas atmosféricas
     */
    private void initializeParticles() {
        particles = new java.util.ArrayList<>();
    }
    
    /**
     * Actualiza todas las animaciones
     */
    private void updateAnimations() {
        if (gameEngine == null || gameEngine.getTower() == null) return;
        
        int towerHeight = gameEngine.getTower().getHeight();
        
        // Auto-scroll para seguir la torre
        if (towerHeight > VISIBLE_SEGMENTS / 2) {
            int targetOffset = Math.min(towerHeight - VISIBLE_SEGMENTS / 2, MAX_TOWER_HEIGHT - VISIBLE_SEGMENTS);
            currentViewOffset += (targetOffset - currentViewOffset) * 0.05; // Smooth scroll
        }
        
        // Generar partículas atmosféricas según la zona
        if (random.nextFloat() < 0.3f) {
            generateAtmosphereParticles(towerHeight);
        }
        
        // Actualizar partículas existentes
        particles.removeIf(p -> {
            p.update();
            return !p.isAlive();
        });
    }
    
    /**
     * Genera partículas según la zona atmosférica actual
     */
    private void generateAtmosphereParticles(int towerHeight) {
        float x = random.nextFloat() * getWidth();
        float y = random.nextFloat() * 300 + 60; // Zona de la torre
        
        Color particleColor;
        if (towerHeight < CITY_LEVEL) {
            particleColor = new Color(139, 69, 19, 100); // Polvo terrestre
        } else if (towerHeight < CLOUD_LEVEL) {
            particleColor = new Color(128, 128, 128, 120); // Smog urbano
        } else if (towerHeight < SKY_LEVEL) {
            particleColor = new Color(255, 255, 255, 150); // Vapor de nubes
        } else if (towerHeight < SPACE_LEVEL) {
            particleColor = new Color(173, 216, 230, 100); // Cristales de hielo
        } else {
            particleColor = new Color(255, 255, 255, 80); // Partículas espaciales
        }
        
        particles.add(new AtmosphereParticle(x, y, particleColor));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (gameEngine == null || gameEngine.getTower() == null) {
            drawLoadingState(g2d);
            g2d.dispose();
            return;
        }
        
        Tower tower = gameEngine.getTower();
        int towerHeight = tower.getHeight();
        
        // Panel de fondo con gradiente atmosférico
        drawAtmosphericBackground(g2d, towerHeight);
        
        // Dibujar la torre épica desde la base hasta la atmósfera
        drawEpicTower(g2d, tower);
        
        // Efectos atmosféricos y partículas
        drawAtmosphereEffects(g2d, towerHeight);
        
        // Indicadores de zona atmosférica
        drawAtmosphereZones(g2d, towerHeight);
        
        // Estadísticas compactas
        drawCompactStats(g2d, tower);
        
        g2d.dispose();
    }
    
    /**
     * Dibuja el fondo atmosférico dinámico
     */
    private void drawAtmosphericBackground(Graphics2D g2d, int towerHeight) {
        int panelWidth = getWidth() - 10;
        int panelHeight = getHeight() - 10;
        
        // Determinar colores de fondo según la altura de la torre
        Color topColor, bottomColor;
        
        if (towerHeight < CITY_LEVEL) {
            topColor = new Color(135, 206, 235, 100);  // Cielo azul claro
            bottomColor = new Color(139, 69, 19, 150); // Tierra marrón
        } else if (towerHeight < CLOUD_LEVEL) {
            topColor = new Color(176, 196, 222, 120);  // Nubes
            bottomColor = new Color(64, 64, 64, 150);  // Ciudad gris
        } else if (towerHeight < SKY_LEVEL) {
            topColor = new Color(135, 206, 235, 140);  // Cielo azul
            bottomColor = new Color(176, 196, 222, 120); // Nubes
        } else if (towerHeight < SPACE_LEVEL) {
            topColor = new Color(25, 25, 112, 160);    // Espacio azul marino
            bottomColor = new Color(135, 206, 235, 140); // Cielo azul
        } else {
            topColor = new Color(72, 61, 139, 180);    // Atmósfera púrpura
            bottomColor = new Color(25, 25, 112, 160); // Espacio azul marino
        }
        
        // Gradiente atmosférico
        GradientPaint atmosphereGradient = new GradientPaint(
            0, 5, topColor,
            0, panelHeight, bottomColor
        );
        
        g2d.setPaint(atmosphereGradient);
        g2d.fillRoundRect(5, 5, panelWidth, panelHeight, 15, 15);
        
        // Borde dinámico que cambia con la altura
        Color borderColor = getBorderColorForHeight(towerHeight);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(5, 5, panelWidth, panelHeight, 15, 15);
        
        // Efecto de brillo en el borde para torres altas
        if (towerHeight > SKY_LEVEL) {
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(6, 6, panelWidth - 2, panelHeight - 2, 14, 14);
        }
    }
    
    /**
     * Obtiene el color del borde según la altura
     */
    private Color getBorderColorForHeight(int height) {
        if (height < CITY_LEVEL) return new Color(139, 69, 19, 200);      // Marrón tierra
        if (height < CLOUD_LEVEL) return new Color(64, 64, 64, 200);      // Gris ciudad
        if (height < SKY_LEVEL) return new Color(135, 206, 235, 200);     // Azul cielo
        if (height < SPACE_LEVEL) return new Color(25, 25, 112, 200);     // Azul espacio
        return new Color(72, 61, 139, 200);                               // Púrpura atmósfera
    }
    
    /**
     * Estado de carga cuando no hay datos
     */
    private void drawLoadingState(Graphics2D g2d) {
        g2d.setColor(PANEL_BACKGROUND);
        g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15);
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(titleFont);
        String loading = "Iniciando torre...";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(loading)) / 2;
        g2d.drawString(loading, x, getHeight() / 2);
    }

    /**
     * Dibuja la torre épica desde la base hasta la atmósfera
     */
    private void drawEpicTower(Graphics2D g2d, Tower tower) {
        int towerHeight = tower.getHeight();
        int centerX = getWidth() / 2;
        int towerAreaTop = 50;
        int towerAreaHeight = 300;
        
        // Calcular el rango visible de segmentos
        int startSegment = Math.max(0, currentViewOffset);
        int endSegment = Math.min(MAX_TOWER_HEIGHT, startSegment + VISIBLE_SEGMENTS);
        
        // Dibujar base de la torre (siempre visible si estamos en el nivel base)
        if (startSegment == 0) {
            drawTowerBase(g2d, centerX, towerAreaTop + towerAreaHeight - 20);
        }
        
        // Dibujar cada segmento visible
        for (int i = startSegment; i < endSegment; i++) {
            int segmentIndex = i - startSegment;
            int segmentY = towerAreaTop + towerAreaHeight - 20 - (segmentIndex * SEGMENT_HEIGHT);
            
            // Calcular ancho del segmento (cónico)
            float heightRatio = (float)i / MAX_TOWER_HEIGHT;
            float widthRatio = 1.0f - heightRatio * 0.7f; // Reducción del 70% hacia la cima
            int segmentWidth = (int)(TOWER_BASE_WIDTH * widthRatio);
            segmentWidth = Math.max(segmentWidth, TOWER_TOP_WIDTH); // Mínimo ancho
            
            // Determinar color y efectos del segmento
            Color segmentColor = getSegmentColorForHeight(tower, i, towerHeight);
            boolean isActive = i < towerHeight;
            boolean isLatest = i == towerHeight - 1;
            
            // Efectos especiales para el último bloque colocado
            if (isLatest && isActive) {
                drawLatestBlockEffects(g2d, centerX, segmentY, segmentWidth, segmentColor);
            }
            
            // Efecto de brillo para segmentos activos
            if (isActive && segmentColor != SEGMENT_INACTIVE) {
                drawSegmentGlow(g2d, centerX, segmentY, segmentWidth, segmentColor, isLatest);
            }
            
            // Dibujar el segmento principal
            drawTowerSegment(g2d, centerX, segmentY, segmentWidth, segmentColor, i, isActive);
        }
        
        // Indicador de progreso y altura
        drawHeightIndicator(g2d, towerHeight, centerX, towerAreaTop, towerAreaHeight);
    }
    
    /**
     * Dibuja la base sólida de la torre
     */
    private void drawTowerBase(Graphics2D g2d, int centerX, int baseY) {
        // Plataforma de tierra
        g2d.setColor(GROUND_COLOR);
        g2d.fillRect(centerX - TOWER_BASE_WIDTH/2 - 15, baseY, TOWER_BASE_WIDTH + 30, 12);
        
        // Cimientos de la torre
        g2d.setColor(GROUND_COLOR.darker());
        g2d.fillRoundRect(centerX - TOWER_BASE_WIDTH/2 - 5, baseY - 8, TOWER_BASE_WIDTH + 10, 8, 4, 4);
    }
    
    /**
     * Dibuja efectos ESTÁTICOS para el último bloque colocado - SIN PARPADEOS
     */
    private void drawLatestBlockEffects(Graphics2D g2d, int centerX, int segmentY, int segmentWidth, Color segmentColor) {
        // Detectar nuevo bloque
        int currentHeight = gameEngine.getTower().getHeight();
        if (currentHeight != lastBlockHeight) {
            lastBlockHeight = currentHeight;
        }
        
        // Efecto ESTÁTICO (sin animación ni parpadeo)
        if (currentHeight > 0) {
            // Resplandor FIJO para el último bloque
            g2d.setColor(new Color(segmentColor.getRed(), segmentColor.getGreen(), segmentColor.getBlue(), 100));
            g2d.fillRoundRect(centerX - segmentWidth/2 - 4, segmentY - 4, 
                            segmentWidth + 8, SEGMENT_HEIGHT + 8, 8, 8);
        }
    }
    
    /**
     * Dibuja el resplandor de los segmentos activos
     */
    private void drawSegmentGlow(Graphics2D g2d, int centerX, int segmentY, int segmentWidth, 
                                Color segmentColor, boolean isLatest) {
        int glowAlpha = isLatest ? 100 : 60;
        int glowSize = isLatest ? 3 : 2;
        
        g2d.setColor(new Color(segmentColor.getRed(), segmentColor.getGreen(), segmentColor.getBlue(), glowAlpha));
        g2d.fillRoundRect(centerX - segmentWidth/2 - glowSize, segmentY - glowSize, 
                        segmentWidth + glowSize * 2, SEGMENT_HEIGHT + glowSize * 2, 6, 6);
    }
    
    /**
     * Dibuja un segmento individual de la torre
     */
    private void drawTowerSegment(Graphics2D g2d, int centerX, int segmentY, int segmentWidth, 
                                 Color segmentColor, int height, boolean isActive) {
        // Segmento principal
        g2d.setColor(segmentColor);
        g2d.fillRoundRect(centerX - segmentWidth/2, segmentY, segmentWidth, SEGMENT_HEIGHT, 4, 4);
        
        // Borde del segmento
        g2d.setColor(segmentColor.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(centerX - segmentWidth/2, segmentY, segmentWidth, SEGMENT_HEIGHT, 4, 4);
        
        // Highlight interno para segmentos activos
        if (isActive && segmentColor != SEGMENT_INACTIVE) {
            g2d.setColor(new Color(255, 255, 255, 120));
            g2d.fillRoundRect(centerX - segmentWidth/2 + 2, segmentY + 1, segmentWidth - 4, 2, 2, 2);
        }
        
        // Marcadores de altura cada 10 niveles
        if (height % 10 == 9 && height > 0) {
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(atmosphereFont);
            String levelText = String.valueOf(height + 1);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(levelText, centerX + segmentWidth/2 + 3, segmentY + SEGMENT_HEIGHT/2 + 2);
        }
    }
    
    /**
     * Dibuja el indicador de altura actual
     */
    /**
     * Dibuja el indicador de altura actual CON EFECTOS CREATIVOS Y SEGURO PARA EPILEPSIA
     */
    private void drawHeightIndicator(Graphics2D g2d, int towerHeight, int centerX, 
                                   int towerAreaTop, int towerAreaHeight) {
        if (towerHeight == 0) return;
        
        // DIBUJAR TORRE PROGRESIVA - Cada piso se ilumina cuando se alcanza
        drawProgressiveTower(g2d, towerHeight, centerX, towerAreaTop, towerAreaHeight);
        
        // EFECTO CREATIVO: Barras de progreso estáticas (sin parpadeo)
        drawStaticProgressBars(g2d, towerHeight, centerX, towerAreaTop, towerAreaHeight);
        
        // EFECTO CREATIVO: Flecha con gradiente fijo (sin animación)
        drawStaticArrow(g2d, towerHeight, centerX, towerAreaTop, towerAreaHeight);
        
        // EFECTO CREATIVO: Texto con sombra fija
        drawStaticText(g2d, towerHeight, centerX, towerAreaTop, towerAreaHeight);
    }
    
    /**
     * Dibuja una torre que se ilumina progresivamente - SEGURO PARA EPILEPSIA
     */
    /**
     * Dibuja torre progresiva usando colores REALES de los bloques - SIN PARPADEOS
     * Sistema 100% ESTÁTICO para EPILEPSY SAFETY - no hay animaciones ni efectos temporales
     */
    private void drawProgressiveTower(Graphics2D g2d, int towerHeight, int centerX, 
                                    int towerAreaTop, int towerAreaHeight) {
        // Configuración de la torre lateral
        int towerX = centerX - TOWER_BASE_WIDTH/2 - 120; // Posición a la izquierda
        int towerBottomY = towerAreaTop + towerAreaHeight - 50;
        int towerWidth = 40;
        int maxVisibleFloors = 20; // Máximo 20 pisos visibles
        int floorHeight = 15;
        
        // Base de la torre (siempre visible)
        g2d.setColor(new Color(80, 80, 80)); // Gris oscuro para base
        g2d.fillRect(towerX - 5, towerBottomY + 5, towerWidth + 10, 8);
        
        // Dibujar cada piso según la altura alcanzada
        for (int floor = 1; floor <= Math.min(towerHeight, maxVisibleFloors); floor++) {
            int floorY = towerBottomY - (floor * floorHeight);
            
            // Color del piso según la altura (iluminación estática)
            Color floorColor = getFloorColor(floor, towerHeight);
            
            // Dibujar el piso
            g2d.setColor(floorColor);
            g2d.fillRect(towerX, floorY, towerWidth, floorHeight - 2);
            
            // Borde del piso
            g2d.setColor(floorColor.brighter());
            g2d.drawRect(towerX, floorY, towerWidth, floorHeight - 2);
            
            // Ventanas del piso (detalles fijos)
            drawFloorWindows(g2d, towerX, floorY, towerWidth, floorHeight, floor);
        }
        
        // Indicador de más pisos si la torre es muy alta
        if (towerHeight > maxVisibleFloors) {
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(statsFont);
            g2d.drawString("+" + (towerHeight - maxVisibleFloors), towerX + 5, 
                          towerBottomY - (maxVisibleFloors * floorHeight) - 10);
        }
    }
    
    /**
     * Obtiene el color del piso basado en el color REAL del bloque colocado
     */
    private Color getFloorColor(int floor, int currentHeight) {
        if (floor <= currentHeight) {
            // Obtener el color del bloque real si existe
            try {
                if (gameEngine.getTower() != null && gameEngine.getTower().getBlocks().size() >= floor) {
                    // Usar el color real del bloque (indexado desde 0)
                    Color blockColor = gameEngine.getTower().getBlocks().get(floor - 1).getColor();
                    // Hacer el color semi-transparente para el efecto visual
                    return new Color(blockColor.getRed(), blockColor.getGreen(), 
                                   blockColor.getBlue(), 200);
                }
            } catch (Exception e) {
                // Si hay error, usar color por defecto
            }
            
            // Colores por defecto si no se puede obtener el color del bloque
            if (floor <= 5) return new Color(100, 200, 100, 200);      // Verde claro
            if (floor <= 10) return new Color(100, 150, 200, 200);     // Azul suave
            if (floor <= 15) return new Color(200, 150, 100, 200);     // Naranja suave
            return new Color(200, 100, 150, 200);                      // Rosa suave
        } else {
            // Piso no alcanzado - gris oscuro
            return new Color(60, 60, 60, 150);
        }
    }
    
    /**
     * Dibuja ventanas en cada piso para dar detalle
     */
    private void drawFloorWindows(Graphics2D g2d, int floorX, int floorY, 
                                 int floorWidth, int floorHeight, int floorNumber) {
        // Color de ventanas (amarillo suave fijo)
        g2d.setColor(new Color(255, 255, 180, 180));
        
        // 3 ventanas por piso
        int windowWidth = 4;
        int windowHeight = 6;
        int windowSpacing = (floorWidth - (3 * windowWidth)) / 4;
        
        for (int window = 0; window < 3; window++) {
            int windowX = floorX + windowSpacing + (window * (windowWidth + windowSpacing));
            int windowY = floorY + 4;
            g2d.fillRect(windowX, windowY, windowWidth, windowHeight);
        }
    }
    
    /**
     * Dibuja barras de progreso ESTÁTICAS (sin animación)
     */
    private void drawStaticProgressBars(Graphics2D g2d, int towerHeight, int centerX, 
                                       int towerAreaTop, int towerAreaHeight) {
        // Barra de progreso principal FIJA
        int barWidth = 60;
        int barHeight = 8;
        int barX = centerX + TOWER_BASE_WIDTH/2 + 20; // Al lado derecho
        int barY = towerAreaTop + 50;
        
        // Fondo de la barra
        g2d.setColor(new Color(30, 30, 30, 180));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 4, 4);
        
        // Progreso - diferente color según la altura (ESTÁTICO)
        double progress = Math.min(1.0, towerHeight / 20.0);
        int progressWidth = (int)(barWidth * progress);
        
        Color progressColor = getStaticProgressColor(towerHeight);
        g2d.setColor(progressColor);
        g2d.fillRoundRect(barX, barY, progressWidth, barHeight, 4, 4);
        
        // Borde fijo
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.drawRoundRect(barX, barY, barWidth, barHeight, 4, 4);
        
        // Texto de nivel
        g2d.setFont(statsFont);
        g2d.setColor(TEXT_COLOR);
        String levelText = "Nivel " + (towerHeight / 5 + 1);
        g2d.drawString(levelText, barX, barY - 5);
    }
    
    /**
     * Obtiene el color de progreso ESTÁTICO según la altura
     */
    private Color getStaticProgressColor(int height) {
        if (height < 5) return new Color(100, 200, 100);      // Verde
        if (height < 10) return new Color(100, 150, 200);     // Azul  
        if (height < 15) return new Color(200, 150, 100);     // Naranja
        if (height < 20) return new Color(200, 100, 150);     // Rosa
        return new Color(150, 100, 200);                      // Púrpura
    }
    
    /**
     * Dibuja una flecha ESTÁTICA con gradiente fijo
     */
    private void drawStaticArrow(Graphics2D g2d, int towerHeight, int centerX, 
                               int towerAreaTop, int towerAreaHeight) {
        int visibleHeight = Math.min(towerHeight, VISIBLE_SEGMENTS);
        int indicatorY = towerAreaTop + towerAreaHeight - 20 - ((visibleHeight - 1) * SEGMENT_HEIGHT);
        
        // Gradiente dorado FIJO (sin animación)
        GradientPaint gradient = new GradientPaint(
            centerX - TOWER_BASE_WIDTH/2 - 20, indicatorY,
            new Color(255, 215, 0, 180),
            centerX - TOWER_BASE_WIDTH/2 - 8, indicatorY,
            new Color(255, 165, 0, 120)
        );
        g2d.setPaint(gradient);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Línea del indicador
        g2d.drawLine(centerX - TOWER_BASE_WIDTH/2 - 20, indicatorY + SEGMENT_HEIGHT/2, 
                    centerX - TOWER_BASE_WIDTH/2 - 8, indicatorY + SEGMENT_HEIGHT/2);
        
        // Punta de flecha
        int[] xPoints = {centerX - TOWER_BASE_WIDTH/2 - 8, centerX - TOWER_BASE_WIDTH/2 - 12, 
                        centerX - TOWER_BASE_WIDTH/2 - 12};
        int[] yPoints = {indicatorY + SEGMENT_HEIGHT/2, indicatorY + SEGMENT_HEIGHT/2 - 4, 
                        indicatorY + SEGMENT_HEIGHT/2 + 4};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Dibuja texto ESTÁTICO con sombra fija
     */
    private void drawStaticText(Graphics2D g2d, int towerHeight, int centerX, 
                              int towerAreaTop, int towerAreaHeight) {
        int visibleHeight = Math.min(towerHeight, VISIBLE_SEGMENTS);
        int indicatorY = towerAreaTop + towerAreaHeight - 20 - ((visibleHeight - 1) * SEGMENT_HEIGHT);
        
        g2d.setFont(statsFont);
        String heightText = "Altura: " + towerHeight;
        
        // Sombra FIJA del texto
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.drawString(heightText, centerX - TOWER_BASE_WIDTH/2 - 44, indicatorY + SEGMENT_HEIGHT/2 + 4);
        
        // Texto principal
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(heightText, centerX - TOWER_BASE_WIDTH/2 - 45, indicatorY + SEGMENT_HEIGHT/2 + 3);
    }
    
    /**
     * Dibuja efectos atmosféricos y partículas
     */
    private void drawAtmosphereEffects(Graphics2D g2d, int towerHeight) {
        // Dibujar partículas atmosféricas
        for (AtmosphereParticle particle : particles) {
            g2d.setColor(new Color(particle.color.getRed(), particle.color.getGreen(), 
                                 particle.color.getBlue(), (int)(particle.alpha * 255)));
            g2d.fillOval((int)particle.x, (int)particle.y, particle.size, particle.size);
        }
        
        // Efectos especiales según la altura
        if (towerHeight >= SPACE_LEVEL) {
            drawSpaceEffects(g2d);
        } else if (towerHeight >= SKY_LEVEL) {
            drawHighAltitudeEffects(g2d);
        } else if (towerHeight >= CLOUD_LEVEL) {
            drawCloudEffects(g2d);
        }
    }
    
    /**
     * Efectos del espacio - COMPLETAMENTE ESTÁTICOS (seguro para epilepsia)
     */
    private void drawSpaceEffects(Graphics2D g2d) {
        // Estrellas COMPLETAMENTE ESTÁTICAS (sin parpadeo ni animación)
        Random staticRandom = new Random(12345); // Seed fija para posiciones consistentes
        for (int i = 0; i < 15; i++) {
            if (staticRandom.nextFloat() < 0.3f) {
                int x = staticRandom.nextInt(getWidth());
                int y = 60 + staticRandom.nextInt(200);
                g2d.setColor(new Color(255, 255, 255, 120)); // Color completamente fijo
                g2d.fillOval(x, y, 2, 2); // Estrellas un poco más grandes para mejor visibilidad
            }
        }
    }
    
    /**
     * Efectos de gran altitud
     */
    private void drawHighAltitudeEffects(Graphics2D g2d) {
        // Aurora boreal sutil
        if (random.nextFloat() < 0.1f) {
            g2d.setColor(new Color(0, 255, 127, 30));
            g2d.fillOval(random.nextInt(getWidth()), 60 + random.nextInt(100), 20, 5);
        }
    }
    
    /**
     * Efectos de nubes ESTÁTICOS - Sin movimiento
     */
    private void drawCloudEffects(Graphics2D g2d) {
        // Nubes FIJAS sin animación
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillOval((int)(getWidth() * 0.7), 100, 25, 15);
        g2d.fillOval((int)(getWidth() * 0.2), 150, 30, 18);
    }
    
    /**
     * Dibuja indicadores de zonas atmosféricas
     */
    private void drawAtmosphereZones(Graphics2D g2d, int towerHeight) {
        g2d.setFont(atmosphereFont);
        int rightX = getWidth() - 5;
        
        // Mostrar zona actual
        String currentZone = getAtmosphereZoneName(towerHeight);
        Color zoneColor = getAtmosphereZoneColor(towerHeight);
        
        g2d.setColor(zoneColor);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(currentZone, rightX - fm.stringWidth(currentZone), 25);
    }
    
    /**
     * Obtiene el nombre de la zona atmosférica actual
     */
    private String getAtmosphereZoneName(int height) {
        if (height < CITY_LEVEL) return "SUPERFICIE";
        if (height < CLOUD_LEVEL) return "CIUDAD";
        if (height < SKY_LEVEL) return "NUBES";
        if (height < SPACE_LEVEL) return "CIELO";
        if (height < ATMOSPHERE_LEVEL) return "ESPACIO";
        return "ATMÓSFERA";
    }
    
    /**
     * Obtiene el color de la zona atmosférica actual
     */
    private Color getAtmosphereZoneColor(int height) {
        if (height < CITY_LEVEL) return GROUND_COLOR;
        if (height < CLOUD_LEVEL) return CITY_COLOR;
        if (height < SKY_LEVEL) return CLOUD_COLOR;
        if (height < SPACE_LEVEL) return SKY_COLOR;
        if (height < ATMOSPHERE_LEVEL) return SPACE_COLOR;
        return ATMOSPHERE_COLOR;
    }
    
    /**
     * Dibuja estadísticas compactas
     */
    private void drawCompactStats(Graphics2D g2d, Tower tower) {
        g2d.setFont(statsFont);
        int y = getHeight() - 80;
        
        // Fondo semi-transparente para las estadísticas
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(10, y - 15, getWidth() - 20, 70, 8, 8);
        
        // Vidas
        int lives = gameEngine.getLives();
        g2d.setColor(lives > 1 ? SEGMENT_EXCELLENT : SEGMENT_DANGER);
        g2d.drawString("♥ " + lives, 15, y);
        y += 15;
        
        // Altura con indicador de zona
        g2d.setColor(getAtmosphereZoneColor(tower.getHeight()));
        g2d.drawString("↑ " + tower.getHeight() + "/" + MAX_TOWER_HEIGHT, 15, y);
        y += 15;
        
        // Estabilidad con barra compacta
        double stability = (1.0 - tower.getInstabilityScore()) * 100;
        Color stabilityColor = stability >= 80 ? SEGMENT_EXCELLENT : 
                              stability >= 60 ? SEGMENT_GOOD : SEGMENT_DANGER;
        
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Estab:", 15, y);
        
        // Mini barra de estabilidad
        int barWidth = 60;
        int barHeight = 4;
        int barX = 55;
        
        g2d.setColor(SEGMENT_INACTIVE);
        g2d.fillRect(barX, y - 3, barWidth, barHeight);
        
        g2d.setColor(stabilityColor);
        g2d.fillRect(barX, y - 3, (int)(barWidth * stability / 100), barHeight);
        
        g2d.setColor(stabilityColor);
        g2d.drawString(String.format("%.0f%%", stability), barX + barWidth + 5, y);
        y += 15;
        
        // Puntuación
        g2d.setColor(SEGMENT_PERFECT);
        int score = gameEngine.getScoreManager().getCurrentScore();
        g2d.drawString("★ " + score, 15, y);
    }
    
    /**
     * Determina el color de un segmento basado en su altura y estabilidad
     */
    private Color getSegmentColorForHeight(Tower tower, int segmentIndex, int towerHeight) {
        if (segmentIndex >= towerHeight) {
            return SEGMENT_INACTIVE; // Segmento no alcanzado
        }
        
        List<Block> blocks = tower.getBlocks();
        if (segmentIndex >= blocks.size()) {
            return SEGMENT_INACTIVE;
        }
        
        Block block = blocks.get(segmentIndex);
        double stability = block.getStability();
        
        // Color basado en estabilidad con variaciones por altura
        if (stability >= 0.95) {
            return SEGMENT_PERFECT;     // Oro - Perfecto
        } else if (stability >= 0.85) {
            return SEGMENT_EXCELLENT;   // Verde - Excelente
        } else if (stability >= 0.70) {
            return SEGMENT_GOOD;        // Naranja - Bueno
        } else if (stability >= 0.50) {
            return SEGMENT_WARNING;     // Rojo naranja - Advertencia
        } else {
            return SEGMENT_DANGER;      // Rojo - Peligro
        }
    }
}
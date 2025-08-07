package com.skillparty.towerblox.effects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Sistema de efectos visuales profesionales para Tower Bloxx
 * Incluye partículas, animaciones y efectos especiales
 */
public class ProfessionalEffects {
    
    private List<VisualEffect> activeEffects;
    private Random random;
    
    // Configuración de efectos
    private static final int MAX_PARTICLES = 200;
    private static final Color[] SPARK_COLORS = {
        Color.YELLOW, Color.ORANGE, new Color(255, 215, 0), Color.WHITE
    };
    
    public ProfessionalEffects() {
        this.activeEffects = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Efecto base para todos los efectos visuales
     */
    private abstract static class VisualEffect {
        protected double x, y;
        protected long startTime;
        protected long duration;
        protected boolean expired;
        
        public VisualEffect(double x, double y, long duration) {
            this.x = x;
            this.y = y;
            this.startTime = System.currentTimeMillis();
            this.duration = duration;
            this.expired = false;
        }
        
        public abstract void update(long deltaTime);
        public abstract void render(Graphics2D g2d);
        
        public boolean isExpired() {
            return expired || (System.currentTimeMillis() - startTime) > duration;
        }
        
        protected double getProgress() {
            return Math.min(1.0, (System.currentTimeMillis() - startTime) / (double)duration);
        }
    }
    
    /**
     * Efecto de chispas para colocación perfecta
     */
    private static class SparkEffect extends VisualEffect {
        private List<Spark> sparks;
        
        private static class Spark {
            double x, y, vx, vy;
            Color color;
            double life;
            
            Spark(double x, double y, double vx, double vy, Color color) {
                this.x = x;
                this.y = y;
                this.vx = vx;
                this.vy = vy;
                this.color = color;
                this.life = 1.0;
            }
        }
        
        public SparkEffect(double x, double y) {
            super(x, y, 1500);
            this.sparks = new ArrayList<>();
            
            // Crear chispas
            Random rand = new Random();
            for (int i = 0; i < 15; i++) {
                double angle = rand.nextDouble() * 2 * Math.PI;
                double speed = 2 + rand.nextDouble() * 4;
                double vx = Math.cos(angle) * speed;
                double vy = Math.sin(angle) * speed - 2; // Bias hacia arriba
                
                Color color = SPARK_COLORS[rand.nextInt(SPARK_COLORS.length)];
                sparks.add(new Spark(x, y, vx, vy, color));
            }
        }
        
        @Override
        public void update(long deltaTime) {
            double dt = deltaTime / 1000.0;
            
            for (Spark spark : sparks) {
                spark.x += spark.vx * dt * 60;
                spark.y += spark.vy * dt * 60;
                spark.vy += 9.8 * dt; // Gravedad
                spark.life -= dt / 1.5; // Desvanecimiento
            }
            
            sparks.removeIf(spark -> spark.life <= 0);
            if (sparks.isEmpty()) expired = true;
        }
        
        @Override
        public void render(Graphics2D g2d) {
            for (Spark spark : sparks) {
                float alpha = (float)Math.max(0, spark.life);
                Color color = new Color(
                    spark.color.getRed(),
                    spark.color.getGreen(),
                    spark.color.getBlue(),
                    (int)(alpha * 255)
                );
                
                g2d.setColor(color);
                g2d.fillOval((int)spark.x - 2, (int)spark.y - 2, 4, 4);
                
                // Estela de la chispa
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine((int)spark.x, (int)spark.y, 
                           (int)(spark.x - spark.vx * 0.5), 
                           (int)(spark.y - spark.vy * 0.5));
            }
        }
    }
    
    /**
     * Efecto de ondas de impacto
     */
    private static class ShockwaveEffect extends VisualEffect {
        private double maxRadius;
        private Color color;
        
        public ShockwaveEffect(double x, double y, double maxRadius, Color color) {
            super(x, y, 800);
            this.maxRadius = maxRadius;
            this.color = color;
        }
        
        @Override
        public void update(long deltaTime) {
            // El progreso se calcula automáticamente
        }
        
        @Override
        public void render(Graphics2D g2d) {
            double progress = getProgress();
            double currentRadius = maxRadius * progress;
            float alpha = (float)(1.0 - progress);
            
            Color waveColor = new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                (int)(alpha * 100)
            );
            
            g2d.setColor(waveColor);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int diameter = (int)(currentRadius * 2);
            g2d.drawOval((int)(x - currentRadius), (int)(y - currentRadius), diameter, diameter);
            
            // Onda interior más intensa
            if (progress < 0.7) {
                double innerRadius = currentRadius * 0.6;
                Color innerColor = new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    (int)(alpha * 150)
                );
                g2d.setColor(innerColor);
                g2d.setStroke(new BasicStroke(2));
                int innerDiameter = (int)(innerRadius * 2);
                g2d.drawOval((int)(x - innerRadius), (int)(y - innerRadius), innerDiameter, innerDiameter);
            }
        }
    }
    
    /**
     * Efecto de texto flotante con animación
     */
    private static class FloatingTextEffect extends VisualEffect {
        private String text;
        private Color color;
        private Font font;
        private double startY;
        
        public FloatingTextEffect(double x, double y, String text, Color color, Font font) {
            super(x, y, 2000);
            this.text = text;
            this.color = color;
            this.font = font;
            this.startY = y;
        }
        
        @Override
        public void update(long deltaTime) {
            double progress = getProgress();
            y = startY - (progress * 60); // Flotar hacia arriba
        }
        
        @Override
        public void render(Graphics2D g2d) {
            double progress = getProgress();
            float alpha = (float)(1.0 - progress);
            
            // Efecto de escala
            double scale = 1.0 + Math.sin(progress * Math.PI) * 0.3;
            
            AffineTransform oldTransform = g2d.getTransform();
            g2d.translate(x, y);
            g2d.scale(scale, scale);
            
            // Sombra del texto
            g2d.setColor(new Color(0, 0, 0, (int)(alpha * 100)));
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, -textWidth/2 + 2, 2);
            
            // Texto principal
            Color textColor = new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                (int)(alpha * 255)
            );
            g2d.setColor(textColor);
            g2d.drawString(text, -textWidth/2, 0);
            
            g2d.setTransform(oldTransform);
        }
    }
    
    /**
     * Efecto de combo con animación especial
     */
    private static class ComboEffect extends VisualEffect {
        private int comboCount;
        private double pulsePhase;
        
        public ComboEffect(double x, double y, int comboCount) {
            super(x, y, 1500);
            this.comboCount = comboCount;
            this.pulsePhase = 0;
        }
        
        @Override
        public void update(long deltaTime) {
            pulsePhase += deltaTime * 0.01;
        }
        
        @Override
        public void render(Graphics2D g2d) {
            double progress = getProgress();
            float alpha = (float)(1.0 - progress);
            
            // Efecto de pulso
            double pulse = 1.0 + Math.sin(pulsePhase) * 0.2;
            double scale = pulse * (1.5 - progress * 0.5);
            
            AffineTransform oldTransform = g2d.getTransform();
            g2d.translate(x, y);
            g2d.scale(scale, scale);
            
            // Fondo del combo
            g2d.setColor(new Color(255, 215, 0, (int)(alpha * 150)));
            g2d.fillRoundRect(-40, -15, 80, 30, 15, 15);
            
            // Borde del combo
            g2d.setColor(new Color(255, 165, 0, (int)(alpha * 255)));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(-40, -15, 80, 30, 15, 15);
            
            // Texto del combo
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
            String comboText = "COMBO x" + comboCount;
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(comboText);
            g2d.drawString(comboText, -textWidth/2, 5);
            
            g2d.setTransform(oldTransform);
        }
    }
    
    /**
     * Agrega efecto de chispas para colocación perfecta
     */
    public void addPerfectPlacementEffect(double x, double y) {
        if (activeEffects.size() < MAX_PARTICLES) {
            activeEffects.add(new SparkEffect(x, y));
            activeEffects.add(new ShockwaveEffect(x, y, 50, Color.GREEN));
        }
    }
    
    /**
     * Agrega efecto de colocación buena
     */
    public void addGoodPlacementEffect(double x, double y) {
        if (activeEffects.size() < MAX_PARTICLES) {
            activeEffects.add(new ShockwaveEffect(x, y, 30, Color.YELLOW));
        }
    }
    
    /**
     * Agrega efecto de puntuación flotante
     */
    public void addScoreEffect(double x, double y, int score) {
        if (activeEffects.size() < MAX_PARTICLES) {
            Color scoreColor = score > 1000 ? Color.YELLOW : Color.WHITE;
            Font scoreFont = new Font("Arial", Font.BOLD, score > 1000 ? 18 : 14);
            activeEffects.add(new FloatingTextEffect(x, y, "+" + score, scoreColor, scoreFont));
        }
    }
    
    /**
     * Agrega efecto de combo
     */
    public void addComboEffect(double x, double y, int combo) {
        if (activeEffects.size() < MAX_PARTICLES && combo > 1) {
            activeEffects.add(new ComboEffect(x, y, combo));
        }
    }
    
    /**
     * Agrega efecto de hito alcanzado
     */
    public void addMilestoneEffect(double x, double y, int milestone) {
        if (activeEffects.size() < MAX_PARTICLES) {
            activeEffects.add(new SparkEffect(x, y));
            activeEffects.add(new ShockwaveEffect(x, y, 80, Color.CYAN));
            
            Font milestoneFont = new Font("Arial", Font.BOLD, 20);
            String milestoneText = "FLOOR " + milestone + "!";
            activeEffects.add(new FloatingTextEffect(x, y - 30, milestoneText, Color.CYAN, milestoneFont));
        }
    }
    
    /**
     * Actualiza todos los efectos activos
     */
    public void update(long deltaTime) {
        Iterator<VisualEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            VisualEffect effect = iterator.next();
            effect.update(deltaTime);
            
            if (effect.isExpired()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Renderiza todos los efectos activos
     */
    public void render(Graphics2D g2d) {
        for (VisualEffect effect : activeEffects) {
            effect.render(g2d);
        }
    }
    
    /**
     * Limpia todos los efectos
     */
    public void clear() {
        activeEffects.clear();
    }
    
    /**
     * Obtiene el número de efectos activos
     */
    public int getActiveEffectCount() {
        return activeEffects.size();
    }
}
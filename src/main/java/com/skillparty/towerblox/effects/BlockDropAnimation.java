package com.skillparty.towerblox.effects;

import com.skillparty.towerblox.game.physics.Block;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Animación avanzada para la caída de bloques con efectos realistas
 */
public class BlockDropAnimation {
    private Block block;
    private double velocityY;
    private double accelerationY;
    private double rotation;
    private double rotationSpeed;
    
    // Efectos visuales
    private TrailEffect trailEffect;
    private ShadowEffect shadowEffect;
    private SquashEffect squashEffect;
    
    // Parámetros de animación
    private static final double GRAVITY = 800.0; // pixels/s²
    private static final double TERMINAL_VELOCITY = 600.0; // pixels/s
    private static final double AIR_RESISTANCE = 0.02;
    
    // Efectos de rebote y impacto
    private boolean hasLanded;
    private double bounceVelocity;
    private int bounceCount;
    private static final int MAX_BOUNCES = 2;
    
    // Efectos de partículas
    private ImpactParticleSystem impactParticles;
    
    private Random random = new Random();
    
    public BlockDropAnimation(Block block) {
        this.block = block;
        this.velocityY = 0;
        this.accelerationY = GRAVITY;
        this.rotation = 0;
        this.rotationSpeed = (random.nextDouble() - 0.5) * 2.0; // Rotación aleatoria suave
        this.hasLanded = false;
        this.bounceCount = 0;
        
        // Inicializar efectos
        this.trailEffect = new TrailEffect();
        this.shadowEffect = new ShadowEffect();
        this.squashEffect = new SquashEffect();
        this.impactParticles = new ImpactParticleSystem();
    }
    
    public void update(double deltaTime) {
        if (hasLanded && bounceCount >= MAX_BOUNCES) {
            return; // Animación terminada
        }
        
        // Aplicar gravedad
        velocityY += accelerationY * deltaTime;
        
        // Aplicar resistencia del aire
        velocityY *= (1.0 - AIR_RESISTANCE);
        
        // Limitar velocidad terminal
        if (velocityY > TERMINAL_VELOCITY) {
            velocityY = TERMINAL_VELOCITY;
        }
        
        // Actualizar posición
        double oldY = block.getY();
        block.setY(oldY + velocityY * deltaTime);
        
        // Actualizar rotación
        rotation += rotationSpeed * deltaTime;
        
        // Actualizar efectos
        trailEffect.update(deltaTime, block.getX() + block.getWidth()/2, 
                          block.getY() + block.getHeight()/2, velocityY);
        shadowEffect.update(deltaTime, block, velocityY);
        squashEffect.update(deltaTime, velocityY, hasLanded);
        
        if (hasLanded) {
            impactParticles.update(deltaTime);
        }
    }
    
    public void render(Graphics2D g2d, double cameraY) {
        // Renderizar sombra primero
        shadowEffect.render(g2d, cameraY);
        
        // Renderizar trail del bloque
        trailEffect.render(g2d, cameraY);
        
        // Guardar transformación original
        AffineTransform originalTransform = g2d.getTransform();
        
        // Aplicar efectos de squash and stretch
        double scaleX = squashEffect.getScaleX();
        double scaleY = squashEffect.getScaleY();
        
        // Calcular centro del bloque para rotación
        double centerX = block.getX() + block.getWidth() / 2.0;
        double centerY = block.getY() + block.getHeight() / 2.0 - cameraY;
        
        // Aplicar transformaciones
        g2d.translate(centerX, centerY);
        g2d.rotate(Math.toRadians(rotation));
        g2d.scale(scaleX, scaleY);
        g2d.translate(-block.getWidth() / 2.0, -block.getHeight() / 2.0);
        
        // Renderizar el bloque con efectos de brillo
        renderBlockWithEffects(g2d);
        
        // Restaurar transformación
        g2d.setTransform(originalTransform);
        
        // Renderizar partículas de impacto
        if (hasLanded) {
            impactParticles.render(g2d, cameraY);
        }
    }
    
    private void renderBlockWithEffects(Graphics2D g2d) {
        // Efecto de brillo basado en velocidad
        float glowIntensity = (float) Math.min(velocityY / TERMINAL_VELOCITY, 1.0);
        
        // Color base del bloque con brillo
        Color baseColor = block.getColor();
        Color glowColor = new Color(
            Math.min(255, baseColor.getRed() + (int)(50 * glowIntensity)),
            Math.min(255, baseColor.getGreen() + (int)(50 * glowIntensity)),
            Math.min(255, baseColor.getBlue() + (int)(50 * glowIntensity))
        );
        
        // Renderizar resplandor exterior
        if (glowIntensity > 0.3) {
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), 
                                  glowColor.getBlue(), (int)(100 * glowIntensity)));
            g2d.fillRoundRect(-2, -2, (int)block.getWidth() + 4, 
                             (int)block.getHeight() + 4, 8, 8);
        }
        
        // Renderizar bloque principal
        g2d.setColor(glowColor);
        g2d.fillRoundRect(0, 0, (int)block.getWidth(), (int)block.getHeight(), 5, 5);
        
        // Borde del bloque
        g2d.setColor(baseColor.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, (int)block.getWidth(), (int)block.getHeight(), 5, 5);
        
        // Efecto de reflexión
        GradientPaint reflection = new GradientPaint(
            0, 0, new Color(255, 255, 255, 80),
            0, (float)block.getHeight() / 3, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(reflection);
        g2d.fillRoundRect(2, 2, (int)block.getWidth() - 4, 
                         (int)block.getHeight() / 3, 3, 3);
    }
    
    public void triggerLanding(double groundY) {
        if (!hasLanded) {
            hasLanded = true;
            
            // Calcular rebote basado en velocidad de impacto
            bounceVelocity = -velocityY * 0.3; // 30% de la velocidad de impacto
            velocityY = bounceVelocity;
            
            // Ajustar posición al suelo
            block.setY(groundY - block.getHeight());
            
            // Activar efectos de impacto
            squashEffect.triggerImpact();
            impactParticles.triggerImpact(block.getX() + block.getWidth()/2, 
                                        groundY, velocityY);
            
            bounceCount++;
        }
    }
    
    public boolean isAnimationComplete() {
        return hasLanded && bounceCount >= MAX_BOUNCES && 
               Math.abs(velocityY) < 10 && Math.abs(bounceVelocity) < 10;
    }
    
    public double getVelocityY() {
        return velocityY;
    }
    
    public boolean hasLanded() {
        return hasLanded;
    }
    
    // Clase interna para efecto de rastro
    private static class TrailEffect {
        private java.util.List<TrailPoint> points = new java.util.ArrayList<>();
        private static final int MAX_POINTS = 8;
        
        private static class TrailPoint {
            double x, y;
            long time;
            float alpha;
            
            TrailPoint(double x, double y, long time) {
                this.x = x; this.y = y; this.time = time;
                this.alpha = 1.0f;
            }
        }
        
        public void update(double deltaTime, double x, double y, double velocity) {
            // Agregar nuevo punto solo si hay movimiento significativo
            if (velocity > 50) {
                points.add(0, new TrailPoint(x, y, System.currentTimeMillis()));
            }
            
            // Remover puntos antiguos
            points.removeIf(p -> System.currentTimeMillis() - p.time > 500);
            
            // Limitar número de puntos
            while (points.size() > MAX_POINTS) {
                points.remove(points.size() - 1);
            }
            
            // Actualizar alpha de los puntos
            for (int i = 0; i < points.size(); i++) {
                TrailPoint p = points.get(i);
                float age = (System.currentTimeMillis() - p.time) / 500.0f;
                p.alpha = Math.max(0, 1.0f - age);
            }
        }
        
        public void render(Graphics2D g2d, double cameraY) {
            for (int i = 1; i < points.size(); i++) {
                TrailPoint p1 = points.get(i - 1);
                TrailPoint p2 = points.get(i);
                
                float alpha = Math.min(p1.alpha, p2.alpha);
                if (alpha <= 0) continue;
                
                g2d.setColor(new Color(255, 255, 255, (int)(alpha * 100)));
                g2d.setStroke(new BasicStroke(3 * alpha));
                g2d.drawLine((int)p1.x, (int)(p1.y - cameraY), 
                           (int)p2.x, (int)(p2.y - cameraY));
            }
        }
    }
    
    // Clase interna para efecto de sombra
    private static class ShadowEffect {
        private double shadowOpacity = 0.3;
        
        public void update(double deltaTime, Block block, double velocity) {
            // La sombra es más intensa cuanto más rápido cae el bloque
            shadowOpacity = 0.2 + (velocity / TERMINAL_VELOCITY) * 0.3;
        }
        
        public void render(Graphics2D g2d, double cameraY) {
            // Implementación básica de sombra
            // La sombra se renderizaría en el suelo proyectada
        }
    }
    
    // Clase interna para efecto de squash and stretch
    private static class SquashEffect {
        private double scaleX = 1.0;
        private double scaleY = 1.0;
        private boolean impactTriggered = false;
        private long impactTime = 0;
        
        public void update(double deltaTime, double velocity, boolean hasLanded) {
            if (impactTriggered && hasLanded) {
                // Efecto de squash en el impacto
                long elapsed = System.currentTimeMillis() - impactTime;
                if (elapsed < 200) {
                    double t = elapsed / 200.0;
                    // Squash effect: más ancho, menos alto
                    scaleX = 1.0 + 0.3 * (1.0 - t);
                    scaleY = 1.0 - 0.2 * (1.0 - t);
                } else {
                    scaleX = 1.0;
                    scaleY = 1.0;
                    impactTriggered = false;
                }
            } else {
                // Efecto de stretch durante la caída
                double stretchFactor = Math.min(velocity / TERMINAL_VELOCITY, 1.0);
                scaleX = 1.0 - stretchFactor * 0.1;
                scaleY = 1.0 + stretchFactor * 0.2;
            }
        }
        
        public void triggerImpact() {
            impactTriggered = true;
            impactTime = System.currentTimeMillis();
        }
        
        public double getScaleX() { return scaleX; }
        public double getScaleY() { return scaleY; }
    }
    
    // Clase interna para sistema de partículas de impacto
    private static class ImpactParticleSystem {
        private java.util.List<Particle> particles = new java.util.ArrayList<>();
        private Random random = new Random();
        
        private static class Particle {
            double x, y, vx, vy;
            Color color;
            long life, maxLife;
            double size;
            
            Particle(double x, double y, double vx, double vy, Color color, long maxLife) {
                this.x = x; this.y = y; this.vx = vx; this.vy = vy;
                this.color = color; this.maxLife = maxLife;
                this.life = maxLife;
                this.size = 2 + Math.random() * 3;
            }
        }
        
        public void triggerImpact(double x, double y, double impactVelocity) {
            int particleCount = (int)(5 + impactVelocity / 100);
            
            for (int i = 0; i < particleCount; i++) {
                double angle = random.nextDouble() * Math.PI; // Solo hacia arriba
                double speed = 50 + random.nextDouble() * 100;
                double vx = Math.cos(angle) * speed * (random.nextBoolean() ? 1 : -1);
                double vy = -Math.sin(angle) * speed;
                
                Color color = new Color(200 + random.nextInt(55), 
                                       150 + random.nextInt(105),
                                       100 + random.nextInt(55));
                
                particles.add(new Particle(x, y, vx, vy, color, 1000 + random.nextInt(500)));
            }
        }
        
        public void update(double deltaTime) {
            particles.removeIf(p -> p.life <= 0);
            
            for (Particle p : particles) {
                p.x += p.vx * deltaTime;
                p.y += p.vy * deltaTime;
                p.vy += 400 * deltaTime; // Gravedad en las partículas
                p.life -= deltaTime * 1000;
            }
        }
        
        public void render(Graphics2D g2d, double cameraY) {
            for (Particle p : particles) {
                float alpha = (float)p.life / p.maxLife;
                Color color = new Color(p.color.getRed(), p.color.getGreen(), 
                                       p.color.getBlue(), (int)(alpha * 255));
                g2d.setColor(color);
                g2d.fillOval((int)(p.x - p.size), (int)(p.y - cameraY - p.size), 
                           (int)(p.size * 2), (int)(p.size * 2));
            }
        }
    }
}

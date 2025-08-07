package com.skillparty.towerblox.game;

import com.skillparty.towerblox.game.physics.Block;
import com.skillparty.towerblox.game.physics.Crane;
import com.skillparty.towerblox.game.physics.Tower;
import com.skillparty.towerblox.game.camera.CameraSystem;
import com.skillparty.towerblox.performance.PerformanceMonitor;
import com.skillparty.towerblox.utils.BlockPool;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

/**
 * Enhanced Game Engine that integrates all improvements
 * while maintaining compatibility with existing code
 * 
 * @author joseAlejandro - 2025 Professional Edition
 */
public class EnhancedGameEngine implements KeyListener {
    
    // Core enhanced systems
    private CameraSystem camera;
    private PerformanceMonitor performanceMonitor;
    private BlockPool blockPool;
    private boolean showPerformanceOverlay = false;
    private boolean advancedPhysicsEnabled = true;
    
    // Original game objects (for compatibility)
    private GameEngine originalEngine;
    
    // Enhanced features
    private Map<String, Object> enhancements = new HashMap<>();
    private long lastUpdateTime = System.currentTimeMillis();
    private double deltaTime = 0;
    
    // Visual enhancements
    private java.util.List<VisualEffect> effects = new ArrayList<>();
    private Random random = new Random();
    
    // Game constants
    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 720;
    private static final int GROUND_LEVEL = GAME_HEIGHT - 80;
    
    public EnhancedGameEngine(GameEngine originalEngine) {
        this.originalEngine = originalEngine;
        initializeEnhancements();
        
        System.out.println("🚀 ===== ENHANCED GAME ENGINE 2025 INITIALIZED =====");
        System.out.println("📸 Advanced Camera System: ✅ Active");
        System.out.println("📊 Performance Monitoring: ✅ Active");
        System.out.println("🧱 Object Pooling System: ✅ Active");
        System.out.println("✨ Visual Enhancements: ✅ Active");
        System.out.println("⚡ Advanced Physics: ✅ Active");
        System.out.println("════════════════════════════════════════════════════");
    }
    
    private void initializeEnhancements() {
        // Initialize camera system
        camera = new CameraSystem(GAME_WIDTH, GAME_HEIGHT);
        
        // Initialize performance monitoring
        performanceMonitor = new PerformanceMonitor();
        
        // Initialize object pooling
        blockPool = new BlockPool(100, 300);
        
        // Store original methods for compatibility
        enhancements.put("originalUpdate", originalEngine);
        enhancements.put("originalRender", originalEngine);
        
        System.out.println("✅ Enhanced systems initialized successfully");
    }
    
    /**
     * Enhanced update method
     */
    public void update() {
        // Calculate delta time
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        deltaTime = Math.min(deltaTime, 1.0 / 30.0); // Cap at 30 FPS minimum
        long deltaTimeMs = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        
        // Update performance monitoring
        performanceMonitor.update();
        
        // Call original update with proper deltaTime
        if (originalEngine != null) {
            originalEngine.update(deltaTimeMs);
        }
        
        // Update enhanced systems
        updateEnhancedSystems();
        
        // Performance optimization
        optimizePerformance();
    }
    
    private void updateEnhancedSystems() {
        // Update camera system
        updateAdvancedCamera();
        
        // Update visual effects
        updateVisualEffects();
        
        // Update block pool statistics
        checkPoolHealth();
    }
    
    private void updateAdvancedCamera() {
        if (originalEngine != null && originalEngine.getTower() != null) {
            Tower tower = originalEngine.getTower();
            
            // Calculate tower center and height
            double towerCenterX = GAME_WIDTH / 2.0; // Default center
            double towerHeight = tower.getBlocks().size() * 35; // Approximate height
            
            // Follow tower with smooth camera
            camera.followTower(towerCenterX, towerHeight);
        }
        
        camera.update();
    }
    
    private void updateVisualEffects() {
        // Update existing effects
        effects.removeIf(effect -> effect.update(deltaTime));
        
        // Add new effects based on game state
        if (random.nextDouble() < 0.02) { // 2% chance per frame
            addEnvironmentalEffect();
        }
    }
    
    private void checkPoolHealth() {
        if (blockPool.isLow()) {
            System.out.println("⚠️ Block pool running low: " + 
                             blockPool.getAvailableCount() + "/" + blockPool.getTotalCount());
        }
    }
    
    private void optimizePerformance() {
        double fps = performanceMonitor.getCurrentFPS();
        
        if (fps < 25) {
            // Reduce visual effects
            if (effects.size() > 10) {
                effects.subList(0, effects.size() / 2).clear();
            }
            System.out.println("⚡ Performance optimization: Reduced effects");
        }
    }
    
    /**
     * Enhanced render method with professional visuals
     */
    public void render(Graphics2D g2d) {
        // Enable high-quality rendering
        enableProfessionalRendering(g2d);
        
        // Apply camera transformation
        AffineTransform originalTransform = g2d.getTransform();
        camera.applyTransform(g2d);
        
        // Render enhanced background
        renderEnhancedBackground(g2d);
        
        // Call original render (this will render the main game content)
        if (originalEngine != null) {
            originalEngine.render(g2d);
        }
        
        // Render enhanced visual effects
        renderEnhancedEffects(g2d);
        
        // Reset camera transformation
        camera.resetTransform(g2d, originalTransform);
        
        // Render enhanced UI
        renderEnhancedUI(g2d);
        
        // Render performance overlay
        if (showPerformanceOverlay) {
            renderProfessionalPerformanceOverlay(g2d);
        }
    }
    
    private void enableProfessionalRendering(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }
    
    private void renderEnhancedBackground(Graphics2D g2d) {
        // Professional sky gradient
        GradientPaint skyGradient = new GradientPaint(
            0, -1000, new Color(135, 206, 250, 200),
            0, GROUND_LEVEL, new Color(176, 224, 230, 150)
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(-2000, -2000, 4000, 4000);
        
        // Enhanced ground
        renderProfessionalGround(g2d);
    }
    
    private void renderProfessionalGround(Graphics2D g2d) {
        // Main ground with gradient
        GradientPaint groundGradient = new GradientPaint(
            0, GROUND_LEVEL, new Color(34, 139, 34),
            0, GROUND_LEVEL + 100, new Color(25, 100, 25)
        );
        g2d.setPaint(groundGradient);
        g2d.fillRect(-2000, GROUND_LEVEL, 4000, 200);
        
        // Ground texture
        g2d.setColor(new Color(44, 160, 44, 100));
        g2d.setStroke(new BasicStroke(1.5f));
        for (int x = -2000; x < 2000; x += 30) {
            g2d.drawLine(x, GROUND_LEVEL, x + 15, GROUND_LEVEL + 8);
        }
        
        // Ground shine effect
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fillRect(-2000, GROUND_LEVEL, 4000, 5);
    }
    
    private void renderEnhancedEffects(Graphics2D g2d) {
        // Render all visual effects
        for (VisualEffect effect : effects) {
            effect.render(g2d);
        }
        
        // Add subtle ambient particles
        renderAmbientParticles(g2d);
    }
    
    private void renderAmbientParticles(Graphics2D g2d) {
        // Floating dust particles in sunlight
        g2d.setColor(new Color(255, 255, 200, 50));
        long time = System.currentTimeMillis();
        
        for (int i = 0; i < 20; i++) {
            double x = (Math.sin(time * 0.001 + i) * 200) + GAME_WIDTH / 2.0;
            double y = (Math.sin(time * 0.0008 + i * 0.5) * 100) + GAME_HEIGHT / 3.0;
            
            // Apply camera transformation to particles
            x = camera.worldToScreenX(x);
            y = camera.worldToScreenY(y);
            
            if (x >= 0 && x <= GAME_WIDTH && y >= 0 && y <= GAME_HEIGHT) {
                g2d.fillOval((int)x, (int)y, 2, 2);
            }
        }
    }
    
    private void renderEnhancedUI(Graphics2D g2d) {
        // Professional UI overlay
        renderProfessionalHUD(g2d);
        
        // Enhanced controls
        renderAdvancedControls(g2d);
    }
    
    private void renderProfessionalHUD(Graphics2D g2d) {
        // Professional HUD background
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(20, 20, 300, 100, 15, 15);
        
        // HUD border
        g2d.setColor(new Color(100, 150, 255, 200));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(20, 20, 300, 100, 15, 15);
        
        // HUD content
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        
        if (originalEngine != null && originalEngine.getScoreManager() != null) {
            g2d.drawString("Score: " + originalEngine.getScoreManager().getCurrentScore(), 35, 45);
        }
        
        if (originalEngine != null && originalEngine.getTower() != null) {
            g2d.drawString("Blocks: " + originalEngine.getTower().getBlocks().size(), 35, 70);
        }
        
        g2d.drawString("Camera: " + String.format("%.0fx%.0f", camera.getX(), camera.getY()), 35, 95);
        
        // FPS indicator with color coding
        double fps = performanceMonitor.getCurrentFPS();
        if (fps > 50) {
            g2d.setColor(Color.GREEN);
        } else if (fps > 30) {
            g2d.setColor(Color.YELLOW);
        } else {
            g2d.setColor(Color.RED);
        }
        g2d.drawString("FPS: " + String.format("%.0f", fps), 200, 45);
    }
    
    private void renderAdvancedControls(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(255, 255, 255, 180));
        
        String[] controls = {
            "SPACE: Drop Block | ESC: Pause",
            "F1: Performance Overlay | F2: Camera Reset",
            "F3: Advanced Physics | F4: Visual Effects"
        };
        
        for (int i = 0; i < controls.length; i++) {
            g2d.drawString(controls[i], 20, GAME_HEIGHT - 60 + i * 15);
        }
    }
    
    private void renderProfessionalPerformanceOverlay(Graphics2D g2d) {
        // Professional performance overlay
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(GAME_WIDTH - 350, 20, 330, 200, 10, 10);
        
        // Border
        g2d.setColor(new Color(0, 255, 128, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(GAME_WIDTH - 350, 20, 330, 200, 10, 10);
        
        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString("⚡ PERFORMANCE MONITOR 2025", GAME_WIDTH - 340, 40);
        
        // Stats
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        int y = 60;
        int lineHeight = 15;
        
        g2d.drawString(String.format("FPS Current: %.2f", performanceMonitor.getCurrentFPS()), GAME_WIDTH - 340, y);
        y += lineHeight;
        g2d.drawString(String.format("FPS Average: %.2f", performanceMonitor.getAverageFPS()), GAME_WIDTH - 340, y);
        y += lineHeight;
        g2d.drawString(String.format("Frame Count: %d", performanceMonitor.getFrameCount()), GAME_WIDTH - 340, y);
        y += lineHeight;
        g2d.drawString(String.format("Block Pool: %d/%d", blockPool.getUsedCount(), blockPool.getTotalCount()), GAME_WIDTH - 340, y);
        y += lineHeight;
        g2d.drawString(String.format("Camera X: %.1f", camera.getX()), GAME_WIDTH - 340, y);
        y += lineHeight;
        g2d.drawString(String.format("Camera Y: %.1f", camera.getY()), GAME_WIDTH - 340, y);
        y += lineHeight;
        g2d.drawString(String.format("Zoom Level: %.2f", camera.getZoom()), GAME_WIDTH - 340, y);
        y += lineHeight;
        g2d.drawString(String.format("Effects: %d active", effects.size()), GAME_WIDTH - 340, y);
        y += lineHeight;
        
        // Performance status
        double fps = performanceMonitor.getCurrentFPS();
        if (fps > 50) {
            g2d.setColor(Color.GREEN);
            g2d.drawString("Status: EXCELLENT ✅", GAME_WIDTH - 340, y);
        } else if (fps > 30) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("Status: GOOD ⚡", GAME_WIDTH - 340, y);
        } else {
            g2d.setColor(Color.RED);
            g2d.drawString("Status: NEEDS OPTIMIZATION ⚠️", GAME_WIDTH - 340, y);
        }
    }
    
    private void addEnvironmentalEffect() {
        // Add random environmental effects
        int effectType = random.nextInt(3);
        switch (effectType) {
            case 0:
                effects.add(new CloudEffect());
                break;
            case 1:
                effects.add(new BirdEffect());
                break;
            case 2:
                effects.add(new SunbeamEffect());
                break;
        }
    }
    
    // =================== INPUT HANDLING ===================
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Handle enhanced features
        boolean handled = handleEnhancedInput(e);
        
        // If not handled by enhanced features, pass to original engine
        if (!handled && originalEngine != null) {
            originalEngine.keyPressed(e);
        }
    }
    
    private boolean handleEnhancedInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F1:
                showPerformanceOverlay = !showPerformanceOverlay;
                System.out.println("📊 Performance overlay: " + (showPerformanceOverlay ? "ON" : "OFF"));
                return true;
                
            case KeyEvent.VK_F2:
                camera.setPositionImmediate(0, 0);
                camera.setZoomImmediate(1.0);
                System.out.println("📸 Camera reset to default position");
                return true;
                
            case KeyEvent.VK_F3:
                advancedPhysicsEnabled = !advancedPhysicsEnabled;
                System.out.println("⚡ Advanced physics: " + (advancedPhysicsEnabled ? "ENABLED" : "DISABLED"));
                return true;
                
            case KeyEvent.VK_F4:
                // Add visual effect
                for (int i = 0; i < 5; i++) {
                    addEnvironmentalEffect();
                }
                System.out.println("✨ Added visual effects");
                return true;
                
            case KeyEvent.VK_F5:
                // Print performance stats
                printDetailedStats();
                return true;
                
            case KeyEvent.VK_F6:
                // Trigger camera shake for testing
                camera.shake(10, 500);
                System.out.println("📳 Camera shake test");
                return true;
        }
        return false;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (originalEngine != null) {
            originalEngine.keyReleased(e);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        if (originalEngine != null) {
            originalEngine.keyTyped(e);
        }
    }
    
    private void printDetailedStats() {
        System.out.println("\n🚀 ===== ENHANCED GAME ENGINE STATISTICS =====");
        System.out.println("Performance:");
        System.out.println("  Current FPS: " + String.format("%.2f", performanceMonitor.getCurrentFPS()));
        System.out.println("  Average FPS: " + String.format("%.2f", performanceMonitor.getAverageFPS()));
        System.out.println("  Frame Count: " + performanceMonitor.getFrameCount());
        System.out.println("Object Pooling:");
        System.out.println("  Pool Used: " + blockPool.getUsedCount());
        System.out.println("  Pool Available: " + blockPool.getAvailableCount());
        System.out.println("  Pool Total: " + blockPool.getTotalCount());
        System.out.println("  Pool Health: " + (blockPool.isLow() ? "LOW ⚠️" : "HEALTHY ✅"));
        System.out.println("Camera System:");
        System.out.println("  Position: " + String.format("(%.1f, %.1f)", camera.getX(), camera.getY()));
        System.out.println("  Zoom: " + String.format("%.2f", camera.getZoom()));
        System.out.println("  Is Shaking: " + (camera.isShaking() ? "YES" : "NO"));
        System.out.println("Visual Effects:");
        System.out.println("  Active Effects: " + effects.size());
        System.out.println("  Advanced Physics: " + (advancedPhysicsEnabled ? "ENABLED" : "DISABLED"));
        System.out.println("================================================\n");
    }
    
    // =================== DELEGATION METHODS ===================
    
    public boolean isRunning() { return originalEngine != null ? originalEngine.isRunning() : true; }
    public boolean isPaused() { return originalEngine != null ? originalEngine.isPaused() : false; }
    public GameState getCurrentState() { return originalEngine != null ? originalEngine.getCurrentState() : GameState.PLAYING; }
    public Tower getTower() { return originalEngine != null ? originalEngine.getTower() : null; }
    public Crane getCrane() { return originalEngine != null ? originalEngine.getCrane() : null; }
    
    // Enhanced getters
    public CameraSystem getCamera() { return camera; }
    public PerformanceMonitor getPerformanceMonitor() { return performanceMonitor; }
    public BlockPool getBlockPool() { return blockPool; }
    public boolean isShowingPerformanceOverlay() { return showPerformanceOverlay; }
    
    // =================== VISUAL EFFECT CLASSES ===================
    
    private abstract class VisualEffect {
        protected double x, y;
        protected double life, maxLife;
        protected double alpha = 1.0;
        
        public abstract boolean update(double deltaTime);
        public abstract void render(Graphics2D g2d);
    }
    
    private class CloudEffect extends VisualEffect {
        private double vx;
        
        public CloudEffect() {
            x = -100;
            y = 50 + random.nextDouble() * 100;
            vx = 20 + random.nextDouble() * 30;
            maxLife = life = 10 + random.nextDouble() * 5;
        }
        
        @Override
        public boolean update(double deltaTime) {
            x += vx * deltaTime;
            life -= deltaTime;
            alpha = life / maxLife;
            return x > GAME_WIDTH + 100 || life <= 0;
        }
        
        @Override
        public void render(Graphics2D g2d) {
            g2d.setColor(new Color(255, 255, 255, (int)(alpha * 100)));
            g2d.fillOval((int)x, (int)y, 60, 30);
            g2d.fillOval((int)x + 20, (int)y - 10, 40, 25);
            g2d.fillOval((int)x + 40, (int)y, 50, 28);
        }
    }
    
    private class BirdEffect extends VisualEffect {
        private double vx, vy;
        private double wingPhase = 0;
        
        public BirdEffect() {
            x = random.nextBoolean() ? -50 : GAME_WIDTH + 50;
            y = 30 + random.nextDouble() * 150;
            vx = (x < 0 ? 1 : -1) * (80 + random.nextDouble() * 40);
            vy = (random.nextDouble() - 0.5) * 20;
            maxLife = life = 8;
        }
        
        @Override
        public boolean update(double deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            wingPhase += deltaTime * 10;
            life -= deltaTime;
            alpha = Math.min(1.0, life / 2.0);
            return (x < -100 || x > GAME_WIDTH + 100) && life <= 0;
        }
        
        @Override
        public void render(Graphics2D g2d) {
            g2d.setColor(new Color(50, 50, 50, (int)(alpha * 150)));
            
            // Simple bird shape with animated wings
            double wingOffset = Math.sin(wingPhase) * 3;
            g2d.fillOval((int)(x - 5), (int)(y - wingOffset), 10, 4);
            g2d.fillOval((int)(x + 5), (int)(y + wingOffset), 10, 4);
            g2d.fillOval((int)x, (int)y, 6, 3); // Body
        }
    }
    
    private class SunbeamEffect extends VisualEffect {
        private double angle;
        private double width;
        
        public SunbeamEffect() {
            x = random.nextDouble() * GAME_WIDTH;
            y = -50;
            angle = random.nextDouble() * Math.PI / 4 - Math.PI / 8; // -22.5 to 22.5 degrees
            width = 20 + random.nextDouble() * 30;
            maxLife = life = 5 + random.nextDouble() * 3;
        }
        
        @Override
        public boolean update(double deltaTime) {
            life -= deltaTime;
            alpha = life / maxLife;
            return life <= 0;
        }
        
        @Override
        public void render(Graphics2D g2d) {
            // Sunbeam gradient
            Color startColor = new Color(255, 255, 200, (int)(alpha * 80));
            Color endColor = new Color(255, 255, 200, 0);
            
            GradientPaint gradient = new GradientPaint(
                (float)x, (float)y, startColor,
                (float)(x + Math.sin(angle) * 400), (float)(y + 400), endColor
            );
            
            g2d.setPaint(gradient);
            
            // Draw sunbeam as a polygon
            int[] xPoints = {
                (int)(x - width/2),
                (int)(x + width/2),
                (int)(x + Math.sin(angle) * 400 + width/2),
                (int)(x + Math.sin(angle) * 400 - width/2)
            };
            int[] yPoints = {
                (int)y,
                (int)y,
                (int)(y + 400),
                (int)(y + 400)
            };
            
            g2d.fillPolygon(xPoints, yPoints, 4);
        }
    }
}

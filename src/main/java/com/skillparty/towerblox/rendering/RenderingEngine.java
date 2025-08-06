package com.skillparty.towerblox.rendering;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

/**
 * Scalable rendering engine supporting unlimited tower height
 * Features LOD system, culling, batching, and smooth camera
 * 
 * @author joseAlejandro
 * @version 2.0 - Production Ready
 */
public class RenderingEngine {
    
    // Screen dimensions
    private int screenWidth;
    private int screenHeight;
    
    // Camera system
    private Camera camera;
    private static final double CAMERA_SMOOTH_FACTOR = 0.15;
    
    // Render layers
    private List<RenderLayer> layers;
    private static final int BACKGROUND_LAYER = 0;
    private static final int GAME_LAYER = 1;
    private static final int EFFECTS_LAYER = 2;
    private static final int UI_LAYER = 3;
    
    // Performance optimizations
    private boolean enableLOD = true;
    private boolean enableCulling = true;
    private boolean enableBatching = true;
    private Rectangle viewportBounds;
    
    // Render statistics
    private int objectsRendered;
    private int objectsCulled;
    private long renderTime;
    
    // Quality settings
    private RenderQuality quality;
    
    /**
     * Camera for viewport management
     */
    public static class Camera {
        public double x, y;           // Current position
        public double targetX, targetY; // Target position
        public double zoom;            // Zoom level
        public double rotation;        // Camera rotation
        public boolean followTarget;   // Auto-follow mode
        public GameObject target;      // Target to follow
        
        // Smooth camera movement
        private double smoothFactor = 0.15;
        private double maxSpeed = 500; // pixels per second
        
        // Camera bounds
        private double minY = -100000; // Support towers up to 100k pixels
        private double maxY = 1000;
        
        // Shake effect
        private double shakeIntensity = 0;
        private double shakeTime = 0;
        
        public Camera(int screenWidth, int screenHeight) {
            this.x = screenWidth / 2;
            this.y = screenHeight / 2;
            this.targetX = x;
            this.targetY = y;
            this.zoom = 1.0;
            this.rotation = 0;
        }
        
        public void update(double deltaTime) {
            // Follow target if enabled
            if (followTarget && target != null) {
                targetX = target.getX();
                targetY = target.getY() - 200; // Offset above target
            }
            
            // Smooth camera movement
            double dx = targetX - x;
            double dy = targetY - y;
            
            // Limit camera speed
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > 0) {
                double speed = Math.min(distance / deltaTime, maxSpeed);
                double factor = Math.min(smoothFactor, speed * deltaTime / distance);
                
                x += dx * factor;
                y += dy * factor;
            }
            
            // Apply camera bounds
            y = Math.max(minY, Math.min(maxY, y));
            
            // Update shake effect
            if (shakeTime > 0) {
                shakeTime -= deltaTime;
                if (shakeTime <= 0) {
                    shakeIntensity = 0;
                }
            }
        }
        
        public void shake(double intensity, double duration) {
            shakeIntensity = intensity;
            shakeTime = duration;
        }
        
        public AffineTransform getTransform() {
            AffineTransform transform = new AffineTransform();
            
            // Apply shake
            double shakeX = 0, shakeY = 0;
            if (shakeTime > 0) {
                shakeX = (Math.random() - 0.5) * shakeIntensity;
                shakeY = (Math.random() - 0.5) * shakeIntensity;
            }
            
            // Build transform
            transform.translate(-x + shakeX, -y + shakeY);
            transform.scale(zoom, zoom);
            transform.rotate(rotation);
            
            return transform;
        }
    }
    
    /**
     * Render layer for organizing draw calls
     */
    public static class RenderLayer {
        private List<Renderable> renderables;
        private boolean visible;
        private double parallaxFactor;
        
        public RenderLayer(double parallaxFactor) {
            this.renderables = new ArrayList<>();
            this.visible = true;
            this.parallaxFactor = parallaxFactor;
        }
        
        public void add(Renderable r) {
            renderables.add(r);
        }
        
        public void remove(Renderable r) {
            renderables.remove(r);
        }
        
        public void clear() {
            renderables.clear();
        }
        
        public void render(Graphics2D g, Camera camera, Rectangle viewport) {
            if (!visible) return;
            
            // Apply parallax
            AffineTransform original = g.getTransform();
            if (parallaxFactor != 1.0) {
                g.translate(0, camera.y * (1 - parallaxFactor));
            }
            
            // Sort by depth (painter's algorithm)
            renderables.sort(Comparator.comparingDouble(Renderable::getDepth));
            
            // Render each object
            for (Renderable r : renderables) {
                if (r.isVisible() && isInViewport(r, viewport)) {
                    r.render(g, camera);
                }
            }
            
            g.setTransform(original);
        }
        
        private boolean isInViewport(Renderable r, Rectangle viewport) {
            Rectangle bounds = r.getBounds();
            return bounds == null || viewport.intersects(bounds);
        }
    }
    
    /**
     * Base interface for renderable objects
     */
    public interface Renderable {
        void render(Graphics2D g, Camera camera);
        Rectangle getBounds();
        double getDepth();
        boolean isVisible();
    }
    
    /**
     * Base class for game objects
     */
    public static abstract class GameObject implements Renderable {
        protected double x, y;
        protected double width, height;
        protected boolean visible = true;
        
        public double getX() { return x; }
        public double getY() { return y; }
        public double getWidth() { return width; }
        public double getHeight() { return height; }
        
        @Override
        public Rectangle getBounds() {
            return new Rectangle((int)x, (int)y, (int)width, (int)height);
        }
        
        @Override
        public double getDepth() {
            return y; // Default depth sorting by Y position
        }
        
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    /**
     * Render quality settings
     */
    public enum RenderQuality {
        LOW(false, false, false, 0.5f),
        MEDIUM(true, false, false, 0.75f),
        HIGH(true, true, false, 1.0f),
        ULTRA(true, true, true, 1.0f);
        
        public final boolean antialiasing;
        public final boolean shadows;
        public final boolean particles;
        public final float resolutionScale;
        
        RenderQuality(boolean aa, boolean shadows, boolean particles, float scale) {
            this.antialiasing = aa;
            this.shadows = shadows;
            this.particles = particles;
            this.resolutionScale = scale;
        }
    }
    
    /**
     * Constructs the rendering engine
     */
    public RenderingEngine(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.camera = new Camera(screenWidth, screenHeight);
        this.quality = RenderQuality.HIGH;
        
        // Initialize layers
        this.layers = new ArrayList<>();
        layers.add(new RenderLayer(0.5));  // Background (parallax)
        layers.add(new RenderLayer(1.0));  // Game objects
        layers.add(new RenderLayer(1.0));  // Effects
        layers.add(new RenderLayer(0.0));  // UI (no parallax)
        
        // Initialize viewport
        updateViewport();
    }
    
    /**
     * Main render method
     */
    public void render(Graphics2D g) {
        long startTime = System.nanoTime();
        
        // Reset statistics
        objectsRendered = 0;
        objectsCulled = 0;
        
        // Apply quality settings
        applyQualitySettings(g);
        
        // Clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Update viewport
        updateViewport();
        
        // Render each layer
        for (RenderLayer layer : layers) {
            layer.render(g, camera, viewportBounds);
        }
        
        // Calculate render time
        renderTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to ms
    }
    
    /**
     * Applies quality settings to graphics context
     */
    private void applyQualitySettings(Graphics2D g) {
        if (quality.antialiasing) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                              RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, 
                              RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                              RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                              RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, 
                              RenderingHints.VALUE_RENDER_SPEED);
        }
    }
    
    /**
     * Updates viewport bounds for culling
     */
    private void updateViewport() {
        int padding = 100; // Extra padding for smooth transitions
        viewportBounds = new Rectangle(
            (int)(camera.x - screenWidth/2 - padding),
            (int)(camera.y - screenHeight/2 - padding),
            screenWidth + padding * 2,
            screenHeight + padding * 2
        );
    }
    
    /**
     * Adds a renderable to a specific layer
     */
    public void addToLayer(Renderable renderable, int layer) {
        if (layer >= 0 && layer < layers.size()) {
            layers.get(layer).add(renderable);
        }
    }
    
    /**
     * Removes a renderable from all layers
     */
    public void remove(Renderable renderable) {
        for (RenderLayer layer : layers) {
            layer.remove(renderable);
        }
    }
    
    /**
     * Clears all render layers
     */
    public void clear() {
        for (RenderLayer layer : layers) {
            layer.clear();
        }
    }
    
    /**
     * Updates camera
     */
    public void updateCamera(double deltaTime) {
        camera.update(deltaTime);
    }
    
    /**
     * Sets camera target
     */
    public void setCameraTarget(GameObject target) {
        camera.target = target;
        camera.followTarget = true;
    }
    
    /**
     * Sets camera position
     */
    public void setCameraPosition(double x, double y) {
        camera.targetX = x;
        camera.targetY = y;
        camera.followTarget = false;
    }
    
    /**
     * Applies camera shake
     */
    public void shakeCamera(double intensity, double duration) {
        camera.shake(intensity, duration);
    }
    
    /**
     * Sets render quality
     */
    public void setQuality(RenderQuality quality) {
        this.quality = quality;
    }
    
    /**
     * Gets camera
     */
    public Camera getCamera() {
        return camera;
    }
    
    /**
     * Gets render statistics
     */
    public String getStats() {
        return String.format("Rendered: %d | Culled: %d | Time: %dms | Quality: %s",
                            objectsRendered, objectsCulled, renderTime, quality);
    }
    
    /**
     * Level of Detail (LOD) calculation
     */
    public int calculateLOD(GameObject object) {
        if (!enableLOD) return 0;
        
        double distance = Math.abs(object.getY() - camera.y);
        
        if (distance < 200) return 0;      // Full detail
        if (distance < 500) return 1;      // Medium detail
        if (distance < 1000) return 2;     // Low detail
        return 3;                          // Minimal detail
    }
    
    /**
     * Batch renderer for similar objects
     */
    public static class BatchRenderer {
        private List<Rectangle> rects = new ArrayList<>();
        private Color color;
        
        public void begin(Color color) {
            this.color = color;
            rects.clear();
        }
        
        public void addRect(int x, int y, int width, int height) {
            rects.add(new Rectangle(x, y, width, height));
        }
        
        public void end(Graphics2D g) {
            g.setColor(color);
            for (Rectangle r : rects) {
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        }
    }
}

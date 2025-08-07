package com.skillparty.towerblox.performance;

/**
 * Monitor de performance del juego para detectar problemas
 * y optimizar el rendimiento
 * 
 * @author joseAlejandro
 */
public class PerformanceMonitor {
    private long lastFrameTime = System.nanoTime();
    private double currentFPS = 0.0;
    private double averageFPS = 0.0;
    private long frameCount = 0;
    private double totalFrameTime = 0.0;
    
    // Umbral de FPS para mostrar advertencias
    private static final double LOW_FPS_THRESHOLD = 30.0;
    private static final double TARGET_FPS = 60.0;
    
    /**
     * Actualiza las métricas de performance
     */
    public void update() {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0; // Convertir a segundos
        
        if (deltaTime > 0) {
            currentFPS = 1.0 / deltaTime;
            frameCount++;
            totalFrameTime += deltaTime;
            averageFPS = frameCount / totalFrameTime;
            
            // Mostrar advertencia si el FPS es bajo
            if (currentFPS < LOW_FPS_THRESHOLD && frameCount % 60 == 0) {
                System.out.printf("⚠️ Performance warning: FPS = %.2f (Target: %.0f)%n", 
                                currentFPS, TARGET_FPS);
            }
        }
        
        lastFrameTime = currentTime;
    }
    
    /**
     * Obtiene los FPS actuales
     */
    public double getCurrentFPS() {
        return currentFPS;
    }
    
    /**
     * Obtiene los FPS promedio
     */
    public double getAverageFPS() {
        return averageFPS;
    }
    
    /**
     * Obtiene el número total de frames
     */
    public long getFrameCount() {
        return frameCount;
    }
    
    /**
     * Verifica si el performance es aceptable
     */
    public boolean isPerformanceGood() {
        return currentFPS >= LOW_FPS_THRESHOLD;
    }
    
    /**
     * Reinicia las estadísticas
     */
    public void reset() {
        frameCount = 0;
        totalFrameTime = 0.0;
        currentFPS = 0.0;
        averageFPS = 0.0;
        lastFrameTime = System.nanoTime();
    }
    
    /**
     * Obtiene un reporte detallado de performance
     */
    public String getPerformanceReport() {
        return String.format(
            "🎮 Performance Report:%n" +
            "   Current FPS: %.2f%n" +
            "   Average FPS: %.2f%n" +
            "   Target FPS: %.0f%n" +
            "   Total Frames: %d%n" +
            "   Status: %s%n",
            currentFPS,
            averageFPS,
            TARGET_FPS,
            frameCount,
            isPerformanceGood() ? "✅ Good" : "⚠️ Needs optimization"
        );
    }
}

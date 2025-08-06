package com.skillparty.towerblox.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Professional game loop implementation with precise delta time management
 * Maintains stable 60 FPS with interpolation for smooth visuals
 * 
 * @author joseAlejandro
 * @version 2.0 - Production Ready
 */
public class GameLoop {
    
    // Constants for optimal performance
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS; // nanoseconds
    private static final int MAX_FRAME_SKIP = 5;
    private static final double INTERPOLATION_ALPHA = 0.8;
    
    // Loop control
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private ScheduledExecutorService executor;
    
    // Timing management
    private final AtomicLong lastUpdateTime = new AtomicLong();
    private final AtomicLong deltaAccumulator = new AtomicLong();
    private volatile double currentFPS = 0;
    private volatile double averageFPS = 0;
    private long frameCount = 0;
    
    // Performance monitoring
    private long updateDuration = 0;
    private long renderDuration = 0;
    private double cpuUsage = 0;
    private long memoryUsage = 0;
    
    // Callbacks
    private UpdateCallback updateCallback;
    private RenderCallback renderCallback;
    private PerformanceCallback performanceCallback;
    
    /**
     * Callback interfaces for game logic
     */
    public interface UpdateCallback {
        void update(double deltaTime);
    }
    
    public interface RenderCallback {
        void render(double interpolation);
    }
    
    public interface PerformanceCallback {
        void onPerformanceUpdate(double fps, double cpu, long memory);
    }
    
    /**
     * Constructs a new game loop
     */
    public GameLoop(UpdateCallback update, RenderCallback render) {
        this.updateCallback = update;
        this.renderCallback = render;
    }
    
    /**
     * Starts the game loop with adaptive timing
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            lastUpdateTime.set(System.nanoTime());
            executor = Executors.newScheduledThreadPool(2);
            
            // Main game loop with fixed timestep and interpolation
            executor.scheduleAtFixedRate(this::gameLoopCycle, 0, 16, TimeUnit.MILLISECONDS);
            
            // Performance monitoring thread
            executor.scheduleAtFixedRate(this::updatePerformanceMetrics, 0, 1, TimeUnit.SECONDS);
            
            System.out.println("🎮 Professional Game Loop Started - Target: " + TARGET_FPS + " FPS");
        }
    }
    
    /**
     * Core game loop cycle with fixed timestep
     */
    private void gameLoopCycle() {
        if (paused.get()) {
            return;
        }
        
        long currentTime = System.nanoTime();
        long elapsed = currentTime - lastUpdateTime.get();
        lastUpdateTime.set(currentTime);
        
        deltaAccumulator.addAndGet(elapsed);
        
        // Fixed timestep with frame skipping
        int loops = 0;
        while (deltaAccumulator.get() >= OPTIMAL_TIME && loops < MAX_FRAME_SKIP) {
            long updateStart = System.nanoTime();
            
            // Update game logic with fixed delta
            double deltaSeconds = OPTIMAL_TIME / 1_000_000_000.0;
            if (updateCallback != null) {
                updateCallback.update(deltaSeconds);
            }
            
            updateDuration = System.nanoTime() - updateStart;
            deltaAccumulator.addAndGet(-OPTIMAL_TIME);
            loops++;
        }
        
        // Render with interpolation for smooth visuals
        long renderStart = System.nanoTime();
        double interpolation = deltaAccumulator.get() / (double) OPTIMAL_TIME;
        
        if (renderCallback != null) {
            renderCallback.render(interpolation);
        }
        
        renderDuration = System.nanoTime() - renderStart;
        
        // Update FPS counter
        frameCount++;
        currentFPS = 1_000_000_000.0 / elapsed;
        
        // Calculate rolling average FPS
        averageFPS = averageFPS * 0.95 + currentFPS * 0.05;
    }
    
    /**
     * Updates performance metrics
     */
    private void updatePerformanceMetrics() {
        Runtime runtime = Runtime.getRuntime();
        memoryUsage = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024); // MB
        
        // Estimate CPU usage based on frame timings
        double totalFrameTime = (updateDuration + renderDuration) / 1_000_000.0; // ms
        cpuUsage = Math.min(100, (totalFrameTime / 16.67) * 100); // percentage of 60 FPS frame
        
        if (performanceCallback != null) {
            performanceCallback.onPerformanceUpdate(averageFPS, cpuUsage, memoryUsage);
        }
    }
    
    /**
     * Pauses the game loop
     */
    public void pause() {
        paused.set(true);
        System.out.println("⏸️ Game Loop Paused");
    }
    
    /**
     * Resumes the game loop
     */
    public void resume() {
        paused.set(false);
        lastUpdateTime.set(System.nanoTime());
        System.out.println("▶️ Game Loop Resumed");
    }
    
    /**
     * Stops the game loop
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            if (executor != null) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                }
            }
            System.out.println("⏹️ Game Loop Stopped");
        }
    }
    
    /**
     * Sets the performance monitoring callback
     */
    public void setPerformanceCallback(PerformanceCallback callback) {
        this.performanceCallback = callback;
    }
    
    /**
     * Gets current FPS
     */
    public double getCurrentFPS() {
        return averageFPS;
    }
    
    /**
     * Gets CPU usage percentage
     */
    public double getCPUUsage() {
        return cpuUsage;
    }
    
    /**
     * Gets memory usage in MB
     */
    public long getMemoryUsage() {
        return memoryUsage;
    }
    
    /**
     * Checks if loop is running
     */
    public boolean isRunning() {
        return running.get();
    }
    
    /**
     * Checks if loop is paused
     */
    public boolean isPaused() {
        return paused.get();
    }
    
    /**
     * Gets total frame count
     */
    public long getFrameCount() {
        return frameCount;
    }
    
    /**
     * Force garbage collection (use sparingly)
     */
    public void requestGarbageCollection() {
        System.gc();
        System.out.println("🗑️ Garbage Collection Requested");
    }
}

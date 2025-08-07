package com.skillparty.towerblox;

import com.skillparty.towerblox.integration.GameIntegration2025;

/**
 * Quick demo launcher for Tower Bloxx 2025 Professional Edition
 * This demonstrates all the improvements integrated into the game
 * 
 * @author joseAlejandro - 2025 Edition
 */
public class QuickDemo2025 {
    
    public static void main(String[] args) {
        System.out.println("🚀 ============================================");
        System.out.println("🚀   TOWER BLOXX 2025 - QUICK DEMO LAUNCH   ");
        System.out.println("🚀 ============================================");
        System.out.println("📋 Features Included:");
        System.out.println("  ✅ Advanced Camera System with Smooth Following");
        System.out.println("  ✅ Enhanced Block Physics with Rotation & Trails");
        System.out.println("  ✅ Professional Visual Effects & Particles");
        System.out.println("  ✅ Real-time Performance Monitoring");
        System.out.println("  ✅ Object Pooling for Optimal Performance");
        System.out.println("  ✅ Cinematic Camera Modes & Shake Effects");
        System.out.println("  ✅ Professional UI & HUD Design");
        System.out.println("  ✅ High-Quality Rendering Pipeline");
        System.out.println("============================================");
        
        // Set performance options
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("swing.aatext", "true");
        
        try {
            // Launch the integrated game
            GameIntegration2025.main(args);
            
        } catch (Exception e) {
            System.err.println("❌ Error launching demo: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\n🛠️ FALLBACK: Creating simple test...");
            createSimpleTest();
        }
    }
    
    private static void createSimpleTest() {
        System.out.println("🔧 Testing individual components...");
        
        try {
            // Test camera system
            System.out.println("📸 Testing Camera System...");
            com.skillparty.towerblox.game.camera.CameraSystem camera = 
                new com.skillparty.towerblox.game.camera.CameraSystem(1280, 720);
            camera.followTower(640, 200);
            camera.update();
            System.out.println("✅ Camera System: Working");
            
            // Test performance monitor
            System.out.println("📊 Testing Performance Monitor...");
            com.skillparty.towerblox.performance.PerformanceMonitor perfMon = 
                new com.skillparty.towerblox.performance.PerformanceMonitor();
            perfMon.update();
            System.out.println("✅ Performance Monitor: Working - FPS: " + 
                             String.format("%.1f", perfMon.getCurrentFPS()));
            
            // Test block pool
            System.out.println("🧱 Testing Block Pool...");
            com.skillparty.towerblox.utils.BlockPool pool = 
                new com.skillparty.towerblox.utils.BlockPool(50, 150);
            System.out.println("✅ Block Pool: Working - Available: " + 
                             pool.getAvailableCount() + "/" + pool.getTotalCount());
            
            System.out.println("🎉 All core components are working!");
            
        } catch (Exception e) {
            System.err.println("❌ Component test failed: " + e.getMessage());
        }
    }
}

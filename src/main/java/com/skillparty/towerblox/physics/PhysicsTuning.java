package com.skillparty.towerblox.physics;

/**
 * Physics tuning constants for optimal Tower Bloxx gameplay
 * All values have been carefully calibrated for the best feel
 * 
 * @author joseAlejandro
 * @version 2.0 - Fine-tuned Edition
 */
public class PhysicsTuning {
    
    // ============================================
    // GRAVITY & AIR PHYSICS
    // ============================================
    
    /**
     * Gravity acceleration (pixels/s²)
     * Original Tower Bloxx uses ~650-750
     * Higher values = blocks fall faster
     */
    public static final double GRAVITY = 720.0;
    
    /**
     * Air resistance coefficient
     * Affects how quickly blocks reach terminal velocity
     * Range: 0.01 (low resistance) to 0.1 (high resistance)
     */
    public static final double AIR_RESISTANCE = 0.035;
    
    /**
     * Maximum falling speed (pixels/s)
     * Prevents blocks from falling too fast
     */
    public static final double TERMINAL_VELOCITY = 800.0;
    
    // ============================================
    // BLOCK PHYSICS
    // ============================================
    
    /**
     * Bounce damping when block hits tower
     * 0 = no bounce, 1 = perfect elastic bounce
     * Tower Bloxx uses very low bounce for stability
     */
    public static final double BOUNCE_DAMPING = 0.15;
    
    /**
     * Horizontal friction for sliding blocks
     * Higher = blocks stop sliding faster
     */
    public static final double HORIZONTAL_FRICTION = 0.25;
    
    /**
     * Rotation damping for wobbling blocks
     * Controls how quickly rotation stops
     */
    public static final double ROTATION_DAMPING = 0.92;
    
    /**
     * Maximum rotation speed (radians/s)
     */
    public static final double MAX_ROTATION_SPEED = Math.PI * 2;
    
    /**
     * Block wobble amplitude when landing
     * Controls the visual "squash" effect
     */
    public static final double WOBBLE_AMPLITUDE = 0.08;
    
    /**
     * Wobble frequency (Hz)
     * How fast the block wobbles
     */
    public static final double WOBBLE_FREQUENCY = 4.0;
    
    /**
     * Wobble decay rate
     * How quickly wobbling stops
     */
    public static final double WOBBLE_DECAY = 0.85;
    
    // ============================================
    // CRANE PHYSICS
    // ============================================
    
    /**
     * Crane swing speed (radians/s)
     * Base speed for pendulum movement
     */
    public static final double CRANE_SWING_SPEED = 1.8;
    
    /**
     * Crane swing amplitude (radians)
     * Maximum angle of swing
     */
    public static final double CRANE_SWING_AMPLITUDE = Math.PI / 5;
    
    /**
     * Crane cable elasticity
     * Adds slight "bounce" to cable
     */
    public static final double CABLE_ELASTICITY = 0.02;
    
    /**
     * Crane movement smoothing
     * Higher = smoother movement
     */
    public static final double CRANE_SMOOTHING = 0.15;
    
    /**
     * Block release momentum transfer
     * How much crane momentum transfers to block
     */
    public static final double MOMENTUM_TRANSFER = 0.7;
    
    // ============================================
    // COLLISION & STABILITY
    // ============================================
    
    /**
     * Collision detection threshold (pixels)
     * Precision of collision detection
     */
    public static final double COLLISION_THRESHOLD = 1.0;
    
    /**
     * Stability threshold for tower
     * Maximum offset before tower is unstable
     */
    public static final double STABILITY_THRESHOLD = 40.0;
    
    /**
     * Tower sway amount
     * Visual effect when tower is tall
     */
    public static final double TOWER_SWAY_AMOUNT = 0.003;
    
    /**
     * Tower sway frequency (Hz)
     */
    public static final double TOWER_SWAY_FREQUENCY = 0.5;
    
    // ============================================
    // SCORING & GAMEPLAY
    // ============================================
    
    /**
     * Perfect placement threshold (pixels)
     * Distance for "perfect" alignment bonus
     */
    public static final double PERFECT_THRESHOLD = 5.0;
    
    /**
     * Good placement threshold (pixels)
     */
    public static final double GOOD_THRESHOLD = 15.0;
    
    /**
     * Score multiplier for perfect placement
     */
    public static final double PERFECT_MULTIPLIER = 3.0;
    
    /**
     * Score multiplier for good placement
     */
    public static final double GOOD_MULTIPLIER = 1.5;
    
    /**
     * Speed increase per level
     * Makes game progressively harder
     */
    public static final double SPEED_INCREASE_PER_LEVEL = 0.05;
    
    // ============================================
    // PARTICLE EFFECTS
    // ============================================
    
    /**
     * Number of particles on impact
     */
    public static final int IMPACT_PARTICLES = 12;
    
    /**
     * Particle speed range (pixels/s)
     */
    public static final double PARTICLE_MIN_SPEED = 50;
    public static final double PARTICLE_MAX_SPEED = 200;
    
    /**
     * Particle lifetime (seconds)
     */
    public static final double PARTICLE_LIFETIME = 1.5;
    
    /**
     * Particle gravity multiplier
     */
    public static final double PARTICLE_GRAVITY = 0.5;
    
    // ============================================
    // CAMERA & RENDERING
    // ============================================
    
    /**
     * Camera follow speed
     * How quickly camera follows tower height
     */
    public static final double CAMERA_FOLLOW_SPEED = 0.12;
    
    /**
     * Camera shake intensity on impact
     */
    public static final double CAMERA_SHAKE_INTENSITY = 8.0;
    
    /**
     * Camera shake duration (seconds)
     */
    public static final double CAMERA_SHAKE_DURATION = 0.3;
    
    /**
     * Camera shake decay rate
     */
    public static final double CAMERA_SHAKE_DECAY = 0.9;
    
    // ============================================
    // DIFFICULTY ADJUSTMENTS
    // ============================================
    
    /**
     * Difficulty scaling for EASY mode
     */
    public static class Easy {
        public static final double GRAVITY_MULTIPLIER = 0.8;
        public static final double CRANE_SPEED_MULTIPLIER = 0.7;
        public static final double STABILITY_MULTIPLIER = 1.5;
        public static final double PERFECT_THRESHOLD_MULTIPLIER = 2.0;
    }
    
    /**
     * Difficulty scaling for NORMAL mode
     */
    public static class Normal {
        public static final double GRAVITY_MULTIPLIER = 1.0;
        public static final double CRANE_SPEED_MULTIPLIER = 1.0;
        public static final double STABILITY_MULTIPLIER = 1.0;
        public static final double PERFECT_THRESHOLD_MULTIPLIER = 1.0;
    }
    
    /**
     * Difficulty scaling for HARD mode
     */
    public static class Hard {
        public static final double GRAVITY_MULTIPLIER = 1.2;
        public static final double CRANE_SPEED_MULTIPLIER = 1.3;
        public static final double STABILITY_MULTIPLIER = 0.7;
        public static final double PERFECT_THRESHOLD_MULTIPLIER = 0.5;
    }
    
    /**
     * Difficulty scaling for EXPERT mode
     */
    public static class Expert {
        public static final double GRAVITY_MULTIPLIER = 1.4;
        public static final double CRANE_SPEED_MULTIPLIER = 1.6;
        public static final double STABILITY_MULTIPLIER = 0.5;
        public static final double PERFECT_THRESHOLD_MULTIPLIER = 0.3;
    }
    
    // ============================================
    // DEBUG & TESTING
    // ============================================
    
    /**
     * Enable physics debug visualization
     */
    public static final boolean DEBUG_PHYSICS = false;
    
    /**
     * Show collision boxes
     */
    public static final boolean SHOW_COLLISION_BOXES = false;
    
    /**
     * Show velocity vectors
     */
    public static final boolean SHOW_VELOCITY_VECTORS = false;
    
    /**
     * Time scale for slow-motion testing
     */
    public static final double DEBUG_TIME_SCALE = 1.0;
}

package com.skillparty.towerblox.game.physics;

/**
 * Professional Tower Bloxx-style crane movement system
 * Implements the classic circular/pendulum movement pattern from the original game
 */
public class StandardCraneMovement {
    
    // Movement patterns based on Tower Bloxx 2005
    public enum MovementPattern {
        STANDARD_PENDULUM,    // Classic left-right pendulum
        CIRCULAR_SMOOTH,      // Smooth circular motion
        FIGURE_EIGHT,         // Figure-8 pattern for advanced levels
        SPIRAL_IN,           // Spiral inward pattern
        SPIRAL_OUT,          // Spiral outward pattern
        RANDOM_SMOOTH        // Smooth random movement
    }
    
    private MovementPattern currentPattern;
    private double centerX, centerY;
    private double radius;
    private double angle;
    private double angularSpeed;
    private double time;
    
    // Pattern-specific parameters
    private double pendulumRange;
    private double spiralFactor;
    private double randomSeed;
    
    public StandardCraneMovement(double centerX, double centerY, double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.currentPattern = MovementPattern.STANDARD_PENDULUM;
        this.angularSpeed = 1.5; // Moderate speed for good gameplay
        this.angle = 0;
        this.time = 0;
        
        // Initialize pattern parameters
        this.pendulumRange = radius;
        this.spiralFactor = 0.1;
        this.randomSeed = Math.random() * 1000;
    }
    
    /**
     * Updates the crane position based on the current movement pattern
     * Returns the new position as a double array [x, y]
     */
    public double[] updatePosition(long deltaTime) {
        time += deltaTime / 1000.0;
        
        double x, y;
        
        switch (currentPattern) {
            case STANDARD_PENDULUM:
                x = centerX + Math.sin(time * angularSpeed) * pendulumRange;
                y = centerY;
                break;
                
            case CIRCULAR_SMOOTH:
                angle += angularSpeed * deltaTime / 1000.0;
                x = centerX + Math.cos(angle) * radius;
                y = centerY + Math.sin(angle) * radius * 0.3; // Elliptical for realism
                break;
                
            case FIGURE_EIGHT:
                x = centerX + Math.sin(time * angularSpeed) * radius;
                y = centerY + Math.sin(time * angularSpeed * 2) * radius * 0.5;
                break;
                
            case SPIRAL_IN:
                angle += angularSpeed * deltaTime / 1000.0;
                double currentRadius = radius * (1 - spiralFactor * time);
                if (currentRadius < radius * 0.2) currentRadius = radius * 0.2;
                x = centerX + Math.cos(angle) * currentRadius;
                y = centerY + Math.sin(angle) * currentRadius * 0.3;
                break;
                
            case SPIRAL_OUT:
                angle += angularSpeed * deltaTime / 1000.0;
                currentRadius = radius * (1 + spiralFactor * time);
                if (currentRadius > radius * 2) currentRadius = radius * 2;
                x = centerX + Math.cos(angle) * currentRadius;
                y = centerY + Math.sin(angle) * currentRadius * 0.3;
                break;
                
            case RANDOM_SMOOTH:
                // Smooth random movement using Perlin-like noise
                double noiseX = Math.sin(time * angularSpeed + randomSeed) * 
                               Math.cos(time * angularSpeed * 0.7 + randomSeed * 2);
                double noiseY = Math.cos(time * angularSpeed * 1.3 + randomSeed * 3) * 
                               Math.sin(time * angularSpeed * 0.5 + randomSeed * 4);
                x = centerX + noiseX * radius;
                y = centerY + noiseY * radius * 0.3;
                break;
                
            default:
                x = centerX;
                y = centerY;
        }
        
        return new double[]{x, y};
    }
    
    /**
     * Creates a standard Tower Bloxx movement pattern for the given difficulty level
     */
    public static StandardCraneMovement createStandardPattern(double centerX, double centerY, 
                                                             double gameWidth, int difficultyLevel) {
        double radius = gameWidth * 0.25; // 25% of screen width
        StandardCraneMovement movement = new StandardCraneMovement(centerX, centerY, radius);
        
        // Adjust pattern based on difficulty
        switch (difficultyLevel) {
            case 1: // Easy
                movement.setPattern(MovementPattern.STANDARD_PENDULUM);
                movement.setAngularSpeed(1.0);
                break;
            case 2: // Medium
                movement.setPattern(MovementPattern.CIRCULAR_SMOOTH);
                movement.setAngularSpeed(1.5);
                break;
            case 3: // Hard
                movement.setPattern(MovementPattern.FIGURE_EIGHT);
                movement.setAngularSpeed(2.0);
                break;
            case 4: // Expert
                movement.setPattern(MovementPattern.SPIRAL_IN);
                movement.setAngularSpeed(2.5);
                break;
            default: // Master
                movement.setPattern(MovementPattern.RANDOM_SMOOTH);
                movement.setAngularSpeed(3.0);
        }
        
        return movement;
    }
    
    /**
     * Creates the classic Tower Bloxx 2005 movement pattern
     */
    public static StandardCraneMovement createClassicPattern(double centerX, double centerY, double gameWidth) {
        double radius = gameWidth * 0.3; // Slightly larger range for classic feel
        StandardCraneMovement movement = new StandardCraneMovement(centerX, centerY, radius);
        movement.setPattern(MovementPattern.STANDARD_PENDULUM);
        movement.setAngularSpeed(1.2); // Classic speed
        return movement;
    }
    
    // Getters and setters
    public void setPattern(MovementPattern pattern) {
        this.currentPattern = pattern;
        this.time = 0; // Reset time for smooth transition
    }
    
    public MovementPattern getPattern() {
        return currentPattern;
    }
    
    public void setAngularSpeed(double speed) {
        this.angularSpeed = speed;
    }
    
    public double getAngularSpeed() {
        return angularSpeed;
    }
    
    public void setRadius(double radius) {
        this.radius = radius;
    }
    
    public double getRadius() {
        return radius;
    }
    
    public void setCenter(double x, double y) {
        this.centerX = x;
        this.centerY = y;
    }
    
    public double[] getCenter() {
        return new double[]{centerX, centerY};
    }
    
    /**
     * Resets the movement to start from the beginning
     */
    public void reset() {
        this.time = 0;
        this.angle = 0;
    }
    
    /**
     * Gets the current progress of the movement cycle (0.0 to 1.0)
     */
    public double getMovementProgress() {
        return (time * angularSpeed) % (2 * Math.PI) / (2 * Math.PI);
    }
}

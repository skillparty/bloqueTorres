package com.skillparty.towerblox.physics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**
 * Professional crane system with realistic physics and smooth controls
 * Mimics the feel of the original Tower Bloxx with modern enhancements
 * 
 * @author joseAlejandro
 * @version 2.0 - Production Ready
 */
public class CraneSystem {
    
    // Position and dimensions
    private double x, y;              // Crane tip position
    private double pivotX, pivotY;    // Pivot point (top of screen)
    private double cableLength;       // Dynamic cable length
    private double angle;              // Pendulum angle (radians)
    
    // Physics properties
    private double angularVelocity;   // Angular speed
    private double angularAcceleration;
    private static final double GRAVITY = 9.8;
    private static final double DAMPING = 0.98; // Air resistance - more realistic swing
    private static final double MAX_ANGLE = PhysicsTuning.CRANE_SWING_AMPLITUDE;
    
    // Movement parameters
    private double baseSpeed;
    private double currentSpeed;
    private MovementPattern pattern;
    private double patternTime;
    private boolean autoSwing;
    
    // Block attachment
    private PhysicsEngine.PhysicsBody attachedBlock;
    private boolean hasBlock;
    private double clawOpenAmount; // 0 = closed, 1 = fully open
    private double clawAnimationSpeed = 5.0; // Faster claw animation
    
    // Visual properties
    private static final Color CABLE_COLOR = new Color(80, 80, 80);
    private static final Color CRANE_COLOR = new Color(255, 200, 0); // Yellow crane
    private static final Color CLAW_COLOR = new Color(60, 60, 60);
    private static final Stroke CABLE_STROKE = new BasicStroke(3);
    private static final Stroke CRANE_STROKE = new BasicStroke(5);
    
    /**
     * Movement patterns for variety
     */
    public enum MovementPattern {
        PENDULUM,       // Classic pendulum swing
        CIRCULAR,       // Smooth circular motion
        FIGURE_EIGHT,   // Complex figure-8 pattern
        RANDOM,         // Controlled random movement
        MANUAL          // Player controlled
    }
    
    /**
     * Constructs the crane system
     */
    public CraneSystem(double screenWidth, double screenHeight) {
        this.pivotX = screenWidth / 2;
        this.pivotY = 50;
        this.cableLength = 200;
        this.angle = 0;
        this.angularVelocity = 0;
        this.angularAcceleration = 0;
        
        this.x = pivotX;
        this.y = pivotY + cableLength;
        
        this.baseSpeed = PhysicsTuning.CRANE_SWING_SPEED;
        this.currentSpeed = baseSpeed;
        this.pattern = MovementPattern.PENDULUM;
        this.patternTime = 0;
        this.autoSwing = true;
        
        this.hasBlock = true;
        this.clawOpenAmount = 0;
    }
    
    /**
     * Updates crane physics and position
     */
    public void update(double deltaTime) {
        // Update pattern-based movement
        if (autoSwing) {
            updateMovementPattern(deltaTime);
        }
        
        // Apply pendulum physics
        updatePendulumPhysics(deltaTime);
        
        // Update position from angle
        x = pivotX + Math.sin(angle) * cableLength;
        y = pivotY + Math.cos(angle) * cableLength;
        
        // Update attached block position
        if (hasBlock && attachedBlock != null) {
            attachedBlock.x = x - attachedBlock.width / 2;
            attachedBlock.y = y + 20; // Offset below claw
            attachedBlock.rotation = angle * 0.3; // Subtle rotation with swing
        }
        
        // Update claw animation
        updateClawAnimation(deltaTime);
        
        patternTime += deltaTime;
    }
    
    /**
     * Updates pendulum physics simulation
     */
    private void updatePendulumPhysics(double deltaTime) {
        // Calculate angular acceleration (pendulum equation)
        angularAcceleration = -(GRAVITY / cableLength) * Math.sin(angle);
        
        // Add movement pattern force
        if (autoSwing && pattern == MovementPattern.PENDULUM) {
            double targetAngle = Math.sin(patternTime * currentSpeed) * MAX_ANGLE * 0.7;
            double angleDiff = targetAngle - angle;
            angularAcceleration += angleDiff * 2.0; // Spring force towards target
        }
        
        // Update angular velocity
        angularVelocity += angularAcceleration * deltaTime;
        angularVelocity *= DAMPING; // Apply damping
        
        // Limit angular velocity
        double maxVelocity = 3.0;
        angularVelocity = Math.max(-maxVelocity, Math.min(maxVelocity, angularVelocity));
        
        // Update angle
        angle += angularVelocity * deltaTime;
        
        // Limit maximum swing angle
        angle = Math.max(-MAX_ANGLE, Math.min(MAX_ANGLE, angle));
    }
    
    /**
     * Updates movement pattern
     */
    private void updateMovementPattern(double deltaTime) {
        switch (pattern) {
            case CIRCULAR:
                double targetAngle = Math.sin(patternTime * currentSpeed) * MAX_ANGLE * 0.6;
                angle = angle * 0.9 + targetAngle * 0.1; // Smooth interpolation
                break;
                
            case FIGURE_EIGHT:
                double t = patternTime * currentSpeed;
                double fig8Angle = Math.sin(t) * Math.cos(t * 0.5) * MAX_ANGLE * 0.7;
                angle = angle * 0.85 + fig8Angle * 0.15;
                break;
                
            case RANDOM:
                if (Math.random() < 0.02) { // 2% chance per frame
                    double randomTarget = (Math.random() - 0.5) * MAX_ANGLE;
                    angularVelocity += (randomTarget - angle) * 0.5;
                }
                break;
                
            case MANUAL:
                // Controlled by player input
                break;
        }
    }
    
    /**
     * Updates claw opening/closing animation
     */
    private void updateClawAnimation(double deltaTime) {
        if (!hasBlock && clawOpenAmount < 1.0) {
            clawOpenAmount = Math.min(1.0, clawOpenAmount + clawAnimationSpeed * deltaTime);
        } else if (hasBlock && clawOpenAmount > 0) {
            clawOpenAmount = Math.max(0, clawOpenAmount - clawAnimationSpeed * deltaTime);
        }
    }
    
    /**
     * Releases the attached block
     */
    public PhysicsEngine.PhysicsBody releaseBlock() {
        if (!hasBlock || attachedBlock == null) {
            return null;
        }
        
        // Transfer crane momentum to block
        attachedBlock.vx = angularVelocity * cableLength * Math.cos(angle);
        attachedBlock.vy = Math.abs(angularVelocity) * 50; // Initial drop velocity
        attachedBlock.angularVelocity = angularVelocity * 0.5;
        
        PhysicsEngine.PhysicsBody released = attachedBlock;
        attachedBlock = null;
        hasBlock = false;
        
        return released;
    }
    
    /**
     * Attaches a new block to the crane
     */
    public void attachBlock(PhysicsEngine.PhysicsBody block) {
        this.attachedBlock = block;
        this.hasBlock = true;
        this.clawOpenAmount = 0;
    }
    
    /**
     * Renders the crane system
     */
    public void render(Graphics2D g, double cameraY) {
        // Save original transform
        AffineTransform originalTransform = g.getTransform();
        
        // Enable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw cable
        drawCable(g, cameraY);
        
        // Draw crane mechanism at pivot
        drawCraneMechanism(g, cameraY);
        
        // Draw claw
        drawClaw(g, cameraY);
        
        // Restore transform
        g.setTransform(originalTransform);
    }
    
    /**
     * Draws the cable
     */
    private void drawCable(Graphics2D g, double cameraY) {
        g.setColor(CABLE_COLOR);
        g.setStroke(CABLE_STROKE);
        
        // Draw main cable
        g.drawLine(
            (int)pivotX, (int)(pivotY - cameraY),
            (int)x, (int)(y - cameraY)
        );
        
        // Draw cable segments for realism
        int segments = 5;
        for (int i = 1; i < segments; i++) {
            double t = i / (double)segments;
            double segX = pivotX + (x - pivotX) * t;
            double segY = pivotY + (y - pivotY) * t;
            
            // Add slight sag to cable
            double sag = Math.sin(t * Math.PI) * 3 * (1 + Math.abs(angularVelocity) * 0.2);
            segY += sag;
            
            // Draw small connector
            g.fillOval((int)segX - 2, (int)(segY - cameraY - 2), 4, 4);
        }
    }
    
    /**
     * Draws the crane mechanism at the top
     */
    private void drawCraneMechanism(Graphics2D g, double cameraY) {
        g.setColor(CRANE_COLOR);
        g.setStroke(CRANE_STROKE);
        
        // Draw crane arm
        int armWidth = 100;
        int armHeight = 20;
        g.fillRect(
            (int)(pivotX - armWidth/2), 
            (int)(pivotY - armHeight - cameraY),
            armWidth, armHeight
        );
        
        // Draw pivot wheel
        int wheelRadius = 15;
        g.setColor(CLAW_COLOR);
        g.fillOval(
            (int)(pivotX - wheelRadius),
            (int)(pivotY - wheelRadius - cameraY),
            wheelRadius * 2, wheelRadius * 2
        );
        
        // Draw wheel details
        g.setColor(CRANE_COLOR);
        g.drawOval(
            (int)(pivotX - wheelRadius),
            (int)(pivotY - wheelRadius - cameraY),
            wheelRadius * 2, wheelRadius * 2
        );
    }
    
    /**
     * Draws the claw mechanism
     */
    private void drawClaw(Graphics2D g, double cameraY) {
        AffineTransform transform = g.getTransform();
        
        // Rotate claw with cable angle
        g.translate(x, y - cameraY);
        g.rotate(angle);
        
        // Draw claw base
        g.setColor(CLAW_COLOR);
        g.fillRect(-15, -10, 30, 20);
        
        // Draw claw arms
        double openAngle = clawOpenAmount * 30; // Max 30 degrees open
        
        // Left claw arm
        Path2D leftClaw = new Path2D.Double();
        leftClaw.moveTo(-15, 10);
        leftClaw.lineTo(-15 - Math.sin(Math.toRadians(openAngle)) * 20, 
                        10 + Math.cos(Math.toRadians(openAngle)) * 20);
        leftClaw.lineTo(-10 - Math.sin(Math.toRadians(openAngle)) * 25, 
                        10 + Math.cos(Math.toRadians(openAngle)) * 25);
        leftClaw.lineTo(-5, 10);
        leftClaw.closePath();
        
        // Right claw arm
        Path2D rightClaw = new Path2D.Double();
        rightClaw.moveTo(15, 10);
        rightClaw.lineTo(15 + Math.sin(Math.toRadians(openAngle)) * 20, 
                         10 + Math.cos(Math.toRadians(openAngle)) * 20);
        rightClaw.lineTo(10 + Math.sin(Math.toRadians(openAngle)) * 25, 
                         10 + Math.cos(Math.toRadians(openAngle)) * 25);
        rightClaw.lineTo(5, 10);
        rightClaw.closePath();
        
        g.setColor(CLAW_COLOR);
        g.fill(leftClaw);
        g.fill(rightClaw);
        
        // Draw claw grips
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2));
        g.draw(leftClaw);
        g.draw(rightClaw);
        
        // Draw block if attached
        if (hasBlock && attachedBlock != null) {
            g.setColor(new Color(200, 100, 50)); // Brown block color
            g.fillRect(-30, 15, 60, 40);
            
            // Block details
            g.setColor(Color.DARK_GRAY);
            g.drawRect(-30, 15, 60, 40);
            
            // Windows on block
            g.setColor(new Color(150, 200, 255));
            g.fillRect(-20, 25, 10, 10);
            g.fillRect(-5, 25, 10, 10);
            g.fillRect(10, 25, 10, 10);
        }
        
        g.setTransform(transform);
    }
    
    /**
     * Applies manual control input
     */
    public void applyManualControl(double force) {
        if (pattern == MovementPattern.MANUAL) {
            angularVelocity += force * 0.1;
        }
    }
    
    /**
     * Sets movement pattern
     */
    public void setMovementPattern(MovementPattern pattern) {
        this.pattern = pattern;
        this.patternTime = 0;
    }
    
    /**
     * Sets movement speed
     */
    public void setSpeed(double speedMultiplier) {
        this.currentSpeed = baseSpeed * speedMultiplier;
    }
    
    /**
     * Adjusts cable length (for camera following)
     */
    public void adjustCableLength(double targetY) {
        double minLength = 150;
        double maxLength = 300;
        
        // Smoothly adjust cable length
        double targetLength = Math.max(minLength, Math.min(maxLength, targetY - pivotY));
        cableLength = cableLength * 0.95 + targetLength * 0.05;
    }
    
    /**
     * Gets current position
     */
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public boolean hasBlock() { return hasBlock; }
    public double getClawOpenAmount() { return clawOpenAmount; }
    
    /**
     * Checks if crane is stable enough to drop
     */
    public boolean isStableForDrop() {
        return Math.abs(angularVelocity) < 1.5; // Reasonable threshold
    }
}

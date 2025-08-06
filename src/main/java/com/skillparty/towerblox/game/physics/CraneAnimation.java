package com.skillparty.towerblox.game.physics;

import com.skillparty.towerblox.utils.Constants;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;

/**
 * Handles realistic crane claw animation with smooth frame transitions
 */
public class CraneAnimation {
    
    public enum AnimationState {
        IDLE,           // Claw is closed and not animating
        OPENING,        // Claw is opening to release block
        FULLY_OPEN,     // Claw is fully open (brief pause)
        CLOSING,        // Claw is closing back to idle
        GRABBING        // Special animation for grabbing a new block
    }
    
    private AnimationState currentState;
    private int currentFrame;
    private long lastFrameTime;
    private long animationStartTime;
    
    // Animation parameters
    private static final int MAX_FRAMES = Constants.CRANE_ANIMATION_FRAMES;
    private static final long FRAME_DURATION = Constants.CRANE_ANIMATION_FRAME_TIME;
    private static final long OPEN_PAUSE_DURATION = 100; // Pause when fully open
    
    // Visual parameters for realistic claw rendering
    private static final int CLAW_BASE_WIDTH = 8;
    private static final int CLAW_MAX_OPENING = 25;
    private static final int CLAW_ARM_LENGTH = 15;
    private static final int CLAW_THICKNESS = 4;
    
    // Animation curves for smooth motion
    private static final double[] OPENING_CURVE = {
        0.0, 0.15, 0.35, 0.65, 0.85, 1.0  // Ease-out curve
    };
    
    private static final double[] CLOSING_CURVE = {
        1.0, 0.8, 0.55, 0.3, 0.1, 0.0     // Ease-in curve
    };
    
    // Pausa en extremos del movimiento pendular
    private long pauseEndTime = 0;
    private boolean isPaused = false;
    
    public CraneAnimation() {
        this.currentState = AnimationState.IDLE;
        this.currentFrame = 0;
        this.lastFrameTime = 0;
        this.animationStartTime = 0;
    }
    
    /**
     * Starts the block release animation sequence
     */
    public void startReleaseAnimation() {
        if (currentState == AnimationState.IDLE) {
            currentState = AnimationState.OPENING;
            currentFrame = 0;
            animationStartTime = System.currentTimeMillis();
            lastFrameTime = animationStartTime;
        }
    }
    
    /**
     * Starts the block grabbing animation
     */
    public void startGrabAnimation() {
        if (currentState == AnimationState.IDLE) {
            currentState = AnimationState.GRABBING;
            currentFrame = 0;
            animationStartTime = System.currentTimeMillis();
            lastFrameTime = animationStartTime;
        }
    }
    
    /**
     * Updates the animation state and frame
     */
    public void update(long deltaTime) {
        if (currentState == AnimationState.IDLE) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        switch (currentState) {
            case OPENING:
                updateOpeningAnimation(currentTime);
                break;
                
            case FULLY_OPEN:
                updateFullyOpenState(currentTime);
                break;
                
            case CLOSING:
                updateClosingAnimation(currentTime);
                break;
                
            case GRABBING:
                updateGrabbingAnimation(currentTime);
                break;
        }
    }
    
    /**
     * Updates the opening animation phase
     */
    private void updateOpeningAnimation(long currentTime) {
        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            currentFrame++;
            lastFrameTime = currentTime;
            
            if (currentFrame >= MAX_FRAMES - 1) {
                currentState = AnimationState.FULLY_OPEN;
                currentFrame = MAX_FRAMES - 1;
                animationStartTime = currentTime; // Reset for pause timing
            }
        }
    }
    
    /**
     * Updates the fully open pause state
     */
    private void updateFullyOpenState(long currentTime) {
        if (currentTime - animationStartTime >= OPEN_PAUSE_DURATION) {
            currentState = AnimationState.CLOSING;
            animationStartTime = currentTime;
            lastFrameTime = currentTime;
        }
    }
    
    /**
     * Updates the closing animation phase
     */
    private void updateClosingAnimation(long currentTime) {
        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            currentFrame--;
            lastFrameTime = currentTime;
            
            if (currentFrame <= 0) {
                currentState = AnimationState.IDLE;
                currentFrame = 0;
            }
        }
    }
    
    /**
     * Updates the grabbing animation (quick open-close for new blocks)
     */
    private void updateGrabbingAnimation(long currentTime) {
        long elapsed = currentTime - animationStartTime;
        long totalDuration = FRAME_DURATION * MAX_FRAMES;
        
        if (elapsed >= totalDuration) {
            currentState = AnimationState.IDLE;
            currentFrame = 0;
        } else {
            // Quick open-close cycle
            double progress = (double) elapsed / totalDuration;
            if (progress < 0.5) {
                // Opening phase
                currentFrame = (int) (progress * 2 * (MAX_FRAMES - 1));
            } else {
                // Closing phase
                currentFrame = (int) ((1 - (progress - 0.5) * 2) * (MAX_FRAMES - 1));
            }
        }
    }
    
    /**
     * Renders the animated claw at the specified position
     */
    public void renderClaw(Graphics2D g2d, double x, double y) {
        // Calculate opening amount based on current frame and animation curve
        double openingRatio = getOpeningRatio();
        int clawOpening = (int) (CLAW_BASE_WIDTH + openingRatio * CLAW_MAX_OPENING);
        
        // Set rendering properties
        g2d.setStroke(new BasicStroke(CLAW_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(105, 105, 105)); // Dim gray for claw
        
        int clawX = (int) x;
        int clawY = (int) y;
        
        // Draw left claw arm with realistic articulation
        drawClawArm(g2d, clawX, clawY, -clawOpening, true);
        
        // Draw right claw arm
        drawClawArm(g2d, clawX, clawY, clawOpening, false);
        
        // Draw claw pivot mechanism
        drawClawPivot(g2d, clawX, clawY);
        
        // Draw hydraulic cylinders for realism
        drawHydraulicCylinders(g2d, clawX, clawY, openingRatio);
        
        // Add visual feedback for animation state
        if (currentState != AnimationState.IDLE) {
            drawAnimationEffects(g2d, clawX, clawY, openingRatio);
        }
    }
    
    /**
     * Draws a single claw arm with realistic articulation
     */
    private void drawClawArm(Graphics2D g2d, int centerX, int centerY, int offset, boolean isLeft) {
        // Calculate arm positions with realistic joint movement
        int armStartX = centerX + (offset / 3);
        int armStartY = centerY;
        
        int armMidX = centerX + (offset * 2 / 3);
        int armMidY = centerY + CLAW_ARM_LENGTH / 2;
        
        int armEndX = centerX + offset;
        int armEndY = centerY + CLAW_ARM_LENGTH;
        
        // Draw upper arm segment
        g2d.drawLine(armStartX, armStartY, armMidX, armMidY);
        
        // Draw lower arm segment (claw finger)
        g2d.drawLine(armMidX, armMidY, armEndX, armEndY);
        
        // Draw claw tip
        int tipLength = 5;
        int tipX = armEndX + (isLeft ? tipLength : -tipLength);
        g2d.drawLine(armEndX, armEndY, tipX, armEndY + tipLength);
        
        // Draw joint at mid-point
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillOval(armMidX - 2, armMidY - 2, 4, 4);
        g2d.setColor(new Color(105, 105, 105));
    }
    
    /**
     * Draws the central pivot mechanism
     */
    private void drawClawPivot(Graphics2D g2d, int x, int y) {
        // Main pivot housing
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(x - 6, y - 4, 12, 8);
        
        // Pivot pin
        g2d.setColor(new Color(169, 169, 169)); // Light gray
        g2d.fillOval(x - 3, y - 3, 6, 6);
        
        // Pivot pin center
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 1, y - 1, 2, 2);
    }
    
    /**
     * Draws hydraulic cylinders for realistic mechanical appearance
     */
    private void drawHydraulicCylinders(Graphics2D g2d, int x, int y, double openingRatio) {
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(128, 128, 128)); // Medium gray
        
        // Left cylinder
        int leftCylinderX = x - 8;
        int cylinderLength = (int) (6 + openingRatio * 4); // Extends as claw opens
        g2d.drawLine(leftCylinderX, y - 2, leftCylinderX - cylinderLength, y + 8);
        
        // Right cylinder
        int rightCylinderX = x + 8;
        g2d.drawLine(rightCylinderX, y - 2, rightCylinderX + cylinderLength, y + 8);
        
        // Cylinder housings
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(leftCylinderX - 2, y - 4, 4, 6);
        g2d.fillRect(rightCylinderX - 2, y - 4, 4, 6);
    }
    
    /**
     * Draws visual effects during animation
     */
    private void drawAnimationEffects(Graphics2D g2d, int x, int y, double openingRatio) {
        // Motion blur effect during fast movement
        if (currentState == AnimationState.OPENING || currentState == AnimationState.CLOSING) {
            g2d.setColor(new Color(255, 255, 255, 30)); // Semi-transparent white
            
            // Draw motion lines
            for (int i = 1; i <= 3; i++) {
                int offset = (int) (i * openingRatio * 5);
                g2d.drawLine(x - offset, y + 10, x - offset, y + 15);
                g2d.drawLine(x + offset, y + 10, x + offset, y + 15);
            }
        }
        
        // Spark effect when fully opening (block release)
        if (currentState == AnimationState.FULLY_OPEN) {
            drawSparkEffect(g2d, x, y);
        }
    }
    
    /**
     * Draws spark effect for dramatic block release
     */
    private void drawSparkEffect(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(1));
        
        long time = System.currentTimeMillis();
        for (int i = 0; i < 6; i++) {
            double angle = (time / 100.0 + i * Math.PI / 3) % (2 * Math.PI);
            int sparkX = x + (int) (Math.cos(angle) * 8);
            int sparkY = y + (int) (Math.sin(angle) * 8);
            
            g2d.drawLine(x, y, sparkX, sparkY);
        }
    }
    
    /**
     * Gets the current opening ratio (0.0 = closed, 1.0 = fully open)
     */
    private double getOpeningRatio() {
        if (currentFrame <= 0) {
            return 0.0;
        }
        if (currentFrame >= MAX_FRAMES - 1) {
            return 1.0;
        }
        
        // Use animation curves for smooth motion
        double frameRatio = (double) currentFrame / (MAX_FRAMES - 1);
        
        switch (currentState) {
            case OPENING:
            case FULLY_OPEN:
                return interpolateCurve(OPENING_CURVE, frameRatio);
                
            case CLOSING:
                return interpolateCurve(CLOSING_CURVE, 1.0 - frameRatio);
                
            case GRABBING:
                // Use sine wave for smooth grab motion
                return Math.sin(frameRatio * Math.PI);
                
            default:
                return 0.0;
        }
    }
    
    /**
     * Interpolates along an animation curve
     */
    private double interpolateCurve(double[] curve, double t) {
        if (t <= 0) return curve[0];
        if (t >= 1) return curve[curve.length - 1];
        
        double scaledT = t * (curve.length - 1);
        int index = (int) scaledT;
        double fraction = scaledT - index;
        
        if (index >= curve.length - 1) {
            return curve[curve.length - 1];
        }
        
        // Linear interpolation between curve points
        return curve[index] + (curve[index + 1] - curve[index]) * fraction;
    }
    
    /**
     * Gets animation status information
     */
    public String getAnimationStatus() {
        return String.format("State: %s | Frame: %d/%d | Opening: %.1f%%", 
                           currentState, currentFrame, MAX_FRAMES - 1, getOpeningRatio() * 100);
    }
    
    /**
     * Adds a pause for smoother movement transitions
     * @param pauseDuration duration in milliseconds
     */
    public void addPause(int pauseDuration) {
        isPaused = true;
        pauseEndTime = System.currentTimeMillis() + pauseDuration;
    }
    
    /**
     * Checks if crane is currently paused
     */
    private boolean checkPause() {
        if (isPaused) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= pauseEndTime) {
                isPaused = false;
                pauseEndTime = 0;
                return false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * Resets the animation to idle state
     */
    public void reset() {
        currentState = AnimationState.IDLE;
        currentFrame = 0;
        lastFrameTime = 0;
        animationStartTime = 0;
        isPaused = false;
        pauseEndTime = 0;
    }
    
    // Getters
    public AnimationState getCurrentState() { return currentState; }
    public int getCurrentFrame() { return currentFrame; }
    public double getCurrentOpeningRatio() { return getOpeningRatio(); }
    public boolean isPaused() { return isPaused; }
    public boolean isAnimating() { return currentState != AnimationState.IDLE || checkPause(); }
    public boolean isOpening() { return currentState == AnimationState.OPENING; }
    public boolean isClosing() { return currentState == AnimationState.CLOSING; }
    public boolean isFullyOpen() { return currentState == AnimationState.FULLY_OPEN; }
}
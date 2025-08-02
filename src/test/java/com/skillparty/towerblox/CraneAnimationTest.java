package com.skillparty.towerblox;

import com.skillparty.towerblox.game.physics.CraneAnimation;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for CraneAnimation class
 */
public class CraneAnimationTest {
    private CraneAnimation animation;

    @Before
    public void setUp() {
        animation = new CraneAnimation();
    }

    @Test
    public void testInitialState() {
        assertEquals(CraneAnimation.AnimationState.IDLE, animation.getCurrentState());
        assertEquals(0, animation.getCurrentFrame());
        assertFalse(animation.isAnimating());
        assertFalse(animation.isOpening());
        assertFalse(animation.isClosing());
        assertFalse(animation.isFullyOpen());
    }

    @Test
    public void testStartReleaseAnimation() {
        animation.startReleaseAnimation();
        
        assertEquals(CraneAnimation.AnimationState.OPENING, animation.getCurrentState());
        assertTrue(animation.isAnimating());
        assertTrue(animation.isOpening());
        assertFalse(animation.isClosing());
        assertFalse(animation.isFullyOpen());
    }

    @Test
    public void testStartGrabAnimation() {
        animation.startGrabAnimation();
        
        assertEquals(CraneAnimation.AnimationState.GRABBING, animation.getCurrentState());
        assertTrue(animation.isAnimating());
        assertFalse(animation.isOpening());
        assertFalse(animation.isClosing());
        assertFalse(animation.isFullyOpen());
    }

    @Test
    public void testCannotStartAnimationWhenAlreadyAnimating() {
        animation.startReleaseAnimation();
        CraneAnimation.AnimationState firstState = animation.getCurrentState();
        
        // Try to start another animation
        animation.startGrabAnimation();
        
        // Should still be in the first animation state
        assertEquals(firstState, animation.getCurrentState());
    }

    @Test
    public void testAnimationProgression() {
        animation.startReleaseAnimation();
        
        // Simulate time passing
        for (int i = 0; i < 10; i++) {
            animation.update(25); // 25ms per update (faster than frame time)
        }
        
        // Animation should have progressed
        assertTrue(animation.getCurrentFrame() > 0 || 
                  animation.getCurrentState() != CraneAnimation.AnimationState.OPENING);
    }

    @Test
    public void testReset() {
        animation.startReleaseAnimation();
        animation.update(100); // Let it progress
        
        animation.reset();
        
        assertEquals(CraneAnimation.AnimationState.IDLE, animation.getCurrentState());
        assertEquals(0, animation.getCurrentFrame());
        assertFalse(animation.isAnimating());
    }

    @Test
    public void testUpdateWhenIdle() {
        // Update when idle should not change state
        animation.update(100);
        
        assertEquals(CraneAnimation.AnimationState.IDLE, animation.getCurrentState());
        assertEquals(0, animation.getCurrentFrame());
        assertFalse(animation.isAnimating());
    }

    @Test
    public void testAnimationStatus() {
        String status = animation.getAnimationStatus();
        assertNotNull(status);
        assertTrue(status.contains("IDLE"));
        assertTrue(status.contains("Frame: 0"));
        
        animation.startReleaseAnimation();
        status = animation.getAnimationStatus();
        assertTrue(status.contains("OPENING"));
    }

    @Test
    public void testStateTransitions() {
        animation.startReleaseAnimation();
        assertEquals(CraneAnimation.AnimationState.OPENING, animation.getCurrentState());
        
        // Fast-forward through animation frames
        for (int i = 0; i < 100; i++) {
            animation.update(50); // Long updates to ensure frame progression
            
            // Check that we eventually reach other states
            if (animation.getCurrentState() == CraneAnimation.AnimationState.FULLY_OPEN) {
                assertTrue(animation.isFullyOpen());
                break;
            }
        }
    }

    @Test
    public void testGrabAnimationCompletion() {
        animation.startGrabAnimation();
        
        // Run animation for a long time to ensure completion
        for (int i = 0; i < 200; i++) {
            animation.update(50);
            
            if (animation.getCurrentState() == CraneAnimation.AnimationState.IDLE) {
                break;
            }
        }
        
        // Should eventually return to idle
        assertEquals(CraneAnimation.AnimationState.IDLE, animation.getCurrentState());
        assertFalse(animation.isAnimating());
    }

    @Test
    public void testFrameBounds() {
        animation.startReleaseAnimation();
        
        // Run animation and check frame bounds
        for (int i = 0; i < 1000; i++) {
            animation.update(10);
            
            int frame = animation.getCurrentFrame();
            assertTrue("Frame should be >= 0", frame >= 0);
            assertTrue("Frame should be < 5", frame < 5); // MAX_FRAMES = 5
        }
    }

    @Test
    public void testMultipleAnimationCycles() {
        // First animation
        animation.startReleaseAnimation();
        
        // Let it complete
        for (int i = 0; i < 500; i++) {
            animation.update(50);
            if (!animation.isAnimating()) break;
        }
        
        assertTrue("First animation should complete", !animation.isAnimating());
        
        // Second animation
        animation.startGrabAnimation();
        assertTrue("Second animation should start", animation.isAnimating());
        
        // Let it complete
        for (int i = 0; i < 500; i++) {
            animation.update(50);
            if (!animation.isAnimating()) break;
        }
        
        assertTrue("Second animation should complete", !animation.isAnimating());
    }
}
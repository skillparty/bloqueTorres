package com.skillparty.towerblox;

import com.skillparty.towerblox.game.physics.Block;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.Color;

/**
 * Unit tests for Block class physics and collision detection
 */
public class BlockTest {
    private Block block;
    private static final double DELTA = 0.001; // For double comparisons

    @Before
    public void setUp() {
        block = new Block(100, 100, 50, 30, Color.BLUE);
    }

    @Test
    public void testBlockCreation() {
        assertEquals(100.0, block.getX(), DELTA);
        assertEquals(100.0, block.getY(), DELTA);
        assertEquals(50.0, block.getWidth(), DELTA);
        assertEquals(30.0, block.getHeight(), DELTA);
        assertEquals(Color.BLUE, block.getColor());
        assertFalse(block.isStable());
        assertFalse(block.isDropped());
    }

    @Test
    public void testBlockDrop() {
        assertFalse(block.isDropped());
        block.drop();
        assertTrue(block.isDropped());
    }

    @Test
    public void testGravityApplication() {
        double initialY = block.getY();
        block.drop();
        
        // Update several times to see gravity effect
        for (int i = 0; i < 5; i++) {
            block.update();
        }
        
        // Block should have moved down due to gravity
        assertTrue(block.getY() > initialY);
        assertTrue(block.getVelocityY() > 0);
    }

    @Test
    public void testCollisionDetection() {
        Block other = new Block(120, 110, 50, 30, Color.RED);
        assertTrue(block.collidesWith(other));
        
        Block farAway = new Block(200, 200, 50, 30, Color.GREEN);
        assertFalse(block.collidesWith(farAway));
    }

    @Test
    public void testGroundCollision() {
        assertFalse(block.collidesWithGround(100)); // Ground above block
        assertTrue(block.collidesWithGround(130));  // Ground below block bottom
        assertTrue(block.collidesWithGround(125));  // Ground intersecting block
    }

    @Test
    public void testMakeStable() {
        block.drop();
        block.setVelocityX(5.0);
        block.setVelocityY(3.0);
        
        block.makeStable();
        
        assertTrue(block.isStable());
        assertEquals(0.0, block.getVelocityX(), DELTA);
        assertEquals(0.0, block.getVelocityY(), DELTA);
    }

    @Test
    public void testAlignmentScore() {
        Block below = new Block(100, 150, 50, 30, Color.RED);
        
        // Perfect alignment
        assertEquals(100, block.getAlignmentScore(below));
        
        // Partial alignment
        below.setX(110); // Offset by 10 pixels
        int score = block.getAlignmentScore(below);
        assertTrue(score > 0 && score < 100);
        
        // No alignment (too far)
        below.setX(200);
        assertEquals(0, block.getAlignmentScore(below));
        
        // First block (no block below)
        assertEquals(100, block.getAlignmentScore(null));
    }

    @Test
    public void testFriction() {
        block.drop();
        block.setVelocityX(10.0);
        
        double initialVelocityX = block.getVelocityX();
        block.update();
        
        // Velocity should decrease due to friction
        assertTrue(block.getVelocityX() < initialVelocityX);
    }
}
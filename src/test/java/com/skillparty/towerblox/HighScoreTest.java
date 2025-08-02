package com.skillparty.towerblox;

import com.skillparty.towerblox.game.DifficultyLevel;
import com.skillparty.towerblox.score.HighScore;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

/**
 * Unit tests for HighScore class
 */
public class HighScoreTest {
    private HighScore highScore;
    private LocalDateTime testDate;

    @Before
    public void setUp() {
        testDate = LocalDateTime.of(2024, 1, 15, 14, 30);
        highScore = new HighScore("ABC", 10000, DifficultyLevel.NORMAL, testDate);
    }

    @Test
    public void testBasicCreation() {
        assertEquals("ABC", highScore.getNickname());
        assertEquals(10000, highScore.getScore());
        assertEquals(DifficultyLevel.NORMAL, highScore.getDifficulty());
        assertEquals(testDate, highScore.getDate());
    }

    @Test
    public void testCreationWithCurrentDate() {
        HighScore score = new HighScore("XYZ", 5000, DifficultyLevel.EASY);
        
        assertEquals("XYZ", score.getNickname());
        assertEquals(5000, score.getScore());
        assertEquals(DifficultyLevel.EASY, score.getDifficulty());
        assertNotNull(score.getDate());
        
        // Date should be recent (within last minute)
        assertTrue(score.getDate().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    public void testNicknameValidation() {
        assertTrue(HighScore.isValidNickname("ABC"));
        assertTrue(HighScore.isValidNickname("A1B"));
        assertTrue(HighScore.isValidNickname("123"));
        assertTrue(HighScore.isValidNickname("X"));
        
        assertFalse(HighScore.isValidNickname(null));
        assertFalse(HighScore.isValidNickname(""));
        assertFalse(HighScore.isValidNickname("   "));
        assertFalse(HighScore.isValidNickname("ABCD")); // Too long
        assertFalse(HighScore.isValidNickname("A@B")); // Invalid character
        assertFalse(HighScore.isValidNickname("A B")); // Space not allowed
    }

    @Test
    public void testNicknameProcessing() {
        // Test truncation
        HighScore longNick = new HighScore("ABCDEF", 1000, DifficultyLevel.EASY);
        assertEquals("ABC", longNick.getNickname());
        
        // Test padding
        HighScore shortNick = new HighScore("A", 1000, DifficultyLevel.EASY);
        assertEquals("A  ", shortNick.getNickname()); // Padded with spaces
        
        // Test uppercase conversion
        HighScore lowerNick = new HighScore("abc", 1000, DifficultyLevel.EASY);
        assertEquals("ABC", lowerNick.getNickname());
        
        // Test null handling
        HighScore nullNick = new HighScore(null, 1000, DifficultyLevel.EASY);
        assertEquals("???", nullNick.getNickname());
        
        // Test empty string handling
        HighScore emptyNick = new HighScore("", 1000, DifficultyLevel.EASY);
        assertEquals("???", emptyNick.getNickname());
    }

    @Test
    public void testDisplayNickname() {
        HighScore paddedScore = new HighScore("A", 1000, DifficultyLevel.EASY);
        assertEquals("A  ", paddedScore.getNickname()); // With padding
        assertEquals("A", paddedScore.getDisplayNickname()); // Without padding
    }

    @Test
    public void testComparison() {
        HighScore score1 = new HighScore("AAA", 10000, DifficultyLevel.NORMAL, testDate);
        HighScore score2 = new HighScore("BBB", 5000, DifficultyLevel.NORMAL, testDate);
        HighScore score3 = new HighScore("CCC", 10000, DifficultyLevel.HARD, testDate);
        HighScore score4 = new HighScore("DDD", 10000, DifficultyLevel.NORMAL, testDate.plusDays(1));
        
        // Higher score should come first
        assertTrue(score1.compareTo(score2) < 0);
        assertTrue(score2.compareTo(score1) > 0);
        
        // Same score, harder difficulty should come first
        assertTrue(score3.compareTo(score1) < 0);
        
        // Same score and difficulty, newer date should come first
        assertTrue(score4.compareTo(score1) < 0);
        
        // Same score should be equal in comparison
        assertEquals(0, score1.compareTo(new HighScore("AAA", 10000, DifficultyLevel.NORMAL, testDate)));
    }

    @Test
    public void testFormattedDate() {
        String formatted = highScore.getFormattedDate();
        assertNotNull(formatted);
        assertTrue(formatted.contains("15/01/2024"));
        assertTrue(formatted.contains("14:30"));
    }

    @Test
    public void testDifficultyName() {
        assertEquals("Normal", highScore.getDifficultyName());
        
        HighScore easyScore = new HighScore("ABC", 1000, DifficultyLevel.EASY);
        assertEquals("Fácil", easyScore.getDifficultyName());
        
        HighScore hardScore = new HighScore("ABC", 1000, DifficultyLevel.HARD);
        assertEquals("Difícil", hardScore.getDifficultyName());
    }

    @Test
    public void testToString() {
        String str = highScore.toString();
        assertNotNull(str);
        assertTrue(str.contains("ABC"));
        assertTrue(str.contains("10000"));
        assertTrue(str.contains("Normal"));
        assertTrue(str.contains("15/01/2024"));
    }

    @Test
    public void testToTableString() {
        String tableStr = highScore.toTableString(1);
        assertNotNull(tableStr);
        assertTrue(tableStr.contains(" 1."));
        assertTrue(tableStr.contains("ABC"));
        assertTrue(tableStr.contains("10,000")); // Formatted number
        assertTrue(tableStr.contains("Normal"));
    }

    @Test
    public void testEquals() {
        HighScore identical = new HighScore("ABC", 10000, DifficultyLevel.NORMAL, testDate);
        HighScore different = new HighScore("XYZ", 10000, DifficultyLevel.NORMAL, testDate);
        
        assertEquals(highScore, identical);
        assertNotEquals(highScore, different);
        assertNotEquals(highScore, null);
        assertNotEquals(highScore, "not a HighScore");
    }

    @Test
    public void testHashCode() {
        HighScore identical = new HighScore("ABC", 10000, DifficultyLevel.NORMAL, testDate);
        HighScore different = new HighScore("XYZ", 10000, DifficultyLevel.NORMAL, testDate);
        
        assertEquals(highScore.hashCode(), identical.hashCode());
        assertNotEquals(highScore.hashCode(), different.hashCode());
    }

    @Test
    public void testNicknameSetterEdgeCases() {
        // Test whitespace trimming
        HighScore whitespaceScore = new HighScore("  A  ", 1000, DifficultyLevel.EASY);
        assertEquals("A  ", whitespaceScore.getNickname()); // Trimmed and padded
        
        // Test mixed case
        HighScore mixedCase = new HighScore("aBc", 1000, DifficultyLevel.EASY);
        assertEquals("ABC", mixedCase.getNickname());
    }
}
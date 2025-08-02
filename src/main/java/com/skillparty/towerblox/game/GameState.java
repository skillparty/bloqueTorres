package com.skillparty.towerblox.game;

/**
 * Enum representing the different states of the Tower Bloxx game
 */
public enum GameState {
    /**
     * Main menu state - showing difficulty selection and logo
     */
    MENU,

    /**
     * Active gameplay state - player is building the tower
     */
    PLAYING,

    /**
     * Game over state - tower has fallen or become unstable
     */
    GAME_OVER,

    /**
     * High scores display state - showing leaderboard
     */
    HIGH_SCORES
}
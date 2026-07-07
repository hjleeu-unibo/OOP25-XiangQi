package it.unibo.xiangqi.controller.api;

import it.unibo.xiangqi.model.api.Player;

/**
 * The timer of the each player.
 * Each player has two timer, one for the turn and the other for the game.
 * GameTimer
 */
public interface GameTimer {
    /**
     * Start the turn timer for the given player.
     * 
     * @param player the current player
     */
    void startTurn(Player player);

    /**
     * Stop the turn timer for the given player.
     * Reset the turn timer, which can't be bigger than the total remaining.
     * 
     * @param player the current player
     */
    void stopTurn(Player player);

    /**
     * Returns the remaining time of the current turn.
     * 
     * @param player the current player
     * @return seconds left
     */
    long getTurnRemaining(Player player);

    /**
     * Returns the remaining time of the entire game.
     * 
     * @param player the current player
     * @return seconds left
     */
    long getGameRemaining(Player player);

    /**
     * Tell you if the timer of this turn is expired or not.
     * 
     * @param player the current player
     * @return true if the time is expired
     */
    boolean isTurnExpired(Player player);

    /**
     * Tell you if the timer of the entire game is expired or not.
     * 
     * @param player the current player
     * @return true if the time is expired
     */
    boolean isTotalExpired(Player player);

    /**
     * Reset all timers of both players.
     */
    void reset();
}

package it.unibo.xiangqi.model.api;

import it.unibo.xiangqi.common.Color;

public interface Player {

    /**
     * Returns the color of this player.
     *
     * @return the player's color
     */
    Color getColor();

    /**
     * Returns whether this player is a human.
     *
     * @return true if human, false if bot
     */
    boolean isHuman();
}
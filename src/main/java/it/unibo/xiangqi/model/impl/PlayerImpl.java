package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Player;

/**
 * The implementation of the player interface.
 * PlayerImpl
 */
public class PlayerImpl implements Player {

    private final Color color;
    private final Boolean isHuman;

    /**
     * Constructor.
     * 
     * @param color the rapresenting color
     * @param isHuman human or bot
     */
    public PlayerImpl(final Color color, final boolean isHuman) {
        this.color = color;
        this.isHuman = isHuman;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public boolean isHuman() {
       return this.isHuman;
    }

    @Override
    public String toString() {
        return "HumanPlayer[" + color + "]";
    }
}

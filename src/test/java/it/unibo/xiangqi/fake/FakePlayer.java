package it.unibo.xiangqi.fake;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Player;

/**
 * Simulation of the Player class.
 * FakePlayer
 */
public final class FakePlayer implements Player {
    private final Color color; 

    /**
     * Constructor.
     * 
     * @param color the rapresenting color
     */
    public FakePlayer(final Color color) {
        this.color = color; 
    }

    @Override
    public Color getColor() {
        return this.color; 
    }

    @Override
    public boolean isHuman() {
        throw new UnsupportedOperationException("Unimplemented method 'isHuman'");
    }
}

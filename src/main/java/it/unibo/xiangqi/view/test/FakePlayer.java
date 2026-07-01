package it.unibo.xiangqi.view.test;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.model.api.Player;

/**
 * test class
 * 
 * @hidden
 */
public class FakePlayer implements Player{

    private Color color; 

    public FakePlayer(Color color){
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

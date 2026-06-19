package it.unibo.xiangqi.model;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.model.api.Player;

public class FakePlayer implements Player{

    private Color color; 

    public FakePlayer(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor() {
        return this.color; 
    }
    
}

package it.unibo.xiangqi.view.test;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Player;

public class FakePlayer implements Player{

    private Color color; 

    public FakePlayer(Color color){
        this.color = color; 
    }

    @Override
    public Color getColor() {
        return this.color; 
    }
    
}

package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.model.api.Player;

public class PlayerImpl implements Player{

    private Color color; 
    private boolean isHuman; 

    public PlayerImpl(Color color, boolean isHuman) {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + (isHuman ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerImpl other = (PlayerImpl) obj;
        if (color != other.color)
            return false;
        if (isHuman != other.isHuman)
            return false;
        return true;
    }
    
}

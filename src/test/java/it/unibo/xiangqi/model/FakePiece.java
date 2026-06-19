package it.unibo.xiangqi.model;

import java.util.Objects;

import it.unibo.xiangqi.common.PieceType;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;

public class FakePiece implements Piece{
    private PieceType type; 
    private Player owner; 
    private Position position; 

    

    public FakePiece(PieceType type, Player owner, Position position) {
        this.type = type;
        this.owner = owner;
        this.position = position;
    }

    @Override
    public PieceType getType() {
        return this.type; 
    }

    @Override
    public Player getOwner() {
        return this.owner; 
    }

    @Override
    public Position getPosition() {
        return this.position; 
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FakePiece)) {
            return false;
        }

        FakePiece other = (FakePiece) obj; 

        return Objects.equals(this.type, other.type)
            && Objects.equals(this.owner, other.owner)
            && Objects.equals(this.position, other.position);
    }
    
}

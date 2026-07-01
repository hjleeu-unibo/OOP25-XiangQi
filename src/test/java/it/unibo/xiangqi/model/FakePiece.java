package it.unibo.xiangqi.model;

import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.common.Move;
import it.unibo.xiangqi.common.PieceType;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.api.Board;
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

    @Override
    public void setPosition(Position position) {
        throw new UnsupportedOperationException("Unimplemented method 'setPosition'");
    }

    @Override
    public List<Move> getMoves(Board board) {
        throw new UnsupportedOperationException("Unimplemented method 'getMoves'");
    }

    @Override
    public Boolean isDefensor() {
        throw new UnsupportedOperationException("Unimplemented method 'isDefensor'");
    }

    @Override
    public int getInitialValue() {
        throw new UnsupportedOperationException("Unimplemented method 'getInitialValue'");
    }

    @Override
    public int getCurrentValue() {
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentValue'");
    }

    @Override
    public void setValue(int value) {
        throw new UnsupportedOperationException("Unimplemented method 'setValue'");
    }
    
}

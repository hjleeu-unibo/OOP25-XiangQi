package it.unibo.xiangqi.view.test;

import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

/**
 * test class
 * 
 * @hidden
 */
public class FakePiece implements Piece{

    private PieceType type; 
    private Player owner; 
    private Position pos; 


    public FakePiece(PieceType type, Player owner, Position pos) {
        this.type = type;
        this.owner = owner;
        this.pos = pos;
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
        return this.pos; 
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

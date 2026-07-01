package it.unibo.xiangqi.view.test;

import it.unibo.xiangqi.common.PieceType;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;

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
    
}

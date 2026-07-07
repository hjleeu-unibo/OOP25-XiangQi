package it.unibo.xiangqi.view.test;

import java.util.List;

import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Position;

/**
 * test class.
 * 
 * @hidden
 */
public final class FakeBoard implements Board {

    private List<Piece> pieces;

    public FakeBoard(final List<Piece> pieces) {
        this.pieces = pieces; 
    }

    @Override
    public List<Piece> getPieces() {
        return this.pieces; 
    }

    @Override
    public Piece getPieceAt(final Position pos) {

        Piece piece = null; 

        for (final Piece p : this.pieces) {
            if (p.getPosition().equals(pos)) {
                piece = p;
            } 
        }

        return piece;
    }

    @Override
    public void deletePiece(final Piece piece) {
        throw new UnsupportedOperationException("Unimplemented method 'deletePiece'");
    }

    @Override
    public void addPiece(final Piece piece) {
        throw new UnsupportedOperationException("Unimplemented method 'addPiece'");
    }

    @Override
    public Board afterMove(final Move move) {
        throw new UnsupportedOperationException("Unimplemented method 'afterMove'");
    }
}

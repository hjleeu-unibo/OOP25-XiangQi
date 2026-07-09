package it.unibo.xiangqi.fake;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Position;

/**
 * Simulate the Board class.
 * FakeBoard
 */
public final class FakeBoard implements Board {

    private final List<Piece> pieces;

    /**
     * Constructor.
     * 
     * @param pieces list of pieces
     */
    public FakeBoard(final List<Piece> pieces) {
        this.pieces = new ArrayList<>(pieces);
    }

    @Override
    public List<Piece> getPieces() {
        return List.copyOf(this.pieces); 
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

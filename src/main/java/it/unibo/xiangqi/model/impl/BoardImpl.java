package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Position;

/**
 * Concrete implementation of the {@link Board} interface.
 * This class stores the pieces currently placed on the board
 * and provides operations to access and modify them.
 */
public class BoardImpl implements Board {
    private final List<Piece> pieces; 

    /**
     * Creates a new board containing the specified pieces.
     *
     * @param pieces the initial list of pieces on the board
     * @throws NullPointerException if {@code pieces} is {@code null}
     */
    public BoardImpl(final List<Piece> pieces) {
        Objects.requireNonNull(pieces);
        this.pieces = new ArrayList<>(pieces);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Piece> getPieces() {
        return new ArrayList<>(this.pieces);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Piece getPieceAt(final Position position) {
        Objects.requireNonNull(position); 
        Piece piece = null; 
        for (final Piece p : this.pieces) {
            if (p.getPosition().equals(position)) {
                piece = p; 
            }
        }
        return piece; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletePiece(final Piece piece) {
        Objects.requireNonNull(piece); 
        this.pieces.remove(piece); 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPiece(final Piece piece) {
        Objects.requireNonNull(piece);
        this.pieces.add(piece);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board afterMove(final Move move) {
        Objects.requireNonNull(move);

        final Board newBoard = copyBoard();

        if (newBoard.getPieceAt(move.getTo()) != null) {
            newBoard.deletePiece(newBoard.getPieceAt(move.getTo()));
        }
        final Piece piece = Objects.requireNonNull(newBoard.getPieceAt(move.getFrom()));
        piece.setPosition(move.getTo());

        return newBoard;
    }

    // Creates a new board containing a fresh copy of this board's pieces.
    private Board copyBoard() {
        final List<Piece> copiedPieces = this.getPieces().stream()
            .map(PieceFactory::copyPiece)
            .toList();
        return Board.createBoard(copiedPieces);
    }

}

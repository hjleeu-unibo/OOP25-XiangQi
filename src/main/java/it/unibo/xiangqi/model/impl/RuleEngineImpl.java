package it.unibo.xiangqi.model.impl;
 
import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.model.api.RuleEngine;
 
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Implementation of the Xiangqi rule engine.
 * Responsible for validating legal moves and evaluating game states
 * as check, checkmate, draw, and the flying gneral rule.
 */
public final class RuleEngineImpl implements RuleEngine {

    @Override
    public boolean isCheck(final Player player, final Board board) {
        final Position generalPosition = findGeneralPosition(board, p -> p.getOwner().equals(player));

        // MoveCalculatorImpl evaluates legal moves on simulated boards where
        // a general may already have been captured; without this guard that would NPE here.
        if (generalPosition == null) {
            return false;
        }

        for (final Piece enemyPiece : selectPieces(board, p -> !p.getOwner().equals(player))) {
            for (final Move move : enemyPiece.getMoves(board)) {
                if (move.getTo().equals(generalPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Move> getLegalMoves(final Piece piece, final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final Move move : piece.getMoves(board)) {
            if (isLegalMove(move, piece.getOwner(), board)) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    @Override
    public boolean isCheckMate(final Player player, final Board board) {
        return isCheck(player, board) && !hasAnyLegalMove(player, board);
    }

    @Override
    public boolean isDraw(final Board board) {
        for (final Piece piece : board.getPieces()) {
            if (piece.getType() != PieceType.GENERAL && !piece.isDefensor()) {
                return false;
            }
        }
        return true;
    }

    // Checks whether the  player has at least one legal move.
    private boolean hasAnyLegalMove(final Player player, final Board board) {
        for (final Piece piece : selectPieces(board, p -> p.getOwner().equals(player))) {
            if (!getLegalMoves(piece, board).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Verifies whether a move is legal by simulating it and checking 
    // that it does not leave the player's general in check or create
    // a flying general situation.
    private boolean isLegalMove(final Move move, final Player player, final Board board) {
        final Board simulatedBoard = board.afterMove(move);

        return !isCheck(player, simulatedBoard) && !isFlyingGeneral(simulatedBoard);
    }

    // Checks whether the two generals face each other directly on the 
    // same coloumn with no pieces in between.
    private boolean isFlyingGeneral(final Board board) {
        final Position redGeneral = findGeneralPosition(board, p -> p.getOwner().getColor() == Color.RED);
        final Position blackGeneral = findGeneralPosition(board, p -> p.getOwner().getColor() == Color.BLACK);

        if (redGeneral == null || blackGeneral == null) {
            return false;
        }

        if (redGeneral.getCol() != blackGeneral.getCol()) {
            return false;
        }

        final int start = Math.min(redGeneral.getRow(), blackGeneral.getRow());
        final int end = Math.max(redGeneral.getRow(), blackGeneral.getRow());

        for (int y = start + 1; y < end; y++) {

            if (board.getPieceAt(new Position(y, redGeneral.getCol())) != null) {
                return false;
            }
        }
        return true;
    }

    // Selects all pieces belonging to the specified player
    private List<Piece> selectPieces(final Board board, final Predicate<Piece> owner) {
        final List<Piece> selectedPieces = new ArrayList<>();
        for (final Piece piece : board.getPieces()) {
            if (owner.test(piece)) {
                selectedPieces.add(piece);
            }
        }
        return selectedPieces;
    }

    // Finds the position of the general belonging to the specified player,
    // or null if it is not on the board (e.g. captured in a simulated position).
    private Position findGeneralPosition(final Board board, final Predicate<Piece> owner) {
        for (final Piece piece : board.getPieces()) {
            if (piece.getType() == PieceType.GENERAL && owner.test(piece)) {
                return piece.getPosition();
            }
        }
        return null;
    }
}

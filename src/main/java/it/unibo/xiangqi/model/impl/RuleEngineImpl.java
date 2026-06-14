package it.unibo.xiangqi.model.impl;
 
import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
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
public class RuleEngineImpl implements RuleEngine {

    
    private final GameModel gameModel;

    
    // Builds a new rule engine bound to the given game model.
    // It is used to simulate moves on a copy of the current game state. 
    public RuleEngineImpl(GameModel gameModel) {
        this.gameModel = gameModel;
    }


    @Override
    public boolean isCheck(final Player player, final Board board) {
        Position generalPosition = findGeneralPosition(board, p -> p.getOwner().equals(player));

        for(Piece enemyPiece : selectPieces(board, p-> !p.getOwner().equals(player))) {
            
            for(Move move : enemyPiece.getMoves(board)){
                if (move.getTo().equals(generalPosition)) {
                    return true;
                }
            }
        }  
        return false;

    }

    @Override
    public List<Move> getLegalMoves(final Piece piece, final Board board) {
        List<Move> legalMoves = new ArrayList<>();

        for(Move move : piece.getMoves(board)) {
            if (isLegalMove(move, piece.getOwner())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    // Verifies whether a move is legal by simulating it and checking 
    // that it does not leave the player's general in check or create
    // a flying general situation.
    private boolean isLegalMove(final Move move, final Player player){

        Board simulatedBoard = simulateMove(move);
        
        return !isCheck(player, simulatedBoard) && !isFlyingGeneral(simulatedBoard);

    }

    //Simulates the execution of a move on a copy of the current game state.
    private Board simulateMove(final Move move) {

        return gameModel.copyState().applyMove(move).getBoard();

    }

    @Override
    public boolean isCheckMate(final Player player, final Board board) {
        return isCheck(player, board) && !hasAnyLegalMove(player, board);
    }

    //Checks whether the  player has at least one legal move.
    private boolean hasAnyLegalMove(final Player player, final Board board) {
        for(Piece piece : selectPieces(board, p -> p.getOwner().equals(player))) {
            if (!getLegalMoves(piece, board).isEmpty()) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean isDraw(final Board board) {
        for(Piece piece : board.getPieces()) {
            if (!piece.isDefensor()) {
                return false;
            }
        }

        return true;
    }

    // Checks whether the two generals face each other directly on the 
    // same coloumn with no pieces in between.
    private boolean isFlyingGeneral(final Board board)  {

        Position redGeneral = findGeneralPosition(board, p -> p.getOwner().getColor() == Color.RED);
        Position blackGeneral = findGeneralPosition(board, p -> p.getOwner.getColor() == Color.BLACK);

        if (redGeneral.getX() != blackGeneral.getX()) {

            return false;
        }

        final int start = Math.min(redGeneral.getY(), blackGeneral.getY());
        final int end = Math.max(redGeneral.getY(), blackGeneral.getY());
    

        for(int y=start+1; y<end; y++) {

            if (board.getPieceAt(new Position(redGeneral.getX(), y)) != null) {
                return false;
            }
        }

        return true;
    }

    // Selects all pieces belonging to the specified player
    private List<Piece> selectPieces(final Board board, final Predicate<Piece> owner) {

        List<Piece> selectedPieces = new ArrayList<>();
        for(Piece piece : board.getPieces()) {
            if (owner.test(piece)) {
                selectedPieces.add(piece);
            }
        }

        return selectedPieces;
     }

     //Finds the position of the general belonging to the specified player
     private Position findGeneralPosition(final Board board, final Predicate<Piece> owner){

        for(Piece piece : board.getPieces()) {
            if (piece.getType() == PieceType.GENERAL && owner.test(piece)) {
                return piece.getPosition();
            }
        }

        throw new  IllegalStateException("The General has not been found.");

     }


}



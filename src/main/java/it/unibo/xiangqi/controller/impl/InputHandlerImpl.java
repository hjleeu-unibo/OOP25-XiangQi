package it.unibo.xiangqi.controller.impl;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.GameController;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.model.api.RuleEngine;
import it.unibo.xiangqi.view.api.GameView;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Input Handler.
 * 
 * Receives user interactions from the view and delegates the
 * corresponding actions to the appropriate components.
 */
public final class InputHandlerImpl implements InputHandler{

    private final GameController gameController;
    private final GameModel gameModel;
    private final RuleEngine ruleEngine;
    private final GameView gameView;

    /**
     * Builds a new InputHandler with the given collaborators.
     * 
     * @param gameController the controller coordinating the game flow
     * @param gameModel the model holding the game state
     * @param ruleEngine the engine evaluating game rules
     * @param gameView the view rendering the game
     */
    public InputHandlerImpl(final GameController gameController,
                            final GameModel gameModel,
                            final RuleEngine ruleEngine,
                            final GameView gameView) {

        this.gameController = gameController;
        this.gameModel = gameModel;
        this.ruleEngine = ruleEngine;
        this.gameView = gameView;
  
    }

    @Override
    public void onStart(final GameModeType mode) {
        gameController.start(mode);
    }

    @Override
    public void onSelect(final Position position) {
        final Board board = gameModel.getBoard();
        final Piece selectedPiece = board.getPieceAt(position);

        if (selectedPiece == null ||
                !selectedPiece.getOwner().equals(gameModel.getCurrentPlayer())) {
                    return;
        }

        final List<Move> legalMoves = ruleEngine.getLegalMoves(selectedPiece,board);
        final List<Position> legalDestinations = toDestinations(legalMoves);
        
        gameView.highlightCells(legalDestinations);
        
    }

    @Override
    public void onMove(final Move move) {
        final Player currentPlayer = gameModel.getCurrentPlayer();

        gameView.setPlayerDisabled(currentPlayer.getColor());
        gameView.setHintDisabled();

        gameModel.movePiece(move);
        gameController.nextTurn();
    }

    @Override
    public void onResume() {
        gameController.load();
    }

    @Override
    public void onHint() {
        gameController.hint();
    }

    @Override
    public void onExit() {
        gameController.save();
    }

    // Map a list of moves in a list of their final positions
    private List<Position> toDestinations(final List<Move> moves) {
        final List<Position> destinations = new ArrayList<>();
        for (Move move : moves) {
            destinations.add(move.getTo());
        }

        return destinations;
    }

}

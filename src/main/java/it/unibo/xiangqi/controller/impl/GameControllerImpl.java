package it.unibo.xiangqi.controller.impl;

import it.unibo.xiangqi.ai.api.MoveCalculator;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.GameController;
import it.unibo.xiangqi.controller.api.GameTimer;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameLoader;
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
 * Implementation of the game controller.
 * 
 * Manages the game flow by delegating operations to the 
 * underlying model components and updating the view.
 */
public final class GameControllerImpl implements GameController {

    private final GameModel gameModel;
    private final GameView gameView;
    private final RuleEngine ruleEngine;
    private final MoveCalculator moveCalculator;
    private final GameLoader gameLoader;
    private final GameTimer gameTimer;

    private boolean checkActive;

    /**
     * Builds a new game controller with the given collaborators.
     * 
     * @param gameModel the model holding the game state
     * @param gameView the view rendering the game
     * @param ruleEngine the engine evaluating game rules
     * @param moveCalculator the calculator computing bot moves and hints
     * @param gameLoader the loader managing save and load of the game state
     */
    public GameControllerImpl(final GameModel gameModel,
                              final GameView gameView,
                              final RuleEngine ruleEngine,
                              final MoveCalculator moveCalculator,
                              final GameLoader gameLoader,
                              final GameTimer gameTimer) {

        this.gameModel = gameModel;
        this.gameView = gameView;
        this.ruleEngine = ruleEngine;
        this.moveCalculator = moveCalculator;
        this.gameLoader = gameLoader;
        this.gameTimer = gameTimer;
    }

    @Override
    public void start(final GameModeType mode) {
        gameModel.startGame(mode);
        gameView.showGamePanel();
        dispatchTurn();
    }

     @Override
    public void applyMove(final Move move) {
        final Player currentPlayer= gameModel.getCurrentPlayer();

        gameView.setPlayerDisabled(currentPlayer.getColor());
        gameView.setHintButtonDisabled();

        gameModel.movePiece(move);
        nextTurn();

    }

    @Override
    public void select(Position position) {
        List<Position> destinations=getLegalDestinations(position);
        gameView.highlightCells(destinations);
    }


    @Override
    public void save() {
        switch(gameModel.getStatus()) {
            case NOT_STARTED:
                break;  
            case IN_PROGRESS:
                gameLoader.store(gameModel);
                break;
            case FINISHED:
                gameLoader.discardSave();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isResumeAvailable() {
        return gameLoader.hasStoredGame();
    }

    @Override
    public void load() {
        gameLoader.restore(gameModel);

        dispatchTurn();
    }

    @Override
    public void hint() {
        final Player currentPlayer = gameModel.getCurrentPlayer();

        if (gameModel.getHintsRemaining(currentPlayer) == 0) {
            return;
        }

        final Move suggestedMove = moveCalculator.getBestMove(gameModel);

        if (suggestedMove == null) {  
            return;
        }

        gameModel.useHint(currentPlayer);
        gameView.setHintButtonDisabled();
        gameView.showSuggestedMove(suggestedMove);

    }

   private void playerTurn() {
        final Player currentPlayer = gameModel.getCurrentPlayer();

        gameView.setPlayerEnabled(currentPlayer.getColor());
        updateHintState(currentPlayer);

        /* Haojie-Liu | Game Timer section. */
        gameTimer.startTurn(currentPlayer);
        /* Using Thread, this block will run at the same time of other methods. */
        new Thread(() -> {
            /* This loop will never stop until something happens (see below). */
            while (true) {
                try {
                    /* Wait 1s between every loop. */
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                final long turnRemaining = gameTimer.getTurnRemaining(currentPlayer);
                final long gameRemaining = gameTimer.getGameRemaining(currentPlayer);

                gameView.updateTimer(currentPlayer, turnRemaining, gameRemaining);

                if (gameTimer.isTurnExpired(currentPlayer) || gameTimer.isTotalExpired(currentPlayer)) {
                    gameTimer.stopTurn(currentPlayer);
                    gameModel.endGame();
                    gameView.showExpiredTime(currentPlayer);
                    return;
                }
            }
        }).start();
    }

    private void botTurn() {
        gameView.setHintButtonDisabled();

        final Move botMove = moveCalculator.getBestMove(gameModel);

        //reaching this branch indicates an unexpected state
        if (botMove == null) {

            gameModel.endGame();  
            return;
        }

        gameModel.movePiece(botMove);
        nextTurn();
    }

    
    private void nextTurn() {
        /* Haojie-Liu | Game timer section. */
        final Player currentPlayer = gameModel.getCurrentPlayer();
        gameTimer.stopTurn(currentPlayer);

        gameModel.switchTurn();
        dispatchTurn();
    }


    //triggers the current turn after checking the game state
    private void dispatchTurn() {
         final Player currentPlayer = gameModel.getCurrentPlayer();
         final Board board = gameModel.getBoard();

         gameView.updateBoard(board);

         if (checkActive) {
            gameView.resetCheck();
            checkActive = false;
         }

         if (ruleEngine.isDraw(board)) {
            gameView.showDraw();
            gameModel.endGame();
            return;
         }

         if (ruleEngine.isCheckMate(currentPlayer, board)) {
            Player winner = gameModel.getOpponentPlayer();
            gameView.showWinner(winner.getColor());
            gameModel.endGame();
            return;
         }

         if (ruleEngine.isCheck(currentPlayer, board)) {
            gameView.showCheck(); 
            checkActive = true;
         }   

         if (currentPlayer.isHuman()) {
            playerTurn();
         } else {
            botTurn();
         }
    }
    
    // enables or disables the hint button based on how many
    // hints the player has left in this game 
    private void updateHintState(final Player currentPlayer) {

            if (gameModel.getHintsRemaining(currentPlayer) > 0){
                gameView.setHintButtonEnabled();
            } else {
                gameView.setHintButtonDisabled();
            }
     }


     private List<Position> getLegalDestinations(final Position position) {
        final Board board = gameModel.getBoard();
        final Piece selectedPiece = board.getPieceAt(position);

        if (selectedPiece == null ||
                !selectedPiece.getOwner().equals(gameModel.getCurrentPlayer())) {

            return List.of();
        }

        final List<Move> legalMoves = ruleEngine.getLegalMoves(selectedPiece, board);
        return toDestinations(legalMoves);
    }

    private List<Position> toDestinations (final List<Move> moves) {
        final List<Position> destinations = new ArrayList<>();
        for (Move move: moves) {
            destinations.add(move.getTo());
        }

        return destinations;
    }
  
}

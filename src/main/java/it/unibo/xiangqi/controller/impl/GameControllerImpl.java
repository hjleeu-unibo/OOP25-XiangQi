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
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implementation of the game controller.
 *
 * <p>Manages the game flow by delegating operations to the
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
     * @param gameTimer the timer tracking turn and game time
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public GameControllerImpl(final GameModel gameModel,
                              final GameView gameView,
                              final RuleEngine ruleEngine,
                              final MoveCalculator moveCalculator,
                              final GameLoader gameLoader,
                              final GameTimer gameTimer) {

        this.gameModel = Objects.requireNonNull(gameModel);
        this.gameView = Objects.requireNonNull(gameView);
        this.ruleEngine = Objects.requireNonNull(ruleEngine);
        this.moveCalculator = Objects.requireNonNull(moveCalculator);
        this.gameLoader = Objects.requireNonNull(gameLoader);
        this.gameTimer = Objects.requireNonNull(gameTimer);
    }

    @Override
    public void start(final GameModeType mode) {
        gameModel.startGame(mode);
        gameView.showGamePanel();
        dispatchTurn();
    }

    @Override
    public void makeMove(final Move move) {
        final Player currentPlayer = gameModel.getCurrentPlayer();

        gameView.setPlayerDisabled(currentPlayer.getColor());
        gameView.setHintButtonDisabled();

        gameModel.movePiece(move);
        nextTurn();
    }

    @Override
    public void select(final Position position) {
        final List<Position> destinations = getLegalDestinations(position);
        gameView.highlightCells(destinations);
    }

    @Override
    public void save() {
        switch (gameModel.getStatus()) {
            case NOT_STARTED:
                break;
            case IN_PROGRESS:
                gameLoader.store(gameModel);
                break;
            case FINISHED:
                gameLoader.discardSave();
                break;
        }
    }

    @Override
    public void load() {
        if (!isResumeAvailable()) {
            gameView.showResumeNotification();
            return;
        }
        gameLoader.restore(gameModel);
        gameView.showGamePanel();

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

    // reports whether a previously saved game is available to be resumed
    private boolean isResumeAvailable() {
        return gameLoader.hasStoredGame();
    }

    // enables the current human player's controls and starts their turn
    // timer, polling it every second on a background thread and ending
    // the game if the time runs out
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
                } catch (final InterruptedException e) {
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

    // starts the bot's turn timer on a background thread and, in parallel,
    // computes and applies the bot's move, advancing to the next turn once
    // it is played
    private void botTurn() {
        final Player currentPlayer = gameModel.getCurrentPlayer();
        gameView.setHintButtonDisabled();

        /* Haojie-Liu | Bot timer section. */
        gameTimer.startTurn(currentPlayer);
        new Thread(() -> {
            /* This loop will never stop until something happens (see below). */
            while (true) {
                try {
                    /* Wait 1s between every loop. */
                    Thread.sleep(1000);
                } catch (final InterruptedException e) {
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

        /* Haojie-Liu | Using thread to avoid that the ui will be blocked for async. */
        new Thread(() -> {
            final Move botMove = moveCalculator.getBestMove(gameModel);

            // reaching this branch indicates an unexpected state
            if (botMove == null) {
                gameTimer.stopTurn(currentPlayer);
                gameModel.endGame();
                return;
            }

            gameModel.movePiece(botMove);
            nextTurn();
        }).start();
    }

    // stops the current player's timer, switches turn and dispatches the next one
    private void nextTurn() {
        final Player currentPlayer = gameModel.getCurrentPlayer();
        gameTimer.stopTurn(currentPlayer);

        gameModel.switchTurn();
        dispatchTurn();
    }

    // triggers the current turn after checking the game state
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
            final Player winner = gameModel.getOpponentPlayer();
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
        if (gameModel.getHintsRemaining(currentPlayer) > 0) {
            gameView.setHintButtonEnabled();
        } else {
            gameView.setHintButtonDisabled();
        }
    }

    // returns the legal destinations for the piece at the given position,
    // or an empty list if there is no piece there or it belongs to the other player
    private List<Position> getLegalDestinations(final Position position) {
        final Board board = gameModel.getBoard();
        final Piece selectedPiece = board.getPieceAt(position);

        if (selectedPiece == null
                || !selectedPiece.getOwner().equals(gameModel.getCurrentPlayer())) {
            return List.of();
        }

        final List<Move> legalMoves = ruleEngine.getLegalMoves(selectedPiece, board);
        return toDestinations(legalMoves);
    }

    // extracts the destination positions from a list of moves
    private List<Position> toDestinations(final List<Move> moves) {
        final List<Position> destinations = new ArrayList<>();
        for (final Move move : moves) {
            destinations.add(move.getTo());
        }

        return destinations;
    }
}

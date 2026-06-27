package it.unibo.xiangqi.controller.impl;

import it.unibo.xiangqi.ai.api.MoveCalculator;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.GameController;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameLoader;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.RuleEngine;
import it.unibo.xiangqi.view.api.GameView;

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
                              final GameLoader gameLoader) {

        this.gameModel = gameModel;
        this.gameView = gameView;
        this.ruleEngine = ruleEngine;
        this.moveCalculator = moveCalculator;
        this.gameLoader = gameLoader;                        
    }

    @Override
    public void start(final GameModeType mode) {
        gameModel.startGame(mode);
        dispatchTurn();
    }

    @Override
    public void playerTurn() {
        final Player currentPlayer = gameModel.getCurrentPlayer();

        gameView.setPlayerEnabled(currentPlayer.getColor());
        updateHintState(currentPlayer);
    }

    @Override
    public void botTurn() {
        gameView.setHintDisabled();

        final Move botMove = moveCalculator.getBestMove(gameModel);

        //reaching this branch indicates an unexpected state
        if (botMove == null) {

            gameModel.endGame();  
            return;
        }

        gameModel.movePiece(botMove);
        nextTurn();
    }

    @Override
    public void nextTurn() {
        gameModel.switchTurn();
        dispatchTurn();
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
        gameView.setHintDisabled();
        gameView.showSuggestedMove(suggestedMove);

    }

    //triggers the current turn after checking the game state
    private void dispatchTurn() {
         final Player currentPlayer = gameModel.getCurrentPlayer();
         final Board board = gameModel.getBoard();

         gameView.update();

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
            Player winner = getEnemy(currentPlayer);
            gameView.showWinner(winner);
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
                gameView.setHintEnabled();
            } else {
                gameView.setHintDisabled();
            }
     }

     //returns the opponent of the given player
     private Player getEnemy (final Player player) {
        for (Player p : gameModel.getPlayers()) {

            if (!p.equals(player)) {
                return p;
            }
        }

        throw new IllegalStateException("The enemy was not found");
     }
  
}

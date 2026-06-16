package it.unibo.xiangqi.controller.impl;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.GameController;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameLoader;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.MoveCalculator;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.RuleEngine;
import it.unibo.xiangqi.view.api.GameView;

/**
 * Implemetation of the game controller.
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

        final Move botMove = moveCalculator.getBestMove();

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
        gameLoader.store(gameModel);
    }

    @Override
    public void load() {
        gameLoader.restore(gameModel);

        dispatchTurn();
    }

    @Override
    public void hint() {
        final Player currentPlayer = gameModel.getCurrentPlayer();

        //reaching this branch indicates an unexpected state
        if (gameModel.getHintsRemaining(currentPlayer) == 0) {
            return;
        }

        final Move suggestedMove = moveCalculator.getBestMove();

        if (suggestedMove == null) {  
            return;
        }

        gameModel.useHint(currentPlayer);
        gameView.setHintDisabled();
        gameView.showSuggestedMove(suggestedMove);

    }

    private void dispatchTurn() {
         final Player currentPlayer = gameModel.getCurrentPlayer();
         final Board board = gameModel.getBoard();

         gameView.update();

         if (ruleEngine.isDraw(board)) {
            gameView.showDraw();
            gameModel.endGame();
            return;
         }

         if (ruleEngine.isCheckMate(currentPlayer, board)) {
            gameView.showCheckMate(currentPlayer);
            gameModel.endGame();
            return;
         }

         if (ruleEngine.isCheck(currentPlayer, board)) {
            gameView.showCheck(); 
         }   

         if (currentPlayer.isHuman()) {
            playerTurn();
         } else {
            botTurn();
         }

         
    }

    private void updateHintState(final Player currentPlayer) {

            if (gameModel.getHintsRemaining(currentPlayer) > 0){
                gameView.setHintEnabled();
            } else {
                gameView.setHintDisabled();
            }
     }
  
}

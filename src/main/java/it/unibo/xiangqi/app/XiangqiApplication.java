package it.unibo.xiangqi.app;

import java.util.List;

import it.unibo.xiangqi.ai.api.MoveCalculator;
import it.unibo.xiangqi.ai.impl.MoveCalculatorImpl;
import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.controller.api.GameController;
import it.unibo.xiangqi.controller.api.GameTimer;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.controller.impl.GameControllerImpl;
import it.unibo.xiangqi.controller.impl.GameTimerImpl;
import it.unibo.xiangqi.controller.impl.InputHandlerImpl;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameLoader;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.RuleEngine;
import it.unibo.xiangqi.model.impl.GameLoaderImpl;
import it.unibo.xiangqi.model.impl.GameModelImpl;
import it.unibo.xiangqi.model.impl.PlayerImpl;
import it.unibo.xiangqi.model.impl.RuleEngineImpl;
import it.unibo.xiangqi.view.api.GameView;
import it.unibo.xiangqi.view.impl.GameViewImpl;

/**
 * Application entry point.
 * Creates and connects all MVC components together,
 * then shows the main menu.
 */
public final class XiangqiApplication {

    private XiangqiApplication() { }

    public static void main(String[] args) {

        // Create the graphical user interface.
        final GameView view = new GameViewImpl();

        // Temporary players required to initialize the model.
        // They will be replaced when a new game is started.
        final Player placeholderRed = new PlayerImpl(Color.RED, true);
        final Player placeholderBlack = new PlayerImpl(Color.BLACK, true);

        // Create an initially empty game model.
        final GameModel gameModel = new GameModelImpl(
            Board.createBoard(List.of()),
            List.of(placeholderRed, placeholderBlack)
        );

        // Rule engine validates moves according to Xiangqi rules.
        final RuleEngine ruleEngine = new RuleEngineImpl();

        // AI component computes moves using the rule engine.
        final MoveCalculator moveCalculator = new MoveCalculatorImpl(ruleEngine);

        // Components responsible for saving/loading games and time management.
        final GameLoader gameLoader = new GameLoaderImpl();
        final GameTimer gameTimer = new GameTimerImpl();

        // Main controller coordinating interactions between model and view.
        final GameController gameController = new GameControllerImpl(
            gameModel, view, ruleEngine, moveCalculator, gameLoader, gameTimer
        );

        // Handles all user input coming from the graphical interface.
        final InputHandler inputHandler = new InputHandlerImpl(gameController);

        // Connect the view to the input handler.
        // The application starts on the main menu, where the user can
        // choose to start a new game or resume a saved one.
        view.setInputHandler(inputHandler);
    }
}
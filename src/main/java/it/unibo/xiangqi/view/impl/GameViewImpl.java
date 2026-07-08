package it.unibo.xiangqi.view.impl;

import java.util.List;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.view.api.GameView;

/**
 * Game view implementation based on Swing components.
 */
public class GameViewImpl implements GameView {

    private final JFrame frame; 
    private final JPanel rootPanel; 
    private final CardLayout cardLayout;
    private final MenuPanel menuPanel;
    private final BoardPanel boardPanel;
    private InputHandler inputHandler; 

    /**
     * Creates a new game view.
     *
     * <p>Initializes the graphical user interface, configures the main window,
     * creates the menu and game panels, and displays the main menu.</p>
     */
    public GameViewImpl() {

        /* Main window and panels initialization */
        frame = new JFrame("Xiangqi");
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);
        menuPanel = new MenuPanel();
        boardPanel = new BoardPanel();

        /* Register the application views */
        rootPanel.add(menuPanel, "MENU");
        rootPanel.add(boardPanel, "GAME");
        frame.setContentPane(rootPanel);
        cardLayout.show(rootPanel, "MENU");

        /* Configure the window size relative to the screen resolution */
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int height = (int) (screenSize.height * 0.6); 
        final int width = (int) (screenSize.width * 0.6);

        frame.setSize(width, height); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        /* Notify the controller before closing the application */
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                if (inputHandler != null) {
                    inputHandler.onExit();
                }
                System.exit(0);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBoard(final Board board) {
        boardPanel.updateBoard(board);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlayerEnabled(final Color c) {
        boardPanel.setPlayerEnabled(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlayerDisabled(final Color c) {
        boardPanel.setPlayerDisabled(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHintButtonEnabled() {
        boardPanel.setHintButtonEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHintButtonDisabled() {
        boardPanel.setHintButtonDisabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void highlightCells(final List<Position> cells) {
        boardPanel.highlightCells(cells);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showSuggestedMove(final Move move) {
        boardPanel.showSuggestedMove(move);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInputHandler(final InputHandler handler) {
        Objects.requireNonNull(handler); 
        this.inputHandler = handler; 
        boardPanel.setInputHandler(handler);
        menuPanel.setInputHandler(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showCheck() {
        this.boardPanel.showCheck();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetCheck() {
        this.boardPanel.resetCheck();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWinner(final Color color) {
        this.boardPanel.showWinner(color);
    }

    private void setGameSize() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int height = (int) (screenSize.height * 0.8); 
        final int width = height;
        this.frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showGamePanel() {
        this.setGameSize();
        this.cardLayout.show(rootPanel, "GAME");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTimer(final Player player, final long turnRemaining, final long gameRemaining) {
        boardPanel.updateTimer(player, turnRemaining, gameRemaining);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showExpiredTime(final Player player) {
        boardPanel.showExpiredTime(player);
        Color enemyColor = Color.RED;
        if (player.getColor() == Color.RED) {
            enemyColor = Color.BLACK;
        }
        showWinner(enemyColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showDraw() {
        boardPanel.showDraw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showResumeNotification() {
        this.menuPanel.showResumeNotification();
    }
}

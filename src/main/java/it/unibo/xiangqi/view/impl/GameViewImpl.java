package it.unibo.xiangqi.view.impl;

import java.util.List;
import java.util.Objects;

import javax.swing.*;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.view.api.GameView;

/**
 * Game view implementation based on Swing components.
 */
public class GameViewImpl implements GameView{

    private JFrame frame; 
    private JPanel rootPanel; 
    private CardLayout cardLayout;
    private MenuPanel menuPanel;
    private BoardPanel boardPanel;
    private InputHandler inputHandler; 

    /**
     * Creates a new game view.
     *
     * <p>Initializes the graphical user interface, configures the main window,
     * creates the menu and game panels, and displays the main menu.</p>
     */
    public GameViewImpl() {

        frame = new JFrame("Xiangqi");
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);
        menuPanel = new MenuPanel();
        boardPanel = new BoardPanel();

        rootPanel.add(menuPanel, "MENU");
        rootPanel.add(boardPanel, "GAME");
        frame.setContentPane(rootPanel);
        cardLayout.show(rootPanel, "MENU");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int)(screenSize.height * 0.6); 
        int width = (int)(screenSize.width * 0.6);

        frame.setSize(width, height); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
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
    public void updateBoard(Board board) {
        boardPanel.updateBoard(board);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void setPlayerEnabled(Color c) {
        boardPanel.setPlayerEnabled(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlayerDisabled(Color c) {
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
    public void highlightCells(List<Position> cells) {
        boardPanel.highlightCells(cells);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showSuggestedMove(Move move) {
        boardPanel.showSuggestedMove(move);
    }

    /**
     * {@inheritDoc}
     */
    public void setInputHandler(InputHandler handler) {
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
    public void showWinner(Color color) {
        this.boardPanel.showWinner(color);
    }

    /**
     * {@inheritDoc}
     */
    private void setGameSize(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int)(screenSize.height * 0.8); 
        int width = height;
        this.frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
    }

    /**
     * {@inheritDoc}
     */
    public void showGamePanel(){
        this.setGameSize();
        this.cardLayout.show(rootPanel, "GAME");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showDraw() {
        boardPanel.showDraw();
    }

}

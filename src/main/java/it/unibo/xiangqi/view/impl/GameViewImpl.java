package it.unibo.xiangqi.view.impl;

import java.util.List;

import javax.swing.*;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.common.Move;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.view.api.GameView;

public class GameViewImpl implements GameView{

    private JFrame frame; 
    private JPanel rootPanel; 
    private CardLayout cardLayout;
    private MenuPanel menuPanel;
    private BoardPanel boardPanel;
    private InputHandler inputHandler; 


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
    

    @Override
    public void updateBoard(Board board) {
        boardPanel.updateBoard(board);
    }

    @Override
    public void setPlayerEnabled(Color c) {
        boardPanel.setPlayerEnabled(c);
    }

    @Override
    public void setPlayerDisabled(Color c) {
        boardPanel.setPlayerDisabled(c);
    }

    @Override
    public void setHintButtonEnabled() {
        boardPanel.setHintButtonEnabled();
    }

    @Override
    public void setHintButtonDisabled() {
        boardPanel.setHintButtonDisabled();
    }

    @Override
    public void highlightCells(List<Position> cells) {
        boardPanel.highlightCells(cells);
    }

    @Override
    public void showSuggestedMove(Move move) {
        boardPanel.showSuggestedMove(move);
    }

    public void setInputHandler(InputHandler handler) {
        this.inputHandler = handler; 
        boardPanel.setInputHandler(handler);
        menuPanel.setInputHandler(handler);
    }

    @Override
    public void showCheck() {
        this.boardPanel.showCheck();
    }

    @Override
    public void resetCheck() {
        this.boardPanel.resetCheck();
    }

    @Override
    public void showWinner(Color color) {
        this.boardPanel.showWinner(color);
    }

    private void setGameSize(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int)(screenSize.height * 0.8); 
        int width = height;
        this.frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
    }

    public void showGamePanel(){
        this.setGameSize();
        this.cardLayout.show(rootPanel, "GAME");
    }
    
}

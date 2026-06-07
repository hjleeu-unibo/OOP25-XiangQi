package it.unibo.xiangqi.view.impl;

import java.util.List;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.view.api.BoardRenderer;
import it.unibo.xiangqi.view.api.GameView;

public class GameViewImpl implements GameView{

    private BoardRenderer renderer;
    private InputHandler inputHandler; 
    private List<Position> highlightedCells;
    private Position selectedPosition;
    private JButton hintButton;
    private Board currentBoard;
    
    private JFrame frame; 
    private JPanel boardPanel; 
    private JButton[][] cells;


    public GameViewImpl() {
    this.frame = new JFrame("Xiangqi");
    this.boardPanel = new JPanel(new GridLayout(10, 9));
    this.cells = new JButton[10][9];
    /*Frame dimensions */
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int height = (int) (screenSize.height * 0.8);
    int width = height; 
    frame.setSize(width, height);
    frame.setLocationRelativeTo(null); // centra la finestra
    
    for (int row = 0; row < 10; row++) {
        for (int col = 0; col < 9; col++) {
            JButton button = new JButton();
            cells[row][col] = button;
            boardPanel.add(button);
        }
    }

    frame.add(boardPanel);
    frame.setVisible(true);
}
    

    @Override
    public void updateBoard(Board board) {
    }

    @Override
    public void setPlayerEnabled(Color c) {
    }

    @Override
    public void setPlayerDisabled(Color c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPlayerDisabled'");
    }

    @Override
    public void setHintButtonEnabled() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setHintButtonEnabled'");
    }

    @Override
    public void setHintButtonDisabled() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setHintButtonDisabled'");
    }

    @Override
    public void highlightCells(List<Position> cells) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'highlightCells'");
    }

    @Override
    public void showSuggestedMove(Move move) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showSuggestedMove'");
    }
    
}

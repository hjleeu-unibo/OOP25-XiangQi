package it.unibo.xiangqi.view.impl;

import java.util.List;

import javax.swing.*;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.Position;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.view.api.GameView;

public class GameViewImpl implements GameView{

    private JFrame frame; 
    private JPanel rootPanel; 
    private CardLayout cardLayout;
    private MenuPanel menuPanel;
    private BoardPanel boardPanel;


    public GameViewImpl() {

        frame = new JFrame("Xiangqi");
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);
        menuPanel = new MenuPanel();
        boardPanel = new BoardPanel();

        rootPanel.add(menuPanel, "MENU");
        rootPanel.add(boardPanel, "GAME");
        frame.setContentPane(rootPanel);
        cardLayout.show(rootPanel, "GAME");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int)(screenSize.height * 0.7); 
        int width = (int)(screenSize.height * 0.8);

        frame.setSize(width, height); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

}
    

    @Override
    public void updateBoard(Board board) {
        boardPanel.updateBoard(board);
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

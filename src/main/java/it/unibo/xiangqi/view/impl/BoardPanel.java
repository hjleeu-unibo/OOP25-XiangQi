package it.unibo.xiangqi.view.impl;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.view.api.BoardView;
import it.unibo.xiangqi.view.api.HintView;
import it.unibo.xiangqi.view.api.PlayerView;


public class BoardPanel extends JPanel implements BoardView, HintView, PlayerView{

    private JButton[][] cells;
    private JButton hintButton;
    private JPanel boardGrid; 
    private JPanel sidePanel; 

    public BoardPanel() {

        boardGrid = new JPanel(new GridLayout(10, 9));
        sidePanel = new JPanel();
        hintButton = new JButton("Hint");
        cells = new JButton[10][9];

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                JButton button = new JButton();
                cells[row][col] = button;
                boardGrid.add(button);
            }
        }

        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.add(hintButton);
        this.setLayout(new BorderLayout());
        this.add(boardGrid, BorderLayout.CENTER); 
        this.add(sidePanel, BorderLayout.EAST);
}

    @Override
    public void updateBoard(Board board) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateBoard'");
    }

    @Override
    public void highlightCells(List<Position> positions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'highlightCells'");
    }

    @Override
    public void showSuggestedMove(Move move) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showSuggestedMove'");
    }

    @Override
    public void setPlayerEnabled(Color c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPlayerEnabled'");
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
    
}

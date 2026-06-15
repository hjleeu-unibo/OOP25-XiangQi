package it.unibo.xiangqi.view.impl;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.Move;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.common.api.Position;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.view.api.BoardView;
import it.unibo.xiangqi.view.api.HintView;
import it.unibo.xiangqi.view.api.PlayerView;


public class BoardPanel extends JPanel implements BoardView, HintView, PlayerView{

    private JButton[][] cells;
    private JButton hintButton;
    private JPanel boardGrid; 
    private JPanel sidePanel; 
    private Board currentBoard; 
    private List<Position> highlightedCells;

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

        this.currentBoard = board; 

        for (int row = 0; row < 10; row++ ){
            for (int col = 0; col < 9; col++ ){
                
                Position pos = new Position(row, col); 
                Piece piece = this.currentBoard.getPieceAt(pos); 

                if (piece == null){
                    cells[row][col].setText("");
                }else{
                    cells[row][col].setText(pieceToText(piece)); 
                }
            }
        }
    }

    private String pieceToText(Piece piece){

        PieceType type = piece.getType(); 
        Color c = piece.getOwner().getColor(); 

        return c.getSymbol()+type.getSymbol(); 
    }

    @Override
    public void highlightCells(List<Position> positions) {
        this.highlightedCells = positions; 

        for (int row = 0; row < 10; row++ ){
            for (int col = 0; col < 9; col++ ){
                Position pos = new Position(row, col); 
                if(this.highlightedCells.contains(pos)){
                    highlightCell(pos, java.awt.Color.YELLOW);
                }
            }
        }

    }

    @Override
    public void showSuggestedMove(Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        highlightCell(from, java.awt.Color.GREEN);
        highlightCell(to, java.awt.Color.GREEN);
    }

    private void highlightCell(Position pos, java.awt.Color color) {
        int row = pos.getRow();
        int col = pos.getCol();
        cells[row][col].setBackground(color);
    }

    private void resetHighlights() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setBackground(null);
            }
        }
    }

    @Override
    public void setPlayerEnabled(Color c) {
        this.setPlayer(true, c);
    }

    @Override
    public void setPlayerDisabled(Color c) {
        this.setPlayer(false, c);
    }

    private void setPlayer(boolean enable, Color c){

        for (int row = 0; row < 10; row++ ){
            for (int col = 0; col < 9; col++ ){
                
                Position pos = new Position(row, col); 
                Piece piece = this.currentBoard.getPieceAt(pos); 
                
                if (piece != null && piece.getOwner().getColor() == c){
                    cells[row][col].setEnabled(enable);
                }
            }
        }

    }

    @Override
    public void setHintButtonEnabled() {
        this.hintButton.setEnabled(true);
    }

    @Override
    public void setHintButtonDisabled() {
        this.hintButton.setEnabled(false);
    }
    
}

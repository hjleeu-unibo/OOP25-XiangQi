package it.unibo.xiangqi.view.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.common.Move;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Piece;


public class BoardPanel extends JPanel {

    private JButton[][] cells;
    private JButton hintButton;
    private JPanel boardGrid; 
    private JPanel sidePanel; 
    private Board currentBoard; 
    private List<Position> highlightedCells;
    private Position selectedCell; 
    private InputHandler inputHandler; 
    private int cellSize; 
    private JPanel notificationPanel; 
    private JLabel notificationLabel;

    public BoardPanel() {

        boardGrid = new JPanel(new GridLayout(10, 9));
        sidePanel = new JPanel();
        hintButton = new JButton("HINT"); 
        cells = new JButton[10][9];

        notificationPanel = new JPanel(); 
        notificationLabel = new JLabel();
        notificationPanel.add(notificationLabel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.cellSize = (int)(screenSize.height * 0.05);
        notificationPanel.setPreferredSize( new Dimension(0, (int)(screenSize.height * 0.075)));

        hintButton.addActionListener(e -> {
            if(this.inputHandler != null)
                inputHandler.onHint(); 
            else{
                throw new IllegalStateException("input handler has not been setted"); 
            }
        });

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                JButton button = new JButton();
                button.addActionListener(new CellListener(this, new Position(row, col)));
                cells[row][col] = button;
                boardGrid.add(button);
            }
        }

        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.add(hintButton);
        this.setLayout(new BorderLayout());
        this.add(boardGrid, BorderLayout.CENTER); 
        this.add(sidePanel, BorderLayout.EAST);
        this.add(notificationPanel, BorderLayout.SOUTH); 
}

    public void updateBoard(Board board) {

        if( board != null){
            this.currentBoard = board;
        }else{
            throw new IllegalArgumentException("argument 'board' is null"); 
        }

        for (int row = 0; row < 10; row++ ){
            for (int col = 0; col < 9; col++ ){
                
                Position pos = new Position(row, col); 
                Piece piece = this.currentBoard.getPieceAt(pos); 

                if (piece == null){
                    cells[row][col].setText("");
                }else{
                    cells[row][col].setIcon(pieceToIcon(piece));  
                }
            }
        }
    }

    private ImageIcon pathToIcon(String path, int width, int height){
        if(path == null){
            throw new IllegalArgumentException(); 
        }
        URL url = ClassLoader.getSystemResource(path); 
        ImageIcon icon = new ImageIcon(url); 
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled); 
    }

    private ImageIcon pieceToIcon(Piece piece){
        String color = piece.getOwner().getColor().getName();
        String type = piece.getType().getName();
        if(color == null || type == null){
            throw new NullPointerException(); 
        }
        String path = "icons/" + color + "_" + type + ".png";
        return this.pathToIcon(path, this.cellSize, this.cellSize); 
    }

    public void highlightCells(List<Position> positions) {
        if(positions != null){
            this.highlightedCells = positions;
        } else{
            throw new IllegalArgumentException(); 
        }

        this.disableAll();
        for (int row = 0; row < 10; row++ ){
            for (int col = 0; col < 9; col++ ){
                Position pos = new Position(row, col); 
                if(this.highlightedCells.contains(pos)){
                    highlightCell(pos, java.awt.Color.YELLOW);
                    cells[row][col].setEnabled(true);
                }
            }
        }

        int row = this.selectedCell.getRow(); 
        int col = this.selectedCell.getCol(); 
        cells[row][col].setEnabled(true);
    }

    private void disableAll(){
        for (int row = 0; row < 10; row++ ){
            for (int col = 0; col < 9; col++ ){
                cells[row][col].setEnabled(false);
            }
        }
    }

    public void showSuggestedMove(Move move) {
        if(move == null){
            throw new NullPointerException(); 
        }
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

    public void setPlayerEnabled(Color c) {
        this.disableAll();
        this.setPlayer(true, Objects.requireNonNull(c));
    }

    public void setPlayerDisabled(Color c) {
        this.disableAll();
        this.setPlayer(false, Objects.requireNonNull(c));
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

    public void setHintButtonEnabled() {
        this.hintButton.setEnabled(true);
    }

    public void setHintButtonDisabled() {
        this.hintButton.setEnabled(false);
    }
    
    public void setInputHandler(InputHandler inputHandler) {
        Objects.requireNonNull(inputHandler); 
        this.inputHandler = inputHandler;
    }

    public InputHandler getInputHandler(){
        return this.inputHandler; 
    }

    public void handleCellClick(Position position){
        Objects.requireNonNull(position); 
        if(this.selectedCell == null){
            this.selectedCell = position; 
            this.inputHandler.onSelect(position);
        }else if(this.selectedCell.equals(position)){
            Color c = this.currentBoard.getPieceAt(position).getOwner().getColor(); 
            this.setPlayerEnabled(c);
            this.resetHighlights();
            this.selectedCell = null; 
        }else{
            this.inputHandler.onMove(new Move(this.selectedCell, position)); 
            this.selectedCell = null; 
            this.disableAll();
            this.resetHighlights();
        }
    }

    public void showCheck(){
        String path = "notifications/check.png";
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h = (int)(screenSize.height * 0.075);
        int w = (int)(screenSize.height * 0.4);
        
        notificationLabel.setIcon(this.pathToIcon(path, w, h));
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    public void resetCheck() {
        notificationLabel.setIcon(null);
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    public void showWinner(Color color){
        this.disableAll();
        String path; 
        if (color == Color.BLACK){
            path = "notifications/black_wins.png"; 
        }else{
            path = "notifications/red_wins.png";
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h = (int)(screenSize.height * 0.075);
        int w = (int)(screenSize.height * 0.65);

        notificationLabel.setIcon(this.pathToIcon(path, w, h));
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }
    
}

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
import it.unibo.xiangqi.view.api.GameView;

/**
 * Represents the graphical panel containing the Xiangqi game board.
 *
 * <p>This panel manages the board cells, player interactions, hint button,
 * and game notifications. It is responsible for updating the visual state
 * of the game according to the current board configuration.</p>
 *
 * <p>This class is part of the view implementation and handles the Swing
 * components used to display the game.</p>
 */
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

    /**
     * Creates a new board panel.
     *
     * <p>The panel initializes the board grid, control buttons,
     * notification area, and cell listeners.</p>
     */
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

    /**
     * {@link GameView#updateBoard(Board)} implementation.
     * 
     * @param board the new board configuration to display
     * @throws NullPointerException if board is null
     */
    public void updateBoard(Board board) {

        if( board != null){
            this.currentBoard = board;
        }else{
            throw new NullPointerException("argument 'board' is null"); 
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

    /**
     * Internal helper method
     * 
     * @hidden
     */
    private ImageIcon pathToIcon(String path, int width, int height){
        Objects.requireNonNull(path); 
        URL url = ClassLoader.getSystemResource(path); 
        ImageIcon icon = new ImageIcon(url); 
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled); 
    }

    /**
     * Internal helper method
     * 
     * @hidden
     */
    private ImageIcon pieceToIcon(Piece piece){
        String color = piece.getOwner().getColor().getName();
        String type = piece.getType().getName();
        if(color == null || type == null){
            throw new NullPointerException(); 
        }
        String path = "icons/" + color + "_" + type + ".png";
        return this.pathToIcon(path, this.cellSize, this.cellSize); 
    }

    /**
     * {@link GameView#highlightCells(List)} implementation.
     * 
     * @param positions the list of positions to highlight
     * @throws NullPointerException if {@code positions} is null
     */
    public void highlightCells(List<Position> positions) {
        if(positions != null){
            this.highlightedCells = positions;
        } else{
            throw new NullPointerException("positions is null"); 
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

    /**
     * Internal helper method
     * 
     * @hidden
     */
    private void disableAll(){
        for (int row = 0; row < 10; row++ ){
            for (int col = 0; col < 9; col++ ){
                cells[row][col].setEnabled(false);
            }
        }
    }

    /**
     * {@link GameView#showSuggestedMove(Move)} implementation.
     * 
     * @param move the move to display
     * @throws NullPointerException if {@code move} is null
     */
    public void showSuggestedMove(Move move) {
        Objects.requireNonNull(move); 
        Position from = move.getFrom();
        Position to = move.getTo();
        highlightCell(from, java.awt.Color.GREEN);
        highlightCell(to, java.awt.Color.GREEN);
    }

    /**
     * Internal helper method
     * 
     * @hidden
     */
    private void highlightCell(Position pos, java.awt.Color color) {
        int row = pos.getRow();
        int col = pos.getCol();
        cells[row][col].setBackground(color);
    }

    /**
     * Internal helper method
     * 
     * @hidden
     */
    private void resetHighlights() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setBackground(null);
            }
        }
    }

    /**
     * {@link GameView#setPlayerEnabled(Color)} implementation.
     * 
     * @param c the color of the player to enable
     * @throws NullPointerException if {@code c} is null
     */
    public void setPlayerEnabled(Color c) {
        this.disableAll();
        this.setPlayer(true, Objects.requireNonNull(c));
    }

    /**
     * {@link GameView#setPlayerDisabled(Color)} implementation.
     * 
     * @param c the color of the player to disable
     * @throws NullPointerException if {@code c} is null
     */
    public void setPlayerDisabled(Color c) {
        this.disableAll();
        this.setPlayer(false, Objects.requireNonNull(c));
    }

    /**
     * Internal helper method
     * 
     * @hidden
     */
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

    /**
     * {@link GameView#setHintButtonEnabled()} implementation.
     */
    public void setHintButtonEnabled() {
        this.hintButton.setEnabled(true);
    }

    /**
     * {@link GameView#setHintButtonDisabled()} implementation.
     */
    public void setHintButtonDisabled() {
        this.hintButton.setEnabled(false);
    }
    
    /**
     * {@link GameView#setInputHandler(InputHandler)} implementation.
     * 
     * @param inputHandler the input handler associated with this view
     * @throws NullPointerException if {@code inputHandler} is null
     */
    public void setInputHandler(InputHandler inputHandler) {
        Objects.requireNonNull(inputHandler); 
        this.inputHandler = inputHandler;
    }

    /**
     * Internal helper method
     * 
     * @return the current input handler
     * @hidden
     */
    public InputHandler getInputHandler(){
        return this.inputHandler; 
    }

    /**
     * Internal helper method
     * 
     * @param position the clicked cell position
     * @hidden
     */
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

    /**
     * {@link GameView#showCheck()} implementation.
     */
    public void showCheck(){
        String path = "notifications/check.png";
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h = (int)(screenSize.height * 0.075);
        int w = (int)(screenSize.height * 0.4);
        
        notificationLabel.setIcon(this.pathToIcon(path, w, h));
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    /**
     * {@link GameView#resetCheck()} implementation.
     */
    public void resetCheck() {
        notificationLabel.setIcon(null);
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    /**
     * {@link GameView#showWinner(Color)} implementation.
     * 
     * @param color the color of the winning player
     */
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

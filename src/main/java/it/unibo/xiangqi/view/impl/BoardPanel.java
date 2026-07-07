package it.unibo.xiangqi.view.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.common.api.Notification;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
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
    private Timer timer; 
    private JPanel timerPanel; 
    private final int ROWS = 10;
    private final int COLS = 9;

    /**
     * Creates a new board panel.
     *
     * <p>The panel initializes the board grid, control buttons,
     * notification area, and cell listeners.</p>
     */
    public BoardPanel() {

        /* Initialize the main Swing components */
        boardGrid = new JPanel(new GridLayout(ROWS, COLS));
        sidePanel = new JPanel();
        hintButton = new JButton("HINT"); 
        cells = new JButton[ROWS][COLS]; 
        notificationPanel = new JPanel(); 
        notificationLabel = new JLabel();
        notificationPanel.add(notificationLabel);

        /* Configure component dimensions according to the screen size */
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.cellSize = (int) (screenSize.height * 0.05);
        notificationPanel.setPreferredSize(new Dimension(0, (int) (screenSize.height * 0.075)));

        /* Register the hint button listener */
        hintButton.addActionListener(e -> {
            if (this.inputHandler != null) {
                inputHandler.onHint(); 
            } else {
                throw new IllegalStateException("input handler has not been setted"); 
            }
        });

        /* Create the board cells and associate each one with its listener */
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton button = new JButton();
                button.addActionListener(new CellListener(this, new Position(row, col)));
                cells[row][col] = button;
                boardGrid.add(button);
            }
        }

        /* Arrange the board layout */
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.add(hintButton);

        this.setLayout(new BorderLayout());
        this.add(boardGrid, BorderLayout.CENTER); 
        this.add(sidePanel, BorderLayout.EAST);
        this.add(notificationPanel, BorderLayout.SOUTH); 

        /* Initialize and place the game timer */
        timer = new Timer(); 
        timerPanel = timer.getTimerPanel();
        this.add(timerPanel, BorderLayout.NORTH);

        /* Highlight the fixed board areas (river and palaces) */
        this.highlightBoardAreas();
}

    /**
     * {@link GameView#updateBoard(Board)} implementation.
     * 
     * @param board the new board configuration to display
     * @throws NullPointerException if board is null
     */
    public void updateBoard(final Board board) {
        if (board != null) {
            this.currentBoard = board;
        } else {
            throw new NullPointerException("argument 'board' is null"); 
        }

        for (int row = 0; row < this.ROWS; row++) {
            for (int col = 0; col < this.COLS; col++) {
                final Position pos = new Position(row, col); 
                final Piece piece = this.currentBoard.getPieceAt(pos); 

                if (piece == null) {
                    cells[row][col].setIcon(null);
                    cells[row][col].setText("");
                } else {
                    cells[row][col].setIcon(pieceToIcon(piece));  
                }
            }
        }
    }

    /**
     * Internal helper method.
     * 
     * @hidden
     */
    private ImageIcon pathToIcon(String path, int width, int height) {
        Objects.requireNonNull(path); 
        final URL url = ClassLoader.getSystemResource(path); 
        final ImageIcon icon = new ImageIcon(url); 
        final Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled); 
    }

    /**
     * Internal helper method.
     * 
     * @hidden
     */
    private ImageIcon pieceToIcon(final Piece piece) {
        final String color = piece.getOwner().getColor().getName();
        final String type = piece.getType().getName();
        if (color == null || type == null) {
            throw new NullPointerException(); 
        }
        final String path = "icons/" + color + "_" + type + ".png";
        return this.pathToIcon(path, this.cellSize, this.cellSize); 
    }

    /**
     * {@link GameView#highlightCells(List)} implementation.
     * 
     * @param positions the list of positions to highlight
     * @throws NullPointerException if {@code positions} is null
     */
    public void highlightCells(final List<Position> positions) {
        if (positions != null) {
            this.highlightedCells = new ArrayList<>(positions);
        } else {
            throw new NullPointerException("positions is null"); 
        }

        this.disableAll();
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                final Position pos = new Position(row, col); 
                if (this.highlightedCells.contains(pos)) {
                    highlightCell(pos, java.awt.Color.YELLOW);
                    cells[row][col].setEnabled(true);
                }
            }
        }

        final int row = this.selectedCell.getRow(); 
        final int col = this.selectedCell.getCol(); 
        cells[row][col].setEnabled(true);
    }

    /**
     * Internal helper method.
     * 
     * @hidden
     */
    private void disableAll() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
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
    public void showSuggestedMove(final Move move) {
        Objects.requireNonNull(move); 
        final Position from = move.getFrom();
        final Position to = move.getTo();
        highlightCell(from, java.awt.Color.GREEN);
        highlightCell(to, java.awt.Color.GREEN);
    }

    /**
     * Internal helper method.
     * 
     * @hidden
     */
    private void highlightCell(final Position pos, final java.awt.Color color) {
        final int row = pos.getRow();
        final int col = pos.getCol();
        cells[row][col].setBackground(color);
    }

    /**
     * Internal helper method.
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
    public void setPlayerEnabled(final Color c) {
        this.disableAll();
        this.setPlayer(true, Objects.requireNonNull(c));
    }

    /**
     * {@link GameView#setPlayerDisabled(Color)} implementation.
     * 
     * @param c the color of the player to disable
     * @throws NullPointerException if {@code c} is null
     */
    public void setPlayerDisabled(final Color c) {
        this.disableAll();
        this.setPlayer(false, Objects.requireNonNull(c));
    }

    /**
     * Internal helper method.
     * 
     * @hidden
     */
    private void setPlayer(final boolean enable, final Color c) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                final Position pos = new Position(row, col); 
                final Piece piece = this.currentBoard.getPieceAt(pos); 
                
                if (piece != null && piece.getOwner().getColor() == c) {
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
    public void setInputHandler(final InputHandler inputHandler) {
        Objects.requireNonNull(inputHandler); 
        this.inputHandler = inputHandler;
    }

    /**
     * Internal helper method.
     * 
     * @return the current input handler
     * @hidden
     */
    public InputHandler getInputHandler() {
        return this.inputHandler; 
    }

    /**
     * Internal helper method
     * 
     * @param position the clicked cell position
     * @hidden
     */
    public void handleCellClick(final Position position) {
        Objects.requireNonNull(position); 

        /* First click: select the piece and request its legal moves */
        if (this.selectedCell == null) {
            this.selectedCell = position; 
            this.inputHandler.onSelect(position);

        /* Clicking the selected piece again cancels the selection */
        } else if (this.selectedCell.equals(position)) {
            final Color c = this.currentBoard.getPieceAt(position).getOwner().getColor(); 
            this.setPlayerEnabled(c);
            this.resetHighlights();
            this.selectedCell = null; 

        /* Second click on a different cell: perform the move */
        } else {
            this.inputHandler.onMove(new Move(this.selectedCell, position)); 
            this.selectedCell = null; 
            this.resetHighlights();
        }
    }

    /**
     * {@link GameView#showCheck()} implementation.
     */
    public void showCheck() {
        showNotification(Notification.CHECK);
    }

    /**
     * {@link GameView#showDraw()} implementation.
     */
    public void showDraw() {
        showNotification(Notification.DRAW);
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
    public void showWinner(final Color color) {
        if (color == Color.BLACK) {
            showNotification(Notification.BLACK_WINS);
        } else {
            showNotification(Notification.RED_WINS);
        }
    }

    /**
     * Internal helper method.
     * 
     * @hidden
     */
    private void showNotification(final Notification notification) {
        String path; 
        int w, h; 
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        switch (notification) {
            case CHECK: 
                path = "notifications/check.png";
                h = (int) (screenSize.height * 0.075);
                w = (int) (screenSize.height * 0.4);
                break; 
            case RED_WINS: 
                this.disableAll();
                path = "notifications/red_wins.png"; 
                h = (int) (screenSize.height * 0.075);
                w = (int) (screenSize.height * 0.65);
                break; 
            case BLACK_WINS: 
                this.disableAll();
                path = "notifications/black_wins.png"; 
                h = (int) (screenSize.height * 0.075);
                w = (int) (screenSize.height * 0.65); 
                break; 
            case DRAW: 
                this.disableAll();
                path = "notifications/draw.png";
                h = (int) (screenSize.height * 0.075);
                w = (int) (screenSize.height * 0.4);
                break;  
            default: 
                throw new IllegalArgumentException("The argument is wrong"); 
        }

        notificationLabel.setIcon(this.pathToIcon(path, w, h));
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    /**
     * {@link GameView#updateTimer(Player, long, long)} implementation.
     * 
     * @param player the player refers to
     * @param turnRemaining seconds remaining for the current turn
     * @param gameRemaining seconds remaing for the entire game
     * @throws NullPointerException if {@code player} is null
     */
    void updateTimer(final Player player, final long turnRemaining, final long gameRemaining) {
        Objects.requireNonNull(player); 
        timer.updateTimer(player, turnRemaining, gameRemaining);
    }

    /**
     * {@link GameView#showExpiredTime(Player)} implementation.
     * 
     * @param player the player refers to
     * @throws NullPointerException if {@code player} is null
     */
    void showExpiredTime(final Player player) {
        Objects.requireNonNull(player);
        timer.showExpiredTime(player);
    }

    /**
     * Highlights the borders of the river and the two palaces.
     */
    private void highlightBoardAreas() {
        final int thickness = 3;
        final java.awt.Color palaceColor = java.awt.Color.RED;
        final java.awt.Color riverColor = java.awt.Color.BLUE;

        // Black palace (rows 0-2, cols 3-5) 
        for (int row = 0; row <= 2; row++) {
            for (int col = 3; col <= 5; col++) {

                final int top = (row == 0) ? thickness : 1;
                final int bottom = (row == 2) ? thickness : 1;
                final int left = (col == 3) ? thickness : 1;
                final int right = (col == 5) ? thickness : 1;

                cells[row][col].setBorder(
                    BorderFactory.createMatteBorder(
                        top, left, bottom, right, palaceColor
                    )
                );
            }
        }

        // Red palace (rows 7-9, cols 3-5)
        for (int row = 7; row <= 9; row++) {
            for (int col = 3; col <= 5; col++) {

                final int top = (row == 7) ? thickness : 1;
                final int bottom = (row == 9) ? thickness : 1;
                final int left = (col == 3) ? thickness : 1;
                final int right = (col == 5) ? thickness : 1;

                cells[row][col].setBorder(
                    BorderFactory.createMatteBorder(
                        top, left, bottom, right, palaceColor
                    )
                );
            }
        }

        // River
        for (int col = 0; col < 9; col++) {
            // Bottom border of row 4
            cells[4][col].setBorder(
                BorderFactory.createMatteBorder(
                    1, 1, thickness, 1, riverColor
                )
            );

            // Top border of row 5
            cells[5][col].setBorder(
                BorderFactory.createMatteBorder(
                    thickness, 1, 1, 1, riverColor
                )
            );
        }
    }
}

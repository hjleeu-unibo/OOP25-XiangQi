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
    private static final long serialVersionUID = 1L; /* Because this class implements Serializable. */

    private static final int ROWS = 10;
    private static final int COLS = 9;
    private static final double CELL_SIZE_RATIO = 0.05;
    private static final double NOTIFICATION_PANEL_RATIO = 0.075;

    private static final int THICK_BORDER = 3;
    private static final int NORMAL_BORDER = 1;
    private static final int PALACE_START_COL = 3;
    private static final int PALACE_END_COL = 5;
    private static final int BLACK_PALACE_START_ROW = 0;
    private static final int BLACK_PALACE_END_ROW = 2;
    private static final int RED_PALACE_START_ROW = 7;
    private static final int RED_PALACE_END_ROW = 9;
    private static final int RIVER_LOWER_ROW = 5;
    private static final int RIVER_UPPER_ROW = 4;

    /* Notifications. */
    private static final double HEIGHT_RATIO = 0.075;
    private static final double LOW_WIDTH_RATIO = 0.4;
    private static final double HIGH_WIDTH_RATIO = 0.65;

    /* Transient field will not be serialized. */
    private final JButton[][] cells;
    private final JButton hintButton;
    private transient Board currentBoard;
    private transient List<Position> highlightedCells;
    private transient Position selectedCell;
    private transient InputHandler inputHandler;
    private final int cellSize;
    private final JPanel notificationPanel;
    private final JLabel notificationLabel;
    private final transient Timer timer;

    /**
     * Creates a new board panel.
     *
     * <p>The panel initializes the board grid, control buttons,
     * notification area, and cell listeners.</p>
     */
    public BoardPanel() {
        /* Initialize the main Swing components */
        final JPanel boardGrid = new JPanel(new GridLayout(ROWS, COLS));
        final JPanel sidePanel = new JPanel();
        hintButton = new JButton("HINT");
        cells = new JButton[ROWS][COLS];
        notificationPanel = new JPanel();
        notificationLabel = new JLabel();
        notificationPanel.add(notificationLabel);

        /* Configure component dimensions according to the screen size */
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.cellSize = (int) (screenSize.height * CELL_SIZE_RATIO);
        notificationPanel.setPreferredSize(new Dimension(0, (int) (screenSize.height * NOTIFICATION_PANEL_RATIO)));

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
                final JButton button = new JButton();
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

        /* Initialize and place the game timer. */
        timer = new Timer(); 
        final JPanel timerPanel = timer.getTimerPanel();
        this.add(timerPanel, BorderLayout.NORTH);

        /* Highlight the fixed board areas (river and palaces). */
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
            this.currentBoard = Objects.requireNonNull(board);
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
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
     * Creates an image icon from a resource path and scales it to the given size.
     *
     * @param path the path of the image resource
     * @param width the desired width of the icon
     * @param height the desired height of the icon
     * @return a scaled image icon created from the resource
     * @throws NullPointerException if {@code path} is null
     *
     * @hidden
     */
    private ImageIcon pathToIcon(final String path, final int width, final int height) {
        Objects.requireNonNull(path);
        final URL url = ClassLoader.getSystemResource(path);
        final ImageIcon icon = new ImageIcon(url);
        final Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Converts a game piece into its corresponding image icon.
     *
     * @param piece the piece to convert into an icon
     * @return the image icon representing the given piece
     * @throws NullPointerException if the piece color or type is not defined
     *
     * @hidden
     */
    private ImageIcon pieceToIcon(final Piece piece) {
        final String color = piece.getOwner().getColor().getName();
        final String type = piece.getType().getName();

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
        }

        this.disableAll();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
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
     * Disables all board cells. 
     * 
     * @hidden
     */
    private void disableAll() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
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
     * Highlights a board cell by changing its background color.
     * 
     * @param pos the position of the cell to highlight
     * @param color the color to apply to the cell background
     *
     * @hidden
     */
    private void highlightCell(final Position pos, final java.awt.Color color) {
        final int row = pos.getRow();
        final int col = pos.getCol();
        cells[row][col].setBackground(color);
    }

    /**
     * Resets all board cells highlights.
     * 
     * @hidden
     */
    private void resetHighlights() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
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
     * Enables or disables all cells containing pieces owned by the specified player color.
     *
     * @param enable {@code true} to enable the player's cells, {@code false} to disable them
     * @param c the color of the player whose pieces should be updated
     *
     * @hidden
     */
    private void setPlayer(final boolean enable, final Color c) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
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
     * Returns the input handler.
     * 
     * @return the current input handler
     * @hidden
     */
    public InputHandler getInputHandler() {
        return this.inputHandler; 
    }

    /**
     * Handles the inputs on board cells.
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
     * Shows the specified notification on the notification panel. 
     * 
     * @param notification the notification to display
     * @hidden
     */
    private void showNotification(final Notification notification) {
        String path = "";
        int w = 0;
        int h = 0;
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        switch (notification) {
            case CHECK:
                path = "notifications/check.png";
                h = (int) (screenSize.height * HEIGHT_RATIO);
                w = (int) (screenSize.height * LOW_WIDTH_RATIO);
                break;
            case RED_WINS:
                this.disableAll();
                path = "notifications/red_wins.png";
                h = (int) (screenSize.height * HEIGHT_RATIO);
                w = (int) (screenSize.height * HIGH_WIDTH_RATIO);
                break;
            case BLACK_WINS:
                this.disableAll();
                path = "notifications/black_wins.png"; 
                h = (int) (screenSize.height * HEIGHT_RATIO);
                w = (int) (screenSize.height * HIGH_WIDTH_RATIO); 
                break;
            case DRAW:
                this.disableAll();
                path = "notifications/draw.png";
                h = (int) (screenSize.height * HEIGHT_RATIO);
                w = (int) (screenSize.height * LOW_WIDTH_RATIO);
                break;
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
        final java.awt.Color palaceColor = java.awt.Color.RED;
        final java.awt.Color riverColor = java.awt.Color.BLUE;

        // Black palace
        for (int row = BLACK_PALACE_START_ROW; row <= BLACK_PALACE_END_ROW; row++) {
            for (int col = PALACE_START_COL; col <= PALACE_END_COL; col++) {

                final int top = (row == BLACK_PALACE_START_ROW) ? THICK_BORDER : NORMAL_BORDER;
                final int bottom = (row == BLACK_PALACE_END_ROW) ? THICK_BORDER : NORMAL_BORDER;
                final int left = (col == PALACE_START_COL) ? THICK_BORDER : NORMAL_BORDER;
                final int right = (col == PALACE_END_COL) ? THICK_BORDER : NORMAL_BORDER;

                cells[row][col].setBorder(
                    BorderFactory.createMatteBorder(
                        top, left, bottom, right, palaceColor
                    )
                );
            }
        }

        // Red palace
        for (int row = RED_PALACE_START_ROW; row <= RED_PALACE_END_ROW; row++) {
            for (int col = PALACE_START_COL; col <= PALACE_END_COL; col++) {

                final int top = (row == RED_PALACE_START_ROW) ? THICK_BORDER : NORMAL_BORDER;
                final int bottom = (row == RED_PALACE_END_ROW) ? THICK_BORDER : NORMAL_BORDER;
                final int left = (col == PALACE_START_COL) ? THICK_BORDER : NORMAL_BORDER;
                final int right = (col == PALACE_END_COL) ? THICK_BORDER : NORMAL_BORDER;

                cells[row][col].setBorder(
                    BorderFactory.createMatteBorder(
                        top, left, bottom, right, palaceColor
                    )
                );
            }
        }

        // River
        for (int col = 0; col < COLS; col++) {
            cells[RIVER_UPPER_ROW][col].setBorder(
                BorderFactory.createMatteBorder(
                    NORMAL_BORDER, NORMAL_BORDER, THICK_BORDER, NORMAL_BORDER, riverColor
                )
            );

            cells[RIVER_LOWER_ROW][col].setBorder(
                BorderFactory.createMatteBorder(
                    THICK_BORDER, NORMAL_BORDER, NORMAL_BORDER, NORMAL_BORDER, riverColor
                )
            );
        }
    }
}

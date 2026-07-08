package it.unibo.xiangqi.view.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Position;

/**
 * Action listener associated with a board cell.
 *
 * <p>When the corresponding button is clicked, the listener notifies the
 * owning {@code BoardPanel} of the selected board position.</p>
 */
public class CellListener implements ActionListener {

    private final BoardPanel boardPanel;
    private final Position position;

    /**
     * Creates a new listener associated with the specified board cell.
     *
     * @param boardPanel the board panel that owns the cell
     * @param position the position represented by the cell
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public CellListener(final BoardPanel boardPanel, final Position position) {
        this.boardPanel = Objects.requireNonNull(boardPanel);
        this.position = new Position(position.getRow(), position.getCol());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final InputHandler handler = boardPanel.getInputHandler();
        if (handler != null) {
            boardPanel.handleCellClick(position);
        } else {
            throw new IllegalStateException("Input handler has not been set."); 
        }
    }
}

package it.unibo.xiangqi.view.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Position;

/**
 * Action listener associated with a board cell.
 *
 * <p>When the corresponding button is clicked, the listener notifies the
 * owning {@code BoardPanel} of the selected board position.</p>
 */
public class CellListener implements ActionListener {

    private BoardPanel boardPanel;
    private Position position;

    /**
     * Creates a new listener associated with the specified board cell.
     *
     * @param boardPanel the board panel that owns the cell
     * @param position the position represented by the cell
     */
    public CellListener(BoardPanel boardPanel, Position position) {
        this.boardPanel = boardPanel;
        this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        InputHandler handler = boardPanel.getInputHandler();
        if (handler != null) {
            boardPanel.handleCellClick(position);
        }else{
            throw new IllegalStateException("Input handler has not been set."); 
        }
    }
}

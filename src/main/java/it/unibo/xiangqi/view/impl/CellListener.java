package it.unibo.xiangqi.view.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.controller.api.InputHandler;

public class CellListener implements ActionListener {

    private BoardPanel boardPanel;
    private Position position;

    public CellListener(BoardPanel boardPanel, Position position) {
        this.boardPanel = boardPanel;
        this.position = position;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        InputHandler handler = boardPanel.getInputHandler();

        if (handler != null) {
            boardPanel.handleCellClick(position);
        }
    }
}

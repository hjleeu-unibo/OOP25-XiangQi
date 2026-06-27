package it.unibo.xiangqi.view.impl;

import java.util.List;
import java.util.Objects;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.view.api.GameView;

public class GameViewImpl implements GameView{

    private JFrame frame; 
    private JPanel rootPanel; 
    private CardLayout cardLayout;
    private MenuPanel menuPanel;
    private BoardPanel boardPanel;
    private InputHandler inputHandler; 

    /* Haojie-Liu | Game timer section. */
    private JPanel timerPanel;
    private JLabel redTurnLabel;
    private JLabel blackTurnLabel;
    private JLabel redGameLabel;
    private JLabel blackGameLabel;


    public GameViewImpl() {

        frame = new JFrame("Xiangqi");
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);
        menuPanel = new MenuPanel();
        boardPanel = new BoardPanel();

        rootPanel.add(menuPanel, "MENU");
        rootPanel.add(boardPanel, "GAME");
        frame.setContentPane(rootPanel);
        cardLayout.show(rootPanel, "MENU");

        /* Haojie-Liu | Game timer section. */
        timerPanel = new JPanel(cardLayout);
        redTurnLabel = new JLabel("Red turn: --s");
        redGameLabel = new JLabel("Red game: --s");
        blackTurnLabel = new JLabel("Black turn: --s");
        blackGameLabel = new JLabel("Black game: --s");

        timerPanel.add(new JLabel("RED"));
        timerPanel.add(redTurnLabel);
        timerPanel.add(redGameLabel);
        timerPanel.add(new JLabel(" | "));
        timerPanel.add(new JLabel("BLACK"));
        timerPanel.add(blackTurnLabel);
        timerPanel.add(blackGameLabel);

        frame.add(timerPanel, BorderLayout.SOUTH);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int)(screenSize.height * 0.6); 
        int width = (int)(screenSize.width * 0.6);

        frame.setSize(width, height); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            if (inputHandler != null) {
                inputHandler.onExit();
            }
            System.exit(0);
        }
});
}
    

    @Override
    public void updateBoard(Board board) {
        boardPanel.updateBoard(board);
    }

    @Override
    public void setPlayerEnabled(Color c) {
        boardPanel.setPlayerEnabled(c);
    }

    @Override
    public void setPlayerDisabled(Color c) {
        boardPanel.setPlayerDisabled(c);
    }

    @Override
    public void setHintButtonEnabled() {
        boardPanel.setHintButtonEnabled();
    }

    @Override
    public void setHintButtonDisabled() {
        boardPanel.setHintButtonDisabled();
    }

    @Override
    public void highlightCells(List<Position> cells) {
        boardPanel.highlightCells(cells);
    }

    @Override
    public void showSuggestedMove(Move move) {
        boardPanel.showSuggestedMove(move);
    }

    public void setInputHandler(InputHandler handler) {
        Objects.requireNonNull(handler); 
        this.inputHandler = handler; 
        boardPanel.setInputHandler(handler);
        menuPanel.setInputHandler(handler);
    }

    @Override
    public void showCheck() {
        this.boardPanel.showCheck();
    }

    @Override
    public void resetCheck() {
        this.boardPanel.resetCheck();
    }

    @Override
    public void showWinner(Color color) {
        this.boardPanel.showWinner(color);
    }

    private void setGameSize(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int)(screenSize.height * 0.8); 
        int width = height;
        this.frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
    }

    public void showGamePanel(){
        this.setGameSize();
        this.cardLayout.show(rootPanel, "GAME");
    }
    
    /* Haojie-Liu | Game timer section. */
    @Override
    public void updateTimer(Player player, long turnRemaining, long gameRemaining) {
        /* The invokeLater method make async update of the view with threads. */
        SwingUtilities.invokeLater(() -> {
            switch (player.getColor()) {
                case RED:
                    redTurnLabel.setText("Turn: " + timeToString(turnRemaining));
                    redGameLabel.setText("Game: " + timeToString(gameRemaining));
                    break;
                case BLACK:
                    blackTurnLabel.setText("Turn: " + timeToString(turnRemaining));
                    blackGameLabel.setText("Game: " + timeToString(gameRemaining));
                    break;
            }
        });
    }

    @Override
    public void showExpiredTime(Player player) {
        SwingUtilities.invokeLater(() -> {
            String msg = player.getColor() + " player ran out of time. LOST!";
            /* Pop-up message window. */
            JOptionPane.showMessageDialog(frame, msg);
        });
    }

    /**
     * Return the time in a specified format.
     * @param time the time in seconds
     * @return the formatted time string
     */
    private String timeToString(long time) {
        long minutes = time / 60;
        long seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
package it.unibo.xiangqi.view.impl;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.xiangqi.model.api.Player;

/**
 * The timer view component.
 * Timer
 */
public class Timer {
    private JPanel timerPanel;
    private JLabel redTurnLabel;
    private JLabel blackTurnLabel;
    private JLabel redGameLabel;
    private JLabel blackGameLabel;

    /**
     * Constructor. Creates the timer panel.
     */
    public Timer() {
        timerPanel = new JPanel();
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.X_AXIS));

        redTurnLabel = new JLabel("TURN: 00:00");
        redGameLabel = new JLabel("GAME: 00:00");
        blackTurnLabel = new JLabel("TURN: 00:00");
        blackGameLabel = new JLabel("GAME: 00:00");

        final Font timerFont = new Font("Monospaced", Font.BOLD, 15);
        redTurnLabel.setFont(timerFont);
        redGameLabel.setFont(timerFont);
        blackTurnLabel.setFont(timerFont);
        blackGameLabel.setFont(timerFont);

        timerPanel.add(new JLabel("RED"));
        timerPanel.add(redTurnLabel);
        timerPanel.add(redGameLabel);
        
        timerPanel.add(new JLabel(" | "));

        timerPanel.add(new JLabel("BLACK"));
        timerPanel.add(blackTurnLabel);
        timerPanel.add(blackGameLabel);

        timerPanel.add(Box.createHorizontalGlue());
    }

    public void updateTimer(final Player player, final long turnRemaining, final long gameRemaining) {
        /* The invokeLater method make async update of the view with threads. */
        SwingUtilities.invokeLater(() -> {
            switch (player.getColor()) {
                case RED:
                    redTurnLabel.setText("TURN: " + timeToString(turnRemaining));
                    redGameLabel.setText("GAME: " + timeToString(gameRemaining));
                    break;
                case BLACK:
                    blackTurnLabel.setText("TURN: " + timeToString(turnRemaining));
                    blackGameLabel.setText("GAME: " + timeToString(gameRemaining));
                    break;
            }
        });
    }

    public void showExpiredTime(final Player player) {
        SwingUtilities.invokeLater(() -> {
            switch (player.getColor()) {
                case RED:
                    redTurnLabel.setText("LOST!");
                    redGameLabel.setText("LOST!");
                    break;
                case BLACK:
                    blackTurnLabel.setText("LOST!");
                    blackGameLabel.setText("LOST!");
                    break;
            }
        });
    }

    public JPanel getTimerPanel() {
        return timerPanel;
    }

    /**
     * Return the time in a specified format.
     * 
     * @param time the time in seconds
     * @return the formatted time string
     */
    private String timeToString(final long time) {
        long minutes = Math.max(0, time) / 60;
        long seconds = Math.max(0, time) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

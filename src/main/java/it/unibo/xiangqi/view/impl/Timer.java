package it.unibo.xiangqi.view.impl;

import java.awt.BorderLayout;
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
public final class Timer {
    private static final String LOST_MSG = "LOST!";
    private static final int TIME_DIVIDER = 60;

    private final JPanel timerPanel;
    private final JLabel redTurnLabel;
    private final JLabel blackTurnLabel;
    private final JLabel redGameLabel;
    private final JLabel blackGameLabel;

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

    /**
     * Update the timer on the view.
     * 
     * @param player the player refers to
     * @param turnRemaining the remaining time for that turn
     * @param gameRemaining the remaining time for that game
     */
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

    /**
     * Show something at time expired.
     * 
     * @param player whos' time expired
     */
    public void showExpiredTime(final Player player) {
        SwingUtilities.invokeLater(() -> {
            switch (player.getColor()) {
                case RED:
                    redTurnLabel.setText(LOST_MSG);
                    redGameLabel.setText(LOST_MSG);
                    break;
                case BLACK:
                    blackTurnLabel.setText(LOST_MSG);
                    blackGameLabel.setText(LOST_MSG);
                    break;
            }
        });
    }

    /**
     * Returs a copy of the panel of the time.
     * 
     * @return the JPanel component
     */
    public JPanel getTimerPanel() {
        final JPanel copy = new JPanel(new BorderLayout());
        copy.add(timerPanel);
        return copy;
    }

    /**
     * Return the time in a specified format.
     * 
     * @param time the time in seconds
     * @return the formatted time string
     */
    private String timeToString(final long time) {
        final long minutes = Math.max(0, time) / TIME_DIVIDER;
        final long seconds = Math.max(0, time) % TIME_DIVIDER;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

package it.unibo.xiangqi.view.api;

import javax.swing.JPanel;

import it.unibo.xiangqi.model.api.Player;

public interface TimerView {
    /** Show the updated timer on view.
     * @param player the player refers to
     * @param turnRemaining seconds remaining for the current turn
     * @param gameRemaining seconds remaing for the entire game
     */
    void updateTimer(Player player, long turnRemaining, long gameRemaining);

    /**
     * Show a message indicating that the time is expired, so the player lost.
     * @param player the player refers to
     */
    void showExpiredTime(Player player);

    /**
     * Return the timer panel.
     * @return the time JPanel
     */
    JPanel getTimerPanel();
}

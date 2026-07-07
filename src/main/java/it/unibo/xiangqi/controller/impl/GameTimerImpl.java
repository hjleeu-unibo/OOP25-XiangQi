package it.unibo.xiangqi.controller.impl;

import java.util.HashMap;
import java.util.Map;

import it.unibo.xiangqi.controller.api.GameTimer;
import it.unibo.xiangqi.model.api.Player;

/**
 * The implementation of the game timer interface.
 * GameTimerImpl
 */
public class GameTimerImpl implements GameTimer {
    private static final long TURN_LIMIT_SECONDS = 30;
    private static final long GAME_LIMIT_SECONDS = 600; /* 10min. */

    private final Map<Player, Long> turnStartTime = new HashMap<>();
    private final Map<Player, Long> totalRemaining = new HashMap<>();

    @Override
    public void startTurn(final Player player) {
        /* The reason of the long choice. */
        turnStartTime.put(player, System.currentTimeMillis());
        totalRemaining.putIfAbsent(player, GAME_LIMIT_SECONDS);
    }

    @Override
    public void stopTurn(final Player player) {
        if (turnStartTime.containsKey(player)) {
            final long timeUsed = (System.currentTimeMillis() - turnStartTime.get(player)) / 1000; /* Cast to seconds. */
            final long remaining = totalRemaining.getOrDefault(player, GAME_LIMIT_SECONDS);
            totalRemaining.put(player, Math.max(0, remaining - (int) timeUsed));
            turnStartTime.remove(player);
        }
    }

    @Override
    public long getTurnRemaining(final Player player) {
        /* If it's not my turn. */
        if (!turnStartTime.containsKey(player)) {
            return TURN_LIMIT_SECONDS;
        }

        final long timeUsed = (System.currentTimeMillis() - turnStartTime.get(player)) / 1000;
        final long turnRemaining = Math.max(0, TURN_LIMIT_SECONDS - timeUsed);
        final long gameRemaining = getGameRemaining(player);

        /* If the gameRemaining is less than 30, both have the same value. */
        return Math.min(gameRemaining, turnRemaining);
    }

    @Override
    public long getGameRemaining(final Player player) {
        if (!turnStartTime.containsKey(player)) {
            return totalRemaining.getOrDefault(player, GAME_LIMIT_SECONDS);
        }

        final long timeUsed = (System.currentTimeMillis() - turnStartTime.get(player)) / 1000;
        final long stored = totalRemaining.getOrDefault(player, GAME_LIMIT_SECONDS);

        return Math.max(0, stored - timeUsed);
    }

    @Override
    public boolean isTurnExpired(final Player player) {
        return getTurnRemaining(player) <= 0;
    }

    @Override
    public boolean isTotalExpired(final Player player) {
        return getGameRemaining(player) <= 0;
    }

    @Override
    public void reset() {
        turnStartTime.clear();
        totalRemaining.clear();
    }
}

package it.unibo.xiangqi.model.api;

import java.util.List;

import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.impl.GameStateImpl;

public interface GameState {
    static GameState createGameState(Board board, List<Player> players, Player currentPlayer){
        return new GameStateImpl(board, players.get(0), players.get(1), currentPlayer);
    }
    Board getBoard(); 
    Player getCurrentPlayer();
    void applyMove(Move move);
}
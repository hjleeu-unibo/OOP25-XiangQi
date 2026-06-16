package it.unibo.xiangqi.controller.api;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.Move;
import it.unibo.xiangqi.common.api.Position;

public interface InputHandler {
    void onSelect(Position position); 
    void onMove(Move move); 
    void onStart(GameModeType mode); 
    void onExit(); 
    void onResume(); 
    void onHint(); 
}
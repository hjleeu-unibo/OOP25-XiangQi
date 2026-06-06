package it.unibo.xiangqi.ai.impl;

/**
 * LOGIC OF THIS SYSTEM | HOW THE BOARD SCORES ARE CALCULATED:
 * Each piece has their own initial value (stored as double):
 *  - GENERAL   ->  100.0
 *  - ADVISOR   ->  2.0
 *  - ELEPHANT  ->  2.0
 *  - HORSE     ->  4.0
 *  - CHARIOT   ->  9.0
 *  - CANNON    ->  4.5
 *  - SOLDIER   ->  1.0
 * For a more real game experience, some pieces get different values depending by some factors:
 *  - SOLDIER   ->  over the river, its value changes to 2.0.
 *  - CANNON    ->  when its position is on the same line or column of the general, its values changes to 5.0,
 *                  but this real value is: own_value (4.5 -> 5.0) - 0.1 per dead piece (of all players).
 *                  The variable factor is because it became useless when it has less "stepping stones".
 *                  However, its minimum value is 4.0 -> max(4.5/5.0 - 0.1x, 4.0).
 *  - CHARIOT   ->  when its position is ont he same line or column of the enemy's general,
 *                  its value changes to 10.0.
 *  - HORSE     ->  over the river, its value changes to 5.0.
 *                  The horse became more useful when there are less "obstacles" (pieces) on the board.
 *                  The formula is: own_value (4.0 -> 5.0) + 0.1 per dead piece (of all players).
 *                  The maximum value is 6.5 -> min(4.0/5.0 + 0.1, 6.5).
 *  - ELEPHANT  ->  out of its initial position, its value changes to 3.0.
 *  - ADVISOR   ->  out of its initial position, its value changes to 3.0.
 *  - GENERAL   ->  its value will not be changed.
 * 
 * Above, these are the value of each piece during the game.
 * But they can get some extra value:
 *  - when they are protecting some ally pieces: own_value + ally_pieces_values
 *  - when they are threatening some enemy pieces: own_value + 1.5 x enemy_pieces_value
 * With these logics, this system will prefer attack instead of protect, make the game flow faster.
 */

import it.unibo.xiangqi.ai.api.MoveCalculator;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;

public class MoveCalculatorImpl implements MoveCalculator {
    public double calculateBoardScore(GameState gm) {
        return 0;
    }

    public Move getBestMove(Board board) {
        return null;
    }
}

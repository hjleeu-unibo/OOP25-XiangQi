package it.unibo.xiangqi.app;

import java.util.List;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.common.PieceType;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.view.api.GameView;
import it.unibo.xiangqi.view.impl.GameViewImpl;
import it.unibo.xiangqi.view.test.FakeBoard;
import it.unibo.xiangqi.view.test.FakeInputHandler;
import it.unibo.xiangqi.view.test.FakePiece;
import it.unibo.xiangqi.view.test.FakePlayer;

/**
 * temp
 * 
 * @hidden
 */
public class XiangqiApplication {
    public static void main(String[] args) {
        GameView view = new GameViewImpl();

        InputHandler ih = new FakeInputHandler(view); 
        view.setInputHandler(ih);

        Player player1 = new FakePlayer(Color.RED);
        Player player2 = new FakePlayer(Color.BLACK); 
        
        Piece p1 = new FakePiece(PieceType.GENERAL, player2, new Position(0, 2)); 
        Piece p2 = new FakePiece(PieceType.ADVISOR, player1, new Position(0, 0));
        Piece p3 = new FakePiece(PieceType.SOLDIER, player1, new Position(2, 3));

        Board board = new FakeBoard(List.of(p1, p2, p3)); 

        view.updateBoard(board);
        view.showCheck();

        //view.highlightCells(List.of(new Position(1, 3), new Position(0, 5)));
        
        //Move m = new Move(new Position(0, 0), new Position(2, 5));
        //view.showSuggestedMove(m); 

        //view.setHintButtonDisabled();
        //view.setHintButtonEnabled();
        //view.setPlayerEnabled(Color.RED);

    }
}

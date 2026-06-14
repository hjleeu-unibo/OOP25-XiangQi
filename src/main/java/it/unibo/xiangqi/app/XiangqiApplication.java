package it.unibo.xiangqi.app;

import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.common.api.Position;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.view.api.GameView;
import it.unibo.xiangqi.view.impl.GameViewImpl;
import it.unibo.xiangqi.view.test.FakeBoard;
import it.unibo.xiangqi.view.test.FakePiece;
import it.unibo.xiangqi.view.test.FakePlayer;

public class XiangqiApplication {
    public static void main(String[] args) {
        GameView view = new GameViewImpl();

        Player player1 = new FakePlayer(Color.RED);
        Player player2 = new FakePlayer(Color.BLACK); 
        
        Piece p1 = new FakePiece(PieceType.CANNON, player2, new Position(0, 2)); 
        Piece p2 = new FakePiece(PieceType.ADVISOR, player1, new Position(0, 0));

        Board board = new FakeBoard(List.of(p1, p2)); 

        view.updateBoard(board);

    }
}

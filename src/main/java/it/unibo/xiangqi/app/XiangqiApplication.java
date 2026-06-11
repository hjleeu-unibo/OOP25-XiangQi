package it.unibo.xiangqi.app;

import it.unibo.xiangqi.view.api.GameView;
import it.unibo.xiangqi.view.impl.GameViewImpl;

public class XiangqiApplication {
    public static void main(String[] args) {
        GameView view = new GameViewImpl();
    }
}

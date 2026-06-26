package it.unibo.xiangqi.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import it.unibo.xiangqi.ai.impl.MoveCalculatorImpl;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.RuleEngine;

final class TestMoveCalculator {
    private MoveCalculatorImpl moveCalculator;
    private RuleEngine ruleEngine;
    private GameModel gameModel;

    @BeforeEach
    void setup() {
        ruleEngine = mock(RuleEngine.class);
        gameModel = mock(GameModel.class);

        moveCalculator = new MoveCalculatorImpl(ruleEngine);
    }
}

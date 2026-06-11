package it.unibo.xiangqi.view.impl;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import it.unibo.xiangqi.view.api.MenuView;

public class MenuPanel extends JPanel implements MenuView{

    private JButton pvpButton;
    private JButton pveButton;
    private JButton resumeButton;

    public MenuPanel() {

        this.setLayout(new GridLayout(3, 1, 10, 10));

        pvpButton = new JButton("Player vs Player");
        pveButton = new JButton("Player vs AI");
        resumeButton = new JButton("Resume Game");

        this.add(pvpButton);
        this.add(pveButton);
        this.add(resumeButton);
}

    @Override
    public void showMainMenu() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showMainMenu'");
    }

    @Override
    public void showGameScreen() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showGameScreen'");
    }
    
}

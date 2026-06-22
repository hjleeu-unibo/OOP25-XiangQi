package it.unibo.xiangqi.view.impl;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import it.unibo.xiangqi.common.GameModeType;
import it.unibo.xiangqi.controller.api.InputHandler;

public class MenuPanel extends JPanel{

    private JButton pvpButton;
    private JButton pveButton;
    private JButton resumeButton;
    private InputHandler inputHandler; 

    public MenuPanel() {
        pvpButton = new JButton("Player vs Player");
        pveButton = new JButton("Player vs AI");
        resumeButton = new JButton("Resume Game");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        pvpButton.setAlignmentX(CENTER_ALIGNMENT);
        pveButton.setAlignmentX(CENTER_ALIGNMENT);
        resumeButton.setAlignmentX(CENTER_ALIGNMENT);

        this.add(Box.createVerticalGlue());
        this.add(pvpButton);
        this.add(Box.createVerticalStrut(10));
        this.add(pveButton);
        this.add(Box.createVerticalStrut(10));
        this.add(resumeButton);
        this.add(Box.createVerticalGlue());

        /*Event listeners */
        pvpButton.addActionListener(e -> {
            inputHandler.onStart(GameModeType.PVP); 
        });
        pveButton.addActionListener(e -> {
            inputHandler.onStart(GameModeType.PVE);
        });
        resumeButton.addActionListener(e -> {
            inputHandler.onResume();
        });
}

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }
    
}

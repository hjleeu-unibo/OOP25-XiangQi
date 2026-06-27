package it.unibo.xiangqi.view.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import it.unibo.xiangqi.common.GameModeType;
import it.unibo.xiangqi.controller.api.InputHandler;

public class MenuPanel extends JPanel{

    private JButton pvpButton;
    private JButton pveButton;
    private JButton resumeButton;
    private InputHandler inputHandler; 
    private Image backgroundImage; 

    public MenuPanel() {
        pvpButton = new JButton("Player vs Player");
        pveButton = new JButton("Player vs AI");
        resumeButton = new JButton("Resume Game");

        this.backgroundImage = new ImageIcon(
            ClassLoader.getSystemResource("menu/menu.png")
        ).getImage();

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

    @Override
    protected void paintComponent(Graphics g) {
        Objects.requireNonNull(this.backgroundImage); 
        super.paintComponent(g);
        g.drawImage(
            this.backgroundImage,
            0,
            0,
            this.getWidth(),
            this.getHeight(),
            this
        );
    }
    
}

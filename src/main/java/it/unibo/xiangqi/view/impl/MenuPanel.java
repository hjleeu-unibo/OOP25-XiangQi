package it.unibo.xiangqi.view.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.InputHandler;

/**
 * Represents the graphical menu panel of the game.
 *
 * <p>This panel provides the user interface for selecting the game mode
 * and resuming a previous game. It also manages the background image and
 * forwards user actions to the assigned {@link InputHandler}.</p>
 */
public class MenuPanel extends JPanel{

    private JButton pvpButton;
    private JButton pveButton;
    private JButton resumeButton;
    private InputHandler inputHandler; 
    private Image backgroundImage; 

    /**
     * Creates a new menu panel.
     *
     * <p>The panel initializes the menu buttons, configures the layout,
     * loads the background image and registers the required action listeners.</p>
     */
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

    /**
     * Sets the input handler used to notify the controller about user actions.
     *
     * @param inputHandler the handler responsible for processing user inputs
     */
    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * {@inheritDoc}
     */
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

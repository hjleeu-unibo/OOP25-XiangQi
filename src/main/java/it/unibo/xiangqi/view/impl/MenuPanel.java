package it.unibo.xiangqi.view.impl;

import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.view.api.GameView;

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
    private JPanel notificationPanel;
    private JLabel notificationLabel;

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

        notificationPanel = new JPanel();
        notificationPanel.setOpaque(false); //shows the background

        notificationLabel = new JLabel();
        notificationLabel.setText("There are no games to resume");
        notificationLabel.setForeground(java.awt.Color.RED);
        notificationLabel.setVisible(false);
        notificationPanel.add(notificationLabel);

        this.backgroundImage = new ImageIcon(
            ClassLoader.getSystemResource("menu/menu.png")
        ).getImage();

        Box box = Box.createVerticalBox();

        box.add(pvpButton);
        box.add(Box.createVerticalStrut(10));
        box.add(pveButton);
        box.add(Box.createVerticalStrut(10));
        box.add(resumeButton);
        box.add(Box.createVerticalStrut(20));
        box.add(notificationPanel);

        pvpButton.setAlignmentX(CENTER_ALIGNMENT);
        pveButton.setAlignmentX(CENTER_ALIGNMENT);
        resumeButton.setAlignmentX(CENTER_ALIGNMENT);
        notificationPanel.setAlignmentX(CENTER_ALIGNMENT);

        this.setLayout(new GridBagLayout());
        this.add(box);

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
     * {@link GameView#showResumeNotification()} implementation.
     */
    public void showResumeNotification(){
        this.notificationLabel.setVisible(true);
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

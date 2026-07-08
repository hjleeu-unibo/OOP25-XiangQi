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
public final class MenuPanel extends JPanel {
    private static final long serialVersionUID = 1L; /* Because this class implements Serializable. */

    private static final int VERTICAL_STRUCT_SIZE_1 = 10;
    private static final int VERTICAL_STRUCT_SIZE_2 = 20;

    /* Transient fields will be skipped during serialization. */
    private transient InputHandler inputHandler;
    private final transient Image backgroundImage;
    private final JLabel notificationLabel;

    /**
     * Creates a new menu panel.
     *
     * <p>The panel initializes the menu buttons, configures the layout,
     * loads the background image and registers the required action listeners.</p>
     */
    public MenuPanel() {
        /* Initialize menu buttons and notification components */
        final JButton pvpButton = new JButton("Player vs Player");
        final JButton pveButton = new JButton("Player vs AI");
        final JButton resumeButton = new JButton("Resume Game");

        final JPanel notificationPanel = new JPanel();
        notificationPanel.setOpaque(false); 

        notificationLabel = new JLabel();
        notificationLabel.setText("There are no games to resume");
        notificationLabel.setForeground(java.awt.Color.RED);
        notificationLabel.setVisible(false);
        notificationPanel.add(notificationLabel);

        /* Load the menu background image */
        this.backgroundImage = new ImageIcon(
            ClassLoader.getSystemResource("menu/menu.png")
        ).getImage();

        /* Arrange the menu components */
        final Box box = Box.createVerticalBox();

        box.add(pvpButton);
        box.add(Box.createVerticalStrut(VERTICAL_STRUCT_SIZE_1));
        box.add(pveButton);
        box.add(Box.createVerticalStrut(VERTICAL_STRUCT_SIZE_1));
        box.add(resumeButton);
        box.add(Box.createVerticalStrut(VERTICAL_STRUCT_SIZE_2));
        box.add(notificationPanel);

        pvpButton.setAlignmentX(CENTER_ALIGNMENT);
        pveButton.setAlignmentX(CENTER_ALIGNMENT);
        resumeButton.setAlignmentX(CENTER_ALIGNMENT);
        notificationPanel.setAlignmentX(CENTER_ALIGNMENT);

        this.setLayout(new GridBagLayout());
        this.add(box);

        /* Register button listeners */
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
    public void setInputHandler(final InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * {@link GameView#showResumeNotification()} implementation.
     */
    public void showResumeNotification() {
        this.notificationLabel.setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(final Graphics g) {
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

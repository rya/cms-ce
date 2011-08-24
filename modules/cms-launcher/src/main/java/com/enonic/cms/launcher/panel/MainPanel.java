package com.enonic.cms.launcher.panel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class MainPanel
    extends JPanel
{
    private final BufferedImage image;
    private final LoggingPanel loggingPanel;
    private final ControlPanel controlPanel;

    public MainPanel()
        throws Exception
    {
        setOpaque(false);
        setLayout(new BorderLayout());
        this.image = ImageIO.read(getClass().getResource("background.jpg"));

        this.loggingPanel = new LoggingPanel();
        this.controlPanel = new ControlPanel();

        final Box box = Box.createVerticalBox();
        box.add(this.loggingPanel);
        box.add(this.controlPanel);
        add(box, BorderLayout.SOUTH);
    }

    protected void paintComponent(final Graphics graphics)
    {
        graphics.drawImage(this.image, 0, 0, null);
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(this.image.getWidth(), this.image.getHeight());
    }

    public LoggingPanel getLoggingPanel()
    {
        return this.loggingPanel;
    }

    public ControlPanel getControlPanel()
    {
        return this.controlPanel;
    }
}

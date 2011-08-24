package com.enonic.cms.launcher.panel;

import com.enonic.cms.launcher.action.ClearLogAction;
import com.enonic.cms.launcher.action.LaunchBrowserAction;
import com.enonic.cms.launcher.action.StartServerAction;
import com.enonic.cms.launcher.action.StopServerAction;

import javax.swing.*;
import java.awt.*;

public final class ControlPanel
    extends JPanel
{
    private final StartServerAction startServerAction;
    private final StopServerAction stopServerAction;
    private final LaunchBrowserAction launchBrowserAction;
    private final ClearLogAction clearLogAction;

    public ControlPanel()
    {
        setOpaque(false);

        this.startServerAction = new StartServerAction();
        this.stopServerAction = new StopServerAction();
        this.launchBrowserAction = new LaunchBrowserAction();
        this.clearLogAction = new ClearLogAction();

        addAction(this.startServerAction);
        addAction(this.stopServerAction);
        addAction(this.launchBrowserAction);
        addAction(this.clearLogAction);

        startStopState(false);
    }

    private void addAction(final Action action)
    {
        final JButton button = new JButton(action);
        button.setOpaque(false);
        add(button);
    }

    protected void paintComponent(final Graphics graphics)
    {
        final Graphics2D g2 = (Graphics2D) graphics;

        final Composite comp = g2.getComposite();
        final Color c = g2.getColor();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4F));
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setComposite(comp);
        g2.setColor(c);
    }

    public void progressState()
    {
        this.clearLogAction.setEnabled(true);
        this.startServerAction.setEnabled(false);
        this.stopServerAction.setEnabled(false);
        this.launchBrowserAction.setEnabled(false);
    }

    public void startStopState(final boolean started)
    {
        this.clearLogAction.setEnabled(true);
        this.startServerAction.setEnabled(!started);
        this.stopServerAction.setEnabled(started);
        this.launchBrowserAction.setEnabled(started);
    }
}

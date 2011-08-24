package com.enonic.cms.launcher;

import com.enonic.cms.launcher.logging.LogService;
import com.enonic.cms.launcher.panel.MainPanel;
import com.enonic.cms.launcher.tomcat.TomcatListener;
import com.enonic.cms.launcher.tomcat.TomcatService;
import com.enonic.cms.launcher.util.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public final class Launcher
    extends JFrame implements TomcatListener
{
    private static Launcher INSTANCE;

    private final TomcatService tomcatService;
    private final MainPanel mainPanel;

    public Launcher(final File tomcatDir)
        throws Exception
    {
        INSTANCE = this;

        final LogService logService = new LogService();
        this.tomcatService = new TomcatService(tomcatDir, this);

        setTitle(Messages.get("product.title"));
        setBackground(Color.WHITE);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        this.mainPanel = new MainPanel();
        getContentPane().add(this.mainPanel);

        logService.addListener(this.mainPanel.getLoggingPanel().getLogListener());
        logService.addListener(this.tomcatService);

        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Enonic CMS");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent windowEvent) {
                exit();
            }
        });
    }

    public void showFrame()
    {
        pack();

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) screenSize.getWidth() / 2 - getWidth() / 2, (int) screenSize.getHeight() / 2 - getHeight() / 2);

        setVisible(true);
    }

    public void startServer()
    {
        progressState();
        this.mainPanel.getLoggingPanel().clearLogs();
        this.tomcatService.start();
    }

    private void progressState()
    {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.mainPanel.getControlPanel().progressState();
    }

    private void startStopState(final boolean state)
    {
        this.mainPanel.getControlPanel().startStopState(state);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void serverStarted()
    {
        startStopState(true);
    }

    public void serverStopped()
    {
        startStopState(false);
    }

    public void stopServer()
    {
        progressState();
        this.tomcatService.stop();
    }

    public void clearLogs()
    {
        this.mainPanel.getLoggingPanel().clearLogs();
    }

    public int getPortNumber()
    {
        return this.tomcatService.getPortNumber();
    }

    private void exit()
    {
        dispose();
        System.exit(0);
    }

    public static Launcher get()
    {
        return INSTANCE;
    }
}

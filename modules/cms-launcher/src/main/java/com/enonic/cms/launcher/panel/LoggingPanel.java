package com.enonic.cms.launcher.panel;

import javax.swing.*;
import java.awt.*;
import com.enonic.cms.launcher.logging.LogListener;

public final class LoggingPanel
    extends JScrollPane 
{
    private final LoggingTextArea text;

    public LoggingPanel()
    {
        this.text = new LoggingTextArea();
        setViewportView(this.text);

        setOpaque(false);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));
        getViewport().setOpaque(false);
    }

    public LogListener getLogListener()
    {
        return this.text;
    }

    public void clearLogs()
    {
        this.text.setText("");
    }
}

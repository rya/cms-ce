package com.enonic.cms.launcher.panel;

import com.enonic.cms.launcher.logging.LogListener;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;

public final class LoggingTextArea
    extends JTextArea implements LogListener
{
    public LoggingTextArea()
    {
        super(14, 7);
        setMargin(new Insets(3, 3, 3, 3));
        setEditable(false);
        setOpaque(false);
        setWrapStyleWord(true);
        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(12f));
        setTabSize(2);
    }

    protected void paintComponent(final Graphics graphics)
    {
        final Graphics2D g2 = (Graphics2D) graphics;
        final Composite comp = g2.getComposite();
        final Color c = g2.getColor();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5F));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(comp);
        g2.setColor(c);

        super.paintComponent(graphics);
    }

    public void log(final LogRecord record)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                appendText(format(record));
            }
        });
    }

    private void appendText(final String text)
    {
        append(text);
        setCaretPosition(getDocument().getLength());

        final int size = 100000;
        final int maxOverflow = 500;

        final int overflow = getDocument().getLength() - size;
        if (overflow >= maxOverflow) {
            replaceRange("", 0, overflow);
        }
    }

    private String format(final LogRecord record)
    {
        final StringBuilder str = new StringBuilder();
        str.append("[").append(record.getLevel().getName()).append("] ");
        str.append(record.getMessage()).append("\n");

        if (record.getThrown() != null) {
            final StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            str.append(writer.getBuffer()).append("\n");
        }

        return str.toString();
    }
}

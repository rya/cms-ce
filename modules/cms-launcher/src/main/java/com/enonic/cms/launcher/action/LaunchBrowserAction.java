package com.enonic.cms.launcher.action;

import java.awt.*;
import java.awt.event.*;
import java.net.URI;

import com.enonic.cms.launcher.Launcher;
import com.enonic.cms.launcher.util.Icons;
import com.enonic.cms.launcher.util.Messages;
import javax.swing.*;

public final class LaunchBrowserAction
    extends AbstractAction
{
    public LaunchBrowserAction()
    {
        super(Messages.get("launchBrowserAction.title"), Icons.LAUNCH);
        putValue(SHORT_DESCRIPTION, getValue(NAME));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent event)
    {
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:" + Launcher.get().getPortNumber()));
        } catch (Exception e) {
            // Do nothing
        }
    }
}

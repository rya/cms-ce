package com.enonic.cms.launcher.action;

import com.enonic.cms.launcher.Launcher;
import com.enonic.cms.launcher.util.Icons;
import com.enonic.cms.launcher.util.Messages;

import java.awt.event.*;
import javax.swing.*;

public final class StopServerAction
    extends AbstractAction
{
    public StopServerAction()
    {
        super(Messages.get("stopServerAction.title"), Icons.STOP);
        putValue(SHORT_DESCRIPTION, getValue(NAME));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e)
    {
        Launcher.get().stopServer();
    }
}

package com.enonic.cms.launcher.action;

import java.awt.event.*;

import com.enonic.cms.launcher.Launcher;
import com.enonic.cms.launcher.util.Icons;
import com.enonic.cms.launcher.util.Messages;
import javax.swing.*;

public final class StartServerAction
    extends AbstractAction
{
    public StartServerAction()
    {
        super(Messages.get("startServerAction.title"), Icons.START);
        putValue(SHORT_DESCRIPTION, getValue(NAME));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e)
    {
        Launcher.get().startServer();
    }
}

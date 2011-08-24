package com.enonic.cms.launcher.action;

import java.awt.event.*;
import com.enonic.cms.launcher.Launcher;
import com.enonic.cms.launcher.util.Icons;
import com.enonic.cms.launcher.util.Messages;
import javax.swing.*;

public final class ClearLogAction
    extends AbstractAction
{
    public ClearLogAction()
    {
        super(Messages.get("clearLogAction.title"), Icons.CLEAR);
        putValue(SHORT_DESCRIPTION, getValue(NAME));
        setEnabled(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        Launcher.get().clearLogs();
    }
}

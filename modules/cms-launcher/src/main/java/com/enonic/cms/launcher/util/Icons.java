package com.enonic.cms.launcher.util;

import javax.swing.*;
import java.net.URL;

public final class Icons
{
    public final static ImageIcon LOGO =
        loadIcon("logo.png");

    public final static ImageIcon LOGO_BIG =
        loadIcon("logo_big.png");

    public final static ImageIcon START =
        loadIcon("start.png");

    public final static ImageIcon STOP =
        loadIcon("stop.png");

    public final static ImageIcon LAUNCH =
        loadIcon("launch.png");

    public final static ImageIcon CLEAR =
        loadIcon("clear.png");

    private static ImageIcon loadIcon(final String iconName)
    {
        final URL url = Icons.class.getResource(iconName);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            throw new Error("Failed to load icon [" + iconName + "]");
        }
    }
}

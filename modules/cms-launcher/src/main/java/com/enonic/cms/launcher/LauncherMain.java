package com.enonic.cms.launcher;

import java.io.File;

public final class LauncherMain
{
    public static void main(final String... args)
        throws Exception
    {
        final Launcher frame = new Launcher(requireDir("catalina.home"));
        frame.showFrame();
    }

    private static File requireDir(final String propName)
    {
        final String value = System.getProperty(propName);
        if ((value == null) || "".equals(value)) {
            throw new IllegalArgumentException("Property [" + propName + "] must be set");
        }

        final File dir = new File(value);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Property [" + propName + "] must be set to a valid directory");
        }

        return dir;
    }
}

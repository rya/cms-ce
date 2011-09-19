/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtil
{
    private final static String DEFAULT_TMP_PREFIX = "enonic-";

    private final static String DEFAULT_TMP_SUFFIX = ".tmp";

    public static File createTempDir( String prefix, String suffix, boolean deleteOnExit )
    {
        if ( prefix == null )
        {
            prefix = DEFAULT_TMP_PREFIX;
        }

        if ( suffix == null )
        {
            suffix = DEFAULT_TMP_SUFFIX;
        }

        File dir;

        try
        {
            File file = File.createTempFile( prefix, suffix );
            file.delete();
            dir = new File( file.getAbsolutePath() );
        }
        catch ( IOException e )
        {
            dir = new File( prefix + UUID.randomUUID().toString() + suffix );
        }

        dir.mkdirs();

        if ( deleteOnExit )
        {
            dir.deleteOnExit();
        }

        return dir;
    }
}

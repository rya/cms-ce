package com.enonic.cms.core.plugin.deploy;

import java.io.File;
import java.io.FileFilter;

final class JarFileFilter
    implements FileFilter
{
    public boolean accept( final File file )
    {
        return file.exists() && file.isFile() && file.getName().endsWith( ".jar" );
    }
}

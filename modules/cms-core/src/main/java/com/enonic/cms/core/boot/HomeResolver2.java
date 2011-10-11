package com.enonic.cms.core.boot;

import com.enonic.cms.api.util.LogFacade;
import com.google.common.base.Strings;
import org.springframework.core.env.Environment;
import java.io.File;

final class HomeResolver2
    implements HomeConstants
{
    private final static LogFacade LOG = LogFacade.get(HomeResolver2.class);

    private final Environment env;

    public HomeResolver2(final Environment env)
    {
        this.env = env;
    }

    public File resolve()
    {
        return validatePath( resolvePath() );
    }

    private String resolvePath()
    {
        String path = this.env.getProperty( CMS_HOME );
        if ( !Strings.isNullOrEmpty(path) )
        {
            return path;
        }

        path = this.env.getProperty(CMS_HOME_ENV);
        if ( !Strings.isNullOrEmpty(path) )
        {
            return path;
        }

        throw new IllegalArgumentException( "Home directory not set. Please set either [cms.home] " +
                "system property or [CMS_HOME] environment variable." );
    }

    private File validatePath( final String path )
    {
        final File dir = new File( path ).getAbsoluteFile();
        if ( !dir.exists() || !dir.isDirectory() )
        {
            throw new IllegalArgumentException( "Invalid home directory: [" + path + "] is not a directory" );
        }

        LOG.info( "Home directory is set to [{0}]", dir.getAbsolutePath() );
        return dir;
    }
}

package com.enonic.cms.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

/**
 * This class implements the mime type resolver.
 */
public final class MimeTypeResolverImpl
    implements MimeTypeResolver, InitializingBean, ServletContextAware
{
    private static final Logger LOG = LoggerFactory.getLogger( MimeTypeResolverImpl.class.getName() );

    /**
     * Default mime type.
     */
    private final static String DEFAULT_MIME_TYPE = "application/octet-stream";

    /**
     * Mime types collection.
     */
    private Properties mimeTypes;

    private ServletContext servletContext;

    private String mimetypesLocation;

    /**
     * Construct the resolver.
     */
    public MimeTypeResolverImpl()
    {
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        this.mimeTypes = loadMimeTypes( mimetypesLocation );
    }

    /**
     * Return the mime type by file.
     */
    @Override
    public String getMimeType( String fileName )
    {
        String ext = fileName.substring( fileName.lastIndexOf( "." ) + 1 );

        if ( ext.equals( "" ) )
        {
            ext = fileName;
        }

        return getMimeTypeByExtension( ext );
    }

    /**
     * Return the mime type by extension.
     */
    @Override
    public String getMimeTypeByExtension( String ext )
    {
        final String key = ext.toLowerCase();

        final String localProperty = this.mimeTypes.getProperty( key );

        if (localProperty != null)
        {
            return localProperty;
        }

        if (servletContext != null)
        {
            final String containerProperty = servletContext.getMimeType( key );

            if (containerProperty != null)
            {
                return containerProperty;
            }
        } else {
            return DEFAULT_MIME_TYPE;
        }


        return DEFAULT_MIME_TYPE;
    }

    /**
     * Find extension by mime type.
     */
    @Override
    public String getExtension( String mimeType )
    {
        String ext = "";

        if ( mimeType == null || ext.equals( mimeType ) || DEFAULT_MIME_TYPE.equals( mimeType ) )
        {
            return ext;
        }

        for (final Map.Entry entry : mimeTypes.entrySet())
        {
            final String key = (String) entry.getKey();
            final String value = (String) entry.getValue();

            if ( value.equals( mimeType ) && mimeType.endsWith( key ) )
            {
                return key;
            }

            if ( value.equals( mimeType ) && ext.equals( "" ) )
            {
                ext = key;
            }
            else if ( value.equals( mimeType ) && !ext.equals( "" ) )
            {
                return ext;
            }
        }

        return ext;
    }

    /**
     * Load mime types. User can override default mimetypes by own in CMS_HOME directory.
     * @param mimetypesLocation location of .properties file
     * @return map of mime types
     */
    private Properties loadMimeTypes(final String mimetypesLocation)
    {
        final Properties userProps = new Properties();
        final Properties systemProps = new Properties();

        try
        {
            // load user defined mime types from mimetypesLocation ( e.g. CMS_HOME )
            final File file = new File(mimetypesLocation);
            if ( file.exists() )
            {
                userProps.load( new FileInputStream( file ) );

                LOG.info( "loaded {} user-defined mimetypes from file {}", userProps.size(), mimetypesLocation  );
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Unable to load user mimetypes from {}. Reason: {}", mimetypesLocation, e.toString() );
        }

        try
        {
            final InputStream input = MimeTypeResolverImpl.class.getResourceAsStream( "mimetypes.properties" );

            if ( input == null )
            {
                throw new InternalError( "Unable to find mimetypes.properties" );
            }

            systemProps.load( input );
        }
        catch ( IOException e )
        {
            throw new InternalError( "Unable to load system mimetypes: " + e.toString() );
        }

        // overwrite default properties with user properties
        systemProps.putAll( userProps );

        return systemProps;
    }

    public void setMimetypesLocation( String mimetypesLocation )
    {
        this.mimetypesLocation = mimetypesLocation;
    }

    public void setServletContext( final ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }
}

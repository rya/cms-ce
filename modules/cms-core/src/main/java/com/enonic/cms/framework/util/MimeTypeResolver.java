/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * This class implements the mime type resolver.
 */
public final class MimeTypeResolver
{
    /**
     * Shared instance.
     */
    private final static MimeTypeResolver INSTANCE = new MimeTypeResolver();

    /**
     * Default mime type.
     */
    private final static String DEFAULT_MIME_TYPE = "application/octet-stream";

    /**
     * Mime types.
     */
    private final Properties mimeTypes;

    /**
     * Construct the resolver.
     */
    private MimeTypeResolver()
    {
        this.mimeTypes = loadMimeTypes();
    }

    /**
     * Return the default mime type.
     */
    public String getDefaultMimeType()
    {
        return DEFAULT_MIME_TYPE;
    }

    /**
     * Return the mime type by file.
     */
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
    public String getMimeTypeByExtension( String ext )
    {
        return this.mimeTypes.getProperty( ext.toLowerCase(), DEFAULT_MIME_TYPE );
    }

    /**
     * Find extension by mime type.
     */
    public String getExtension( String mimeType )
    {
        if ( mimeType.equals( "" ) || mimeType.equals( DEFAULT_MIME_TYPE ) )
        {
            return "";
        }

        Iterator iterator = this.mimeTypes.keySet().iterator();
        String ext = "";

        while ( iterator.hasNext() )
        {
            String key = (String) iterator.next();
            String value = (String) this.mimeTypes.get( key );

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

        return "";
    }

    /**
     * Load mime types.
     */
    private static Properties loadMimeTypes()
    {
        try
        {
            InputStream input = MimeTypeResolver.class.getResourceAsStream( "mimetypes.properties" );
            if ( input == null )
            {
                throw new InternalError( "Unable to find mimetypes.properties" );
            }
            else
            {
                Properties props = new Properties();
                props.load( input );
                return props;
            }
        }
        catch ( IOException e )
        {
            throw new InternalError( "Unable to load mimetypes: " + e.toString() );
        }
    }

    /**
     * Return the instance.
     */
    public static MimeTypeResolver getInstance()
    {
        return INSTANCE;
    }
}

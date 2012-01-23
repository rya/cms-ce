/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class HttpServletUtil
{

    private final static Logger LOG = LoggerFactory.getLogger( HttpServletUtil.class );

    private static final int BUFFER_SIZE = 4096;

    private static final String SDF_EXPIRES_PATTERN = "E, dd MMM yyyy HH:mm:ss z";

    private static final String SDF_DATE_PATTERN = "E, dd MMM yyyy HH:mm:ss z";

    private static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone( "GMT" );

    public static void setDateHeader( HttpServletResponse response, Date dateTime )
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat( SDF_DATE_PATTERN, Locale.ENGLISH );
        dateFormat.setTimeZone( TIMEZONE_GMT );
        response.setHeader( "Date", dateFormat.format( dateTime ) );
    }

    public static void setExpiresHeader( HttpServletResponse response, Date expirationTime )
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat( SDF_EXPIRES_PATTERN, Locale.ENGLISH );
        dateFormat.setTimeZone( TIMEZONE_GMT );
        response.setHeader( "Expires", dateFormat.format( expirationTime ) );
    }

    public static void setCacheControl( HttpServletResponse response, HttpCacheControlSettings settings )
    {
        StringBuffer s = new StringBuffer();
        if ( settings.publicAccess )
        {
            s.append( "public" ); //Indicates that the response may be cached by any cache
        }
        else
        {
            s.append( "private" ); //Indicates that all or part of the response message is intended for a single user and must not be cached by a shared cache.
        }
        if ( settings.maxAgeSecondsToLive != null )
        {
            s.append( ", max-age=" ).append( settings.maxAgeSecondsToLive );
        }
        response.setHeader( "Cache-Control", s.toString() );
    }

    public static void setCacheControlNoCache( HttpServletResponse response )
    {
        response.setHeader( "Cache-Control", "private, no-cache, no-store" ); //HTTP 1.1
        response.setHeader( "Pragma", "no-cache" ); //HTTP 1.0
        response.setDateHeader( "Expires", -1 ); //prevents caching at the proxy server
    }

    public static void setContentDisposition( HttpServletResponse response, boolean attachment, String filename )
    {

        StringBuffer value = new StringBuffer();
        if ( attachment )
        {
            value.append( "attachment" );
        }
        else
        {
            value.append( "inline" );
        }

        value.append( ";filename=\"" ).append( filename ).append( "\"" );

        response.setHeader( "Content-Disposition", value.toString() );
    }

    public static String resolveMimeType( ServletContext servletContext, String filename )
    {
        final WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext( servletContext );

        final MimeTypeResolver mimeTypeResolver = (MimeTypeResolver) wac.getBean("mimeTypeResolver");

        final String mimeType = mimeTypeResolver.getMimeType( filename );

        return mimeType;    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     *
     * @param in  The stream to copy from.
     * @param out The stram to copy to.
     * @return The number of bytes copied from the input stream to the output stream.
     * @throws java.io.IOException If an I/O error occurs.
     */
    public static int copyNoCloseOut( InputStream in, OutputStream out )
            throws IOException
    {
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ( ( bytesRead = in.read( buffer ) ) != -1 )
        {
            try
            {
                out.write( buffer, 0, bytesRead );
            }
            catch ( IOException e )
            {
                LOG.warn( "Error writing to outputstream: " + e.getMessage() +
                                  ". Closing inputstream and passing on the original exception" );
                try
                {
                    in.close();
                }
                catch ( IOException ex )
                {
                    LOG.warn( "Error closing inputstream: " + ex.getMessage() );
                }
                throw e;
            }
            byteCount += bytesRead;
        }
        out.flush();
        in.close();
        return byteCount;
    }

    public static int copyNoCloseOut( Reader in, Writer out )
            throws IOException
    {
        int byteCount = 0;
        char[] buffer = new char[BUFFER_SIZE];
        int bytesRead;
        while ( ( bytesRead = in.read( buffer ) ) != -1 )
        {
            try
            {
                out.write( buffer, 0, bytesRead );
            }
            catch ( IOException e )
            {
                LOG.warn( "Error writing to outputstream: " + e.getMessage() +
                                  ". Closing inputstream and passing on the original exception" );
                try
                {
                    in.close();
                }
                catch ( IOException ex )
                {
                    LOG.warn( "Error closing inputstream: " + ex.getMessage() );
                }
                throw e;
            }
            byteCount += bytesRead;
        }
        out.flush();
        in.close();
        return byteCount;
    }

    public static boolean isContentModifiedAccordingToIfNoneMatchHeader( HttpServletRequest req, String etagFromContent )
    {
        String etagFromHeader = req.getHeader( "If-None-Match" );
        return !etagFromContent.equals( etagFromHeader );
    }

    public static void setEtag( HttpServletResponse res, String etag )
    {
        res.setHeader( "ETag", etag );
    }
}

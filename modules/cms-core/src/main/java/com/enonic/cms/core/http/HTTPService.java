package com.enonic.cms.core.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.enonic.vertical.VerticalProperties;

public class HTTPService
{

    private static final Logger LOG = Logger.getLogger( HTTPService.class.getName() );

    private final static int DEFAULT_CONNECTION_TIMEOUT = 2000;

    private final static int DEFAULT_CONNECTION_READ_TIMEOUT = 10000;

    public String getURL( String address, String encoding, int timeoutMs )
    {
        BufferedReader reader = null;
        try
        {
            URLConnection urlConn = setUpConnection( address, timeoutMs );
            reader = setUpReader( encoding, urlConn );
            StringBuffer sb = new StringBuffer( 1024 );
            char[] line = new char[1024];
            int charCount = reader.read( line );
            while ( charCount > 0 )
            {
                sb.append( line, 0, charCount );
                charCount = reader.read( line );
            }
            return sb.toString();

        }
        catch ( Exception e )
        {
            String message = "Failed to get URL: \"" + address + "\": " + e.getMessage();
            LOG.warning( message );
        }
        finally
        {
            try
            {
                closeReader( reader );
            }
            catch ( IOException ioe )
            {
                String message = "Failed to close reader stream: \"" + address + "\": " + ioe.getMessage();
                LOG.warning( message );
            }
        }

        return null;
    }

    private URLConnection setUpConnection( String address, int timeoutMs )
            throws IOException
    {
        URL url = new URL( address );
        URLConnection urlConn = url.openConnection();
        urlConn.setConnectTimeout( timeoutMs > 0 ? timeoutMs : DEFAULT_CONNECTION_TIMEOUT );
        urlConn.setReadTimeout( DEFAULT_CONNECTION_READ_TIMEOUT );
        urlConn.setRequestProperty( "User-Agent", VerticalProperties.getVerticalProperties().getDataSourceUserAgent() );
        String userInfo = url.getUserInfo();
        if ( StringUtils.isNotBlank( userInfo ) )
        {
            String userInfoBase64Encoded = new String( Base64.encodeBase64( userInfo.getBytes() ) );
            urlConn.setRequestProperty( "Authorization", "Basic " + userInfoBase64Encoded );
        }
        return urlConn;

    }

    private BufferedReader setUpReader( String encoding, URLConnection urlConn )
            throws IOException
    {
        InputStream in = urlConn.getInputStream();
        BufferedReader reader;
        if ( encoding == null )
        {
            reader = new BufferedReader( new InputStreamReader( in ) );
        }
        else
        {
            reader = new BufferedReader( new InputStreamReader( in, encoding ) );
        }
        return reader;
    }

    private void closeReader( BufferedReader reader )
            throws IOException
    {
        if ( reader != null )
        {
            reader.close();
        }

    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.binrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.enonic.cms.api.client.ClientException;

/**
 * This class implements the invocation handler.
 */
public final class BinRpcInvocationHandler
    implements InvocationHandler
{
    /**
     * Session id cookie.
     */
    private final static String SESSION_COOKIE_NAME = "JSESSIONID";

    /**
     * Content type.
     */
    private final static String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    /**
     * Http method post.
     */
    private final static String HTTP_METHOD_POST = "POST";

    /**
     * Http cookie header.
     */
    private final static String HTTP_HEADER_COOKIE = "Cookie";

    /**
     * Http set-cookie header.
     */
    private final static String HTTP_HEADER_SET_COOKIE = "Set-Cookie";

    /**
     * Http content type.
     */
    private final static String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * Http content length.
     */
    private final static String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";

    /**
     * Connection url.
     */
    private final String serviceUrl;

    /**
     * Use global session.
     */
    private final boolean useGlobalSession;

    /**
     * Session id per thread.
     */
    private final ThreadLocal<String> localSessionIds;

    /**
     * Current session id.
     */
    private String sessionId;

    /**
     * Construct the invocation handler.
     */
    public BinRpcInvocationHandler( String serviceUrl, boolean useGlobalSession )
    {
        this.serviceUrl = serviceUrl;
        this.useGlobalSession = useGlobalSession;
        this.localSessionIds = new ThreadLocal<String>();
    }

    /**
     * Invoke the method.
     */
    public Object invoke( Object object, Method method, Object[] args )
        throws Throwable
    {
        try
        {
            String methodName = method.getName();
            Class<?>[] params = method.getParameterTypes();
            BinRpcInvocation invocation = new BinRpcInvocation( methodName, params, args );
            BinRpcInvocationResult result = executeRequest( invocation );
            return result.recreate();
        }
        catch ( ClientException e )
        {
            throw e;
        }
        catch ( UndeclaredThrowableException e )
        {
            throw new ClientException( e.getUndeclaredThrowable() );
        }
        catch ( Throwable e )
        {
            throw new ClientException( e );
        }
    }

    /**
     * Open connection.
     */
    private HttpURLConnection openConnection()
        throws IOException
    {
        URLConnection conn = new URL( this.serviceUrl ).openConnection();
        if ( !( conn instanceof HttpURLConnection ) )
        {
            throw new ClientException( "Service URL [" + this.serviceUrl + "] is not an HTTP URL" );
        }

        return (HttpURLConnection) conn;
    }

    /**
     * Execute the request.
     */
    private BinRpcInvocationResult executeRequest( BinRpcInvocation invocation )
        throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = getByteArrayOutputStream( invocation );
        HttpURLConnection conn = openConnection();
        prepareConnection( conn, baos.size() );
        writeRequestBody( conn, baos );
        validateResponse( conn );
        checkResponseHeaders( conn );
        return readResponseBody( conn );
    }

    /**
     * Prepare connection.
     */
    private void prepareConnection( HttpURLConnection conn, int contentLength )
        throws IOException
    {
        conn.setDoOutput( true );
        conn.setRequestMethod( HTTP_METHOD_POST );
        conn.setRequestProperty( HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_SERIALIZED_OBJECT );
        conn.setRequestProperty( HTTP_HEADER_CONTENT_LENGTH, Integer.toString( contentLength ) );

        if ( getSessionId() != null )
        {
            conn.setRequestProperty( HTTP_HEADER_COOKIE, SESSION_COOKIE_NAME + "=" + getSessionId() );
        }
    }

    /**
     * Write the request body.
     */
    private void writeRequestBody( HttpURLConnection conn, ByteArrayOutputStream baos )
        throws IOException
    {
        baos.writeTo( conn.getOutputStream() );
    }

    /**
     * Validate the response.
     */
    private void validateResponse( HttpURLConnection conn )
        throws IOException
    {
        if ( conn.getResponseCode() >= 300 )
        {
            throw new ClientException(
                "Did not receive successful HTTP response: status code = " + conn.getResponseCode() + ", status message = [" +
                    conn.getResponseMessage() + "]" );
        }
    }

    /**
     * Check the session information.
     */
    private void checkResponseHeaders( HttpURLConnection conn )
    {
        String setCookie = conn.getHeaderField( HTTP_HEADER_SET_COOKIE );
        if ( ( setCookie != null ) && setCookie.startsWith( SESSION_COOKIE_NAME + "=" ) )
        {
            String[] bits = setCookie.split( "[=;]" );
            setSessionId( bits[1] );
        }
    }

    /**
     * Read response body.
     */
    private BinRpcInvocationResult readResponseBody( HttpURLConnection conn )
        throws IOException, ClassNotFoundException
    {
        InputStream in = conn.getInputStream();
        ObjectInputStream ois = new ObjectInputStream( in );

        try
        {
            Object obj = ois.readObject();
            if ( !( obj instanceof BinRpcInvocationResult ) )
            {
                throw new ClientException(
                    "Deserialized object needs to be assignable to type [" + BinRpcInvocationResult.class.getName() + "]: " + obj );
            }
            else
            {
                return (BinRpcInvocationResult) obj;
            }
        }
        finally
        {
            ois.close();
        }
    }

    /**
     * Return the stream for invocation.
     */
    private ByteArrayOutputStream getByteArrayOutputStream( BinRpcInvocation invocation )
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );

        try
        {
            oos.writeObject( invocation );
            oos.flush();
        }
        finally
        {
            oos.close();
        }

        return baos;
    }

    /**
     * Return the session id.
     */
    private String getSessionId()
    {
        if ( this.useGlobalSession )
        {
            return this.sessionId;
        }
        else
        {
            return this.localSessionIds.get();
        }
    }

    /**
     * Set the session id.
     */
    private void setSessionId( String sessionId )
    {
        if ( this.useGlobalSession )
        {
            this.sessionId = sessionId;
        }
        else
        {
            this.localSessionIds.set( sessionId );
        }
    }
}

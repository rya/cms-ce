/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.NestedServletException;

import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.api.client.binrpc.BinRpcInvocation;
import com.enonic.cms.api.client.binrpc.BinRpcInvocationResult;

/**
 * This class implements the service exporter.
 */
@RequestMapping(value = "/rpc/bin")
public final class BinRpcServiceExporter
    implements Controller
{
    /**
     * Content type.
     */
    private final static String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    /**
     * Local client.
     */
    private LocalClient client;

    /**
     * Set the local client.
     */
    @Autowired
    public void setLocalClient( LocalClient client )
    {
        this.client = client;
    }

    /**
     * Handle the request.
     */
    public ModelAndView handleRequest( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( "POST".equalsIgnoreCase( req.getMethod() ) )
        {
            try
            {
                BinRpcInvocation invocation = readInvocation( req );
                BinRpcInvocationResult result = invokeAndCreateResult( invocation, this.client );
                writeInvocationResult( res, result );
            }
            catch ( ClassNotFoundException ex )
            {
                throw new NestedServletException( "Class not found during deserialization", ex );
            }
        }
        else
        {
            res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Only POST is allowed" );
        }

        return null;
    }

    /**
     * Read the invocation.
     */
    private BinRpcInvocation readInvocation( HttpServletRequest req )
        throws IOException, ClassNotFoundException
    {
        InputStream in = req.getInputStream();
        ObjectInputStream ois = new ObjectInputStream( in );

        try
        {
            Object obj = ois.readObject();
            if ( !( obj instanceof BinRpcInvocation ) )
            {
                throw new IOException(
                    "Deserialized object needs to be assignable to type [" + BinRpcInvocation.class.getName() + "]: " + obj );
            }
            else
            {
                return (BinRpcInvocation) obj;
            }
        }
        finally
        {
            ois.close();
        }
    }

    /**
     * Write invocation result.
     */
    private void writeInvocationResult( HttpServletResponse res, BinRpcInvocationResult result )
        throws IOException
    {
        res.setContentType( BinRpcServiceExporter.CONTENT_TYPE_SERIALIZED_OBJECT );
        OutputStream out = res.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( out );

        try
        {
            oos.writeObject( result );
            oos.flush();
        }
        finally
        {
            oos.close();
        }
    }

    /**
     * Invoke and create result.
     */
    private BinRpcInvocationResult invokeAndCreateResult( BinRpcInvocation invocation, Object targetObject )
    {
        try
        {
            Object value = invocation.invoke( targetObject );
            return new BinRpcInvocationResult( value );
        }
        catch ( Throwable ex )
        {
            return new BinRpcInvocationResult( ex );
        }
    }
}

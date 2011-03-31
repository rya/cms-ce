/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.binrpc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * This class implements the carrier of invocation result information.
 */
public final class BinRpcInvocationResult
    implements Serializable
{
    /**
     * Serial version UID.
     */
    private final static long serialVersionUID = 1L;

    /**
     * Result value.
     */
    private final Object value;

    /**
     * Exception value.
     */
    private final Throwable exception;

    /**
     * Construct the result.
     */
    public BinRpcInvocationResult( Object value )
    {
        this.value = value;
        this.exception = null;
    }

    /**
     * Construct the result.
     */
    public BinRpcInvocationResult( Throwable exception )
    {
        this.value = null;
        this.exception = exception;
    }

    /**
     * Return the value.
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     * Return the exception.
     */
    public Throwable getException()
    {
        return this.exception;
    }

    /**
     * Return true if it has exception.
     */
    public boolean hasException()
    {
        return this.exception != null;
    }

    /**
     * Recreate the invocation result, either returning the result value in case of a successful invocation of the target method, or
     * rethrowing the exception thrown by the target method.
     */
    public Object recreate()
        throws Throwable
    {
        if ( this.exception != null )
        {
            Throwable exToThrow = this.exception;
            if ( this.exception instanceof InvocationTargetException )
            {
                exToThrow = ( (InvocationTargetException) this.exception ).getTargetException();
            }

            fillInStackTrace( exToThrow );
            throw exToThrow;
        }
        else
        {
            return this.value;
        }
    }

    /**
     * Fill in stack trace.
     */
    private static void fillInStackTrace( Throwable ex )
    {
        StackTraceElement[] clientStack = new Throwable().getStackTrace();
        Throwable exToUpdate = ex;

        while ( exToUpdate != null )
        {
            StackTraceElement[] serverStack = exToUpdate.getStackTrace();
            StackTraceElement[] combinedStack = new StackTraceElement[serverStack.length + clientStack.length];
            System.arraycopy( serverStack, 0, combinedStack, 0, serverStack.length );
            System.arraycopy( clientStack, 0, combinedStack, serverStack.length, clientStack.length );
            exToUpdate.setStackTrace( combinedStack );
            exToUpdate = exToUpdate.getCause();
        }
    }
}

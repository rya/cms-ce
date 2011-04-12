/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.enonic.cms.portal.datasource.DataSourceContext;

/**
 * Keeps track of all executed methods and the result returned.  If a method is invoked with the same parameters, the same result is
 * returned as in the first invocation, without executing the method.
 */
public final class InvocationCache
{

    /**
     * Source target.
     */
    private final Object target;

    /**
     * A cache of invocations.
     */
    private final HashMap<String, Object> cache;

    public InvocationCache( Object target )
    {
        this.target = target;
        this.cache = new HashMap<String, Object>();
    }

    public Object getTarget()
    {
        return this.target;
    }

    public Class getTargetClass()
    {
        return this.target.getClass();
    }

    private void appendSignature( StringBuffer str, Method method )
    {
        str.append( method.getName() ).append( "(" );

        Class[] params = method.getParameterTypes();
        for ( int i = 0; i < params.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( "," );
            }

            str.append( params[i].getName() );
        }

        str.append( ")" );
    }


    private String getCacheKey( Method method, Object[] args )
    {
        StringBuffer str = new StringBuffer();
        appendSignature( str, method );
        str.append( "-" );
        appendArguments( str, args );
        return str.toString();
    }

    public Object invoke( Method method, Object[] args )
        throws Throwable
    {
        Object result;

        if ( isCacheable( method ) )
        {
            String key = getCacheKey( method, args );
            result = this.cache.get( key );

            if ( result == null )
            {
                result = invokeReal( method, args );
                this.cache.put( key, result );
            }
        }
        else
        {
            result = invokeReal( method, args );
        }

        return result;
    }

    private boolean isCacheable( Method method )
    {
        // we cant cache getPreferences calls, cause they depend on objectKey, which change within a request
        return !method.getName().startsWith( "getPreferences" );
    }

    private Object invokeReal( Method method, Object[] args )
        throws Throwable
    {
        try
        {
            return method.invoke( this.target, args );
        }
        catch ( InvocationTargetException e )
        {
            throw e.getTargetException();
        }
    }

    private void appendArguments( StringBuffer str, Object[] args )
    {
        for ( int i = 0; i < args.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( "," );
            }

            if ( args[i] == null )
            {
                str.append( "null" );
            }
            else if ( args[i] instanceof int[] )
            {

                appendArguments( str, (int[]) args[i] );
            }
            else if ( ( args[i] instanceof DataSourceContext) )
            {
                // skip data source context, not necessary as long as it contains the same values for every datasource
            }
            else
            {
                str.append( args[i].toString() );
            }

        }
    }

    private void appendArguments( StringBuffer str, int[] args )
    {
        str.append( "{" );
        for ( int i = 0; i < args.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( "," );
            }
            str.append( args[i] );
        }
        str.append( "}" );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.methodcall;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocumentHelper;

import com.enonic.cms.portal.InvocationCache;
import com.enonic.cms.portal.datasource.DatasourceException;
import com.enonic.cms.portal.rendering.tracing.RenderTrace;

public final class MethodCall
{
    private final Object target;

    private final MethodCallParameter[] parameters;

    private final Method method;

    private final boolean isCacheable;

    public MethodCall( Object target, MethodCallParameter[] parameters, Method method, boolean isCacheable )
    {
        this.target = target;
        this.parameters = parameters;
        this.method = method;
        this.isCacheable = isCacheable;
    }

    public Document invoke()
    {
        Object o = null;

        try
        {
            RenderTrace.enterFunction( method.getName() );
            o = invokeMethod( target, method, getArguments(), isCacheable );
        }
        catch ( Throwable iae )
        {
            MethodCallParameter[] params = getParameters();
            int numParams = params.length;
            if ( ( params.length > 0 ) && params[0].isContext() )
            {
                numParams--;
            }
            throw new DatasourceException( "Failed to execute method [" + method.getName() + "] with " + numParams + " parameters",
                                               iae );
        }
        finally
        {
            RenderTrace.exitFunction();
        }

        if ( o instanceof org.jdom.Document )
        {
            return (Document) o;
        }
        else if ( o instanceof org.w3c.dom.Document )
        {
            return XMLDocumentHelper.convertToJDOMDocument( (org.w3c.dom.Document) o );
        }
        else
        {
            return createValueDocument( o );
        }
    }

    private Document createValueDocument( Object value )
    {
        Element root = new Element( "value" );
        if ( value != null )
        {
            root.setText( value.toString() );
        }

        return new Document( root );
    }

    private Object invokeMethod( Object target, Method method, Object[] args, boolean isCacheable )
        throws Throwable
    {
        try
        {
            if ( target instanceof InvocationCache )
            {
                return ( (InvocationCache) target ).invoke( method, args, isCacheable );
            }
            else
            {
                return method.invoke( target, args );
            }
        }
        catch ( InvocationTargetException e )
        {
            throw e.getTargetException();
        }
    }


    public String getMethodName()
    {
        return this.method.getName();
    }

    public Class[] getParamTypes()
    {
        Class[] paramTypes = new Class[parameters.length];
        for ( int i = 0; i < parameters.length; i++ )
        {
            paramTypes[i] = parameters[i].getType();
        }
        return paramTypes;
    }

    public Object[] getArguments()
    {
        Object[] args = new Object[parameters.length];
        for ( int i = 0; i < parameters.length; i++ )
        {
            args[i] = parameters[i].getArgument();
        }
        return args;
    }

    public MethodCallParameter[] getParameters()
    {
        return parameters;
    }

}

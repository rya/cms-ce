/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.datasource.methodcall;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.vertical.presentation.renderer.VerticalRenderException;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.business.portal.InvocationCache;
import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;

public final class MethodCall
{
    private final Object target;

    private final InvocationCache dataSourceObject;

    private final MethodCallParameter[] parameters;

    private final Method method;

    private final boolean isCacheable;

    public MethodCall( InvocationCache dataSourceObject, Object target, MethodCallParameter[] parameters, Method method, boolean isCacheable )
    {
        this.dataSourceObject = dataSourceObject;
        this.target = target;
        this.parameters = parameters;
        this.method = method;
        this.isCacheable = isCacheable;
    }

    public XMLDocument invoke()
    {
        Object o = null;

        try
        {
            RenderTrace.enterFunction( method.getName() );
            o = invokeMethod( dataSourceObject, target, method, getArguments(), isCacheable );
        }
        catch ( Throwable iae )
        {
            MethodCallParameter[] params = getParameters();
            int numParams = params.length;
            if ( ( params.length > 0 ) && params[0].isContext() )
            {
                numParams--;
            }
            throw new VerticalRenderException( "Failed to execute method [" + method.getName() + "] with " + numParams + " parameters",
                                               iae );
        }
        finally
        {
            RenderTrace.exitFunction();
        }

        if ( o instanceof XMLDocument )
        {
            return (XMLDocument) o;
        }
        else if ( o instanceof Document )
        {
            return XMLDocumentFactory.create( (Document) o );
        }
        else if ( o instanceof org.w3c.dom.Document )
        {
            return XMLDocumentFactory.create( (org.w3c.dom.Document) o );
        }
        else
        {
            return createValueDocument( o );
        }
    }

    private XMLDocument createValueDocument( Object value )
    {
        Element root = new Element( "value" );
        if ( value != null )
        {
            root.setText( value.toString() );
        }

        return XMLDocumentFactory.create( new Document( root ) );
    }

    private Object invokeMethod( InvocationCache dataSourceObject, Object target, Method method, Object[] args, boolean isCacheable )
        throws Throwable
    {
        try
        {
            if ( target instanceof InvocationCache )
            {
                InvocationCache invocationCache = (InvocationCache) target;
                return dataSourceObject.invoke( invocationCache.getTarget(), method, args, isCacheable );
            }
            else
            {
                return dataSourceObject.invoke( target, method, args, isCacheable );
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

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.binrpc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class implements the carrier of invocation information.
 */
public final class BinRpcInvocation
    implements Serializable
{

    private final static long serialVersionUID = 1L;

    private final String methodName;

    private final Class<?>[] parameterTypes;

    private final Object[] arguments;

    public BinRpcInvocation( String methodName, Class<?>[] parameterTypes, Object[] arguments )
    {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }

    public String getMethodName()
    {
        return this.methodName;
    }

    public Class<?>[] getParameterTypes()
    {
        return this.parameterTypes;
    }

    public Object[] getArguments()
    {
        return this.arguments;
    }

    public Object invoke( Object targetObject )
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Method method = targetObject.getClass().getMethod( this.methodName, this.parameterTypes );
        return method.invoke( targetObject, this.arguments );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.viewtransformer;

/**
 * May 20, 2009
 */
public abstract class AbstractTransformationParameter
{
    private String name;

    private TransformationParameterOrigin origin;

    public AbstractTransformationParameter( String name, TransformationParameterOrigin origin )
    {
        this.name = name;
        this.origin = origin;
    }

    public String getName()
    {
        return name;
    }

    public TransformationParameterOrigin getOrigin()
    {
        return origin;
    }

    @Override
    public String toString()
    {
        return "TransformationParameter{" + "name='" + name + '\'' + ", origin=" + origin + '}';
    }
}

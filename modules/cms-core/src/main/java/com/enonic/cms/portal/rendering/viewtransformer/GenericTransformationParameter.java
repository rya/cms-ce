/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.viewtransformer;

/**
 * May 20, 2009
 */
public class GenericTransformationParameter
    extends AbstractTransformationParameter
    implements TransformationParameter
{
    private Object value;

    public GenericTransformationParameter( String name, Object value, TransformationParameterOrigin origin )
    {
        super( name, origin );
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }
}

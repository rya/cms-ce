/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.viewtransformer;

/**
 * May 20, 2009
 */
public class StringTransformationParameter
    extends AbstractTransformationParameter
    implements TransformationParameter
{
    private String value;

    public StringTransformationParameter( String name, String value, TransformationParameterOrigin origin )
    {
        super( name, origin );
        this.value = value == null ? "" : value;
    }

    public Object getValue()
    {
        return value;
    }
}

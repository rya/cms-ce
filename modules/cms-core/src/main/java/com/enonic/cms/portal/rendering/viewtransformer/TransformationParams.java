/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.viewtransformer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * May 20, 2009
 */
public class TransformationParams
{
    private Map<String, TransformationParameter> params = new LinkedHashMap<String, TransformationParameter>();

    public void add( TransformationParameter param )
    {
        params.put( param.getName(), param );
    }

    public boolean notContains( String paramName )
    {
        return !params.containsKey( paramName );
    }

    public boolean contains( String paramName )
    {
        return params.containsKey( paramName );
    }

    public TransformationParameter get( String name )
    {
        return params.get( name );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rmy - Date: Apr 29, 2009
 */
public class ScriptResolverResult
{
    private final static String REDIRECT_RETURN_VALUE_KEY = "resolverRedirectValue";


    protected Map<String, Object> resolverReturnValues = new HashMap<String, Object>();

    public String getRedirectUrl()
    {
        return (String) resolverReturnValues.get( REDIRECT_RETURN_VALUE_KEY );
    }

    public Map<String, Object> getResolverReturnValues()
    {
        return resolverReturnValues;
    }
}

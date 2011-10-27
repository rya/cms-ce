/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.deviceclass;

import org.springframework.stereotype.Component;

import com.enonic.cms.core.resolver.AbstractXsltScriptResolver;
import com.enonic.cms.core.resolver.ScriptResolverResult;

@Component
public class DeviceClassXsltScriptResolver
    extends AbstractXsltScriptResolver
{
    public final static String DEVICE_CLASS_RETURN_VALUE_KEY = "deviceClassReturnValue";

    protected ScriptResolverResult populateScriptResolverResult( String resolvedValue )
    {
        ScriptResolverResult result = new ScriptResolverResult();
        result.getResolverReturnValues().put( DEVICE_CLASS_RETURN_VALUE_KEY, resolvedValue );

        return result;
    }
}

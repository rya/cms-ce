/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.deviceclass.mock;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.resolver.ResolverHttpRequestInput;
import com.enonic.cms.core.resolver.ResolverHttpRequestInputCreator;

/**
 * Created by rmy - Date: Apr 15, 2009
 */
public class DeviceClassResolverHttpRequestInputCreatorMock
    extends ResolverHttpRequestInputCreator
{
    public ResolverHttpRequestInput createResolverHttpRequestInput( HttpServletRequest request )
    {
        ResolverHttpRequestInput resolverHttpRequestInput = new ResolverHttpRequestInput();
        return resolverHttpRequestInput;
    }

}

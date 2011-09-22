/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.mock;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.resolver.ResolverHttpRequestInput;
import com.enonic.cms.core.resolver.ResolverHttpRequestInputCreator;

/**
 * Created by rmy - Date: Aug 24, 2009
 */
public class ResolverHttpRequestInputCreatorMock
    extends ResolverHttpRequestInputCreator
{

    public ResolverHttpRequestInput createResolverHttpRequestInput( HttpServletRequest request )
    {
        ResolverHttpRequestInput httpRequestInput = new ResolverHttpRequestInput();

        return httpRequestInput;
    }

}

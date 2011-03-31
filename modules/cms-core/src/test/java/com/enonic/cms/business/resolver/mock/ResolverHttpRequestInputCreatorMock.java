/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.resolver.mock;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.business.resolver.ResolverHttpRequestInputCreator;

import com.enonic.cms.domain.resolver.ResolverHttpRequestInput;

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

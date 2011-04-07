/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.resolver;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.enonic.cms.core.resource.ResourceService;

import com.enonic.cms.domain.resolver.ResolverContext;

import static org.easymock.EasyMock.createMock;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by rmy - Date: May 6, 2009
 */
public class BaseResolverTest
{

    protected ForceResolverValueService forcedResolverValueService;

    protected CacheResolverValueService cacheResolverValueService;

    protected MockHttpServletRequest request;

    protected MockHttpServletResponse response;

    protected HttpSession session;

    protected ResourceService resourceServiceMock;

    @Before
    public void setUpSuper()
    {

        resourceServiceMock = createMock( ResourceService.class );

        request = new MockHttpServletRequest();
        request.setRequestURI( "/site/0/Frontpage" );

        response = new MockHttpServletResponse();
        session = new MockHttpSession();

        forcedResolverValueService = mock( ForceResolverValueService.class );
        cacheResolverValueService = mock( CacheResolverValueService.class );
    }

    @Test
    public void testNothing()
    {

    }

    protected void setUpForcedResolverValue( String forcedValue )
    {
        when( forcedResolverValueService.getForcedResolverValue( isA( ResolverContext.class ), isA( String.class ) ) ).thenReturn(
            forcedValue );
    }

    protected void setUpCachedValue( String cachedValue )
    {
        when( cacheResolverValueService.getCachedResolverValue( isA( ResolverContext.class ), isA( String.class ) ) ).thenReturn(
            cachedValue );
        when( cacheResolverValueService.setCachedResolverValue( isA( ResolverContext.class ), isA( String.class ),
                                                                isA( String.class ) ) ).thenReturn( true );
    }


}

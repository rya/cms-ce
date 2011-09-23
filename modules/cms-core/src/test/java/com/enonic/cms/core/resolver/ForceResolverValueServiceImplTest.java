/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: May 6, 2009
 */
public class ForceResolverValueServiceImplTest
{

    private ForceResolverValueServiceImpl forcedResolverValueServiceImpl;

    private MockHttpServletRequest requestMock;

    private MockHttpServletResponse responseMock;

    private HttpSession sessionMock;

    private static final String FORCED_VALUE_KEY = "forcedValueKey";

    private static final String FORCED_VALUE_VALUE = "forcedValue";

    @Before
    public void setUp()
    {

        forcedResolverValueServiceImpl = new ForceResolverValueServiceImpl();

        requestMock = new MockHttpServletRequest();
        requestMock.setRequestURI( "/site/0/Frontpage" );

        responseMock = new MockHttpServletResponse();
        sessionMock = new MockHttpSession();

    }

    @Test
    public void testSetPermanentForcedValue()
    {

        ResolverContext context = new ResolverContext( requestMock, null );

        forcedResolverValueServiceImpl.setForcedValue( context, responseMock, FORCED_VALUE_KEY,
                                                       ForcedResolverValueLifetimeSettings.permanent, FORCED_VALUE_VALUE );

        String forcedValue = responseMock.getCookie( FORCED_VALUE_KEY ).getValue();

        assertEquals( FORCED_VALUE_VALUE, forcedValue );
    }

    @Test
    public void testSetSessionForcedValue()
    {

        ResolverContext context = new ResolverContext( requestMock, null );

        forcedResolverValueServiceImpl.setForcedValue( context, responseMock, FORCED_VALUE_KEY, ForcedResolverValueLifetimeSettings.session,
                                                       FORCED_VALUE_VALUE );

        String forcedValue = forcedResolverValueServiceImpl.getForcedResolverValue( context, FORCED_VALUE_KEY );

        String sessionForcedValue = (String) requestMock.getSession().getAttribute( FORCED_VALUE_KEY );

        assertEquals( FORCED_VALUE_VALUE, forcedValue );
        assertEquals( FORCED_VALUE_VALUE, sessionForcedValue );
    }


    @Test
    public void testClearPermanentForcedValue()
    {

        Cookie cookie = new Cookie( FORCED_VALUE_KEY, FORCED_VALUE_VALUE );
        cookie.setMaxAge( 999 );
        Cookie[] cookies = new Cookie[]{cookie};

        requestMock.setCookies( cookies );

        ResolverContext context = new ResolverContext( requestMock, null );

        forcedResolverValueServiceImpl.clearForcedValue( context, responseMock, FORCED_VALUE_KEY );

        assertEquals( 0, responseMock.getCookie( FORCED_VALUE_KEY ).getMaxAge() );

    }

}



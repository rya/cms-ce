/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.spring.mvc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

public class RedirectAndForwardHelperTest
    extends TestCase
{

    private RedirectAndForwardHelper redirectAndForwardHelper;

    private MockHttpServletRequest request;


    protected void setUp()
        throws Exception
    {
        redirectAndForwardHelper = new RedirectAndForwardHelper();
        request = new MockHttpServletRequest();
    }

    public void testGetForwardModelAndView()
    {

        String path = "/Frontpage/";

        ModelAndView modelAndView = redirectAndForwardHelper.getForwardModelAndView( request, path );

        assertEquals( "customforward", modelAndView.getViewName() );
        Map model = modelAndView.getModel();
        assertEquals( "/Frontpage/", model.get( "path" ) );
    }

    public void testGetForwardModelAndViewWithParams()
    {

        String path = "/Frontpage/";

        Map params = new HashMap();
        params.put( "key1", "val1" );
        params.put( "key2", "val2" );
        ModelAndView modelAndView = redirectAndForwardHelper.getForwardModelAndView( request, path, params );

        Map model = modelAndView.getModel();
        assertEquals( "customforward", modelAndView.getViewName() );
        assertEquals( "/Frontpage/", model.get( "path" ) );

        Map<String, Object> requestParams = (Map<String, Object>) model.get( "requestParams" );
        assertEquals( requestParams.get( "key1" ), "val1" );
        assertEquals( requestParams.get( "key2" ), "val2" );

    }

    public void testGetRedirectModelAndView()
    {

        String path = "/Frontpage/";

        ModelAndView modelAndView = redirectAndForwardHelper.getRedirectModelAndView( request, path );

        assertEquals( "redirect:/Frontpage/", modelAndView.getViewName() );
    }

    public void testGetRedirectModelAndViewWithParams()
    {

        String path = "/Frontpage/";

        Map params = new HashMap();
        params.put( "key1", "val1" );
        params.put( "key2", "val2" );
        ModelAndView modelAndView = redirectAndForwardHelper.getRedirectModelAndView( request, path, params );

        boolean eq1 = "redirect:/Frontpage/?key1=val1&key2=val2".equals( modelAndView.getViewName() );
        boolean eq2 = "redirect:/Frontpage/?key2=val2&key1=val1".equals( modelAndView.getViewName() );
        assertTrue( eq1 | eq2 );

    }
}

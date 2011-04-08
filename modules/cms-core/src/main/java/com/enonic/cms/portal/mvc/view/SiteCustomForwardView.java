/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.enonic.esl.servlet.http.HttpServletRequestWrapper;

public class SiteCustomForwardView
    extends AbstractView
{

    @SuppressWarnings({"unchecked"})
    protected void renderMergedOutputModel( Map model, HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {

        Map<String, String[]> params = (Map<String, String[]>) model.get( "requestParams" );

        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper( request, params );

        String path = (String) model.get( "path" );
        request.getRequestDispatcher( path ).forward( wrappedRequest, response );
    }
}

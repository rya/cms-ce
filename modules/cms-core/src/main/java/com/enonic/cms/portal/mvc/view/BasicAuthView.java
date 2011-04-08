/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

public final class BasicAuthView
    extends AbstractView
{

    @SuppressWarnings({"unchecked"})
    protected void renderMergedOutputModel( Map model, HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        response.setHeader( "WWW-Authenticate", "Basic" );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.model;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

public class StandardModelFactory
{
    private UrlPathHelper urlDecodingUrlPathHelper;


    public void setUrlDecodingUrlPathHelper( UrlPathHelper value )
    {
        this.urlDecodingUrlPathHelper = value;
    }

    public Map<String, String> createStandardModel( HttpServletRequest request )
    {
        Map<String, String> model = new HashMap<String, String>();
        model.put( "contextPath", urlDecodingUrlPathHelper.getContextPath( request ) );
        return model;
    }
}

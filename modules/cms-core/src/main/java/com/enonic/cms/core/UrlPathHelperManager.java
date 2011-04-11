/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.util.UrlPathHelper;

import com.enonic.cms.domain.SiteKey;

public class UrlPathHelperManager
{

    private SitePropertiesService sitePropertiesService;

    private Map urlPathHelperMapBySiteKey = new HashMap();

    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

    public synchronized UrlPathHelper getUrlPathHelper( SiteKey siteKey )
    {

        UrlPathHelper urlPathHelper = (UrlPathHelper) urlPathHelperMapBySiteKey.get( siteKey );
        if ( urlPathHelper == null )
        {
            urlPathHelper = createUrlPathHelper( siteKey );
            urlPathHelperMapBySiteKey.put( siteKey, urlPathHelper );
        }
        return urlPathHelper;
    }

    private UrlPathHelper createUrlPathHelper( SiteKey siteKey )
    {

        String defaultEncoding =
                sitePropertiesService.getProperty( SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, siteKey );

        SiteUrlPathHelper urlPathHelper = new SiteUrlPathHelper();
        urlPathHelper.setUrlDecode( true );
        urlPathHelper.setDefaultEncoding( defaultEncoding );
        return urlPathHelper;
    }

}

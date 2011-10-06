/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.domain.SiteKey;

/**
 * Oct 28, 2009
 */
public class ContentLocationSpecification
{
    private SiteKey siteKey;

    private boolean includeInactiveLocationsInSection = true;

    public void setSiteKey( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public void setIncludeInactiveLocationsInSection( boolean includeInactiveLocationsInSection )
    {
        this.includeInactiveLocationsInSection = includeInactiveLocationsInSection;
    }

    public boolean includeInactiveLocationsInSection()
    {
        return includeInactiveLocationsInSection;
    }
}

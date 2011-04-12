/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem.section;

import com.enonic.cms.domain.SiteKey;

public class ContentHomeSpecification
{
    private SiteKey siteKey;

    private boolean includeUnActivesInSection = true;

    public ContentHomeSpecification( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public void setIncludeUnActivesInSection( boolean value )
    {
        this.includeUnActivesInSection = value;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public boolean satisifes( SectionContentEntity sectionContent )
    {
        if ( !sectionContent.getSite().getKey().equals( siteKey ) )
        {
            return false;
        }

        if ( !includeUnActivesInSection && !sectionContent.isApproved() )
        {
            return false;
        }

        return true;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import com.enonic.cms.domain.SiteKey;

public interface SiteEventListener
{
    void onSiteRegistered( SiteKey siteKey );

    void onSiteUnregistered( SiteKey siteKey );
}

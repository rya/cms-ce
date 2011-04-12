/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.util.List;

import com.enonic.cms.core.SiteContext;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.portal.SiteNotFoundException;
import com.enonic.cms.core.security.user.User;

public interface SiteService
{


    /**
     * Returns true if site exists, otherwise false.
     *
     * @param siteKey the key identifying the site
     */
    boolean siteExists( SiteKey siteKey );

    /**
     * Checks if site exists. If not a SiteNotFoundException is thrown.
     */
    public void checkSiteExist( SiteKey siteKey )
        throws SiteNotFoundException;

    /**
     * Returns the site's SiteContext. If the site have'nt been requested before the site is initialised with it's necessaries (caches
     * etc).
     *
     * @param siteKey the key identifying the site
     * @throws com.enonic.cms.portal.SiteNotFoundException if site is not found
     */
    SiteContext getSiteContext( SiteKey siteKey )
        throws SiteNotFoundException;

    public void registerSiteEventListener( SiteEventListener l );

    public void unregisterSiteEventListener( SiteEventListener l );

    List<SiteEntity> getSitesToPublishTo( int contentTypeKey, User user );

    List<SiteEntity> findAll();
}

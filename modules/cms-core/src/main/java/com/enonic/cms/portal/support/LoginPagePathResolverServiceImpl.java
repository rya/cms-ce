/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.support;

import javax.inject.Inject;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.portal.LoginPageNotFoundException;
import com.enonic.cms.portal.ReservedLocalPaths;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

public class LoginPagePathResolverServiceImpl
    implements LoginPagePathResolverService
{

    @Inject
    private SiteDao siteDao;

    public SitePath resolvePathToUserServicesLoginPage( SitePath sitePath )
    {
        // forward to userservice "login" for processing
        SitePath loginSitePath = new SitePath( sitePath.getSiteKey(), ReservedLocalPaths.PATH_USERSERVICES, sitePath.getParams() );
        loginSitePath.addParam( "_handler", "user" );
        loginSitePath.addParam( "_op", "login" );
        return loginSitePath;
    }

    public SitePath resolvePathToDefaultPageInMenu( SitePath sitePath )
    {
        SiteKey siteKey = sitePath.getSiteKey();
        SiteEntity site = siteDao.findByKey( siteKey );
        MenuItemEntity loginPage = site.getLoginPage();
        if ( loginPage == null )
        {
            throw new LoginPageNotFoundException( siteKey );
        }

        Path newLocalPath = new Path( loginPage.getPathAsString() );
        return sitePath.createNewInSameSite( newLocalPath, sitePath.getParams() );
    }
}

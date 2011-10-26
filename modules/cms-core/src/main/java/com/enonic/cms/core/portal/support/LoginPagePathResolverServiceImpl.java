/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.support;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.portal.LoginPageNotFoundException;
import com.enonic.cms.core.portal.ReservedLocalPaths;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.store.dao.SiteDao;

public class LoginPagePathResolverServiceImpl
    implements LoginPagePathResolverService
{
    @Autowired
    private SiteDao siteDao;

    public SitePath resolvePathToUserServicesLoginPage( final SitePath sitePath )
    {
        final SitePath loginSitePath = new SitePath( sitePath.getSiteKey(), ReservedLocalPaths.PATH_USERSERVICES, sitePath.getParams() );
        loginSitePath.addParam( "_handler", "user" );
        loginSitePath.addParam( "_op", "login" );
        return loginSitePath;
    }

    public SitePath resolvePathToDefaultPageInMenu( final SitePath sitePath )
    {
        final SiteKey siteKey = sitePath.getSiteKey();
        final SiteEntity siteEntity = this.siteDao.findByKey(siteKey);
        final MenuItemEntity loginPage = siteEntity.getLoginPage();

        if ( loginPage == null )
        {
            throw new LoginPageNotFoundException( siteKey );
        }

        final Path newLocalPath = loginPage.getPath();
        return sitePath.createNewInSameSite( newLocalPath, sitePath.getParams() );
    }
}

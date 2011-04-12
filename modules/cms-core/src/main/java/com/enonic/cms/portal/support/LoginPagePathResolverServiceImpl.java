/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.support;

import com.enonic.cms.portal.ReservedLocalPaths;
import com.enonic.vertical.engine.Types;

import com.enonic.cms.core.service.PresentationService;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.portal.LoginPageNotFoundException;

public class LoginPagePathResolverServiceImpl
    implements LoginPagePathResolverService
{

    private PresentationService presentationService;

    public void setPresentationService( PresentationService value )
    {
        this.presentationService = value;
    }

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
        int menuItemKey = presentationService.getLoginPage( siteKey.toInt() );
        if ( menuItemKey < 0 )
        {
            throw new LoginPageNotFoundException( siteKey );
        }

        Path newLocalPath = new Path( presentationService.getPathString( Types.MENUITEM, menuItemKey, false ) );
        return sitePath.createNewInSameSite( newLocalPath, sitePath.getParams() );
    }
}

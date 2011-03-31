/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.support;

import com.enonic.cms.domain.SitePath;

public interface LoginPagePathResolverService
{

    SitePath resolvePathToUserServicesLoginPage( SitePath sitePath );

    SitePath resolvePathToDefaultPageInMenu( SitePath sitePath );
}

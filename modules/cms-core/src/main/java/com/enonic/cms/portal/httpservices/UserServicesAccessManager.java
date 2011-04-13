/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.httpservices;

import com.enonic.cms.domain.SiteKey;


public interface UserServicesAccessManager
{
    boolean isOperationAllowed( SiteKey site, String service, String operation );
}

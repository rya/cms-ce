/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import com.enonic.cms.domain.SiteKey;

public interface PresentationService
{

    /**
     * Get error page key for a menu.
     *
     * @param menuKey menu key
     * @return error page key
     */
    public int getErrorPage( int menuKey );

    /**
     * Get login page key for a menu.
     *
     * @param menuKey menu key
     * @return error page key
     */
    public int getLoginPage( int menuKey );

    public boolean hasErrorPage( int menuKey );

    public boolean siteExists( SiteKey siteKey );


}

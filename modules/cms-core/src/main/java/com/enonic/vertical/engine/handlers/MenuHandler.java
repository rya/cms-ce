/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import com.enonic.cms.core.structure.SiteEntity;

public final class MenuHandler
    extends BaseHandler
{
    public int getErrorPage( int key )
    {
        SiteEntity entity = siteDao.findByKey( key );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getErrorPage().getKey();
        }
    }

    public int getLoginPage( int key )
    {
        SiteEntity entity = siteDao.findByKey( key );
        if ( ( entity == null ) || ( entity.getLoginPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getLoginPage().getKey();
        }
    }
}

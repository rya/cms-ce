/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import javax.inject.Inject;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.SiteKey;

public class PresentationServiceImpl
    implements PresentationService
{

    @Inject
    protected ContentBinaryDataDao contentBinaryDataDao;

    @Inject
    protected SecurityService securityService;

    @Inject
    protected ContentDao contentDao;

    @Inject
    protected BinaryDataDao binaryDataDao;

    @Inject
    private SiteDao siteDao;


    /**
     * Get error page key for a menu.
     *
     * @param menuKey menu key
     * @return error page key
     */
    public int getErrorPage( int menuKey )
    {
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getErrorPage().getKey();
        }
    }

    public boolean hasErrorPage( int menuKey )
    {
        int result;
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            result = -1;
        }
        else
        {
            result = entity.getErrorPage().getKey();
        }
        return result >= 0;
    }

    public int getLoginPage( int menuKey )
    {
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getLoginPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getLoginPage().getKey();
        }
    }

    public boolean siteExists( SiteKey siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        return ( site != null );
    }


}

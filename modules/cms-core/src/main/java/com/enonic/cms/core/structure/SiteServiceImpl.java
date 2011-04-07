/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.service.PresentationService;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.business.SiteContext;
import com.enonic.cms.business.SiteContextManager;
import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.portal.cache.SiteCachesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.portal.SiteNotFoundException;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.SiteEntity;

public class SiteServiceImpl
        implements SiteService
{

    private static final Logger LOG = LoggerFactory.getLogger( SiteServiceImpl.class );

    private PresentationService presentationService;

    private SiteCachesService siteCachesService;

    private SiteContextManager siteContextManager;

    private SitePropertiesService sitePropertiesService;

    private List<SiteEventListener> siteEventListeners = new ArrayList<SiteEventListener>();

    private SiteDao siteDao;

    private UserDao userDao;

    private final Object lock = new Object();


    public void registerSiteEventListener( SiteEventListener l )
    {
        siteEventListeners.add( l );
    }

    public void unregisterSiteEventListener( SiteEventListener l )
    {
        siteEventListeners.remove( l );
    }

    private void fireSiteRegisteredEvent( SiteKey siteKey )
    {
        for ( SiteEventListener l : siteEventListeners )
        {
            l.onSiteRegistered( siteKey );
        }
    }

    private void fireSiteUnregisteredEvent( SiteKey siteKey )
    {
        for ( SiteEventListener l : siteEventListeners )
        {
            l.onSiteUnregistered( siteKey );
        }
    }

    private void registerSite( SiteKey siteKey )
    {

        synchronized ( lock )
        {
            if ( !siteContextManager.isRegistered( siteKey ) )
            {
                LOG.info( "Site [" + siteKey + "] is registering..." );
                SiteContext siteContext = createSiteContext( siteKey );
                siteContextManager.registerSiteContext( siteContext );

                boolean usingCustomProperties = sitePropertiesService.getPropertyAsBoolean( "customSiteProperties", siteKey );
                String typeOfProperties = usingCustomProperties ? "custom" : "only default";

                LOG.info( "Site [" + siteKey + "] is registered, using " + typeOfProperties + " properties." );
            }
        }

        fireSiteRegisteredEvent( siteKey );
    }

    private void unregisterSite( SiteKey siteKey )
    {

        synchronized ( lock )
        {
            if ( siteContextManager.isRegistered( siteKey ) )
            {
                LOG.info( "Site [" + siteKey + "] is unregistering..." );

                siteContextManager.unregisterSiteContext( siteKey );
                siteCachesService.tearDownSiteCachesService( siteKey );

                LOG.info( "Site [" + siteKey + "] is unregistered." );
            }
        }

        fireSiteUnregisteredEvent( siteKey );
    }

    private SiteContext createSiteContext( SiteKey siteKey )
    {
        SiteContext siteContext = new SiteContext( siteKey );

        initCache( siteContext );

        siteContext.setAccessLoggingEnabled( sitePropertiesService.getPropertyAsBoolean( "cms.site.logging.access", siteKey ) );
        siteContext.setAuthenticationLoggingEnabled( sitePropertiesService.getPropertyAsBoolean( "cms.site.logging.authentication", siteKey ) );

        return siteContext;
    }

    private void initCache( SiteContext siteContext )
    {

        SiteKey siteKey = siteContext.getSiteKey();

        siteCachesService.setUpSiteCachesService( siteKey );

        siteContext.setPageAndObjectCacheService( siteCachesService.getPageCacheService( siteKey ) );
    }

    /**
     * @inheritDoc
     */
    public boolean siteExists( SiteKey siteKey )
    {
        return presentationService.siteExists( siteKey );
    }

    /**
     * @inheritDoc
     */
    public void checkSiteExist( SiteKey siteKey )
            throws SiteNotFoundException
    {
        if ( !siteExists( siteKey ) )
        {
            throw new SiteNotFoundException( siteKey );
        }
    }

    /**
     * @inheritDoc
     */
    public SiteContext getSiteContext( SiteKey siteKey )
            throws SiteNotFoundException
    {

        SiteContext siteContext = siteContextManager.getSiteContext( siteKey );

        boolean siteExistsInDb = presentationService.siteExists( siteKey );
        boolean isRegistered = siteContext != null;

        if ( siteExistsInDb && isRegistered )
        {
            return siteContext;
        }

        if ( siteExistsInDb && !isRegistered )
        {
            registerSite( siteKey );
            return getSiteContext( siteKey );
        }

        if ( !siteExistsInDb && isRegistered )
        {
            unregisterSite( siteKey );
            throw new SiteNotFoundException( siteKey );
        }

        throw new SiteNotFoundException( siteKey );
    }

    public List<SiteEntity> getSitesToPublishTo( int contentTypeKey, User oldUser )
    {

        UserEntity user = userDao.findByKey( oldUser.getKey() );
        return siteDao.findByPublishPossible( contentTypeKey, user );
    }

    public void setPresentationService( PresentationService value )
    {
        this.presentationService = value;
    }

    public void setSiteCachesService( SiteCachesService value )
    {
        this.siteCachesService = value;
    }

    public void setSiteContextManager( SiteContextManager value )
    {
        this.siteContextManager = value;
    }

    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

    public void setSiteDao( SiteDao value )
    {
        this.siteDao = value;
    }

    public void setUserDao( UserDao value )
    {
        this.userDao = value;
    }
}

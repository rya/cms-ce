/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.vertical.engine.PresentationEngine;

import com.enonic.cms.core.service.PresentationService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.security.user.User;

public class PresentationServiceImpl
    implements PresentationService
{

    private PresentationEngine presentationEngine;

    public void setPresentationEngine( PresentationEngine value )
    {
        presentationEngine = value;
    }

    /**
     * Returns only binary data that is newer than the timestamp specified. Only binary data for this site and for a
     * user that is allowed to see it are returned.
     *
     * @param user          the user accessing the binary data
     * @param binaryDataKey key to the binary data
     * @param menuKey       the menu key
     * @param url           used by the log handler to record which url loaded the binary
     * @param referrer      used by the log handler to record which referrer loaded the binary
     * @param timestamp     the timestamp to check against
     * @param updateLog     if true, update the log
     * @return binary data object with or without binary data depending on the timestamp
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BinaryData getBinaryData( User user, int binaryDataKey, int menuKey, String url, String referrer, long timestamp,
                                     boolean updateLog )
    {
        // must be write transaction because user can by synchronized to database
        return presentationEngine.getBinaryData( user, binaryDataKey, timestamp );
    }

    /**
     * Get error page key for a menu.
     *
     * @param menuKey menu key
     * @return error page key
     */
    public int getErrorPage( int menuKey )
    {
        return presentationEngine.getErrorPage( menuKey );
    }

    public boolean hasErrorPage( int menuKey )
    {
        return presentationEngine.hasErrorPage( menuKey );
    }

    public int getLoginPage( int menuKey )
    {
        return presentationEngine.getLoginPage( menuKey );
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        return presentationEngine.getBinaryDataKey( contentKey, label );
    }

    public String getPathString( int type, int key, boolean includeRoot )
    {
        return presentationEngine.getPathString( type, key, includeRoot );
    }

    public boolean siteExists( SiteKey siteKey )
    {
        return presentationEngine.siteExists( siteKey );
    }

    // public String renderPage( User user, int menuKey, int menuItemKey, String profile, HashMap parameters, int
    // pageTemplateKey,
    // String languageCode, String basePath, boolean encodeURIs )
    // {
    // return presentationEngine.renderPage( user, menuKey, menuItemKey, profile, parameters, pageTemplateKey,
    // languageCode, basePath,
    // encodeURIs );
    // }
    //
    // public String renderContentPage( User user, int menuKey, int contentKey, String profile, HashMap parameters,
    // String basePath,
    // boolean encodeURIs )
    // {
    // return presentationEngine.renderContentPage( user, menuKey, contentKey, profile, parameters, basePath, encodeURIs
    // );
    // }

}

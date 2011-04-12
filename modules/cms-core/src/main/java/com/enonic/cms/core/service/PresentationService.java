/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.security.user.User;

public interface PresentationService
{

    /**
     * Returns only binary data that is newer than the timestamp specified. Only binary data for this site and for a user that is allowed to
     * see it are returned.
     *
     * @param user          the user accessing the binary data
     * @param binaryDataKey key to the binary data
     * @param menuKey       The menu key.
     * @param url           used by the log handler to record which url loaded the binary
     * @param referrer      The refering page.
     * @param timestamp     the timestamp to check against
     * @param updateLog     if true, update the log
     * @return binary data object with or without binary data depending on the timestamp
     */
    public BinaryData getBinaryData( User user, int binaryDataKey, int menuKey, String url, String referrer, long timestamp,
                                     boolean updateLog );

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

    public String getPathString( int type, int key, boolean includeRoot );

    public boolean hasErrorPage( int menuKey );

    public int getBinaryDataKey( int contentKey, String label );

    public boolean siteExists( SiteKey siteKey );

//    public String renderPage( User user, int menuKey, int menuItemKey, String profile, HashMap parameters, int pageTemplateKey,
//                              String languageCode, String basePath, boolean encodeURIs );
//
//    public String renderContentPage( User user, int menuKey, int contentKey, String profile, HashMap parameters, String basePath,
//                                     boolean encodeURIs );

}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.portal.PortalInstanceKey;


public class PreferenceScopeKeyResolver
{

    public static PreferenceScopeKey resolve( PreferenceScopeType scopeType, PortalInstanceKey instanceKey, SiteKey siteKey )
        throws IllegalArgumentException
    {

        switch ( scopeType )
        {

            case GLOBAL:
                break;

            case SITE:
                if ( siteKey == null )
                {
                    throw new IllegalArgumentException( "siteKey is null" );
                }
                return new PreferenceScopeKey( siteKey.toString() );

            case PAGE:
                if ( instanceKey.getMenuItemKey() == null )
                {
                    throw new IllegalArgumentException( "pageKey is null" );
                }
                return new PreferenceScopeKey( instanceKey.getMenuItemKey().toString() );

            case PORTLET:
                if ( instanceKey.getPortletKey() == null )
                {
                    throw new IllegalArgumentException( "portletKey is null" );
                }
                return new PreferenceScopeKey( instanceKey.getPortletKey().toString() );

            case WINDOW:
                if ( instanceKey.getMenuItemKey() == null || instanceKey.getPortletKey() == null )
                {
                    throw new IllegalArgumentException( "pageKey or portletKey is null" );
                }
                return new PreferenceScopeKey( instanceKey.getMenuItemKey().toString() + ":" + instanceKey.getPortletKey().toString() );
        }

        return null;
    }
}

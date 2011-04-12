/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.portal.PortalInstanceKey;


public class PreferenceScopeResolver
{

    public static List<PreferenceScope> resolveScopes( String scopeTypeNames, PortalInstanceKey instanceKey, SiteKey siteKey )
    {

        List<PreferenceScope> scopes = new ArrayList<PreferenceScope>();
        List<PreferenceScopeType> scopeTypes = PreferenceScopeType.parseScopes( scopeTypeNames );
        for ( PreferenceScopeType type : scopeTypes )
        {
            scopes.add( resolveScope( type, instanceKey, siteKey ) );
        }
        return scopes;
    }

    private static PreferenceScope resolveScope( PreferenceScopeType type, PortalInstanceKey instanceKey, SiteKey siteKey )
    {
        PreferenceScopeKey key = PreferenceScopeKeyResolver.resolve( type, instanceKey, siteKey );
        return new PreferenceScope( type, key );
    }

    public static List<PreferenceScope> resolveAllScopes( PortalInstanceKey instanceKey, SiteKey siteKey )
    {

        List<PreferenceScope> scopes = new ArrayList<PreferenceScope>();

        if ( instanceKey.getMenuItemKey() != null && instanceKey.getPortletKey() != null )
        {
            scopes.add( resolveScope( PreferenceScopeType.WINDOW, instanceKey, siteKey ) );
        }
        if ( instanceKey.getPortletKey() != null )
        {
            scopes.add( resolveScope( PreferenceScopeType.PORTLET, instanceKey, siteKey ) );
        }
        if ( instanceKey.getMenuItemKey() != null )
        {
            scopes.add( resolveScope( PreferenceScopeType.PAGE, instanceKey, siteKey ) );
        }
        if ( siteKey != null )
        {
            scopes.add( resolveScope( PreferenceScopeType.SITE, instanceKey, siteKey ) );
        }
        scopes.add( resolveScope( PreferenceScopeType.GLOBAL, instanceKey, siteKey ) );

        return scopes;
    }

    public static com.enonic.cms.api.client.model.preference.PreferenceScope resolveClientScope( PreferenceScope scope )
    {

        com.enonic.cms.api.client.model.preference.PreferenceScope clientScope = null;
        switch ( scope.getType() )
        {
            case GLOBAL:
                clientScope = com.enonic.cms.api.client.model.preference.PreferenceScope.createGlobal();
                break;
            case PAGE:
                clientScope = com.enonic.cms.api.client.model.preference.PreferenceScope.createPage( scope.getKey().getFirstKey() );
                break;
            case PORTLET:
                clientScope = com.enonic.cms.api.client.model.preference.PreferenceScope.createPortlet( scope.getKey().getFirstKey() );
                break;
            case SITE:
                clientScope = com.enonic.cms.api.client.model.preference.PreferenceScope.createSite( scope.getKey().getFirstKey() );
                break;
            case WINDOW:
                clientScope = com.enonic.cms.api.client.model.preference.PreferenceScope.createWindow( scope.getKey().getFirstKey(),
                                                                                                       scope.getKey().getSecondKey() );
                break;
        }
        return clientScope;
    }
}

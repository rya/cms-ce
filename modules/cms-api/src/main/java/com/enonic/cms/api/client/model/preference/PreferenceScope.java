/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.preference;

import java.io.Serializable;

public class PreferenceScope
    implements Serializable
{
    private static final long serialVersionUID = -2725021446120281144L;

    private PreferenceScopeType type;

    private String key;

    private PreferenceScope()
    {
    }

    public PreferenceScopeType getType()
    {
        return type;
    }

    public String getKey()
    {
        return key;
    }

    public static PreferenceScope createGlobal()
    {
        PreferenceScope scope = new PreferenceScope();
        scope.type = PreferenceScopeType.GLOBAL;
        return scope;
    }

    public static PreferenceScope createSite( int siteKey )
    {
        PreferenceScope scope = new PreferenceScope();
        scope.key = String.valueOf( siteKey );
        scope.type = PreferenceScopeType.SITE;
        return scope;
    }

    public static PreferenceScope createPage( int pageKey )
    {
        PreferenceScope scope = new PreferenceScope();
        scope.key = String.valueOf( pageKey );
        scope.type = PreferenceScopeType.PAGE;
        return scope;
    }

    public static PreferenceScope createPortlet( int portletKey )
    {
        PreferenceScope scope = new PreferenceScope();
        scope.key = String.valueOf( portletKey );
        scope.type = PreferenceScopeType.PORTLET;
        return scope;
    }

    public static PreferenceScope createWindow( int pageKey, int portletKey )
    {
        PreferenceScope scope = new PreferenceScope();
        scope.key = String.valueOf( pageKey ) + ":" + String.valueOf( portletKey );
        scope.type = PreferenceScopeType.WINDOW;
        return scope;
    }
}

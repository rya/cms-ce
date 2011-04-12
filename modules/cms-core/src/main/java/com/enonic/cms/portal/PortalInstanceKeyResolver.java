/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.util.StringTokenizer;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.structure.portlet.PortletKey;

import static com.enonic.cms.core.preferences.PreferenceScopeType.PAGE;
import static com.enonic.cms.core.preferences.PreferenceScopeType.SITE;
import static com.enonic.cms.core.preferences.PreferenceScopeType.WINDOW;

public class PortalInstanceKeyResolver
{

    public PortalInstanceKey resolvePortalInstanceKey( String instanceKey )
    {
        if ( ( instanceKey == null ) || ( instanceKey.equals( "" ) ) )
        {
            throw new IllegalArgumentException( "No instanceKey provided, input is empty." );
        }

        StringTokenizer tokenizer = new StringTokenizer( instanceKey, ":" );
        String context = tokenizer.nextToken();
        if ( context.equals( WINDOW.getName() ) )
        {
            if ( tokenizer.countTokens() != 2 )
            {
                throw new IllegalArgumentException( "WINDOW instance key has wrong number of keys: " + tokenizer.countTokens() );
            }
            MenuItemKey menuItemKey = new MenuItemKey( tokenizer.nextToken() );
            PortletKey portletKey = new PortletKey( tokenizer.nextToken() );
            return PortalInstanceKey.createWindow( menuItemKey, portletKey );
        }
        else if ( context.equals( PAGE.getName() ) )
        {
            if ( tokenizer.countTokens() != 1 )
            {
                throw new IllegalArgumentException( "PAGE instance key has wrong number of keys: " + tokenizer.countTokens() );
            }
            MenuItemKey menuItemKey = new MenuItemKey( tokenizer.nextToken() );
            return PortalInstanceKey.createPage( menuItemKey );
        }
        else if ( context.equals( SITE.getName() ) )
        {
            if ( tokenizer.countTokens() != 1 )
            {
                throw new IllegalArgumentException( "SITE instance key has wrong number of keys: " + tokenizer.countTokens() );
            }
            SiteKey siteKey = new SiteKey( tokenizer.nextToken() );
            return PortalInstanceKey.createSite( siteKey );
        }
        else
        {
            throw new IllegalArgumentException( "No valid instance key context in key: " + instanceKey );
        }
    }

}

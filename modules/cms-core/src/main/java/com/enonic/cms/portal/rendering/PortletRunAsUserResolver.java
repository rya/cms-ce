/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.portlet.PortletEntity;

public class PortletRunAsUserResolver
{

    public static UserEntity resolveRunAsUser( PortletEntity portlet, UserEntity currentUser, MenuItemEntity menuItem )
    {
        if ( currentUser.isAnonymous() )
        {
            // Anonymous user cannot run as any other user
            return currentUser;
        }

        RunAsType runAs = portlet.getRunAs();

        if ( runAs.equals( RunAsType.PERSONALIZED ) )
        {
            return currentUser;
        }
        else if ( runAs.equals( RunAsType.DEFAULT_USER ) )
        {
            if ( portlet.getSite().resolveDefaultRunAsUser() != null )
            {
                return portlet.getSite().resolveDefaultRunAsUser();
            }
            return null;
        }
        else if ( runAs.equals( RunAsType.INHERIT ) )
        {
            return inherit( currentUser, menuItem );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported runAs: " + runAs );
        }
    }

    private static UserEntity inherit( UserEntity current, MenuItemEntity menuItem )
    {
        if ( menuItem != null )
        {
            return menuItem.resolveRunAsUser( current, false );
        }
        else
        {
            throw new IllegalStateException( "Expected to render portlet in context of either a menuitem or a page template" );
        }
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.portal.PageRequestType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/20/10
 * Time: 9:49 AM
 */
public class ResourceNameResolver
{

    public static String resolveDisplayName( PageRequestType pageRequestType, ContentEntity contentFromRequest, MenuItemEntity menuItem )
    {
        String displayName = null;

        if ( PageRequestType.CONTENT.equals( pageRequestType ) )
        {
            displayName = contentFromRequest.getMainVersion().getTitle();
        }
        else if ( PageRequestType.MENUITEM.equals( pageRequestType ) )
        {
            if ( menuItem != null )
            {
                displayName = menuItem.getDisplayName();
            }
        }

        if ( displayName == null )
        {
            displayName = "";
        }

        return displayName;
    }


    public static String resolveName( PageRequestType pageRequestType, ContentEntity contentFromRequest, MenuItemEntity menuItem )
    {
        if ( PageRequestType.CONTENT.equals( pageRequestType ) )
        {
            return contentFromRequest.getName();
        }
        else if ( menuItem != null )
        {
            return menuItem.getName();
        }

        return "";
    }

    public static String resolveMenuName( PageRequestType pageRequestType, MenuItemEntity menuItem )
    {
        if ( PageRequestType.CONTENT.equals( pageRequestType ) )
        {
            return "";
        }

        return menuItem.getMenuName();
    }


}

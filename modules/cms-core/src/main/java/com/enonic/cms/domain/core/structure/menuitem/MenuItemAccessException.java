/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.core.structure.menuitem;

import com.enonic.cms.domain.security.user.QualifiedUsername;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessType;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

public class MenuItemAccessException
    extends RuntimeException
{
    public MenuItemAccessException( String message, QualifiedUsername user, MenuItemAccessType menuItemAccesType, MenuItemKey menuItemKey )
    {
        super( buildMessage( message, user, menuItemAccesType, menuItemKey ) );
    }

    private static String buildMessage( String message, QualifiedUsername user, MenuItemAccessType menuItemAccesType,
                                        MenuItemKey menuItemKey )
    {
        StringBuffer msg = new StringBuffer();
        msg.append( message );
        msg.append( " User " ).append( user ).append( " do not have " ).append( menuItemAccesType );
        msg.append( " access on menu item " ).append( menuItemKey );
        return msg.toString();
    }
}

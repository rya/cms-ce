/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Comparator;
import java.util.List;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;


public class ContentLocationComparatorOnPath
    implements Comparator<ContentLocation>
{
    public int compare( final ContentLocation a, final ContentLocation b )
    {
        final int siteNameCompare = a.getSiteName().compareTo( b.getSiteName() );
        if ( siteNameCompare != 0 )
        {
            return siteNameCompare;
        }

        final MenuItemEntity menuitemA = a.getMenuItem();
        final MenuItemEntity menuitemB = b.getMenuItem();

        final int menuitemALevel = menuitemA.getLevel();
        final int menuitemBLevel = menuitemB.getLevel();
        if ( menuitemALevel < menuitemBLevel )
        {
            return -1;
        }
        else if ( menuitemALevel > menuitemBLevel )
        {
            return 1;
        }

        // at this point save levels...compare each level
        List<MenuItemEntity> menuitemAPath = menuitemA.getMenuItemPath();
        List<MenuItemEntity> menuitemBPath = menuitemB.getMenuItemPath();

        for ( int i = 0; i < menuitemAPath.size(); i++ )
        {
            final MenuItemEntity currentALevel = menuitemAPath.get( i );
            final MenuItemEntity currentBLevel = menuitemBPath.get( i );
            final int currentLevelCompare = currentALevel.getName().compareTo( currentBLevel.getName() );
            if ( currentLevelCompare != 0 )
            {
                return currentLevelCompare;
            }
        }

        // at this point they are completely equal, according to this comparator
        return 0;
    }
}

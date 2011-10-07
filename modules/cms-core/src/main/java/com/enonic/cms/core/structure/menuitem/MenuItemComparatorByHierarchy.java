/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.util.Comparator;

/**
 * Jul 24, 2009
 */
public class MenuItemComparatorByHierarchy
    implements Comparator<MenuItemEntity>
{
    public int compare( final MenuItemEntity a, final MenuItemEntity b )
    {
        // return -1 if a is before than b
        // return 0 if a has equal order to b
        // return +1 if a is after b

        final int levelOfA = a.getLevel();
        final int levelOfB = b.getLevel();
        if ( levelOfA < levelOfB )
        {
            return -1;
        }
        else if ( levelOfA > levelOfB )
        {
            return +1;
        }

        // at this point a and be are at the same level in the menu hierarchy

        final int orderOfA = a.getOrder();
        final int orderOfB = b.getOrder();
        if ( orderOfA < orderOfB )
        {
            return -1;
        }
        else if ( orderOfA > orderOfB )
        {
            return +1;
        }

        // NOTE: This has to be done to prevent trouble with equals when menuItems have same order but different keys
        return ( a.getKey() < b.getKey() ? -1 : ( a.getKey() == b.getKey() ? 0 : 1 ) );
    }


}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.io.Serializable;

import com.enonic.vertical.engine.criteria.Criteria;
import com.enonic.vertical.engine.criteria.MenuCriteria;
import com.enonic.vertical.engine.criteria.MenuItemCriteria;

/**
 * User: jvs Date: 21.mai.2003 Time: 14:26:52
 */
public class MenuGetterSettings
    implements Serializable
{
    private final static long serialVersionUID = 4539984739845L;

    private int[] menuKeys;

    private boolean onlyRootMenuItems = false;

    private boolean includeSections = false;

    private Integer onlyChildrenFromThisMenuItemKey = null;

    private Integer tagParentsOfThisMenuItemKey = null;

    private String tagName = null;

    private String tagValue = null;

    private MenuCriteria menuCriteria = new MenuCriteria( Criteria.NONE );

    private MenuItemCriteria menuItemCriteria = new MenuItemCriteria( Criteria.NONE );

    public void setTagParentsOfThisMenuItem( int menuItemKey, String tagName, String tagValue )
    {
        tagParentsOfThisMenuItemKey = new Integer( menuItemKey );
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    public boolean hasTagParentsOfThisMenuItem()
    {
        return ( tagParentsOfThisMenuItemKey != null && tagParentsOfThisMenuItemKey.intValue() != -1 ? true : false );
    }

    public Integer getTagParentsOfThisMenuItemKeyAsInteger()
    {
        return tagParentsOfThisMenuItemKey;
    }

    public String getTagParentsOfThisMenuItemTagName()
    {
        return tagName;
    }

    public String getTagParentsOfThisMenuItemTagValue()
    {
        return tagValue;
    }

    public void setMenuKey( int value )
    {
        menuKeys = new int[]{value};
    }

    public boolean hasMenuKeys()
    {
        return ( menuKeys != null && menuKeys.length > 0 );
    }

    public int[] getMenuKeys()
    {
        return menuKeys;
    }

    public void setMenuKeys( int[] keys )
    {
        menuKeys = keys;
    }

    public void setOnlyChildrenFromThisMenuItemKey( int value )
    {
        onlyChildrenFromThisMenuItemKey = new Integer( value );
    }

    public boolean hasOnlyChildrenFromThisMenuItemKey()
    {
        return ( onlyChildrenFromThisMenuItemKey != null ? true : false );
    }

    public Integer getMenuItemKeyAsInteger()
    {
        return onlyChildrenFromThisMenuItemKey;
    }

    public void setOnlyRootMenuItems( boolean value )
    {
        onlyRootMenuItems = value;
    }

    public boolean getOnlyRootMenuItems()
    {
        return onlyRootMenuItems;
    }

    public void setMenuCriteria( MenuCriteria value )
    {
        menuCriteria = value;
    }

    public MenuCriteria getMenuCriteria()
    {
        return menuCriteria;
    }

    public void setMenuItemCriteria( MenuItemCriteria value )
    {
        menuItemCriteria = value;
    }

    public MenuItemCriteria getMenuItemCriteria()
    {
        return menuItemCriteria;
    }

    public void setIncludeSections( boolean value )
    {
        includeSections = value;
    }

}

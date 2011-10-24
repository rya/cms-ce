/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.io.Serializable;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

public class SectionCriteria
    implements Serializable
{
    private final static long serialVersionUID = 1234759384537945L;

    private SiteKey[] siteKeys;

    private MenuItemKey[] menuItemKeys;

    private int sectionKey = -1;

    private int contentKey = -1;

    private int contentKeyExcludeFilter = -1;

    private int contentTypeKeyFilter = -1;

    private boolean includeSectionsWithoutContentTypeEvenWhenFilterIsSet = true;

    private boolean treeStructure;

    private boolean includeChildCount;

    private boolean markContentFilteredSections;

    private boolean includeAll;

    private boolean addAccessRights;

    private boolean includeSectionContentTypesInfo = true;

    public boolean appendAccessRights()
    {
        return addAccessRights;
    }

    public void setAppendAccessRights( boolean val )
    {
        addAccessRights = val;
    }

    public void setSiteKeys( int[] keys )
    {
        siteKeys = new SiteKey[keys.length];
        for ( int i = 0; i < keys.length; i++ )
        {
            siteKeys[i] = new SiteKey( keys[i] );
        }
    }

    public SiteKey[] getSiteKeys()
    {
        return siteKeys;
    }

    public void setMenuItemKeys( int[] keys )
    {
        menuItemKeys = new MenuItemKey[keys.length];
        for ( int i = 0; i < keys.length; i++ )
        {
            menuItemKeys[i] = new MenuItemKey( keys[i] );
        }
    }

    public MenuItemKey[] getMenuItemKeys()
    {
        return menuItemKeys;
    }

    public void setSectionKey( int key )
    {
        sectionKey = key;
    }

    public int getSectionKey()
    {
        return sectionKey;
    }

    public void setIncludeChildCount( boolean includeChildCount )
    {
        this.includeChildCount = includeChildCount;
    }

    public boolean getIncludeChildCount()
    {
        return includeChildCount;
    }

    public boolean isTreeStructure()
    {
        return treeStructure;
    }

    public void setTreeStructure( boolean b )
    {
        treeStructure = b;
    }

    public int getContentKeyExcludeFilter()
    {
        return contentKeyExcludeFilter;
    }

    public void setContentKeyExcludeFilter( int i )
    {
        contentKeyExcludeFilter = i;
    }

    public boolean isMarkContentFilteredSections()
    {
        return markContentFilteredSections;
    }

    public void setMarkContentFilteredSections( boolean markContentFilteredSections )
    {
        this.markContentFilteredSections = markContentFilteredSections;
    }

    public int getContentTypeKeyFilter()
    {
        return contentTypeKeyFilter;
    }

    public void setContentTypeKeyFilter( int i )
    {
        contentTypeKeyFilter = i;
    }

    public void setContentKey( final int value )
    {
        contentKey = value;
    }

    public int getContentKey()
    {
        return contentKey;
    }

    public boolean isIncludeAll()
    {
        return includeAll;
    }

    public void setIncludeAll( boolean b )
    {
        includeAll = b;
    }

    public boolean isIncludeSectionContentTypesInfo()
    {
        return includeSectionContentTypesInfo;
    }

    public void setIncludeSectionContentTypesInfo( boolean value )
    {
        this.includeSectionContentTypesInfo = value;
    }

    public boolean isIncludeSectionsWithoutContentTypeEvenWhenFilterIsSet()
    {
        return includeSectionsWithoutContentTypeEvenWhenFilterIsSet;
    }

    public void setIncludeSectionsWithoutContentTypeEvenWhenFilterIsSet( boolean value )
    {
        this.includeSectionsWithoutContentTypeEvenWhenFilterIsSet = value;
    }
}

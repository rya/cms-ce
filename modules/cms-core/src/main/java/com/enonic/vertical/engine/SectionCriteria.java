/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.io.Serializable;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

public class SectionCriteria
    implements Serializable
{
    private final static long serialVersionUID = 1234759384537945L;

    private SiteKey[] siteKeys;

    private MenuItemKey[] menuItemKeys;

    private int sectionKey = -1;

    private int superSectionKey = -1;

    private boolean includeSection;

    private int level;

    private int[] sectionKeys;

    private int contentKey = -1;

    private int contentKeyExcludeFilter = -1;

    private int contentTypeKeyFilter = -1;

    private boolean includeSectionsWithoutContentTypeEvenWhenFilterIsSet = true;

    private boolean publishRight;

    private boolean approveRight;

    private boolean adminRight;

    private boolean treeStructure;

    private boolean includeChildCount;

    private boolean recursivly;

    private boolean markContentFilteredSections;

    private boolean includeAll;

    private boolean addAccessRights;

    private boolean includeSectionContentTypesInfo = true;

    public String toString()
    {
        StringBuffer sql = new StringBuffer();
        sql.append( "[siteKeys=" );
        if ( siteKeys != null )
        {
            sql.append( '[' );
            for ( int i = 0; i < siteKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    sql.append( ',' );
                }
                sql.append( siteKeys[i] );
            }
            sql.append( ']' );
        }
        else
        {
            sql.append( "null" );
        }
        sql.append( ",menuItemKeys=" );
        if ( menuItemKeys != null )
        {
            sql.append( '[' );
            for ( int i = 0; i < menuItemKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    sql.append( ',' );
                }
                sql.append( menuItemKeys[i] );
            }
            sql.append( ']' );
        }
        else
        {
            sql.append( "null" );
        }
        sql.append( ",sectionKey=" );
        sql.append( sectionKey );
        sql.append( ",superSectionKey=" );
        sql.append( superSectionKey );
        sql.append( ",includeSection=" );
        sql.append( includeSection );
        sql.append( ",level=" );
        sql.append( level );
        sql.append( ",sectionKeys=" );
        if ( sectionKeys != null )
        {
            sql.append( '[' );
            for ( int i = 0; i < sectionKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    sql.append( ',' );
                }
                sql.append( sectionKeys[i] );
            }
            sql.append( ']' );
        }
        else
        {
            sql.append( "null" );
        }
        sql.append( ",contentKey=" );
        sql.append( contentKey );
        sql.append( ",contentKeyExcludeFilter=" );
        sql.append( contentKeyExcludeFilter );
        sql.append( ",contentTypeKeyFilter=" );
        sql.append( contentTypeKeyFilter );
        sql.append( ",publishRight=" );
        sql.append( publishRight );
        sql.append( ",approveRight=" );
        sql.append( approveRight );
        sql.append( ",adminRight=" );
        sql.append( adminRight );
        sql.append( ",treeStructure=" );
        sql.append( treeStructure );
        sql.append( ",includeChildCount=" );
        sql.append( includeChildCount );
        sql.append( ",recursivly=" );
        sql.append( recursivly );
        sql.append( ",markContentFilteredSections=" );
        sql.append( markContentFilteredSections );
        sql.append( ",includeAll=" );
        sql.append( includeAll );
        sql.append( ",addAccessRights=" );
        sql.append( addAccessRights );
        sql.append( ']' );
        return sql.toString();
    }

    public boolean appendAccessRights()
    {
        return addAccessRights;
    }

    public void setSiteKey( SiteKey key )
    {
        siteKeys = new SiteKey[]{key};
    }

    public SiteKey[] getSiteKeys()
    {
        return siteKeys;
    }

    public MenuItemKey[] getMenuItemKeys()
    {
        return menuItemKeys;
    }

    public boolean getSectionsRecursivly()
    {
        return recursivly;
    }

    public void setSectionKey( int key )
    {
        sectionKey = key;
    }

    public int getSectionKey()
    {
        return sectionKey;
    }


    public boolean isAdminRight()
    {
        return adminRight;
    }

    public boolean isApproveRight()
    {
        return approveRight;
    }

    public boolean isPublishRight()
    {
        return publishRight;
    }

    /**
     * Set to true if sections should be retrieved recursivly.
     *
     * @param recursivly
     */
    public void setSectionRecursivly( boolean recursivly )
    {
        this.recursivly = recursivly;
    }

    public boolean getIncludeChildCount()
    {
        return includeChildCount;
    }

    public int getSuperSectionKey()
    {
        return superSectionKey;
    }

    public void setSuperSectionKey( int i )
    {
        superSectionKey = i;
    }

    public void setSectionKeys( int[] keys )
    {
        if ( keys != null && keys.length > 0 )
        {
            int[] newArray = new int[keys.length];
            System.arraycopy( keys, 0, newArray, 0, keys.length );
            this.sectionKeys = newArray;
        }
        else
        {
            this.sectionKeys = null;
        }
    }

    public int[] getSectionKeys()
    {
        if ( sectionKeys != null )
        {
            int[] newArray = new int[sectionKeys.length];
            System.arraycopy( sectionKeys, 0, newArray, 0, sectionKeys.length );
            return newArray;
        }
        else
        {
            return null;
        }
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

    public boolean isMarkContentFilteredSections()
    {
        return markContentFilteredSections;
    }

    public int getContentTypeKeyFilter()
    {
        return contentTypeKeyFilter;
    }

    public int getContentKey()
    {
        return contentKey;
    }

    public boolean isIncludeAll()
    {
        return includeAll;
    }

    public boolean isIncludeSection()
    {
        return includeSection;
    }

    public int getLevel()
    {
        return level;
    }

    public void setIncludeSection( boolean b )
    {
        includeSection = b;
    }

    public void setLevel( int i )
    {
        level = i;
    }

    public boolean isIncludeSectionContentTypesInfo()
    {
        return includeSectionContentTypesInfo;
    }

    public boolean isIncludeSectionsWithoutContentTypeEvenWhenFilterIsSet()
    {
        return includeSectionsWithoutContentTypeEvenWhenFilterIsSet;
    }

}

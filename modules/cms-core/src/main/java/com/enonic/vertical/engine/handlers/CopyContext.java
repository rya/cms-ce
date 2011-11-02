/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import com.enonic.cms.core.security.user.User;
import com.google.common.collect.Maps;

import java.util.Map;

public class CopyContext
{
    private User user;

    private boolean includeContents;

    private final Map<Integer, Integer> menuKeyMap = Maps.newHashMap();

    private final Map<Integer, Integer> menuItemKeyMap = Maps.newHashMap();

    private final Map<Integer, Integer> pageTemplateKeyMap = Maps.newHashMap();

    private final Map<Integer, Integer> pageTemplateParameterKeyMap = Maps.newHashMap();

    private final Map<Integer, Integer> contentObjectKeyMap = Maps.newHashMap();

    private final Map<Integer, Integer> sectionKeyMap = Maps.newHashMap();

    private final Map<Integer, Integer> categoryKeyMap = Maps.newHashMap();

    public boolean isIncludeContents()
    {
        return includeContents;
    }

    public void setIncludeContents( boolean includeContents )
    {
        this.includeContents = includeContents;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

    public void putMenuKey( int oldMenuKey, int newMenuKey )
    {
        menuKeyMap.put( oldMenuKey, newMenuKey );
    }

    public int getMenuKey( int oldMenuKey )
    {
        if ( menuKeyMap.containsKey( oldMenuKey ) )
        {
            return menuKeyMap.get( oldMenuKey );
        }
        else
        {
            return -1;
        }
    }

    public void putMenuItemKey( int oldMenuItemKey, int newMenuItemKey )
    {
        menuItemKeyMap.put( oldMenuItemKey, newMenuItemKey );
    }

    public int getMenuItemKey( int oldMenuItemKey )
    {
        if ( menuItemKeyMap.containsKey( oldMenuItemKey ) )
        {
            return menuItemKeyMap.get( oldMenuItemKey );
        }
        else
        {
            return -1;
        }
    }

    public void putCategoryKey( int oldCategoryKey, int newCategoryKey )
    {
        categoryKeyMap.put( oldCategoryKey, newCategoryKey );
    }

    public int getCategoryKey( int oldCategoryKey )
    {
        if ( categoryKeyMap.containsKey( oldCategoryKey ) )
        {
            return categoryKeyMap.get( oldCategoryKey );
        }
        else
        {
            return -1;
        }
    }

    public void putPageTemplateKey( int oldPageTemplateKey, int newPageTemplateKey )
    {
        pageTemplateKeyMap.put( oldPageTemplateKey, newPageTemplateKey );
    }

    public int getPageTemplateKey( int oldPageTemplateKey )
    {
        if ( pageTemplateKeyMap.containsKey( oldPageTemplateKey ) )
        {
            return pageTemplateKeyMap.get( oldPageTemplateKey );
        }
        else
        {
            return -1;
        }
    }

    public void putPageTemplateParameterKey( int oldPageTemplateParameterKey, int newPageTemplateParameterKey )
    {
        pageTemplateParameterKeyMap.put( oldPageTemplateParameterKey, newPageTemplateParameterKey );
    }

    public int getPageTemplateParameterKey( int oldPageTemplateParameterKey )
    {
        if ( pageTemplateParameterKeyMap.containsKey( oldPageTemplateParameterKey ) )
        {
            return pageTemplateParameterKeyMap.get( oldPageTemplateParameterKey );
        }
        else
        {
            return -1;
        }
    }

    public void putContentObjectKey( int oldContentObjectKey, int newContentObjectKey )
    {
        contentObjectKeyMap.put( oldContentObjectKey, newContentObjectKey );
    }

    public int getContentObjectKey( int oldContentObjectKey )
    {
        if ( contentObjectKeyMap.containsKey( oldContentObjectKey ) )
        {
            return contentObjectKeyMap.get( oldContentObjectKey );
        }
        else
        {
            return -1;
        }
    }

    public void putSectionKey( int oldSectionKey, int newSectionKey )
    {
        sectionKeyMap.put( oldSectionKey, newSectionKey );
    }

}

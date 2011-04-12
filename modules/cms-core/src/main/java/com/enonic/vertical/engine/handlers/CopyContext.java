/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import com.enonic.esl.util.StringUtil;
import com.enonic.vertical.VerticalRuntimeException;

import com.enonic.cms.framework.util.TIntIntHashMap;

import com.enonic.cms.core.security.user.User;

public class CopyContext
{
    private User user;

    private boolean includeContents;

    private TIntIntHashMap menuKeyMap = new TIntIntHashMap();

    private TIntIntHashMap menuItemKeyMap = new TIntIntHashMap();

    private TIntIntHashMap pageTemplateKeyMap = new TIntIntHashMap();

    private TIntIntHashMap pageTemplateParameterKeyMap = new TIntIntHashMap();

    private TIntIntHashMap contentObjectKeyMap = new TIntIntHashMap();

    private TIntIntHashMap sectionKeyMap = new TIntIntHashMap();

    private TIntIntHashMap unitKeyMap = new TIntIntHashMap();

    private TIntIntHashMap contentTypeKeyMap = new TIntIntHashMap();

    private TIntIntHashMap categoryKeyMap = new TIntIntHashMap();

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

    public void put( String elementName, int oldKey, int newKey )
    {
        if ( elementName.equals( "menu" ) )
        {
            menuKeyMap.put( oldKey, newKey );
        }
        else if ( elementName.equals( "menuitem" ) )
        {
            menuItemKeyMap.put( oldKey, newKey );
        }
        else if ( elementName.equals( "pagetemplate" ) )
        {
            pageTemplateKeyMap.put( oldKey, newKey );
        }
        else if ( elementName.equals( "pagetemplateparameter" ) )
        {
            pageTemplateParameterKeyMap.put( oldKey, newKey );
        }
        else if ( elementName.equals( "contentobject" ) )
        {
            contentObjectKeyMap.put( oldKey, newKey );
        }
        else if ( elementName.equals( "section" ) )
        {
            sectionKeyMap.put( oldKey, newKey );
        }
        else if ( elementName.equals( "unit" ) )
        {
            unitKeyMap.put( oldKey, newKey );
        }
        else if ( elementName.equals( "contenttype" ) )
        {
            contentTypeKeyMap.put( oldKey, newKey );
        }
        else if ( elementName.equals( "category" ) )
        {
            categoryKeyMap.put( oldKey, newKey );
        }
        else
        {
            String msg = "Element not defined for copy context: %0";

            VerticalRuntimeException.error( this.getClass(), VerticalRuntimeException.class,
                                            StringUtil.expandString( msg, elementName, null ) );
        }
    }

    public void put( String elementName, String oldKey, String newKey )
    {
        // NOTE! At the moment, none of the tables using a string key is supported by the copy context

        String msg = "Element not defined for copy context: %0";

        VerticalRuntimeException.error( this.getClass(), VerticalRuntimeException.class,
                                        StringUtil.expandString( msg, elementName, null ) );
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

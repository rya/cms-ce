/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.category.access.CategoryAccessRights;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Mar 9, 2010
 */
public class StoreNewCategoryCommand
{
    private UserKey creator;

    private ContentTypeKey contentType;

    private UnitKey unitKey;

    private CategoryKey parentCategory;

    private String name;

    private boolean autoApprove = false;

    private List<CategoryAccessRights> accessRights = null;

    private String description;

    public void setCreator( UserKey creator )
    {
        this.creator = creator;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public void setContentType( ContentTypeKey contentType )
    {
        this.contentType = contentType;
    }

    public ContentTypeKey getContentType()
    {
        return contentType;
    }

    public UnitKey getUnitKey()
    {
        return unitKey;
    }

    public void setUnitKey( UnitKey unitKey )
    {
        this.unitKey = unitKey;
    }

    public void setParentCategory( CategoryKey parentCategory )
    {
        this.parentCategory = parentCategory;
    }

    public CategoryKey getParentCategory()
    {
        return parentCategory;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setAutoApprove( boolean autoApprove )
    {
        this.autoApprove = autoApprove;
    }

    public boolean getAutoApprove()
    {
        return autoApprove;
    }

    public void addAccessRight( CategoryAccessRights accessRight )
    {
        if ( accessRights == null )
        {
            accessRights = new ArrayList<CategoryAccessRights>();
        }
        accessRights.add( accessRight );
    }

    public List<CategoryAccessRights> getAccessRights()
    {
        return accessRights;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }
}

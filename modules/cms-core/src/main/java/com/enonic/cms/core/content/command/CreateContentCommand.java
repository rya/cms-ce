/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.command;

import org.springframework.util.Assert;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;

public class CreateContentCommand
    extends BaseContentCommand
{
    private UserKey creator;

    private CategoryKey category;

    private ContentStatus status;

    private ContentKey source;

    private AccessRightsStrategy accessRightsStrategy = AccessRightsStrategy.USE_GIVEN;

    private String contentName;

    public void populateCommandWithContentValues( ContentEntity content )
    {
        setContentName( content.getName() );
        setCategory( content.getCategory() );
        setAvailableFrom( content.getAvailableFrom() );
        setAvailableTo( content.getAvailableTo() );
        setPriority( content.getPriority() );
        setLanguage( content.getLanguage() );

        if ( content.getSource() != null )
        {
            setSource( content.getSource().getKey() );
        }

        addContentAccessRights( content.getContentAccessRights(), content );
    }

    public void populateCommandWithContentVersionValues( ContentVersionEntity contentVersion )
    {
        setContentData( contentVersion.getContentData() );
        setStatus( contentVersion.getStatus() );
        setChangeComment( contentVersion.getChangeComment() );
    }

    public void setCreator( UserKey value )
    {
        Assert.notNull( value );
        this.creator = value;
    }

    public void setCreator( UserEntity value )
    {
        Assert.notNull( value );
        this.creator = value.getKey();
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public CategoryKey getCategory()
    {
        return category;
    }

    public void setCategory( CategoryEntity value )
    {
        Assert.notNull( value );
        this.category = value.getKey();
    }

    public void setCategory( CategoryKey value )
    {
        Assert.notNull( value );
        this.category = value;
    }

    public void setAccessRightsStrategy( AccessRightsStrategy value )
    {
        this.accessRightsStrategy = value;
    }

    public AccessRightsStrategy getAccessRightsStrategy()
    {
        return accessRightsStrategy;
    }

    public enum AccessRightsStrategy
    {
        USE_GIVEN,
        INHERIT_FROM_CATEGORY
    }

    public ContentStatus getStatus()
    {
        return status;
    }

    public void setStatus( ContentStatus status )
    {
        this.status = status;
    }

    public ContentKey getSource()
    {
        return source;
    }

    public void setSource( ContentKey value )
    {
        this.source = value;
    }

    public String getContentName()
    {
        return contentName;
    }

    public void setContentName( String contentName )
    {
        this.contentName = contentName;
    }

}




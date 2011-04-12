/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;

public class CategoryEntity
    implements Serializable
{

    private CategoryKey key;

    private String name;

    private String description;

    private Date created;

    private Date timestamp;

    private int deleted;

    private UserEntity owner;

    private UserEntity modifier;

    private UnitEntity unit;

    private ContentTypeEntity contentType;

    private CategoryEntity parent;

    private List<CategoryEntity> children = new ArrayList<CategoryEntity>();

    private Map<GroupKey, CategoryAccessEntity> accessRights = null;

    private Set<ContentEntity> contents = null;

    private Integer autoMakeAvailable;

    public CategoryEntity()
    {
        // Default constructor used by Hibernate.
    }

    public CategoryEntity( CategoryEntity source )
    {
        this();

        this.key = source.getKey();
        this.name = source.getName();
        this.description = source.getDescription();
        this.created = source.getCreated();
        this.timestamp = source.getTimestamp();
        this.deleted = source.getDeleted();
        this.owner = source.getOwner();
        this.modifier = source.getModifier();
        this.unit = source.getUnit();
        this.contentType = source.getContentType();
        this.parent = source.getParent();
        this.children = source.getChildren() != null ? Lists.newArrayList( source.getChildren() ) : null;
        this.accessRights = source.getAccessRights() != null ? Maps.newHashMap( source.getAccessRights() ) : null;
        this.contents = source.getContents() != null ? Sets.newLinkedHashSet( source.getContents() ) : null;
        this.autoMakeAvailable = source.getAutoMakeAvailable();
    }

    public CategoryKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getCreated()
    {
        return created;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public int getDeleted()
    {
        return deleted;
    }

    public boolean isDeleted()
    {
        return deleted != 0 || unit.isDeleted();
    }

    public UserEntity getOwner()
    {
        return owner;
    }

    public UserEntity getModifier()
    {
        return modifier;
    }

    public UnitEntity getUnit()
    {
        return getUnit( true );
    }

    public UnitEntity getUnitExcludeDeleted()
    {
        return getUnit( false );
    }

    public UnitEntity getUnit( boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return unit;
        }
        else
        {
            return unit.isDeleted() ? null : unit;
        }
    }

    public ContentTypeEntity getContentType()
    {
        return contentType;
    }

    public CategoryEntity getParent()
    {
        return parent;
    }

    public Integer getAutoMakeAvailable()
    {
        return autoMakeAvailable;
    }

    public boolean getAutoMakeAvailableAsBoolean()
    {
        return autoMakeAvailable != null && autoMakeAvailable == 1;
    }

    public void setChildren( List<CategoryEntity> children )
    {
        this.children = children;
    }

    public void addChild( CategoryEntity child )
    {
        if ( children == null )
        {
            children = new ArrayList<CategoryEntity>();
        }
        children.add( child );
    }

    public boolean hasChildren()
    {
        return children.size() > 0;
    }

    public List<CategoryEntity> getChildren()
    {
        return children;
    }

    public List<CategoryKey> getChildrenKeys()
    {
        List<CategoryKey> childrenKeys = new ArrayList<CategoryKey>();
        for ( CategoryEntity descendant : children )
        {
            childrenKeys.add( descendant.getKey() );
        }
        return childrenKeys;
    }

    public Map<GroupKey, CategoryAccessEntity> getAccessRights()
    {
        return accessRights;
    }

    public void accumulateAccess( CategoryAccessRightsAccumulated accumulated, GroupEntity group )
    {
        CategoryAccessEntity access = accessRights.get( group.getGroupKey() );
        if ( access != null )
        {
            accumulated.accumulate( access );
        }
    }

    public boolean hasAccess( final GroupEntity group, final CategoryAccessType type )
    {
        if ( group == null )
        {
            throw new IllegalArgumentException( "Given group cannot be null" );
        }

        CategoryAccessEntity access = accessRights.get( group.getGroupKey() );
        if ( access == null )
        {
            return false;
        }

        switch ( type )
        {
            case READ:
                return access.givesRead();
            case ADMIN_BROWSE:
                return access.givesAdminBrowse();
            case CREATE:
                return access.givesCreate();
            case APPROVE:
                return access.givesApprove();
            case ADMINISTRATE:
                return access.givesAdministrate();
        }

        return false;
    }

    public boolean hasAccessRightSet( final GroupEntity group, final CategoryAccessType type )
    {
        if ( group == null )
        {
            throw new IllegalArgumentException( "Given group cannot be null" );
        }

        CategoryAccessEntity access = accessRights.get( group.getGroupKey() );
        if ( access == null )
        {
            return false;
        }

        switch ( type )
        {
            case READ:
                return access.isReadAccess();
            case ADMIN_BROWSE:
                return access.isAdminBrowseAccess();
            case CREATE:
                return access.isCreateAccess();
            case APPROVE:
                return access.isPublishAccess();
            case ADMINISTRATE:
                return access.isAdminAccess();
        }

        return false;
    }

    public Set<ContentEntity> getContents()
    {
        return contents;
    }

    public void setKey( CategoryKey key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setCreated( Date created )
    {
        this.created = created;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setDeleted( boolean deleted )
    {
        this.deleted = deleted ? 1 : 0;
    }

    public void setOwner( UserEntity owner )
    {
        this.owner = owner;
    }

    public void setModifier( UserEntity modifier )
    {
        this.modifier = modifier;
    }

    public void setUnit( UnitEntity unit )
    {
        this.unit = unit;
    }

    public void setContentType( ContentTypeEntity contentType )
    {
        this.contentType = contentType;
    }

    public void setParent( CategoryEntity parent )
    {
        this.parent = parent;
    }

    public void setAccessRights( Map<GroupKey, CategoryAccessEntity> accessRights )
    {
        this.accessRights = accessRights;
    }

    public void addAccessRight( CategoryAccessEntity accessRight )
    {
        if ( accessRights == null )
        {
            accessRights = new LinkedHashMap<GroupKey, CategoryAccessEntity>();
        }
        accessRights.put( accessRight.getKey().getGroupKey(), accessRight );
    }

    public void setContents( Set<ContentEntity> contents )
    {
        this.contents = contents;
    }

    public void setAutoMakeAvailable( boolean value )
    {
        this.autoMakeAvailable = value ? 1 : 0;
    }

    /**
     * @return All descendants of this category, recursively collected.
     */
    public List<CategoryEntity> getDescendants()
    {

        List<CategoryEntity> allDescendants = new ArrayList<CategoryEntity>();
        doCategoryDescendantsRecursively( this, allDescendants );
        return allDescendants;
    }

    private void doCategoryDescendantsRecursively( CategoryEntity parent, List<CategoryEntity> allDescendants )
    {

        Collection<CategoryEntity> children = parent.getChildren();
        if ( children == null )
        {
            return;
        }

        for ( CategoryEntity child : children )
        {
            allDescendants.add( child );
            doCategoryDescendantsRecursively( child, allDescendants );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof CategoryEntity ) )
        {
            return false;
        }

        CategoryEntity that = (CategoryEntity) o;

        if ( key != null ? !key.equals( that.getKey() ) : that.getKey() != null )
        {
            return false;
        }

        return true;
    }

    /**
     * @return the path (from archive top level) of this category.
     */
    public String getPathAsString()
    {

        List<CategoryEntity> categories = getCategoryPath();
        StringBuffer pathString = new StringBuffer( 25 * categories.size() );
        pathString.append( "/" );
        for ( int i = 0; i < categories.size(); i++ )
        {
            CategoryEntity mi = categories.get( i );
            pathString.append( mi.getName() );
            if ( i < categories.size() - 1 )
            {
                pathString.append( "/" );
            }
        }
        return pathString.toString();
    }

    /**
     * @return The breadcrumbspath of this menu item, with the top level parent as index 0.
     */
    public List<CategoryEntity> getCategoryPath()
    {

        List<CategoryEntity> path = new ArrayList<CategoryEntity>();
        addPath( path );
        return Collections.unmodifiableList( path );
    }

    private void addPath( List<CategoryEntity> path )
    {
        CategoryEntity parent = getParent();
        if ( parent != null )
        {
            parent.addPath( path );
        }
        path.add( this );
    }

    public LanguageEntity getLanguage()
    {
        return getUnitExcludeDeleted().getLanguage();
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 227, 231 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "key = " ).append( getKey() ).append( ", name = '" ).append( getName() ).append( "'" );
        return s.toString();
    }
}
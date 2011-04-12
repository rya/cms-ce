/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.enonic.cms.core.structure.menuitem.ContentHomeKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupKeyComparator;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemComparatorByHierarchy;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;

public class ContentEntity
    implements Serializable
{
    private ContentKey key;

    private Date createdAt;

    private Date timestamp;

    private Integer deleted;

    private String name;

    private Integer priority;

    private Date availableFrom;

    private Date availableTo;

    private UserEntity owner;

    private UserEntity assignee;

    private UserEntity assigner;

    private Date assignmentDueDate;

    private String assignmentDescription;

    private CategoryEntity category;

    private LanguageEntity language;

    private ContentEntity source;

    @SuppressWarnings({"JpaModelErrorInspection"})
    private ContentVersionEntity mainVersion;

    private ContentVersionEntity draftVersion;

    private SortedMap<Integer, ContentHomeEntity> contentHomes = new TreeMap<Integer, ContentHomeEntity>();

    private List<ContentVersionEntity> versions = new ArrayList<ContentVersionEntity>();

    private Set<ContentVersionEntity> relatedParents = new HashSet<ContentVersionEntity>();

    private Set<SectionContentEntity> sectionContents = new HashSet<SectionContentEntity>();

    private SortedSet<MenuItemEntity> directMenuItemPlacements = new TreeSet<MenuItemEntity>( new MenuItemComparatorByHierarchy() );


    public ContentEntity()
    {
        // Default constructor used by Hibernate.
    }

    /**
     * Constructor that creates a new instance as a copy of the given content.
     */
    public ContentEntity( ContentEntity source )
    {
        this();

        this.key = source.getKey();
        this.createdAt = source.getCreatedAt();
        this.timestamp = source.getTimestamp();
        this.deleted = source.getDeleted();
        this.name = source.getName();
        this.priority = source.getPriority();
        this.availableFrom = source.getAvailableFrom();
        this.availableTo = source.getAvailableTo();
        this.owner = source.getOwner();
        this.assignee = source.getAssignee();
        this.assigner = source.getAssigner();
        this.assignmentDueDate = source.getAssignmentDueDate();
        this.assignmentDescription = source.getAssignmentDescription();
        this.category = source.getCategory();
        this.language = source.getLanguage();
        this.source = source.getSource();
        this.mainVersion = source.getMainVersion();
        this.draftVersion = source.getDraftVersion();
        this.contentHomes = source.getContentHomesAsMap() != null ? Maps.newTreeMap( source.getContentHomesAsMap() ) : null;
        this.versions = source.getVersions() != null ? Lists.newArrayList( source.getVersions() ) : null;
        this.relatedParents =
            source.getRelatedParentContentVersions() != null ? Sets.newHashSet( source.getRelatedParentContentVersions() ) : null;
        this.sectionContents = source.getSectionContents() != null ? Sets.newHashSet( source.getSectionContents() ) : null;
        this.directMenuItemPlacements =
            source.getDirectMenuItemPlacements() != null ? new TreeSet<MenuItemEntity>( source.getDirectMenuItemPlacements() ) : null;
    }

    /**
     * Key is groupKey
     */
    private SortedMap<GroupKey, ContentAccessEntity> contentAccessRights =
        new TreeMap<GroupKey, ContentAccessEntity>( new GroupKeyComparator() );

    public ContentKey getKey()
    {
        return key;
    }

    public boolean isDeleted()
    {
        return deleted != null && deleted != 0;
    }

    public Integer getDeleted()
    {
        return deleted;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public UserEntity getOwner()
    {
        return owner;
    }

    public Date getAvailableFrom()
    {
        return availableFrom;
    }

    public Date getAvailableTo()
    {
        return availableTo;
    }

    public UserEntity getAssignee()
    {
        return assignee;
    }

    public UserEntity getAssigner()
    {
        return assigner;
    }

    public Date getAssignmentDueDate()
    {
        return assignmentDueDate;
    }

    public boolean isAssignmentOverdue()
    {
        if ( assignmentDueDate == null )
        {
            return false;
        }

        DateTime dueDate = new DateTime( assignmentDueDate );

        return dueDate.isBeforeNow();
    }

    public String getAssignmentDescription()
    {
        return assignmentDescription;
    }

    public CategoryEntity getCategory()
    {
        return category;
    }

    public ContentTypeEntity getContentType()
    {
        return category.getContentType();
    }

    public LanguageEntity getLanguage()
    {
        return language;
    }

    public ContentEntity getSource()
    {
        return source;
    }

    public ContentVersionEntity getMainVersion()
    {
        return mainVersion;
    }

    public boolean isAssigned()
    {
        return assignee != null;
    }

    public ContentVersionEntity getAssignedVersion()
    {
        if ( assignee == null )
        {
            return null;
        }

        if ( draftVersion != null )
        {
            return draftVersion;
        }

        return mainVersion;
    }

    public SortedMap<Integer, ContentHomeEntity> getContentHomesAsMap()
    {
        return contentHomes;
    }

    public Collection<ContentHomeEntity> getContentHomes()
    {
        return contentHomes.values();
    }

    public List<ContentVersionEntity> getVersions()
    {
        return versions;
    }

    public int getVersionCount()
    {
        return versions.size();
    }

    public void addVersion( ContentVersionEntity version )
    {
        version.setContent( this );
        versions.add( version );
    }

    public void removeVersion( ContentVersionEntity version )
    {
        versions.remove( version );
    }

    public Set<ContentVersionEntity> getRelatedParentContentVersions()
    {
        return relatedParents;
    }

    public List<ContentEntity> getRelatedParents( boolean includeDeleted )
    {
        List<ContentEntity> parents = new ArrayList<ContentEntity>();

        final Set<ContentKey> usedKeys = new HashSet<ContentKey>();
        for ( final ContentVersionEntity relContentVersion : getRelatedParentContentVersions() )
        {
            final ContentEntity relatedParentContent = relContentVersion.getContent();
            final ContentKey relatedKey = relatedParentContent.getKey();

            if ( !usedKeys.contains( relatedKey ) && ( includeDeleted || !relatedParentContent.isDeleted() ) )
            {
                usedKeys.add( relatedKey );
                parents.add( relatedParentContent );
            }
        }

        return parents;
    }

    public SortedSet<MenuItemEntity> getDirectMenuItemPlacements()
    {
        return directMenuItemPlacements;
    }

    public void setKey( ContentKey key )
    {
        this.key = key;
    }

    public void setDeleted( boolean deleted )
    {
        if ( deleted )
        {
            this.deleted = 1;
        }
        else
        {
            this.deleted = 0;
        }
    }

    public void setPriority( Integer priority )
    {
        this.priority = priority;
    }

    public void setCreatedAt( Date created )
    {
        this.createdAt = created;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public boolean setAvailableFrom( Date value )
    {
        if ( availableFrom == null && value == null )
        {
            return false;
        }

        long otherTime = value != null ? value.getTime() : -1;
        long thisTime = availableFrom != null ? availableFrom.getTime() : -1;

        if ( thisTime == otherTime )
        {
            return false;
        }

        this.availableFrom = value;

        return true;
    }

    public boolean setAvailableTo( Date value )
    {
        if ( availableTo == null && value == null )
        {
            return false;
        }

        long otherTime = value != null ? value.getTime() : -1;
        long thisTime = availableTo != null ? availableTo.getTime() : -1;

        if ( thisTime == otherTime )
        {
            return false;
        }

        this.availableTo = value;

        return true;
    }

    public void setAssigner( UserEntity assigner )
    {
        this.assigner = assigner;
    }

    public void setAssignee( UserEntity assignee )
    {
        this.assignee = assignee;
    }

    public boolean setAssignmentDueDate( Date value )
    {
        if ( assignmentDueDate == null && value == null )
        {
            return false;
        }

        long otherTime = value != null ? value.getTime() : -1;
        long thisTime = assignmentDueDate != null ? assignmentDueDate.getTime() : -1;

        if ( thisTime == otherTime )
        {
            return false;
        }

        this.assignmentDueDate = value;

        return true;
    }

    public void setAssignmentDescription( String assignmentDescription )
    {
        this.assignmentDescription = assignmentDescription;
    }


    public void setOwner( UserEntity owner )
    {
        this.owner = owner;
    }

    public void setCategory( CategoryEntity category )
    {
        this.category = category;
    }

    public void setLanguage( LanguageEntity language )
    {
        this.language = language;
    }

    public void setSource( ContentEntity source )
    {
        this.source = source;
    }

    public void setMainVersion( ContentVersionEntity value )
    {
        this.mainVersion = value;
    }

    public void addContentHome( ContentHomeEntity contentHome )
    {
        Assert.notNull( contentHome );
        Assert.notNull( contentHome.getSite() );

        final SiteKey siteKey = contentHome.getSite().getKey();

        contentHome.setKey( new ContentHomeKey( siteKey, this.getKey() ) );
        contentHome.setContent( this );
        contentHomes.put( siteKey.toInt(), contentHome );
    }

    public void addRelatedParent( ContentVersionEntity relatedParent )
    {
        this.relatedParents.add( relatedParent );
    }

    public void addSectionContent( SectionContentEntity sectionContent )
    {
        sectionContent.setContent( this );
        this.sectionContents.add( sectionContent );
    }

    public Set<SectionContentEntity> getSectionContents()
    {
        return sectionContents;
    }

    public void addContentAccessRights( Collection<ContentAccessEntity> values )
    {
        for ( ContentAccessEntity contentAccess : values )
        {
            contentAccess.setContent( this );
            contentAccessRights.put( contentAccess.getGroup().getGroupKey(), contentAccess );
        }
    }

    public void addContentAccessRight( ContentAccessEntity contentAccess )
    {
        contentAccess.setContent( this );
        contentAccessRights.put( contentAccess.getGroup().getGroupKey(), contentAccess );
    }

    public boolean removeContentAccessRightByGroup( GroupKey groupKey )
    {
        Object removedObject = contentAccessRights.remove( groupKey );
        return removedObject != null;
    }

    public void removeAllContentAccessRights()
    {
        contentAccessRights.clear();
    }

    public ContentAccessEntity getContentAccessRight( GroupKey groupKey )
    {
        return contentAccessRights.get( groupKey );
    }

    public Collection<ContentAccessEntity> getContentAccessRights()
    {
        return contentAccessRights.values();
    }

    public void setContentHomes( SortedMap<Integer, ContentHomeEntity> contentHomes )
    {
        this.contentHomes = contentHomes;
    }

    public void setSectionContents( Set<SectionContentEntity> sectionContents )
    {
        this.sectionContents = sectionContents;
    }

    public void setDirectMenuItemPlacements( SortedSet<MenuItemEntity> directMenuItemPlacements )
    {
        this.directMenuItemPlacements = directMenuItemPlacements;
    }

    public void addDirectMenuItemPlacement( MenuItemEntity menuItem )
    {
        directMenuItemPlacements.add( menuItem );
    }

    public boolean hasDirectMenuItemPlacements()
    {
        return !directMenuItemPlacements.isEmpty();
    }

    public boolean hasDraft()
    {
        return draftVersion != null;
    }

    public ContentVersionEntity getDraftVersion()
    {
        return draftVersion;
    }

    public void setDraftVersion( ContentVersionEntity draftVersion )
    {
        this.draftVersion = draftVersion;
    }

    public MenuItemEntity getFirstDirectPlacementOnMenuItem( SiteKey siteKey )
    {
        for ( MenuItemEntity menuItem : directMenuItemPlacements )
        {
            if ( menuItem.getSite().getKey().equals( siteKey ) )
            {
                return menuItem;
            }
        }
        return null;
    }

    public boolean isOnline( DateTime date )
    {
        return isOnline( date.toDate() );
    }

    public boolean isOnline( Date date )
    {
        return !isDeleted() && isAvailableByDateTime( date ) && getMainVersion().isApproved();
    }

    private boolean isAvailableByDateTime( Date date )
    {
        if ( availableFrom == null )
        {
            return false;
        }

        if ( availableFrom.after( date ) )
        {
            return false;
        }

        if ( availableTo != null )
        {
            if ( date.after( availableTo ) || date.equals( availableTo ) )
            {
                return false;
            }
        }

        return true;
    }

    public ContentHomeEntity getContentHome( SiteKey siteKey )
    {
        return contentHomes.get( siteKey.toInt() );
    }

    public boolean hasAccessRightSet( final GroupEntity group, final ContentAccessType type )
    {
        if ( group == null )
        {
            throw new IllegalArgumentException( "Given group cannot be null" );
        }

        ContentAccessEntity access = contentAccessRights.get( group.getGroupKey() );
        if ( access == null )
        {
            return false;
        }

        switch ( type )
        {
            case READ:
                return access.isReadAccess();
            case UPDATE:
                return access.isUpdateAccess();
            case DELETE:
                return access.isDeleteAccess();
            default:
                return false;
        }

    }

    public void accumulateAccess( ContentAccessRightsAccumulated accumulated, GroupEntity group )
    {
        ContentAccessEntity access = contentAccessRights.get( group.getGroupKey() );
        if ( access != null )
        {
            accumulated.accumulate( access );
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentEntity ) )
        {
            return false;
        }

        ContentEntity that = (ContentEntity) o;

        if ( key != null ? !key.equals( that.getKey() ) : that.getKey() != null )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 97;
        final int multiplierNonZeroOddNumber = 71;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "key = " ).append( key );
        return s.toString();
    }

    public boolean isActiviatedInSection( MenuItemEntity menuItem )
    {
        for ( SectionContentEntity sectionContent : getSectionContents() )
        {
            if ( menuItem.equals( sectionContent.getMenuItem() ) && sectionContent.isApproved() )
            {
                return true;
            }
        }

        return false;
    }

    public ContentLocations getLocations( final ContentLocationSpecification spec )
    {
        ContentLocations contentLocations = new ContentLocations( this );

        for ( MenuItemEntity menuItem : directMenuItemPlacements )
        {
            final boolean siteResrictionOK = spec.getSiteKey() == null || menuItem.getSite().getKey().equals( spec.getSiteKey() );

            if ( siteResrictionOK )
            {
                contentLocations.addDirectMenuItemLocation( menuItem );
            }
        }

        for ( SectionContentEntity sectionContent : sectionContents )
        {
            final MenuItemEntity menuItem = sectionContent.getMenuItem();
            final boolean siteRestrictionOK = spec.getSiteKey() == null || menuItem.getSite().getKey().equals( spec.getSiteKey() );
            final boolean activeInSectionRestrictionOK = sectionContent.isApproved() || spec.includeInactiveLocationsInSection();

            if ( siteRestrictionOK && activeInSectionRestrictionOK )
            {
                contentLocations.addSectionMenuItemLocation( sectionContent );
            }
        }

        for ( ContentHomeEntity contentHome : contentHomes.values() )
        {
            final MenuItemEntity contentHomeMenuItem = contentHome.getMenuItem();
            if ( contentHomeMenuItem == null )
            {
                continue;
            }
            final boolean siteRestrictionOK =
                spec.getSiteKey() == null || spec.getSiteKey().equals( contentHomeMenuItem.getSite().getKey() );

            if ( siteRestrictionOK )
            {
                contentLocations.addSectionHomeLocation( contentHome );
            }
        }
        contentLocations.resolveHomes();
        return contentLocations;
    }

    /**
     * @return the path (from archive top level) of this content.
     */
    public String getPathAsString()
    {
        String categoryPath = getCategory().getPathAsString();
        if ( categoryPath == null )
        {
            return null;
        }

        ContentVersionEntity currentVersion = getMainVersion();
        if ( null != currentVersion )
        {
            return categoryPath + "/" + currentVersion.getTitle();
        }
        return null;
    }

    public void addOwnerAccessRight()
    {
        if ( getOwner().isAnonymous() || getOwner().isRoot() )
        {
            return;
        }

        ContentAccessEntity contentAccess = new ContentAccessEntity();
        contentAccess.setGroup( getOwner().getUserGroup() );
        contentAccess.setReadAccess( true );
        contentAccess.setUpdateAccess( true );
        contentAccess.setDeleteAccess( true );
        addContentAccessRight( contentAccess );
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setTimestamp()
    {
        this.timestamp = Calendar.getInstance().getTime();
    }

    public SectionContentEntity removeSectionContent( MenuItemKey sectionKey )
    {
        SectionContentEntity sectionContentToRemove = null;
        if ( sectionContents != null && sectionContents.size() > 0 )
        {
            for ( SectionContentEntity sectionContent : sectionContents )
            {
                if ( sectionContent.getMenuItem().getKey() == sectionKey.toInt() )
                {
                    sectionContentToRemove = sectionContent;
                    break;
                }
            }
            if ( sectionContentToRemove == null )
            {
                return null;
            }
            else
            {
                sectionContents.remove( sectionContentToRemove );
                return sectionContentToRemove;
            }
        }
        return null;
    }
}

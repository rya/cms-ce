/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public class GroupEntity
    implements Serializable
{

    private GroupKey key;

    private String name;

    private String description;

    private Integer deleted;

    private Integer restricted;

    private String syncValue;

    private Integer type;

    private Set<UserEntity> user = new HashSet<UserEntity>();

    private UserStoreEntity userStore;

    private Set<GroupEntity> memberships = new LinkedHashSet<GroupEntity>();

    private Set<GroupEntity> members = new LinkedHashSet<GroupEntity>();

    private transient QualifiedGroupname qualifiedName;

    private transient Set<GroupEntity> allMemberships;

    private transient List<GroupKey> allMembershipsGroupKeys;

    public GroupKey getGroupKey()
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

    public boolean isDeleted()
    {
        return deleted != 0;
    }

    public boolean isRestricted()
    {
        return restricted != 0;
    }

    public String getSyncValue()
    {
        return syncValue;
    }

    public GroupType getType()
    {
        return GroupType.get( type );
    }

    public UserEntity getUser()
    {
        if ( user.size() == 1 )
        {
            return user.iterator().next();
        }
        else if ( user.size() > 1 )
        {
            throw new IllegalStateException( "Unexpected number of users with relation to this group: " + user.size() );
        }
        else
        {
            return null;
        }
    }

    public UserStoreEntity getUserStore()
    {
        return userStore;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStore != null ? userStore.getKey() : null;
    }

    public Set<GroupEntity> getMemberships( boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return memberships;
        }

        Set<GroupEntity> notDeletedMemberships = new LinkedHashSet<GroupEntity>();
        for ( GroupEntity membership : memberships )
        {
            if ( !membership.isDeleted() )
            {
                notDeletedMemberships.add( membership );
            }
        }
        return notDeletedMemberships;
    }

    public Set<GroupEntity> getMembers( boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return members;
        }

        Set<GroupEntity> notDeletedMembers = new LinkedHashSet<GroupEntity>();
        for ( GroupEntity member : members )
        {
            if ( !member.isDeleted() )
            {
                notDeletedMembers.add( member );
            }
        }
        return notDeletedMembers;
    }

    public void setKey( GroupKey value )
    {
        this.key = value;
    }

    public void setKey( String value )
    {
        this.key = new GroupKey( value );
    }

    public void setName( String value )
    {
        this.name = value;

        // invalidate
        qualifiedName = null;
    }

    public void setDescription( String value )
    {
        this.description = value;
    }

    public void setDeleted( int value )
    {
        this.deleted = value;
    }

    public void setDeleted( boolean value )
    {
        this.deleted = value ? 1 : 0;
    }

    public void setRestricted( int value )
    {
        this.restricted = value;
    }

    public void setRestricted( boolean value )
    {
        this.restricted = value ? 1 : 0;
    }

    public void setSyncValue( String value )
    {
        this.syncValue = value;
    }

    public void setType( GroupType value )
    {
        this.type = value.toInteger();
    }

    public void setUser( UserEntity value )
    {
        if ( value != null )
        {
            user.clear();
            user.add( value );
        }
    }

    public void setUserStore( UserStoreEntity value )
    {
        this.userStore = value;

        // invalidate
        qualifiedName = null;
    }

    public void setMemberships( Set<GroupEntity> value )
    {
        this.memberships = value;
    }

    public void setMembers( Set<GroupEntity> value )
    {
        this.members = value;
    }

    public QualifiedGroupname getQualifiedName()
    {
        if ( qualifiedName == null )
        {
            if ( isGlobal() )
            {
                qualifiedName = new QualifiedGroupname( true, null, getName() );
            }
            else
            {
                qualifiedName = new QualifiedGroupname( false, getUserStore().getName(), getName() );
            }
        }

        return qualifiedName;
    }

    public boolean isGlobal()
    {
        return getType().isGlobal();
    }

    /**
     * @return All memberships group keys including memberships thru other memberships (note that the groups are not
     *         duplicated).
     */
    public List<GroupKey> getAllMembershipsGroupKeys()
    {

        if ( allMembershipsGroupKeys == null )
        {
            allMembershipsGroupKeys = new ArrayList<GroupKey>();
            for ( GroupEntity group : getAllMemberships() )
            {
                allMembershipsGroupKeys.add( group.getGroupKey() );
            }
        }

        return allMembershipsGroupKeys;
    }

    /**
     * @return The set of all memberships including memberships thru other memberships (note that the groups are not
     *         duplicated).
     */
    public Set<GroupEntity> getAllMemberships()
    {

        if ( allMemberships == null )
        {
            allMemberships = new HashSet<GroupEntity>();
            this.doAddAllMemberships( allMemberships );
        }

        return allMemberships;
    }

    private void doAddAllMemberships( Set<GroupEntity> allMemberships )
    {

        for ( GroupEntity membership : getMemberships( false ) )
        {
            boolean alreadyAdded = allMemberships.contains( membership );
            if ( !alreadyAdded )
            {
                allMemberships.add( membership );
                membership.doAddAllMemberships( allMemberships );
            }
        }
    }

    public boolean isOfType( final GroupType groupType, boolean recursively )
    {

        if ( groupType.equals( getType() ) )
        {
            return true;
        }

        if ( recursively )
        {

            GroupMembershipSearcher searcher = new GroupMembershipSearcher()
            {
                public boolean isGroupFound( GroupEntity traversedGroup )
                {

                    return groupType.equals( traversedGroup.getType() );
                }
            };

            return searcher.startSearch( this );

        }
        else
        {
            for ( GroupEntity currGroup : getMemberships( false ) )
            {
                if ( groupType.equals( currGroup.getType() ) )
                {
                    return true;
                }
            }
        }

        return false;

    }

    public boolean isUserstoreAdmin( final UserStoreEntity userStore )
    {

        if ( isOfType( GroupType.USERSTORE_ADMINS, false ) )
        {
            UserStoreEntity currUserStore = getUserStore();
            if ( currUserStore != null && currUserStore.equals( userStore ) )
            {
                return true;
            }
        }

        GroupMembershipSearcher searcher = new GroupMembershipSearcher()
        {
            public boolean isGroupFound( GroupEntity traversedGroup )
            {

                if ( GroupType.USERSTORE_ADMINS.equals( traversedGroup.getType() ) )
                {
                    UserStoreEntity currUserStore = traversedGroup.getUserStore();
                    if ( currUserStore != null && currUserStore.equals( userStore ) )
                    {
                        return true;
                    }
                }

                return false;
            }
        };

        return searcher.startSearch( this );
    }

    public boolean isAdministrator()
    {
        return isOfType( GroupType.ADMINS, true );
    }

    public boolean isContributor()
    {
        return isOfType( GroupType.CONTRIBUTORS, true );
    }

    public boolean isExpertContributor()
    {
        return isOfType( GroupType.EXPERT_CONTRIBUTORS, true );
    }

    public boolean isDeveloper()
    {
        return isOfType( GroupType.DEVELOPERS, true );
    }

    public boolean isMemberOf( final GroupEntity group, boolean recursively )
    {

        if ( recursively )
        {

            GroupMembershipSearcher searcher = new GroupMembershipSearcher()
            {
                public boolean isGroupFound( GroupEntity traversedGroup )
                {

                    return group.equals( traversedGroup );
                }
            };

            return searcher.startSearch( this );

        }
        else
        {
            for ( GroupEntity currGroup : getMemberships( false ) )
            {
                if ( currGroup.equals( group ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasMember( GroupEntity group )
    {

        final Collection<GroupEntity> members = this.getMembers( false );
        for ( GroupEntity currGroup : members )
        {
            if ( group.equals( currGroup ) )
            {
                return true;
            }
        }
        return false;
    }

    public GroupEntity addMembership( GroupEntity group )
    {
        this.memberships.add( group );
        group.members.add( this );
        return this;
    }

    public void removeMembership( GroupEntity group )
    {
        this.memberships.remove( group );
        group.members.remove( this );
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        // this breaks the Java equals protocol but is necessary when using potentially proxified object
        if ( !( o instanceof GroupEntity ) )
        {
            return false;
        }

        GroupEntity that = (GroupEntity) o;
        return getGroupKey().equals( that.getGroupKey() );
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 133;
        final int multiplierNonZeroOddNumber = 77;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( key ).toHashCode();
    }

    public String toString()
    {

        StringBuffer s = new StringBuffer();
        s.append( "key = " ).append( getGroupKey() ).append( ", name = '" ).append( getName() ).append( "'" );
        return s.toString();
    }

    public boolean isAnonymous()
    {
        return getType().equals( GroupType.ANONYMOUS );
    }

    public boolean isBuiltIn()
    {
        return getType().isBuiltIn();
    }

    public void removeAllMembers()
    {
        members.clear();
    }

    public boolean hasMembership( GroupEntity group )
    {
        return memberships.contains( group );
    }

    public Set<GroupEntity> getAllMembersRecursively()
    {
        return getAllMembersRecursively( null );

    }

    public Set<GroupEntity> getAllMembersRecursively( final Set<GroupType> groupTypeFilter )
    {
        Set<GroupEntity> allMembers = new LinkedHashSet<GroupEntity>();
        return getAllMembersRecursively( this, allMembers, groupTypeFilter );

    }

    private Set<GroupEntity> getAllMembersRecursively( final GroupEntity root, final Set<GroupEntity> allMembers,
                                                       final Set<GroupType> groupTypeFilter )
    {

        if ( allMembers.contains( root ) )
        {
            return allMembers;
        }

        final boolean checkGroupFilter = groupTypeFilter != null ? true : false;

        Iterator<GroupEntity> membersIterator = members.iterator();
        while ( membersIterator.hasNext() )
        {
            GroupEntity member = membersIterator.next();
            if ( allMembers.contains( member ) || member.equals( root ) )
            {
                continue;
            }
            if ( checkGroupFilter && !groupTypeFilter.contains( member.getType() ) )
            {
                allMembers.addAll( member.getAllMembersRecursively( root, allMembers, groupTypeFilter ) );
            }
            else
            {
                allMembers.add( member );
                allMembers.addAll( member.getAllMembersRecursively( root, allMembers, groupTypeFilter ) );
            }

        }
        return allMembers;

    }

}

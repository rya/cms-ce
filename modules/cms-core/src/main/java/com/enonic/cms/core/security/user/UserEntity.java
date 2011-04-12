/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.springframework.core.style.ToStringCreator;

import com.google.common.collect.Maps;

import com.enonic.cms.framework.CmsToStringStyler;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.domain.user.UserInfo;
import com.enonic.cms.domain.user.field.UserFieldMap;
import com.enonic.cms.domain.user.field.UserFieldTransformer;
import com.enonic.cms.domain.user.field.UserInfoTransformer;

public class UserEntity
    implements User, Serializable
{
    private UserKey key;

    private String name;

    private String displayName;

    private Integer deleted;

    private UserType type;

    private DateTime timestamp;

    private String syncValue;

    private String email;

    private UserStoreEntity userStore;

    private GroupEntity userGroup;

    private byte[] photo;

    private String password;

    private Map<String, String> fieldMap = new HashMap<String, String>();

    private transient UserInfo userInfo = null;

    private transient QualifiedUsername qualifiedName;

    private transient List<GroupKey> allMembershipsGroupKeys;

    public UserEntity()
    {
        // Default constructor used by Hibernate.
    }

    public UserEntity( UserEntity source )
    {
        this();

        this.key = source.getKey();
        this.name = source.getName();
        this.displayName = source.getDisplayName();
        this.email = source.getEmail();
        this.deleted = source.getDeleted();
        this.type = source.getType();
        this.timestamp = source.getTimestamp();
        this.syncValue = source.getSync();
        this.email = source.getEmail();
        this.userStore = source.getUserStore() != null ? new UserStoreEntity( source.getUserStore() ) : null;
        this.userGroup = source.getUserGroup();
        this.photo = source.getPhoto();
        this.password = source.getPassword();
        this.fieldMap = source.getFieldMap() != null ? Maps.newHashMap( source.getFieldMap() ) : null;
    }

    public UserKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getEmail()
    {
        return this.email;
    }

    public Integer getDeleted()
    {
        return deleted;
    }

    public boolean isDeleted()
    {
        return deleted != 0;
    }

    public UserType getType()
    {
        return type;
    }

    public boolean isAnonymous()
    {
        return getType().isAnonymous();
    }

    /**
     * @return true if user is the hard coded admin super user.
     */
    public boolean isRoot()
    {
        return getName().equals( ROOT_UID );
    }

    public boolean isEnterpriseAdmin()
    {
        if ( isRoot() )
        {
            return true;
        }
        if ( isAnonymous() )
        {
            return false;
        }

        if ( getUserGroup() == null )
        {

            return false;
        }

        return getUserGroup().isOfType( GroupType.ENTERPRISE_ADMINS, true );
    }

    public boolean isUserstoreAdmin( UserStoreEntity userStore )
    {
        return !isAnonymous() && getUserGroup() != null && getUserGroup().isUserstoreAdmin( userStore );
    }

    public boolean isAdministrator()
    {
        return !isAnonymous() && getUserGroup() != null && getUserGroup().isAdministrator();
    }

    public boolean isContributor()
    {
        return !isAnonymous() && getUserGroup() != null && getUserGroup().isContributor();
    }

    public boolean isExpertContributor()
    {
        return !isAnonymous() && getUserGroup() != null && getUserGroup().isExpertContributor();
    }

    public boolean isDeveloper()
    {
        return !isAnonymous() && getUserGroup() != null && getUserGroup().isDeveloper();
    }

    public static boolean isBuiltInUser( String uid )
    {
        return uid.equalsIgnoreCase( ROOT_UID ) || uid.equalsIgnoreCase( ANONYMOUS_UID );
    }

    public boolean isBuiltIn()
    {
        if ( getType().isBuiltIn() )
        {
            return true;
        }

        return getUserGroup() != null && getUserGroup().isBuiltIn();
    }

    public DateTime getTimestamp()
    {
        return timestamp;
    }

    public String getSync()
    {
        return syncValue;
    }

    public UserStoreEntity getUserStore()
    {
        return userStore;
    }

    public UserStoreKey getUserStoreKey()
    {
        return getUserStore() != null ? getUserStore().getKey() : null;
    }

    public void setKey( UserKey key )
    {
        this.key = key;
    }

    public void setName( String value )
    {
        this.name = value;

        // invalidate
        qualifiedName = null;
    }

    public void setDisplayName( final String value )
    {
        displayName = value;
    }

    public void setDeleted( int deleted )
    {
        this.deleted = deleted;
    }

    public void setDeleted( boolean value )
    {
        this.deleted = value ? 1 : 0;
    }

    public void setType( UserType value )
    {
        type = value;
    }

    public void setTimestamp( DateTime value )
    {
        this.timestamp = value;
    }

    public void setSyncValue( String value )
    {
        this.syncValue = value;
    }

    public void setEmail( String value )
    {
        this.email = value;
    }

    public void setUserStore( UserStoreEntity value )
    {
        this.userStore = value;

        // invalidate
        qualifiedName = null;
    }

    public void setUserGroup( GroupEntity value )
    {
        this.userGroup = value;
    }

    public GroupKey getUserGroupKey()
    {
        return getUserGroup() != null ? getUserGroup().getGroupKey() : null;
    }

    public GroupEntity getUserGroup()
    {
        return userGroup;
    }

    public QualifiedUsername getQualifiedName()
    {

        if ( qualifiedName == null )
        {
            String uid = getName();
            if ( "anonymous".equals( uid ) || "admin".equals( uid ) )
            {
                qualifiedName = new QualifiedUsername( uid );
            }
            else if ( userStore != null )
            {
                qualifiedName = new QualifiedUsername( userStore.getName(), uid );
            }
        }

        return qualifiedName;
    }

    /**
     * @return The distinct set of all group keys (recursively) of this users memberships, including the user group key.
     */
    public List<GroupKey> getAllMembershipsGroupKeys()
    {

        if ( allMembershipsGroupKeys == null )
        {
            allMembershipsGroupKeys = new ArrayList<GroupKey>();
            GroupEntity userGroup = getUserGroup();
            allMembershipsGroupKeys.add( userGroup.getGroupKey() );
            allMembershipsGroupKeys.addAll( userGroup.getAllMembershipsGroupKeys() );
        }

        return allMembershipsGroupKeys;
    }

    /**
     * @return The distinct set of all groups, including recursively fetched subgroups of this users memberships.
     */
    public Set<GroupEntity> getAllMembershipsGroups()
    {

        if ( getUserGroup() != null )
        {
            return getUserGroup().getAllMemberships();
        }
        else
        {
            return new HashSet<GroupEntity>();
        }
    }

    public boolean isMemberOf( GroupEntity group, boolean recursively )
    {
        final GroupEntity userGroup = getUserGroup();
        if ( userGroup == null )
        {
            return false;
        }
        return group.equals( userGroup ) || userGroup.isMemberOf( group, recursively );
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof UserEntity ) )
        {
            return false;
        }

        UserEntity that = (UserEntity) o;

        return getKey().equals( that.getKey() );

    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 273;
        final int multiplierNonZeroOddNumber = 637;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( getKey() ).toHashCode();
    }

    public String getPassword()
    {
        return this.password;
    }

    public void encodePassword( String password )
    {
        if ( password != null )
        {
            this.password = DigestUtils.shaHex( password );
        }
        else
        {
            this.password = null;
        }
    }

    public boolean verifyPassword( String password )
    {
        if ( password == null )
        {
            return this.password == null;
        }

        return DigestUtils.shaHex( password ).equals( this.password );
    }

    public boolean hasUserGroup()
    {
        return getUserGroup() != null;
    }

    public Set<GroupEntity> getAllMemberships()
    {
        return getAllMembershipsGroups();
    }

    public Set<GroupEntity> getDirectMemberships()
    {
        if ( !hasUserGroup() )
        {
            return new HashSet<GroupEntity>();
        }

        return getUserGroup().getMemberships( false );
    }

    public UserInfo getUserInfo()
    {
        return doGetUserInfo();
    }

    public Map<String, String> getFieldMap()
    {
        return fieldMap;
    }

    private UserInfo doGetUserInfo()
    {
        if ( this.userInfo == null )
        {
            final UserFieldTransformer fieldTransformer = new UserFieldTransformer();
            final UserFieldMap fieldMap = fieldTransformer.fromStoreableMap( this.fieldMap );
            fieldTransformer.updatePhoto( fieldMap, this.photo );

            final UserInfoTransformer infoTransformer = new UserInfoTransformer();
            this.userInfo = infoTransformer.toUserInfo( fieldMap );
        }
        return this.userInfo;
    }

    public boolean updateUserInfoNewOnly( final UserInfo info )
    {
        return doUpdateUserInfo( info, true );
    }

    public boolean updateUserInfo( final UserInfo info )
    {
        return doUpdateUserInfo( info, false );
    }

    private boolean doUpdateUserInfo( final UserInfo info, final boolean updateExistingOnly )
    {
        final UserInfo oldInfo = doGetUserInfo();

        final UserInfoTransformer infoTransformer = new UserInfoTransformer();
        final UserFieldMap fieldMap = infoTransformer.toUserFields( info );

        final UserFieldTransformer fieldTransformer = new UserFieldTransformer();

        if ( !updateExistingOnly )
        {
            this.fieldMap.clear();
        }

        // invalidate          
        this.userInfo = null;

        this.fieldMap.putAll( fieldTransformer.toStoreableMap( fieldMap ) );
        this.photo = info.getPhoto();

        final UserInfo newInfo = doGetUserInfo();

        return !oldInfo.equals( newInfo );
    }

    @Override
    public String toString()
    {
        ToStringCreator toString = new ToStringCreator( this, CmsToStringStyler.DEFAULT );
        toString.append( "qualifiedName", getQualifiedName() );
        return toString.toString();
    }

    public byte[] getPhoto()
    {
        return photo;
    }

    public boolean hasPhoto()
    {
        return photo != null;
    }

    public void setPhoto( byte[] photo )
    {
        this.photo = photo;
    }
}

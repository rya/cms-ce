/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public class GroupEntityDao
    extends AbstractBaseEntityDao<GroupEntity>
    implements GroupDao
{
    private final static GroupType[] GLOBAL_GROUP_TYPES =
        new GroupType[]{GroupType.ANONYMOUS, GroupType.CONTRIBUTORS, GroupType.ADMINS, GroupType.GLOBAL_GROUP, GroupType.DEVELOPERS,
            GroupType.EXPERT_CONTRIBUTORS};

    private boolean initializedCacheKeys = false;

    private transient GroupKey cachedEnterpriseAdminGroupKey;

    private transient GroupKey cachedAdministratorGroupKey;

    private transient GroupKey cachedDeveloperGroupKey;

    private transient GroupKey cachedExpertContributorGroupKey;

    private transient GroupKey cachedContributorGroupKey;

    private transient GroupKey cachedAnonymousGroupKey;

    public void invalidateCachedKeys()
    {
        initializedCacheKeys = false;
    }

    private void initalizeCacheKeys()
    {
        if ( initializedCacheKeys )
        {
            return;
        }

        // Enterprise Administrator
        GroupSpecification enterpriseAdminSpec = new GroupSpecification();
        enterpriseAdminSpec.setType( GroupType.ENTERPRISE_ADMINS );
        GroupEntity enterpriseAdministrator = findSingleBySpecification( enterpriseAdminSpec );
        if ( enterpriseAdministrator == null )
        {
            throw new IllegalStateException( "Built-in Enterprise Administrator group does not exist" );
        }
        cachedEnterpriseAdminGroupKey = enterpriseAdministrator.getGroupKey();

        // Administrator
        GroupSpecification administratorSpec = new GroupSpecification();
        administratorSpec.setType( GroupType.ADMINS );
        GroupEntity administrator = findSingleBySpecification( administratorSpec );
        if ( administrator == null )
        {
            throw new IllegalStateException( "Built-in Administrator group does not exist" );
        }
        cachedAdministratorGroupKey = administrator.getGroupKey();

        // Developer
        GroupSpecification developerSpec = new GroupSpecification();
        developerSpec.setType( GroupType.DEVELOPERS );
        GroupEntity developer = findSingleBySpecification( developerSpec );
        if ( developer == null )
        {
            throw new IllegalStateException( "Built-in Developer group does not exist" );
        }
        cachedDeveloperGroupKey = developer.getGroupKey();

        // Expert Contributor
        GroupSpecification expertContributorSpec = new GroupSpecification();
        expertContributorSpec.setType( GroupType.EXPERT_CONTRIBUTORS );
        GroupEntity expertContributor = findSingleBySpecification( expertContributorSpec );
        if ( expertContributor == null )
        {
            throw new IllegalStateException( "Built-in Expert Contributor group does not exist" );
        }
        cachedExpertContributorGroupKey = expertContributor.getGroupKey();

        // Contributor
        GroupSpecification contributorSpec = new GroupSpecification();
        contributorSpec.setType( GroupType.CONTRIBUTORS );
        GroupEntity contributor = findSingleBySpecification( contributorSpec );
        if ( contributor == null )
        {
            throw new IllegalStateException( "Built-in Contributor group does not exist" );
        }
        cachedContributorGroupKey = contributor.getGroupKey();

        // Anonymous / Everyone
        GroupSpecification anonymousSpec = new GroupSpecification();
        anonymousSpec.setType( GroupType.ANONYMOUS );
        GroupEntity anonymous = findSingleBySpecification( anonymousSpec );
        if ( anonymous == null )
        {
            throw new IllegalStateException( "Built-in Anonymous group does not exist" );
        }
        cachedAnonymousGroupKey = anonymous.getGroupKey();

        initializedCacheKeys = true;
    }


    public GroupEntity find( String groupKey )
    {
        return findByKey( new GroupKey( groupKey ) );
    }

    public GroupEntity findByKey( GroupKey groupKey )
    {
        return get( GroupEntity.class, groupKey );
    }

    public Collection<GroupEntity> findAll( boolean includeDeleted )
    {
        return findByNamedQuery( GroupEntity.class, "GroupEntity.findAll", "deleted", includeDeleted ? 1 : 0 );
    }

    public GroupEntity findSingleBySpecification( GroupSpecification spec )
    {
        List<GroupEntity> list = findBySpecification( spec );
        if ( list.isEmpty() )
        {
            return null;
        }
        if ( list.size() > 1 )
        {
            throw new IllegalArgumentException( "Expected a single row" );
        }
        return list.get( 0 );
    }

    public GroupEntity findBuiltInEnterpriseAdministrator()
    {
        initalizeCacheKeys();
        return findByKey( cachedEnterpriseAdminGroupKey );
    }

    public GroupEntity findBuiltInUserStoreAdministrator( UserStoreKey userStoreKey )
    {
        GroupSpecification userStoreAdminSpec = new GroupSpecification();
        userStoreAdminSpec.setUserStoreKey( userStoreKey );
        userStoreAdminSpec.setType( GroupType.USERSTORE_ADMINS );

        return findSingleBySpecification( userStoreAdminSpec );
    }

    public GroupEntity findBuiltInAuthenticatedUsers( UserStoreKey userStoreKey )
    {
        Assert.notNull( userStoreKey, "userStoreKey cannot be null" );

        GroupSpecification userStoreAdminSpec = new GroupSpecification();
        userStoreAdminSpec.setUserStoreKey( userStoreKey );
        userStoreAdminSpec.setType( GroupType.AUTHENTICATED_USERS );

        return findSingleBySpecification( userStoreAdminSpec );
    }

    public GroupEntity findBuiltInAdministrator()
    {
        initalizeCacheKeys();
        return findByKey( cachedAdministratorGroupKey );
    }

    public GroupEntity findBuiltInDeveloper()
    {
        initalizeCacheKeys();
        return findByKey( cachedDeveloperGroupKey );
    }

    public GroupEntity findBuiltInExpertContributor()
    {
        initalizeCacheKeys();
        return findByKey( cachedExpertContributorGroupKey );
    }

    public GroupEntity findBuiltInContributor()
    {
        initalizeCacheKeys();
        return findByKey( cachedContributorGroupKey );
    }

    public GroupEntity findBuiltInAnonymous()
    {
        initalizeCacheKeys();
        return findByKey( cachedAnonymousGroupKey );
    }

    public List<GroupEntity> findBySpecification( GroupSpecification spec )
    {
        String hqlQuery = createHqlQuery( spec );

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hqlQuery );
        compiled.setCacheable( true );
        if ( spec.getKey() != null )
        {
            compiled.setString( "key", spec.getKey().toString() );
        }
        if ( spec.getName() != null )
        {
            compiled.setString( "name", spec.getName() );
        }
        if ( spec.getSyncValue() != null )
        {
            compiled.setString( "syncValue", spec.getSyncValue() );
        }
        if ( spec.getUserStoreKey() != null )
        {
            compiled.setInteger( "userStoreKey", spec.getUserStoreKey().toInt() );
        }
        if ( spec.getType() != null )
        {
            compiled.setInteger( "type", spec.getType().toInteger() );
        }
        return compiled.list();
    }

    public Collection<GroupEntity> findByUserstore( UserStoreKey userStoreKey, boolean includeDeleted )
    {
        return findByNamedQuery( GroupEntity.class, "GroupEntity.findByUserStore", new String[]{"userStoreKey", "deleted"},
                                 new Object[]{userStoreKey.toInt(), includeDeleted ? 1 : 0} );
    }

    public GroupEntity findByUserStoreKeyAndSyncValue( UserStoreKey userStoreKey, String syncValue, boolean includeDeleted )
    {
        return findSingleByNamedQuery( GroupEntity.class, "GroupEntity.findByUserStoreAndSyncValue",
                                       new String[]{"userStoreKey", "syncValue", "deleted"},
                                       new Object[]{userStoreKey.toInt(), syncValue, includeDeleted ? 1 : 0} );

    }

    public List<GroupEntity> findByUserStoreKeyAndGroupname( UserStoreKey userStoreKey, String groupName, boolean includeDeleted )
    {
        return findByNamedQuery( GroupEntity.class, "GroupEntity.findByQualifiedGroupname", new String[]{"userStoreKey", "name", "deleted"},
                                 new Object[]{userStoreKey.toInt(), groupName, includeDeleted ? 1 : 0} );
    }

    public GroupEntity findSingleUndeletedByUserStoreKeyAndGroupname( UserStoreKey userStoreKey, String groupName )
    {
        return findSingleByNamedQuery( GroupEntity.class, "GroupEntity.findByQualifiedGroupname",
                                       new String[]{"userStoreKey", "name", "deleted"}, new Object[]{userStoreKey.toInt(), groupName, 0} );
    }

    public GroupEntity findSingleByGroupType( GroupType groupType )
    {

        if ( !groupType.isOnlyOneGroupOccurance() )
        {
            throw new IllegalArgumentException( "Given group type can have more than one group in the system" );
        }

        return findSingleByNamedQuery( GroupEntity.class, "GroupEntity.findByGroupType", "groupType", groupType.toInteger() );
    }

    public GroupEntity findSingleByGroupTypeAndUserStore( GroupType groupType, UserStoreKey userStoreKey )
    {

        return findSingleByNamedQuery( GroupEntity.class, "GroupEntity.findByGroupTypeAndUserStore",
                                       new String[]{"groupType", "userStoreKey"},
                                       new Object[]{groupType.toInteger(), userStoreKey.toInt()} );
    }

    public GroupEntity findGlobalGroupByName( final String name, final boolean includeDeleted )
    {

        return executeSingleResult( GroupEntity.class, new HibernateCallback()
        {

            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {

                Criteria crit = session.createCriteria( GroupEntity.class ).setCacheable( true );
                crit.add( Restrictions.eq( "name", name ) );
                crit.add( Restrictions.in( "type", GroupType.getIntegerValues( GLOBAL_GROUP_TYPES ) ) );

                if ( !includeDeleted )
                {
                    crit.add( Restrictions.eq( "deleted", 0 ) );
                }

                List list = crit.list();
                if ( list.size() == 0 )
                {
                    return null;
                }

                // we return first one if found
                return list.get( 0 );
            }
        } );
    }

    @SuppressWarnings({"unchecked"})
    public List<GroupEntity> findByQuery( final GroupQuery spec )
    {
        return (List<GroupEntity>) getHibernateTemplate().execute( new HibernateCallback()
        {

            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {

                Criteria crit = session.createCriteria( GroupEntity.class ).setCacheable( true );

                if ( spec.getUserStoreKey() != null )
                {
                    crit.add( Restrictions.eq( "userStore.key", spec.getUserStoreKey().toInt() ) );
                }
                else if ( spec.isGlobalOnly() )
                {
                    crit.add( Restrictions.isNull( "userStore.key" ) );
                }

                if ( !spec.isIncludeDeleted() )
                {
                    crit.add( Restrictions.eq( "deleted", 0 ) );
                }
                if ( spec.getQuery() != null && spec.getQuery().length() > 0 )
                {
                    crit.add( Restrictions.ilike( "name", spec.getQuery(), MatchMode.ANYWHERE ) );
                }

                if ( spec.getOrderBy() != null && !spec.getOrderBy().equals( "" ) )
                {
                    if ( spec.isOrderAscending() )
                    {
                        crit.addOrder( Order.asc( spec.getOrderBy() ).ignoreCase() );
                    }
                    else
                    {
                        crit.addOrder( Order.desc( spec.getOrderBy() ).ignoreCase() );
                    }
                }

                if ( spec.getGroupTypes() != null )
                {
                    Collection<GroupType> gt = new ArrayList<GroupType>( spec.getGroupTypes() );
                    if ( spec.isIncludeBuiltInGroups() )
                    {
                        if ( !spec.isIncludeAnonymousGroups() )
                        {
                            gt.remove( GroupType.ANONYMOUS );
                        }
                    }
                    else
                    {
                        gt.removeAll( GroupType.getBuiltInTypes() );
                    }
                    if ( spec.isIncludeUserGroups() )
                    {
                        gt.add( GroupType.USER );
                    }
                    crit.add( Restrictions.in( "type", GroupType.getIntegerValues( gt ) ) );
                }
                else
                {
                    Collection<GroupType> notGroupType = new ArrayList<GroupType>();
                    if ( !spec.isIncludeBuiltInGroups() )
                    {
                        notGroupType.addAll( GroupType.getBuiltInTypes() );
                        if ( spec.isIncludeAnonymousGroups() )
                        {
                            notGroupType.remove( GroupType.ANONYMOUS );
                        }
                    }
                    if ( !spec.isIncludeUserGroups() )
                    {
                        notGroupType.add( GroupType.USER );
                    }
                    if ( !spec.isIncludeAnonymousGroups() && !notGroupType.contains( GroupType.ANONYMOUS ) )
                    {
                        notGroupType.add( GroupType.ANONYMOUS );
                    }
                    crit.add( Restrictions.not( Restrictions.in( "type", GroupType.getIntegerValues( notGroupType ) ) ) );
                }

                crit.setFirstResult( spec.getIndex() );
                List list = crit.list();
                if ( spec.getCount() == null )
                {
                    return list;
                }
                else
                {
                    return list.subList( 0, Math.min( spec.getCount(), list.size() ) );
                }
            }
        } );
    }

    private String createHqlQuery( final GroupSpecification spec )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );

        hqlQuery.addFromTable( GroupEntity.class.getName(), "g", SelectBuilder.NO_JOIN, null );

        if ( spec.getDeletedState() != null )
        {

            if ( spec.getDeletedState() == GroupSpecification.DeletedState.DELETED )
            {
                hqlQuery.addFilter( "AND", "g.deleted = 1" );
            }
            if ( spec.getDeletedState() == GroupSpecification.DeletedState.NOT_DELETED )
            {
                hqlQuery.addFilter( "AND", "g.deleted = 0" );
            }
        }
        if ( spec.getUserStoreKey() != null )
        {
            hqlQuery.addFilter( "AND", "g.userStore.key = :userStoreKey" );
        }
        if ( spec.getName() != null )
        {
            hqlQuery.addFilter( "AND", "g.name = :name" );
        }
        if ( spec.getKey() != null )
        {
            hqlQuery.addFilter( "AND", "g.key = :key" );
        }
        if ( spec.getSyncValue() != null )
        {
            hqlQuery.addFilter( "AND", "g.syncValue = :syncValue" );
        }
        if ( spec.getType() != null )
        {
            hqlQuery.addFilter( "AND", "g.type = :type" );
        }

        return hqlQuery.toString();
    }

    public EntityPageList<GroupEntity> findAll( int index, int count )
    {
        return findPageList( GroupEntity.class, "x.deleted = 0", index, count );
    }
}

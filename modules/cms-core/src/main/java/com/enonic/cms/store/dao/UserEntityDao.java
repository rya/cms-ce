/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.sql.SQLException;
import java.util.List;

import com.enonic.cms.core.security.userstore.UserStoreKey;
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
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;

public class UserEntityDao
    extends AbstractBaseEntityDao<UserEntity>
    implements UserDao
{

    private transient UserKey cachedAnonymousUserKey;

    private transient UserKey cachedEnterpriseAdminUserKey;

    private boolean initializedCacheKeys = false;

    private void initializeCachedKeys()
    {
        if ( initializedCacheKeys )
        {
            return;
        }

        // Anonymous user
        UserSpecification anonymousUserSpec = new UserSpecification();
        anonymousUserSpec.setType( UserType.ANONYMOUS );
        UserEntity anonymous = findSingleBySpecification( anonymousUserSpec );
        if ( anonymous == null )
        {
            throw new IllegalStateException( "Built-in Anonymous user does not exist" );
        }
        cachedAnonymousUserKey = anonymous.getKey();

        // Enterprise Admin user
        UserSpecification enterpriseAdminUserSpec = new UserSpecification();
        enterpriseAdminUserSpec.setType( UserType.ADMINISTRATOR );
        UserEntity enterpriseAdmin = findSingleBySpecification( enterpriseAdminUserSpec );
        if ( enterpriseAdmin == null )
        {
            throw new IllegalStateException( "Built-in Enterprise Admin user does not exist" );
        }
        cachedEnterpriseAdminUserKey = enterpriseAdmin.getKey();

        initializedCacheKeys = true;
    }

    public List<UserEntity> findAll( boolean deleted )
    {
        return findByNamedQuery( UserEntity.class, "UserEntity.findAll", "deleted", deleted ? 1 : 0 );
    }

    public UserEntity findSingleBySpecification( UserSpecification spec )
    {
        List<UserEntity> list = findBySpecification( spec );
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

    public List<UserEntity> findBySpecification( final UserSpecification spec )
    {

        String hqlQuery = createHqlQuery( spec );

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hqlQuery );
        compiled.setCacheable( true );
        if ( spec.getKey() != null )
        {
            compiled.setParameter( "key", spec.getKey() );
        }
        if ( spec.getName() != null )
        {
            compiled.setString( "name", spec.getName().toLowerCase() );
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
            compiled.setInteger( "type", spec.getType().getKey() );
        }
        if ( spec.getUserGroupKey() != null )
        {
            compiled.setString( "userGroupKey", spec.getUserGroupKey().toString() );
        }
        if ( spec.getEmail() != null )
        {
            compiled.setString( "email", spec.getEmail().toLowerCase() );
        }
        return compiled.list();
    }


    public UserEntity findBuiltInAnonymousUser()
    {
        initializeCachedKeys();
        return findByKey( cachedAnonymousUserKey );
    }

    public UserEntity findBuiltInEnterpriseAdminUser()
    {
        initializeCachedKeys();
        return findByKey( cachedEnterpriseAdminUserKey );
    }

    public UserEntity findByKey( String key )
    {
        return get( UserEntity.class, new UserKey( key ) );
    }

    public UserEntity findByKey( UserKey key )
    {
        Assert.notNull( key, "key cannot be null" );

        return get( UserEntity.class, key );
    }


    public UserEntity findBuiltInGlobalByName( String name )
    {
        return findSingleByNamedQuery( UserEntity.class, "UserEntity.findBuiltInGlobalByName", new String[]{"name", "deleted"},
                                       new Object[]{name.toLowerCase(), 0} );
    }

    public UserEntity findByQualifiedUsername( final QualifiedUsername qualifiedUsername )
    {
        String userName = qualifiedUsername.getUsername();

        if ( userName != null )
        {
            userName = userName.toLowerCase();
        }

        if ( qualifiedUsername.getUserStoreKey() == null && qualifiedUsername.hasUserStoreNameSet() )
        {
            return findByUserstoreNameAndUsername( qualifiedUsername.getUserStoreName(), userName );
        }
        else
        {
            return findByUserStoreKeyAndUsername( qualifiedUsername.getUserStoreKey(), userName );
        }
    }

    public UserEntity findByUserstoreNameAndUsername( final String userstoreName, final String username )
    {

        return findSingleByNamedQuery( UserEntity.class, "UserEntity.findByUserstoreNameAndUsername", new String[]{"userstoreName", "name"},
                                       new Object[]{userstoreName, username.toLowerCase()} );

    }

    public UserEntity findByUserStoreKeyAndUsername( final UserStoreKey userStoreKey, final String name )
    {
        if ( userStoreKey != null )
        {

            return findSingleByNamedQuery( UserEntity.class, "UserEntity.findByQualifiedUsername", new String[]{"userStoreKey", "name"},
                                           new Object[]{userStoreKey.toInt(), name.toLowerCase()} );
        }
        else
        {
            return findSingleByNamedQuery( UserEntity.class, "UserEntity.findByQualifiedUsernameNoUserStore", new String[]{"name"},
                                           new Object[]{name.toLowerCase()} );

        }
    }

    public List<UserEntity> findByUserStoreKey( final UserStoreKey userStoreKey, final Integer index, final Integer count,
                                                final boolean includeDeleted )
    {

        return executeListResult( UserEntity.class, new HibernateCallback()
        {

            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {
                Query query = session.getNamedQuery( "UserEntity.findByUserStoreKey" );
                if ( index != null )
                {
                    query.setFirstResult( index );
                }
                query.setInteger( "userStoreKey", userStoreKey.toInt() );
                query.setInteger( "deleted", includeDeleted ? 1 : 0 );

                List list = query.list();
                if ( count == null )
                {
                    return list;
                }
                else
                {
                    return list.subList( 0, Math.min( count, list.size() ) );
                }
            }
        } );
    }

    public List<UserEntity> findByQuery( final UserStoreKey userStoreKey, final String queryStr, final String orderBy,
                                         final boolean orderAscending )
    {

        return (List<UserEntity>) getHibernateTemplate().execute( new HibernateCallback()
        {

            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {

                Criteria crit = session.createCriteria( UserEntity.class ).setCacheable( true );
                crit.add( Restrictions.eq( "deleted", 0 ) );
                crit.add( Restrictions.ne( "type", UserType.ADMINISTRATOR.getKey() ) );
                if ( userStoreKey != null )
                {
                    crit.add( Restrictions.eq( "userStore.key", userStoreKey.toInt() ) );
                }

                if ( queryStr != null && queryStr.length() > 0 )
                {
                    crit.add( Restrictions.or( Restrictions.or( Restrictions.ilike( "name", queryStr, MatchMode.ANYWHERE ),
                                                                Restrictions.ilike( "displayName", queryStr, MatchMode.ANYWHERE ) ),
                                               Restrictions.ilike( "email", queryStr, MatchMode.ANYWHERE ) ) );

                }

                if ( orderBy != null )
                {
                    if ( orderAscending )
                    {
                        crit.addOrder( Order.asc( orderBy ).ignoreCase() );
                    }
                    else
                    {
                        crit.addOrder( Order.desc( orderBy ).ignoreCase() );
                    }
                }

                return crit.list();
            }
        } );
    }

    private String createHqlQuery( final UserSpecification spec )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );

        hqlQuery.addFromTable( UserEntity.class.getName(), "u", SelectBuilder.NO_JOIN, null );

        if ( spec.getDeletedState() != UserSpecification.DeletedState.ANY )
        {

            if ( spec.getDeletedState() == UserSpecification.DeletedState.DELETED )
            {
                hqlQuery.addFilter( "AND", "u.deleted = 1" );
            }
            if ( spec.getDeletedState() == UserSpecification.DeletedState.NOT_DELETED )
            {
                hqlQuery.addFilter( "AND", "u.deleted = 0" );
            }
        }
        if ( spec.getUserStoreKey() != null )
        {
            hqlQuery.addFilter( "AND", "u.userStore.key = :userStoreKey" );
        }
        if ( spec.getName() != null )
        {
            hqlQuery.addFilter( "AND", "lower(u.name) = :name" );
        }
        if ( spec.getKey() != null )
        {
            hqlQuery.addFilter( "AND", "u.key = :key" );
        }
        if ( spec.getSyncValue() != null )
        {
            hqlQuery.addFilter( "AND", "u.syncValue = :syncValue" );
        }
        if ( spec.getType() != null )
        {
            hqlQuery.addFilter( "AND", "u.type = :type" );
        }
        if ( spec.getUserGroupKey() != null )
        {
            hqlQuery.addFilter( "AND", "u.userGroup.key = :userGroupKey" );
        }
        if ( spec.getEmail() != null )
        {
            hqlQuery.addFilter( "AND", "lower(u.email) = :email" );
        }

        return hqlQuery.toString();
    }

    public EntityPageList<UserEntity> findAll( int index, int count )
    {
        return findPageList( UserEntity.class, "x.deleted = 0", index, count );
    }
}
/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.core.EntityPageList;


public abstract class AbstractBaseEntityDao<T>
    implements EntityDao<T>
{

    private HibernateTemplate hibernateTemplate;

    protected final <T> T get( Class<T> clazz, Serializable key )
    {
        return typecast( clazz, getHibernateTemplate().get( clazz, key ) );
    }

    protected final <T> List<T> findByNamedQuery( Class<T> clazz, String queryName )
    {
        return typecastList( clazz, getHibernateTemplate().findByNamedQuery( queryName ) );
    }

    protected final <T> List<T> findByNamedQuery( Class<T> clazz, String queryName, String name, Object value )
    {
        return typecastList( clazz, getHibernateTemplate().findByNamedQueryAndNamedParam( queryName, name, value ) );
    }

    protected final <T> List<T> findByNamedQuery( Class<T> clazz, String queryName, String[] nameList, Object[] valueList )
    {
        return typecastList( clazz, getHibernateTemplate().findByNamedQueryAndNamedParam( queryName, nameList, valueList ) );
    }

    protected final <T> T findSingleByNamedQuery( Class<T> clazz, String queryName )
    {
        List<T> list = findByNamedQuery( clazz, queryName );
        return list.isEmpty() ? null : list.get( 0 );
    }

    protected final <T> T findSingleByNamedQuery( Class<T> clazz, String queryName, String name, Object value )
    {
        List<T> list = findByNamedQuery( clazz, queryName, name, value );
        checkSingleResult( list, queryName );
        return list.isEmpty() ? null : list.get( 0 );
    }

    protected final <T> T findFirstByNamedQuery( Class<T> clazz, String queryName, String name, Object value )
    {
        List<T> list = findByNamedQuery( clazz, queryName, name, value );
        return list.isEmpty() ? null : list.get( 0 );
    }

    protected final <T> T findSingleByNamedQuery( Class<T> clazz, String queryName, String[] nameList, Object[] valueList )
    {
        List<T> list = findByNamedQuery( clazz, queryName, nameList, valueList );
        checkSingleResult( list, queryName );
        return list.isEmpty() ? null : list.get( 0 );
    }

    protected final <T> T findFirstByNamedQuery( Class<T> clazz, String queryName, String[] nameList, Object[] valueList )
    {
        List<T> list = findByNamedQuery( clazz, queryName, nameList, valueList );
        return list.isEmpty() ? null : list.get( 0 );
    }

    protected final Integer selectSingleIntegerByNamedQuery( String queryName, String[] nameList, Object[] valueList )
    {
        final List list = getHibernateTemplate().findByNamedQueryAndNamedParam( queryName, nameList, valueList );
        checkSingleResult( list, queryName );
        return (Integer) ( list.isEmpty() ? null : list.get( 0 ) );
    }

    protected final <T> T executeSingleResult( Class<T> clazz, HibernateCallback callback )
    {
        return typecast( clazz, getHibernateTemplate().execute( callback ) );
    }

    protected final <T> List<T> executeListResult( Class<T> clazz, HibernateCallback callback )
    {
        return typecastList( clazz, getHibernateTemplate().execute( callback ) );
    }

    @SuppressWarnings("unchecked")
    private <T> T typecast( Class<T> clazz, Object value )
    {
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> typecastList( Class<T> clazz, Object list )
    {
        return (List<T>) list;
    }

    private void checkSingleResult( List list, String queryName )
    {
        if ( list.size() > 1 )
        {
            throw new IllegalStateException( "Expected a single row while running query '" + queryName + "', got: " + list.size() );
        }
    }

    public void storeNew( T entity )
    {
        if ( entity == null )
        {
            throw new IllegalArgumentException( "Given entity cannot be null" );
        }

        getHibernateTemplate().save( entity );
    }

    public void storeNew( List<T> entities )
    {
        if ( entities == null )
        {
            throw new IllegalArgumentException( "Given entities cannot be null" );
        }

        for ( T entity : entities )
        {
            getHibernateTemplate().save( entity );
        }
    }

    public void updateExisting( T entity )
    {
        if ( entity == null )
        {
            throw new IllegalArgumentException( "Given entity cannot be null" );
        }

        getHibernateTemplate().update( entity );
    }

    public void updateExisting( List<T> entities )
    {
        if ( entities == null )
        {
            throw new IllegalArgumentException( "Given entities cannot be null" );
        }

        for ( T entity : entities )
        {
            getHibernateTemplate().update( entity );
        }
    }

    public void store( T entity )
    {
        getHibernateTemplate().saveOrUpdate( entity );
    }

    public void storeAll( List<T> entities )
    {
        getHibernateTemplate().saveOrUpdateAll( entities );
    }

    /**
     * Deletes the specified object from the database.
     *
     * @param entity A Hibernate persisted object to be deleted.
     */
    public void delete( T entity )
    {
        getHibernateTemplate().delete( entity );
    }

    public final void deleteAll( List<?> entities )
    {
        getHibernateTemplate().deleteAll( entities );
    }

    public final void flush()
    {
        getHibernateTemplate().flush();
    }

    public void refresh( T object )
    {
        getHibernateTemplate().refresh( object );
    }

    public void evict( T object )
    {
        getHibernateTemplate().evict( object );
    }

    public int deleteByNamedQuery( final String queryName, String paramName, Object paramValue )
    {
        return executeByNamedQuery( queryName, paramName != null ? new String[]{paramName} : null,
                                    paramValue != null ? new Object[]{paramValue} : null );
    }

    public int deleteByNamedQuery( final String queryName, String[] paramName, Object[] paramValue )
    {
        return executeByNamedQuery( queryName, paramName, paramValue );
    }

    public int updateByNamedQuery( final String queryName, String paramName, Object paramValue )
    {
        return executeByNamedQuery( queryName, paramName != null ? new String[]{paramName} : null,
                                    paramValue != null ? new Object[]{paramValue} : null );
    }

    private int executeByNamedQuery( final String queryName, final String[] paramNames, final Object[] paramValues )
    {
        return ( (Number) getHibernateTemplate().execute( new HibernateCallback()
        {
            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {
                Query query = session.getNamedQuery( queryName );
                if ( paramNames != null )
                {
                    for ( int i = 0; i < paramNames.length; i++ )
                    {
                        query.setParameter( paramNames[i], paramValues[i] );
                    }
                }

                return query.executeUpdate();
            }
        } ) ).intValue();
    }

    public HibernateTemplate getHibernateTemplate()
    {
        return hibernateTemplate;
    }

    @Autowired
    public final void setHibernateTemplate( HibernateTemplate value )
    {
        hibernateTemplate = value;
    }

    protected final EntityPageList<T> findPageList( Class<T> clz, String filter, int index, int count )
    {
        final int totalCount = findTotalCount( clz, filter );
        final List<T> list = findPageItems( clz, filter, index, count );
        return new EntityPageList<T>( index, totalCount, list );
    }

    private int findTotalCount( Class<T> clz, String filter )
    {
        final StringBuffer hql = new StringBuffer( "select count(x) from " );
        hql.append( clz.getName() ).append( " x" );

        if ( filter != null )
        {
            hql.append( " where " ).append( filter );
        }

        final Query query = createQuery( hql.toString() );
        return ( (Number) query.list().get( 0 ) ).intValue();
    }

    @SuppressWarnings("unchecked")
    private List<T> findPageItems( Class<T> clz, String filter, int index, int count )
    {
        final StringBuffer hql = new StringBuffer( "select x from " );
        hql.append( clz.getName() ).append( " x" );

        if ( filter != null )
        {
            hql.append( " where " ).append( filter );
        }

        final Query query = createQuery( hql.toString() );
        query.setFirstResult( index );
        query.setMaxResults( count );
        return (List<T>) query.list();
    }

    private Query createQuery( final String query )
    {
        return (Query) this.hibernateTemplate.execute( new HibernateCallback()
        {
            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {
                return session.createQuery( query );
            }
        } );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.enonic.cms.core.content.contenttype.ContentTypeKey;

public class ContentIndexEntityDao
    extends AbstractBaseEntityDao<ContentIndexEntity>
    implements ContentIndexDao
{

    public ContentIndexEntity findByKey( String key )
    {
        return get( ContentIndexEntity.class, key );
    }

    public int removeAll()
    {
        return deleteByNamedQuery( "ContentIndexEntity.deleteAll", null, null );
    }

    public int removeByContentKey( ContentKey contentKey )
    {

        List<ContentIndexEntity> existing =
            findByNamedQuery( ContentIndexEntity.class, "ContentIndexEntity.findByContentKey", "contentKey", contentKey.toInt() );

        for ( ContentIndexEntity contentIndex : existing )
        {
            delete( contentIndex );
        }
        return existing.size();
    }

    public void remove( List<ContentIndexEntity> entities )
    {
        getHibernateTemplate().deleteAll( entities );
    }

    public int removeByCategoryKey( CategoryKey categoryKey )
    {
        return deleteByNamedQuery( "ContentIndexEntity.deleteByCategoryKey", "categoryKey", categoryKey.toInt() );
    }

    public int removeByContentTypeKey( ContentTypeKey contentTypeKey )
    {
        return deleteByNamedQuery( "ContentIndexEntity.deleteByContentTypeKey", "contentTypeKey", contentTypeKey.toInt() );
    }

    public int findCountByContentKey( ContentKey contentKey )
    {
        return findSingleByNamedQuery( Long.class, "ContentIndexEntity.findCountByContentKey", "contentKey",
                                       contentKey.toInt() ).intValue();
    }

    public List<Object[]> findIndexValues( final String query )
    {
        return executeListResult( Object[].class, new HibernateCallback()
        {
            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {
                Query compiled = session.createQuery( query );
                compiled.setCacheable( true );
                return compiled.list();
            }
        } );
    }

    public List<ContentKey> findContentKeysByQuery( final String hqlQuery, final Map<String, Object> parameters, final boolean cacheable )
    {
        return executeListResult( ContentKey.class, new HibernateCallback()
        {

            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {
                Query compiled = session.createQuery( hqlQuery );
                compiled.setCacheable( cacheable );

                for ( String key : parameters.keySet() )
                {
                    Object value = parameters.get( key );
                    if ( value instanceof Date )
                    {
                        compiled.setTimestamp( key, (Date) value );
                    }
                    else if ( value instanceof String )
                    {
                        compiled.setString( key, (String) value );
                    }
                    else if ( value instanceof Boolean )
                    {
                        compiled.setBoolean( key, (Boolean) value );
                    }
                    else if ( value instanceof Long )
                    {
                        compiled.setLong( key, (Long) value );
                    }
                    else if ( value instanceof Integer )
                    {
                        compiled.setInteger( key, (Integer) value );
                    }
                    else if ( value instanceof Byte )
                    {
                        compiled.setByte( key, (Byte) value );
                    }
                    else if ( value instanceof byte[] )
                    {
                        compiled.setBinary( key, (byte[]) value );
                    }
                    else if ( value instanceof Float )
                    {
                        compiled.setFloat( key, (Float) value );
                    }
                    else if ( value instanceof Double )
                    {
                        compiled.setDouble( key, (Double) value );
                    }
                    else if ( value instanceof BigDecimal )
                    {
                        compiled.setBigDecimal( key, (BigDecimal) value );
                    }
                    else if ( value instanceof Short )
                    {
                        compiled.setShort( key, (Short) value );
                    }
                    else if ( value instanceof BigInteger )
                    {
                        compiled.setBigInteger( key, (BigInteger) value );
                    }
                    else if ( value instanceof Character )
                    {
                        compiled.setCharacter( key, (Character) value );
                    }
                    else
                    {
                        compiled.setParameter( key, value );
                    }
                }

                final List result = compiled.list();

                LinkedHashSet<ContentKey> distinctContentKeySet = new LinkedHashSet<ContentKey>( result.size() );

                for ( Object value : result )
                {
                    if ( value instanceof ContentKey )
                    {
                        distinctContentKeySet.add( (ContentKey) value );
                    }
                    else
                    {
                        Object[] valueList = (Object[]) value;
                        distinctContentKeySet.add( ( (ContentKey) valueList[0] ) );
                    }
                }

                List<ContentKey> distinctContentKeyList = new ArrayList<ContentKey>( distinctContentKeySet.size() );
                distinctContentKeyList.addAll( distinctContentKeySet );
                return distinctContentKeyList;
            }
        } );
    }

    public List<ContentIndexEntity> findByContentKey( ContentKey contentKey )
    {
        return findByNamedQuery( ContentIndexEntity.class, "ContentIndexEntity.findByContentKey", "contentKey", contentKey.toInt() );
    }


}

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
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentTypeFilterEntity;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

public class SiteEntityDao
    extends AbstractBaseEntityDao<SiteEntity>
    implements SiteDao
{

    /**
     * Find all sites.
     */
    public List<SiteEntity> findAll()
    {
        return findAll( false );
    }

    /**
     * Find all sites.
     */
    private List<SiteEntity> findAll( boolean deleted )
    {
        return findByNamedQuery( SiteEntity.class, "SiteEntity.findAll" );
    }

    public SiteEntity findByKey( int siteKey )
    {
        return get( SiteEntity.class, new SiteKey( siteKey ) );
    }

    public SiteEntity findByKey( SiteKey siteKey )
    {
        return get( SiteEntity.class, siteKey );
    }

    public Collection<SiteEntity> findByDefaultCss( ResourceKey resourceKey )
    {
        Collection<SiteEntity> allSites = findAll();
        Collection<SiteEntity> cssSites = new ArrayList<SiteEntity>();
        for ( SiteEntity site : allSites )
        {
            ResourceKey defaultCssKey = site.getDefaultCssKey();
            if ( defaultCssKey != null && defaultCssKey.equals( resourceKey ) )
            {
                cssSites.add( site );
            }
        }
        return cssSites;
    }

    private List<SiteEntity> findByDefaultCssPrefix( String prefix )
    {
        Collection<SiteEntity> allSites = findAll();
        List<SiteEntity> cssSites = new ArrayList<SiteEntity>();
        for ( SiteEntity site : allSites )
        {
            ResourceKey defaultCssKey = site.getDefaultCssKey();
            if ( defaultCssKey != null )
            {
                if ( defaultCssKey.toString().startsWith( prefix ) )
                {
                    cssSites.add( site );
                }
            }
        }
        return cssSites;
    }

    public List getResourceUsageCountDefaultCSS()
    {
        List list = new ArrayList();
        Collection<SiteEntity> allSites = findAll();

        for ( SiteEntity site : allSites )
        {
            ResourceKey resourceKey = site.getDefaultCssKey();
            if ( resourceKey != null )
            {
                list.add( new Object[]{resourceKey, 1L} );
            }
        }
        return list;
    }

    public void updateResourceCSSReference( ResourceKey oldResourceKey, ResourceKey newResourceKey )
    {
        Collection<SiteEntity> entityList = findByDefaultCss( oldResourceKey );

        for ( SiteEntity entity : entityList )
        {
            entity.setDefaultCssKey( newResourceKey );
        }
    }

    public void updateResourceCSSReferencePrefix( String oldPrefix, String newPrefix )
    {
        List<SiteEntity> entityList = findByDefaultCssPrefix( oldPrefix );

        for ( SiteEntity entity : entityList )
        {
            String key = entity.getDefaultCssKey().toString();
            key = key.replace( oldPrefix, newPrefix );
            entity.setDefaultCssKey( new ResourceKey( key ) );
        }
    }

    public List<SiteEntity> findByPublishPossible( final int contentTypeKey, final UserEntity user )
    {
        return executeListResult( new HibernateCallback()
        {
            public Object doInHibernate( Session session )
                throws HibernateException, SQLException
            {

                StringBuffer hql = new StringBuffer();
                SelectBuilder sites = new SelectBuilder( hql, 0 );
                sites.addSelect( "sit" );
                sites.addFromTable( SiteEntity.class.getName(), "sit", SelectBuilder.NO_JOIN, null );

                SelectBuilder mei = new SelectBuilder( 3 );
                mei.addSelect( "mei.key" );
                mei.addFromTable( MenuItemEntity.class.getName(), "mei", SelectBuilder.NO_JOIN, null );
                mei.append( "left outer join mei.page.template pat" );
                mei.addFilter( "AND", "mei.site.key = sit.key" );
                mei.addFilter( "AND", "( mei.menuItemType = 6 or pat.type = 6 )" );
                mei.addFilter( "AND", "( mei.section = 1 )" );

                SelectBuilder sctf = new SelectBuilder( 9 );
                sctf.addSelect( "sctf.key" );
                sctf.addFromTable( SectionContentTypeFilterEntity.class.getName(), "sctf", SelectBuilder.NO_JOIN, null );
                sctf.addFilter( "AND", "sctf.contentType.key = :contentTypeKey" );
                sctf.addFilter( "AND", "sctf.section.key = mei.key" );

                mei.addFilter( "AND", "exists (" + sctf.toString() + ")" );

                if ( !user.isEnterpriseAdmin() )
                {
                    Collection<String> groupKeys = resolveUsersAllGroupMemberships( user );

                    if ( groupKeys.isEmpty() )
                    {
                        return new ArrayList<SiteEntity>();
                    }
                    else
                    {
                        SelectBuilder mia = new SelectBuilder( 6 );
                        mia.addSelect( "mia.key.menuItemKey" );
                        mia.addFromTable( MenuItemAccessEntity.class.getName(), "mia", SelectBuilder.NO_JOIN, null );
                        mia.addFilter( "AND", "mia.addAccess = 1" );
                        mia.addFilter( "AND", "mia.key.menuItemKey = mei.key" );
                        mia.addFilter( "AND", new InClauseBuilder<String>( "mia.key.groupKey", groupKeys )
                        {
                            public void appendValue( StringBuffer sql, String value )
                            {
                                sql.append( "'" ).append( value ).append( "'" );
                            }
                        }.toString() );
                        mei.addFilter( "AND", "exists (" + mia.toString() + ")" );
                    }
                }

                sites.addFilter( "AND", "exists (" + mei.toString() + ")" );

                Query compiled = session.createQuery( hql.toString() );
                compiled.setCacheable( false );
                compiled.setInteger( "contentTypeKey", contentTypeKey );
                return compiled.list();
            }
        } );
    }

    private Collection<String> resolveUsersAllGroupMemberships( UserEntity user )
    {

        List<String> groupKeys = new ArrayList<String>();
        GroupEntity userGroup = user.getUserGroup();
        if ( userGroup == null )
        {
            return groupKeys;
        }
        groupKeys.add( userGroup.getGroupKey().toString() );
        for ( GroupEntity group : userGroup.getAllMemberships() )
        {
            groupKeys.add( group.getGroupKey().toString() );
        }
        return groupKeys;
    }

    private List<SiteEntity> executeListResult( HibernateCallback callback )
    {
        return (List<SiteEntity>) getHibernateTemplate().execute( callback );
    }

    public EntityPageList<SiteEntity> findAll( int index, int count )
    {
        return findPageList( SiteEntity.class, null, index, count );
    }
}

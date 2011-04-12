/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.content.*;
import com.enonic.cms.core.content.category.CategoryEntity;
import org.hibernate.Query;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.RelatedContentEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedParentContent;

public class ContentEntityDao
    extends AbstractBaseEntityDao<ContentEntity>
    implements ContentDao
{

    public ContentEntity findByKey( ContentKey contentKey )
    {
        return get( ContentEntity.class, contentKey );
    }


    @SuppressWarnings("unchecked")
    public List<ContentKey> findBySpecification( ContentSpecification specification, String orderBy, int count )
    {

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(
            getContentKeysHQL( specification, orderBy, false ) );

        compiled.setCacheable( true );
        compiled.setMaxResults( count );

        @SuppressWarnings({"unchecked"}) List<ContentKey> list = compiled.list();

        List<ContentKey> contentKeys = new ArrayList<ContentKey>();
        for ( ContentKey row : list )
        {
            contentKeys.add( row );
        }

        return contentKeys;
    }

    private String getContentKeysHQL( ContentSpecification specification, String orderBy, boolean count )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );

        if ( count )
        {
            hqlQuery.addSelect( "count (c.key) " );
        }
        else
        {
            hqlQuery.addSelect( "c.key" );
        }
        hqlQuery.addFromTable( "ContentEntity", "c", SelectBuilder.NO_JOIN, null );

        applyIsDeletedFilter( specification, hqlQuery );

        applyAssignedToContentsHql( specification, hqlQuery );

        if ( orderBy != null )
        {
            hqlQuery.addOrderBy( orderBy );
        }

        return hqlQuery.toString();
    }

    private void applyAssignedToContentsHql( ContentSpecification specification, SelectBuilder hqlQuery )
    {
        hqlQuery.addFilter( "AND", "c.assignee = '" + specification.getAssignee().getKey().toString() + "'" );

        if ( specification.assignedDraftsOnly() )
        {
            hqlQuery.addFilter( "AND", "c.draftVersion != null" );
        }
    }

    private void applyIsDeletedFilter( ContentSpecification specification, final SelectBuilder hqlQuery )
    {
        if ( !specification.doIncludeDeleted() )
        {
            hqlQuery.addFilter( "AND", "c.deleted = 0" );
        }
    }

    public Collection<RelatedChildContent> findRelatedChildrenByKeys( List<ContentVersionKey> contentVersionKeys )
    {
        if ( contentVersionKeys == null || contentVersionKeys.size() == 0 )
        {
            throw new IllegalArgumentException( "Given contentVersionKeys must contain values" );
        }

        String hql = getRelatedChildrenByKeyHQL( contentVersionKeys );

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( true );

        @SuppressWarnings({"unchecked"}) List<Object[]> list = compiled.list();

        List<RelatedChildContent> relatedChildContrents = new ArrayList<RelatedChildContent>();
        for ( Object[] row : list )
        {
            ContentVersionKey versionKey = (ContentVersionKey) row[0];
            ContentEntity content = (ContentEntity) row[1];
            RelatedChildContent relatedChildContent = new RelatedChildContent( versionKey, content );
            relatedChildContrents.add( relatedChildContent );
        }

        return relatedChildContrents;
    }

    public Collection<RelatedParentContent> findRelatedParentByKeys( List<ContentKey> contentKeys, boolean includeOnlyMainVersions )
    {
        if ( contentKeys == null || contentKeys.size() == 0 )
        {
            throw new IllegalArgumentException( "Given contentKeys must contain values" );
        }

        String hql = getRelatedParentsByKeyHQL( contentKeys, includeOnlyMainVersions );

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( true );

        @SuppressWarnings({"unchecked"}) List<Object[]> list = compiled.list();

        List<RelatedParentContent> relatedChildContents = new ArrayList<RelatedParentContent>();
        for ( Object[] row : list )
        {
            ContentKey contentKey = (ContentKey) row[0];
            ContentEntity content = (ContentEntity) row[1];
            RelatedParentContent relatedParentContent = new RelatedParentContent( contentKey, content );
            relatedChildContents.add( relatedParentContent );
        }

        return relatedChildContents;
    }

    public List<ContentKey> findContentKeysByContentType( ContentTypeEntity contentType )
    {
        return findByNamedQuery( ContentKey.class, "ContentEntity.findContentKeysByContentTypeKey", new String[]{"contentTypeKey"},
                                 new Object[]{contentType.getKey()} );
    }

    public List<ContentKey> findContentKeysByCategory( CategoryEntity category )
    {
        return findByNamedQuery( ContentKey.class, "ContentEntity.findContentKeysByCategoryKey", new String[]{"categoryKey"},
                                 new Object[]{category.getKey()} );
    }

    public int getNumberOfRelatedParentsByKey( List<ContentKey> contentKeys )
    {
        return doGetNumberOfRelatedParentsByKey( contentKeys );
    }

    private int doGetNumberOfRelatedParentsByKey( List<ContentKey> contentKeys )
    {
        String hql = getNumberOfRelatedParentsByKeyHQL( contentKeys );

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( true );

        @SuppressWarnings({"unchecked"}) int count = ( (Number) compiled.uniqueResult() ).intValue();

        return count;
    }

    private String getRelatedChildrenByKeyHQL( List<ContentVersionKey> contentVersionKeys )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "rc.key.parentContentVersionKey" );
        hqlQuery.addSelectColumn( "c" );
        hqlQuery.addFromTable( RelatedContentEntity.class.getName(), "rc", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( ContentEntity.class.getName(), "c", SelectBuilder.NO_JOIN, null );
        // hqlQuery.addFromTable( "c.mainVersion", null, SelectBuilder.LEFT_JOIN_FETCH, null );
        // hqlQuery.addFromTable( "c.sectionContents", null, SelectBuilder.LEFT_JOIN_FETCH, null );
        // hqlQuery.addFromTable( "c.contentHomes", null, SelectBuilder.LEFT_JOIN_FETCH, null );
        hqlQuery.addFilter( "AND", "rc.key.childContentKey = c.key" );
        hqlQuery.addFilter( "AND", new InClauseBuilder<ContentVersionKey>( "rc.key.parentContentVersionKey", contentVersionKeys )
        {
            public void appendValue( StringBuffer sql, ContentVersionKey value )
            {
                sql.append( value.toString() );
            }
        }.toString() );
        hqlQuery.addFilter( "AND", "c.deleted = 0" );
        hqlQuery.addOrderBy( "c.createdAt" );
        return hqlQuery.toString();
    }

    private String getRelatedParentsByKeyHQL( List<ContentKey> contentKeys, boolean includeOnlyMainVersions )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "rc.key.childContentKey" );
        hqlQuery.addSelectColumn( "c" );
        hqlQuery.addFromTable( ContentVersionEntity.class.getName(), "cv", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( RelatedContentEntity.class.getName(), "rc", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( ContentEntity.class.getName(), "c", SelectBuilder.NO_JOIN, null );
        // hqlQuery.addFromTable( "c.mainVersion", null, SelectBuilder.LEFT_JOIN_FETCH, null );
        // hqlQuery.addFromTable( "c.sectionContents", null, SelectBuilder.LEFT_JOIN_FETCH, null );
        // hqlQuery.addFromTable( "c.contentHomes", null, SelectBuilder.LEFT_JOIN_FETCH, null );

        if ( includeOnlyMainVersions )
        {
            hqlQuery.addFilter( "AND", "c.mainVersion.key = cv.key" );
        }
        else
        {
            hqlQuery.addFilter( "AND", "c.key = cv.content.key" );
        }

        hqlQuery.addFilter( "AND", "cv.key = rc.key.parentContentVersionKey" );

        hqlQuery.addFilter( "AND", new InClauseBuilder<ContentKey>( "rc.key.childContentKey", contentKeys )
        {
            public void appendValue( StringBuffer sql, ContentKey value )
            {
                sql.append( value.toString() );
            }
        }.toString() );

        hqlQuery.addFilter( "AND", "c.deleted = 0" );
        hqlQuery.addOrderBy( "c.createdAt" );
        return hqlQuery.toString();
    }

    public String getNumberOfRelatedParentsByKeyHQL( List<ContentKey> contentKeys )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "count(*)" );
        hqlQuery.addFromTable( ContentVersionEntity.class.getName(), "cv", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( RelatedContentEntity.class.getName(), "rc", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( ContentEntity.class.getName(), "c", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFilter( "AND", "cv.key = rc.key.parentContentVersionKey" );
        hqlQuery.addFilter( "AND", "c.mainVersion.key = cv.key" );
        hqlQuery.addFilter( "AND", new InClauseBuilder<ContentKey>( "rc.key.childContentKey", contentKeys )
        {
            public void appendValue( StringBuffer sql, ContentKey value )
            {
                sql.append( value.toString() );
            }
        }.toString() );

        hqlQuery.addFilter( "AND", "c.deleted = 0" );
        return hqlQuery.toString();
    }

    public int findCountBySpecification( ContentSpecification specification )
    {
        List<Long> result = getHibernateTemplate().find( getContentKeysHQL( specification, null, true ) );

        if ( result == null || result.size() == 0 )
        {
            return 0;
        }

        Long count = result.get( 0 );

        return count.intValue();

    }

    public List<ContentKey> findAll()
    {
        return findByNamedQuery( ContentKey.class, "ContentEntity.findAll" );
    }

    public EntityPageList<ContentEntity> findAll( int index, int count )
    {
        return findPageList( ContentEntity.class, "x.deleted = 0", index, count );
    }
}

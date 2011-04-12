/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.ContentVersionSpecification;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.security.group.GroupKey;
import org.hibernate.Query;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.category.CategoryAccessEntity;

public class ContentVersionEntityDao
    extends AbstractBaseEntityDao<ContentVersionEntity>
    implements ContentVersionDao
{

    public ContentVersionEntity findByKey( ContentVersionKey key )
    {
        return get( ContentVersionEntity.class, key );
    }

    @SuppressWarnings("unchecked")
    public List<ContentVersionKey> findBySpecification( ContentVersionSpecification specification, String orderBy, int count )
    {
        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(
            getContentVersionKeysHQL( specification, orderBy, false ) );
        compiled.setCacheable( true );
        compiled.setMaxResults( count );

        @SuppressWarnings({"unchecked"}) List<ContentVersionKey> list = compiled.list();

        List<ContentVersionKey> versionKeys = new ArrayList<ContentVersionKey>();
        for ( ContentVersionKey row : list )
        {
            versionKeys.add( row );
        }

        return versionKeys;
    }

    private String getContentVersionKeysHQL( ContentVersionSpecification specification, String orderBy, boolean count )
    {

        final SelectBuilder hqlQuery = new SelectBuilder( 0 );

        if ( count )
        {
            hqlQuery.addSelect( "count (cv.key) " );
        }
        else
        {
            hqlQuery.addSelect( "cv.key" );
        }
        hqlQuery.addFromTable( ContentVersionEntity.class.getName(), "cv", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFilter( "AND", "cv.content.deleted = 0" );
        applyStatusFilter( specification, hqlQuery );

        applyModifiedByFilter( specification, hqlQuery );

        applyCategoryAccessFilter( specification, hqlQuery );

        if ( orderBy != null )
        {
            hqlQuery.addOrderBy( "cv." + orderBy );
        }

        return hqlQuery.toString();
    }

    private void applyCategoryAccessFilter( ContentVersionSpecification specification, final SelectBuilder hqlQuery )
    {
        if ( specification.getCategoryAccessTypeFilter() != null )
        {
            SelectBuilder categorySelect = createCategoryAccessFilter( hqlQuery, specification );
            hqlQuery.addFilter( "AND", "cv.content.category.key IN ( " + categorySelect.toString() + " )" );

        }
    }

    private void applyModifiedByFilter( ContentVersionSpecification specification, final SelectBuilder hqlQuery )
    {
        if ( specification.getModifier() != null )
        {
            hqlQuery.addFilter( "AND", "cv.modifiedBy = '" + specification.getModifier().toString() + "'" );
        }
    }

    private void applyStatusFilter( ContentVersionSpecification specification, final SelectBuilder hqlQuery )
    {
        if ( specification.getContentStatus() != null )
        {
            hqlQuery.addFilter( "AND", "cv.status = " + specification.getContentStatus() );
        }
    }

    private SelectBuilder createCategoryAccessFilter( final SelectBuilder hqlQuery, ContentVersionSpecification specification )
    {
        SelectBuilder categorySelect = new SelectBuilder( 1 );
        categorySelect.addSelect( "ca.key.categoryKey" );
        categorySelect.addFromTable( CategoryAccessEntity.class.getName(), "ca", SelectBuilder.NO_JOIN, null );
        categorySelect.startFilterGroup( "AND" );
        addAccessTypes( specification, categorySelect );
        categorySelect.endFilterGroup();

        applySecurityFilter( specification, categorySelect );

        return categorySelect;

    }

    private void addAccessTypes( ContentVersionSpecification specification, SelectBuilder categorySelect )
    {
        for ( CategoryAccessType accessType : specification.getCategoryAccessTypeFilter() )
        {
            switch ( accessType )
            {
                case READ:
                    categorySelect.addFilter( specification.getCategoryAccessTypeFilterPolicy().toString(), "ca.readAccess = 1" );
                    break;
                case ADMIN_BROWSE:
                    categorySelect.addFilter( specification.getCategoryAccessTypeFilterPolicy().toString(), "ca.adminBrowseAccess = 1" );
                    break;
                case APPROVE:
                    categorySelect.addFilter( specification.getCategoryAccessTypeFilterPolicy().toString(), "ca.publishAccess = 1" );
                    break;
                case CREATE:
                    categorySelect.addFilter( specification.getCategoryAccessTypeFilterPolicy().toString(), "ca.createAccess = 1" );
                    break;
                case ADMINISTRATE:
                    categorySelect.addFilter( specification.getCategoryAccessTypeFilterPolicy().toString(), "ca.adminAccess = 1" );
                    break;
                default:
                    break;
            }
        }
    }

    private void applySecurityFilter( ContentVersionSpecification specification, SelectBuilder categorySelect )
    {

        if ( specification.getSecurityFilter() != null && specification.getSecurityFilter().size() > 0 )
        {
            String groupKeyFilter = new InClauseBuilder<GroupKey>( "ca.key.groupKey", specification.getSecurityFilter() )
            {
                public void appendValue( StringBuffer sql, GroupKey value )
                {
                    sql.append( "'" ).append( value ).append( "'" );
                }
            }.toString();
            categorySelect.addFilter( "AND", groupKeyFilter );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see com.enonic.cms.store.dao.ContentVersionDao#findCountBySpecification(com.enonic.cms.core.content.ContentVersionSpecification)
     */
    public int findCountBySpecification( ContentVersionSpecification specification )
    {
        List<Long> result = getHibernateTemplate().find( getContentVersionKeysHQL( specification, null, true ) );

        if ( result == null || result.size() == 0 )
        {
            return 0;
        }

        Long count = result.get( 0 );

        return count.intValue();

    }

}
/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentKey;

/**
 * This class implements the content query.
 */
public final class ContentIndexQuery
    extends AbstractQuery
{

    public enum SectionFilterStatus
    {
        ANY,
        APPROVED_ONLY,
        UNAPPROVED_ONLY
    }

    public enum CategoryAccessTypeFilterPolicy
    {
        AND,
        OR
    }

    private SectionFilterStatus sectionFilterStatus;

    private CategoryAccessTypeFilterPolicy categoryAccessTypeFilterPolicy;

    private final String query;

    /**
     * Content key filter.
     */
    private Collection<ContentKey> contentFilter;

    /**
     * Section key filter
     */
    private Collection<MenuItemEntity> sectionFilter;

//    private boolean approvedSectionContentOnly = true;
//    private boolean unapprovedSectionContentOnly = false;

    private int index = 0;

    private int count = Integer.MAX_VALUE;

    private Integer contentStatusFilter;

    private DateTime contentOnlineAtFilter;

    private Collection<CategoryAccessType> categoryAccessTypeFilter;


    /**
     * Construct the query.
     *
     * @param queryWithoutOrderBy A user defined query, similar to the WHERE part of an SQL query.
     * @param orderBy             A user defined order by cluase, similar to the ORDER BY part of an SQL query.
     * @throws IllegalQueryException If the query can not be parsed.
     */
    public ContentIndexQuery( String queryWithoutOrderBy, String orderBy )
    {

        if ( queryWithoutOrderBy == null )
        {
            queryWithoutOrderBy = "";
        }

        if ( ( orderBy != null ) && ( orderBy.length() > 0 ) )
        {
            queryWithoutOrderBy = queryWithoutOrderBy + " ORDER BY " + orderBy;
        }

        this.query = queryWithoutOrderBy.trim();

        validateFullTextQuery( query );
    }

    public ContentIndexQuery( String query )
    {
        this.query = query;
        validateFullTextQuery( query );
    }

    private void validateFullTextQuery( String query )
    {

        Pattern pattern = Pattern.compile( ".*fulltext CONTAINS \"(.*?)\".*" );
        Matcher matcher = pattern.matcher( query );
        if ( !matcher.matches() )
        {
            return;
        }

        String fullTextSearchPattern = matcher.group( 1 ).trim();

        if ( fullTextSearchPattern != null && fullTextSearchPattern.length() < 3 )
        {
            throw new IllegalQueryException( "Fulltext search with less than 3 characters not allowed", query );
        }

    }

    /**
     * Return the query.
     *
     * @return The query supplied query.
     */
    public String getQuery()
    {
        return this.query;
    }

    public int getIndex()
    {
        return this.index;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }

    public int getCount()
    {
        return this.count;
    }

    public void setCount( int count )
    {
        if ( count <= 0 )
        {
            throw new IllegalArgumentException( "Count must be larger than zero" );
        }
        this.count = count;
    }

    public Collection<ContentKey> getContentFilter()
    {
        return contentFilter;
    }

    public void setContentFilter( Collection<ContentKey> filter )
    {

        contentFilter = filter;
    }


    public Collection<MenuItemEntity> getSectionFilter()
    {
        return sectionFilter;
    }

    public boolean isSectionFilter()
    {
//        return sectionFilter != null && sectionFilter.size() > 0;
        return ( sectionFilterStatus != null );
    }

    public void setSectionFilter( Collection<MenuItemEntity> filter, final SectionFilterStatus filterStatus )
    {

        this.sectionFilter = filter;
        this.sectionFilterStatus = filterStatus;

//        if ( approvedSectionContentOnly == true && unapprovedSectionContentOnly == true )
//        {
//            throw new IllegalArgumentException (
//        "Illegal section filter. approvedSectionContentOnly and unapprovedSectionContentOnly cannot both be true" );
//        }
//
//        this.approvedSectionContentOnly = approvedSectionContentOnly;
//        this.unapprovedSectionContentOnly = unapprovedSectionContentOnly;
    }

    public boolean isApprovedSectionContentOnly()
    {
        if ( sectionFilterStatus == SectionFilterStatus.APPROVED_ONLY )
        {
            return true;
        }
        return false;
    }

    public boolean isUnapprovedSectionContentOnly()
    {
        if ( sectionFilterStatus == SectionFilterStatus.UNAPPROVED_ONLY )
        {
            return true;
        }
        return false;
    }

    public void setContentStatusFilter( Integer contentStatus )
    {
        this.contentStatusFilter = contentStatus;
    }

    public Integer getContentStatusFilter()
    {
        return this.contentStatusFilter;
    }

    public DateTime getContentOnlineAtFilter()
    {
        return contentOnlineAtFilter;
    }

    public void setContentOnlineAtFilter( Date value )
    {
        this.contentOnlineAtFilter = new DateTime( value );
    }

    public Collection<CategoryAccessType> getCategoryAccessTypeFilter()
    {
        return categoryAccessTypeFilter;
    }

    public void setCategoryAccessTypeFilter( Collection<CategoryAccessType> value, CategoryAccessTypeFilterPolicy policy )
    {
        if ( value != null && policy == null )
        {
            throw new IllegalArgumentException( "categoryAccessTypeFilter requries a valid policy, and cannot be NULL " );
        }

        this.categoryAccessTypeFilter = value;
        this.categoryAccessTypeFilterPolicy = policy;
    }

    @Override
    public String toString()
    {
        ToStringBuilder s = new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE );
        s.append( "index", index );
        s.append( "count", count );
        s.append( "query", query );
        s.append( "categoryAccessTypeFilter", categoryAccessTypeFilter );
        s.append( "approvedSectionContentOnly", sectionFilterStatus );
        s.append( "contentStatusFilter", contentStatusFilter );
        s.append( "contentOnlineAtFilter", contentOnlineAtFilter );
        s.append( "contentFilter", contentFilter );
        s.append( "sectionFilter", sectionFilter );
        s.append( "categoryFilter", getCategoryFilter() );
        s.append( "contentTypeFilter", getContentTypeFilter() );
        s.append( "securityFilter", getSecurityFilter() );
        return s.toString();
    }

    public CategoryAccessTypeFilterPolicy getCategoryAccessTypeFilterPolicy()
    {
        return categoryAccessTypeFilterPolicy;
    }
}
/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator;

import java.util.Collection;
import java.util.Map;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;
import com.enonic.cms.framework.jdbc.dialect.Dialect;

import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQueryExprParser;
import com.enonic.cms.core.content.index.TranslatedQuery;
import com.enonic.cms.core.content.index.optimizer.LogicalOrOptimizer;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;
import com.enonic.cms.core.content.index.translator.expression.ColumnName;
import com.enonic.cms.core.content.index.translator.expression.ExpressionTranslator;
import com.enonic.cms.core.content.index.translator.expression.FieldName;
import com.enonic.cms.core.content.index.translator.expression.OrderByExprTranslator;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;

/**
 * This class implements the translation of content query.
 */
public final class ContentQueryTranslator
    extends AbstractQueryTranslator
{
    private static final String CONTENTINDEXENTITY_CLASSNAME = ContentIndexEntity.class.getName();

    private static final String SECTIONCONTENTENITTY_CLASSNAME = SectionContentEntity.class.getName();

    private final Dialect dialect;

    public ContentQueryTranslator( Dialect dialect )
    {
        this.dialect = dialect;
    }

    public TranslatedQuery translate( ContentIndexQuery query )
    {
        String generalSubSelectFilter = createCommonFilterHQL( query, null );

        // This code block must be run before everyting else...
        QueryExpr expr = ContentIndexQueryExprParser.parse( query );

        Expression optimizedExpr = new LogicalOrOptimizer().optimize( expr.getExpr() );

        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( parameters );
        expressionTranslator.setValueSubSelectFilter( generalSubSelectFilter );
        expressionTranslator.init();
        OrderByExprTranslator orderByExprTranslator = new OrderByExprTranslator();

//        String translatedExpr = expressionTranslator.translate( expr.getExpr() );
        String translatedExpr = expressionTranslator.translate( optimizedExpr );
        String translatedOrder = orderByExprTranslator.translate( expr.getOrderBy() );

        final SelectBuilder hqlQuery = new SelectBuilder( 0 );

        final Collection<MenuItemEntity> sectionFilter = query.getSectionFilter();
        final boolean filterSections = query.isSectionFilter();
        boolean orderedSection = false;
        if ( sectionFilter != null && sectionFilter.size() == 1 )
        {
            orderedSection = sectionFilter.iterator().next().isOrderedSection();
        }

        hqlQuery.addSelect( "DISTINCT(x.contentKey)" );

        // add more columns if any
        for ( ColumnName columnName : orderByExprTranslator.getSelectList() )
        {
            hqlQuery.addSelectColumn( columnName.toString() );
        }

        if ( orderedSection )
        {
            hqlQuery.addSelectColumn( "sc.menuItem.key" );
            hqlQuery.addSelectColumn( "sc.order" );
        }

        // ** FROM tables...
        hqlQuery.addFromTable( CONTENTINDEXENTITY_CLASSNAME, "x", SelectBuilder.NO_JOIN, null );
        if ( filterSections )
        {
            hqlQuery.addFromTable( SECTIONCONTENTENITTY_CLASSNAME, "sc", SelectBuilder.NO_JOIN, null );
        }
        for ( String tableAlias : expressionTranslator.getTableAliases().keySet() )
        {
            hqlQuery.addFromTable( CONTENTINDEXENTITY_CLASSNAME, tableAlias, SelectBuilder.NO_JOIN, null );
        }
        for ( String orderByTableAlias : orderByExprTranslator.getTableAliasKeySet() )
        {
            hqlQuery.addFromTable( CONTENTINDEXENTITY_CLASSNAME, orderByTableAlias, SelectBuilder.NO_JOIN, null );
        }

        // ** WHERE filters...

        String aliasFilter = createTableJoins( expressionTranslator.getTableAliases() );
        if ( aliasFilter != null )
        {
            hqlQuery.addFilter( "AND", aliasFilter );
        }

        if ( !orderByExprTranslator.getTableAliases().isEmpty() )
        {
            Map<String, FieldName> orderByTableAliases = orderByExprTranslator.getTableAliases();
            String orderByPathFilter = createPathRestrictionsForOrderByExpressions( orderByTableAliases, query );
            hqlQuery.addFilter( "AND", orderByPathFilter );
        }

        if ( translatedExpr != null )
        {
            hqlQuery.addFilter( "AND", "(" + translatedExpr + ")" );
        }

        applyContentStatusFilter( hqlQuery, query.getContentStatusFilter() );
        applyContentPublishedAtFilter( hqlQuery, query.getContentOnlineAtFilter() );
        applyContentFilter( hqlQuery, query.getContentFilter() );
        applyCategoryFilter( hqlQuery, query.getCategoryFilter() );
        applyContentTypeFilter( hqlQuery, query.getContentTypeFilter() );
        applyCategoryAccessTypeFilter( query, hqlQuery );
        applySecurityFilter( hqlQuery, query.getSecurityFilter() );

        if ( filterSections )
        {

            hqlQuery.addFilter( "AND", "sc.content.key = x.contentKey" );

            if ( sectionFilter != null && sectionFilter.size() > 0 )
            {
                InClauseBuilder sectionKeyInClause = new InClauseBuilder<MenuItemEntity>( "sc.menuItem.key", sectionFilter )
                {
                    public void appendValue( StringBuffer sql, MenuItemEntity section )
                    {
                        sql.append( section.getKey() );
                    }
                };
                hqlQuery.addFilter( "AND", sectionKeyInClause.toString() );
            }

            if ( query.isApprovedSectionContentOnly() )
            {
                hqlQuery.addFilter( "AND", "sc.approved = 1" );
            }
            if ( query.isUnapprovedSectionContentOnly() )
            {
                hqlQuery.addFilter( "AND", "sc.approved = 0" );
            }
        }

        if ( translatedOrder != null )
        {
            hqlQuery.addOrderBy( translatedOrder );
        }
        else if ( orderedSection )
        {
            hqlQuery.addOrderBy( "sc.menuItem.key, sc.order" );
        }

        return new TranslatedQuery( hqlQuery.toString(), query.getIndex(), query.getCount(), parameters );
    }

    private String createPathRestrictionsForOrderByExpressions( Map<String, FieldName> orderByTableAliases, ContentIndexQuery contentQuery )
    {
        StringBuffer str = new StringBuffer();
        boolean first = true;
        for ( Map.Entry<String, FieldName> entry : orderByTableAliases.entrySet() )
        {
            if ( first )
            {
                first = false;
            }
            else
            {
                str.append( " AND " );
            }

            String tableAliasName = entry.getKey();
            FieldName fieldName = entry.getValue();
            str.append( tableAliasName ).append( ".contentKey = " ).append( "x.contentKey" ).append( " AND " );
            str.append( tableAliasName ).append( ".path = '" ).append( fieldName.getTranslatedFieldName() ).append( "'" );
            final String commonFilter = createCommonFilterHQL( contentQuery, tableAliasName );
            if ( !StringUtils.isBlank( commonFilter ) )
            {
                str.append( " AND " ).append( commonFilter );
            }
        }
        return str.toString();
    }

    protected void applyCategoryAccessTypeFilter( final ContentIndexQuery query, final SelectBuilder hqlQuery )
    {
        applyCategoryAccessTypeFilter( query, hqlQuery, "x" );
    }

    protected void applyCategoryAccessTypeFilter( final ContentIndexQuery query, final SelectBuilder hqlQuery, final String tableAlias )
    {

        if ( query.getCategoryAccessTypeFilter() != null && query.hasSecurityFilter() )
        {
            // cannot apply category access type filter without security filter

            SelectBuilder categorySelect = new SelectBuilder( 1 );
            categorySelect.addSelect( "ca.key.categoryKey" );
            categorySelect.addFromTable( CategoryAccessEntity.class.getName(), "ca", SelectBuilder.NO_JOIN, null );
            categorySelect.startFilterGroup( "AND" );

            for ( CategoryAccessType accessType : query.getCategoryAccessTypeFilter() )
            {
                switch ( accessType )
                {
                    case READ:
                        categorySelect.addFilter( query.getCategoryAccessTypeFilterPolicy().toString(), "ca.readAccess = 1" );
                        break;
                    case ADMIN_BROWSE:
                        categorySelect.addFilter( query.getCategoryAccessTypeFilterPolicy().toString(), "ca.adminBrowseAccess = 1" );
                        break;
                    case APPROVE:
                        categorySelect.addFilter( query.getCategoryAccessTypeFilterPolicy().toString(), "ca.publishAccess = 1" );
                        break;
                    case CREATE:
                        categorySelect.addFilter( query.getCategoryAccessTypeFilterPolicy().toString(), "ca.createAccess = 1" );
                        break;
                    case ADMINISTRATE:
                        categorySelect.addFilter( query.getCategoryAccessTypeFilterPolicy().toString(), "ca.adminAccess = 1" );
                        break;
                }
            }
            categorySelect.endFilterGroup();

            if ( query.getSecurityFilter() == null || query.getSecurityFilter().size() == 0 )
            {
                throw new IllegalStateException( "Expected security filter to be set" );
            }

            String groupKeyFilter = new InClauseBuilder<GroupKey>( "ca.key.groupKey", query.getSecurityFilter() )
            {
                public void appendValue( StringBuffer sql, GroupKey value )
                {
                    sql.append( "'" ).append( value ).append( "'" );
                }
            }.toString();
            categorySelect.addFilter( "AND", groupKeyFilter );

            //hql.append("\n)");

            hqlQuery.addFilter( "AND", tableAlias + ".categoryKey IN ( " + categorySelect.toString() + " )" );
        }
    }

    protected void applyContentStatusFilter( SelectBuilder hqlQuery, Integer contentStatus )
    {
        if ( contentStatus != null )
        {

            hqlQuery.addFilter( "AND", "x.contentStatus = :contentStatus" );

            parameters.put( "contentStatus", contentStatus );
        }

    }


    protected void applyContentPublishedAtFilter( SelectBuilder hqlQuery, DateTime dateTime )
    {
        if ( dateTime != null )
        {
            DateTime dateTimeRoundedDownToNearestMinute = dateTime.minuteOfHour().roundFloorCopy();

            if ( ( this.dialect != null ) && this.dialect.isInlineTimestampForSpeed() )
            {
                hqlQuery.addFilter( "AND", "x.contentPublishFrom <= " +
                    this.dialect.formatTimestamp( dateTimeRoundedDownToNearestMinute.getMillis() ) );
                hqlQuery.addFilter( "AND", "(x.contentPublishTo IS null OR x.contentPublishTo > " +
                    this.dialect.formatTimestamp( dateTimeRoundedDownToNearestMinute.getMillis() ) + ")" );
            }
            else
            {
                hqlQuery.addFilter( "AND", "x.contentPublishFrom <= :publishFromDate" );
                hqlQuery.addFilter( "AND", "(x.contentPublishTo IS null OR x.contentPublishTo > :publishToDate)" );
                parameters.put( "publishFromDate", dateTimeRoundedDownToNearestMinute.toDate() );
                parameters.put( "publishToDate", dateTimeRoundedDownToNearestMinute.toDate() );
            }
        }
    }

    private String createTableJoins( Map<String, FieldName> tableAliases )
    {
        if ( tableAliases.isEmpty() )
        {
            return null;
        }

        StringBuffer sBuf = new StringBuffer();
        boolean append = false;
        for ( String key : tableAliases.keySet() )
        {
            append = createTableJoinCondition( sBuf, append, key );
        }

        return sBuf.toString();
    }

    private boolean createTableJoinCondition( StringBuffer str, boolean append, String tableAlias )
    {
        if ( append )
        {
            str.append( " \nAND " );
        }

        str.append( tableAlias ).append( ".contentKey = " ).append( "x.contentKey" );
        return true;
    }

    protected void applySecurityFilter( final SelectBuilder hqlQuery, final Collection<GroupKey> groupKeys )
    {
        appendSecurityFilterQuery( hqlQuery, "x", groupKeys );
    }

    protected void applyContentTypeFilter( final SelectBuilder hqlQuery, final Collection<ContentTypeKey> contentTypeKeys )
    {
        appendFilterQuery( hqlQuery, "x.contentTypeKey", contentTypeKeys );
    }

    protected void applyContentFilter( final SelectBuilder hqlQuery, final Collection<ContentKey> contentKeys )
    {
        appendFilterQuery( hqlQuery, "x.contentKey", contentKeys );
    }

    protected void applyCategoryFilter( final SelectBuilder hqlQuery, final Collection<CategoryKey> categoryKeys )
    {
        appendFilterQuery( hqlQuery, "x.categoryKey", categoryKeys );
    }

    private String createCommonFilterHQL( final ContentIndexQuery query, final String tableAlias )
    {
        String columnPrefix = "";
        if ( tableAlias != null )
        {
            columnPrefix = tableAlias + ".";
        }

        StringBuffer str = new StringBuffer();
        appendFilterQuery( str, columnPrefix + "contentKey", query.getContentFilter() );
        if ( query.getCategoryFilterSize() < 100 )
        {
            appendFilterQuery( str, columnPrefix + "categoryKey", query.getCategoryFilter() );
        }
        appendFilterQuery( str, columnPrefix + "contentTypeKey", query.getContentTypeFilter() );
        return str.toString();
    }

    private void appendFilterQuery( StringBuffer query, String columnName, Collection keys )
    {
        if ( keys != null && keys.size() > 0 )
        {

            InClauseBuilder inClauseFilter = new InClauseBuilder<Object>( columnName, keys )
            {
                public void appendValue( StringBuffer sql, Object value )
                {
                    sql.append( value );
                }
            };

            if ( query.length() > 0 )
            {
                query.append( " AND " );
            }

            query.append( inClauseFilter.toString() );
        }
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.index.AggregatedQuery;
import com.enonic.cms.core.content.index.TranslatedQuery;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.index.FieldHelper;

/**
 * This class implements the aggregated index value query translator.
 */
public final class AggregatedQueryTranslator
    extends AbstractQueryTranslator
{

    public TranslatedQuery translate( AggregatedQuery query )
    {

        SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "count(x.value), min(x.numValue), max(x.numValue), sum(x.numValue), avg(x.numValue)" );
        hqlQuery.addFromTable( ContentIndexEntity.class.getName(), "x", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFilter( "AND", "x.path = '" + FieldHelper.translateFieldName( query.getField() ) + "'" );
        hqlQuery.addFilter( "AND", "x.numValue IS NOT NULL" );

        appendFilterQuery( hqlQuery, "x.categoryKey", query.getCategoryFilter() );
        appendFilterQuery( hqlQuery, "x.contentTypeKey", query.getContentTypeFilter() );
        appendSecurityFilterQuery( hqlQuery, "x", query.getSecurityFilter() );

        /*
        StringBuffer str = new StringBuffer();
        str.append("SELECT count(x.value), ");
        str.append("min(x.numValue), max(x.numValue), sum(x.numValue), avg(x.numValue)");
        str.append(" FROM ").append(ContentIndexEntity.class.getName()).append(" AS x WHERE ");
        str.append("x.path = '").append(FieldHelper.translateFieldName(query.getField())).append("'");
        str.append(" AND x.numValue IS NOT NULL");

        String filter = translateFilterExpr(query);

        if (filter != null) {
            str.append(" AND ").append(filter);
        }
        */

        return new TranslatedQuery( hqlQuery.toString(), 0, 0, null );
    }


}

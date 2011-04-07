/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.domain.content.ContentIndexEntity;
import com.enonic.cms.domain.content.index.FieldHelper;
import com.enonic.cms.domain.content.index.IndexValueQuery;
import com.enonic.cms.domain.content.index.TranslatedQuery;
import com.enonic.cms.domain.content.index.translator.AbstractQueryTranslator;

/**
 * This class implements the index value query translator.
 */
public final class IndexValueQueryTranslator
    extends AbstractQueryTranslator
{

    public TranslatedQuery translate( IndexValueQuery query )
    {

        SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "x.contentKey, x.value" );
        hqlQuery.addFromTable( ContentIndexEntity.class.getName(), "x", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFilter( "AND", "x.path = '" + FieldHelper.translateFieldName( query.getField() ) + "'" );

        appendFilterQuery( hqlQuery, "x.categoryKey", query.getCategoryFilter() );
        appendFilterQuery( hqlQuery, "x.contentTypeKey", query.getContentTypeFilter() );
        appendSecurityFilterQuery( hqlQuery, "x", query.getSecurityFilter() );

        String orderBy = "x.orderValue " + ( query.isDescOrder() ? "DESC" : "ASC" );
        hqlQuery.addOrderBy( orderBy );

        return new TranslatedQuery( hqlQuery.toString(), query.getIndex(), query.getCount(), null );

    }


}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.enonic.cms.core.content.index.ContentIndexConstants;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.OrderByExpr;
import com.enonic.cms.core.content.index.queryexpression.OrderFieldExpr;


public class OrderByExprTranslator
{
    private HashMap<String, FieldName> tableAliases = new HashMap<String, FieldName>();

    private final HashSet<ColumnName> selectList = new HashSet<ColumnName>();

    public String translate( OrderByExpr expr )
    {
        if ( expr == null )
        {
            return null;
        }

        boolean append = false;
        StringBuffer str = new StringBuffer();
        for ( OrderFieldExpr orderFieldExpr : expr.getFields() )
        {
            if ( append )
            {
                str.append( ", " );
            }

            final FieldExpr fieldExpr = orderFieldExpr.getField();
            ColumnName selectField = translateFieldName( new FieldName( fieldExpr.getPath() ) );
            str.append( selectField ).append( " " ).append( orderFieldExpr.isAscending() ? "ASC" : "DESC" );
            this.selectList.add( selectField );
            append = true;
        }

        return str.toString();
    }

    public ColumnName translateFieldName( FieldName fieldName )
    {

        if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_KEY ) )
        {
            return new ColumnName( "x", "contentKey" );
        }
        else if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_CATEGORY_KEY ) )
        {
            return new ColumnName( "x", "categoryKey" );
        }
        else if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_CONTENT_TYPE_KEY ) )
        {
            return new ColumnName( "x", "contentTypeKey" );
        }
        else if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_PUBLISH_FROM ) )
        {
            return new ColumnName( "x", "contentPublishFrom" );
        }
        else if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_PUBLISH_TO ) )
        {
            return new ColumnName( "x", "contentPublishTo" );
        }
        else if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_STATUS ) )
        {
            return new ColumnName( "x", "contentStatus" );
        }

        return new ColumnName( getTableAlias( fieldName ), "orderValue" );
    }

    private String getTableAlias( FieldName fieldName )
    {
        final int tableAliasCount = this.tableAliases.size();
        String alias = "ob" + ( tableAliasCount + 1 );
        this.tableAliases.put( alias, fieldName );
        return alias;
    }

    public HashSet<ColumnName> getSelectList()
    {
        return selectList;
    }

    public Set<String> getTableAliasKeySet()
    {
        return tableAliases.keySet();
    }

    public HashMap<String, FieldName> getTableAliases()
    {
        return tableAliases;
    }
}


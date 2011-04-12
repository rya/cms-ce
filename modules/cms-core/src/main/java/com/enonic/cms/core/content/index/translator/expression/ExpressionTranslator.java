/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;


import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.core.content.index.ContentIndexConstants;
import com.enonic.cms.core.content.index.queryexpression.CombinedLogicalOrExpr;
import com.enonic.cms.core.content.index.queryexpression.CompareExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.LogicalExpr;
import com.enonic.cms.core.content.index.queryexpression.NotExpr;
import com.enonic.cms.core.content.index.translator.ContentQueryTranslatorException;


public class ExpressionTranslator
{
    private CompareExprTranslator compareExprTranslator;

    private LogicalExprTranslator logicalExprTranslator;

    private NotExprTranslator notExprTranslator;

    private CombinedLogicalOrExprTranslator combinedLogicalOrExprTranslator;

    private HashMap<String, FieldName> tableAliases = new HashMap<String, FieldName>();

    private Map<String, Object> parameters;

    private String valueSubSelectFilter;

    public ExpressionTranslator()
    {
        compareExprTranslator = new CompareExprTranslator( this );
        logicalExprTranslator = new LogicalExprTranslator( this );
        notExprTranslator = new NotExprTranslator( this );
        combinedLogicalOrExprTranslator = new CombinedLogicalOrExprTranslator( this );
    }

    public void init()
    {
        compareExprTranslator.setParameters( parameters );
        compareExprTranslator.setValueSubSelectFilter( valueSubSelectFilter );
    }

    public String translate( Expression expr )
    {
        String translatedExpression;
        if ( expr == null )
        {
            translatedExpression = null;
        }
        else if ( expr instanceof CompareExpr )
        {
            translatedExpression = compareExprTranslator.translate( (CompareExpr) expr );
        }
        else if ( expr instanceof LogicalExpr )
        {
            translatedExpression = logicalExprTranslator.translate( (LogicalExpr) expr );
        }
        else if ( expr instanceof NotExpr )
        {
            translatedExpression = notExprTranslator.translate( (NotExpr) expr );
        }
        else if ( expr instanceof CombinedLogicalOrExpr )
        {
            translatedExpression = combinedLogicalOrExprTranslator.translate( (CombinedLogicalOrExpr) expr );
        }
        else
        {
            throw new ContentQueryTranslatorException( "Unsupported expression: " + expr.getClass().getName() );
        }

        return translatedExpression;
    }

    public ColumnName translateFieldName( FieldName fieldName, boolean userOrderValue, boolean useTableAlias )
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
        else if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_PUBLISH_TO ) )
        {
            return new ColumnName( "x", "contentPublishTo" );
        }
        else if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_PUBLISH_FROM ) )
        {
            return new ColumnName( "x", "contentPublishFrom" );
        }
        else if ( fieldName.getTranslatedFieldName().equalsIgnoreCase( ContentIndexConstants.M_STATUS ) )
        {
            return new ColumnName( "x", "contentStatus" );
        }
        ColumnName columnName;
        if ( userOrderValue )
        {
            columnName = new ColumnName( "orderValue" );
        }
        else
        {
            columnName = new ColumnName( "value" );
        }

        if ( useTableAlias )
        {
            columnName.setAlias( getTableAlias( fieldName ) );
        }
        return columnName;
    }

    private String getTableAlias( FieldName fieldName )
    {
        final int tableAliasCount = this.tableAliases.size();
        String alias = "t" + ( tableAliasCount + 1 );
        this.tableAliases.put( alias, fieldName );
        return alias;
    }

    public Map<String, FieldName> getTableAliases()
    {
        return tableAliases;
    }

    public void setParameters( Map<String, Object> parameters )
    {
        this.parameters = parameters;
    }

    public void setValueSubSelectFilter( String valueSubSelectFilter )
    {
        this.valueSubSelectFilter = valueSubSelectFilter;
    }

    public CompareExprTranslator getCompareExprTranslator()
    {
        return compareExprTranslator;
    }
}

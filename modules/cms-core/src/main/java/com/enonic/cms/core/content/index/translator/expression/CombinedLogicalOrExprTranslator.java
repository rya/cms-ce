/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;

import java.util.List;

import com.enonic.cms.core.content.index.ContentIndexConstants;
import com.enonic.cms.core.content.index.queryexpression.CombinedLogicalOrExpr;
import com.enonic.cms.core.content.index.queryexpression.CompareExpr;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;

public class CombinedLogicalOrExprTranslator
{
    private ExpressionTranslator expressionTranslator;

    private CompareExprTranslator compareExprTranslator;

    public CombinedLogicalOrExprTranslator( ExpressionTranslator expressionTranslator )
    {
        this.expressionTranslator = expressionTranslator;
        compareExprTranslator = expressionTranslator.getCompareExprTranslator();
    }

    public String translate( CombinedLogicalOrExpr expr )
    {

        StringBuffer stringBuffer = new StringBuffer();

        if ( queryForMetaFields( expr.getExpressions() ) )
        {
            int lastIndex = expr.getExpressions().size();
            int i = 1;

            stringBuffer.append( "( " );
            for ( CompareExpr compareExpr : expr.getExpressions() )
            {
                stringBuffer.append( expressionTranslator.translate( compareExpr ) );
                if ( i != lastIndex )
                {
                    stringBuffer.append( " OR " );
                }
                i++;
            }
            stringBuffer.append( " )" );

        }
        else if ( expr.hasSameFieldsOnly() )
        {
            stringBuffer.append( "( " );
            stringBuffer.append( compareExprTranslator.translateQueryForSameFields( expr ) );
            stringBuffer.append( " )" );
        }
        else
        {
            int lastIndex = expr.getExpressions().size();
            int i = 1;

            stringBuffer.append( "( " );
            for ( CompareExpr compareExpr : expr.getExpressions() )
            {
                stringBuffer.append( expressionTranslator.translate( compareExpr ) );
                if ( i != lastIndex )
                {
                    stringBuffer.append( " OR " );
                }
                i++;
            }
            stringBuffer.append( " )" );
        }

        return stringBuffer.toString();
    }

    private boolean queryForMetaFields( List<CompareExpr> expressions )
    {

        for ( CompareExpr compareExpr : expressions )
        {
            FieldExpr fieldExpr = (FieldExpr) compareExpr.getLeft();
            if ( !isMetaField( fieldExpr.getPath() ) )
            {
                return false;
            }

        }
        return true;
    }

    private boolean isMetaField( String path )
    {

        if ( path == null || path.equals( "" ) )
        {
            return false;
        }

        if ( path.equals( ContentIndexConstants.M_KEY ) || path.equalsIgnoreCase( ContentIndexConstants.M_STATUS ) ||
            path.equalsIgnoreCase( ContentIndexConstants.M_PUBLISH_FROM ) || path.equalsIgnoreCase( ContentIndexConstants.M_PUBLISH_TO ) ||
            path.equalsIgnoreCase( ContentIndexConstants.M_CONTENT_TYPE_KEY ) ||
            path.equalsIgnoreCase( ContentIndexConstants.M_CATEGORY_KEY ) )
        {
            return true;
        }

        return false;
    }

}

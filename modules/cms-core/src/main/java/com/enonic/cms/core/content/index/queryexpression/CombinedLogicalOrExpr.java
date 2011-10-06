/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import java.util.ArrayList;
import java.util.List;

public class CombinedLogicalOrExpr
    implements Expression
{

    List<CompareExpr> expressions = new ArrayList<CompareExpr>();

    public Object evaluate( QueryEvaluator evaluator )
    {
        return this;
    }

    public CombinedLogicalOrExpr add( CompareExpr expr )
    {
        expressions.add( expr );

        return this;
    }

    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "or[" );

        int lastIndex = expressions.size();
        int i = 1;
        for ( Expression expr : expressions )
        {
            stringBuffer.append( expr.toString() );
            if ( i != lastIndex )
            {
                stringBuffer.append( ", " );
            }
            i++;
        }

        stringBuffer.append( "]" );

        return stringBuffer.toString();
    }

    public List<CompareExpr> getExpressions()
    {
        return expressions;
    }

    public boolean hasSameFieldsOnly()
    {

        String field = null;
        if ( expressions != null && expressions.size() > 0 )
        {
            FieldExpr fieldExpr = (FieldExpr) expressions.get( 0 ).getLeft();
            field = fieldExpr.getPath();
        }

        for ( CompareExpr compareExpr : expressions )
        {
            if ( !( (FieldExpr) compareExpr.getLeft() ).getPath().equals( field ) )
            {
                return false;
            }

        }
        return true;

    }
}

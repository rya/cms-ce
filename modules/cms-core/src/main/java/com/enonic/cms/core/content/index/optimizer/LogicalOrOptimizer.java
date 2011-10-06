/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.optimizer;

import com.enonic.cms.core.content.index.queryexpression.CombinedLogicalOrExpr;
import com.enonic.cms.core.content.index.queryexpression.CompareExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.LogicalExpr;
import com.enonic.cms.core.content.index.queryexpression.NotExpr;

public class LogicalOrOptimizer
{
    public Expression optimize( final Expression expression )
    {

        if ( expression instanceof LogicalExpr )
        {
            return optimizeLogicalExpr( (LogicalExpr) expression );
        }
        else if ( expression instanceof NotExpr )
        {
            return optimizeNotExpr( ( (NotExpr) expression ) );
        }
        else if ( expression instanceof CompareExpr )
        {
            return optimizeCompareExpr( ( (CompareExpr) expression ) );
        }

        return expression;
    }

    private Expression optimizeCompareExpr( CompareExpr expr )
    {
        return expr;
    }

    private Expression optimizeLogicalExpr( LogicalExpr expr )
    {

        Expression left = optimize( expr.getLeft() );
        Expression right = optimize( expr.getRight() );

        // does not optimize other than OR expression
        if ( expr.getOperator() != LogicalExpr.OR )
        {
            return new LogicalExpr( expr.getOperator(), left, right );
        }

        if ( left instanceof CompareExpr && right instanceof CompareExpr )
        {
            return new CombinedLogicalOrExpr().add( (CompareExpr) left ).add( (CompareExpr) right );
        }

        if ( left instanceof CompareExpr && right instanceof CombinedLogicalOrExpr )
        {
            return ( (CombinedLogicalOrExpr) right ).add( (CompareExpr) left );
        }

        if ( right instanceof CompareExpr && left instanceof CombinedLogicalOrExpr )
        {
            return ( (CombinedLogicalOrExpr) left ).add( (CompareExpr) right );
        }

        return new LogicalExpr( expr.getOperator(), left, right );
    }

    private Expression optimizeNotExpr( NotExpr expr )
    {
        return optimize( expr.getExpr() );
    }
}

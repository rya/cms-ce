/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;


public class QueryEvaluatorAdapter
    implements QueryEvaluator
{
    public Object evaluate( ArrayExpr expr )
    {
        final ValueExpr[] valueExpressions = expr.getValues();
        final ValueExpr[] newEvaluatedValues = new ValueExpr[valueExpressions.length];

        for ( int i = 0; i < valueExpressions.length; i++ )
        {
            final ValueExpr valueExpression = valueExpressions[i];
            if ( valueExpression != null )
            {
                newEvaluatedValues[i] = (ValueExpr) valueExpression.evaluate( this );
            }
            else
            {
                newEvaluatedValues[i] = null;
            }
        }
        return new ArrayExpr( newEvaluatedValues );
    }

    public Object evaluate( ValueExpr expr )
    {
        return expr;
    }

    public Object evaluate( FieldExpr expr )
    {
        return expr;
    }

    public Object evaluate( FunctionExpr expr )
    {
        return new FunctionExpr( expr.getName(), (ArrayExpr) expr.getArguments().evaluate( this ) );
    }

    public Object evaluate( CompareExpr expr )
    {
        Expression left = (Expression) expr.getLeft().evaluate( this );
        Expression right = (Expression) expr.getRight().evaluate( this );
        return new CompareExpr( expr.getOperator(), left, right );
    }

    public Object evaluate( LogicalExpr expr )
    {
        Expression left = (Expression) expr.getLeft().evaluate( this );
        Expression right = (Expression) expr.getRight().evaluate( this );
        return new LogicalExpr( expr.getOperator(), left, right );
    }

    public Object evaluate( NotExpr expr )
    {
        return new NotExpr( (Expression) expr.getExpr().evaluate( this ) );
    }

    public Object evaluate( OrderByExpr expr )
    {
        return expr;
    }

    public Object evaluate( OrderFieldExpr expr )
    {
        return expr;
    }

    public Object evaluate( QueryExpr queryExpr )
    {
        Expression expr = queryExpr.getExpr();
        if ( expr != null )
        {
            expr = (Expression) expr.evaluate( this );
        }
        return new QueryExpr( expr, queryExpr.getOrderBy() );
    }
}

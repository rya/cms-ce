/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the field expression.
 */
public final class QueryExpr
    implements Expression
{

    /**
     * Root expression.
     */
    private final Expression expr;

    /**
     * Order by expression.
     */
    private final OrderByExpr order;

    /**
     * Construct the field.
     */
    public QueryExpr( Expression expr, OrderByExpr order )
    {
        this.expr = expr;
        this.order = order;
    }

    /**
     * Return the expression.
     */
    public Expression getExpr()
    {
        return this.expr;
    }

    /**
     * Return the expression.
     */
    public OrderByExpr getOrderBy()
    {
        return this.order;
    }

    /**
     * Return the string.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();

        if ( this.expr != null )
        {
            str.append( this.expr.toString() );
        }

        if ( this.order != null )
        {
            if ( str.length() > 0 )
            {
                str.append( " " );
            }

            str.append( this.order.toString() );
        }

        return str.toString();
    }

    /**
     * Evaluate the expression.
     */
    public Object evaluate( QueryEvaluator evaluator )
    {
        return evaluator.evaluate( this );
    }
}

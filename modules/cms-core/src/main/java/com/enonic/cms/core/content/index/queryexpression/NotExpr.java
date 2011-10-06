/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the unary expression.
 */
public final class NotExpr
    implements Expression
{
    /**
     * Real expression.
     */
    private final Expression expr;

    /**
     * Construct the unary expression.
     */
    public NotExpr( Expression expr )
    {
        this.expr = expr;
    }

    /**
     * Return the real expression.
     */
    public Expression getExpr()
    {
        return this.expr;
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( "NOT (" );
        str.append( this.expr.toString() );
        str.append( ")" );
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

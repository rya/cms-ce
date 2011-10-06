/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This interface defines the expression evaluator.
 */
public interface QueryEvaluator
{
    /**
     * Visit the expression.
     */
    public Object evaluate( ArrayExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( ValueExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( FieldExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( FunctionExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( CompareExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( LogicalExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( NotExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( OrderByExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( OrderFieldExpr expr );

    /**
     * Visit the expression.
     */
    public Object evaluate( QueryExpr expr );
}

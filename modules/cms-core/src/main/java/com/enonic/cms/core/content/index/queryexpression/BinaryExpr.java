/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the compare expression.
 */
public abstract class BinaryExpr
    implements Expression
{
    /**
     * Left expression.
     */
    private final Expression left;

    /**
     * Right expression.
     */
    private final Expression right;

    /**
     * Construct the operator expression.
     */
    public BinaryExpr( Expression left, Expression right )
    {
        this.left = left;
        this.right = right;
    }

    /**
     * Return the left expression.
     */
    public Expression getLeft()
    {
        return this.left;
    }

    /**
     * Return the expression.
     */
    public Expression getRight()
    {
        return this.right;
    }
}

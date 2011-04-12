/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the field expression.
 */
public final class FieldExpr
    implements Expression
{
    /**
     * Field expression.
     */
    private final String path;

    /**
     * Construct the field.
     */
    public FieldExpr( String path )
    {
        this.path = path;
    }

    /**
     * Return the field.
     */
    public String getPath()
    {
        return this.path;
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        return this.path;
    }

    /**
     * Evaluate the expression.
     */
    public Object evaluate( QueryEvaluator evaluator )
    {
        return evaluator.evaluate( this );
    }
}

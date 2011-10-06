/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the field expression.
 */
public final class OrderFieldExpr
    implements Expression
{
    /**
     * Field expression.
     */
    private final FieldExpr field;

    /**
     * Descending?
     */
    private final boolean desc;

    /**
     * Construct the field.
     */
    public OrderFieldExpr( FieldExpr field, boolean desc )
    {
        this.field = field;
        this.desc = desc;
    }

    /**
     * Return the field.
     */
    public FieldExpr getField()
    {
        return this.field;
    }

    /**
     * Return true if ascending.
     */
    public boolean isAscending()
    {
        return !this.desc;
    }

    /**
     * Return true if descending.
     */
    public boolean isDescending()
    {
        return this.desc;
    }

    /**
     * Evaluate the expression.
     */
    public Object evaluate( QueryEvaluator evaluator )
    {
        return evaluator.evaluate( this );
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( this.field.toString() );
        str.append( this.desc ? " DESC" : " ASC" );
        return str.toString();
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the field expression.
 */
public final class OrderByExpr
    implements Expression
{
    /**
     * Order field expression.
     */
    private final OrderFieldExpr[] fields;

    /**
     * Construct the field.
     */
    public OrderByExpr( OrderFieldExpr[] fields )
    {
        this.fields = fields;
    }

    /**
     * Return the field.
     */
    public OrderFieldExpr[] getFields()
    {
        return this.fields;
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
        str.append( "ORDER BY " );

        for ( int i = 0; i < this.fields.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( ", " );
            }

            str.append( this.fields[i].toString() );
        }

        return str.toString();
    }
}

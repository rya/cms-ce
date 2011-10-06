/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the value expression.
 */
public final class ArrayExpr
    implements Expression
{
    /**
     * Values.
     */
    private final ValueExpr[] values;

    /**
     * Construct the value.
     */
    public ArrayExpr( ValueExpr[] values )
    {
        this.values = values;
    }

    /**
     * Return the value.
     */
    public ValueExpr[] getValues()
    {
        return this.values;
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( "(" );

        for ( int i = 0; i < this.values.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( ", " );
            }

            str.append( this.values[i].toString() );
        }

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

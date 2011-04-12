/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import org.joda.time.ReadableDateTime;

/**
 * This class implements the value expression.
 */
public final class ValueExpr
    implements Expression
{
    /**
     * Expression value.
     */
    private final Object value;

    /**
     * Construct the value.
     */
    public ValueExpr( String value )
    {
        this.value = value;
    }

    /**
     * Construct the value.
     */
    public ValueExpr( float value )
    {
        this.value = value;
    }

    /**
     * Construct the value.
     */
    public ValueExpr( ReadableDateTime value )
    {
        this.value = value;
    }

    /**
     * Construct the value.
     */
    public ValueExpr( Number value )
    {
        this.value = value;
    }

    /**
     * Return the value.
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     * Return true if number.
     */
    public boolean isNumber()
    {
        return this.value instanceof Number;
    }

    /**
     * Return true if string.
     */
    public boolean isString()
    {
        return this.value instanceof String;
    }

    /**
     * Return true if date.
     */
    public boolean isDate()
    {
        return this.value instanceof ReadableDateTime;
    }

    /**
     * Evaluate the expression.
     */
    public Object evaluate( QueryEvaluator evaluator )
    {
        return evaluator.evaluate( this );
    }

    @Override
    public String toString()
    {
        if ( this.value instanceof String )
        {
            return "'" + this.value + "'";
        }
        else if ( this.value instanceof ReadableDateTime )
        {
            return "'" + this.value + "'";
        }
        else
        {
            return String.valueOf( this.value );
        }
    }
}

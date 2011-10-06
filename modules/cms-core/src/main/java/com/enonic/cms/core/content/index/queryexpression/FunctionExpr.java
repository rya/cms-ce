/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the function expression.
 */
public final class FunctionExpr
    implements Expression
{
    /**
     * Function name.
     */
    private final String name;

    /**
     * Function arguments.
     */
    private final ArrayExpr args;

    /**
     * Construct the function expression.
     */
    public FunctionExpr( String name, ArrayExpr args )
    {
        this.name = name;
        this.args = args;
    }

    /**
     * Return the name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Return the parameters.
     */
    public ArrayExpr getArguments()
    {
        return this.args;
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( this.name );
        str.append( this.args.toString() );
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

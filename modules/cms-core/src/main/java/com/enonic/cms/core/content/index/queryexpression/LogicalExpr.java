/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This class implements the compare expression.
 */
public final class LogicalExpr
    extends BinaryExpr
{
    /**
     * Operator constants.
     */
    public final static int AND = 0;

    public final static int OR = 1;

    /**
     * Operator type.
     */
    private final int op;

    /**
     * Construct the operator expression.
     */
    public LogicalExpr( int op, Expression left, Expression right )
    {
        super( left, right );
        this.op = op;
    }

    /**
     * Return the operator.
     */
    public int getOperator()
    {
        return this.op;
    }

    /**
     * Return the operator as string.
     */
    public String getToken()
    {
        switch ( this.op )
        {
            case AND:
                return "AND";
            case OR:
                return "OR";
            default:
                return null;
        }
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( "(" );
        str.append( getLeft().toString() );
        str.append( " " );
        str.append( getToken() );
        str.append( " " );
        str.append( getRight().toString() );
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

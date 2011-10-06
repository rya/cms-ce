/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;


import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.LogicalExpr;
import com.enonic.cms.core.content.index.translator.ContentQueryTranslatorException;


public class LogicalExprTranslator
{
    private ExpressionTranslator expressionTranslator;

    public LogicalExprTranslator( ExpressionTranslator expressionTranslator )
    {
        this.expressionTranslator = expressionTranslator;
    }

    public String translate( LogicalExpr expr )
    {
        int op = expr.getOperator();
        Expression left = expr.getLeft();
        Expression right = expr.getRight();

        String translatedLogicalExpression;
        switch ( op )
        {
            case LogicalExpr.AND:
                translatedLogicalExpression = translateLogicalExpr( left, "AND", right );
                break;

            case LogicalExpr.OR:
                translatedLogicalExpression = translateLogicalExpr( left, "OR", right );
                break;

            default:
                throw new ContentQueryTranslatorException( "Unsupported operation: " + op );
        }

        return translatedLogicalExpression;
    }

    private String translateLogicalExpr( Expression left, String op, Expression right )
    {
        boolean wrapLeftExprInParanteses = left instanceof LogicalExpr;
        boolean wrapRightExprInParanteses = right instanceof LogicalExpr;

        String translatedLeft = expressionTranslator.translate( left );
        String translatedRight = expressionTranslator.translate( right );

        StringBuffer str = new StringBuffer();
        if ( wrapLeftExprInParanteses )
        {
            str.append( "(" );
        }
        str.append( translatedLeft );
        if ( wrapLeftExprInParanteses )
        {
            str.append( ")" );
        }

        str.append( " " ).append( op ).append( " " );

        if ( wrapRightExprInParanteses )
        {
            str.append( "(" );
        }
        str.append( translatedRight );
        if ( wrapRightExprInParanteses )
        {
            str.append( ")" );
        }

        return str.toString();
    }
}

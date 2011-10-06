/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;

import com.enonic.cms.core.content.index.queryexpression.NotExpr;


public class NotExprTranslator
{
    private ExpressionTranslator expressionTranslator;

    public NotExprTranslator( ExpressionTranslator expressionTranslator )
    {
        this.expressionTranslator = expressionTranslator;
    }

    public String translate( NotExpr expr )
    {
        String translatedExpression = expressionTranslator.translate( expr.getExpr() );

        StringBuffer str = new StringBuffer();
        str.append( "NOT(" ).append( translatedExpression ).append( ")" );
        return str.toString();
    }
}
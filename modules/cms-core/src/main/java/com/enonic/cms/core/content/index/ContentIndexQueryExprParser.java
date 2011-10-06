/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.content.index.queryexpression.DateCompareEvaluator;
import com.enonic.cms.core.content.index.queryexpression.FunctionEvaluator;
import com.enonic.cms.core.content.index.queryexpression.QueryEvaluator;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;
import com.enonic.cms.core.content.index.queryexpression.QueryParser;


public class ContentIndexQueryExprParser
{
    private static QueryEvaluator functionEvaluator = new FunctionEvaluator();

    private static QueryEvaluator dateCompareEvaluator = new DateCompareEvaluator();

    public static QueryExpr parse( ContentIndexQuery query )
    {
        QueryExpr expr = QueryParser.newInstance().parse( query.getQuery() );

        // first we want to invoke any functions...
        expr = (QueryExpr) expr.evaluate( functionEvaluator );

        // then we want to do some tricks with dates in some special cases...
        expr = (QueryExpr) expr.evaluate( dateCompareEvaluator );

        return expr;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;

import java.util.HashMap;

import org.junit.Test;

import com.enonic.cms.core.content.index.optimizer.LogicalOrOptimizer;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;
import com.enonic.cms.core.content.index.queryexpression.QueryParser;

import static org.junit.Assert.*;

public class ExpressionTranslatorTest
{

    @Test
    public void combinedLogicalOrExprTranslatorWithDifferentFields()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "a = 1 or b = 2 or c = 3" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals(
            "( x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND orderValue = :v0) OR x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'b' AND orderValue = :v1) OR x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'c' AND orderValue = :v2) )",
            translatedExpr );

    }

    @Test
    public void combinedLogicalOrExprTranslatorWithSingleRightValue()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "a = 1" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals(
            "x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND orderValue = :v0)",
            translatedExpr );
    }


    @Test
    public void combinedLogicalOrExprTranslatorWithSameFieldsAndNumberValues()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "a = 1 or a = 2 or a = 3" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals(
            "( x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND (orderValue = :v0 OR orderValue = :v1 OR orderValue = :v2)) )",
            translatedExpr );
    }


    @Test
    public void combinedLogicalOrExprTranslatorWithSameFieldsDifferentOperator()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "a = 1 or a > 2 or a = 3" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals(
            "( x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND (orderValue = :v0 OR orderValue > :v1 OR orderValue = :v2)) )",
            translatedExpr );

    }

    @Test
    public void combinedLogicalOrExprTranslatorWithSameFieldsAndANDClause()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "a = 1 and ( a = 2 or a = '3' )" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals(
            "x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND orderValue = :v0) AND ( x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND (orderValue = :v1 OR value = :v2)) )",
            translatedExpr );

    }


    @Test
    public void combinedLogicalOrExprTranslatorWithSameFieldsAndInvertedANDClause()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "( a = 2 or a = 3 ) and a = 1" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals(
            "( x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND (orderValue = :v0 OR orderValue = :v1)) ) AND x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND orderValue = :v2)",
            translatedExpr );

    }


    @Test
    public void combinedLogicalOrExprTranslatorWithSameFieldsAndStringValues()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "a = '1' or a = '2' or a = '3'" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals(
            "( x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND (value = :v0 OR value = :v1 OR value = :v2)) )",
            translatedExpr );

    }

    @Test
    public void combinedLogicalOrExprTranslatorWithSameFieldsAndStringAndNumberValues()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "a = 1 or a = '2' or a = '3'" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals(
            "( x.contentKey IN ( SELECT contentKey FROM com.enonic.cms.core.content.ContentIndexEntity WHERE path LIKE 'a' AND (orderValue = :v0 OR value = :v1 OR value = :v2)) )",
            translatedExpr );

    }


    @Test
    public void combinedLogicalOrExprTranslatorWithSingleMetaField()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "status = 2" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals( "x.contentStatus = 2.0", translatedExpr );

    }

    @Test
    public void combinedLogicalOrExprTranslatorWithMetaFieldsOnly()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "status = 2 or publishfrom > 'mydate' or key = 3398" );
        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals( "( x.contentStatus = 2.0 OR x.contentPublishFrom > :v0 OR x.contentKey = 3398.0 )", translatedExpr );

    }

    @Test
    public void combinedLogicalOrExprTranslatorWithSameMetaFieldsNumeric()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "status = 2 or status = 1 or status < 3" );
        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals( "( x.contentStatus = 2.0 OR x.contentStatus = 1.0 OR x.contentStatus < 3.0 )", translatedExpr );

    }


    @Test
    public void combinedLogicalOrExprTranslatorWithSameMetaFields()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "status = '2' or status = '1' or status < '3'" );
        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals( "( x.contentStatus = :v0 OR x.contentStatus = :v1 OR x.contentStatus < :v2 )", translatedExpr );

    }


    @Test
    public void combinedLogicalOrExprTranslatorWithMetaFieldsAndSameFields()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "status = 2 or status = 3 or publishfrom > 'mydate'" );
        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals( "( x.contentStatus = 2.0 OR x.contentStatus = 3.0 OR x.contentPublishFrom > :v0 )", translatedExpr );

    }

    @Test
    public void combinedLogicalOrExprTranslatorWithMetaFieldsOnlyAndANDClause()
    {
        ExpressionTranslator expressionTranslator = new ExpressionTranslator();
        expressionTranslator.setParameters( new HashMap<String, Object>() );
        expressionTranslator.init();

        QueryExpr testExpr = QueryParser.newInstance().parse( "status = 2 and ( publishfrom > 'mydate' or key = 3398 )" );

        LogicalOrOptimizer logicalOrOptimizer = new LogicalOrOptimizer();
        Expression optExpr = logicalOrOptimizer.optimize( testExpr.getExpr() );

        String translatedExpr = expressionTranslator.translate( optExpr );

        assertEquals( "x.contentStatus = 2.0 AND ( x.contentPublishFrom > :v0 OR x.contentKey = 3398.0 )", translatedExpr );

    }


}

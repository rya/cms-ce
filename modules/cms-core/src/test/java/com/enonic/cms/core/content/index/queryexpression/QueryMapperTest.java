/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import java.util.List;

import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map2;
import org.codehaus.jparsec.functors.Map3;
import org.codehaus.jparsec.functors.Unary;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class QueryMapperTest
{
    @Test
    public void testStringToNumberExpr()
    {
        final Map<String, ValueExpr> map = QueryMapper.stringToNumberExpr();

        final ValueExpr v1 = map.map( "1.88" );
        assertNotNull( v1 );
        assertEquals( 1.88d, v1.getValue() );
    }

    @Test
    public void testStringToStringExpr()
    {
        final Map<String, ValueExpr> map = QueryMapper.stringToStringExpr();

        final ValueExpr v1 = map.map( "ab" );
        assertNotNull( v1 );
        assertEquals( "ab", v1.getValue() );
    }

    @Test
    public void testStringToFieldExpr()
    {
        final Map<String, FieldExpr> map = QueryMapper.stringToFieldExpr();

        final FieldExpr v1 = map.map( "abc/def.ghi" );
        assertNotNull( v1 );
        assertEquals( "abc/def.ghi", v1.getPath() );
    }

    @Test
    public void testStringToFragment()
    {
        final Map<String, Tokens.Fragment> map = QueryMapper.stringToFragment( "tag" );

        final Tokens.Fragment v1 = map.map( "hello" );
        assertNotNull( v1 );
        assertEquals( "hello", v1.text() );
        assertEquals( "tag", v1.tag() );
    }

    @Test
    public void testCompareExprMapper()
    {
        final Map3<FieldExpr, Integer, Expression, CompareExpr> map = QueryMapper.compareExprMapper();

        final CompareExpr v1 = map.map( new FieldExpr( "a" ), CompareExpr.EQ, new ValueExpr( 1 ) );
        assertNotNull( v1.getLeft() );
        assertEquals( "a", v1.getLeft().toString() );
        assertNotNull( v1.getRight() );
        assertEquals( "1.0", v1.getRight().toString() );
        assertEquals( CompareExpr.EQ, v1.getOperator() );
    }

    @Test
    public void testValuesToArrayExpr()
    {
        final Map<List<ValueExpr>, ArrayExpr> map = QueryMapper.valuesToArrayExpr();
        final List<ValueExpr> list = Lists.newArrayList( new ValueExpr( 1 ), new ValueExpr( 2 ) );

        final ArrayExpr v1 = map.map( list );
        assertNotNull( v1 );
        assertNotNull( v1.getValues() );
        assertEquals( 2, v1.getValues().length );
        assertEquals( "1.0", v1.getValues()[0].toString() );
        assertEquals( "2.0", v1.getValues()[1].toString() );
    }

    @Test
    public void testFunctionExprMapper()
    {
        final Map2<String, ArrayExpr, FunctionExpr> map = QueryMapper.functionExprMapper();

        final FunctionExpr v1 = map.map( "func", new ArrayExpr( new ValueExpr[]{new ValueExpr( 1 )} ) );
        assertNotNull( v1 );
        assertEquals( "func", v1.getName() );
        assertNotNull( v1.getArguments() );
    }

    @Test
    public void testPrefixSuffixMapper()
    {
        Map<ValueExpr, ValueExpr> map = QueryMapper.prefixSuffixMapper( null, null );
        ValueExpr value = map.map( new ValueExpr( "ab" ) );
        assertNotNull( value );
        assertEquals( "ab", value.getValue() );

        map = QueryMapper.prefixSuffixMapper( "%", null );
        value = map.map( new ValueExpr( "ab" ) );
        assertNotNull( value );
        assertEquals( "%ab", value.getValue() );

        map = QueryMapper.prefixSuffixMapper( null, "%" );
        value = map.map( new ValueExpr( "ab" ) );
        assertNotNull( value );
        assertEquals( "ab%", value.getValue() );

        map = QueryMapper.prefixSuffixMapper( "%", "%" );
        value = map.map( new ValueExpr( "ab" ) );
        assertNotNull( value );
        assertEquals( "%ab%", value.getValue() );
    }

    @Test
    public void testLogicalExprMapper()
    {
        final Expression left = new ValueExpr( "a" );
        final Expression right = new ValueExpr( "b" );

        Binary<Expression> map = QueryMapper.logicalExprMapper( LogicalExpr.AND );
        Expression value = map.map( left, right );

        assertNotNull( value );
        assertSame( LogicalExpr.class, value.getClass() );
        assertEquals( LogicalExpr.AND, ( (LogicalExpr) value ).getOperator() );
        assertSame( left, ( (LogicalExpr) value ).getLeft() );
        assertSame( right, ( (LogicalExpr) value ).getRight() );
    }

    @Test
    public void testNotExprMapper()
    {
        final Expression expr = new ValueExpr( 1 );
        final Unary<Expression> map = QueryMapper.notExprMapper();
        final Expression value = map.map( expr );

        assertNotNull( value );
        assertSame( NotExpr.class, value.getClass() );
        assertSame( expr, ( (NotExpr) value ).getExpr() );
    }

    @Test
    public void testOrderByExprMapper()
    {
        final Map<List<OrderFieldExpr>, OrderByExpr> map = QueryMapper.orderByExprMapper();

        final List<OrderFieldExpr> list = Lists.newArrayList( new OrderFieldExpr( new FieldExpr( "a" ), true ) );
        final OrderByExpr value = map.map( list );

        assertNotNull( value );
        assertEquals( 1, value.getFields().length );
        assertSame( list.get( 0 ), value.getFields()[0] );
    }

    @Test
    public void testOrderFieldExprMapper()
    {
        final Map2<FieldExpr, Boolean, OrderFieldExpr> map = QueryMapper.orderFieldExprMapper();

        final FieldExpr field = new FieldExpr( "a" );
        final OrderFieldExpr value = map.map( field, true );

        assertNotNull( value );
        assertEquals( true, value.isDescending() );
        assertSame( field, value.getField() );
    }

    @Test
    public void testQueryExprMapper()
    {
        final Map2<Expression, OrderByExpr, QueryExpr> map = QueryMapper.queryExprMapper();

        final Expression expr = new ValueExpr( "a" );
        final OrderByExpr orderBy = new OrderByExpr( new OrderFieldExpr[0] );
        final QueryExpr value = map.map( expr, orderBy );

        assertNotNull( value );
        assertSame( expr, value.getExpr() );
        assertSame( orderBy, value.getOrderBy() );
    }
}

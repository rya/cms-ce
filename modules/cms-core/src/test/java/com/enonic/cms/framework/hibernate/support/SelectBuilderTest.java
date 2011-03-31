/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.hibernate.support;

import junit.framework.TestCase;


public class SelectBuilderTest
    extends TestCase
{

    public void testAddColumns()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "col_a, col_b AS alias" );

        assertEquals( "\tSELECT col_a, col_b AS alias", hql.toString() );
    }

    public void testAddColumnsFrom()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "col_a, col_b AS alias" );
        select.addFromTable( "Content", "c", SelectBuilder.NO_JOIN, null );

        StringBuffer expectedHql = new StringBuffer();
        expectedHql.append( "\tSELECT col_a, col_b AS alias" );
        expectedHql.append( "\n\tFROM Content AS c" );
        assertEquals( expectedHql.toString(), hql.toString() );
    }

    public void testAddColumnsFromWithLeftJoin()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "col_a, col_b AS alias" );
        select.addFromTable( "Content", "c", SelectBuilder.NO_JOIN, null );
        select.addFromTable( "c.contentHome", null, SelectBuilder.LEFT_JOIN, null );
        select.addFromTable( "c.currentVersion", null, SelectBuilder.LEFT_JOIN_FETCH, null );

        StringBuffer expectedHql = new StringBuffer();
        expectedHql.append( "\tSELECT col_a, col_b AS alias" );
        expectedHql.append( "\n\tFROM Content AS c" );
        expectedHql.append( "\n\tLEFT JOIN c.contentHome" );
        expectedHql.append( "\n\tLEFT JOIN FETCH c.currentVersion" );
        assertEquals( expectedHql.toString(), hql.toString() );
    }

    public void testAddColumnsFromWithInnerAndLeftJoin()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "col_a, col_b AS alias" );
        select.addFromTable( "Content", "c", SelectBuilder.NO_JOIN, null );
        select.addFromTable( "Content", "x", SelectBuilder.INNER_JOIN, "x.key = c.key" );
        select.addFromTable( "c.contentHomes", null, SelectBuilder.LEFT_JOIN, null );

        StringBuffer expectedHql = new StringBuffer();
        expectedHql.append( "\tSELECT col_a, col_b AS alias" );
        expectedHql.append( "\n\tFROM Content AS c" );
        expectedHql.append( "\n\tINNER JOIN Content AS x ON x.key = c.key" );
        expectedHql.append( "\n\tLEFT JOIN c.contentHomes" );
        assertEquals( expectedHql.toString(), hql.toString() );
    }

    public void testAddColumnsFromWhere()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "col_a, col_b AS alias" );
        select.addFromTable( "Content", "c", SelectBuilder.NO_JOIN, null );
        select.addFilter( "AND", "col_a = 1" );

        StringBuffer expectedHql = new StringBuffer();
        expectedHql.append( "\tSELECT col_a, col_b AS alias" );
        expectedHql.append( "\n\tFROM Content AS c" );
        expectedHql.append( "\n\tWHERE col_a = 1" );
        assertEquals( expectedHql.toString(), hql.toString() );
    }

    public void testAddColumnsFromWhereAnd()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "a, b" );
        select.addFromTable( "Content", "c", SelectBuilder.NO_JOIN, null );
        select.addFilter( "AND", "a = 1" );
        select.addFilter( "AND", "b = 2" );

        StringBuffer expectedHql = new StringBuffer();
        expectedHql.append( "\tSELECT a, b" );
        expectedHql.append( "\n\tFROM Content AS c" );
        expectedHql.append( "\n\tWHERE a = 1" );
        expectedHql.append( "\n\tAND b = 2" );
        assertEquals( expectedHql.toString(), hql.toString() );
    }

    public void testAddColumnsFromWhereOr()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "a, b" );
        select.addFromTable( "Content", "c", SelectBuilder.NO_JOIN, null );
        select.addFilter( "OR", "a = 1" );
        select.addFilter( "OR", "b = 2" );

        StringBuffer expectedHql = new StringBuffer();
        expectedHql.append( "\tSELECT a, b" );
        expectedHql.append( "\n\tFROM Content AS c" );
        expectedHql.append( "\n\tWHERE a = 1" );
        expectedHql.append( "\n\tOR b = 2" );
        assertEquals( expectedHql.toString(), hql.toString() );
    }

    public void testAddColumnsAndFromAndFilterGroupWithAnd()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "a, b" );
        select.addFromTable( "Content", "c", SelectBuilder.NO_JOIN, null );
        select.addFilter( "AND", "a > 0" );
        select.addFilter( "AND", "b < 100" );
        select.startFilterGroup( "AND" );
        select.addFilter( "OR", "a = 1" );
        select.addFilter( "OR", "b = 2" );
        select.addFilter( "OR", "b = 3" );
        select.endFilterGroup();
        select.addFilter( "AND", "a = b" );

        StringBuffer expectedHql = new StringBuffer();
        expectedHql.append( "\tSELECT a, b" );
        expectedHql.append( "\n\tFROM Content AS c" );
        expectedHql.append( "\n\tWHERE a > 0" );
        expectedHql.append( "\n\tAND b < 100" );
        expectedHql.append( "\n\tAND (" );
        expectedHql.append( "\n\ta = 1" );
        expectedHql.append( "\n\tOR b = 2" );
        expectedHql.append( "\n\tOR b = 3" );
        expectedHql.append( "\n\t)" );
        expectedHql.append( "\n\tAND a = b" );
        assertEquals( expectedHql.toString(), hql.toString() );
    }

    public void testAddColumnsAndFromAndFilterGroupWithOr()
    {

        StringBuffer hql = new StringBuffer();

        SelectBuilder select = new SelectBuilder( hql, 1 );
        select.addSelect( "a, b" );
        select.addFromTable( "Content", "c", SelectBuilder.NO_JOIN, null );
        select.addFilter( "AND", "a > 0" );
        select.addFilter( "AND", "b < 100" );
        select.startFilterGroup( "OR" );
        select.addFilter( "OR", "a = 1" );
        select.addFilter( "OR", "b = 2" );
        select.addFilter( "OR", "b = 3" );
        select.endFilterGroup();

        StringBuffer expectedHql = new StringBuffer();
        expectedHql.append( "\tSELECT a, b" );
        expectedHql.append( "\n\tFROM Content AS c" );
        expectedHql.append( "\n\tWHERE a > 0" );
        expectedHql.append( "\n\tAND b < 100" );
        expectedHql.append( "\n\tOR (" );
        expectedHql.append( "\n\ta = 1" );
        expectedHql.append( "\n\tOR b = 2" );
        expectedHql.append( "\n\tOR b = 3" );
        expectedHql.append( "\n\t)" );
        assertEquals( expectedHql.toString(), hql.toString() );
    }
}

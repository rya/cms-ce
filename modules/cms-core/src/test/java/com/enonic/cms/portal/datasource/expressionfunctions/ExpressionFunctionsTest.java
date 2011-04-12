/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.expressionfunctions;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.joda.time.DateTime;

import junit.framework.TestCase;

import com.enonic.cms.framework.time.MockTimeService;
import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.portal.PortalInstanceKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.portlet.PortletKey;

public class ExpressionFunctionsTest
    extends TestCase
{
    private ExpressionFunctions expressionFunctions = new ExpressionFunctions();

    public void testSelect()
        throws Exception
    {
        String result1 = expressionFunctions.select( "abc", null );
        String result2 = expressionFunctions.select( "abc", "" );
        String result3 = expressionFunctions.select( "abc", "  " );
        String result4 = expressionFunctions.select( "abd", "abc" );
        String result5 = expressionFunctions.select( "   ", "abc" );
        String result6 = expressionFunctions.select( "   ", null );
        String result7 = expressionFunctions.select( "", "abc" );
        String result8 = expressionFunctions.select( "", null );
        String result9 = expressionFunctions.select( null, "abc" );
        assertEquals( "abc", result1 );
        assertEquals( "abc", result2 );
        assertEquals( "abc", result3 );
        assertEquals( "abd", result4 );
        assertEquals( "abc", result5 );
        assertNull( result6 );
        assertEquals( "abc", result7 );
        assertNull( result8 );
        assertEquals( "abc", result9 );
    }

    public void testReplace()
        throws Exception
    {
        String testString = "Dette �r 1   tilfeldig  �vre tekststring med rare tegn � tall som 1.234e12!";

        String result1 = expressionFunctions.replace( testString, "\\s", "TEST" );
        String result2 = expressionFunctions.replace( testString, "\\s+", "TEST" );
        String result3 = expressionFunctions.replace( testString, "\\d", "tall" );
        assertTrue( result1.contains( "�rTEST1TESTTESTTESTtilfeldig" ) );
        assertTrue( result2.contains( "�rTEST1TESTtilfeldig" ) );
        assertTrue( result3.endsWith( "som tall.talltalltalletalltall!" ) );

        String result4 = expressionFunctions.replace( testString, "�vre", "nedre" );
        assertTrue( result4.contains( "tilfeldig  nedre tekststring" ) );
        int index = result4.indexOf( "nedre" );
        assertTrue( index > 1 );
        index = result4.indexOf( "nedre", index + 1 );
        assertEquals( -1, index );
    }

    public void testBuildFreetextQuery()
        throws Exception
    {
        String result0a = expressionFunctions.buildFreetextQuery( "freetext", "", "AND" );
        assertEquals( "", result0a );

        String result0b = expressionFunctions.buildFreetextQuery( "", "", "or" );
        assertEquals( "", result0b );

        String result1 = expressionFunctions.buildFreetextQuery( "freetext", "Lagavullin", "AND" );
        assertEquals( "freetext CONTAINS \"Lagavullin\"", result1 );

        String result2 = expressionFunctions.buildFreetextQuery( "freetext", "Lagavullin", "or" );
        assertEquals( "freetext CONTAINS \"Lagavullin\"", result2 );

        String result3 = expressionFunctions.buildFreetextQuery( "contentdata/article/text", "Vertical Site", "anD" );
        assertEquals( "contentdata/article/text CONTAINS \"Vertical\" AND contentdata/article/text CONTAINS \"Site\"", result3 );

        String result4 = expressionFunctions.buildFreetextQuery( "data/*", "Jan J�rund J�rgen Jens Jarle", "Or" );
        assertEquals( "data/* CONTAINS \"Jan\" OR data/* CONTAINS \"J�rund\" OR " +
            "data/* CONTAINS \"J�rgen\" OR data/* CONTAINS \"Jens\" OR data/* CONTAINS \"Jarle\"", result4 );

        String result5 = expressionFunctions.buildFreetextQuery( "data/*", "CMS 4.3.4", "and" );
        assertEquals( "data/* CONTAINS \"CMS\" AND data/* CONTAINS \"4.3.4\"", result5 );

        String result6 = expressionFunctions.buildFreetextQuery( "data/*", "3.14", "or" );
        assertEquals( "data/* CONTAINS \"3.14\"", result6 );

        String result7 = expressionFunctions.buildFreetextQuery( "data/*", "12345 Colorado Blvd, San Diego, CA 92111", "And" );
        assertEquals( "data/* CONTAINS \"12345\" AND data/* CONTAINS \"Colorado\" AND " +
            "data/* CONTAINS \"Blvd,\" AND data/* CONTAINS \"San\" AND " +
            "data/* CONTAINS \"Diego,\" AND data/* CONTAINS \"CA\" AND data/* CONTAINS \"92111\"", result7 );

        String result8 = expressionFunctions.buildFreetextQuery( "data/*", "2+2 < {Four*}", "or" );
        assertEquals( "data/* CONTAINS \"2+2\" OR data/* CONTAINS \"<\" OR data/* CONTAINS \"{Four*}\"", result8 );

        String result9 = expressionFunctions.buildFreetextQuery( "data/*", "(a+2)=b || ^(&ptr<1.2e2)", "and" );
        assertEquals( "data/* CONTAINS \"(a+2)=b\" AND data/* CONTAINS \"||\" AND data/* CONTAINS \"^(&ptr<1.2e2)\"", result9 );
    }

    public void testBuildFreetextQueryWithQuotes()
        throws Exception
    {
        String result1 = expressionFunctions.buildFreetextQuery( "freetext", "Enonic's", "and" );
        assertEquals( "freetext CONTAINS \"Enonic's\"", result1 );

        String result2 = expressionFunctions.buildFreetextQuery( "freetext", "Enonic's \"Vertical Site\"", "anD" );
        assertEquals( "freetext CONTAINS \"Enonic's\" AND freetext CONTAINS \"Vertical Site\"", result2 );

        String result3 = expressionFunctions.buildFreetextQuery( "data/text", "�l \"La humla suse", "Or" );
        assertEquals( "data/text CONTAINS \"�l\" OR data/text CONTAINS \"La humla suse\"", result3 );

        String result4 = expressionFunctions.buildFreetextQuery( "data/*", "\"Hei du\" \"Hadde bra!", "and" );
        assertEquals( "data/* CONTAINS \"Hei du\" AND data/* CONTAINS \"Hadde bra!\"", result4 );
    }

    public void testBuildFreetextQueryErrorHandling()
        throws Exception
    {
        try
        {
            expressionFunctions.buildFreetextQuery( "freetext", "Ja, ha!", "NOT" );
            fail( "An exception should have been thrown." );
        }
        catch ( IllegalArgumentException e )
        {
            // Successful:
            assertEquals( "Illegal operator: NOT", e.getMessage() );
        }

        try
        {
            expressionFunctions.buildFreetextQuery( "freetext", "Jo, da!", "rubbish" );
            fail( "An exception should have been thrown." );
        }
        catch ( IllegalArgumentException e )
        {
            // Successful:
            assertEquals( "Illegal operator: rubbish", e.getMessage() );
        }

        try
        {
            expressionFunctions.buildFreetextQuery( null, "Oh, yeah!", "and" );
            fail( "An exception should have been thrown." );
        }
        catch ( IllegalArgumentException e )
        {
            // Successful!
        }

        try
        {
            expressionFunctions.buildFreetextQuery( "   ", "Oh, yeah!", "or" );
            fail( "An exception should have been thrown." );
        }
        catch ( IllegalArgumentException e )
        {
            // Successful!
        }
    }

    public void testGetPageKey()
    {
        ExpressionFunctions eFuncs = new ExpressionFunctions();
        ExpressionContext context = new ExpressionContext();
        MenuItemEntity menuItem = new MenuItemEntity();
        final int key = 123;
        menuItem.setKey( key );
        context.setMenuItem( menuItem );
        eFuncs.setContext( context );
        assertEquals( "123", eFuncs.getPageKey() );
    }

    public void testGetPortletWindowKey()
    {
        ExpressionFunctions eFuncs = new ExpressionFunctions();
        ExpressionContext context = new ExpressionContext();
        final int menuKey = 123;
        final int portletKey = 246;
        PortalInstanceKey portalInstanceKey = PortalInstanceKey.createWindow( new MenuItemKey( menuKey ), new PortletKey( portletKey ) );
        context.setPortalInstanceKey( portalInstanceKey );
        eFuncs.setContext( context );
        assertEquals( "123:246", eFuncs.getWindowKey() );
    }

    public void testCurrentDate()
    {
        TimeService timeService = new MockTimeService( new DateTime( 2010, 5, 28, 12, 30, 5, 4 ) );

        ExpressionFunctions expressionFunctions = new ExpressionFunctions();
        expressionFunctions.setTimeService( timeService );

        assertEquals( "28.05.2010 12:30", expressionFunctions.currentDate( "dd.MM.yyyy HH:mm" ) );
    }

    public void testPeriodHoursMinutes()
    {
        TimeService timeService = new MockTimeService( new DateTime( 2010, 5, 28, 12, 30, 5, 4 ) );

        ExpressionFunctions expressionFunctions = new ExpressionFunctions();
        expressionFunctions.setTimeService( timeService );

        assertEquals( "PT2H", expressionFunctions.periodHoursMinutes( 2, 0 ) );
        assertEquals( "PT2H5M", expressionFunctions.periodHoursMinutes( 2, 5 ) );
        assertEquals( "PT-2H5M", expressionFunctions.periodHoursMinutes( -2, 5 ) );
        assertEquals( "PT-2H-5M", expressionFunctions.periodHoursMinutes( -2, -5 ) );
        assertEquals( "PT2H61M", expressionFunctions.periodHoursMinutes( 2, 61 ) );
    }

    public void testCurrentDatePlusOffset()
    {
        TimeService timeService = new MockTimeService( new DateTime( 2010, 5, 28, 12, 30, 0, 4 ) );

        ExpressionFunctions ef = new ExpressionFunctions();
        ef.setTimeService( timeService );

        assertEquals( "28.05.2010 14:35", ef.currentDatePlusOffset( "dd.MM.yyyy HH:mm", "PT2H5M" ) );
        assertEquals( "28.05.2010 10:25", ef.currentDatePlusOffset( "dd.MM.yyyy HH:mm", "PT-2H-5M" ) );

        assertEquals( "28.05.2010 14:35", ef.currentDatePlusOffset( "dd.MM.yyyy HH:mm", ef.periodHoursMinutes( 2, 5 ) ) );
        assertEquals( "28.05.2010 10:25", ef.currentDatePlusOffset( "dd.MM.yyyy HH:mm", ef.periodHoursMinutes( -2, -5 ) ) );
    }

    public void testCurrentDateMinusOffset()
    {
        TimeService timeService = new MockTimeService( new DateTime( 2010, 5, 28, 12, 30, 0, 4 ) );

        ExpressionFunctions ef = new ExpressionFunctions();
        ef.setTimeService( timeService );

        assertEquals( "28.05.2010 10:25", ef.currentDateMinusOffset( "dd.MM.yyyy HH:mm", "PT2H5M" ) );
        assertEquals( "28.05.2010 14:35", ef.currentDateMinusOffset( "dd.MM.yyyy HH:mm", "PT-2H-5M" ) );

        assertEquals( "28.05.2010 10:25", ef.currentDateMinusOffset( "dd.MM.yyyy HH:mm", ef.periodHoursMinutes( 2, 5 ) ) );
        assertEquals( "28.05.2010 14:35", ef.currentDateMinusOffset( "dd.MM.yyyy HH:mm", ef.periodHoursMinutes( -2, -5 ) ) );
    }

    public void testIsblank()
    {
        ExpressionFunctions ef = new ExpressionFunctions();
        assertEquals( true, ef.isblank( null ) );
        assertEquals( true, ef.isblank( "" ) );
        assertEquals( true, ef.isblank( " " ) );
        assertEquals( true, ef.isblank( "    " ) );
        assertEquals( false, ef.isblank( "s" ) );
        assertEquals( false, ef.isblank( " s " ) );
    }

    public void testIsnotblank()
    {
        ExpressionFunctions ef = new ExpressionFunctions();
        assertEquals( false, ef.isnotblank( null ) );
        assertEquals( false, ef.isnotblank( "" ) );
        assertEquals( false, ef.isnotblank( " " ) );
        assertEquals( false, ef.isnotblank( "    " ) );
        assertEquals( true, ef.isnotblank( "s" ) );
        assertEquals( true, ef.isnotblank( " s " ) );
    }

    public void testIsempty()
    {
        ExpressionFunctions ef = new ExpressionFunctions();
        assertEquals( true, ef.isempty( null ) );
        assertEquals( true, ef.isempty( "" ) );
        assertEquals( false, ef.isempty( " " ) );
        assertEquals( false, ef.isempty( "    " ) );
        assertEquals( false, ef.isempty( "s" ) );
        assertEquals( false, ef.isempty( " s " ) );
    }

    public void testIsnotempty()
    {
        ExpressionFunctions ef = new ExpressionFunctions();
        assertEquals( false, ef.isnotempty( null ) );
        assertEquals( false, ef.isnotempty( "" ) );
        assertEquals( true, ef.isnotempty( " " ) );
        assertEquals( true, ef.isnotempty( "    " ) );
        assertEquals( true, ef.isnotempty( "s" ) );
        assertEquals( true, ef.isnotempty( " s " ) );
    }
}

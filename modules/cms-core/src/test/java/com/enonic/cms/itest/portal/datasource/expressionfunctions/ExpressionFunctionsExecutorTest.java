/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.portal.datasource.expressionfunctions;

import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionContext;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.time.MockTimeService;

import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionFunctionsFactory;

import com.enonic.cms.portal.datasource.ExpressionFunctionsExecutor;

import com.enonic.cms.core.security.user.UserEntity;

import static org.junit.Assert.*;

/**
 * May 28, 2010
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ExpressionFunctionsExecutorTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    private MockTimeService timeService;

    private UserEntity defaultUser;

    private ExpressionContext expressionContext;

    private ExpressionFunctionsFactory efFactory;

    private ExpressionFunctionsExecutor efExecutor;


    @Before
    public void before()
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        defaultUser = fixture.createAndStoreNormalUserWithUserGroup( "testuser", "testuser", "testuserstore" );

        timeService = new MockTimeService();

        expressionContext = new ExpressionContext();
        expressionContext.setUser( defaultUser );

        efFactory = new ExpressionFunctionsFactory();
        efFactory.setTimeService( timeService );
        efFactory.setContext( expressionContext );

        efExecutor = new ExpressionFunctionsExecutor();
        efExecutor.setExpressionContext( expressionContext );
    }

    @Test
    public void testEvaluateCurrentDateWithTime()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDate( 'yyyy.MM.dd HH:mm' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 12:30", evaluted );
    }

    @Test
    public void testEvaluateCurrentDateWithoutTime()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDate( 'yyyy.MM.dd' )}" );
        assertEquals( "@publishfrom >= 2010.05.28", evaluted );
    }

    @Test
    public void testEvaluateCurrentDateMinusOffset()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted =
            efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( 2, 35 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 09:55", evaluted );

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', 'PT2H35M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 09:55", evaluted );

        // .. and with negative periods

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( -2, -35 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 15:05", evaluted );

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', 'PT-2H-35M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 15:05", evaluted );
    }

    @Test
    public void testEvaluateCurrentDatePlusOffset()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted =
            efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( 2, 5 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 14:35", evaluted );

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', 'PT2H5M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 14:35", evaluted );

        // .. and with negative periods

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( -2, -5 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 10:25", evaluted );

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', 'PT-2H-5M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 10:25", evaluted );
    }

    @Test
    public void testEvaluatePositiveDurationHoursMinutes()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted = efExecutor.evaluate( "${periodHoursMinutes( 2, 5 )}" );
        assertEquals( "PT2H5M", evaluted );
    }

    @Test
    public void testEvaluateNegativeDurationHoursMinutes()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted = efExecutor.evaluate( "${periodHoursMinutes( -2, -5 )}" );
        assertEquals( "PT-2H-5M", evaluted );
    }
}

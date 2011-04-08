/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.ExpectedException;

import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionFunctionsFactory;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.portal.PortalInstanceKey;
import com.enonic.cms.domain.portal.datasource.Datasource;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;

import static org.junit.Assert.*;

public class DatasourceExecutorTest
{

    private DatasourceExecutorContext context;

    private UserEntity userEntity;

    @Before
    public void setup()
    {

        context = new DatasourceExecutorContext();

        MockHttpServletRequest request = new MockHttpServletRequest();
        LanguageEntity languageEntity = new LanguageEntity();
        languageEntity.setCode( "no" );

        PortalInstanceKey portalInstanceKey = PortalInstanceKey.createSite( new SiteKey( 0 ) );

        UserStoreEntity userStoreEntity = new UserStoreEntity();
        userStoreEntity.setName( "demousers" );

        // looks strange, but is needed
        new ExpressionFunctionsFactory();

        userEntity = new UserEntity();
        userEntity.setName( "elvis" );
        userEntity.setKey( new UserKey( "userkey" ) );
        userEntity.setDisplayName( "Elvis Presley" );
        userEntity.setEmail( "elvis@graceland.com" );
        userEntity.setUserStore( userStoreEntity );

        context.setDeviceClass( "default" );
        context.setHttpRequest( request );
        context.setLanguage( languageEntity );
        context.setPortalInstanceKey( portalInstanceKey );
        context.setSite( context.getSite() );
        context.setUser( userEntity );
        context.setVerticalSession( null );
    }

    @Test
    public void testNullCondition()
    {
        DatasourceExecutor executor = new DatasourceExecutor( context );
        Datasource ds = new Datasource( new Element( "datasource" ) );
        assertTrue( executor.isRunnableByCondition( ds ) );
    }

    @Test
    public void testEmptyCondition()
    {
        DatasourceExecutor executor = new DatasourceExecutor( context );
        Element datasourceEl = new Element( "datasource" );
        datasourceEl.setAttribute( "condition", "" );
        Datasource ds = new Datasource( datasourceEl );

        assertTrue( executor.isRunnableByCondition( ds ) );
    }

    @Test
    @ExpectedException(DatasourceException.class)
    public void testIncorrectElCondition()
    {
        DatasourceExecutor executor = new DatasourceExecutor( context );

        Element datasourceEl = new Element( "datasource" );
        datasourceEl.setAttribute( "condition", "1=1" );
        Datasource ds = new Datasource( datasourceEl );

        executor.isRunnableByCondition( ds );
    }

    @Test
    public void testConditionWithoutBraces()
    {
        DatasourceExecutor executor = new DatasourceExecutor( context );

        Element datasourceEl = new Element( "datasource" );
        datasourceEl.setAttribute( "condition", "1==1" );
        Datasource ds = new Datasource( datasourceEl );

        assertFalse( executor.isRunnableByCondition( ds ) );
    }

    @Test
    public void testTrueUserUidCondition()
    {
        DatasourceExecutor executor = new DatasourceExecutor( context );

        Element datasourceEl = new Element( "datasource" );
        datasourceEl.setAttribute( "condition", "${user.uid == 'elvis'}" );
        Datasource ds = new Datasource( datasourceEl );

        assertTrue( executor.isRunnableByCondition( ds ) );
    }


    @Test
    public void testFalseUserUidCondition()
    {
        DatasourceExecutor executor = new DatasourceExecutor( context );
        Element datasourceEl = new Element( "datasource" );
        datasourceEl.setAttribute( "condition", "${user.uid == 'aron'}" );
        Datasource ds = new Datasource( datasourceEl );

        assertFalse( executor.isRunnableByCondition( ds ) );
    }

    @Test
    public void testIsblank()
    {
        RequestParameters requestParameters = new RequestParameters();

        context.setRequestParameters( requestParameters );

        Element datasourceEl = new Element( "datasource" );

        datasourceEl.setAttribute( "condition", "${ isblank( param.myParam ) }" );

        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", "value" );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", " " );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", "" );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.removeParameter( "myParam" );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );
    }

    @Test
    public void testIsnotblank()
    {
        RequestParameters requestParameters = new RequestParameters();

        context.setRequestParameters( requestParameters );

        Element datasourceEl = new Element( "datasource" );

        datasourceEl.setAttribute( "condition", "${ isnotblank( param.myParam ) }" );

        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", "value" );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", " " );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", "" );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.removeParameter( "myParam" );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );
    }

    @Test
    public void testIsempty()
    {
        RequestParameters requestParameters = new RequestParameters();

        context.setRequestParameters( requestParameters );

        Element datasourceEl = new Element( "datasource" );

        datasourceEl.setAttribute( "condition", "${ isempty( param.myParam ) }" );

        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", "value" );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", " " );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", "" );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.removeParameter( "myParam" );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );
    }

    @Test
    public void testIsnotempty()
    {
        RequestParameters requestParameters = new RequestParameters();

        context.setRequestParameters( requestParameters );

        Element datasourceEl = new Element( "datasource" );

        datasourceEl.setAttribute( "condition", "${ isnotempty( param.myParam ) }" );

        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", "value" );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", " " );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.setParameterValue( "myParam", "" );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        requestParameters.removeParameter( "myParam" );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );
    }

    @Test
    public void testIsWindowInline()
    {
        Element datasourceEl = new Element( "datasource" );
        datasourceEl.setAttribute( "condition", "${ portal.isWindowInline == true }" );

        context.setPortletWindowRenderedInline( true );
        assertTrue( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );

        context.setPortletWindowRenderedInline( false );
        assertFalse( new DatasourceExecutor( context ).isRunnableByCondition( new Datasource( datasourceEl ) ) );
    }


}

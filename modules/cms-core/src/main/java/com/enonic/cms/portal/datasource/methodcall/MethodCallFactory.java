/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.methodcall;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.util.TStringArrayList;

import com.enonic.cms.portal.datasource.DatasourceException;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.portal.InvocationCache;
import com.enonic.cms.portal.datasource.DatasourceExecutorContext;
import com.enonic.cms.portal.datasource.ExpressionFunctionsExecutor;

import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.domain.portal.datasource.DataSourceContext;
import com.enonic.cms.domain.portal.datasource.Datasource;
import com.enonic.cms.domain.portal.datasource.expressionfunctions.ExpressionContext;

/**
 * Jul 31, 2009
 */
public class MethodCallFactory
{

    public static MethodCall create( final DatasourceExecutorContext context, final Datasource datasource,
                                     final InvocationCache dataSourceObject )
    {
        String methodName = datasource.getMethodName();
        if ( methodName == null )
        {
            return null;
        }

        Class targetClass = dataSourceObject.getTargetClass();

        List parameterEl = datasource.getParameterElements();
        int paramCount = parameterEl.size() + 1;

        Method method = resolveMethod( targetClass, methodName, paramCount, true );
        Class[] paramTypes = method.getParameterTypes();
        MethodCallParameter[] parameters = new MethodCallParameter[paramCount];

        DataSourceContext dataSourceContext = createDataSourceContext( context );
        parameters[0] = new MethodCallParameter( "__context__", dataSourceContext, "false", DataSourceContext.class );

        int paramOffset = 1;
        for ( int i = 0; i < parameterEl.size(); i++ )
        {

            Element paramEl = (Element) parameterEl.get( i );
            String paramName = paramEl.getAttributeValue( "name" );
            try
            {
                int paramterIndex = i + paramOffset;

                parameters[paramterIndex] = createParameter( paramEl, paramTypes[paramterIndex], context );
            }
            catch ( Exception e )
            {
                StringBuffer msg = new StringBuffer();
                msg.append( "Method [" ).append( methodName ).append( "]" );
                msg.append( " has correct number of parameters [" ).append( paramCount ).append( "]" );
                msg.append( ", but parameter number " ).append( i + 1 ).append( " with name [" ).append( paramName ).append( "]" );
                msg.append( " is possibly wrong. Please check documentation." );
                throw new IllegalArgumentException( msg.toString(), e );
            }
        }

        return new MethodCall( dataSourceObject, parameters, method );
    }

    private static MethodCallParameter createParameter( Element parmeterEl, Class paramType, DatasourceExecutorContext context )
    {
        String parameterName = parmeterEl.getAttributeValue( "name" );

        String overrideStr = parmeterEl.getAttributeValue( "override" );
        boolean overrideByRequest = resolveOverridableByRequest( overrideStr );
        boolean overrideBySession = resolveOverridableBySession( overrideStr );

        String defValue = JDOMUtil.getElementText( parmeterEl );
        if ( ( defValue != null ) && defValue.contains( "${" ) )
        {
            defValue = evaluateExpression( defValue, context );
        }

        String value = defValue;

        if ( StringUtils.isNotEmpty( parameterName ) )
        {
            String overridedValue = getOverrideValue( overrideByRequest, overrideBySession, parameterName, context );

            if ( StringUtils.isNotEmpty( overridedValue ) )
            {
                value = overridedValue;
            }
        }

        Object argument = convertParameter( paramType, value );
        return new MethodCallParameter( parameterName, argument, overrideStr, paramType );
    }


    private static String getOverrideValue( boolean overrideByRequest, boolean overrideBySession, String parameterName,
                                            DatasourceExecutorContext context )
    {
        String overridedValue = null;

        RequestParameters requestParameters = context.getRequestParameters();
        RequestParameters.Param requestParam = requestParameters.getParameter( parameterName );

        if ( overrideByRequest && requestParam != null )
        {
            overridedValue = requestParam.getParameterValuesAsCommaSeparatedString();
        }
        else if ( context.getVerticalSession() != null && overrideBySession &&
            context.getVerticalSession().getAttribute( parameterName ) != null )
        {
            overridedValue = context.getVerticalSession().getAttribute( parameterName ).toString();
        }

        return overridedValue;
    }

    private static boolean resolveOverridableBySession( String override )
    {
        return "session".equals( override );
    }

    private static boolean resolveOverridableByRequest( String override )
    {
        return "url".equals( override ) || "true".equals( override );
    }

    private static DataSourceContext createDataSourceContext( DatasourceExecutorContext context )
    {
        DataSourceContext dataSourceContext = new DataSourceContext( context.getPreviewContext() );
        dataSourceContext.setPortalInstanceKey( context.getPortalInstanceKey() );
        dataSourceContext.setShoppingCart( context.getShoppingCart() );
        dataSourceContext.setSiteKey( context.getSite() != null ? context.getSite().getKey() : null );
        dataSourceContext.setUser( context.getUser() );
        return dataSourceContext;
    }

    private static String resolveLocalMethodName( String methodName )
    {
        int pos = methodName.indexOf( '.' );
        if ( pos > 0 )
        {
            return methodName.substring( pos + 1 );
        }
        else
        {
            return methodName;
        }
    }

    private static Method resolveMethod( Class targetClass, String methodName, int numParams, boolean useContext )
    {
        final String localMethodName = resolveLocalMethodName( methodName );

        for ( Method method : targetClass.getMethods() )
        {
            if ( localMethodName.equals( method.getName() ) && ( method.getParameterTypes().length == numParams ) )
            {
                return method;
            }
        }
        throw new DatasourceException(
            "Method [" + localMethodName + "] with [" + ( useContext ? numParams - 1 : numParams ) + "] parameters does not exist" );
    }

    private static Object convertParameter( Class type, String value )
    {

        if ( type == Integer.TYPE )
        {
            try
            {
                return new Integer( value );
            }
            catch ( NumberFormatException e )
            {
                throw new IllegalArgumentException( "Expected value of type Integer, got: " + value, e );
            }
        }
        else if ( type == String.class )
        {
            return value;
        }
        else if ( type == Boolean.TYPE )
        {
            return Boolean.valueOf( value );
        }
        else if ( type == String[].class )
        {
            if ( value == null )
            {
                return null;
            }

            TStringArrayList arrayList = new TStringArrayList();
            String[] stringArray = StringUtil.splitString( value, ',' );
            for ( String strValue : stringArray )
            {
                strValue = strValue.trim();
                if ( !StringUtils.isEmpty( strValue ) )
                {
                    arrayList.add( strValue );
                }
            }

            return arrayList.toNativeArray();
        }
        else if ( type == int[].class )
        {
            if ( value == null )
            {
                return null;
            }

            String[] tmpArray = StringUtil.splitString( value, ',' );
            int[] intArray = new int[tmpArray.length];
            int index = 0;
            for ( String tmp : tmpArray )
            {
                intArray[index++] = Integer.parseInt( tmp.trim() );
            }

            return intArray;
        }
        else
        {
            return null;
        }
    }

    private static String evaluateExpression( String expression, DatasourceExecutorContext context )
    {
        try
        {
            ExpressionContext expressionFunctionsContext = new ExpressionContext();
            expressionFunctionsContext.setContentFromRequest( context.getContentFromRequest() );
            expressionFunctionsContext.setSite( context.getSite() );
            expressionFunctionsContext.setMenuItem( context.getMenuItem() );
            expressionFunctionsContext.setUser( context.getUser() );
            expressionFunctionsContext.setPortalInstanceKey( context.getPortalInstanceKey() );
            expressionFunctionsContext.setLocale( context.getLocale() );
            expressionFunctionsContext.setDeviceClass( context.getDeviceClass() );
            expressionFunctionsContext.setPortletWindowRenderedInline( context.isPortletWindowRenderedInline() );

            ExpressionFunctionsExecutor expressionExecutor = new ExpressionFunctionsExecutor();
            expressionExecutor.setExpressionContext( expressionFunctionsContext );
            expressionExecutor.setHttpRequest( context.getHttpRequest() );
            expressionExecutor.setRequestParameters( context.getRequestParameters() );
            expressionExecutor.setVerticalSession( context.getVerticalSession() );

            return expressionExecutor.evaluate( expression );
        }
        catch ( Exception e )
        {
            throw new DatasourceException( "Failed to evaluate expression", e );
        }
    }
}

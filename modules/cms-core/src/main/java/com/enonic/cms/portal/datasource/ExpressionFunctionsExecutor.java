/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.portal.VerticalSession;
import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionContext;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionFunctionsFactory;
import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionFunctionsStatic;

import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.core.security.user.UserEntity;

public final class ExpressionFunctionsExecutor
{

    private static final ExpressionFactory EXPR_FACTORY = new ExpressionFactoryImpl();

    private RequestParameters requestParameters;

    private VerticalSession verticalSession;

    private HttpServletRequest httpRequest;

    private ExpressionContext expressionContext;

    public void setRequestParameters( RequestParameters requestParameters )
    {
        this.requestParameters = requestParameters;
    }

    public void setExpressionContext( ExpressionContext expressionContext )
    {
        this.expressionContext = expressionContext;
    }

    public void setVerticalSession( VerticalSession verticalSession )
    {
        this.verticalSession = verticalSession;
    }

    public void setHttpRequest( HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public String evaluate( String expression )
        throws Exception
    {
        SimpleContext context = new SimpleContext();
        addFunctions( context );
        context.setVariable( "param", EXPR_FACTORY.createValueExpression( createParameterMap(), Map.class ) );
        context.setVariable( "session", EXPR_FACTORY.createValueExpression( createSessionMap(), Map.class ) );
        context.setVariable( "cookie", EXPR_FACTORY.createValueExpression( createCookieMap(), Map.class ) );
        context.setVariable( "user", EXPR_FACTORY.createValueExpression( createUserMap(), Map.class ) );
        context.setVariable( "portal", EXPR_FACTORY.createValueExpression( createPortalMap(), Map.class ) );

        ValueExpression exp = EXPR_FACTORY.createValueExpression( context, expression, String.class );

        ExpressionFunctionsFactory.get().setContext( expressionContext );

        String evaluatedString;
        try
        {
            evaluatedString = exp.getValue( context ).toString();
        }
        finally
        {
            ExpressionFunctionsFactory.get().removeContext();
        }
        return evaluatedString;
    }

    private Map<String, String> createPortalMap()
    {
        Map<String, String> portalMap = new HashMap<String, String>();

        portalMap.put( "deviceClass", expressionContext.getDeviceClass() );
        portalMap.put( "locale", createLocale() );
        portalMap.put( "instanceKey", createPortalInstanceKey() );
        portalMap.put( "pageKey", createPageKey() );
        portalMap.put( "contentKey", createContentKey() );
        portalMap.put( "windowKey", createWindowKey() );
        portalMap.put( "isWindowInline", createIsWindowInline() );

        return portalMap;

    }

    private String createIsWindowInline()
    {
        if ( expressionContext.isPortletWindowRenderedInline() != null )
        {
            return expressionContext.isPortletWindowRenderedInline().toString();
        }
        return null;
    }

    private String createPortalInstanceKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null )
        {
            return expressionContext.getPortalInstanceKey().toString();
        }
        return null;
    }

    private String createLocale()
    {
        if ( expressionContext.getLocale() != null )
        {
            return expressionContext.getLocale().toString();
        }
        return null;

    }


    private String createWindowKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null && expressionContext.getPortalInstanceKey().isWindow() )
        {
            return expressionContext.getPortalInstanceKey().getWindowKey().asString();
        }
        return null;
    }

    private String createPageKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null )
        {
            MenuItemKey menuItemKey = expressionContext.getPortalInstanceKey().getMenuItemKey();
            if ( menuItemKey != null )
            {
                return menuItemKey.toString();
            }
        }
        return null;
    }

    private String createContentKey()
    {
        if ( expressionContext.getContentFromRequest() == null )
        {
            return null;
        }

        return expressionContext.getContentFromRequest().getKey().toString();
    }

    private Map<String, String> createUserMap()
    {
        UserEntity user = expressionContext.getUser();
        Map<String, String> userMap = new HashMap<String, String>();
        UserStoreEntity userStore = user.getUserStore();
        String userStoreName = "";
        if ( userStore != null )
        {
            userStoreName = userStore.getName();
        }
        String uid = user.getName();
        if ( userStoreName.length() > 0 )
        {
            userMap.put( "qualifiedName", userStoreName + "\\" + uid );
        }
        else
        {
            userMap.put( "qualifiedName", uid );
        }
        userMap.put( "key", user.getKey().toString() );
        userMap.put( "userStore", userStoreName );
        userMap.put( "uid", uid );
        userMap.put( "fullName", user.getDisplayName() );
        return userMap;
    }

    private Map<String, String> createParameterMap()
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( this.requestParameters != null )
        {
            for ( RequestParameters.Param param : this.requestParameters.getParameters() )
            {
                String name = param.getName();
                String value = param.getFirstValue();

                if ( value != null )
                {
                    map.put( name, value );
                }
            }
        }

        return map;
    }

    private Map<String, String> createSessionMap()
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( this.verticalSession != null )
        {
            for ( String name : this.verticalSession.getAttributeNames() )
            {
                Object value = this.verticalSession.getAttribute( name );
                if ( value != null )
                {
                    map.put( name, value.toString() );
                }
            }
        }

        return map;
    }

    private Map<String, String> createCookieMap()
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( this.httpRequest != null )
        {
            Cookie[] cookies = httpRequest.getCookies();
            if ( cookies != null )
            {
                for ( Cookie cookie : cookies )
                {
                    if ( cookie != null )
                    {
                        map.put( cookie.getName(), cookie.getValue() );
                    }
                }
            }
        }
        return map;
    }

    private void addFunctions( SimpleContext context )
    {
        for ( Method method : ExpressionFunctionsStatic.class.getMethods() )
        {
            int modifiers = method.getModifiers();
            if ( Modifier.isPublic( modifiers ) && Modifier.isStatic( modifiers ) )
            {
                addFunction( context, method );
            }
        }
    }

    private void addFunction( SimpleContext context, Method method )
    {
        String name = method.getName();
        String prefix = "";
        String localName = name;

        int pos = name.indexOf( "_" );
        if ( pos > -1 )
        {
            prefix = name.substring( 0, pos );
            localName = name.substring( pos + 1 );
        }

        context.setFunction( prefix, localName, method );
    }


}

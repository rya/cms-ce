/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionContext;

import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionFunctionsFactory;
import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionFunctionsStatic;

import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.core.security.user.UserEntity;

public final class ExpressionFunctionsExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( "EL" );

    private static final ExpressionParser EXPR_FACTORY = new SpelExpressionParser();
    private static final TemplateParserContext TEMPLATE_PARSER_CONTEXT= new TemplateParserContext();


    private RequestParameters requestParameters;

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

    public void setHttpRequest( HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public String evaluate( String expression )
        throws Exception
    {
        // all functions in ExpressionFunctionsStatic are available by default
        StandardEvaluationContext context = new StandardEvaluationContext(new ExpressionFunctionsStatic() {
            public Map<String, String> param = createParameterMap();
            //public Map<String, String> session = createSessionMap(); /* hza: i do not know why session is not supported ! */
            public Map<String, String> cookie = createCookieMap();
            public Map<String, String> user = createUserMap();
            public Map<String, Object> portal = createPortalMap();
        });

        ExpressionFunctionsFactory.get().setContext( expressionContext );

        context.addPropertyAccessor( new SafeMapAccessor() );

        Expression exp = EXPR_FACTORY.parseExpression( expression, TEMPLATE_PARSER_CONTEXT );

        String evaluatedString = "";

        try
        {
            evaluatedString = exp.getValue( context ).toString();
        }
        catch ( Exception e )
        {
            LOG.error( "EL evaluation fails !", e );
        }
        finally
        {
            ExpressionFunctionsFactory.get().removeContext();
        }

        return evaluatedString;
    }


    private Map<String, Object> createPortalMap()
    {
        Map<String, Object> portalMap = new HashMap<String, Object>();

        portalMap.put( "deviceClass", expressionContext.getDeviceClass() );
        portalMap.put( "locale", createLocale() );
        portalMap.put( "instanceKey", createPortalInstanceKey() );
        portalMap.put( "pageKey", createPageKey() );
        portalMap.put( "siteKey", createSiteKey() );
        portalMap.put( "contentKey", createContentKey() );
        portalMap.put( "windowKey", createWindowKey() );
        portalMap.put( "isWindowInline", createIsWindowInline() );

        return portalMap;

    }

    private Boolean createIsWindowInline()
    {
        if ( expressionContext.isPortletWindowRenderedInline() != null )
        {
            return expressionContext.isPortletWindowRenderedInline();
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

    private String createSiteKey()
    {
        return expressionContext.getSite().getKey().toString();
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

    /**
     *  does not throw Exception if map does not contain key
     */
    private static class SafeMapAccessor
            extends MapAccessor
    {
        @Override
        public TypedValue read( EvaluationContext context, Object target, String name )
                throws AccessException
        {
            Map map = (Map) target;
            Object value = map.get(name);
            if (value == null && !map.containsKey(name)) {
                return TypedValue.NULL;
            }
            return new TypedValue(value);
        }

        @Override
        public boolean canRead( EvaluationContext context, Object target, String name )
                throws AccessException
        {
            return true;
        }
    }

    /**
     *  support for ${} format. SPEL by default uses #{} format
     */
    private static class TemplateParserContext
            implements ParserContext
    {
        public String getExpressionPrefix()
        {
            return "${";
        }

        public String getExpressionSuffix()
        {
            return "}";
        }

        public boolean isTemplate()
        {
            return true;
        }
    }}

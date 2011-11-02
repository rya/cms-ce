/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.enonic.esl.net.URLUtil;
import com.enonic.esl.servlet.http.CookieUtil;

import com.enonic.cms.core.DeploymentPathResolver;

/**
 * Created by rmy - Date: May 5, 2009
 */
public class ForceResolverValueServiceImpl
    implements ForceResolverValueService
{

    public static final int FORCED_VALUE_COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24 * 365 * 50; // 50 years

    public String getForcedResolverValue( ResolverContext context, String forcedValueKey )
    {

        String forcedDeviceClass = getForcedResolverValueFromCookie( context.getRequest(), forcedValueKey );

        if ( StringUtils.isNotEmpty( forcedDeviceClass ) )
        {
            return forcedDeviceClass;
        }

        return getForcedDeviceClassFromSession( context.getRequest(), forcedValueKey );
    }

    public void setForcedValue( ResolverContext context, HttpServletResponse response, String forcedValueKey,
                                ForcedResolverValueLifetimeSettings lifeTimeSettings, String forcedValue )
    {

        switch ( lifeTimeSettings )
        {
            case permanent:
                setForcedValueInCookie( context.getRequest(), response, forcedValueKey, forcedValue );
                break;
            case session:
                setForcedValueInSession( context.getRequest(), forcedValueKey, forcedValue );
        }
    }

    public void clearForcedValue( ResolverContext context, HttpServletResponse response, String forcedValueKey )
    {
        removeSessionAttribute( context.getRequest(), forcedValueKey );

        if ( CookieUtil.getCookie( context.getRequest(), forcedValueKey ) != null )
        {
            CookieUtil.setCookie( response, forcedValueKey, "", 0, getSiteDeploymentPath( context.getRequest() ) );
        }
    }

    private void removeSessionAttribute( HttpServletRequest request, String toBeRemoved )
    {
        Enumeration attributeNames = request.getSession().getAttributeNames();

        while ( attributeNames != null && attributeNames.hasMoreElements() )
        {
            String attributeName = (String) attributeNames.nextElement();
            if ( attributeName.startsWith( toBeRemoved ) )
            {
                request.getSession().removeAttribute( attributeName );
            }
        }
    }

    private void setForcedValueInCookie( HttpServletRequest request, HttpServletResponse response, String cookieName, String forcedValue )
    {
        CookieUtil.setCookie( response, cookieName, encodeCookieValue( forcedValue ), FORCED_VALUE_COOKIE_MAX_AGE_SECONDS,
                              getSiteDeploymentPath( request ) );
    }

    private void setForcedValueInSession( HttpServletRequest request, String sessionKey, String deviceClass )
    {
        request.getSession().setAttribute( sessionKey, deviceClass );
    }


    private String getForcedDeviceClassFromSession( HttpServletRequest request, String sessionKey )
    {
        return (String) request.getSession().getAttribute( sessionKey );
    }

    private String getForcedResolverValueFromCookie( HttpServletRequest request, String forcedValueKey )
    {

        Cookie cookie = CookieUtil.getCookie( request, forcedValueKey );

        return cookie == null || cookie.getMaxAge() == 0 ? null : decodeCookieValue( cookie.getValue() );
    }

    protected String encodeCookieValue( String value )
    {
        return URLUtil.encode( value );
    }

    protected String decodeCookieValue( String value )
    {
        return URLUtil.decode( value );
    }

    protected String getSiteDeploymentPath( HttpServletRequest request )
    {
        return DeploymentPathResolver.getSiteDeploymentPath( request );
    }

}

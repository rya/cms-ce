/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rmy - Date: May 5, 2009
 */
public class CacheResolverValueServiceImpl
    implements CacheResolverValueService
{


    public String getCachedResolverValue( ResolverContext context, String sessionDeviceClassKey )
    {
        HttpServletRequest request = context.getRequest();

        return (String) request.getSession().getAttribute( sessionDeviceClassKey );
    }

    public boolean setCachedResolverValue( ResolverContext context, String deviceClass, String cacheKey )
    {
        setResolverValueInSession( context.getRequest(), deviceClass, cacheKey );
        return true;
    }

    public boolean clearCachedResolverValue( ResolverContext context, String cacheKey )
    {
        removeSessionAttribute( context.getRequest(), cacheKey );
        return true;
    }

    protected void removeSessionAttribute( HttpServletRequest request, String toBeRemoved )
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

    private void setResolverValueInSession( HttpServletRequest request, String deviceClass, String cacheKey )
    {
        request.getSession().setAttribute( cacheKey, deviceClass );
    }

}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This class implements method to be used to access the upgrade checker bean from a servlet. It is looked up using the application
 * context.
 */
public final class UpgradeCheckerHelper
{
    /**
     * Return the upgrade checker.
     */
    private static UpgradeChecker getUpgradeChecker( ServletContext context )
        throws ServletException
    {
        return new UpgradeChecker( getUpgradeService( context ) );
    }

    /**
     * Return the upgrade service.
     */
    private static UpgradeService getUpgradeService( ServletContext context )
        throws ServletException
    {
        return getUpgradeService( WebApplicationContextUtils.getRequiredWebApplicationContext( context ) );
    }


    /**
     * Return the upgrade service.
     */
    private static UpgradeService getUpgradeService( ApplicationContext context )
        throws ServletException
    {
        Map map = context.getBeansOfType( UpgradeService.class );
        if ( map.isEmpty() )
        {
            throw new ServletException( "No UpgradeService bean registered" );
        }
        else if ( map.size() > 1 )
        {
            throw new ServletException( "Only one UpgradeService should be defined" );
        }
        else
        {
            return (UpgradeService) map.values().iterator().next();
        }
    }

    /**
     * Check the upgrade.
     */
    public static boolean checkUpgrade( ServletContext context, HttpServletResponse res )
        throws ServletException, IOException
    {
        return getUpgradeChecker( context ).checkUpgrade( res );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.expressionfunctions;

import com.enonic.cms.core.structure.menuitem.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.preferences.PreferenceService;

import com.enonic.cms.domain.portal.datasource.expressionfunctions.ExpressionContext;


public class ExpressionFunctionsFactory
{
    private static ExpressionFunctionsFactory instance;

    private PreferenceService preferenceService;

    private TimeService timeService;

    private MenuItemService menuItemService;

    private ThreadLocal<ExpressionContext> context = new ThreadLocal<ExpressionContext>();

    public static ExpressionFunctionsFactory get()
    {
        return instance;
    }

    public ExpressionFunctionsFactory()
    {
        instance = this;
    }

    public void setContext( ExpressionContext value )
    {
        context.set( value );
    }

    public ExpressionContext getContext()
    {
        return context.get();
    }

    public void removeContext()
    {
        context.remove();
    }

    public ExpressionFunctions createExpressionFunctions()
    {
        ExpressionFunctions expressionFunctions = new ExpressionFunctions();
        expressionFunctions.setPreferenceService( preferenceService );
        expressionFunctions.setContext(getContext());
        expressionFunctions.setTimeService(timeService);
        expressionFunctions.setMenuItemService(menuItemService);
        return expressionFunctions;
    }

    @Autowired
    public void setPreferenceService( PreferenceService preferenceService )
    {
        this.preferenceService = preferenceService;
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }


    @Autowired
    public void setMenuItemService( MenuItemService menuItemService )
    {
        this.menuItemService = menuItemService;
    }
}

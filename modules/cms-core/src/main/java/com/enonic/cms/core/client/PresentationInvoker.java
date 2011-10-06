/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import org.jdom.Document;

import com.enonic.cms.api.client.model.GetCategoriesParams;
import com.enonic.cms.api.client.model.GetMenuBranchParams;
import com.enonic.cms.api.client.model.GetMenuDataParams;
import com.enonic.cms.api.client.model.GetMenuItemParams;
import com.enonic.cms.api.client.model.GetMenuParams;
import com.enonic.cms.api.client.model.GetSubMenuParams;
import com.enonic.cms.core.service.DataSourceService;

import com.enonic.cms.business.core.security.SecurityService;

import com.enonic.cms.domain.portal.datasource.DataSourceContext;

/**
 * This class wraps the presentation service and calls with the new client api.
 */
public final class PresentationInvoker
{

    private final DataSourceService dataSourceService;

    private final SecurityService securityService;

    public PresentationInvoker( DataSourceService dataSourceService, SecurityService securityService )
    {
        this.dataSourceService = dataSourceService;
        this.securityService = securityService;
    }

    private DataSourceContext createDataSourceContext()
    {
        DataSourceContext dataSourceContext = new DataSourceContext();
        dataSourceContext.setUser( securityService.getRunAsUser() );
        return dataSourceContext;
    }

    public Document getCategories( GetCategoriesParams params )
        throws Exception
    {
        assertMinValue( "categoryKey", params.categoryKey, 0 );
        return this.dataSourceService.getCategories( createDataSourceContext(), params.categoryKey, params.levels,
                                                     params.includeTopCategory, true, false,
                                                     params.includeContentCount ).getAsJDOMDocument();
    }

    public Document getMenu( GetMenuParams params )
        throws Exception
    {
        assertMinValue( "menuKey", params.menuKey, 0 );

        return this.dataSourceService.getMenu( createDataSourceContext(), params.menuKey, params.tagItem, params.levels,
                                               false ).getAsJDOMDocument();
    }

    public Document getMenuBranch( GetMenuBranchParams params )
        throws Exception
    {
        assertMinValue( "menuItemKey", params.menuItemKey, 0 );

        return this.dataSourceService.getMenuBranch( createDataSourceContext(), params.menuItemKey, params.includeTopLevel,
                                                     params.startLevel, params.levels ).getAsJDOMDocument();
    }

    public Document getMenuData( GetMenuDataParams params )
        throws Exception
    {
        assertMinValue( "menuKey", params.menuKey, 0 );

        return this.dataSourceService.getMenuData( createDataSourceContext(), params.menuKey ).getAsJDOMDocument();
    }

    public Document getMenuItem( GetMenuItemParams params )
        throws Exception
    {
        assertMinValue( "menuItemKey", params.menuItemKey, 0 );

        return this.dataSourceService.getMenuItem( createDataSourceContext(), params.menuItemKey, params.withParents,
                                                   params.details ).getAsJDOMDocument();
    }

    public Document getSubMenu( GetSubMenuParams params )
        throws Exception
    {
        assertMinValue( "menuItemKey", params.menuItemKey, 0 );
        return this.dataSourceService.getSubMenu( createDataSourceContext(), params.menuItemKey, params.tagItem, params.levels,
                                                  false ).getAsJDOMDocument();
    }

    private void assertMinValue( String name, int value, int minValue )
    {
        if ( value < minValue )
        {
            throw new IllegalArgumentException( "Parameter [" + name + "] must be >= " + minValue );
        }
    }
}

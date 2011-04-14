/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.portal.datasource.DatasourceExecutorFactory;

public abstract class BaseEngine
{
    protected DatasourceExecutorFactory datasourceExecutorFactory;

    protected DataSourceService dataSourceService;

    public void setDataSourceService( DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }

    public void setDatasourceExecutorFactory( DatasourceExecutorFactory value )
    {
        this.datasourceExecutorFactory = value;
    }
}

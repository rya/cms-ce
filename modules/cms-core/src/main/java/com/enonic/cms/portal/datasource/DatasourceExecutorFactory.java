/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import javax.inject.Inject;

import com.enonic.cms.portal.datasource.context.DatasourcesContextXmlCreator;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;

/**
 * Apr 20, 2009
 */
public class DatasourceExecutorFactory
{
    @Inject
    private DatasourcesContextXmlCreator datasourcesContextXmlCreator;

    @Inject
    private LivePortalTraceService livePortalTraceService;

    public DatasourceExecutor createDatasourceExecutor( DatasourceExecutorContext datasourceExecutorContext )
    {
        DatasourceExecutor dataSourceExecutor = new DatasourceExecutor( datasourceExecutorContext );
        dataSourceExecutor.setDatasourcesContextXmlCreator( datasourcesContextXmlCreator );
        dataSourceExecutor.setLivePortalTraceService( livePortalTraceService );
        return dataSourceExecutor;
    }
}

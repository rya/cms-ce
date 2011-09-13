/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.datasource;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.business.portal.datasource.context.DatasourcesContextXmlCreator;
import com.enonic.cms.business.portal.livetrace.LivePortalTraceService;

/**
 * Apr 20, 2009
 */
public class DatasourceExecutorFactory
{
    @Autowired
    private DatasourcesContextXmlCreator datasourcesContextXmlCreator;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    public DatasourceExecutor createDatasourceExecutor( DatasourceExecutorContext datasourceExecutorContext )
    {
        DatasourceExecutor dataSourceExecutor = new DatasourceExecutor( datasourceExecutorContext );
        dataSourceExecutor.setDatasourcesContextXmlCreator( datasourcesContextXmlCreator );
        dataSourceExecutor.setLivePortalTraceService( livePortalTraceService );
        return dataSourceExecutor;
    }
}

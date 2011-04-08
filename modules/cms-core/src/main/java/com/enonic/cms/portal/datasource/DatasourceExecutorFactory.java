/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.portal.datasource.context.DatasourcesContextXmlCreator;

/**
 * Apr 20, 2009
 */
public class DatasourceExecutorFactory
{
    @Autowired
    private DatasourcesContextXmlCreator datasourcesContextXmlCreator;

    public DatasourceExecutor createDatasourceExecutor( DatasourceExecutorContext datasourceExecutorContext )
    {
        DatasourceExecutor dataSourceExecutor = new DatasourceExecutor( datasourceExecutorContext );
        dataSourceExecutor.setDatasourcesContextXmlCreator( datasourcesContextXmlCreator );
        return dataSourceExecutor;
    }
}

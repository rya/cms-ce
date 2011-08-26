/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.store.support.ConnectionTraceInfo;
import com.enonic.cms.store.support.DecoratedDataSource;
import com.enonic.cms.store.support.TraceableDataSource;

/**
 * This class implements the connection info controller.
 */
public final class ConnectionInfoController
    extends AbstractToolController
{
    private DataSource dataSource;

    private boolean isTraceEnabled()
    {
        return getTraceableDataSource() != null;
    }

    private TraceableDataSource getTraceableDataSource()
    {
        if ( this.dataSource instanceof TraceableDataSource )
        {
            return (TraceableDataSource) this.dataSource;
        }
        else if ( this.dataSource instanceof DecoratedDataSource )
        {
            DataSource ds = ( (DecoratedDataSource) this.dataSource ).getWrappedDataSource();
            if ( ds instanceof TraceableDataSource )
            {
                return (TraceableDataSource) ds;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    private Collection<String> getConnectionList()
    {
        TraceableDataSource ds = getTraceableDataSource();
        ArrayList<String> list = new ArrayList<String>();

        if ( ds != null )
        {
            for ( ConnectionTraceInfo e : ds.getTraceInfo() )
            {
                list.add( e.toString() );
            }
        }

        return list;
    }

    @Override
    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "enabled", isTraceEnabled() ? 1 : 0 );
        model.put( "connlist", getConnectionList() );
        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        process( res, model, "connectionInfoPage" );
    }

    @Autowired
    public void setDataSource( DataSource value )
    {
        this.dataSource = value;
    }

}

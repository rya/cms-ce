/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.store.support.ConnectionTraceInfo;
import com.enonic.cms.store.support.TraceableDataSource;

/**
 * This class implements the connection info controller.
 */
@RequestMapping(value = "/tools/connectioninfo")
public final class ConnectionInfoController
    extends AbstractToolController
{
    private DataSource dataSource;

    @Autowired
    public void setDataSource( DataSource value )
    {
        this.dataSource = value;
    }

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

    /**
     * Handle the request.
     */
    protected ModelAndView doHandleRequest( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "enabled", isTraceEnabled() ? 1 : 0 );
        model.put( "connlist", getConnectionList() );
        return new ModelAndView( "connectionInfoPage", model );
    }
}

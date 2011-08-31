/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URL;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.business.tools.ReindexContentToolService;

public class ReindexContentToolController
    extends AbstractToolController
{

    @Autowired
    private ReindexContentToolService reindexContentToolService;

    private List<String> logEntries = new ArrayList<String>();

    private Boolean reindexingInProgress = Boolean.FALSE;

    @Override
    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {
        if ( req.getParameter( "reindex" ) != null )
        {
            startReindexAllContentTypes();

            try
            {
                URL referer = new URL( req.getHeader( "referer" ) );
                redirectClientToURL( referer, res );
            }
            catch ( Exception e )
            {
                //TODO : FIX, what happend in tools
            }
        }

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "reindexInProgress", reindexingInProgress );
        model.put( "reindexLog", logEntries );
        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );

        process( res, model, "reindexContentPage" );
    }

    private synchronized void startReindexAllContentTypes()
    {

        if ( reindexingInProgress )
        {
            return;
        }

        reindexingInProgress = Boolean.TRUE;

        Thread reindexThread = new Thread( new Runnable()
        {
            public void run()
            {

                try
                {
                    reindexContentToolService.reindexAllContent( logEntries );
                }
                finally
                {
                    reindexingInProgress = Boolean.FALSE;
                }
            }
        }, "Reindex Content Thread" );

        reindexThread.start();
    }

    public void setReindexContentToolService( ReindexContentToolService value )
    {
        this.reindexContentToolService = value;
    }
}

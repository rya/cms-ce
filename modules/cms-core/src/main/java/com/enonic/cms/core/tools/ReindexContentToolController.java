/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.core.tools.ReindexContentToolService;

@RequestMapping(value = "/tools/reindexcontent")
public class ReindexContentToolController
    extends AbstractToolController
{

    private ReindexContentToolService reindexContentToolService;

    private List<String> logEntries = new ArrayList<String>();

    private Boolean reindexingInProgress = Boolean.FALSE;

    protected ModelAndView doHandleRequest( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        if ( req.getParameter( "reindex" ) != null )
        {
            startReindexAllContentTypes();
            redirectToSelf( req, res );
        }

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "reindexInProgress", reindexingInProgress );
        model.put( "reindexLog", logEntries );
        model.put( "baseUrl", createBaseUrl( req ) );

        return new ModelAndView( "reindexContentPage", model );
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

    @Autowired
    public void setReindexContentToolService( ReindexContentToolService value )
    {
        this.reindexContentToolService = value;
    }
}

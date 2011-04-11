/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preview;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.domain.Attribute;

/**
 * Sep 30, 2010
 */
class PreviewServiceImpl
    implements PreviewService
{
    public boolean isInPreview()
    {
        HttpServletRequest request = doGetRequest();
        return "true".equals( request.getAttribute( Attribute.PREVIEW_ENABLED ) );
    }

    public PreviewContext getPreviewContext()
    {
        if ( !isInPreview() )
        {
            return PreviewContext.NO_PREVIEW;
        }

        return doGetPreviewContext();
    }

    public void setPreviewContext( PreviewContext previewContext )
    {
        HttpSession session = doGetSession();

        if ( previewContext.isPreviewingContent() )
        {
            NoLazyInitializationEnforcerForPreview.enforceNoLazyInitialization(
                previewContext.getContentPreviewContext().getContentPreviewed() );
        }

        session.setAttribute( "_preview-context", previewContext );
    }

    private PreviewContext doGetPreviewContext()
    {
        HttpSession session = doGetSession();
        PreviewContext previewContext = (PreviewContext) session.getAttribute( "_preview-context" );
        Preconditions.checkNotNull( session, "Expected preview context to exist in session" );
        return previewContext;
    }

    private HttpSession doGetSession()
    {
        HttpServletRequest servletRequest = doGetRequest();
        HttpSession session = servletRequest.getSession( false );
        Preconditions.checkNotNull( session, "Expected HttpServletRequest to have a session" );
        return session;
    }

    private HttpServletRequest doGetRequest()
    {
        HttpServletRequest request = ServletRequestAccessor.getRequest();
        Preconditions.checkNotNull( request, "Expected ServletRequestAccessor to return a request" );
        return request;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.google.common.io.ByteStreams;

import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.server.domain.content.binary.AttachmentRequestResolver;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.business.core.content.binary.BinaryService;
import com.enonic.cms.business.core.security.SecurityService;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.PathAndParams;
import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.binary.AttachmentRequest;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.security.user.User;

public class AttachmentController
    extends AbstractController
    implements InitializingBean
{
    private BinaryService binaryService;

    private ContentDao contentDao;

    private SecurityService securityService;

    private AttachmentRequestResolver attachmentRequestResolver;

    private UrlPathHelper urlEncodingUrlPathHelper;


    public void afterPropertiesSet()
        throws Exception
    {

        attachmentRequestResolver = new AttachmentRequestResolver()
        {

            protected BinaryDataKey getBinaryData( ContentEntity content, String label )
            {
                BinaryDataEntity binaryData = content.getMainVersion().getSingleBinaryData( label );
                if ( binaryData != null )
                {
                    return new BinaryDataKey( binaryData.getKey() );
                }
                return null;
            }

            protected ContentEntity getContent( ContentKey contentKey )
            {
                return contentDao.findByKey( contentKey );
            }
        };

    }

    protected final ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {

        User loggedInUser = securityService.getLoggedInAdminConsoleUser();

        final PathAndParams pathAndParams = resolvePathAndParams( request );
        AttachmentRequest attachmentRequest = attachmentRequestResolver.resolveBinaryDataKey( pathAndParams );
        BinaryDataEntity binaryData = binaryService.getBinaryDataForAdmin( loggedInUser, attachmentRequest.getBinaryDataKey() );

        boolean download = "true".equals( request.getParameter( "download" ) );
        download |= "true".equals( request.getParameter( "_download" ) );

        putBinaryOnResponse( download, response, binaryData );
        return null;
    }

    private PathAndParams resolvePathAndParams( HttpServletRequest request )
    {
        @SuppressWarnings({"unchecked"}) Map<String, String[]> parameterMap = request.getParameterMap();

        RequestParameters requestParameters = new RequestParameters( parameterMap );
        String pathAsString = urlEncodingUrlPathHelper.getRequestUri( request );
        Path path = new Path( pathAsString );

        return new PathAndParams( path, requestParameters );
    }

    private void putBinaryOnResponse( boolean download, HttpServletResponse response, BinaryDataEntity binaryData )
        throws IOException
    {
        final BlobRecord blob = this.binaryService.fetchBinary( binaryData.getBinaryDataKey() );
        HttpServletUtil.setContentDisposition( response, download, binaryData.getName() );

        response.setContentType( HttpServletUtil.resolveMimeType( getServletContext(), binaryData.getName() ) );
        response.setContentLength( (int) blob.getLength() );

        ByteStreams.copy( blob.getStream(), response.getOutputStream() );
    }

    public void setBinaryService( BinaryService value )
    {
        this.binaryService = value;
    }

    public void setContentDao( ContentDao dao )
    {
        contentDao = dao;
    }

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    public void setUrlEncodingUrlPathHelper( UrlPathHelper value )
    {
        this.urlEncodingUrlPathHelper = value;
    }
}
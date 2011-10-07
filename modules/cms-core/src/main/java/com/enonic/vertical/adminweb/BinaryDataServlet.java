/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.security.user.User;

public class BinaryDataServlet
    extends AbstractAdminwebServlet
{

    public void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        int key = 0;
        BinaryData binaryData;
        HttpSession session = request.getSession( false );

        if ( session != null )
        {
            try
            {
                AdminService admin = lookupAdminBean();
                User user = securityService.getLoggedInAdminConsoleUser();
                String keyStr = request.getParameter( "id" );

                if ( keyStr != null && keyStr.length() > 0 )
                {
                    key = Integer.parseInt( keyStr );
                }
                else
                {
                    String pathInfo = request.getPathInfo();
                    if ( pathInfo != null )
                    {
                        if ( pathInfo.startsWith( "/" ) )
                        {
                            pathInfo = pathInfo.substring( 1 );
                        }

                        StringTokenizer st = new StringTokenizer( pathInfo, "/" );

                        // pathInfo should be /binary/xx/file in 4.1, was /xx/file in 4.0
                        if ( st.countTokens() < 3 )
                        {
                            String message = "Error in binary path: \"%0\"";
                            VerticalAdminLogger.error( this.getClass(), 0, message, pathInfo, null );
                            return;
                        }

                        // skip 'binary'
                        st.nextToken();

                        String contentKeyStr = st.nextToken();
                        int contentKey;
                        try
                        {
                            contentKey = Integer.parseInt( contentKeyStr );
                        }
                        catch ( NumberFormatException nfe )
                        {
                            String message = "Content key is not a number: \"%0\"";
                            VerticalAdminLogger.error( this.getClass(), 0, message, contentKeyStr, null );
                            return;
                        }

                        String label = st.nextToken();

                        key = admin.getBinaryDataKey( contentKey, label );

                        if ( key == -1 )
                        {
                            String message = "Binary key not found for path: \"%0\"";
                            VerticalAdminLogger.error( this.getClass(), 0, message, pathInfo, null );
                            return;
                        }
                    }
                }

                binaryData = admin.getBinaryData( user, key );

                if ( binaryData != null )
                {
                    String mimeType = getServletContext().getMimeType( binaryData.fileName.toLowerCase() );
                    if ( mimeType == null )
                    {
                        mimeType = "www/unknown";
                    }

                    response.setContentType( mimeType );
                    HttpServletUtil.setContentDisposition( response, false, binaryData.fileName );
                    response.setContentLength( binaryData.data.length );
                    OutputStream os = response.getOutputStream();
                    os.write( binaryData.data );
                    os.flush();
                }
            }
            catch ( Exception e )
            {
                String message = "Failed to get binary data: %t";
                VerticalAdminLogger.error( this.getClass(), 0, message, e );
            }
        }
    }
}
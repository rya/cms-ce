/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;

import com.enonic.cms.core.resource.access.ResourceAccessResolver;
import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.domain.security.user.QualifiedUsername;

/**
 * This class implements the session provider.
 */
public final class DavSessionProviderImpl
    implements DavSessionProvider
{

    /**
     * Security service.
     */
    private final SecurityService securityService;


    private DavAccessResolver accessResolver;

    /**
     * Construct the provier.
     */
    public DavSessionProviderImpl( SecurityService securityService, ResourceAccessResolver resourceAccessResolver )
    {
        this.securityService = securityService;
        this.accessResolver = new DavAccessResolverImpl( resourceAccessResolver );
    }

    /**
     * {@inheritDoc}
     */
    public boolean attachSession( WebdavRequest request )
        throws DavException
    {
        DavSession session = createSession( request );
        if ( session != null )
        {
            request.setDavSession( session );
        }

        return session != null;
    }

    /**
     * {@inheritDoc}
     */
    public void releaseSession( WebdavRequest request )
    {
        request.setDavSession( null );
    }

    /**
     * Login the user.
     */
    private DavSession createSession( WebdavRequest request )
        throws DavException
    {
        String[] auth = getCredentials( request );
        if ( auth == null )
        {
            throw new DavException( DavServletResponse.SC_UNAUTHORIZED );
        }
        if ( !login( auth[0], auth[1] ) )
        {
            throw new DavException( DavServletResponse.SC_UNAUTHORIZED );
        }

        return new DavSessionImpl();
    }

    /**
     * Return the credentials.
     */
    private String[] getCredentials( WebdavRequest request )
        throws DavException
    {
        try
        {
            String authHeader = request.getHeader( DavConstants.HEADER_AUTHORIZATION );
            if ( authHeader != null )
            {
                String[] authStr = authHeader.split( " " );
                if ( authStr.length >= 2 && authStr[0].equalsIgnoreCase( HttpServletRequest.BASIC_AUTH ) )
                {
                    String decAuthStr = new String( Base64.decodeBase64( authStr[1].getBytes() ), "ISO-8859-1" );
                    int pos = decAuthStr.indexOf( ':' );
                    String userid = decAuthStr.substring( 0, pos );
                    String passwd = decAuthStr.substring( pos + 1 );
                    return new String[]{userid, passwd};
                }
            }

            return null;
        }
        catch ( Exception e )
        {
            throw new DavException( DavServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    /**
     * Login the user. Returns true if it has access.
     */
    private boolean login( String user, String password )
    {
        try
        {
            securityService.loginDavUser( QualifiedUsername.parse( user ), password );
            return accessResolver.hasAccess( securityService.getLoggedInPortalUserAsEntity() );
        }
        catch ( Exception e )
        {
            return false;
        }
    }
}

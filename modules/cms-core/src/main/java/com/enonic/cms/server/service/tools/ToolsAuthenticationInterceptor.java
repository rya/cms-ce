package com.enonic.cms.server.service.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.enonic.cms.domain.security.user.User;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 8/10/11
 * Time: 10:49 AM
 */
public final class ToolsAuthenticationInterceptor
    extends HandlerInterceptorAdapter
{

    private static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";

    private static final String WWW_AUTHENTICATE_VALUE = "Basic";

    private static final String WWW_AUTHORIZATION_HEADER = "Authorization";

    private String entrerpriseAdminPassword;

    private ToolsAccessResolver toolsAccessResolver;

    public boolean preHandle( HttpServletRequest req, HttpServletResponse res, Object o )
        throws Exception
    {
        if ( toolsAccessResolver.hasAccess( req ) )
        {
            return super.preHandle( req, res, o );
        }

        return authenticateByBasicAuthentication( req, res, o );
    }

    private boolean authenticateByBasicAuthentication( HttpServletRequest req, HttpServletResponse res, Object o )
        throws Exception
    {
        String[] cred = getAuthCredentials( req );

        if ( cred != null && cred.length == 2 )
        {
            boolean isAuthenticated = authenticate( cred[0], cred[1] );

            if ( isAuthenticated )
            {
                return super.preHandle( req, res, o );
            }
        }

        res.setHeader( WWW_AUTHENTICATE_HEADER, WWW_AUTHENTICATE_VALUE );
        res.setStatus( HttpServletResponse.SC_UNAUTHORIZED );

        return false;
    }


    private String[] getAuthCredentials( HttpServletRequest req )
    {
        String auth = req.getHeader( WWW_AUTHORIZATION_HEADER );
        if ( auth == null )
        {
            return null;
        }

        String[] tmp = auth.split( " " );
        if ( tmp.length < 2 )
        {
            return null;
        }

        if ( !WWW_AUTHENTICATE_VALUE.equalsIgnoreCase( tmp[0] ) )
        {
            return null;
        }

        String authStr = new String( Base64.decodeBase64( tmp[1].getBytes() ) );

        String[] credentials = authStr.split( ":" );

        if ( credentials == null )
        {
            return null;
        }

        // Set blank password if none provided
        if ( credentials.length == 1 )
        {
            return new String[]{credentials[0], ""};
        }
        else if ( credentials.length == 2 )
        {
            return credentials;
        }
        else
        {
            return null;
        }
    }

    private boolean authenticate( String user, String password )
    {
        return User.ROOT_UID.equals( user ) && this.entrerpriseAdminPassword.equals( password );
    }


    @Value("${cms.admin.password}")
    public void setEntrerpriseAdminPassword( String password )
    {
        this.entrerpriseAdminPassword = password;
    }

    /**
     * Set access resolver.
     */
    @Autowired
    public void setToolsAccessResolver( ToolsAccessResolver toolsAccessResolver )
    {
        this.toolsAccessResolver = toolsAccessResolver;
    }
}

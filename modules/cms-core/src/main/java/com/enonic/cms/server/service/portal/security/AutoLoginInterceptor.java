package com.enonic.cms.server.service.portal.security;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.server.service.servlet.OriginalPathResolver;

import com.enonic.cms.business.core.security.SecurityService;
import com.enonic.cms.core.plugin.ExtensionManager;

import com.enonic.cms.domain.security.user.QualifiedUsername;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * This interceptor executes any auto login plugins available.
 */
public final class AutoLoginInterceptor
    extends HandlerInterceptorAdapter
{
    private final static Logger LOG = Logger.getLogger( AutoLoginInterceptor.class.getName() );

    private ExtensionManager pluginManager;

    private SecurityService securityService;

    private OriginalPathResolver originalPathResolver = new OriginalPathResolver();

    public void setPluginManager( ExtensionManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    /**
     * Execute the auto login, if an auto login plugin has been configured.
     */
    public boolean preHandle( HttpServletRequest req, HttpServletResponse res, Object o )
        throws Exception
    {

        String path = originalPathResolver.getRequestPathFromHttpRequest( req );
        HttpAutoLogin plugin = pluginManager.findMatchingHttpAutoLoginPlugin( path );

        if ( plugin != null )
        {
            doAutoLogin( req, plugin );
        }

        return super.preHandle( req, res, o );
    }

    private void doAutoLogin( HttpServletRequest req, HttpAutoLogin plugin )
    {
        UserEntity current = securityService.getLoggedInPortalUserAsEntity();

        if ( !current.isAnonymous() )
        {
            if ( current.isEnterpriseAdmin() )
            {
                LOG.finest( "Already logged in as Enterprise Admin. Skipping auto-login." );
                return;
            }

            LOG.finest( "Already logged in. Checking if current user equals SSO user." );
            boolean currentUserIsValid = plugin.validateCurrentUser( current.getName(), current.getUserStore().getName(), req );
            if ( currentUserIsValid )
            {
                LOG.finest( "Already logged in. Skipping auto-login." );
                return;
            }
            else
            {
                LOG.finest( "A new SSO user has arrived. Logging out current user before continueing" );
                securityService.logoutPortalUser();
            }
        }

        QualifiedUsername qualifiedUserName = getAuthenticatedUser( req, plugin );
        if ( qualifiedUserName == null )
        {
            return;
        }

        if ( securityService.autoLoginPortalUser( qualifiedUserName ) )
        {
            LOG.finest( "Auto-login logged in user [" + qualifiedUserName + "]" );
        }
        else
        {
            LOG.severe( "Auto-login user [" + qualifiedUserName + "] does not exist. Auto-login failed." );
        }
    }

    private QualifiedUsername getAuthenticatedUser( HttpServletRequest req, HttpAutoLogin plugin )
    {
        try
        {
            String qualifiedUsernameStr = plugin.getAuthenticatedUser( req );
            if ( qualifiedUsernameStr == null )
            {
                return null;
            }

            return QualifiedUsername.parse( qualifiedUsernameStr );
        }
        catch ( Exception e )
        {
            LOG.log( Level.SEVERE, "Failed to get authenticated user from plugin", e );
            return null;
        }
    }
}

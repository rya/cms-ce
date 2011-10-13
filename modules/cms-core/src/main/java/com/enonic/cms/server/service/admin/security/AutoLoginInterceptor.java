package com.enonic.cms.server.service.admin.security;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.core.plugin.PluginManager;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.enonic.esl.servlet.http.CookieUtil;

import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.server.service.servlet.OriginalPathResolver;

import com.enonic.cms.business.AdminConsoleTranslationService;

import com.enonic.cms.core.security.user.QualifiedUsername;

/**
 * This interceptor executes any auto login plugins available.
 */
public final class AutoLoginInterceptor
    extends HandlerInterceptorAdapter
{
    private final static Logger LOG = Logger.getLogger( AutoLoginInterceptor.class.getName() );

    private PluginManager pluginManager;

    private SecurityService securityService;

    private OriginalPathResolver originalPathResolver = new OriginalPathResolver();

    public void setPluginManager( PluginManager pluginManager )
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
        HttpAutoLogin plugin = pluginManager.getExtensions().findMatchingHttpAutoLoginPlugin( path );

        if ( plugin != null )
        {
            doAutoLogin( req, plugin );
        }

        return super.preHandle( req, res, o );
    }


    private void doAutoLogin( HttpServletRequest req, HttpAutoLogin plugin )
    {
        UserEntity current = securityService.getLoggedInAdminConsoleUserAsEntity();

        if ( current != null && !current.isAnonymous() )
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
                LOG.finest( "A new SSO user has arrived. Logging out current user before continuing" );
                securityService.logoutAdminUser();
            }
        }

        QualifiedUsername qualifiedUserName = getAuthenticatedUser( req, plugin );
        if ( qualifiedUserName == null )
        {
            return;
        }

        if ( securityService.autoLoginAdminUser( qualifiedUserName ) )
        {
            LOG.finest( "Auto-login logged in user [" + qualifiedUserName + "]" );

            // Setting the user selected language, so it's available for all admin XSLs.
            AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();
            String languageCode;
            Cookie cookie = CookieUtil.getCookie( req, "languageCode" );
            if ( cookie == null )
            {
                languageCode = languageMap.getDefaultLanguageCode();
            }
            else
            {
                languageCode = cookie.getValue();
            }
            req.getSession().setAttribute( "languageCode", languageCode );
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

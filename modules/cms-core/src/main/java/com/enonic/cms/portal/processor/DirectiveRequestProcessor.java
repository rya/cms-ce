/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.processor;

import java.util.Map;

import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

/**
 * Sep 28, 2009
 */
public class DirectiveRequestProcessor
    extends AbstractBasePortalRequestProcessor
{
    private DirectiveRequestProcessorContext context;

    public DirectiveRequestProcessor( DirectiveRequestProcessorContext context )
    {
        this.context = context;
    }

    public DirectiveRequestProcessorResult process()
    {
        SitePath sitePath = context.getOriginalSitePath();
        portalAccessService.checkAccessToPage( context.getMenuItem(), sitePath, context.getRequester() );

        MenuItemType type = context.getMenuItem().getType();
        if ( type.equals( MenuItemType.SHORTCUT ) )
        {
            return processMenuItemShortcutRequest();
        }
        else if ( type.equals( MenuItemType.URL ) )
        {
            return processMenuItemUrlRequest();
        }
        else
        {
            throw new IllegalStateException( "Directive is of illegal menuitem type: " + type );
        }
    }

    private DirectiveRequestProcessorResult processMenuItemShortcutRequest()
    {
        SitePath sitePath = context.getSitePath();
        MenuItemEntity shortcuttedMenuItem = context.getMenuItem().getMenuItemShortcut();
        Path pathToShortcuttedMenuItem = shortcuttedMenuItem.getPath();
        SitePath shortcutSitePath = sitePath.createNewInSameSite( pathToShortcuttedMenuItem, sitePath.getParams() );
        shortcutSitePath.removeParam( "id" );

        // Check for eternal shortcut loop that can occurs when you have menuitems with equals names
        SitePath originalSitePath = context.getOriginalSitePath();
        String originalPath = originalSitePath.getLocalPath().toString();
        if ( originalSitePath.getLocalPath().toString().equalsIgnoreCase( pathToShortcuttedMenuItem.toString() ) )
        {
            throw new RuntimeException( "Eternal shortcut loop prevented. " + "Shortcut '" + originalPath + "' points to the menuitem '" +
                pathToShortcuttedMenuItem.toString() + "' which have equal name (case ignored). Shortcut must be deleted!" );
        }

        if ( context.getMenuItem().isShortcutForward() )
        {
            return DirectiveRequestProcessorResult.createForwardToSitePath( shortcutSitePath );
        }
        else
        {
            Map shortcutParams = context.getRequestParams();
            if ( context.getMenuItem().getMenuItemKey().toString().equals( shortcutParams.get( "id" ) ) )
            {
                shortcutParams.remove( "id" );
            }
            // String redirectUrl = resolveRedirectToPageUrl( shortcuttedMenuItem, request.getSite(), httpRequest,
            // shortcutParams );
            return DirectiveRequestProcessorResult.createRedirectToSitePath( shortcutSitePath );
        }
    }

    private DirectiveRequestProcessorResult processMenuItemUrlRequest()
    {
        return DirectiveRequestProcessorResult.createRedirectToAbsoluteURL( context.getMenuItem().getUrl() );
    }
}

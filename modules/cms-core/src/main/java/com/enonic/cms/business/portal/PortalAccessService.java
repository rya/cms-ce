/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.structure.access.MenuItemAccessResolver;

import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.portal.PathRequiresAuthenticationException;
import com.enonic.cms.domain.portal.PortalAccessDeniedException;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessType;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

/**
 * This is called a Service because it not only checks the access but also throws corresponding exceptions.
 */
public class PortalAccessService
{
    @Autowired
    private GroupDao groupDao;

    public void checkAccessToPage( MenuItemEntity menuItem, SitePath requestedPath, UserEntity requester )
    {
        MenuItemAccessResolver menuItemAccessResolver = new MenuItemAccessResolver( groupDao );
        boolean hasAccess = menuItemAccessResolver.hasAccess( requester, menuItem, MenuItemAccessType.READ );

        if ( !hasAccess )
        {
            if ( requester.isAnonymous() )
            {
                throw new PathRequiresAuthenticationException( requestedPath );
            }
            else
            {
                throw new PortalAccessDeniedException( menuItem.getMenuItemKey() );
            }
        }
    }

    public void checkAccessToContent( SitePath requestedPath, UserEntity requester, ContentEntity requestedContent,
                                      MenuItemEntity requestedMenuItem )
    {

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );
        boolean hasAccess = contentAccessResolver.hasReadContentAccess( requester, requestedContent );

        if ( !hasAccess )
        {
            if ( requester.isAnonymous() )
            {
                throw new PathRequiresAuthenticationException( requestedPath );
            }
            else
            {
                throw new PortalAccessDeniedException( requestedMenuItem.getMenuItemKey() );
            }
        }
    }
}

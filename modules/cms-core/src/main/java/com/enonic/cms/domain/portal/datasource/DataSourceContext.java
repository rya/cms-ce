/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.datasource;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.preview.PreviewContext;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.portal.PortalInstanceKey;
import com.enonic.cms.domain.portal.ShoppingCart;
import com.enonic.cms.domain.security.user.UserEntity;

public class DataSourceContext
{
    private SiteKey siteKey = null;

    private ShoppingCart shoppingCart;

    private PortalInstanceKey portalInstanceKey;

    private UserEntity user;

    private PreviewContext previewContext;

    public DataSourceContext( PreviewContext previewContext )
    {
        Preconditions.checkNotNull( previewContext );

        this.previewContext = previewContext;
    }

    public DataSourceContext()
    {
        this.previewContext = PreviewContext.NO_PREVIEW;
    }

    public void setSiteKey( final SiteKey value )
    {
        siteKey = value;
    }

    public void setShoppingCart( final ShoppingCart value )
    {
        shoppingCart = value;
    }

    public void setPortalInstanceKey( final PortalInstanceKey value )
    {
        portalInstanceKey = value;
    }

    public void setUser( final UserEntity user )
    {
        this.user = user;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public ShoppingCart getShoppingCart()
    {
        return shoppingCart;
    }

    public PortalInstanceKey getPortalInstanceKey()
    {
        return portalInstanceKey;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public PreviewContext getPreviewContext()
    {
        return previewContext;
    }
}

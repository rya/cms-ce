/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import com.enonic.cms.core.resource.access.ResourceAccessResolver;

import com.enonic.cms.core.security.user.UserEntity;

public class DavAccessResolverImpl
    implements DavAccessResolver
{

    private ResourceAccessResolver resourceAccessResolver;

    public DavAccessResolverImpl( ResourceAccessResolver resourceAccessResolver )
    {
        this.resourceAccessResolver = resourceAccessResolver;
    }

    public boolean hasAccess( UserEntity user )
    {
        return resourceAccessResolver.hasAccessToResourceTree( user );
    }

}

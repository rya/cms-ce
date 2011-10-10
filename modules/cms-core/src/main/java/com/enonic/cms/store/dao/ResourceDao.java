/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.resource.ResourceFolder;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceFile;


public interface ResourceDao
{
    ResourceFolder getResourceRoot();

    ResourceFile getResourceFile( ResourceKey resourceKey );

    ResourceFolder getResourceFolder( ResourceKey resourceKey );
}

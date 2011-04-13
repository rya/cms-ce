/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import com.google.common.collect.Multimap;

public interface ResourceService
{
    public ResourceFile getResourceFile( ResourceKey resourceKey );

    public ResourceBase getResource( ResourceKey resourceKey );

    public Multimap<ResourceKey, ResourceReferencer> getUsedBy( ResourceKey resourceKey );
}

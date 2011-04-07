/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.HashMap;

import com.google.common.collect.Multimap;

import com.enonic.cms.domain.resource.ResourceBase;
import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceFolder;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.resource.ResourceReferencer;

public interface ResourceService
{

    public ResourceFolder getResourceRoot();

    public ResourceFile getResourceFile( ResourceKey resourceKey );

    public ResourceFolder getResourceFolder( ResourceKey resourceKey );

    /**
     * @return either ResourceFile or ResourceFolder
     */
    public ResourceBase getResource( ResourceKey resourceKey );

    public HashMap<ResourceKey, Long> getUsageCountMap();

    public Multimap<ResourceKey, ResourceReferencer> getUsedBy( ResourceKey resourceKey );

    public ResourceKey moveResource( ResourceBase source, ResourceFolder destination );
}

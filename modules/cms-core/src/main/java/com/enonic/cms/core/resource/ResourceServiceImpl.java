/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Multimap;

import com.enonic.cms.store.dao.ResourceDao;
import com.enonic.cms.store.dao.ResourceUsageDao;

public class ResourceServiceImpl
    implements ResourceService
{
    @Autowired
    private ResourceUsageDao resourceUsageDao;

    @Autowired
    private ResourceDao resourceDao;

    public ResourceFolder getResourceRoot()
    {
        return resourceDao.getResourceRoot();
    }

    public ResourceFile getResourceFile( ResourceKey resourceKey )
    {
        return resourceDao.getResourceFile( resourceKey );
    }

    public ResourceFolder getResourceFolder( ResourceKey resourceKey )
    {
        return resourceDao.getResourceFolder( resourceKey );
    }

    public ResourceBase getResource( ResourceKey resourceKey )
    {
        ResourceBase resource = resourceDao.getResourceFile( resourceKey );
        if ( resource == null )
        {
            resource = resourceDao.getResourceFolder( resourceKey );
        }
        return resource;
    }

    public HashMap<ResourceKey, Long> getUsageCountMap()
    {
        return resourceUsageDao.getUsageCountMap();
    }

    public Multimap<ResourceKey, ResourceReferencer> getUsedBy( ResourceKey resourceKey )
    {
        return resourceUsageDao.getUsedBy( resourceKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
    public ResourceKey moveResource( ResourceBase source, ResourceFolder destination )
    {

        if ( source == null )
        {
            throw new IllegalArgumentException( "Resource destination cannot be null" );
        }

        if ( source instanceof ResourceFile )
        {
            ResourceKey newResourceKey = new ResourceKey( destination.getResourceKey() + "/" + source.getName() );
            resourceUsageDao.updateResoruceReference( source.getResourceKey(), newResourceKey );
        }
        else if ( source instanceof ResourceFolder )
        {
            String oldPrefix = source.getPath() + "/";
            String newPrefix = destination.getPath() + "/" + source.getName() + "/";
            resourceUsageDao.updateResoruceReferencePrefix( oldPrefix, newPrefix );
        }
        else
        {
            throw new IllegalArgumentException(
                "Resource must be of type ResourceFile or ResourceFolder, was: " + source.getClass().getName() );
        }

        return source.moveTo( destination );
    }
}

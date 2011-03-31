/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * This factory selects the first resource that exists and returns that resource. If none exists, it either returns null or throws an
 * exception.
 */
public final class SelectResourceFactory
    implements FactoryBean
{
    private boolean requireResource;

    private Resource[] resources;

    public void setRequireResource( boolean requireResource )
    {
        this.requireResource = requireResource;
    }

    public void setResources( Resource[] resources )
    {
        this.resources = resources;
    }

    public Object getObject()
        throws Exception
    {
        if ( this.resources == null )
        {
            throw new IllegalArgumentException( "No resources was set" );
        }

        for ( Resource resource : this.resources )
        {
            if ( resource.exists() )
            {
                return resource;
            }
        }

        if ( this.requireResource )
        {
            throw new IllegalArgumentException( "No resources in list existed" );
        }

        return null;
    }

    public Class getObjectType()
    {
        return Resource.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}

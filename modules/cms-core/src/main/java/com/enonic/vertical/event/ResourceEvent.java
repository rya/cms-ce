/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.user.User;

public class ResourceEvent
    extends ContentHandlerEvent
{
    private ResourceKey[] keys;

    private String[] titles;

    public ResourceEvent( User user, Object source, ResourceKey[] keys, String[] titles )
    {
        super( user, source );
        this.keys = keys;
        this.titles = titles;
    }

    public ResourceEvent( User user, Object source, ResourceKey key, String title )
    {
        super( user, source );
        this.keys = new ResourceKey[]{key};
        this.titles = new String[]{title};
    }

    public ResourceKey[] getKeys()
    {
        return keys;
    }

    public String[] getTitles()
    {
        return titles;
    }

}

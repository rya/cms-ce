/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.resource;

import com.enonic.cms.core.resource.FileResourceName;

public final class FileResourceEvent
{
    public enum Type
    {
        ADDED,
        DELETED,
        UPDATED
    }

    private final Type type;

    private final FileResourceName name;

    public FileResourceEvent( Type type, FileResourceName name )
    {
        this.type = type;
        this.name = name;
    }

    public Type getType()
    {
        return type;
    }

    public FileResourceName getName()
    {
        return name;
    }

    public String toString()
    {
        return getClass().getSimpleName() + "[" + this.type.toString() + ", " + this.name.getPath() + "]";
    }
}

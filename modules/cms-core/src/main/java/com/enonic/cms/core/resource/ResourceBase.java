/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.Calendar;

public interface ResourceBase
{

    String toString();

    String getName();

    String getPath();

    ResourceKey getResourceKey();

    ResourceFolder getParentFolder();

    Calendar getLastModified();

    boolean isHidden();

    public ResourceKey moveTo( ResourceFolder destinationFolder );

    public String getETag();
}

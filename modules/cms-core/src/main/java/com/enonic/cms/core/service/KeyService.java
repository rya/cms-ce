/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import com.enonic.vertical.engine.VerticalKeyException;

public interface KeyService
{
    public int generateNextKeySafe( String tableName )
        throws VerticalKeyException;

    public boolean keyExists( String tableName, int key );
}

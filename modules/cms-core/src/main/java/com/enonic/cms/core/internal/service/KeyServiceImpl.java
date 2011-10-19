/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.vertical.engine.KeyEngine;
import com.enonic.vertical.engine.VerticalKeyException;

import com.enonic.cms.core.service.KeyService;

@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class KeyServiceImpl
    implements KeyService
{
    protected KeyEngine keyEngine;

    public void setKeyEngine( KeyEngine value )
    {
        this.keyEngine = value;
    }

    public int generateNextKeySafe( String tableName )
        throws VerticalKeyException
    {
        return keyEngine.generateNextKeySafe( tableName );
    }

    public boolean keyExists( String tableName, int key )
    {
        return keyEngine.keyExists( tableName, key );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import com.enonic.vertical.engine.handlers.KeyHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.enonic.cms.core.service.KeyService;

@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class KeyServiceImpl
    implements KeyService
{
    private KeyHandler keyHandler;

    public void setKeyHandler( KeyHandler keyHandler )
    {
        this.keyHandler = keyHandler;
    }

    public int generateNextKeySafe( String tableName )
    {
        return keyHandler.generateNextKeySafe(tableName);
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.engine.handlers.KeyHandler;

public class KeyEngine
    extends BaseEngine
{
    private KeyHandler keyHandler;

    public void setKeyHandler( KeyHandler keyHandler )
    {
        this.keyHandler = keyHandler;
    }

    public int generateNextKeySafe( String tableName )
        throws VerticalKeyException
    {
        return keyHandler.generateNextKeySafe( tableName );
    }

    public void updateKey( String tableName, String pkColumnName, int minumumValue )
        throws VerticalKeyException
    {
        keyHandler.updateKey( tableName, minumumValue );
    }

    public boolean keyExists( String tableName, int key )
    {
        return keyHandler.keyExists( tableName, key );
    }
}
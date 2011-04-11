/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.KeyHandler;

public class KeyEngine
    extends BaseEngine
{

    private KeyHandler keyHandler;

    private CommonHandler commonHandler;


    public void setKeyHandler( KeyHandler keyHandler )
    {
        this.keyHandler = keyHandler;
    }

    public void setCommonHandler( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
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

    /**
     * @see com.enonic.vertical.engine.BaseEngine#getCommonHandler()
     */
    public CommonHandler getCommonHandler()
    {
        return commonHandler;
    }
}
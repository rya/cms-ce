/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest;

import com.enonic.cms.core.service.KeyService;

/**
 * Extend this mock class with functinality when needed.
 */
public class MockKeyService
    implements KeyService
{
    private int nextKey = 0;

    public int generateNextKeySafe( String tableName )
    {
        int keyToReturn = nextKey;
        nextKey++;
        return keyToReturn;
    }

    public void updateKey( String tableName, String pkColumnName, int minimumValue )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean keyExists( String tableName, int key )
    {
        return false;
    }
}

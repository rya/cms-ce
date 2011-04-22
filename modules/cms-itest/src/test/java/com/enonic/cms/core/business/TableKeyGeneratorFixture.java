/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.business;

/**
 * Sep 21, 2009
 */
public class TableKeyGeneratorFixture
{
    private static int lastUsedId = -1;

    public static synchronized int nextKey()
    {
        return ++lastUsedId;
    }

    private static int lastUsedId2 = -1;

    public synchronized int nextKey2()
    {
        return ++lastUsedId2;
    }
}

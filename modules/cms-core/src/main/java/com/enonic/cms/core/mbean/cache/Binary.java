/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mbean.cache;

public class Binary
    extends AbstractCache
    implements BinaryMBean
{
    public Binary()
    {
        super( "binary" );
    }
}
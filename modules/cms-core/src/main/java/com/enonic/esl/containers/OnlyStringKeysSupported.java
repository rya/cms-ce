/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.containers;

public class OnlyStringKeysSupported
    extends RuntimeException
{

    /**
     * @param s
     */
    public OnlyStringKeysSupported( Object key )
    {
        super( key + "(" + key.getClass() + ")" );
    }

    /**
     *
     *
     */
    public OnlyStringKeysSupported()
    {
        super();
    }
}

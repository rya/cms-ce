/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.ArrayList;
import java.util.Arrays;

public class TStringArrayList
{
    ArrayList strings = new ArrayList();

    public TStringArrayList()
    {

    }

    public void add( String string )
    {
        strings.add( string );
    }

    public void add( String[] stringArray )
    {
        strings.addAll( Arrays.asList( stringArray ) );
    }

    public String[] toNativeArray()
    {
        return (String[]) strings.toArray( new String[0] );
    }

    public boolean contains( String string )
    {
        return strings.contains( string );
    }

}

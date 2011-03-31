/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.link;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.util.FileCopyUtils;

public abstract class AbstractLinkTest
{
    protected final String readFile( String file )
        throws Exception
    {
        String name = "/" + getClass().getName().replace( ".", "/" ) + "-" + file;
        InputStream in = getClass().getResourceAsStream( name );
        if ( in == null )
        {
            throw new IOException( "Could not find resource " + name );
        }

        return readFile( in );
    }

    private String readFile( InputStream in )
        throws Exception
    {
        return FileCopyUtils.copyToString( new InputStreamReader( in ) ).trim();
    }
}

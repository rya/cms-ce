/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;

class JcrBinaryWrapper
    implements JcrBinary
{
    private final Binary binary;

    JcrBinaryWrapper( Binary binary )
    {
        this.binary = binary;
    }

    @Override
    public InputStream getStream()
    {
        try
        {
            return binary.getStream();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public int read( byte[] b, long position )
        throws IOException
    {
        try
        {
            return binary.read( b, position );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public long getSize()
    {
        try
        {
            return binary.getSize();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public byte[] toByteArray()
    {
        try
        {
            return IOUtils.toByteArray( binary.getStream() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to convert JCR binary property to byte array", e );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public void dispose()
    {
        binary.dispose();
    }
}

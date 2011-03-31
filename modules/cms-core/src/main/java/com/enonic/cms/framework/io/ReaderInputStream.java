/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Exposes a <tt>Reader</tt> as an <tt>InputStream</tt>.
 */
public class ReaderInputStream
    extends InputStream
{
    /**
     * Reader.
     */
    private Reader reader;

    /**
     * Constructs the stream.
     */
    public ReaderInputStream( Reader reader )
    {
        this.reader = reader;
    }

    /**
     * Read character.
     */
    public int read()
        throws IOException
    {
        return this.reader.read();
    }

    /**
     * Close the stream.
     */
    public void close()
        throws IOException
    {
        try
        {
            this.reader.close();
        }
        finally
        {
            super.close();
        }
    }
}

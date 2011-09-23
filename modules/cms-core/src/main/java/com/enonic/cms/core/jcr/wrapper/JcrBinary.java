/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import java.io.IOException;
import java.io.InputStream;

public interface JcrBinary
{
    public InputStream getStream();

    public int read(byte[] b, long position) throws IOException;

    public long getSize();

    public void dispose();

    public byte[] toByteArray();
}

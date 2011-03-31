/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.activation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.commons.fileupload.FileItem;

/**
 * File item data source.
 */
public final class FileItemDataSource
    implements DataSource
{

    /**
     * File item.
     */
    private final FileItem item;

    /**
     * Construct the data source.
     */
    public FileItemDataSource( FileItem item )
    {
        this.item = item;
    }

    /**
     * Return the content type.
     */
    public String getContentType()
    {
        return this.item.getContentType();
    }

    /**
     * Return the input stream.
     */
    public InputStream getInputStream()
        throws IOException
    {
        return this.item.getInputStream();
    }

    /**
     * Return the name.
     */
    public String getName()
    {
        File file = new File( this.item.getName() );
        return file.getName();
    }

    /**
     * Return the output stream.
     */
    public OutputStream getOutputStream()
        throws IOException
    {
        return this.item.getOutputStream();
    }
}

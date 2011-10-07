/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import org.joda.time.DateTime;

public final class FileResource
{
    private final FileResourceName name;

    private boolean folder;

    private long size;

    private String mimeType;

    private DateTime lastModified;

    private String blobKey;

    public FileResource( FileResourceName name )
    {
        this.name = name;
    }

    public FileResourceName getName()
    {
        return this.name;
    }

    public boolean isFolder()
    {
        return folder;
    }

    public void setFolder( boolean folder )
    {
        this.folder = folder;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize( long size )
    {
        this.size = size;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType( String mimeType )
    {
        this.mimeType = mimeType;
    }

    public DateTime getLastModified()
    {
        return lastModified;
    }

    public void setLastModified( DateTime lastModified )
    {
        this.lastModified = lastModified;
    }

    public boolean isHidden()
    {
        return this.name.isHidden();
    }

    public String getBlobKey()
    {
        return blobKey;
    }

    public void setBlobKey( String blobKey )
    {
        this.blobKey = blobKey;
    }
}

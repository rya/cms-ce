/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.vfs.db;

/**
 * This class implements the virtual file entity.
 */
public class VirtualFileEntity
{
    /**
     * File key.
     */
    private String key;

    /**
     * Parent key.
     */
    private String parentKey;

    /**
     * File name.
     */
    private String name;

    /**
     * Last modified.
     */
    private long lastModified;

    /**
     * File length.
     */
    private long length;

    private String blobKey;

    /**
     * File data.
     */
    // private VirtualFileDataEntity data;

    /**
     * Return the key.
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * Set the key.
     */
    public void setKey( String key )
    {
        this.key = key;
    }

    /**
     * Return the parent key.
     */
    public String getParentKey()
    {
        return this.parentKey;
    }

    /**
     * Set the parent key.
     */
    public void setParentKey( String parentKey )
    {
        this.parentKey = parentKey;
    }

    /**
     * Return the name.
     */
    public String getName()
    {
        if ( this.parentKey == null && this.name.equals( "#" ) )
        {
            return "";
        }
        else
        {
            return this.name;
        }
    }

    /**
     * Set the name.
     */
    public void setName( String name )
    {
        if ( name == null || name.length() == 0 )
        {
            this.name = "#";
        }
        else
        {
            this.name = name;
        }
    }

    /**
     * Return the last modified.
     */
    public long getLastModified()
    {
        return this.lastModified;
    }

    /**
     * Set the last modified.
     */
    public void setLastModified( long lastModified )
    {
        this.lastModified = lastModified;
    }

    /**
     * Return the length.
     */
    public long getLength()
    {
        return this.length;
    }

    /**
     * Set the length.
     */
    public void setLength( long length )
    {
        this.length = length;
    }

    /**
     * Return the data.
     */
    /*public VirtualFileDataEntity getData()
    {
        return this.data;
    }*/

    /**
     * Set the data.
     */
    /*public void setData( VirtualFileDataEntity data )
    {
        this.data = data;
    }*/

    /**
     * Return true if file.
     */
    public boolean isFile()
    {
        return length > -1;
    }

    /**
     * Return true if folder.
     */
    public boolean isFolder()
    {
        return length < 0;
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

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jan 11, 2010
 * Time: 8:21:13 PM
 */
public class MockFileItem
    implements FileItem
{

    private byte[] value = null;

    protected MockFileItem( byte[] value )
    {
        this.value = value;
    }

    public InputStream getInputStream()
        throws IOException
    {
        if ( value == null )
        {
            return new ByteArrayInputStream( new byte[]{} );
        }
        return new ByteArrayInputStream( value );
    }

    public String getContentType()
    {
        return null;
    }

    public String getName()
    {
        return "TestFileItem";
    }

    public boolean isInMemory()
    {
        return true;
    }

    public long getSize()
    {
        return value.length;
    }

    public byte[] get()
    {
        return value;
    }

    public String getString( String s )
        throws UnsupportedEncodingException
    {
        return null;
    }

    public String getString()
    {
        return null;
    }

    public void write( File file )
        throws Exception
    {
    }

    public void delete()
    {
    }

    public String getFieldName()
    {
        return null;
    }

    public void setFieldName( String s )
    {
    }

    public boolean isFormField()
    {
        return false;
    }

    public void setFormField( boolean b )
    {
    }

    public OutputStream getOutputStream()
        throws IOException
    {
        return null;


    }
}
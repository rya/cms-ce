/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.enonic.cms.core.resource.FileResourceData;
import org.springframework.util.FileCopyUtils;

import com.enonic.cms.framework.io.UnicodeInputStream;
import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.ResourceFile;

final class ResourceFileImpl
    extends ResourceBaseImpl
    implements ResourceFile
{
    public ResourceFileImpl( FileResourceService service, FileResourceName name )
    {
        super( service, name );
    }

    public String getMimeType()
    {
        return ensureResource().getMimeType();
    }

    public long getSize()
    {
        return ensureResource().getSize();
    }

    public XMLDocument getDataAsXml()
    {
        return doGetDataAsXml( true );
    }

    public XMLBytes getDataAsXmlBytes()
    {
        return doGetDataAsXml( true ).getAsBytes();
    }

    public String getDataAsString()
    {
        FileResourceData data = this.service.getResourceData( this.name );
        return data != null ? data.getAsString() : null;
    }

    public byte[] getDataAsByteArray()
    {
        FileResourceData data = this.service.getResourceData( this.name );
        return data != null ? data.getAsBytes() : null;
    }

    public InputStream getDataAsInputStream()
    {
        byte[] data = getDataAsByteArray();
        return data != null ? new ByteArrayInputStream( data ) : null;
    }

    public void setData( XMLDocument data )
    {
        doSetData( data.getAsBytes().getData() );
    }

    public void setData( XMLBytes data )
    {
        doSetData( data.getData() );
    }

    public void setData( String data )
    {
        doSetData( data.getBytes() );
    }

    public void setData( byte[] data )
    {
        doSetData( data );
    }

    public void setData( InputStream data )
    {
        try
        {
            setData( FileCopyUtils.copyToByteArray( data ) );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( e );
        }
    }

    private ByteArrayOutputStream getAsByteArrayOutputStream( boolean skipBOM )
    {
        try
        {
            InputStream in = doGetDataAsInputStream( skipBOM );
            if ( in == null )
            {
                return null;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileCopyUtils.copy( in, out );
            return out;
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    private XMLDocument doGetDataAsXml( boolean skipBOM )
    {
        byte[] data = doGetDataAsByteArray( skipBOM );
        if ( data == null )
        {
            return null;
        }
        return XMLDocumentFactory.create( data, "UTF-8" );
    }

    private byte[] doGetDataAsByteArray( boolean skipBOM )
    {
        ByteArrayOutputStream out = getAsByteArrayOutputStream( skipBOM );
        if ( out == null )
        {
            return null;
        }
        return out.toByteArray();
    }

    private InputStream doGetDataAsInputStream( boolean skipBOM )
    {
        try
        {
            return new UnicodeInputStream( new ByteArrayInputStream( getDataAsByteArray() ), skipBOM );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( e );
        }
    }

    private void doSetData( byte[] data )
    {
        this.service.setResourceData( this.name, FileResourceData.create(data) );
    }
}

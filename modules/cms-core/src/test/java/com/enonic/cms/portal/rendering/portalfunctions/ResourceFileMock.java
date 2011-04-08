/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.portalfunctions;

import java.io.InputStream;
import java.util.Calendar;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceFolder;
import com.enonic.cms.domain.resource.ResourceKey;

/**
 * Created by rmy - Date: Nov 17, 2009
 */
public class ResourceFileMock
    implements ResourceFile
{

    private Calendar lastModified;

    public void setLastModified( Calendar lastModified )
    {
        this.lastModified = lastModified;
    }

    public ResourceFileMock()
    {
    }

    public String getMimeType()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getSize()
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public XMLDocument getDataAsXml()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public XMLBytes getDataAsXmlBytes()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getDataAsString()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte[] getDataAsByteArray()
    {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getDataAsInputStream()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setData( XMLDocument data )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setData( XMLBytes data )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setData( String data )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setData( byte[] data )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setData( InputStream data )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getPath()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResourceKey getResourceKey()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResourceFolder getParentFolder()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Calendar getLastModified()
    {
        return lastModified;
    }

    public boolean isHidden()
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResourceKey moveTo( ResourceFolder destinationFolder )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getETag()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString()
    {
        return null;
    }
}

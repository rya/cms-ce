/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import org.jdom.Document;

import java.io.InputStream;

public interface ResourceFile
    extends ResourceBase
{
    String getMimeType();

    long getSize();

    Document getDataAsXml();

    String getDataAsString();

    byte[] getDataAsByteArray();

    InputStream getDataAsInputStream();

    void setData( Document data );

    void setData( String data );

    void setData( byte[] data );

    void setData( InputStream data );
}

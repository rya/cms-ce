package com.enonic.cms.admin.image;

import java.io.InputStream;

import com.vaadin.terminal.StreamResource;

/**
 * Image source from java stream
 * for Embedded vaadin controls
 *
 * @author
 */
public class ImageStreamSource implements StreamResource.StreamSource
{

    private InputStream inputStream;

    public ImageStreamSource (InputStream stream){
        this.inputStream = stream;
    }
    @Override
    public InputStream getStream()
    {
        return inputStream;
    }
}

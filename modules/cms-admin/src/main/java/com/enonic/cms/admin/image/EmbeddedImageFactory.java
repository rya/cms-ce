package com.enonic.cms.admin.image;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.Application;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;

import com.enonic.cms.admin.spring.VaadinComponent;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/4/11
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
@VaadinComponent
public class EmbeddedImageFactory
{
    private static String IMAGE_JPG = "image.jpg";

    @Autowired
    private Application adminApplication;

    /**
     * Create embedded image from path to theme resource
     *
     * @param path - path to theme resource
     * @return
     */
    public Embedded createEmbeddedImage( String path )
    {
        ThemeResource resource = new ThemeResource( path );
        Embedded embeddedImage = new Embedded( "", resource );
        return embeddedImage;
    }

    /**
     * Create embedded image from byte array (works only for JPG now)
     *
     * @param imageArray - byte array with image data
     * @return
     */
    public Embedded createEmbeddedImage( byte[] imageArray )
    {
        StreamResource.StreamSource imageSource = new ImageStreamSource( new ByteArrayInputStream( imageArray ) );

        StreamResource resource = new StreamResource( imageSource, IMAGE_JPG, adminApplication );
        Embedded image = new Embedded( "", resource );
        return image;
    }
}

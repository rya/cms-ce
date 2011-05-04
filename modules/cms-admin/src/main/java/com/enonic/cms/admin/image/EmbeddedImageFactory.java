package com.enonic.cms.admin.image;

import java.io.ByteArrayInputStream;

import com.vaadin.Application;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/4/11
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmbeddedImageFactory
{
    private static String IMAGE_JPG = "image.jpg";

    /**
     * Create embedded image from path to theme resource
     *
     * @param path - path to theme resource
     * @return
     */
    static public Embedded createEmbeddedImage( String path )
    {
        ThemeResource resource = new ThemeResource( path );
        Embedded embeddedImage = new Embedded( "", resource );
        return embeddedImage;
    }

    /**
     * Create embedded image from byte array (works only for JPG now)
     *
     * @param imageArray - byte array with image data
     * @param application - application, where embedded image should be registered
     * @return
     */
    static public Embedded createEmbeddedImage( byte[] imageArray, Application application )
    {
        StreamResource.StreamSource imageSource = new ImageStreamSource( new ByteArrayInputStream( imageArray ) );

        StreamResource resource = new StreamResource( imageSource, IMAGE_JPG, application );
        Embedded image = new Embedded( IMAGE_JPG, resource );
        return image;
    }
}

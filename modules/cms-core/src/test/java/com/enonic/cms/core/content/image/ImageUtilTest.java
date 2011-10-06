package com.enonic.cms.core.content.image;

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 9/20/11
 * Time: 2:18 PM
 */
public class ImageUtilTest
{
    private BufferedImage originalImage;

    @Before
    public void setUp()
    {
        try
        {
            originalImage = ImageIO.read( loadImage( "Arn.jpg" ) );
        }
        catch ( IOException e )
        {
            fail( "Image not found" );
        }
    }

    @Test
    public void testScaleImage()
    {
        BufferedImage scaledImage = ImageUtil.scaleImage( originalImage, 200, 200,
                                                          ContentImageUtil.getBufferedImageType( "jpg" ) );

        assertEquals( 200, scaledImage.getHeight() );
        assertEquals( 200, scaledImage.getWidth() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScaleImageIllegalValues()
    {
        ImageUtil.scaleImage( originalImage, -200, -200, ContentImageUtil.getBufferedImageType( "jpg" ) );
    }


    private InputStream loadImage( String fileName )
        throws IOException
    {
        ClassPathResource resource = new ClassPathResource( ImageUtilTest.class.getName().replace( ".", "/" ) + "-" + fileName );
        return resource.getInputStream();
    }
}

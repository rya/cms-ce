package com.enonic.cms.business.core.content.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import junit.framework.TestCase;

import com.enonic.cms.domain.content.binary.BinaryData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 9/20/11
 * Time: 2:12 PM
 */
public class ContentImageUtilTest
    extends TestCase
{
    private final int createOriginalImage = 0;

    @Before
    public void setUp()
    {

    }

    @Test
    public void testScaleExtraLargeImage()
        throws Exception
    {
        final String imageSize = "1600";
        BufferedImage image = getImageFromFile( imageSize );

        List<BinaryData> scaledImages = ContentImageUtil.createStandardSizeImages( image, "jpg", createFileName( imageSize ) );

        assertTrue( image.getWidth() == new Integer( imageSize ) );

        assertEquals( "Wrong number of scaled images", 3 + createOriginalImage, scaledImages.size() );

        checkCreatedImages( scaledImages, true, true, true );
    }

    @Test
    public void testScaleLargeImage()
        throws Exception
    {
        final String imageSize = "1000";
        BufferedImage image = getImageFromFile( imageSize );

        List<BinaryData> scaledImages = ContentImageUtil.createStandardSizeImages( image, "jpg", createFileName( imageSize ) );

        assertTrue( image.getWidth() == new Integer( imageSize ) );

        assertEquals( "Wrong number of scaled images", 2 + createOriginalImage, scaledImages.size() );

        checkCreatedImages( scaledImages, true, true, false );
    }

    @Test
    public void testScaleMediumImage()
        throws Exception
    {
        final String imageSize = "500";
        BufferedImage image = getImageFromFile( imageSize );

        List<BinaryData> scaledImages = ContentImageUtil.createStandardSizeImages( image, "jpg", createFileName( imageSize ) );

        assertTrue( image.getWidth() == new Integer( imageSize ) );

        assertEquals( "Wrong number of scaled images", 1 + createOriginalImage, scaledImages.size() );

        checkCreatedImages( scaledImages, true, false, false );

    }


    @Test
    public void testScaleSmallImage()
        throws Exception
    {
        final String imageSize = "200";
        BufferedImage image = getImageFromFile( imageSize );

        List<BinaryData> scaledImages = ContentImageUtil.createStandardSizeImages( image, "jpg", createFileName( imageSize ) );

        assertTrue( image.getWidth() == 200 );

        assertEquals( "Wrong number of scaled images", 0 + createOriginalImage, scaledImages.size() );

        checkCreatedImages( scaledImages, false, false, false );
    }


    private void checkCreatedImages( List<BinaryData> scaledImages, boolean createSmall, boolean createMedium, boolean createLarge )
        throws Exception
    {

        boolean smallCreated = false, mediumCreated = false, largeCreated = false;

        for ( BinaryData scaledImageData : scaledImages )
        {
            BufferedImage scaledImage = ImageUtil.readImage( scaledImageData.data );

            if ( checkBinaryData( scaledImageData, "small" ) )
            {
                smallCreated = true;
                assertEquals( ContentImageUtil.STANDARD_WIDTH_SIZES[0], scaledImage.getWidth() );
            }
            if ( checkBinaryData( scaledImageData, "medium" ) )
            {
                mediumCreated = true;
                assertEquals( ContentImageUtil.STANDARD_WIDTH_SIZES[1], scaledImage.getWidth() );
            }
            if ( checkBinaryData( scaledImageData, "large" ) )
            {
                largeCreated = true;
                assertEquals( ContentImageUtil.STANDARD_WIDTH_SIZES[2], scaledImage.getWidth() );
            }
        }

        assertEquals( createSmall, smallCreated );
        assertEquals( createMedium, mediumCreated );
        assertEquals( createLarge, largeCreated );
    }

    private boolean checkBinaryData( BinaryData scaledImage, String expectedPattern )
    {
        return scaledImage.fileName.contains( expectedPattern ) && expectedPattern.equals( scaledImage.label );
    }

    private InputStream loadImage( String fileName )
        throws IOException
    {
        ClassPathResource resource = new ClassPathResource( fileName );
        return resource.getInputStream();
    }

    private String createFileName( String fileName )
    {
        return ContentImageUtilTest.class.getName().replace( ".", "/" ) + "-" + fileName + "px.jpg";
    }

    private BufferedImage getImageFromFile( String imageSize )
    {
        try
        {
            final String fileName = createFileName( imageSize );
            return ImageIO.read( loadImage( fileName ) );
        }
        catch ( IOException e )
        {
            fail( "Image not found: " + createFileName( imageSize ) );
        }

        return null;
    }


}


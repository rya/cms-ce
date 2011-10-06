/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Utility class for image operations.
 */
public final class ImageUtil
{

    private final static float COMPRESSION_QUALITY = 0.85f;

    public static BufferedImage scaleImage( final BufferedImage image, final int newWidth, final int newHeight,
                                            final int bufferedImageType )
    {
        final BufferedImage destImage = new BufferedImage( newWidth, newHeight, bufferedImageType );
        final Image scaleImage = image.getScaledInstance( newWidth, newHeight, Image.SCALE_SMOOTH );
        final Graphics2D g = destImage.createGraphics();
        g.drawImage( scaleImage, 0, 0, newWidth, newHeight, null );
        g.dispose();
        return destImage;
    }

    public static BufferedImage readImage( byte[] data )
        throws IOException
    {
        return readImage( new ByteArrayInputStream( data ) );
    }

    public static BufferedImage readImage( InputStream input )
        throws IOException
    {
        return ImageIO.read( input );
    }

    public static BufferedImage readImage( File imageFile )
        throws IOException
    {
        return ImageIO.read( imageFile );
    }

    private static ImageWriter getImageWriter( String format )
        throws IOException
    {
        Iterator i = ImageIO.getImageWritersByFormatName( format );
        if ( i.hasNext() )
        {
            return (ImageWriter) i.next();
        }
        else
        {
            throw new IOException( "Failed to find writer for format [" + format + "]" );
        }
    }

    public static void writeImage( BufferedImage image, String format, OutputStream out )
        throws IOException
    {
        writeImage( image, format, out, COMPRESSION_QUALITY );
    }

    public static void writeImage( BufferedImage image, String format, OutputStream out, float quality )
        throws IOException
    {
        ImageWriter writer = getImageWriter( format );
        MemoryCacheImageOutputStream imageOut = new MemoryCacheImageOutputStream( out );
        writer.setOutput( imageOut );
        ImageWriteParam params = writer.getDefaultWriteParam();

        if ( params instanceof JPEGImageWriteParam )
        {
            params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
            params.setCompressionQuality( quality );
        }

        writer.write( null, new IIOImage( image, null, null ), params );
        writer.dispose();
        imageOut.flush();
        imageOut.close();
    }

    public static BufferedImage rotateImage90( BufferedImage image, final int bufferedImageType )
    {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage destImage = new BufferedImage( height, width, bufferedImageType );

        for ( int i = 0; i < width; i++ )
        {
            for ( int j = 0; j < height; j++ )
            {
                destImage.setRGB( height - 1 - j, i, image.getRGB( i, j ) );
            }
        }

        return destImage;
    }

    public static BufferedImage rotateImage180( BufferedImage image, final int bufferedImageType )
    {
        return rotateImage90( rotateImage90( image, bufferedImageType ), bufferedImageType );
    }

    public static BufferedImage rotateImage270( BufferedImage image, final int bufferedImageType )
    {
        return rotateImage90( rotateImage180( image, bufferedImageType ), bufferedImageType );
    }
}

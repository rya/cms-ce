/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public final class ImageHelper
{
    public static ImageWriter getWriterByFormat( String format )
    {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName( format );
        if ( iter.hasNext() )
        {
            return iter.next();
        }
        else
        {
            throw new IllegalArgumentException( "Image format [" + format + "] is not supported" );
        }
    }

    public static BufferedImage readImage( InputStream in )
        throws IOException
    {
        return ImageIO.read( in );
    }

    public static BufferedImage readImage( byte[] data )
        throws IOException
    {
        return readImage( new ByteArrayInputStream( data ) );
    }

    public static byte[] writeImage( BufferedImage image, String format, int quality )
        throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeImage( out, image, format, quality );
        return out.toByteArray();
    }

    public static void writeImage( OutputStream out, BufferedImage image, String format, int quality )
        throws IOException
    {
        ImageWriter writer = getWriterByFormat( format );
        writer.setOutput( new MemoryCacheImageOutputStream( out ) );
        ImageWriteParam params = writer.getDefaultWriteParam();
        setCompressionQuality( params, quality );
        writer.write( null, new IIOImage( image, null, null ), params );
        writer.dispose();
    }

    private static void setCompressionQuality( ImageWriteParam params, int quality )
    {
        if ( quality <= 0 )
        {
            quality = 1;
        }

        if ( quality > 100 )
        {
            quality = 100;
        }

        try
        {
            params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
            params.setCompressionQuality( (float) quality / 100f );
        }
        catch ( Exception e )
        {
            // DO nothing since compression not supported
        }
    }

    public static BufferedImage createImage( BufferedImage src, boolean hasAlpha )
    {

        return createImage( src.getWidth(), src.getHeight(), hasAlpha );
    }

    public static BufferedImage createImage( int width, int height, boolean hasAlpha )
    {
        return new BufferedImage( width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB );
    }

    public static BufferedImage getScaledInstance( BufferedImage img, int targetWidth, int targetHeight )
    {
        Image scaledImage = img.getScaledInstance( targetWidth, targetHeight, Image.SCALE_SMOOTH );
        BufferedImage targetImage = createImage( targetWidth, targetHeight, true );
        Graphics g = targetImage.createGraphics();
        g.drawImage( scaledImage, 0, 0, null );
        g.dispose();
        return targetImage;
    }

    public static BufferedImage ensureAlphaChannel( BufferedImage img )
    {
        if ( img.getType() == BufferedImage.TYPE_INT_ARGB )
        {
            return img;
        }

        BufferedImage target = createImage( img, true );
        Graphics g = target.createGraphics();
        g.drawImage( img, 0, 0, null );
        g.dispose();
        return target;
    }

    public static BufferedImage removeAlphaChannel( BufferedImage img, int color )
    {
        if ( !img.getColorModel().hasAlpha() )
        {
            return img;
        }

        BufferedImage target = createImage( img, false );
        Graphics2D g = target.createGraphics();
        g.setColor( new Color( color, false ) );
        g.fillRect( 0, 0, img.getWidth(), img.getHeight() );
        g.drawImage( img, 0, 0, null );
        g.dispose();

        return target;
    }

    public static boolean supportsAlphaChannel( String format )
    {
        return format.equals( "png" );
    }

    public static boolean hasAlphaChannel( BufferedImage img )
    {
        return img.getColorModel().hasAlpha();
    }

    public static String getAutoFormat( BufferedImage img )
    {
        if ( hasAlphaChannel( img ) )
        {
            return "png";
        }
        else
        {
            return "jpg";
        }
    }
}

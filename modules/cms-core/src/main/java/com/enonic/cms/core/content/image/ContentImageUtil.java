package com.enonic.cms.core.content.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.binary.BinaryData;

public class ContentImageUtil
{
    protected static final int[] STANDARD_WIDTH_SIZES = new int[]{256, 512, 1024};

    protected static final String[] STANDARD_WIDTH_LABELS = new String[]{"small", "medium", "large"};

    public static List<BinaryData> createStandardSizeImages( BufferedImage origImage, String encodeType,
                                                             String originalFilenameWithoutExtension )
        throws IOException
    {
        List<BinaryData> binaryData = new ArrayList<BinaryData>();

        for ( int i = 0; i < STANDARD_WIDTH_SIZES.length; i++ )
        {
            final int newWidth = STANDARD_WIDTH_SIZES[i];

            if ( newWidth < origImage.getWidth() )
            {
                BufferedImage scaledImage = scaleNewImage( origImage, encodeType, newWidth );

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageUtil.writeImage( scaledImage, encodeType, baos, 1.0f );

                final String filename =
                    resolveFilenameForFixedWidthImage( originalFilenameWithoutExtension, STANDARD_WIDTH_LABELS[i], encodeType );

                final BinaryData binaryDataFromStream =
                    BinaryData.createBinaryDataFromStream( baos, filename, STANDARD_WIDTH_LABELS[i], null );

                binaryData.add( binaryDataFromStream );
            }
        }

        return binaryData;
    }


    private static BufferedImage scaleNewImage( BufferedImage origImage, String encodeType, int newWidth )
    {
        final int newHeight = findNewHeight( origImage, newWidth );
        return ImageUtil.scaleImage( origImage, newWidth, newHeight, getBufferedImageType( encodeType ) );
    }

    private static int findNewHeight( BufferedImage origImage, double newWidth )
    {
        final double ratio = (double) newWidth / (double) origImage.getWidth();
        return (int) Math.max( 1.0, Math.round( ratio * origImage.getHeight() ) );
    }

    public static int getBufferedImageType( String encodeType )
    {
        if ( encodeType.equals( "png" ) )
        {
            return BufferedImage.TYPE_INT_ARGB;
        }
        else
        {
            return BufferedImage.TYPE_INT_RGB;
        }
    }

    public static String resolveFilenameForScaledImage( String originalImageName, BufferedImage image, String fileType )
    {
        StringBuffer name = new StringBuffer();
        name.append( originalImageName ).append( "_" ).append( image.getWidth() ).append( "x" ).append( image.getHeight() );
        name.append( "." ).append( fileType );
        return name.toString();
    }

    private static String resolveFilenameForFixedWidthImage( String originalImageName, String label, String fileType )
    {
        return originalImageName + ( label != null ? "_" + label : "" ) + "." + fileType;
    }

    public static String getEncodeType( String type )
    {
        String encodeType;

        if ( "png".equals( type ) || "gif".equals( type ) )
        {
            encodeType = "png";
        }
        else
        {
            encodeType = "jpeg";
        }
        return encodeType;
    }
}

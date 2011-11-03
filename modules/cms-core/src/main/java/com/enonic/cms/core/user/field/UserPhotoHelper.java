/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.enonic.cms.framework.util.ImageHelper;

public final class UserPhotoHelper
{
    private final static int MAX_SIZE = 256;

    public static byte[] convertPhoto( byte[] photo )
    {
        if ( photo == null )
        {
            return null;
        }

        try
        {
            return doConvertPhoto( photo );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private static byte[] doConvertPhoto( byte[] photo )
        throws IOException
    {
        BufferedImage image = ImageHelper.readImage( photo );
        image = scaleIfNeeded( image );

        String format = ImageHelper.getAutoFormat( image );
        return ImageHelper.writeImage( image, format, 85 );
    }

    private static BufferedImage scaleIfNeeded( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();

        if ( ( width > MAX_SIZE ) || ( height > MAX_SIZE ) )
        {
            return scaleMax( source );
        }

        return source;
    }

    private static BufferedImage scaleMax( BufferedImage source )
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int max = Math.max( width, height );

        float scale = (float) MAX_SIZE / (float) max;
        int newWidth = (int) ( (float) width * scale );
        int newHeight = (int) ( (float) height * scale );

        return ImageHelper.getScaledInstance( source, newWidth, newHeight );
    }
}

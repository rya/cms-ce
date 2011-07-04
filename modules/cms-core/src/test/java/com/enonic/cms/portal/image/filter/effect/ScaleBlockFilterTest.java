/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image.filter.effect;

import com.enonic.cms.portal.image.filter.ImageFilter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;

public class ScaleBlockFilterTest
        extends BaseImageFilterTest
{
    @Test
    public void testSize()
    {
        BufferedImage scaled = scale( 550, 320 );
        assertEquals( 550, scaled.getWidth() );
        assertEquals( 320, scaled.getHeight() );

        BufferedImage scaled2 = scaleWithOffset( 550, 320, 0.1f, 0.3f );
        assertEquals( 550, scaled2.getWidth() );
        assertEquals( 320, scaled2.getHeight() );
    }

    private BufferedImage scale( int width, int height)
    {
        ImageFilter filter = new ScaleBlockFilter( width, height, 0.5f, 0.5f );
        return filter.filter( getSource() );
    }

    private BufferedImage scaleWithOffset( int width, int height, float xOffset, float yOffset)
    {
        ImageFilter filter = new ScaleBlockFilter( width, height, xOffset, yOffset );
        return filter.filter( getSource() );
    }
}

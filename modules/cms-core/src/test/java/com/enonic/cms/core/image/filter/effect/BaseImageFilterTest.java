/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.effect;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Before;

public abstract class BaseImageFilterTest
{
    private BufferedImage source;

    @Before
    public final void setUp()
        throws Exception
    {
        this.source = ImageIO.read( getClass().getResourceAsStream( "source.jpg" ) );
    }

    protected final BufferedImage getSource()
    {
        return this.source;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.captcha;

import java.awt.image.BufferedImage;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;

/**
 * Project : cms-business Created : 20. okt. 2009
 */
public class TransparentBackgroundGenerator
    implements BackgroundGenerator
{

    final int width;

    final int height;

    public TransparentBackgroundGenerator( final int width, final int height )
    {
        this.width = width;
        this.height = height;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator#getBackground()
     */
    public BufferedImage getBackground()
    {
        BufferedImage background = new BufferedImage( getImageWidth(), getImageHeight(), BufferedImage.TYPE_INT_ARGB );
        return background;

    }

    /**
     * {@inheritDoc}
     *
     * @see com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator#getImageHeight()
     */
    public int getImageHeight()
    {
        return height;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator#getImageWidth()
     */
    public int getImageWidth()
    {
        return width;
    }


}

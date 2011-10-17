/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.TransparentBackgroundProducer;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;

@Component("captchaRepository")
public class CaptchaRepositoryImpl
    implements CaptchaRepository
{

    private static final int FONT_SIZE = 24;

    private Captcha captcha = null;

    /**
     * @inheritDoc
     */
    public BufferedImage getImageChallengeForID( String sessionId, Locale locale )
    {
        captcha = getInstance();
        return captcha.getImage();
    }

    /**
     * @inheritDoc
     */
    public Boolean validateResponseForID( String sessionId, Object userResponse )
    {
        if ( userResponse == null )
        {
            return Boolean.FALSE;
        }

        if ( userResponse instanceof String )
        {
            return captcha.isCorrect( ( (String) userResponse ).toLowerCase() );
        }

        return Boolean.FALSE;
    }

    private Captcha getInstance()
    {
        captcha = setupCustomInstance();
        return captcha;
    }

    private Captcha setupCustomInstance()
    {
        int width = 160;
        int height = 50;
        char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9'};
        DefaultTextProducer text = new DefaultTextProducer( 5, chars );
        TransparentBackgroundProducer background = new TransparentBackgroundProducer();
        RippleGimpyRenderer gimpy = new RippleGimpyRenderer();

        Captcha captcha = new Captcha.Builder( width, height )
        .addText( text, new DefaultWordRenderer(createColorList(), createFontsList() ) )
        .addBackground( background )
        .gimp( gimpy )
        .build();

        return  captcha;
    }

    private List<Font> createFontsList()
    {
        return Arrays.asList(
            new Font[]{ new Font( "Dialog", Font.BOLD, FONT_SIZE ), new Font( "Serif", Font.BOLD, FONT_SIZE ),
            new Font( "SansSerif", Font.BOLD, FONT_SIZE ), } );
    }

    private List<Color> createColorList()
    {
        return Arrays.asList( Color.BLACK );
    }

}


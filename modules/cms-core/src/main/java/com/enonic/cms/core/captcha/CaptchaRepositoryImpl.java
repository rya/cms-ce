/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.util.Locale;

import com.jhlabs.image.WaterFilter;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.gimpy.BasicGimpyEngine;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * @inheritDoc
 */
public class CaptchaRepositoryImpl
    implements CaptchaRepository
{

    private static final int FONT_SIZE = 24;

    private ImageCaptchaService instance = getInstance();

    /**
     * @inheritDoc
     */
    public BufferedImage getImageChallengeForID( String sessionId, Locale locale )
    {
        return instance.getImageChallengeForID( sessionId, locale );
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
            return instance.validateResponseForID( sessionId, ( (String) userResponse ).toLowerCase() );
        }

        return instance.validateResponseForID( sessionId, userResponse );
    }

    private ImageCaptchaService getInstance()
    {
        if ( instance == null )
        {

            instance = setupCustomInstance();
        }

        return instance;
    }

    private ImageCaptchaService setupCustomInstance()
    {
        RandomWordGenerator generator = new RandomWordGenerator( "abcdefghijkmnpqrstuvwxyz23456789" );
        RandomFontGenerator fontGenerator = new RandomFontGenerator( FONT_SIZE, FONT_SIZE, createFontsList() );

        TransparentBackgroundGenerator background = new TransparentBackgroundGenerator( 160, 50 );
        RandomTextPaster textPaster = new RandomTextPaster( 5, 5, Color.BLACK );

        WaterFilter waterFilter = new WaterFilter();
        waterFilter.setAntialias( true );
        waterFilter.setAmplitude( 2 );
        waterFilter.setWavelength( 50 );
        ImageFilter[] imageFilters = new ImageFilter[]{waterFilter};
        ImageFilter[] emptyFilters = new ImageFilter[]{};
        ImageDeformationByFilters imageDeformation = new ImageDeformationByFilters( imageFilters );
        ImageDeformationByFilters emptyDeformation = new ImageDeformationByFilters( emptyFilters );

        DeformedComposedWordToImage word2image =
            new DeformedComposedWordToImage( fontGenerator, background, textPaster, emptyDeformation, imageDeformation, emptyDeformation );

        ImageCaptchaFactory factory = new GimpyFactory( generator, word2image );
        BasicGimpyEngine engine = new BasicGimpyEngine();
        engine.setFactories( new CaptchaFactory[]{factory} );
        DefaultManageableImageCaptchaService serviceInstance = new DefaultManageableImageCaptchaService();
        serviceInstance.setCaptchaEngine( engine );
        serviceInstance.setCaptchaStoreMaxSize( 200000 );
        serviceInstance.setMinGuarantedStorageDelayInSeconds( 300 );

        serviceInstance.setCaptchaEngine( engine );
        return serviceInstance;
    }


    private Font[] createFontsList()
    {

        return new Font[]{new Font( "Dialog", Font.BOLD, FONT_SIZE ), new Font( "Serif", Font.BOLD, FONT_SIZE ),
            new Font( "SansSerif", Font.BOLD, FONT_SIZE ),};

    }

//    private Font getFirstAvailableFontFromJre() {
//
//        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        Font[] fonts = environment.getAllFonts();
//
//
//        if ( fonts != null && fonts.length > 0 )
//        {
//            return fonts[0];
//        }
//        //fallback
//        return new Font( "SansSerif", Font.BOLD, FONT_SIZE);
//
//    }

//  return new Font[] {
//  new Font( "LucidaBright", Font.BOLD, size)
//  new Font( "Arial ", Font.PLAIN, size ), new Font( "Arial ", Font.BOLD, size ), new Font( "Arial ", Font.ITALIC, size ),
//  new Font( "Courier ", Font.PLAIN, size ), new Font( "Courier ", Font.BOLD, size ), new Font( "Courier ", Font.ITALIC, size ),
//  new Font( "Dialog ", Font.PLAIN, size ), new Font( "Dialog ", Font.BOLD, size ), new Font( "Dialog ", Font.ITALIC, size ),
//  new Font( "Geneva ", Font.PLAIN, size ), new Font( "Geneva ", Font.BOLD, size ), new Font( "Geneva ", Font.ITALIC, size ),
//  new Font( "Gill Sans ", Font.PLAIN, size ), new Font( "Gill Sans ", Font.BOLD, size ), new Font( "Gill Sans ", Font.ITALIC, size ),
//  new Font( "Helvetica ", Font.PLAIN, size ), new Font( "Helvetica ", Font.BOLD, size ), new Font( "Helvetica ", Font.ITALIC, size ),
//  new Font( "Lucida Console ", Font.PLAIN, size ), new Font( "Lucida Console ", Font.BOLD, size ), new Font( "Lucida Console ", Font.ITALIC, size ),
//  new Font( "Monospaced ", Font.PLAIN, size ), new Font( "Monospaced ", Font.BOLD, size ), new Font( "Monospaced ", Font.ITALIC, size ),
//  new Font( "SansSerif ", Font.PLAIN, size ), new Font( "SansSerif ", Font.BOLD, size ), new Font( "SansSerif ", Font.ITALIC, size ),
//  new Font( "Serif ", Font.PLAIN, size ), new Font( "Serif ", Font.BOLD, size ), new Font( "Serif ", Font.ITALIC, size ),
//  new Font( "Times New Roman ", Font.PLAIN, size ), new Font( "Times New Roman ", Font.BOLD, size ), new Font( "Times New Roman ", Font.ITALIC, size ),
//};

}


/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;

/**
 * Repository for the sentral captcha service.  It is important that there is only one service in use, as it stores the requested captchas
 * based on the Session ID, and the verification of the captcha must be done, using the same
 */
public interface CaptchaRepository
{
    /**
     * Image captcha generator.  The AWT image object returned, can be turned into a number of formats, using the Sun image classes that
     * comes as extensions to the JDK.
     *
     * @param sessionId This id is used to store information about the generated captcha, so that it may later be verified.
     * @param locale    Required by the backend to know the Locale of the installation.
     * @return An image with a text, readable by humans, but not by computers.
     */
    BufferedImage getImageChallengeForID( String sessionId, Locale locale );

    Boolean validateResponseForID( String sessionId, Object userResponse );
}

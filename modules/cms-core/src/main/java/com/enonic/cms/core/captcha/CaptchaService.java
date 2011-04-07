/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.captcha;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.domain.SiteKey;

/**
 *
 */
public interface CaptchaService
{
    /**
     * The standard form variable used to contain the answer from a user to a captcha.
     */
    public final String FORM_VARIABLE_CAPTCHA_RESPONSE = "_captcha_response";

    /**
     * Checks the configuration to see if a captcha is required for the given operation, and if so, validates the formItem,
     * <code>_captcha_response</code>, using the <code>CaptchaRepository</code>, to see that the user typed in a correct text.
     * <p/>
     * If the captcha validation is required for the operation, but the captcha did not validate, an error XML is created and added to the
     * <code>VerticalSession</code> object.
     *
     * @param formItems All the items in the form that the user has entered.
     * @param request   Information on the request, including access to the session.
     * @param handler   The current calling handler, which is checked to see if captcha validation is required.
     * @param operation The current calling operation, which is checked to see if captcha validation is required.
     * @return <code>true</code> if the captcha validated OK, <code>false</code> if the captcha did not validate OK, and <code>null</code>
     *         if no captcha validation was required.
     */
    Boolean validateCaptcha( Map<String, Object> formItems, HttpServletRequest request, String handler, String operation );

    /**
     * This is a utility method that builds an XML of all the parameters for error handling purposes.  The result should be placed in the
     * session scope, so that the XSL engine may grab the data and put it back as defaults in the form, so that the user do not have to
     * type in all the data again, when the captcha failed.
     * <p/>
     * Note: This method is general.  We should look for other uses of this, and possibly place it in a more suitable class.
     *
     * @param formItems All the data from the form, that the user entered.  System parameters starting with underscore ('_'), will not be
     *                  included in the returned XML.
     * @return An XML document containing all the parameters that the user entered.
     */
    XMLDocument buildErrorXMLForSessionContext( Map<String, Object> formItems );

    /**
     * Checks the system properties to see whether a request to the given site and handler, using the given operation, will be captcha
     * validated.
     *
     * @param siteKey   The siteKey of the current site.
     * @param handler   The handler that the captcha should protect.
     * @param operation The operation on the handler that should be protected.  One of <code>create</code>, <code>update</code>,
     *                  <code>remove</code> or <code>*</code>
     * @return <code>true</code>, if the captcha will be checked, <code>false</code> otherwise.
     */
    boolean hasCaptchaCheck( SiteKey siteKey, String handler, String operation );
}

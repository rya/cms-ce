/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

public class SiteUrlPathHelper
        extends UrlPathHelper
{
    private String REGEXP_STRING = "\\+";

    private String REPLACE_STRING = "%20";

    /**
     * Decode the given source string with a URLDecoder. The encoding will be taken
     * from the request, falling back to the default "ISO-8859-1".
     * <p>The default implementation uses <code>URLDecoder.decode(input, enc)</code>.
     * <p/>
     * This method also replaces <code>+</code> with <code>%20</code> before decoding.
     *
     * @param request current HTTP request
     * @param source  the String to decode
     * @return the decoded String
     * @see org.springframework.web.util.WebUtils#DEFAULT_CHARACTER_ENCODING
     * @see javax.servlet.ServletRequest#getCharacterEncoding
     * @see java.net.URLDecoder#decode(String, String)
     * @see java.net.URLDecoder#decode(String)
     */
    @Override
    public String decodeRequestString( HttpServletRequest request, String source )
    {
        source = source.replaceAll( REGEXP_STRING, REPLACE_STRING );
        return super.decodeRequestString( request, source );
    }
}

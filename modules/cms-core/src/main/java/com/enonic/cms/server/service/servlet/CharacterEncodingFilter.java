/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.enonic.vertical.VerticalProperties;

public class CharacterEncodingFilter
    extends OncePerRequestFilter
{
    private String encoding;

    protected void initFilterBean()
        throws ServletException
    {

        String requestCharacterEncoding = VerticalProperties.getVerticalProperties().getUrlCharacterEncoding();
        encoding = requestCharacterEncoding;
    }

    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain )
        throws ServletException, IOException
    {
        if ( request.getCharacterEncoding() == null )
        {
            request.setCharacterEncoding( encoding );
        }

        final String forcedCharset = request.getParameter( "_charset" );
        if ( !StringUtils.isBlank( forcedCharset ) )
        {
            response.setCharacterEncoding( forcedCharset );
        }
        else
        {
            response.setCharacterEncoding( encoding );
        }

        filterChain.doFilter( request, response );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.vertical.VerticalProperties;

public class CharacterEncodingFilter
    extends org.springframework.web.filter.CharacterEncodingFilter
{

    protected void initFilterBean()
        throws ServletException
    {

        String requestCharacterEncoding = VerticalProperties.getVerticalProperties().getUrlCharacterEncoding();
        setEncoding( requestCharacterEncoding );
        setForceEncoding( true );
    }


    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain )
        throws ServletException, IOException
    {

        super.doFilterInternal( request, response, filterChain );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;

import com.enonic.cms.framework.translation.TranslationWriter;
import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.business.AdminConsoleTranslationService;

public class ResourceTranslationController
    extends ResourceController
{

    private AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();


    protected void serveResourceToResponse( HttpServletRequest request, HttpServletResponse response, Resource resource )
        throws Exception
    {

        StringBuffer contentType = new StringBuffer();
        contentType.append( getServletContext().getMimeType( resource.getFilename() ) );
        contentType.append( "; charset=UTF-8" );
        response.setContentType( contentType.toString() );

        String languageCode = (String) request.getSession( true ).getAttribute( "languageCode" );
        Map translationMap = languageMap.getTranslationMap( languageCode );
        TranslationWriter translationWriter = new TranslationWriter( translationMap, response.getWriter() );

        InputStreamReader in = new InputStreamReader( resource.getInputStream(), "UTF-8" );
        HttpServletUtil.copyNoCloseOut( in, translationWriter );
    }

}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.support.ServletContextResourceLoader;

import com.enonic.esl.io.TranslationReader;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.business.AdminConsoleTranslationService;

/**
 * Utility class for admin-console resources.
 */
public final class AdminStore
{
    /**
     * Stylesheet base.
     */
    private static String BASE_PATH;

    /**
     * Resource loader.
     */
    private static ResourceLoader RESOURCE_LOADER;

    private final static class StylesheetURIResolver
        implements URIResolver
    {
        private final String languageCode;

        StylesheetURIResolver( String languageCode )
        {
            this.languageCode = languageCode;
        }

        public Source resolve( String href, String baseHref )
            throws TransformerException
        {

            if ( !href.startsWith( "/" ) )
            {
                href = baseHref.substring( 0, baseHref.lastIndexOf( '/' ) + 1 ) + href;
            }

            return getStylesheet( languageCode, href, true );
        }
    }

    /**
     * Initialize the store.
     */
    public static void initialize( ServletContext context, String basePath )
    {
        if (BASE_PATH == null) {
            BASE_PATH = basePath;
            RESOURCE_LOADER = new ServletContextResourceLoader( context );
        }
    }

    private static URL findResource( String path, boolean absolute )
        throws IOException
    {
        if ( !absolute )
        {
            Resource resource = RESOURCE_LOADER.getResource( BASE_PATH + "/" + path );
            return resource.getURL();
        }
        else
        {
            return new URL( path );
        }
    }

    private static Reader openStream( URL url, String languageCode )
        throws IOException
    {
        InputStream in = url.openStream();
        AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();
        Map translationMap = languageMap.getTranslationMap( languageCode );
        return new TranslationReader( translationMap, new InputStreamReader( in, "UTF-8" ) );
    }

    public static Source getStylesheet( String languageCode, String path )
    {
        return getStylesheet( languageCode, path, false );
    }

    public static Source getStylesheet( String languageCode, String path, boolean absolute )
    {
        return getStylesheetAsDocument(languageCode, path, absolute).getAsSource();
    }

    public static XMLDocument getStylesheetAsDocument( String languageCode, String path, boolean absolute )
    {
        try
        {
            URL url = findResource( path, absolute );
            XMLDocument doc = XMLDocumentFactory.create( openStream( url, languageCode ) );
            doc.setSystemId( url.toString() );
            return doc;
        }
        catch ( Exception e )
        {
            VerticalAdminLogger.fatalAdmin( AdminStore.class, 10, "Could not read resource: %0", path, e );
            return null;
        }
    }

    public static Source getStylesheet( HttpSession session, String path )
    {
        String languageCode = (String) session.getAttribute( "languageCode" );
        return getStylesheet( languageCode, path );
    }

    public static Reader getXML( String languageCode, String name )
    {
        String path = "xml/" + name;

        try
        {
            URL url = findResource( path, false );
            return openStream( url, languageCode );
        }
        catch ( Exception e )
        {
            VerticalAdminLogger.fatalAdmin( AdminStore.class, 10, "Could not read resource: %0", path, e );
            return null;
        }
    }

    public static Reader getXML( HttpSession session, String name )
    {
        String languageCode = (String) session.getAttribute( "languageCode" );
        return getXML( languageCode, name );
    }

    public static URIResolver getURIResolver( String languageCode )
    {
        return new StylesheetURIResolver( languageCode );
    }
}

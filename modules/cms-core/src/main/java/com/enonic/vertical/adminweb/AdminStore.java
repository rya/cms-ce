/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import com.enonic.esl.io.TranslationReader;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.AdminConsoleTranslationService;

/**
 * Utility class for admin-console resources.
 */
public final class AdminStore
{
    /**
     * Stylesheet base.
     */
    private final static String STYLESHEET_PATH =  "/META-INF/stylesheets";

    private final static String HREF_PREFIX = "stylesheet://";

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
            String basePath = "";

            if (baseHref.startsWith(HREF_PREFIX)) {
                final String tmp = baseHref.replace(HREF_PREFIX, "");
                final int pos = tmp.lastIndexOf('/');
                if (pos > -1) {
                    basePath = tmp.substring(0, pos) + "/";
                }
            }

            return getStylesheet( languageCode, basePath + href);
        }
    }

    private static URL findResource( String path )
        throws Exception
    {
        final String normalized = new URI(STYLESHEET_PATH + "/" + path).normalize().toString();
        return AdminStore.class.getResource(normalized);
    }

    private static Reader openStream( URL url, String languageCode )
        throws Exception
    {
        AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();
        Map translationMap = languageMap.getTranslationMap( languageCode );
        return new TranslationReader( translationMap, new InputStreamReader( url.openStream(), "UTF-8" ) );
    }

    public static Source getStylesheet(String languageCode, String path)
    {
        return getStylesheetAsDocument(languageCode, path).getAsSource();
    }

    public static XMLDocument getStylesheetAsDocument(String languageCode, String path)
    {
        try
        {
            URL url = findResource( path );
            XMLDocument doc = XMLDocumentFactory.create( openStream( url, languageCode ) );
            doc.setSystemId( HREF_PREFIX + path );
            return doc;
        }
        catch ( Exception e )
        {
            VerticalAdminLogger.errorAdmin("Could not read resource: {0}", path, e);
            return null;
        }
    }

    public static Source getStylesheet( HttpSession session, String path )
    {
        String languageCode = (String) session.getAttribute( "languageCode" );
        return getStylesheet( languageCode, path);
    }

    public static Reader getXML( String languageCode, String name )
    {
        String path = "xml/" + name;

        try
        {
            URL url = findResource( path );
            return openStream( url, languageCode );
        }
        catch ( Exception e )
        {
            VerticalAdminLogger.errorAdmin("Could not read resource: {0}", path, e);
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

package com.enonic.vertical.adminweb.resource;

import com.enonic.cms.core.AdminConsoleTranslationService;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import com.enonic.vertical.adminweb.TranslationReader;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.google.common.base.Charsets;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

public final class AdminResourcesImpl
    extends AdminResources implements ResourceLoaderAware
{
    private final static String STYLESHEET_PATH =  "/WEB-INF/stylesheets/";
    private final static String HREF_PREFIX = "stylesheet://";

    private ResourceLoader resourceLoader;

    private final class StylesheetURIResolver
        implements URIResolver
    {
        private final String languageCode;

        private StylesheetURIResolver( String languageCode )
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

            return getStylesheet( languageCode, basePath + href).getAsSource();
        }
    }

    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public XMLDocument getStylesheet(final String languageCode, final String path)
    {
        final XMLDocument doc = readDocument(languageCode, path);
        doc.setSystemId( HREF_PREFIX + path );
        return doc;
    }

    public XMLDocument getXml(final String languageCode, final String name )
    {
        return readDocument(languageCode, "xml/" + name);
    }

    public URIResolver getURIResolver( final String languageCode )
    {
        return new StylesheetURIResolver( languageCode );
    }

    private XMLDocument readDocument( final String languageCode, final String path )
    {
        try
        {
            return doReadDocument(languageCode, path);
        }
        catch ( Exception e )
        {
            VerticalAdminLogger.errorAdmin("Could not read resource: {0}", path, e);
            return null;
        }
    }

    private XMLDocument doReadDocument( final String languageCode, final String path )
        throws Exception
    {
        final Resource resource = this.resourceLoader.getResource(STYLESHEET_PATH + path);
        final Reader reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
        
        final AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();
        final Map translationMap = languageMap.getTranslationMap( languageCode );
        final Reader translationReader = new TranslationReader( translationMap, reader );

        return XMLDocumentFactory.create(translationReader);
    }
}

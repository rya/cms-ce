/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.xml.StringSource;

import com.enonic.cms.core.resource.ResourceService;

import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceKey;

/**
 * This class resolves the stylesheets and also other resources. It should be renamed to reflect that it also resolves other files that
 * stylesheets.
 */
public final class StyleSheetURIResolver
        implements URIResolver
{

    private static final Logger LOG = LoggerFactory.getLogger( StyleSheetURIResolver.class );

    /**
     * Resource service.
     */
    private final ResourceService resourceService;

    /**
     * Construct the url resolver.
     */
    public StyleSheetURIResolver( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    /**
     * Resolve the reference.
     */
    public Source resolve( String href, String base )
            throws TransformerException
    {
        final ResourceKey resourceKey = new ResourceKey( getResourcePath( href, base ) );
        final ResourceFile resource = this.resourceService.getResourceFile( resourceKey );

        if ( resource == null )
        {
            final String message =
                    "Failed to resolve resource, did not find it: " + resourceKey.toString() + " (" + href + ")";
            LOG.error( message );
            throw new TransformerException( message );
        }

        final String resourceData = resource.getDataAsString();

        if ( resourceData == null )
        {
            final String message =
                    "Failed to resolve resource, resource data was null: " + resourceKey.toString() + " (" + href + ")";
            LOG.error( message );
            throw new TransformerException( message );
        }

        return new StringSource( resourceData, resourceKey.toString() );
    }

    private String getResourcePath( String href, String base )
    {
        if ( href.startsWith( "/" ) )
        {
            return href;
        }
        return resolveBase( base ) + href;
    }

    private String resolveBase( String base )
    {
        try
        {
            base = URLDecoder.decode( base, "UTF-8" );

            final String parentSepStart = "dummy:/";
            final String parentSepEnd = "/";

            return base.substring( base.indexOf( parentSepStart ) + parentSepStart.length(),
                                   base.lastIndexOf( parentSepEnd ) + parentSepEnd.length() );

        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }
}

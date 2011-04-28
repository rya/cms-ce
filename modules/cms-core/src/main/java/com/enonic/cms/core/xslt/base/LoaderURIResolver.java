package com.enonic.cms.core.xslt.base;

import com.enonic.cms.core.xslt.XsltResource;
import com.enonic.cms.core.xslt.XsltResourceLoader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public final class LoaderURIResolver
    implements URIResolver
{
    private final XsltResourceLoader loader;
    private final String parentPath;

    public LoaderURIResolver(final XsltResourceLoader loader, final String parentPath)
    {
        this.loader = loader;
        this.parentPath = parentPath;
    }

    public Source resolve(final String href, final String base )
        throws TransformerException
    {
        if (href.contains("://")) {
            return resolveRemote(href);
        } else {
            return resolveLocal(href, base);
        }
    }

    private Source resolveRemote(final String href)
    {
        return new StreamSource(href);
    }

    private Source resolveLocal(final String path, final String base)
    {
        final String resolvedPath = resolveLocalPath(path, base);
        return load(resolvedPath);
    }

    private String resolveLocalPath(final String path, String base)
    {
        if (base == null) {
            base = this.parentPath;
        } else if (base.equals("")) {
            base = this.parentPath;
        } else if (base.startsWith(XsltResource.LOCAL_PREFIX)) {
            base = base.substring(XsltResource.LOCAL_PREFIX.length());
        }

        if (path.startsWith("/")) {
            return path;
        }

        final int pos = base.lastIndexOf('/');

        if (pos <= 0) {
            return path;
        }

        return base.substring(0, pos + 1) + path;
    }

    private Source load(final String path)
    {
        final XsltResource res = this.loader.load(path);
        return (res != null) ? res.getSource() : null;
    }
}

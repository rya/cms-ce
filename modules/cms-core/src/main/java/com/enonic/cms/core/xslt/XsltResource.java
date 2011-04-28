package com.enonic.cms.core.xslt;

import org.jdom.Document;
import org.jdom.transform.JDOMSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public final class XsltResource
{
    public final static String LOCAL_PREFIX = "local://";

    private final String path;
    private final Source source;

    private XsltResource(final String path, final Source source)
    {
        this.path = path;
        this.source = source;
        this.source.setSystemId(LOCAL_PREFIX + this.path);
    }

    public String getPath()
    {
        return this.path;
    }

    public Source getSource()
    {
        return this.source;
    }

    private static Source toSource(final Document data)
    {
        return new JDOMSource(data);
    }

    private static Source toSource(final String data)
    {
        return new StreamSource(new StringReader(data));
    }

    public static XsltResource create(final String path, final Document doc)
    {
        return new XsltResource(path, new JDOMSource(doc));
    }

    public static XsltResource create(final String path, final String doc)
    {
        return new XsltResource(path, new StreamSource(new StringReader(doc)));
    }
}

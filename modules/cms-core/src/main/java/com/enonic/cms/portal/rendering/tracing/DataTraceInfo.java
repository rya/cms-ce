/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.tracing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.domain.SiteKey;

/**
 * This class implements a trace info with data elements.
 */
public abstract class DataTraceInfo
    extends TraceInfo
{
    /**
     * A list of functions.
     */
    private final ArrayList<FunctionTraceInfo> functions;

    /**
     * Data source result.
     */
    private XMLDocument dataSourceResult;

    private final Map<Integer, String> contentInfo;

    private final Map<Integer, String> relatedContentInfo;

    private SiteKey siteKey;

    /**
     * Construct the info.
     */
    public DataTraceInfo()
    {
        this.functions = new ArrayList<FunctionTraceInfo>();
        contentInfo = new LinkedHashMap<Integer, String>();
        relatedContentInfo = new LinkedHashMap<Integer, String>();
    }

    /**
     * Return the title.
     */
    public abstract String getTitle();

    /**
     * Return the objects.
     */
    public List<FunctionTraceInfo> getFunctions()
    {
        return Collections.unmodifiableList( this.functions );
    }

    /**
     * Return render trace as xml.
     */
    public final XMLDocument getRenderTraceAsXml()
    {
        return XMLDocumentFactory.create( new Document( getDataTraceXmlElement() ) );
    }

    /**
     * Return render trace as xml.
     */
    public abstract Element getDataTraceXmlElement();

    /**
     * Build function trace xml.
     */
    protected final void appendFunctionsTraceXml( Element root )
    {
        for ( FunctionTraceInfo function : this.functions )
        {
            Element elem = new Element( "function" );
            elem.setAttribute( "name", function.getName() );
            elem.setAttribute( "time", "" + function.getTotalTime() );
            root.addContent( elem );
        }
    }

    /**
     * Add a page object.
     */
    public void addFunction( FunctionTraceInfo info )
    {
        this.functions.add( info );
    }

    /**
     * Return the data source result.
     */
    public XMLDocument getDataSourceResult()
    {
        return this.dataSourceResult;
    }

    /**
     * Set data source result.
     */
    public void setDataSourceResult( XMLDocument dataSourceResult )
    {
        this.dataSourceResult = dataSourceResult;
    }

    public void setSiteKey( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public void addContentInfo( int key, String title )
    {
        contentInfo.put( key, title );
    }

    public Map<Integer, String> getContentInfo()
    {
        return contentInfo;
    }

    public void addRelatedContentInfo( int key, String title )
    {
        relatedContentInfo.put( key, title );
    }

    public Map<Integer, String> getRelatedContentInfo()
    {
        return relatedContentInfo;
    }

}

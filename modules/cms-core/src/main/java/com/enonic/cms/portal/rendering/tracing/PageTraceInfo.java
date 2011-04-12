/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.tracing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.structure.portlet.PortletKey;

/**
 * This class implements the page trace info.
 */
public final class PageTraceInfo
    extends DataTraceInfo
{

    private final int key;

    private String name = "";

    private String displayName = "";

    private String pageTemplateName;

    private QualifiedUsername runAsUser;

    private Long functionTime;

    private Long portletsTime;

    private boolean cacheable;

    private final Map<PortletKey, PagePortletTraceInfo> portlets;


    public PageTraceInfo( int key )
    {
        this.key = key;
        this.portlets = new HashMap<PortletKey, PagePortletTraceInfo>();
    }

    public int getKey()
    {
        return this.key;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getTitle()
    {
        return this.name;
    }

    public void setName( String value )
    {
        this.name = value;
    }

    public void setDisplayName( String value )
    {
        this.displayName = value;
    }

    public void setPageTemplateName( String value )
    {
        this.pageTemplateName = value;
    }

    public boolean isCacheable()
    {
        return cacheable;
    }

    public void setCacheable( boolean cacheable )
    {
        this.cacheable = cacheable;
    }

    public PagePortletTraceInfo getPortlet( PortletKey portletKey )
    {
        try
        {
            return portlets.get( portletKey );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    public Collection<PagePortletTraceInfo> getPortlets()
    {
        return portlets.values();
    }

    public void addPortlet( PagePortletTraceInfo value )
    {
        portlets.put( value.getKey(), value );
    }

    public void setRunAsUser( QualifiedUsername value )
    {
        this.runAsUser = value;
    }

    public long getFunctionTime()
    {
        if ( functionTime == null )
        {
            functionTime = resolveFunctionTime();
        }

        return functionTime;
    }


    public long getProcessingTime()
    {
        return getTotalTime() - getFunctionTime() - getPortletsTime();
    }

    public long getPortletsTime()
    {
        if ( portletsTime == null )
        {
            portletsTime = resolvePortletsTime();
        }

        return portletsTime;
    }

    public Element getDataTraceXmlElement()
    {
        Element rootEl = new Element( "page" );
        rootEl.setAttribute( "key", "" + getKey() );
        rootEl.setAttribute( "name", getName() );
        rootEl.setAttribute( "display-name", getDisplayName() );
        rootEl.setAttribute( "cacheable", String.valueOf( cacheable ) );
        rootEl.setAttribute( "page-template-name", pageTemplateName );
        rootEl.setAttribute( "total-time", "" + getTotalTime() );
        rootEl.setAttribute( "processing-time", "" + getProcessingTime() );
        rootEl.setAttribute( "run-as-user", "" + runAsUser.toString() );

        Element functionsEl = new Element( "functions" );
        functionsEl.setAttribute( "total-time", "" + getFunctionTime() );
        appendFunctionsTraceXml( functionsEl );
        rootEl.addContent( functionsEl );

        Element portletsEl = new Element( "portlets" );
        portletsEl.setAttribute( "total-time", "" + getPortletsTime() );
        appendPortletsTraceXmlElement( portletsEl );
        rootEl.addContent( portletsEl );

        return rootEl;
    }

    private long resolvePortletsTime()
    {
        int time = 0;
        for ( PagePortletTraceInfo portlet : getPortlets() )
        {
            time += portlet.getTotalTime();
        }
        return time;
    }

    private long resolveFunctionTime()
    {
        int time = 0;
        for ( FunctionTraceInfo function : getFunctions() )
        {
            time += function.getTotalTime();
        }
        return time;
    }

    private void appendPortletsTraceXmlElement( Element root )
    {
        for ( PagePortletTraceInfo object : this.portlets.values() )
        {
            root.addContent( object.getDataTraceXmlElement() );
        }
    }
}

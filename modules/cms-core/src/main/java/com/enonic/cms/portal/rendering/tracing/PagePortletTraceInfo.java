/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.tracing;

import org.jdom.Element;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.structure.portlet.PortletKey;

/**
 * This class implements the page object trace info.
 */
public final class PagePortletTraceInfo
    extends DataTraceInfo
{

    private final PortletKey key;

    private String name;

    private boolean cacheable;

    private QualifiedUsername runAsUser;

    private Long functionTime;

    public PagePortletTraceInfo( PortletKey key )
    {
        this.key = key;
    }

    public PortletKey getKey()
    {
        return this.key;
    }

    public String getName()
    {
        return this.name;
    }

    public String getTitle()
    {
        return this.name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public boolean isCacheable()
    {
        return cacheable;
    }

    public void setCacheable( boolean cacheable )
    {
        this.cacheable = cacheable;
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
        return getTotalTime() - getFunctionTime();
    }

    public Element getDataTraceXmlElement()
    {
        Element rootEl = new Element( "portlet" );
        rootEl.setAttribute( "key", "" + getKey() );
        rootEl.setAttribute( "name", getName() );
        rootEl.setAttribute( "cacheable", String.valueOf( cacheable ) );
        rootEl.setAttribute( "total-time", "" + getTotalTime() );
        rootEl.setAttribute( "processing-time", "" + getProcessingTime() );
        rootEl.setAttribute( "run-as-user", "" + runAsUser.toString() );

        Element functionsEl = new Element( "functions" );
        functionsEl.setAttribute( "total-time", "" + getFunctionTime() );
        appendFunctionsTraceXml( functionsEl );
        rootEl.addContent( functionsEl );

        return rootEl;
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

}

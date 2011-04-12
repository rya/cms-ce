/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;

import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;

public class PageEntity
    implements Serializable
{
    private int key;

    private LazyInitializedJDOMDocument xmlData;

    private PageTemplateEntity template;

    private Set<PageWindowEntity> pageWindows = new HashSet<PageWindowEntity>();

    public PageEntity()
    {
    }

    public PageEntity( final PageEntity page )
    {
        this();
        this.key = page.getKey();
        this.xmlData = page.getXmlData();
        this.template = page.getTemplate();
        this.pageWindows = new LinkedHashSet<PageWindowEntity>( page.getPageWindows() );
    }

    public int getKey()
    {
        return key;
    }

    public LazyInitializedJDOMDocument getXmlData()
    {
        return xmlData;
    }

    public Document getXmlDataAsDocument()
    {
        if ( xmlData == null )
        {
            return null;
        }
        return xmlData.getDocument();
    }

    public PageTemplateEntity getTemplate()
    {
        return template;
    }

    public Set<PageWindowEntity> getPageWindows()
    {
        return pageWindows;
    }

    public void setKey( int key )
    {
        this.key = key;
    }

    public void setXmlData( Document value )
    {
        if ( value == null )
        {
            this.xmlData = null;
        }
        else
        {
            this.xmlData = LazyInitializedJDOMDocument.parse( value );
        }
    }

    public void setTemplate( PageTemplateEntity template )
    {
        this.template = template;
    }

    public void removeAllPortletPlacements()
    {
        this.pageWindows.clear();
    }

    public void addPortletPlacement( PageWindowEntity value )
    {
        this.pageWindows.add( value );
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageEntity ) )
        {
            return false;
        }

        PageEntity that = (PageEntity) o;

        if ( key != that.getKey() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 233, 865 ).append( key ).toHashCode();
    }

}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.TemplateParameter;
import com.enonic.cms.portal.datasource.Datasources;
import com.enonic.cms.portal.datasource.DatasourcesType;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.TemplateParameterType;
import com.enonic.cms.core.structure.page.Region;

public class PageTemplateEntity
    implements Serializable
{
    private int key;

    private String name;

    private String description;

    private Date timestamp;

    private LazyInitializedJDOMDocument xmlData;

    private ResourceKey styleKey;

    private SiteEntity site;

    private ResourceKey cssKey;

    private PageTemplateType type;

    private RunAsType runAs;

    private Set<PageTemplateRegionEntity> pageTemplateRegions = new HashSet<PageTemplateRegionEntity>();

    private List<PageTemplatePortletEntity> pagetTemplatePortlets = new ArrayList<PageTemplatePortletEntity>();

    private Set<ContentTypeEntity> contentTypes;

    private transient Datasources datasources;

    private transient Map<String, Region> regions;

    public int getKey()
    {
        return key;
    }

    public PageTemplateKey getPageTemplateKey()
    {
        return new PageTemplateKey( getKey() );
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public Document getXmlData()
    {
        if ( xmlData == null )
        {
            return null;
        }

        return xmlData.getDocument();
    }

    public Document getXmlDataAsJDOMDocument()
    {
        if ( xmlData == null )
        {
            return null;
        }

        return xmlData.getDocument();
    }

    public ResourceKey getStyleKey()
    {
        return styleKey;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public ResourceKey getCssKey()
    {
        return cssKey;
    }

    public PageTemplateType getType()
    {
        return type;
    }

    public RunAsType getRunAs()
    {
        return runAs;
    }

    public Set<ContentTypeEntity> getContentTypes()
    {
        return contentTypes;
    }

    public void addPageTemplateRegion( PageTemplateRegionEntity value )
    {
        pageTemplateRegions.add( value );
    }

    public Set<PageTemplateRegionEntity> getPageTemplateRegions()
    {
        return pageTemplateRegions;
    }

    public void addPagetTemplatePortlet( PageTemplatePortletEntity value )
    {
        pagetTemplatePortlets.add( value );
    }

    public List<PageTemplatePortletEntity> getPortlets()
    {
        return pagetTemplatePortlets;
    }

    public void setKey( int key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
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

    public void setStyleKey( ResourceKey styleKey )
    {
        this.styleKey = styleKey;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public void setCssKey( ResourceKey cssKey )
    {
        this.cssKey = cssKey;
    }

    public void setType( PageTemplateType type )
    {
        this.type = type;
    }

    public void setRunAs( RunAsType runAs )
    {
        this.runAs = runAs;
    }

    public void setContentTypes( Set<ContentTypeEntity> contentTypes )
    {
        this.contentTypes = contentTypes;
    }

    public Map<String, TemplateParameter> getTemplateParameters()
    {
        Map<String, TemplateParameter> params = new LinkedHashMap<String, TemplateParameter>();

        Element rootEl = getXmlDataAsJDOMDocument().getRootElement();
        @SuppressWarnings({"unchecked"}) List<Element> paramElList = rootEl.getChildren( "pagetemplateparameter" );
        for ( Element paramEl : paramElList )
        {
            TemplateParameterType type = TemplateParameterType.parse( paramEl.getAttributeValue( "type" ) );
            String name = paramEl.getAttributeValue( "name" );
            String value = paramEl.getAttributeValue( "value" );
            if ( value != null && value.length() == 0 )
            {
                value = null;
            }
            TemplateParameter templateParameter = new TemplateParameter( type, name, value );
            params.put( name, templateParameter );
        }

        return params;
    }

    public Datasources getDatasources()
    {
        if ( datasources == null )
        {
            Element rootEl = getXmlDataAsJDOMDocument().getRootElement();
            Element datasourcesEl = rootEl.getChild( "datasources" );
            if ( datasourcesEl != null )
            {
                datasources = new Datasources( DatasourcesType.PAGETEMPLATE, datasourcesEl );
            }
            else
            {
                datasources = new Datasources( DatasourcesType.PAGETEMPLATE );
            }
        }

        return datasources;
    }

    public Element getDocumentElement()
    {
        Document doc = getXmlDataAsJDOMDocument();
        Element rootEl = doc.getRootElement();
        return rootEl.getChild( "document" );
    }


    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageTemplateEntity ) )
        {
            return false;
        }

        PageTemplateEntity that = (PageTemplateEntity) o;

        if ( key != that.getKey() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 631, 567 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "key = " ).append( getKey() ).append( ", name = '" ).append( getName() ).append( "'" );
        return s.toString();
    }


    public UserEntity resolveRunAsUser( UserEntity currentUser )
    {
        if ( currentUser.isAnonymous() )
        {
            // Anonymous user cannot run as any other user
            return currentUser;
        }

        RunAsType runAsType = getRunAs();

        if ( runAsType.equals( RunAsType.PERSONALIZED ) )
        {
            return currentUser;
        }
        else if ( runAsType.equals( RunAsType.DEFAULT_USER ) )
        {
            if ( getSite().resolveDefaultRunAsUser() != null )
            {
                return getSite().resolveDefaultRunAsUser();
            }
            return null;
        }
        else if ( runAsType.equals( RunAsType.INHERIT ) )
        {
            return null;
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported runAsType: " + runAsType );
        }
    }
}

package com.enonic.cms.core.structure.portlet;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.CacheSettings;
import com.enonic.cms.core.portal.datasource.Datasources;
import com.enonic.cms.core.portal.datasource.DatasourcesType;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.TemplateParameter;
import com.enonic.cms.core.structure.TemplateParameterType;

public class PortletEntity
    implements Serializable
{
    private int key;

    private String name;

    private Date created;

    private LazyInitializedJDOMDocument xmlData;

    private SiteEntity site;

    private ResourceKey styleKey;

    private ResourceKey borderKey;

    private RunAsType runAs;

    /**
     * For internal caching of the xml data document.
     */
    private transient Document xmlDataAsJDOMDocument;

    private transient Datasources datasources;

    private transient PortletData portletData;

    public int getKey()
    {
        return key;
    }

    public PortletKey getPortletKey()
    {
        return new PortletKey( key );
    }

    public String getName()
    {
        return name;
    }

    public Date getCreated()
    {
        return created;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public ResourceKey getStyleKey()
    {
        return styleKey;
    }

    public ResourceKey getBorderKey()
    {
        return borderKey;
    }

    public RunAsType getRunAs()
    {
        return runAs;
    }

    public void setKey( int key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setCreated( Date created )
    {
        this.created = created;
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

        // Invalidate cache
        xmlDataAsJDOMDocument = null;
        portletData = null;
        datasources = null;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public void setStyleKey( ResourceKey styleKey )
    {
        this.styleKey = styleKey;
    }

    public void setBorderKey( ResourceKey borderKey )
    {
        this.borderKey = borderKey;
    }

    public void setRunAs( RunAsType runAs )
    {
        this.runAs = runAs;
    }

    public Document getXmlDataAsJDOMDocument()
    {
        if ( xmlData == null )
        {
            return null;
        }

        if ( xmlDataAsJDOMDocument == null )
        {
            xmlDataAsJDOMDocument = xmlData.getDocument();
        }

        return (Document) xmlDataAsJDOMDocument.clone();
    }

    /**
     * Returns the cache settigs of this content object.
     */
    public CacheSettings getCacheSettings( int defaultTimeToLive )
    {
        Document doc = getXmlDataAsJDOMDocument();
        Element dataEl = doc.getRootElement();
        String cachedisabledString = dataEl.getAttributeValue( "cachedisabled" );
        String cachetypeString = dataEl.getAttributeValue( "cachetype" );
        String mincachetimeString = dataEl.getAttributeValue( "mincachetime", String.valueOf( defaultTimeToLive ) );
        int secondsToLive = Integer.valueOf( mincachetimeString );
        boolean cacheEnabled = !Boolean.valueOf( cachedisabledString );
        if ( cacheEnabled )
        {
            cacheEnabled = getDatasources().isCacheable();
        }
        return new CacheSettings( cacheEnabled, cachetypeString, secondsToLive );
    }

    public Document getGetDataDocmentChildElementDocumentAsRootElementInItsOwnDocument()
    {
        Element rootEl = getXmlDataAsJDOMDocument().getRootElement();
        Element document = rootEl.getChild( "document" );
        Document documentDoc = new Document();
        documentDoc.addContent( document.detach() );
        return documentDoc;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PortletEntity ) )
        {
            return false;
        }

        PortletEntity that = (PortletEntity) o;

        if ( key != that.getKey() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 61, 437 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "key = " ).append( getKey() ).append( ", name = '" ).append( getName() ).append( "'" );
        return s.toString();
    }


    public Map<String, TemplateParameter> getTemplateParameters()
    {
        Map<String, TemplateParameter> params = new LinkedHashMap<String, TemplateParameter>();

        Element stylesheetparamsEl = getXmlDataAsJDOMDocument().getRootElement().getChild( "stylesheetparams" );
        if ( stylesheetparamsEl != null )
        {
            // get all parameter tags
            @SuppressWarnings({"unchecked"}) List<Element> paramElList = stylesheetparamsEl.getChildren();
            for ( Element paramEl : paramElList )
            {
                TemplateParameterType type = TemplateParameterType.parse( paramEl.getAttributeValue( "type" ) );
                String name = paramEl.getAttributeValue( "name" );
                String value = paramEl.getText();
                if ( value == null )
                {
                    value = "";
                }

                params.put( name, new TemplateParameter( type, name, value ) );

            }
        }

        return params;
    }

    public Map<String, TemplateParameter> getBorderTemplateParameters()
    {
        Map<String, TemplateParameter> params = new LinkedHashMap<String, TemplateParameter>();

        Element stylesheetparamsEl = getXmlDataAsJDOMDocument().getRootElement().getChild( "borderparams" );
        if ( stylesheetparamsEl != null )
        {
            // get all parameter tags
            @SuppressWarnings({"unchecked"}) List<Element> paramElList = stylesheetparamsEl.getChildren();
            for ( Element paramEl : paramElList )
            {
                TemplateParameterType type = TemplateParameterType.parse( paramEl.getAttributeValue( "type" ) );
                String name = paramEl.getAttributeValue( "name" );
                String value = paramEl.getText();
                if ( value != null && value.length() == 0 )
                {
                    value = null;
                }
                params.put( name, new TemplateParameter( type, name, value ) );
            }
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
                datasources = new Datasources( DatasourcesType.PORTLET, datasourcesEl );
            }
        }

        return datasources;
    }

    private PortletData getPortletData()
    {
        if ( portletData == null )
        {
            if ( xmlData != null )
            {
                portletData = new PortletData( this.getXmlDataAsJDOMDocument() );
            }
            else
            {
                portletData = new PortletData();
            }
        }
        return portletData;
    }

    public Boolean getCacheDisabled()
    {
        return getPortletData().getCacheDisabled();
    }

    public String getCacheType()
    {
        return getPortletData().getCacheType();
    }


}

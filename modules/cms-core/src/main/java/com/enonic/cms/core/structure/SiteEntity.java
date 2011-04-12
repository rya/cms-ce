/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;

import com.enonic.cms.domain.CaseInsensitiveString;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.portal.ResourceNotFoundException;
import com.enonic.cms.core.security.user.UserEntity;

public class SiteEntity
    implements Serializable
{
    private SiteKey key;

    private String name;

    private Date timestamp;

    private LazyInitializedJDOMDocument xmlData;

    private MenuItemEntity frontPage;

    private MenuItemEntity loginPage;

    private MenuItemEntity errorPage;

    private PageTemplateEntity pageTemplate;

    private LanguageEntity language;

    private String statisticsUrl;

    private UserEntity defaultRunAsUser;

    private Map<CaseInsensitiveString, MenuItemEntity> topMenuItems;

    private Map<GroupKey, DefaultSiteAccessEntity> defaultAccesses;

    /**
     * For internal caching of the xml data document.
     */
    private transient Document xmlDataAsJDOMDocument;

    private transient SiteData siteData;

    public SiteKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public MenuItemEntity getFrontPage()
    {
        return frontPage;
    }

    public MenuItemEntity getLoginPage()
    {
        return loginPage;
    }

    public MenuItemEntity getErrorPage()
    {
        return errorPage;
    }

    public PageTemplateEntity getPageTemplate()
    {
        return pageTemplate;
    }

    public LanguageEntity getLanguage()
    {
        return language;
    }


    public String getSiteURL()
    {
        return "";
    }

    /*
    public String getBasePath()
    {
        String path = null;
        if ( virtualHost != null && virtualHost.length() > 0 )
        {
            path = virtualHost;
            if ( contextPath != null && contextPath.length() > 0 )
            {
                if ( !virtualHost.endsWith( "/" ) && !contextPath.startsWith( "/" ) )
                {
                    path += "/";
                }
                path += contextPath;
            }
        }
        return path;
    }
    */

    public String getStatisticsUrl()
    {
        return statisticsUrl;
    }

    public UserEntity getDefaultRunAsUser()
    {
        return defaultRunAsUser;
    }

    public UserEntity resolveDefaultRunAsUser()
    {
        final UserEntity defaultRunAsUser = this.defaultRunAsUser;
        if ( defaultRunAsUser != null && defaultRunAsUser.isDeleted() )
        {
            return null;
        }
        return defaultRunAsUser;
    }

    public boolean isDeviceClassificationEnabled()
    {
        return getSiteData().getDeviceClassResolver() != null;
    }

    public ResourceKey getDeviceClassResolver()
    {
        return getSiteData().getDeviceClassResolver();
    }

    public ResourceKey getPathToPublicResources()
    {
        return getSiteData().getPathToPublicResources();
    }

    public ResourceKey getPathToResources()
    {
        return getSiteData().getPathToResources();
    }

    public boolean isLocalizationEnabled()
    {
        return getSiteData().getDefaultLocalizationResource() != null;
    }

    public ResourceKey getDefaultLocalizationResource()
    {
        return getSiteData().getDefaultLocalizationResource();
    }

    public ResourceKey getLocaleResolver()
    {
        return getSiteData().getLocaleResolver();
    }

    public void setKey( int key )
    {
        this.key = new SiteKey( key );
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setFirstPage( MenuItemEntity firstPage )
    {
        this.frontPage = firstPage;
    }

    public void setLoginPage( MenuItemEntity loginPage )
    {
        this.loginPage = loginPage;
    }

    public void setErrorPage( MenuItemEntity errorPage )
    {
        this.errorPage = errorPage;
    }

    public void setPageTemplate( PageTemplateEntity pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }

    public void setLanguage( LanguageEntity language )
    {
        this.language = language;
    }

    public void setStatisticsUrl( String statisticsUrl )
    {
        this.statisticsUrl = statisticsUrl;
    }

    public void setDeviceClassResolver( ResourceKey value )
    {
        this.getSiteData().setDeviceClassResolver( value );
    }

    public void setDefaultLocalizationResource( ResourceKey value )
    {
        this.getSiteData().setDefaultLocalizationResource( value );
    }

    public void setPathToPublicResources( ResourceKey value )
    {
        this.getSiteData().setPathToPublicResources( value );
    }

    public void setLocaleResolver( ResourceKey value )
    {
        this.getSiteData().setLocaleResolver( value );
    }

    public Set<String> getAllowedPageTypes()
    {
        return getSiteData().getAllowedPageTypes();
    }

    public void setDefaultRunAsUser( UserEntity value )
    {
        this.defaultRunAsUser = value;
    }

    public ResourceKey getDefaultCssKey()
    {
        return getSiteData().getDefaultCssKey();
    }

    public void setDefaultCssKey( ResourceKey resourceKey )
    {
        if ( xmlData != null )
        {
            Document doc = xmlData.getDocument();
            Element elem = doc.getRootElement().getChild( "defaultcss" );
            if ( elem != null )
            {
                elem.setAttribute( "key", resourceKey.toString() );
                setXmlData( doc );
            }
        }

        // Invalidate caches
        xmlDataAsJDOMDocument = null;
        siteData = null;
    }

    public Collection<MenuItemEntity> getTopMenuItems()
    {
        return topMenuItems.values();
    }

    public void setTopMenuItems( Map<CaseInsensitiveString, MenuItemEntity> topMenuItems )
    {
        this.topMenuItems = topMenuItems;
    }

    public MenuItemEntity getChild( String name )
    {
        return this.topMenuItems.get( new CaseInsensitiveString( name ) );
    }

    public Document getXmlDataAsClonedJDomDocument()
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

        // Invalidate caches
        xmlDataAsJDOMDocument = null;
        siteData = null;
    }

    public MenuItemEntity resolveMenuItemByPath( Path localPath )
        throws ResourceNotFoundException
    {
        if ( localPath.isRoot() )
        {
            if ( this.getFrontPage() == null )
            {
                return getFirstMenuItem();
            }
            return this.getFrontPage();
        }

        // check first path element in top menu items
        String firstPathElement = localPath.getPathElement( 0 );
        MenuItemEntity menuItem = this.getChild( firstPathElement.toLowerCase() );
        if ( menuItem == null )
        {
            return null;
        }
        else if ( localPath.numberOfElements() == 1 )
        {
            return menuItem;
        }

        //StringBuffer correctPath = new StringBuffer( 32 );
        //correctPath.append( firstPathElement ).append( "/" );

        // check next path elements in children
        for ( int i = 1; i < localPath.numberOfElements(); i++ )
        {
            String pathElement = localPath.getPathElement( i );
            menuItem = menuItem.getChildByName( pathElement );
            if ( menuItem == null )
            {
                break;
            }
            //correctPath.append( pathElement ).append( "/" );
        }

        return menuItem;
    }

    public MenuItemEntity getFirstMenuItem()
    {
        if ( topMenuItems.isEmpty() )
        {
            return null;
        }

        return topMenuItems.values().iterator().next();
    }

    public DefaultSiteAccessEntity getAccess( GroupKey groupKey )
    {
        return defaultAccesses.get( groupKey );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof SiteEntity ) )
        {
            return false;
        }

        SiteEntity that = (SiteEntity) o;

        if ( !getKey().equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 269, 761 ).append( key ).toHashCode();
    }

    private SiteData getSiteData()
    {
        if ( siteData == null )
        {
            Document xmlDataAsDoc = getXmlDataAsClonedJDomDocument();
            if ( xmlDataAsDoc != null )
            {
                siteData = new SiteData( xmlDataAsDoc );
            }
            else
            {
                siteData = new SiteData();
            }
        }
        return siteData;
    }

    public Map<GroupKey, DefaultSiteAccessEntity> getDefaultAccesses()
    {
        return defaultAccesses;
    }
}

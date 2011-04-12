/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;

import com.enonic.esl.util.DigestUtil;

import com.enonic.cms.framework.xml.XMLBuilder;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.domain.CmsDateAndTimeFormats;
import com.enonic.cms.domain.SiteKey;

public class ResourceXmlCreator
{

    private boolean includeFullPath = true;

    /**
     * Zero means all levels are included.
     */
    private int maxLevels = 0;

    private HashMap<ResourceKey, Long> usageCountMap = null;

    private boolean includeData = false;

    private boolean listFolders = true;

    private boolean listResources = true;

    private boolean includeHidden = false;

    private Multimap<ResourceKey, ResourceReferencer> usedByMap = null;

    public void setIncludeFullPath( boolean includeFullPath )
    {
        this.includeFullPath = includeFullPath;
    }

    /**
     * Set number of levels to include. 1 means that only top level is included, and so on. Default is zero, which
     * means that all levels are included.
     *
     * @param maxLevels
     */
    public void setMaxLevels( int maxLevels )
    {
        this.maxLevels = maxLevels;
    }

    public void setUsageCountMap( HashMap<ResourceKey, Long> usageCountMap )
    {
        this.usageCountMap = usageCountMap;
    }

    public void setIncludeData( boolean includeData )
    {
        this.includeData = includeData;
    }

    public void setListFolders( boolean listFolders )
    {
        this.listFolders = listFolders;
    }

    public void setListResources( boolean listResources )
    {
        this.listResources = listResources;
    }

    public void setIncludeHidden( boolean includeHidden )
    {
        this.includeHidden = includeHidden;
    }

    public void setUsedByMap( Multimap<ResourceKey, ResourceReferencer> usedByMap )
    {
        this.usedByMap = usedByMap;
    }

    public XMLDocument createResourceXml( ResourceFile resourceFile )
    {
        XMLBuilder builder = new XMLBuilder();
        addResource( builder, resourceFile );
        return builder.getDocument();
    }

    public XMLDocument createResourceTreeXml( ResourceFolder root )
    {
        XMLBuilder builder = new XMLBuilder( "resources" );
        addRootAttributes( builder, root );
        addSubItems( builder, root, 1 );
        return builder.getDocument();
    }

    private void addSubItems( XMLBuilder builder, ResourceFolder root, int level )
    {
        if ( listFolders )
        {
            for ( ResourceFolder f : root.getFolders() )
            {
                addFolder( builder, f, level );
            }
        }
        if ( listResources )
        {
            for ( ResourceFile f : root.getFiles() )
            {
                addResource( builder, f );
            }
        }
    }

    private void addFolder( XMLBuilder builder, ResourceFolder f, int level )
    {
        if ( f.isHidden() && !includeHidden )
        {
            return;
        }
        builder.startElement( "folder" );
        addFolderAttributes( builder, f );
        if ( level == maxLevels )
        {
            builder.setAttribute( "maxLevelReached", true );
        }
        else
        {
            addSubItems( builder, f, level + 1 );
        }
        builder.endElement();
    }

    private void addResource( XMLBuilder builder, ResourceFile f )
    {
        if ( f.isHidden() && !includeHidden )
        {
            return;
        }
        builder.startElement( "resource" );
        addResourceAttributes( builder, f );
        if ( includeData )
        {
            builder.startElement( "data" );
            builder.addContentAsCDATA( f.getDataAsString() );
            builder.endElement();
        }
        if ( usedByMap != null )
        {
            addUsedBy( builder, new ResourceKey( f.getPath() ) );
        }
        builder.endElement();
    }

    private void addRootAttributes( XMLBuilder builder, ResourceFolder f )
    {
        builder.setAttribute( "root", f.getPath() );
        builder.setAttribute( "maxLevels", maxLevels );
    }

    private void addFolderAttributes( XMLBuilder builder, ResourceFolder f )
    {
        addCommonAttributes( builder, f );

        if ( usageCountMap != null )
        {
            Long count = 0L;
            for ( Map.Entry<ResourceKey, Long> entry : usageCountMap.entrySet() )
            {
                if ( entry.getKey().startsWith( f.getPath() ) )
                {
                    count += entry.getValue();
                }
            }
            builder.setAttribute( "usageCount", count );
        }
    }

    private void addResourceAttributes( XMLBuilder builder, ResourceFile f )
    {
        addCommonAttributes( builder, f );
        builder.setAttribute( "size", f.getSize() );
        builder.setAttribute( "mimeType", f.getMimeType() );

        if ( usageCountMap != null )
        {
            ResourceKey resourceKey = new ResourceKey( f.getPath() );
            Long count = usageCountMap.containsKey( resourceKey ) ? usageCountMap.get( resourceKey ) : 0;
            builder.setAttribute( "usageCount", count );
        }
    }

    private void addCommonAttributes( XMLBuilder builder, ResourceBase f )
    {
        builder.setAttribute( "name", f.getName() );
        builder.setAttribute( "lastModified", CmsDateAndTimeFormats.printAs_XML_TIMESTAMP( f.getLastModified().getTime() ) );
        if ( includeFullPath )
        {
            builder.setAttribute( "hashedFullPath", DigestUtil.generateMD5( f.getPath() ) );
            builder.setAttribute( "fullPath", f.getPath() );
        }
        if ( includeHidden && f.isHidden() )
        {
            builder.setAttribute( "hidden", true );
        }
    }

    private void addUsedBy( XMLBuilder builder, ResourceKey resourceKey )
    {

        SortBySiteBuilderWrapper sortBySiteWrapper = new SortBySiteBuilderWrapper( usedByMap.get( resourceKey ) );

        builder.startElement( "usedBy" );
        addSiteRefUsage( builder, sortBySiteWrapper );
        addContentTypeUsage( builder, sortBySiteWrapper );
        builder.endElement();
    }

    private void addSiteRefUsage( XMLBuilder builder, SortBySiteBuilderWrapper sortBySiteWrapper )
    {
        Set<Map.Entry<SiteKey, String>> sites = sortBySiteWrapper.getSites().entrySet();
        if ( sites.size() == 0 )
        {
            return;
        }

        List<ResourceReferencer> refs = sortBySiteWrapper.getSiteUsage();

        for ( Map.Entry<SiteKey, String> site : sites )
        {
            builder.startElement( "site" );
            builder.setAttribute( "key", site.getKey().toInt() );
            builder.setAttribute( "name", site.getValue() );
            builder.setAttribute( "defaultCss", isSiteRef( refs, site.getKey() ) );
            addContentObjectUsage( builder, sortBySiteWrapper, site.getKey() );
            addPageTemplateUsage( builder, sortBySiteWrapper, site.getKey() );
            builder.endElement();
        }
    }

    private boolean isSiteRef( List<ResourceReferencer> refs, SiteKey siteKey )
    {
        for ( ResourceReferencer ref : refs )
        {
            if ( ref.getSiteKey().equals( siteKey ) )
            {
                return true;
            }
        }
        return false;
    }

    private void addContentObjectUsage( XMLBuilder builder, SortBySiteBuilderWrapper sortBySiteWrapper, SiteKey siteKey )
    {
        List<ResourceReferencer> refs = sortBySiteWrapper.getContentObjectUsage( siteKey );
        if ( refs.size() == 0 )
        {
            return;
        }
        builder.startElement( "contentObjects" );
        for ( ResourceReferencer ref : refs )
        {
            addReferencer( builder, "contentObject", ref );
        }
        builder.endElement();
    }

    private void addPageTemplateUsage( XMLBuilder builder, SortBySiteBuilderWrapper sortBySiteWrapper, SiteKey siteKey )
    {
        List<ResourceReferencer> refs = sortBySiteWrapper.getPageTemplateUsage( siteKey );
        if ( refs.size() == 0 )
        {
            return;
        }
        builder.startElement( "pageTemplates" );
        for ( ResourceReferencer ref : refs )
        {
            addReferencer( builder, "pageTemplate", ref );
        }
        builder.endElement();
    }

    private void addContentTypeUsage( XMLBuilder builder, SortBySiteBuilderWrapper sortBySiteWrapper )
    {
        List<ResourceReferencer> refs = sortBySiteWrapper.getContentTypeUsage();
        if ( refs.size() == 0 )
        {
            return;
        }
        builder.startElement( "contentTypes" );
        for ( ResourceReferencer ref : refs )
        {
            addReferencer( builder, "contentType", ref );
        }
        builder.endElement();
    }

    private void addReferencer( XMLBuilder builder, String elementName, ResourceReferencer ref )
    {
        builder.startElement( elementName );
        builder.setAttribute( "key", ref.getKey() );
        builder.setAttribute( "name", ref.getName() );
        builder.endElement();
    }

    private class SortBySiteBuilderWrapper
    {

        private Collection<ResourceReferencer> usedByCollection = null;

        public SortBySiteBuilderWrapper( Collection<ResourceReferencer> usedByCollection )
        {
            this.usedByCollection = usedByCollection;
        }

        public Map<SiteKey, String> getSites()
        {
            HashMap<SiteKey, String> sites = new HashMap<SiteKey, String>();
            for ( ResourceReferencer ref : usedByCollection )
            {
                if ( ref.getSiteKey() != null && !sites.containsKey( ref.getSiteKey() ) )
                {
                    sites.put( ref.getSiteKey(), ref.getSiteName() );
                }
            }
            return sites;
        }

        public List<ResourceReferencer> getContentObjectUsage( SiteKey siteKey )
        {
            List<ResourceReferencer> objects = getReferencer( siteKey, ResourceReferencerType.CONTENT_OBJECT_STYLE );
            objects.addAll( getReferencer( siteKey, ResourceReferencerType.CONTENT_OBJECT_BORDER ) );
            return objects;
        }

        public List<ResourceReferencer> getPageTemplateUsage( SiteKey siteKey )
        {
            List<ResourceReferencer> templates = getReferencer( siteKey, ResourceReferencerType.PAGE_TEMPLATE_STYLE );
            templates.addAll( getReferencer( siteKey, ResourceReferencerType.PAGE_TEMPLATE_CSS ) );
            return templates;
        }

        public List<ResourceReferencer> getContentTypeUsage()
        {
            return getReferencer( null, ResourceReferencerType.CONTENT_TYPE_CSS );
        }

        public List<ResourceReferencer> getSiteUsage()
        {
            return getReferencer( null, ResourceReferencerType.SITE_DEFAULT_CSS );
        }

        private List<ResourceReferencer> getReferencer( SiteKey siteKey, ResourceReferencerType type )
        {
            List<ResourceReferencer> objects = new ArrayList<ResourceReferencer>();
            for ( ResourceReferencer ref : usedByCollection )
            {
                if ( ref.getType().equals( type ) && ( siteKey == null || ref.getSiteKey().equals( siteKey ) ) )
                {
                    objects.add( ref );
                }
            }
            return objects;
        }
    }
}
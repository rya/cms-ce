/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;


public class ResourceReferencer
{

    private String name;

    private Object realObject;

    private ResourceReferencerType type;

    private Integer key;

    private SiteKey siteKey;

    private String siteName;

    public ResourceReferencer( PortletEntity portlet, ResourceReferencerType type )
    {

        this.name = portlet.getName();
        this.realObject = portlet;
        this.type = type;
        this.key = portlet.getKey();
        this.siteKey = portlet.getSite().getKey();
        this.siteName = portlet.getSite().getName();
    }

    public ResourceReferencer( ContentTypeEntity contentType, ResourceReferencerType type )
    {

        this.name = contentType.getName();
        this.realObject = contentType;
        this.type = type;
        this.key = contentType.getKey();
        this.siteKey = null;
        this.siteName = null;
    }

    public ResourceReferencer( PageTemplateEntity pageTemplate, ResourceReferencerType type )
    {

        this.name = pageTemplate.getName();
        this.realObject = pageTemplate;
        this.type = type;
        this.key = pageTemplate.getKey();
        this.siteKey = pageTemplate.getSite().getKey();
        this.siteName = pageTemplate.getSite().getName();
    }

    public ResourceReferencer( SiteEntity site, ResourceReferencerType type )
    {

        this.name = site.getName();
        this.realObject = site;
        this.type = type;
        this.key = site.getKey().toInt();
        this.siteKey = site.getKey();
        this.siteName = site.getName();
    }

    public String getName()
    {
        return name;
    }

    public Object getRealObject()
    {
        return realObject;
    }

    public ResourceReferencerType getType()
    {
        return type;
    }

    public Integer getKey()
    {
        return key;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public String getSiteName()
    {
        return siteName;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.store.dao.PageTemplateDao;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

/**
 * May 3, 2009
 */
public class PageTemplateResolver
{
    private PageTemplateDao pageTemplateDao;

    public PageTemplateResolver( PageTemplateDao pageTemplateDao )
    {
        this.pageTemplateDao = pageTemplateDao;
    }

    public PageTemplateEntity resolvePageTemplate( SiteEntity site, ContentEntity content )
    {
        // check if content has page template set
        PageTemplateEntity pageTemplate = getForcedPageTemplate( site, content );
        if ( pageTemplate != null )
        {
            return pageTemplate;
        }

        // check whether the site has a framework that supports this content type
        ContentTypeEntity contentType = content.getContentType();

        return pageTemplateDao.findFirstByContentType( site.getKey().toInt(), contentType.getKey(), PageTemplateType.CONTENT );
    }

    private PageTemplateEntity getForcedPageTemplate( SiteEntity site, ContentEntity content )
    {
        ContentHomeEntity contentHome = content.getContentHome( site.getKey() );
        if ( contentHome == null )
        {
            return null;
        }

        return contentHome.getPageTemplate();
    }


}

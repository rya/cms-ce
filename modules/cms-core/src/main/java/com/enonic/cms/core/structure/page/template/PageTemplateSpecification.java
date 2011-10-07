/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

/**
 * Oct 1, 2009
 */
public class PageTemplateSpecification
{
    private PageTemplateType type;

    private PageTemplateKey pageTemplateKey;

    public PageTemplateType getType()
    {
        return type;
    }

    public void setType( PageTemplateType type )
    {
        this.type = type;
    }

    public PageTemplateKey getPageTemplateKey()
    {
        return pageTemplateKey;
    }

    public void setPageTemplateKey( PageTemplateKey pageTemplateKey )
    {
        this.pageTemplateKey = pageTemplateKey;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure.page;

import com.enonic.cms.domain.structure.page.template.PageTemplateSpecification;

/**
 * Oct 1, 2009
 */
public class PageSpecification
{
    private PageTemplateSpecification templateSpecification;

    public PageTemplateSpecification getTemplateSpecification()
    {
        return templateSpecification;
    }

    public void setTemplateSpecification( PageTemplateSpecification templateSpecification )
    {
        this.templateSpecification = templateSpecification;
    }
}

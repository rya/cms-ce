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

    private PageTemplateKey key;

    public PageTemplateType getType()
    {
        return type;
    }

    public void setType( PageTemplateType type )
    {
        this.type = type;
    }

    public PageTemplateKey getKey()
    {
        return key;
    }

    public void setKey( PageTemplateKey pageTemplateKey )
    {
        this.key = pageTemplateKey;
    }

    public boolean satisfies( PageTemplateEntity pageTemplate )
    {
        if ( type != null && !type.equals( pageTemplate.getType() ) )
        {
            return false;
        }

        if ( key != null )
        {
            if ( !key.equals( pageTemplate.getPageTemplateKey() ) )
            {
                return false;
            }
        }

        return true;
    }
}

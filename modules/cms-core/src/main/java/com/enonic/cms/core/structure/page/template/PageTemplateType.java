/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

public enum PageTemplateType
{
    PAGE( 1, "page" ),
    DOCUMENT( 2, "document" ),
    FORM( 3, "form" ),
    NEWSLETTER( 4, "newsletter" ),
    CONTENT( 5, "content" ),
    SECTIONPAGE( 6, "sectionpage" );

    private int key;

    private String name;

    PageTemplateType( int key, String name )
    {
        this.key = key;
        this.name = name;
    }

    public static PageTemplateType get( int key )
    {
        PageTemplateType[] types = PageTemplateType.values();
        for ( PageTemplateType type : types )
        {
            if ( type.key == key )
            {
                return type;
            }
        }
        return null;
    }

    public int getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public boolean equals( PageTemplateType o )
    {
        return ( key == o.key );
    }

    public String toString()
    {
        return getName();
    }
}
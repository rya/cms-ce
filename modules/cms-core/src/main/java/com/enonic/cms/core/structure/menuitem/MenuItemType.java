/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

/**
 *
 */
public enum MenuItemType
{
    /**
     * Is render-able as page. Has page-template of type Default, no content attached.
     */
    PAGE( 1, "page", "A link to a Vertical Page." ),

    /**
     * Is not render-able.
     */
    URL( 2, "url", "A URL (either local or remote)." ),

    /**
     * Is render-able as a page. Has page-template of either type: Content, Document, Form, Section page, Newsletter. Has content attached.
     */
    CONTENT( 4, "content", "A menuitem with a content directly connected" ),

    /**
     * Is not render-able.
     */
    LABEL( 5, "label", "A label." ),

    /**
     * Is not render-able.
     */
    SECTION( 6, "section", "A section." ),

    /**
     * Is not render-able.
     */
    SHORTCUT( 7, "shortcut", "A shortcut to another menu item." );

    private Integer key;

    private String name;

    private String description;

    MenuItemType( int key, String name, String description )
    {
        this.key = key;
        this.name = name;
        this.description = description;
    }

    public Integer getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public static MenuItemType get( Integer key )
    {
        for ( MenuItemType type : values() )
        {
            if ( type.getKey().equals( key ) )
            {
                return type;
            }
        }

        return null;
    }
}

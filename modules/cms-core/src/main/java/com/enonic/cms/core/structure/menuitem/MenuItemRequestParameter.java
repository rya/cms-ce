/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

/**
 * Apr 26, 2009
 */
public class MenuItemRequestParameter
{
    private String name;

    private String value;

    private String override;

    public MenuItemRequestParameter( String name, String value, String override )
    {
        this.name = name;
        this.value = value;
        this.override = override;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public boolean isOverridableByRequest()
    {
        return "url".equals( override ) || "true".equals( override );
    }

    public boolean isEmpty()
    {
        return value == null || value.length() == 0;
    }
}

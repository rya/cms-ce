/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.stylesheet;

public class CmsStylesheetParameter
{

    private String name;

    private String type;

    private Object value;

    public CmsStylesheetParameter( String name, String type )
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public boolean isObjectOrPage()
    {
        return "object".equals( type ) || "page".equals( type );
    }

    public void setValue( Object value )
    {
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    public String getValueAsString()
    {

        if ( value == null )
        {
            return null;
        }

        return value.toString();
    }
}

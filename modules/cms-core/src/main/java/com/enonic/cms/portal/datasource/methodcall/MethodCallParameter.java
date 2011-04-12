/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.methodcall;

import com.enonic.cms.portal.datasource.DataSourceContext;

/**
 * Jul 31, 2009
 */
public class MethodCallParameter
{
    private String name;

    private Object argument;

    private String override;

    private Class type;

    public MethodCallParameter( String name, Object argument, String override, Class type )
    {
        this.name = name;
        this.argument = argument;
        this.override = override;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public String getOverride()
    {
        return override;
    }

    public Class getType()
    {
        return type;
    }

    public Object getArgument()
    {
        return argument;
    }

    public boolean isOverride()
    {
        return ( this.override != null ) && ( this.override.length() > 0 ) && !"false".equals( this.override );
    }

    public boolean isContext()
    {
        return this.argument instanceof DataSourceContext;
    }

}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

/**
 * Apr 26, 2009
 */
public class TemplateParameter
{
    private TemplateParameterType type;

    private String name;

    private String value;

    public TemplateParameter( TemplateParameterType type, String name, String value )
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public TemplateParameterType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }
}

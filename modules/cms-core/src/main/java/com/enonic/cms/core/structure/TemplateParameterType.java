/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

/**
 * May 20, 2009
 */
public enum TemplateParameterType
{
    CATEGORY( "category" ),
    CONTENT( "content" ),
    PAGE( "page" ),
    OBJECT( "object", "region" );

    private String name;

    private String alias;

    public static TemplateParameterType parse( String name )
    {
        if ( name == null )
        {
            return null;
        }

        for ( TemplateParameterType type : TemplateParameterType.values() )
        {
            if ( name.equals( type.getName() ) || name.equals( type.getAlias() ) )
            {
                return type;
            }
        }

        return null;
    }

    TemplateParameterType( String name )
    {
        this.name = name;
    }

    TemplateParameterType( String name, String alias )
    {
        this.name = name;
        this.alias = alias;
    }


    public String getName()
    {
        return name;
    }

    public String getAlias()
    {
        return alias;
    }

}

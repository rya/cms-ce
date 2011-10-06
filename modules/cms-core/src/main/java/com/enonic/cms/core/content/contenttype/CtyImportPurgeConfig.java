/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

public enum CtyImportPurgeConfig
{
    ARCHIVE,
    DELETE,
    NONE;

    public static CtyImportPurgeConfig parse( final String value, String importConfigName )
    {
        if ( value == null )
        {
            return CtyImportPurgeConfig.NONE;
        }
        else if ( value.equals( "archive" ) )
        {
            return CtyImportPurgeConfig.ARCHIVE;
        }
        else if ( value.equals( "delete" ) )
        {
            return CtyImportPurgeConfig.DELETE;
        }
        else
        {
            throw new InvalidImportConfigException( importConfigName, "Invalid \"purge\" attribute value: \"" + value +
                "\". Only \"archive\" and \"delete\" are supported." );
        }
    }
}

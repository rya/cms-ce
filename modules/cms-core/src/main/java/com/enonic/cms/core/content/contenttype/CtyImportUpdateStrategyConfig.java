/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

/**
 * Default is UPDATE_CONTENT_KEEP_STATUS.
 */
public enum CtyImportUpdateStrategyConfig
{
    UPDATE_CONTENT_KEEP_STATUS,
    UPDATE_AND_APPROVE_CONTENT,
    UPDATE_AND_ARCHIVE_CONTENT,
    UPDATE_CONTENT_DRAFT;

    public static CtyImportUpdateStrategyConfig parse( final String importName, final String value )
    {
        if ( value.equalsIgnoreCase( "UPDATE-CONTENT-KEEP-STATUS" ) )
        {
            return UPDATE_CONTENT_KEEP_STATUS;
        }
        else if ( value.equalsIgnoreCase( "UPDATE-AND-ARCHIVE-CONTENT" ) )
        {
            return UPDATE_AND_ARCHIVE_CONTENT;
        }
        else if ( value.equalsIgnoreCase( "UPDATE-AND-APPROVE-CONTENT" ) )
        {
            return UPDATE_AND_APPROVE_CONTENT;
        }
        else if ( value.equalsIgnoreCase( "UPDATE-CONTENT-DRAFT" ) )
        {
            return UPDATE_CONTENT_DRAFT;
        }
        else
        {
            throw new InvalidImportConfigException( importName, "Invalid 'update-strategy' attribute value: " + value );
        }
    }
}

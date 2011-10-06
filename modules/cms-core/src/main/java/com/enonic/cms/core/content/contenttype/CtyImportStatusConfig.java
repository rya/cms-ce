/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import com.enonic.cms.core.content.ContentStatus;


public enum CtyImportStatusConfig
{
    DRAFT,
    APPROVED;

    public ContentStatus toContentStatus()
    {
        if ( this.equals( DRAFT ) )
        {
            return ContentStatus.DRAFT;
        }
        else
        {
            return ContentStatus.APPROVED;
        }
    }

    public static CtyImportStatusConfig parse( final String importName, final String s )
    {
        if ( s == null )
        {
            return DRAFT;
        }

        switch ( Integer.valueOf( s ) )
        {
            case 0:
                return DRAFT;

            case 2:
                return APPROVED;

            default:
                throw new InvalidImportConfigException( importName, "Invalid \"status\" attribute value: " + s );
        }
    }

}



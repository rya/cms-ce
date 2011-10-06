/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

public enum CtyImportModeConfig
{
    XML,
    CSV;

    public static CtyImportModeConfig parse( final String m )
    {
        if ( m == null || m.equals( "xml" ) )
        {
            return CtyImportModeConfig.XML;
        }
        else if ( m.equals( "csv" ) )
        {
            return CtyImportModeConfig.CSV;
        }
        return null;
    }
}

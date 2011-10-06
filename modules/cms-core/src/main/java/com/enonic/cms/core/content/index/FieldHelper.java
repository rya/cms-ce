/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

/**
 * This class implements helper functions for fields.
 */
public final class FieldHelper
{
    /**
     * Translate the field name by stripping @, data, contentdata. All / (slash) are replaced by . (dot).
     */
    public static String translateFieldName( String fieldName )
    {
        fieldName = fieldName.trim().toLowerCase();
        fieldName = fieldName.replace( '.', '/' );

        if ( fieldName.startsWith( "/" ) )
        {
            fieldName = fieldName.substring( 1 );
        }

        if ( fieldName.endsWith( "/" ) )
        {
            fieldName = fieldName.substring( 0, fieldName.length() - 1 );
        }

        if ( fieldName.startsWith( "contentdata/" ) )
        {
            fieldName = "data/" + fieldName.substring( "contentdata/".length() );
        }

        fieldName = fieldName.replaceAll( "@", "" );
        fieldName = fieldName.replaceAll( "/", "#" );
        fieldName = fieldName.replaceAll( "\\*", "%" );
        return fieldName;
    }

    /**
     * Return true if user defined field.
     */
    public static boolean isUserDefinedField( String fieldName )
    {
        return translateFieldName( fieldName ).startsWith( "data#" );
    }
}

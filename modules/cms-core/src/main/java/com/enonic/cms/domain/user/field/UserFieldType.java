/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.user.field;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.enonic.cms.domain.user.Address;
import com.enonic.cms.domain.user.Gender;

public enum UserFieldType
{
    FIRST_NAME( String.class ),
    LAST_NAME( String.class ),
    MIDDLE_NAME( String.class ),
    NICK_NAME( String.class ),
    BIRTHDAY( Date.class ),
    COUNTRY( String.class ),
    DESCRIPTION( String.class ),
    INITIALS( String.class ),
    GLOBAL_POSITION( String.class ),
    HTML_EMAIL( Boolean.class ),
    LOCALE( Locale.class ),
    PERSONAL_ID( String.class ),
    MEMBER_ID( String.class ),
    ORGANIZATION( String.class ),
    PHONE( String.class ),
    FAX( String.class ),
    MOBILE( String.class ),
    PREFIX( String.class ),
    SUFFIX( String.class ),
    TITLE( String.class ),
    TIME_ZONE( TimeZone.class ),
    HOME_PAGE( String.class ),
    ADDRESS( Address.class ),
    PHOTO( byte[].class ),
    GENDER( Gender.class );

    private final String name;

    private final Class<?> typeClass;

    UserFieldType( Class<?> typeClass )
    {
        this.name = name().toLowerCase().replace( "_", "-" );
        this.typeClass = typeClass;
    }

    public String getName()
    {
        return this.name;
    }

    public Class<?> getTypeClass()
    {
        return this.typeClass;
    }

    public boolean isOfType( Class<?> type )
    {
        return this.typeClass.isAssignableFrom( type );
    }

    public static UserFieldType fromName( final String name )
    {
        for ( UserFieldType value : values() )
        {
            if ( value.getName().equals( name ) )
            {
                return value;
            }
        }

        return null;
    }
}

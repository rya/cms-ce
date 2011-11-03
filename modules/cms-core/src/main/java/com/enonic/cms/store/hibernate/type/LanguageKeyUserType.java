/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.language.LanguageKey;

/**
 * Created by rmy - Date: Oct 5, 2009
 */
public class LanguageKeyUserType
    extends AbstractIntegerBasedUserType<LanguageKey>
{
    public LanguageKeyUserType()
    {
        super( LanguageKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public LanguageKey get( int value )
    {
        return new LanguageKey( value );
    }

    public Integer getIntegerValue( LanguageKey value )
    {
        return value.toInt();
    }
}
/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.CaseInsensitiveString;

public class CaseInsensitiveStringUserType
    extends AbstractStringBasedUserType<CaseInsensitiveString>
{
    public CaseInsensitiveStringUserType()
    {
        super( CaseInsensitiveString.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public CaseInsensitiveString get( final String value )
    {
        return new CaseInsensitiveString( value );
    }

    public String getStringValue( CaseInsensitiveString value )
    {
        return value.toString();
    }
}

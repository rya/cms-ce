/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework;

import org.springframework.core.style.DefaultToStringStyler;
import org.springframework.core.style.DefaultValueStyler;

/**
 * Jul 15, 2009
 */
public class CmsToStringStyler
    extends DefaultToStringStyler
{
    public static CmsToStringStyler DEFAULT = new CmsToStringStyler();

    public CmsToStringStyler()
    {
        super( new DefaultValueStyler() );
    }

    @Override
    public void styleStart( StringBuilder stringBuffer, Object o )
    {
        stringBuffer.append( "[" );
    }

    @Override
    public void styleEnd( StringBuilder stringBuffer, Object o )
    {
        stringBuffer.append( " ]" );
    }
}

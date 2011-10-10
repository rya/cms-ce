/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.structure.page.template.PageTemplateKey;

/**
 *
 */
public class PageTemplateKeyUserType
    extends AbstractIntegerBasedUserType<PageTemplateKey>
{
    protected PageTemplateKeyUserType()
    {
        super( PageTemplateKey.class );
    }

    public PageTemplateKey get( int value )
    {
        return new PageTemplateKey( value );
    }

    public Integer getIntegerValue( PageTemplateKey value )
    {
        return value.toInt();
    }

    public boolean isMutable()
    {
        return false;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import com.enonic.cms.framework.xml.XMLDocument;


public final class DataSourceResult
{
    private XMLDocument data;

    public XMLDocument getData()
    {
        return data;
    }

    public void setData( XMLDocument document )
    {
        data = document;
    }
}

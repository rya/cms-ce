/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.processor;

import org.w3c.dom.Document;

import com.enonic.cms.portal.datasource.methodcall.MethodCall;

public class NonDoingDataSourceProcessor
    implements DataSourceProcessor
{
    public void postProcess( Document resultDoc, MethodCall methodCall )
    {
    }

    public void preProcess( MethodCall methodCall )
    {
    }
}

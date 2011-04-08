/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.processor;

import org.w3c.dom.Document;

import com.enonic.cms.portal.datasource.methodcall.MethodCall;

/**
 * This interface defines the data source processor. It pre and post processes the data source result.
 */
public interface DataSourceProcessor
{
    public void preProcess( MethodCall methodCall );

    public void postProcess( Document resultDoc, MethodCall methodCall );
}

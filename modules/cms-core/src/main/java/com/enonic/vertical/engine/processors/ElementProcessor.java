/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import java.io.Serializable;

import org.w3c.dom.Element;

public interface ElementProcessor
    extends Serializable
{
    /**
     * Method for processing an element
     */
    public void process( Element elem );
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports.sourcevalueholders;

public class BinarySourceValue
    extends AbstractSourceValue
{
    final private byte[] value;

    public BinarySourceValue( byte[] value )
    {
        this.value = value;
    }

    public byte[] getValue()
    {
        return this.value;
    }
}

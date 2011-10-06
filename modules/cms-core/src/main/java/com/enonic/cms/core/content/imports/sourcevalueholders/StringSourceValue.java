/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports.sourcevalueholders;

public class StringSourceValue
    extends AbstractSourceValue
{
    final private String value;

    public StringSourceValue( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}

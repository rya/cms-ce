/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports.sourcevalueholders;

public abstract class AbstractSourceValue
{
    String additionalValue = null;

    public void setAdditionalValue( String additionalValue )
    {
        this.additionalValue = additionalValue;
    }

    public boolean hasAdditionalValue()
    {
        return this.additionalValue != null;
    }

    public String getAdditionalValue()
    {
        return this.additionalValue;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports.sourcevalueholders;

import java.util.HashSet;
import java.util.Set;

public class StringArraySourceValue
    extends AbstractSourceValue
{
    final private Set<String> values;

    public StringArraySourceValue()
    {
        this.values = new HashSet<String>();
    }

    public StringArraySourceValue( String[] values )
    {
        this.values = new HashSet<String>();
        for ( String value : values )
        {
            this.values.add( value );
        }
    }

    public StringArraySourceValue( Set<String> values )
    {
        this.values = values;
    }

    public void addValue( String value )
    {
        this.values.add( value );
    }

    public Set<String> getValues()
    {
        return this.values;
    }
}

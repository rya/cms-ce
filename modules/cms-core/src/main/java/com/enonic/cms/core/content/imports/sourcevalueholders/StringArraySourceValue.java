package com.enonic.cms.core.content.imports.sourcevalueholders;

import java.util.LinkedHashSet;
import java.util.Set;

public class StringArraySourceValue
        extends AbstractSourceValue
{
    final private LinkedHashSet<String> values;

    public StringArraySourceValue()
    {
        this.values = new LinkedHashSet<String>();
    }

    public StringArraySourceValue( String[] values )
    {
        this.values = new LinkedHashSet<String>();
        for ( String value : values )
        {
            this.values.add( value );
        }
    }

    public StringArraySourceValue( LinkedHashSet<String> values )
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

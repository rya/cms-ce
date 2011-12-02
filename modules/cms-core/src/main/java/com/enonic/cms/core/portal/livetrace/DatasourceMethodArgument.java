package com.enonic.cms.core.portal.livetrace;

public class DatasourceMethodArgument
{
    private MaxLengthedString name;

    private MaxLengthedString value;

    private String override;

    public DatasourceMethodArgument( String name, String value, String override )
    {
        this.name = new MaxLengthedString( name );
        this.value = new MaxLengthedString( value );
        this.override = override;
    }

    public String getName()
    {
        return name != null ? name.toString() : null;
    }

    public String getValue()
    {
        return value != null ? value.toString() : null;
    }

    public String getOverride()
    {
        return override;
    }
}

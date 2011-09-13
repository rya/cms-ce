package com.enonic.cms.business.portal.livetrace;


public class DatasourceMethodArgument
{
    private String name;

    private String value;

    private String override;

    public DatasourceMethodArgument( String name, String value, String override )
    {
        this.name = name;
        this.value = value;
        this.override = override;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String getOverride()
    {
        return override;
    }
}

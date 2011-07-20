package com.enonic.cms.admin.user;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 7/18/11
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddressModel
{
    private String label;

    private String street;

    private String postalAddress;

    private String postalCode;

    private String region;

    private String country;

    private String isoRegion;

    private String isoCountry;

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet( String street )
    {
        this.street = street;
    }

    @JsonProperty("postal_address")
    public String getPostalAddress()
    {
        return postalAddress;
    }

    public void setPostalAddress( String postalAddress )
    {
        this.postalAddress = postalAddress;
    }

    @JsonProperty("postal_code")
    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode( String postalCode )
    {
        this.postalCode = postalCode;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion( String region )
    {
        this.region = region;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    @JsonProperty("iso_region")
    public String getIsoRegion()
    {
        return isoRegion;
    }

    public void setIsoRegion( String isoRegion )
    {
        this.isoRegion = isoRegion;
    }

    @JsonProperty("iso_country")
    public String getIsoCountry()
    {
        return isoCountry;
    }

    public void setIsoCountry( String isoCountry )
    {
        this.isoCountry = isoCountry;
    }
}

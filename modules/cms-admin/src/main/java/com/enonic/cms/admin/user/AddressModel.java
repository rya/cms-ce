package com.enonic.cms.admin.user;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class AddressModel
{
    public static final String LABEL = "label";
    public static final String STREET = "street";
    public static final String POSTAL_ADDRESS = "postal-address";
    public static final String POSTAL_CODE = "postal-code";
    public static final String REGION = "region";
    public static final String COUNTRY = "country";
    public static final String ISO_REGION = "iso-region";
    public static final String ISO_COUNTRY = "iso-country";

    private String label;

    private String street;

    private String postalAddress;

    private String postalCode;

    private String region;

    private String country;

    private String isoRegion;

    private String isoCountry;

    public AddressModel(){}

    public AddressModel(Map<String, Object> addressData ){
        this.label = addressData.get(LABEL) != null ? addressData.get(LABEL).toString() : null;
        this.street = addressData.get(STREET) != null ? addressData.get(STREET).toString() : null;
        this.postalAddress = addressData.get(POSTAL_ADDRESS) != null ? addressData.get(POSTAL_ADDRESS).toString() : null;
        this.postalCode = addressData.get(POSTAL_CODE) != null ? addressData.get(POSTAL_CODE).toString() : null;
        this.region = addressData.get(REGION) != null ? addressData.get(REGION).toString() : null;
        this.country = addressData.get(COUNTRY) != null ? addressData.get(COUNTRY).toString() : null;
        this.isoCountry = addressData.get(ISO_COUNTRY) != null ? addressData.get(ISO_COUNTRY).toString() : null;
        this.isoRegion = addressData.get(ISO_REGION) != null ? addressData.get(ISO_REGION).toString() : null;
    }

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

    @JsonProperty(POSTAL_ADDRESS)
    public String getPostalAddress()
    {
        return postalAddress;
    }

    public void setPostalAddress( String postalAddress )
    {
        this.postalAddress = postalAddress;
    }

    @JsonProperty(POSTAL_CODE)
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

    @JsonProperty(ISO_REGION)
    public String getIsoRegion()
    {
        return isoRegion;
    }

    public void setIsoRegion( String isoRegion )
    {
        this.isoRegion = isoRegion;
    }

    @JsonProperty(ISO_COUNTRY)
    public String getIsoCountry()
    {
        return isoCountry;
    }

    public void setIsoCountry( String isoCountry )
    {
        this.isoCountry = isoCountry;
    }
}

package com.enonic.cms.admin.country;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 7/11/11
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class CallingCodeModel
{
    private String countryCode;

    private String localName;

    private String englishName;

    private String callingCode;

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode( String countryCode )
    {
        this.countryCode = countryCode;
    }

    public String getLocalName()
    {
        return localName;
    }

    public void setLocalName( String localName )
    {
        this.localName = localName;
    }

    public String getEnglishName()
    {
        return englishName;
    }

    public void setEnglishName( String englishName )
    {
        this.englishName = englishName;
    }

    public String getCallingCode()
    {
        return callingCode;
    }

    public void setCallingCode( String callingCode )
    {
        this.callingCode = callingCode;
    }
}

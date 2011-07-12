package com.enonic.cms.admin.country;

import com.enonic.cms.core.country.Country;

/**
 * Class for converting Country to CallingCodeModel
 *
 * @author Viktar Fiodarau
 * @see Country
 * @see CallingCodeModel
 */
public class CallingCodeModelHelper
{

    static public CallingCodeModel toModel( Country country )
    {
        CallingCodeModel callingCode = new CallingCodeModel();
        callingCode.setCountryCode( country.getCode().toString() );
        callingCode.setCallingCode( country.getCallingCode() );
        callingCode.setEnglishName( country.getEnglishName() );
        callingCode.setLocalName( country.getLocalName() );

        return callingCode;
    }
}

package com.enonic.cms.admin.country;

import com.enonic.cms.core.country.Country;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 7/11/11
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CallingCodeModelHelper
{

    static public CallingCodeModel toModel(Country country){
        CallingCodeModel callingCode = new CallingCodeModel();
        callingCode.setCountryCode( country.getCode().toString() );
        callingCode.setCallingCode( country.getCallingCode() );
        callingCode.setEnglishName( country.getEnglishName() );
        callingCode.setLocalName( country.getLocalName() );

        return callingCode;
    }
}

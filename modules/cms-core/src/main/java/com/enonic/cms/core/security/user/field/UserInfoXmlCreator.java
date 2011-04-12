/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user.field;

import java.util.Collection;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.domain.user.Address;
import com.enonic.cms.domain.user.UserInfo;
import com.enonic.cms.domain.user.field.UserField;
import com.enonic.cms.domain.user.field.UserFieldHelper;
import com.enonic.cms.domain.user.field.UserFieldMap;
import com.enonic.cms.domain.user.field.UserFieldType;
import com.enonic.cms.domain.user.field.UserInfoTransformer;

public final class UserInfoXmlCreator
{
    private final static String DEFAULT_ROOT_ELEMENT_NAME = "block";

    private final UserFieldHelper helper;

    public UserInfoXmlCreator()
    {
        this.helper = new UserFieldHelper( "yyyy-MM-dd" );
    }

    public Document createUserInfoDocument( final User user )
    {
        return new Document( createUserInfoElement( user ) );
    }

    public Element createUserInfoElement( final User user )
    {
        final Element rootEl = new Element( DEFAULT_ROOT_ELEMENT_NAME );
        return addUserInfoToElement( rootEl, user.getUserInfo(), true );
    }

    public Element addUserInfoToElement( final Element rootEl, UserInfo userInfo, final boolean replaceExisting )
    {
        final UserInfoTransformer transformer = new UserInfoTransformer();
        final UserFieldMap userFieldMap = transformer.toUserFields( userInfo );

        for ( final UserField userField : userFieldMap )
        {
            if ( userField.getType() != UserFieldType.ADDRESS )
            {
                final Element userFieldValueEl = doCreateUserInfoElement( userField );

                final Element existingUserFieldValueEl = rootEl.getChild( userFieldValueEl.getName() );
                if ( !UserFieldValueElementComparer.equals( userFieldValueEl, existingUserFieldValueEl ) )
                {
                    // adding new element
                    rootEl.addContent( userFieldValueEl );
                }
                else if ( replaceExisting )
                {
                    // replace existing element
                    rootEl.removeChild( userFieldValueEl.getName() );
                    rootEl.addContent( userFieldValueEl );
                }
                else
                {
                    // do nothing - leave existing element
                }
            }
        }
        final Collection<UserField> addresses = userFieldMap.getFields( UserFieldType.ADDRESS );
        if ( addresses.size() > 0 )
        {
            rootEl.addContent( doCreateAddressesElement( addresses ) );
        }

        return rootEl;
    }

    private Element doCreateUserInfoElement( final UserField userField )
    {
        if ( userField.getType() == UserFieldType.ADDRESS )
        {
            throw new IllegalArgumentException( "UserField of type '" + UserFieldType.ADDRESS + "' found." );
        }

        if ( userField.getType() == UserFieldType.PHOTO )
        {
            final Element elem = new Element( userField.getType().getName() );
            elem.setAttribute( "exists", "true" );
            return elem;
        }
        else
        {
            final Element fieldValueEl = new Element( userField.getType().getName() );
            fieldValueEl.setText( this.helper.toString( userField ) );
            return fieldValueEl;
        }
    }

    private Element doCreateAddressesElement( final Collection<UserField> addresses )
    {
        final Element elem = new Element( "addresses" );

        for ( final UserField address : addresses )
        {
            if ( address.getType() != UserFieldType.ADDRESS )
            {
                throw new IllegalArgumentException(
                    "UserField of type '" + address.getType() + "' found. Type: '" + UserFieldType.ADDRESS + "' expected." );
            }
            elem.addContent( doCreateAddressElement( (Address) address.getValue() ) );
        }
        return elem;
    }


    private Element doCreateAddressElement( final Address address )
    {
        final Element addressEl = new Element( "address" );

        final Element countryEl = new Element( "country" );
        countryEl.setText( address.getCountry() );
        addressEl.addContent( countryEl );

        final Element isoCountryEl = new Element( "iso-country" );
        isoCountryEl.setText( address.getIsoCountry() );
        addressEl.addContent( isoCountryEl );

        final Element isoRegionEl = new Element( "iso-region" );
        isoRegionEl.setText( address.getIsoRegion() );
        addressEl.addContent( isoRegionEl );

        final Element labelEl = new Element( "label" );
        labelEl.setText( address.getLabel() );
        addressEl.addContent( labelEl );

        final Element postalCodeEl = new Element( "postal-code" );
        postalCodeEl.setText( address.getPostalCode() );
        addressEl.addContent( postalCodeEl );

        final Element postalAddressEl = new Element( "postal-address" );
        postalAddressEl.setText( address.getPostalAddress() );
        addressEl.addContent( postalAddressEl );

        final Element regionEl = new Element( "region" );
        regionEl.setText( address.getRegion() );
        addressEl.addContent( regionEl );

        final Element streetEl = new Element( "street" );
        streetEl.setText( address.getStreet() );
        addressEl.addContent( streetEl );

        return addressEl;
    }

    private static class UserFieldValueElementComparer
    {
        private static boolean equals( final Element leftEl, final Element rightEl )
        {
            if ( leftEl == null && rightEl == null )
            {
                return true;
            }
            if ( leftEl == null && rightEl != null )
            {
                return false;
            }
            if ( leftEl != null && rightEl == null )
            {
                return false;
            }
            if ( !leftEl.getName().equals( rightEl.getName() ) )
            {
                return false;
            }
            final Attribute leftNameAtr = leftEl.getAttribute( "name" );
            final Attribute rightNameAtr = rightEl.getAttribute( "name" );
            if ( leftNameAtr == null && rightNameAtr == null )
            {
                return true;
            }
            if ( leftNameAtr == null && rightNameAtr != null )
            {
                return false;
            }
            if ( leftNameAtr != null && rightNameAtr == null )
            {
                return false;
            }
            if ( !leftNameAtr.getValue().equals( rightNameAtr.getValue() ) )
            {
                return false;
            }

            return true;
        }
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.api.client.model.user.UserInfo;

public final class UserInfoTransformer
{
    public UserInfo toUserInfo( UserFieldMap fields )
    {
        UserInfo info = new UserInfo();
        updateUserInfo( info, fields );
        return info;
    }

    public void updateUserInfo( UserInfo info, UserFieldMap fields )
    {
        for ( UserField field : fields )
        {
            if ( !field.isOfType( UserFieldType.ADDRESS ) )
            {
                updateUserInfo( info, field );
            }
        }

        updateAddresses( info, fields );
    }

    private void updateUserInfo( UserInfo info, UserField field )
    {
        switch ( field.getType() )
        {
            case FIRST_NAME:
                info.setFirstName( (String) field.getValue() );
                break;
            case BIRTHDAY:
                info.setBirthday( (Date) field.getValue() );
                break;
            case COUNTRY:
                info.setCountry( (String) field.getValue() );
                break;
            case DESCRIPTION:
                info.setDescription( (String) field.getValue() );
                break;
            case FAX:
                info.setFax( (String) field.getValue() );
                break;
            case GENDER:
                info.setGender( (Gender) field.getValue() );
                break;
            case GLOBAL_POSITION:
                info.setGlobalPosition( (String) field.getValue() );
                break;
            case HOME_PAGE:
                info.setHomePage( (String) field.getValue() );
                break;
            case HTML_EMAIL:
                info.setHtmlEmail( (Boolean) field.getValue() );
                break;
            case INITIALS:
                info.setInitials( (String) field.getValue() );
                break;
            case LAST_NAME:
                info.setLastName( (String) field.getValue() );
                break;
            case LOCALE:
                info.setLocale( (Locale) field.getValue() );
                break;
            case MEMBER_ID:
                info.setMemberId( (String) field.getValue() );
                break;
            case MIDDLE_NAME:
                info.setMiddleName( (String) field.getValue() );
                break;
            case MOBILE:
                info.setMobile( (String) field.getValue() );
                break;
            case NICK_NAME:
                info.setNickName( (String) field.getValue() );
                break;
            case ORGANIZATION:
                info.setOrganization( (String) field.getValue() );
                break;
            case PERSONAL_ID:
                info.setPersonalId( (String) field.getValue() );
                break;
            case PHONE:
                info.setPhone( (String) field.getValue() );
                break;
            case PHOTO:
                info.setPhoto( (byte[]) field.getValue() );
                break;
            case PREFIX:
                info.setPrefix( (String) field.getValue() );
                break;
            case SUFFIX:
                info.setSuffix( (String) field.getValue() );
                break;
            case TIME_ZONE:
                info.setTimezone( (TimeZone) field.getValue() );
                break;
            case TITLE:
                info.setTitle( (String) field.getValue() );
                break;
        }
    }

    private void updateAddresses( UserInfo info, UserFieldMap fields )
    {
        Address[] existing = info.getAddresses();
        Address[] addresses = toAddresses( fields );

        if ( addresses.length == 0 )
        {
            return;
        }

        if ( existing.length == 0 )
        {
            // Overwrite all
            info.setAddresses( addresses );
        }
        else
        {
            // Overwrite only primary
            existing[0] = addresses[0];
            info.setAddresses( existing );
        }
    }

    private Address[] toAddresses( UserFieldMap fields )
    {
        LinkedList<Address> list = new LinkedList<Address>();
        for ( UserField field : fields.getFields( UserFieldType.ADDRESS ) )
        {
            list.add( (Address) field.getValue() );
        }

        return list.toArray( new Address[list.size()] );
    }

    public UserFieldMap toUserFields( UserInfo info )
    {
        UserFieldMap fields = new UserFieldMap( true );

        for ( Address address : info.getAddresses() )
        {
            addIfNotNull( fields, UserFieldType.ADDRESS, address );
        }

        addIfNotNull( fields, UserFieldType.BIRTHDAY, info.getBirthday() );
        addIfNotNull( fields, UserFieldType.COUNTRY, info.getCountry() );
        addIfNotNull( fields, UserFieldType.DESCRIPTION, info.getDescription() );
        addIfNotNull( fields, UserFieldType.FAX, info.getFax() );
        addIfNotNull( fields, UserFieldType.FIRST_NAME, info.getFirstName() );
        addIfNotNull( fields, UserFieldType.GENDER, info.getGender() );
        addIfNotNull( fields, UserFieldType.GLOBAL_POSITION, info.getGlobalPosition() );
        addIfNotNull( fields, UserFieldType.HOME_PAGE, info.getHomePage() );
        addIfNotNull( fields, UserFieldType.HTML_EMAIL, info.getHtmlEmail() );
        addIfNotNull( fields, UserFieldType.INITIALS, info.getInitials() );
        addIfNotNull( fields, UserFieldType.LAST_NAME, info.getLastName() );
        addIfNotNull( fields, UserFieldType.LOCALE, info.getLocale() );
        addIfNotNull( fields, UserFieldType.MEMBER_ID, info.getMemberId() );
        addIfNotNull( fields, UserFieldType.MIDDLE_NAME, info.getMiddleName() );
        addIfNotNull( fields, UserFieldType.MOBILE, info.getMobile() );
        addIfNotNull( fields, UserFieldType.NICK_NAME, info.getNickName() );
        addIfNotNull( fields, UserFieldType.ORGANIZATION, info.getOrganization() );
        addIfNotNull( fields, UserFieldType.PERSONAL_ID, info.getPersonalId() );
        addIfNotNull( fields, UserFieldType.PHONE, info.getPhone() );
        addIfNotNull( fields, UserFieldType.PHOTO, info.getPhoto() );
        addIfNotNull( fields, UserFieldType.PREFIX, info.getPrefix() );
        addIfNotNull( fields, UserFieldType.SUFFIX, info.getSuffix() );
        addIfNotNull( fields, UserFieldType.TIME_ZONE, info.getTimeZone() );
        addIfNotNull( fields, UserFieldType.TITLE, info.getTitle() );

        return fields;
    }

    private void addIfNotNull( UserFieldMap fields, UserFieldType type, Object value )
    {
        if ( value != null )
        {
            fields.add( new UserField( type, value ) );
        }
    }
}

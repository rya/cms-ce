package com.enonic.cms.admin.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.domain.user.Address;
import com.enonic.cms.domain.user.Gender;
import com.enonic.cms.domain.user.UserInfo;

@Component
public final class UserModelTranslator
{

    @Autowired
    private UserDao userDao;

    public UserModel toModel( final UserEntity entity )
    {
        final UserModel model = new UserModel();
        model.setKey( entity.getKey().toString() );
        model.setName( entity.getName() );
        model.setEmail( entity.getEmail() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setLastModified( entity.getLastModified() );

        if ( entity.getUserStore() != null )
        {
            model.setUserStore( entity.getUserStore().getName() );
        }
        else
        {
            model.setUserStore( "system" );
        }

        return model;
    }

    public UsersModel toModel( final EntityPageList<UserEntity> list )
    {
        final UsersModel model = new UsersModel();
        model.setTotal( list.getTotal() );

        for ( final UserEntity entity : list.getList() )
        {
            model.addUser( toModel( entity ) );
        }

        return model;
    }

    public UserModel toUserInfoModel( final UserEntity entity )
    {
        UserModel userModel = toModel( entity );
        UserInfoModel userInfoModel = new UserInfoModel();
        UserInfo userInfo = entity.getUserInfo();
        String birthday = null;
        if ( userInfo.getBirthday() != null )
        {
            birthday = new SimpleDateFormat( "yyyy-MM-dd" ).format( userInfo.getBirthday() );
        }
        userInfoModel.setBirthday( birthday );
        userInfoModel.setCountry( userInfo.getCountry() );
        userInfoModel.setDescription( userInfo.getDescription() );
        userInfoModel.setFax( userInfo.getFax() );
        userInfoModel.setFirstName( userInfo.getFirstName() );
        userInfoModel.setGlobalPosition( userInfo.getGlobalPosition() );
        userInfoModel.setHomePage( userInfo.getHomePage() );
        userInfoModel.setHtmlEmail( userInfo.getHtmlEmail().toString() );
        userInfoModel.setInitials( userInfo.getInitials() );
        userInfoModel.setLastName( userInfo.getLastName() );
        userInfoModel.setLocale( userInfo.getLocale().toString() );
        userInfoModel.setMemberId( userInfo.getMemberId() );
        userInfoModel.setMiddleName( userInfo.getMiddleName() );
        userInfoModel.setMobile( userInfo.getMobile() );
        userInfoModel.setNickName( userInfo.getOrganization() );
        userInfoModel.setPersonalId( userInfo.getPersonalId() );
        userInfoModel.setPhone( userInfo.getPhone() );
        userInfoModel.setPrefix( userInfo.getPrefix() );
        userInfoModel.setSuffix( userInfo.getSuffix() );
        userInfoModel.setTimeZone( userInfo.getTimeZone().toString() );
        userInfoModel.setTitle( userInfo.getTitle() );
        userInfoModel.setGender( userInfo.getGender().toString() );
        userInfoModel.setOrganization( userInfo.getOrganization() );
        for ( Address address : userInfo.getAddresses() )
        {
            userInfoModel.getAddresses().add( toAddressModel( address ) );
        }
        userModel.setUserInfo( userInfoModel );
        return userModel;
    }

    private AddressModel toAddressModel( final Address address )
    {
        AddressModel addressModel = new AddressModel();
        addressModel.setCountry( address.getCountry() );
        addressModel.setIsoCountry( address.getIsoCountry() );
        addressModel.setRegion( address.getRegion() );
        addressModel.setIsoRegion( address.getIsoRegion() );
        addressModel.setLabel( address.getLabel() );
        addressModel.setPostalAddress( address.getPostalAddress() );
        addressModel.setPostalCode( address.getPostalCode() );
        addressModel.setStreet( address.getStreet() );
        return addressModel;
    }

    public UserEntity toEntity( final UserModel userModel )
    {
        UserEntity userEntity = new UserEntity(  );
        if (userModel.getKey() != null){
            userEntity = userDao.findByKey( userModel.getKey() );
        }
        if (userModel.getName() != null){
            userEntity.setName( userModel.getName() );
        }
        if (userModel.getDisplayName() != null){
            userEntity.setDisplayName( userModel.getDisplayName() );
        }
        if (userModel.getEmail() != null){
            userEntity.setEmail( userModel.getEmail() );
        }
        if (userModel.getUserInfo() != null){
            updateUserInfo( userEntity, userModel );
        }
        return userEntity;
    }

    private void updateUserInfo(UserEntity userEntity, UserModel userModel){
        UserInfo userInfo = userEntity.getUserInfo();
        UserInfoModel userInfoModel = userModel.getUserInfo();
        if (userInfoModel.getBirthday() != null){
            try
            {
                userInfo.setBirthday( new SimpleDateFormat( "yyyy-MM-dd" ).parse( userInfoModel.getBirthday() ) );
            }
            catch ( ParseException e )
            {
                System.out.println( "Can't parse date string: " + userInfoModel.getBirthday()  );
            }
        }
        if (userInfoModel.getCountry() != null){
            userInfo.setCountry( userInfoModel.getCountry() );
        }
        if (userInfoModel.getDescription() != null){
            userInfo.setDescription( userInfoModel.getDescription() );
        }
        if (userInfoModel.getFax() != null){
            userInfo.setFax( userInfoModel.getFax() );
        }
        if (userInfoModel.getFirstName() != null){
            userInfo.setFirstName( userInfoModel.getFirstName() );
        }
        if (userInfoModel.getGender() != null){
            userInfo.setGender( Gender.valueOf(userInfoModel.getGender()) );
        }
        if (userInfoModel.getGlobalPosition() != null){
            userInfo.setGlobalPosition( userInfoModel.getGlobalPosition() );
        }
        if (userInfoModel.getHomePage() != null){
            userInfo.setHomePage( userInfoModel.getHomePage() );
        }
        if (userInfoModel.getHtmlEmail() != null){
            userInfo.setHtmlEmail( BooleanUtils.toBoolean( userInfoModel.getHtmlEmail() ) );
        }
        if (userInfoModel.getInitials() != null){
            userInfo.setInitials( userInfoModel.getInitials() );
        }
        if (userInfoModel.getLastName() != null){
            userInfo.setLastName( userInfoModel.getLastName() );
        }
        if (userInfoModel.getLocale() != null){
            userInfo.setLocale( new Locale(userInfoModel.getLocale()) );
        }
        if (userInfoModel.getMemberId() != null){
            userInfo.setMemberId( userInfoModel.getMemberId() );
        }
        if (userInfoModel.getMiddleName() != null){
            userInfo.setMiddleName( userInfoModel.getMiddleName() );
        }
        if (userInfoModel.getMobile() != null){
            userInfo.setMobile( userInfoModel.getMobile() );
        }
        if (userInfoModel.getNickName() != null){
            userInfo.setNickName( userInfoModel.getNickName() );
        }
        if (userInfoModel.getOrganization() != null){
            userInfo.setOrganization( userInfoModel.getOrganization() );
        }
        if (userInfoModel.getPersonalId() != null){
            userInfo.setPersonalId( userInfoModel.getPersonalId() );
        }
        if (userInfoModel.getPhone() != null){
            userInfo.setPhone( userInfoModel.getPhone() );
        }
        if (userInfoModel.getPrefix() != null){
            userInfo.setPrefix( userInfoModel.getPrefix() );
        }
        if (userInfoModel.getSuffix() != null){
            userInfo.setSuffix( userInfoModel.getSuffix() );
        }
        if (userInfoModel.getTimeZone() != null){
            userInfo.setTimezone( TimeZone.getTimeZone( userInfoModel.getTimeZone() ) );
        }
        if (userInfoModel.getTitle() != null){
            userInfo.setTitle( userInfoModel.getTitle() );
        }
        if (userInfoModel.getAddresses() != null){
            List<Address> addresses = new ArrayList<Address>(  );
            for (AddressModel addressModel : userInfoModel.getAddresses()){
                addresses.add( toAddress( addressModel ) );
            }
        }
        userEntity.updateUserInfo( userInfo );
    }

    private Address toAddress(AddressModel addressModel){
        Address address = new Address();
        address.setCountry( addressModel.getCountry() );
        address.setIsoCountry( addressModel.getIsoCountry() );
        address.setIsoRegion( addressModel.getIsoRegion() );
        address.setLabel( addressModel.getLabel() );
        address.setPostalAddress( addressModel.getPostalAddress() );
        address.setPostalCode( addressModel.getPostalCode() );
        address.setRegion( addressModel.getRegion() );
        address.setStreet( addressModel.getStreet() );
        return address;
    }


}

package com.enonic.cms.admin.user;

import java.text.SimpleDateFormat;

import com.enonic.cms.core.security.user.UserEntity;

import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.domain.user.Address;
import com.enonic.cms.domain.user.UserInfo;

public final class UserModelHelper
{
    public static UserModel toModel(final UserEntity entity)
    {
        final UserModel model = new UserModel();
        model.setKey(entity.getKey().toString());
        model.setName(entity.getName());
        model.setEmail(entity.getEmail());
        model.setQualifiedName(entity.getQualifiedName().toString());
        model.setDisplayName(entity.getDisplayName());
        model.setLastModified(entity.getLastModified());

        if (entity.getUserStore() != null) {
            model.setUserStore(entity.getUserStore().getName());
        } else {
            model.setUserStore("system");
        }

        return model;
    }

    public static UsersModel toModel(final EntityPageList<UserEntity> list)
    {
        final UsersModel model = new UsersModel();
        model.setTotal(list.getTotal());

        for (final UserEntity entity : list.getList()) {
            model.addUser(toModel(entity));
        }
        
        return model;
    }

    public static UserModel toUserInfoModel(final UserEntity entity){
        UserModel userModel = toModel( entity );
        UserInfoModel userInfoModel = new UserInfoModel();
        UserInfo userInfo = entity.getUserInfo();
        String birthday = null;
        if (userInfo.getBirthday() != null){
            birthday = new SimpleDateFormat("yyyy-MM-dd").format( userInfo.getBirthday() );
        }
        userInfoModel.setBirthday( birthday );
        userInfoModel.setCountry( userInfo.getCountry() );
        userInfoModel.setDescription( userInfo.getDescription() );
        userInfoModel.setFax( userInfo.getFax() );
        userInfoModel.setFirstName( userInfo.getFirstName() );
        userInfoModel.setGlobalPosition( userInfo.getGlobalPosition() );
        userInfoModel.setHomePage( userInfo.getHomePage() );
        userInfoModel.setHtmlEmail( userInfo.getHtmlEmail() );
        userInfoModel.setInitials( userInfo.getInitials() );
        userInfoModel.setLastName( userInfo.getLastName() );
        userInfoModel.setLocale( userInfo.getLocale() );
        userInfoModel.setMemberId( userInfo.getMemberId() );
        userInfoModel.setMiddleName( userInfo.getMiddleName() );
        userInfoModel.setMobile( userInfo.getMobile() );
        userInfoModel.setNickName( userInfo.getOrganization() );
        userInfoModel.setPersonalId( userInfo.getPersonalId() );
        userInfoModel.setPhone( userInfo.getPhone() );
        userInfoModel.setPrefix( userInfo.getPrefix() );
        userInfoModel.setSuffix( userInfo.getSuffix() );
        userInfoModel.setTimeZone( userInfo.getTimeZone() );
        userInfoModel.setTitle( userInfo.getTitle() );
        userInfoModel.setGender( userInfo.getGender() );
        userInfoModel.setOrganization( userInfo.getOrganization() );
        for (Address address : userInfo.getAddresses()){
            userInfoModel.getAddresses().add(toAddressModel( address ));
        }
        userModel.setUserInfo( userInfoModel );
        return userModel;
    }

    private static AddressModel toAddressModel(final Address address){
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
}

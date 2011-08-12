package com.enonic.cms.admin.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.domain.user.Address;
import com.enonic.cms.domain.user.Gender;
import com.enonic.cms.domain.user.UserInfo;

@Component
public final class UserModelTranslator
{
    private static final Logger LOG = LoggerFactory.getLogger( UsersResource.class );

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    protected SecurityService securityService;

    public UserModel toModel( final UserEntity entity )
    {
        final UserModel model = new UserModel();
        model.setKey( entity.getKey().toString() );
        model.setName( entity.getName() );
        model.setEmail( entity.getEmail() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setLastModified( entity.getLastModified() );
        //TODO: not implemented
        model.setLastLogged( "01-01-2001" );
        //TODO: not implemented
        model.setCreated( "13-09-1998" );
        List<Map<String, String>> groups = new ArrayList<Map<String, String>>(  );
        for ( GroupEntity group : entity.getAllMembershipsGroups()){
            Map <String, String> groupMap = new HashMap<String, String>();
            groupMap.put( "name", group.getDisplayName() );
            groupMap.put( "key", group.getGroupKey().toString() );
            groups.add( groupMap );
        }
        model.setGroups( groups );
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
        if ( userInfo.getHtmlEmail() != null )
        {
            userInfoModel.setHtmlEmail( userInfo.getHtmlEmail().toString() );
        }
        userInfoModel.setInitials( userInfo.getInitials() );
        userInfoModel.setLastName( userInfo.getLastName() );
        if ( userInfo.getLocale() != null )
        {
            userInfoModel.setLocale( userInfo.getLocale().toString() );
        }
        userInfoModel.setMemberId( userInfo.getMemberId() );
        userInfoModel.setMiddleName( userInfo.getMiddleName() );
        userInfoModel.setMobile( userInfo.getMobile() );
        userInfoModel.setNickName( userInfo.getOrganization() );
        userInfoModel.setPersonalId( userInfo.getPersonalId() );
        userInfoModel.setPhone( userInfo.getPhone() );
        userInfoModel.setPrefix( userInfo.getPrefix() );
        userInfoModel.setSuffix( userInfo.getSuffix() );
        if ( userInfo.getTimeZone() != null )
        {
            userInfoModel.setTimeZone( userInfo.getTimeZone().toString() );
        }
        userInfoModel.setTitle( userInfo.getTitle() );
        if ( userInfo.getGender() != null )
        {
            userInfoModel.setGender( userInfo.getGender().toString() );
        }
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

    private UserInfo toUserInfo( UserInfoModel userInfoModel )
    {
        UserInfo userInfo = new UserInfo();
        if ( userInfoModel.getBirthday() != null )
        {
            try
            {
                userInfo.setBirthday( new SimpleDateFormat( "yyyy-MM-dd" ).parse( userInfoModel.getBirthday() ) );
            }
            catch ( ParseException e )
            {
                LOG.error( "Can't parse date string: " + userInfoModel.getBirthday() );
            }
        }
        if ( userInfoModel.getCountry() != null )
        {
            userInfo.setCountry( userInfoModel.getCountry() );
        }
        if ( userInfoModel.getDescription() != null )
        {
            userInfo.setDescription( userInfoModel.getDescription() );
        }
        if ( userInfoModel.getFax() != null )
        {
            userInfo.setFax( userInfoModel.getFax() );
        }
        if ( userInfoModel.getFirstName() != null )
        {
            userInfo.setFirstName( userInfoModel.getFirstName() );
        }
        if ( userInfoModel.getGender() != null )
        {
            userInfo.setGender( Gender.valueOf( userInfoModel.getGender() ) );
        }
        if ( userInfoModel.getGlobalPosition() != null )
        {
            userInfo.setGlobalPosition( userInfoModel.getGlobalPosition() );
        }
        if ( userInfoModel.getHomePage() != null )
        {
            userInfo.setHomePage( userInfoModel.getHomePage() );
        }
        if ( userInfoModel.getHtmlEmail() != null )
        {
            userInfo.setHtmlEmail( BooleanUtils.toBoolean( userInfoModel.getHtmlEmail() ) );
        }
        if ( userInfoModel.getInitials() != null )
        {
            userInfo.setInitials( userInfoModel.getInitials() );
        }
        if ( userInfoModel.getLastName() != null )
        {
            userInfo.setLastName( userInfoModel.getLastName() );
        }
        if ( userInfoModel.getLocale() != null )
        {
            userInfo.setLocale( new Locale( userInfoModel.getLocale() ) );
        }
        if ( userInfoModel.getMemberId() != null )
        {
            userInfo.setMemberId( userInfoModel.getMemberId() );
        }
        if ( userInfoModel.getMiddleName() != null )
        {
            userInfo.setMiddleName( userInfoModel.getMiddleName() );
        }
        if ( userInfoModel.getMobile() != null )
        {
            userInfo.setMobile( userInfoModel.getMobile() );
        }
        if ( userInfoModel.getNickName() != null )
        {
            userInfo.setNickName( userInfoModel.getNickName() );
        }
        if ( userInfoModel.getOrganization() != null )
        {
            userInfo.setOrganization( userInfoModel.getOrganization() );
        }
        if ( userInfoModel.getPersonalId() != null )
        {
            userInfo.setPersonalId( userInfoModel.getPersonalId() );
        }
        if ( userInfoModel.getPhone() != null )
        {
            userInfo.setPhone( userInfoModel.getPhone() );
        }
        if ( userInfoModel.getPrefix() != null )
        {
            userInfo.setPrefix( userInfoModel.getPrefix() );
        }
        if ( userInfoModel.getSuffix() != null )
        {
            userInfo.setSuffix( userInfoModel.getSuffix() );
        }
        if ( userInfoModel.getTimeZone() != null )
        {
            userInfo.setTimezone( TimeZone.getTimeZone( userInfoModel.getTimeZone() ) );
        }
        if ( userInfoModel.getTitle() != null )
        {
            userInfo.setTitle( userInfoModel.getTitle() );
        }
        if ( userInfoModel.getAddresses() != null )
        {
            List<Address> addresses = new ArrayList<Address>();
            for ( AddressModel addressModel : userInfoModel.getAddresses() )
            {
                addresses.add( toAddress( addressModel ) );
            }
            userInfo.setAddresses( (Address[]) addresses.toArray( new Address[addresses.size()] ) );
        }
        return userInfo;
    }

    private Address toAddress( AddressModel addressModel )
    {
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

    public StoreNewUserCommand toNewUserCommand( UserModel userModel )
    {
        StoreNewUserCommand command = new StoreNewUserCommand();
        UserInfo userInfo = toUserInfo( userModel.getUserInfo() );
        UserStoreEntity userStore = userStoreDao.findByName( userModel.getUserStore() );
        if ( userStore == null )
        {
            userStore = userStoreDao.findDefaultUserStore();
        }
        command.setUserInfo( userInfo );
        command.setDisplayName( userModel.getDisplayName() );
        command.setEmail( userModel.getEmail() );
        command.setPassword( "11111" );
        command.setUserStoreKey( userStore.getKey() );
        command.setAllowAnyUserAccess( true );
        command.setStorer( securityService.getLoggedInPortalUser().getKey() );
        return command;
    }

    public UpdateUserCommand toUpdateUserCommand( UserModel userModel )
    {
        UserStoreEntity userStore = userStoreDao.findByName( userModel.getUserStore() );
        if ( userStore == null )
        {
            userStore = userStoreDao.findDefaultUserStore();
        }
        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setDeletedStateNotDeleted();
        userSpecification.setName( userModel.getName() );
        userSpecification.setUserStoreKey( userStore.getKey() );
        UpdateUserCommand command = new UpdateUserCommand( new UserKey( userModel.getKey() ), userSpecification );
        UserInfo userInfo = toUserInfo( userModel.getUserInfo() );
        command.setEmail( userModel.getEmail() );
        command.setDisplayName( userModel.getDisplayName() );
        command.setUserInfo( userInfo );
        command.setAllowUpdateSelf( true );
        command.setUpdateOpenGroupsOnly( true );
        command.setUpdateStrategy( UpdateUserCommand.UpdateStrategy.REPLACE_ALL );
        return command;
    }
}

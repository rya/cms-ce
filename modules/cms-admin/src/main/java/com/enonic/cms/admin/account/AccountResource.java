package com.enonic.cms.admin.account;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

@Component
@Path("/admin/data/account")
@Produces("application/json")
public final class AccountResource
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountResource.class );

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @GET
    @Path("list")
    public AccountsModel getUsersAndGroups()
    {
        final List<UserEntity> userList = this.userDao.findAll( false );

        final List<GroupEntity> groupList = this.groupDao.findAll( false );

        return AccountModelHelper.toModel( userList, groupList );
    }
}

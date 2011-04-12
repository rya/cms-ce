/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.security.group.GroupKey;
import org.junit.Before;
import org.junit.Test;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.UserEntity;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Jul 16, 2009
 */
public class UserHandlerControllerTest
    extends UserHandlerController
{

    UserHandlerController userHandlerController;

    @Before
    public void setUp()
    {
        userHandlerController = new UserHandlerController();
    }

    @Test
    public void testAddGroupsFromSetGroupsConfig()
    {
        ExtendedMap formItems = new ExtendedMap();

        formItems.put( ALLGROUPKEYS, "1,2,3,4,5" );
        formItems.put( JOINGROUPKEY, new String[]{"2", "3", "6"} );

        UpdateUserCommand updateUserCommand = new UpdateUserCommand( null, null );

        MyUserEntityMock user = new MyUserEntityMock();

        userHandlerController.addGroupsFromSetGroupsConfig( formItems, updateUserCommand, user );

        List<GroupKey> expectedEntries = generateGroupKeyList( new String[]{"2", "3", "6", "7"} );

        assertEquals( "Should have 4 groups", updateUserCommand.getMemberships().size(), 4 );
        assertTrue( "Should contain groupKeys: 2, 3, 6, 7", updateUserCommand.getMemberships().containsAll( expectedEntries ) );
    }

    private List<GroupKey> generateGroupKeyList( String[] keys )
    {
        List<GroupKey> groupKeys = new ArrayList<GroupKey>();

        for ( String key : keys )
        {
            groupKeys.add( new GroupKey( key ) );
        }

        return groupKeys;
    }

    private class MyUserEntityMock
        extends UserEntity
    {
        @Override
        public Set<GroupEntity> getDirectMemberships()
        {

            GroupEntity group1 = new GroupEntity();
            group1.setKey( "1" );

            GroupEntity group2 = new GroupEntity();
            group2.setKey( "2" );

            GroupEntity group7 = new GroupEntity();
            group7.setKey( "7" );

            Set<GroupEntity> groupEntities = new HashSet<GroupEntity>();

            groupEntities.add( group1 );

            groupEntities.add( group2 );

            groupEntities.add( group7 );

            return groupEntities;
        }
    }
}

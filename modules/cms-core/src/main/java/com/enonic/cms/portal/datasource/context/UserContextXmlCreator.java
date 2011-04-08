/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.field.UserInfoXmlCreator;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;

public class UserContextXmlCreator
{
    private GroupDao groupDao;

    private final UserInfoXmlCreator userInfoXmlCreator = new UserInfoXmlCreator();

    public UserContextXmlCreator( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    public Document createUserDocument( final UserEntity user )
    {
        final Element userEl = doCreateUserElement( user );
        return new Document( userEl );
    }

    public Element createUserElement( final UserEntity user )
    {
        return doCreateUserElement( user );
    }

    private Element doCreateUserElement( final UserEntity user )
    {
        final Element userEl = new Element( "user" );
        userEl.setAttribute( "key", user.getKey().toString() );
        userEl.setAttribute( "qualified-name", user.getQualifiedName().toString() );
        userEl.setAttribute( "built-in", Boolean.toString( user.isBuiltIn() ) );

        if ( user.getUserStore() != null )
        {
            final UserStoreEntity userstore = user.getUserStore();
            final Element userstoreEl = new Element( "userstore" ).setText( userstore.getName() );
            userEl.addContent( userstoreEl );
        }

        userEl.addContent( new Element( "name" ).setText( user.getName() ) );
        userEl.addContent( new Element( "display-name" ).setText( user.getDisplayName() ) );
        userEl.addContent( new Element( "email" ).setText( user.getEmail() ) );

        userInfoXmlCreator.addUserInfoToElement( userEl, user.getUserInfo(), false );

        userEl.addContent( createMembershipsElement( user ) );

        return userEl;
    }


    private Element createMembershipsElement( final UserEntity user )
    {
        final Element membershipsEl = new Element( "memberships" );

        final GroupEntity anonymousGroup = groupDao.findBuiltInAnonymous();
        membershipsEl.addContent( createGroupElement( anonymousGroup, true ) );

        if ( user.getUserStoreKey() != null )
        {
            final GroupEntity authenticatedUsers = groupDao.findBuiltInAuthenticatedUsers( user.getUserStoreKey() );
            membershipsEl.addContent( createGroupElement( authenticatedUsers, true ) );
        }

        final Set<GroupEntity> directMemberships = user.getDirectMemberships();
        for ( final GroupEntity group : user.getAllMemberships() )
        {
            final boolean directMember = directMemberships.contains( group );
            membershipsEl.addContent( createGroupElement( group, directMember ) );
        }
        return membershipsEl;
    }

    private Element createGroupElement( final GroupEntity group, final boolean directMember )
    {
        final Element groupEl = new Element( "group" );
        groupEl.setAttribute( "key", group.getGroupKey().toString() );
        groupEl.setAttribute( "built-in", Boolean.toString( group.isBuiltIn() ) );
        groupEl.setAttribute( "direct-membership", Boolean.toString( directMember ) );
        groupEl.addContent( new Element( "name" ).setText( group.getName() ) );
        groupEl.addContent( new Element( "restricted" ).setText( Boolean.toString( group.isRestricted() ) ) );
        return groupEl;
    }
}

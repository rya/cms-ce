/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;


import java.util.List;
import java.util.Set;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupXmlCreator;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.util.Assert;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.domain.AbstractPagedXmlCreator;
import com.enonic.cms.core.security.user.field.UserInfoXmlCreator;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public class UserXmlCreator
    extends AbstractPagedXmlCreator
{
    private boolean includeUserFields = false;

    private boolean wrappUserFieldsInBlockElement = true;

    private boolean adminConsoleStyle = true;

    private final GroupXmlCreator groupXmlCreator;

    private final UserInfoXmlCreator userInfoXmlCreator = new UserInfoXmlCreator();

    public UserXmlCreator( final GroupXmlCreator groupXmlCreator )
    {
        this.groupXmlCreator = groupXmlCreator;
    }

    public UserXmlCreator()
    {
        groupXmlCreator = new GroupXmlCreator();
        groupXmlCreator.setAdminConsoleStyle( adminConsoleStyle );
    }

    public Document createUsersDocument( final List<UserEntity> users, final boolean includeMemberships, final boolean normalizeGroups )
    {
        final Document doc = new Document();
        final Element usersEl = new Element( "users" );
        usersEl.setAttribute( "count", String.valueOf( users.size() ) );
        for ( final UserEntity user : users )
        {
            usersEl.addContent( doCreateElement( user, includeMemberships, normalizeGroups ) );
        }
        doc.setRootElement( usersEl );
        return doc;
    }

    public Document createUsersDocument( final UserEntity user, final boolean includeMemberships, final boolean normalizeGroups )
    {
        Assert.notNull( user, "user cannot be null" );

        final Document doc = new Document();
        final Element usersEl = new Element( "users" );

        usersEl.addContent( doCreateElement( user, includeMemberships, normalizeGroups ) );

        doc.setRootElement( usersEl );
        return doc;
    }

    public Document createUserDocument( final UserEntity user, final boolean includeMemberships, final boolean normalizeGroups )
    {
        return new Document( doCreateElement( user, includeMemberships, normalizeGroups ) );
    }

    public Document createEmptyUserDocument()
    {
        final Element userEl = new Element( "user" );
        return new Document( userEl );
    }

    public Element createUserElement( final UserEntity user, final boolean includeMemberships )
    {
        return doCreateElement( user, includeMemberships, false );
    }

    private Element doCreateElement( final UserEntity user, final boolean includeMemberships, final boolean normalizeGroups )
    {
        if ( adminConsoleStyle )
        {
            return doCreateElementAdminConsoleStyle( user, includeMemberships, normalizeGroups );
        }
        else
        {
            return doCreateElementPresentationStyle( user, includeMemberships, normalizeGroups );
        }
    }

    private Element doCreateElementAdminConsoleStyle( final UserEntity user, final boolean includeMemberships,
                                                      final boolean normalizeGroups )
    {
        final Element userEl = new Element( "user" );
        userEl.setAttribute( "key", user.getKey().toString() );
        userEl.setAttribute( "deleted", user.isDeleted() ? "true" : "false" );
        userEl.setAttribute( "builtIn", user.isBuiltIn() ? "true" : "false" );

        if ( user.getUserStoreKey() != null )
        {
            userEl.setAttribute( "userStoreKey", user.getUserStoreKey().toString() );
        }
        if ( user.getUserGroupKey() != null )
        {
            userEl.setAttribute( "groupKey", user.getUserGroupKey().toString() );
        }

        JDOMUtil.createElement( userEl, "name", user.getName() );
        JDOMUtil.createElement( userEl, "displayName", user.getDisplayName() );
        JDOMUtil.createElement( userEl, "qualifiedName", resolveQualifiedNameAsString( user.getQualifiedName() ) );
        JDOMUtil.createElement( userEl, "lastModified", user.getTimestamp().toString() );
        JDOMUtil.createElement( userEl, "email", user.getEmail() );
        if ( !includeUserFields )
        {
            JDOMUtil.createElement( userEl, "photo", String.valueOf( user.getPhoto() != null ) );
        }

        if ( includeMemberships )
        {
            final Element memberOfElement = new Element( "memberOf" );

            // certain special users (like admin, anonymous?) doesnt have a userGroup and dont have any memberships
            if ( user.hasUserGroup() )
            {
                final Set<GroupEntity> memberships = normalizeGroups ? user.getAllMemberships() : user.getDirectMemberships();
                memberOfElement.setAttribute( "count", String.valueOf( memberships.size() ) );
                for ( final GroupEntity group : memberships )
                {
                    final Element groupEl = groupXmlCreator.createElement( group, false, false );
                    memberOfElement.addContent( groupEl );
                }
                userEl.addContent( memberOfElement );
            }
        }

        if ( includeUserFields )
        {
            Element userFieldsRootEl = null;
            if ( wrappUserFieldsInBlockElement )
            {
                userFieldsRootEl = userInfoXmlCreator.createUserInfoElement( user );
                userFieldsRootEl.setAttribute( "oid", "dummy" );
                userEl.addContent( userFieldsRootEl );
            }
            else
            {
                userInfoXmlCreator.addUserInfoToElement( userEl, user.getUserInfo(), false );
                userFieldsRootEl = userEl;
            }
            userFieldsRootEl.addContent( 0, new Element( "email" ).setText( user.getEmail() ) );
            userFieldsRootEl.addContent( 0, new Element( "fullname" ).setText( user.getDisplayName() ) );
            userFieldsRootEl.addContent( 0, new Element( "uid" ).setText( user.getName() ) );
        }

        return userEl;
    }

    private Element doCreateElementPresentationStyle( final UserEntity user, final boolean includeMemberships,
                                                      final boolean normalizeGroups )
    {
        final Element userEl = new Element( "user" );
        userEl.setAttribute( "key", user.getKey().toString() );
        userEl.setAttribute( "deleted", user.isDeleted() ? "true" : "false" );
        userEl.setAttribute( "built-in", user.isBuiltIn() ? "true" : "false" );
        userEl.setAttribute( "qualified-name", resolveQualifiedNameAsString( user.getQualifiedName() ) );

        if ( user.getUserGroupKey() != null )
        {
            userEl.setAttribute( "group-key", user.getUserGroupKey().toString() );
        }

        if ( user.getUserStore() != null )
        {
            UserStoreEntity userstore = user.getUserStore();
            final Element userstoreEl = new Element( "userstore" ).setText( userstore.getName() );
            userEl.addContent( userstoreEl );
        }

        JDOMUtil.createElement( userEl, "name", user.getName() );
        JDOMUtil.createElement( userEl, "display-name", user.getDisplayName() );
        JDOMUtil.createElement( userEl, "last-modified", user.getTimestamp().toString() );
        JDOMUtil.createElement( userEl, "email", user.getEmail() );
        if ( !includeUserFields )
        {
            JDOMUtil.createElement( userEl, "photo", Boolean.toString( user.hasPhoto() ) );
        }

        if ( includeMemberships )
        {
            final Element membershipsEl = new Element( "memberships" );

            // certain special users (like admin, anonymous?) doesnt have a userGroup and dont have any memberships
            if ( user.hasUserGroup() )
            {
                final Set<GroupEntity> memberships = normalizeGroups ? user.getAllMemberships() : user.getDirectMemberships();
                membershipsEl.setAttribute( "count", String.valueOf( memberships.size() ) );
                for ( final GroupEntity group : memberships )
                {
                    final Element groupEl = groupXmlCreator.createElement( group, false, false );
                    membershipsEl.addContent( groupEl );
                }
                userEl.addContent( membershipsEl );
            }
        }

        if ( includeUserFields )
        {
            userInfoXmlCreator.addUserInfoToElement( userEl, user.getUserInfo(), false );
        }

        return userEl;
    }

    private String resolveQualifiedNameAsString( QualifiedUsername qualifiedName )
    {
        String qualifiedNameStr;
        if ( qualifiedName.hasUserStoreNameSet() )
        {
            qualifiedNameStr = qualifiedName.getUserStoreName() + "\\" + qualifiedName.getUsername();
        }
        else
        {
            qualifiedNameStr = qualifiedName.getUsername();
        }
        return qualifiedNameStr;
    }

    @Override
    public Element createElement( final Object obj )
    {
        return doCreateElement( (UserEntity) obj, false, false );
    }

    @Override
    public String getRootName()
    {
        return "users";
    }

    public void setIncludeUserFields( final boolean value )
    {
        includeUserFields = value;
    }

    public void wrappUserFieldsInBlockElement( final boolean value )
    {
        wrappUserFieldsInBlockElement = value;
    }

    public void setAdminConsoleStyle( boolean value )
    {
        this.adminConsoleStyle = value;
        if ( groupXmlCreator != null )
        {
            groupXmlCreator.setAdminConsoleStyle( value );
        }
    }
}

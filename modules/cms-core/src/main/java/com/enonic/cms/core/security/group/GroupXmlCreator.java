/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.domain.AbstractPagedXmlCreator;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserXmlCreator;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public class GroupXmlCreator
    extends AbstractPagedXmlCreator
{

    private final UserXmlCreator userXmlCreator;

    private boolean includeDescription;

    private boolean adminConsoleStyle = true;

    public GroupXmlCreator( UserXmlCreator userXmlCreator )
    {
        this.userXmlCreator = userXmlCreator;
    }

    public GroupXmlCreator()
    {
        this.userXmlCreator = new UserXmlCreator( this );
    }

    public Document createGroupsDocument( List<GroupEntity> groups, boolean includeMembership, boolean includeMembers )
    {

        Document doc = new Document();
        Element groupsEl = new Element( "groups" );
        groupsEl.setAttribute( "count", String.valueOf( groups.size() ) );
        for ( GroupEntity group : groups )
        {
            groupsEl.addContent( doCreateElement( group, includeMembership, includeMembers, false ) );
        }
        doc.setRootElement( groupsEl );
        return doc;
    }

    public Document createGroupDocument( GroupEntity group, boolean includeMembership, boolean includeMembers, boolean normalizeGroups )
    {

        Document doc = new Document();
        Element rootEl = doCreateElement( group, includeMembership, includeMembers, normalizeGroups );
        doc.setRootElement( rootEl );
        return doc;
    }

    public Document createEmptyGroupDocument()
    {
        return new Document();
    }

    public Element createElement( GroupEntity group, boolean includeMembership, boolean includeMembers )
    {
        return doCreateElement( group, includeMembership, includeMembers, false );
    }

    private Element doCreateElement( GroupEntity group, boolean includeMembership, boolean includeMembers, boolean normalizeGroups )
    {
        if ( adminConsoleStyle )
        {
            return doCreateElementAdminConsoleStyle( group, includeMembership, includeMembers, normalizeGroups );
        }
        else
        {
            return doCreateElementPresentationStyle( group, includeMembership, includeMembers, normalizeGroups );
        }
    }

    private Element doCreateElementAdminConsoleStyle( GroupEntity group, boolean includeMembership, boolean includeMembers,
                                                      boolean normalizeGroups )
    {

        final GroupType type = group.getType();

        if ( type.equals( GroupType.USER ) )
        {
            UserEntity userEntity = group.getUser();
            return userXmlCreator.createUserElement( userEntity, false );
        }

        Element groupEl = new Element( "group" );
        groupEl.setAttribute( "builtIn", type.isBuiltIn() ? "true" : "false" );
        groupEl.setAttribute( "deleted", group.isDeleted() ? "true" : "false" );
        groupEl.setAttribute( "key", group.getGroupKey().toString() );
        groupEl.setAttribute( "restricted", group.isRestricted() ? "true" : "false" );
        groupEl.setAttribute( "type", String.valueOf( type.toInteger() ) );
        groupEl.setAttribute( "typeName", type.getName() );

        UserStoreEntity userStore = group.getUserStore();
        if ( userStore != null )
        {
            groupEl.setAttribute( "userStoreKey", String.valueOf( userStore.getKey() ) );
        }

        JDOMUtil.createElement( groupEl, "name", group.getName() );
        JDOMUtil.createElement( groupEl, "displayName", getDisplayName( group ) );
        if ( group.getQualifiedName() != null )
        {
            JDOMUtil.createElement( groupEl, "qualifiedName", group.getQualifiedName().toString() );
        }
        if ( includeDescription )
        {
            JDOMUtil.createElement( groupEl, "description", group.getDescription() );
        }

        if ( includeMembers )
        {
            Element membersEl = new Element( "members" );
            final Set<GroupEntity> groupMembers = group.getMembers( false );
            membersEl.setAttribute( "count", String.valueOf( groupMembers.size() ) );
            for ( GroupEntity member : groupMembers )
            {
                Element msgroupEl = doCreateElement( member, false, false, false );
                membersEl.addContent( msgroupEl );
            }
            groupEl.addContent( membersEl );
        }

        if ( includeMembership )
        {
            Element membershipEl = new Element( "memberOf" );
            final Set<GroupEntity> memberships = normalizeGroups ? group.getAllMemberships() : group.getMemberships( false );
            membershipEl.setAttribute( "count", String.valueOf( memberships.size() ) );
            for ( GroupEntity membershipGroup : memberships )
            {

                Element membershipGroupEl = doCreateElement( membershipGroup, false, false, false );
                membershipEl.addContent( membershipGroupEl );
            }
            groupEl.addContent( membershipEl );
        }

        return groupEl;
    }

    private Element doCreateElementPresentationStyle( GroupEntity group, boolean includeMembership, boolean includeMembers,
                                                      boolean normalizeGroups )
    {

        final GroupType type = group.getType();

        if ( type.equals( GroupType.USER ) )
        {
            UserEntity userEntity = group.getUser();
            return userXmlCreator.createUserElement( userEntity, false );
        }

        Element groupEl = new Element( "group" );
        groupEl.setAttribute( "key", group.getGroupKey().toString() );
        groupEl.setAttribute( "built-in", type.isBuiltIn() ? "true" : "false" );
        groupEl.setAttribute( "deleted", group.isDeleted() ? "true" : "false" );
        groupEl.setAttribute( "type", String.valueOf( type.toInteger() ) );
        groupEl.setAttribute( "type-name", type.getName() );
        if ( group.getQualifiedName() != null )
        {
            groupEl.setAttribute( "qualified-name", group.getQualifiedName().toString() );
        }

        UserStoreEntity userStore = group.getUserStore();
        if ( userStore != null )
        {
            groupEl.addContent( new Element( "userstore" ).setText( userStore.getName() ) );
        }

        JDOMUtil.createElement( groupEl, "name", group.getName() );
        JDOMUtil.createElement( groupEl, "display-name", getDisplayName( group ) );
        groupEl.addContent( new Element( "restricted" ).setText( group.isRestricted() ? "true" : "false" ) );

        if ( includeDescription )
        {
            JDOMUtil.createElement( groupEl, "description", group.getDescription() );
        }

        if ( includeMembers )
        {
            Element membersEl = new Element( "members" );
            final Set<GroupEntity> groupMembers = group.getMembers( false );
            membersEl.setAttribute( "count", String.valueOf( groupMembers.size() ) );
            for ( GroupEntity member : groupMembers )
            {
                Element msgroupEl = doCreateElement( member, false, false, false );
                membersEl.addContent( msgroupEl );
            }
            groupEl.addContent( membersEl );
        }

        if ( includeMembership )
        {
            Element membershipEl = new Element( "memberships" );
            final Set<GroupEntity> memberships = normalizeGroups ? group.getAllMemberships() : group.getMemberships( false );
            membershipEl.setAttribute( "count", String.valueOf( memberships.size() ) );
            for ( GroupEntity membershipGroup : memberships )
            {
                Element membershipGroupEl = doCreateElement( membershipGroup, false, false, false );
                membershipEl.addContent( membershipGroupEl );
            }
            groupEl.addContent( membershipEl );
        }

        return groupEl;
    }

    private String getDisplayName( GroupEntity group )
    {

        final GroupType type = group.getType();

        StringBuffer s = new StringBuffer();

        if ( type.equals( GroupType.GLOBAL_GROUP ) || type.equals( GroupType.USERSTORE_GROUP ) )
        {
            s.append( group.getName() );
        }
        else
        {
            s.append( type.getName() );
            if ( type.isBuiltIn() )
            {
                s.append( " (built-in)" );
            }
        }

        return s.toString();
    }

    @Override
    public Element createElement( Object obj )
    {
        return doCreateElement( (GroupEntity) obj, false, true, false );
    }

    @Override
    public String getRootName()
    {
        return "groups";
    }

    public void setIncludeDescription( boolean value )
    {
        this.includeDescription = value;
    }

    public void setAdminConsoleStyle( boolean value )
    {
        this.adminConsoleStyle = value;
    }
}

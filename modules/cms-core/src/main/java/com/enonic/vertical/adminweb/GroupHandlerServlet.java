/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.domain.security.group.DeleteGroupCommand;
import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.group.GroupSpecification;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.group.GroupXmlCreator;
import com.enonic.cms.domain.security.group.StoreNewGroupCommand;
import com.enonic.cms.domain.security.group.UpdateGroupCommand;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;
import com.enonic.cms.domain.security.userstore.UserStoreXmlCreator;
import com.enonic.cms.domain.security.userstore.connector.config.InvalidUserStoreConnectorConfigException;

final public class GroupHandlerServlet
    extends AdminHandlerBaseServlet
{

    private String buildGroupXML( ExtendedMap dataMap )
        throws VerticalAdminException
    {

        Document doc = XMLTool.createDocument( "group" );
        Element groupElement = doc.getDocumentElement();

        // key
        if ( dataMap.containsKey( "key" ) )
        {
            groupElement.setAttribute( "key", dataMap.getString( "key" ) );
        }

        groupElement.setAttribute( "restricted", dataMap.getString( "restricted", "false" ) );

        if ( dataMap.containsKey( "userstorekey" ) )
        {
            groupElement.setAttribute( "type", GroupType.USERSTORE_GROUP.toInteger().toString() );
        }
        else
        {
            groupElement.setAttribute( "type", GroupType.GLOBAL_GROUP.toInteger().toString() );
        }

        /*
        // group type
        int groupType = dataMap.getInt("grouptype");
        groupElement.setAttribute("type", dataMap.getString("grouptype"));


        switch (groupType) {
            case GroupTypes.DOMAIN_GROUP:
            case GroupTypes.DOMAIN_ADMIN_GROUP:
                groupElement.setAttribute("domainkey", dataMap.getString("domainkey"));
                break;

            case GroupTypes.SITE_GROUP:
            case GroupTypes.SITE_ADMIN_GROUP:
            case GroupTypes.SITE_CONTRIB_GROUP:
                groupElement.setAttribute("sitekey", dataMap.getString("sitekey"));
                break;

            case GroupTypes.ENTERPRISE_GROUP:
                // do something? what?
                break;

            default:
                VerticalAdminLogger.errorAdmin(
                        this.getClass(),
                        10,
                        "Group type not supported: %0",
                        new Object[]{new Integer(groupType)},
                        null);
        }
        */

        // name
        XMLTool.createElement( doc, groupElement, "name", dataMap.getString( "name" ) );

        // description
        if ( dataMap.containsKey( "description" ) )
        {
            XMLTool.createElement( doc, groupElement, "description", dataMap.getString( "description" ) );
        }
        else
        {
            XMLTool.createElement( doc, groupElement, "description" );
        }

        // add members:
        Element membersElement = XMLTool.createElement( doc, groupElement, "members" );
        Element groupsElement = XMLTool.createElement( doc, membersElement, "groups" );
        if ( dataMap.containsKey( "member" ) )
        {
            String[] groupArray;
            if ( isArrayFormItem( dataMap, "member" ) )
            {
                groupArray = (String[]) dataMap.get( "member" );
            }
            else
            {
                groupArray = new String[]{dataMap.getString( "member" )};
            }

            for ( String aGroupArray : groupArray )
            {
                XMLTool.createElement( doc, groupsElement, "member" ).setAttribute( "key", aGroupArray );
            }
        }

        return XMLTool.documentToString( doc );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Document doc;
        GroupKey groupKey;
        if ( formItems.containsKey( "key" ) )
        {
            groupKey = new GroupKey( formItems.getString( "key" ) );
            GroupEntity group = securityService.getGroup( groupKey );
            if ( !( group.isBuiltIn() || group.isGlobal() || group.getUserStore() == null ) )
            {
                group = userStoreService.synchronizeGroup( groupKey );
            }

            GroupXmlCreator xmlCreator = new GroupXmlCreator();
            xmlCreator.setAdminConsoleStyle( true );
            xmlCreator.setIncludeDescription( true );
            doc = XMLDocumentFactory.create( xmlCreator.createGroupDocument( group, true, true, false ) ).getAsDOMDocument();
        }
        else
        {
            doc = XMLTool.createDocument( "groups" );
        }

        final UserStoreXmlCreator userStoreXmlCreator = new UserStoreXmlCreator( userStoreService.getUserStoreConnectorConfigs() );
        List<UserStoreEntity> userStores = securityService.getUserStores();
        Document userStoresDoc =
            XMLDocumentFactory.create( userStoreXmlCreator.createPagedDocument( userStores, 0, 100 ) ).getAsDOMDocument();
        XMLTool.mergeDocuments( doc, userStoresDoc, true );

        // parameters
        Map<String, Object> xslParams = new HashMap<String, Object>();
        xslParams.put( "page", formItems.getString( "page" ) );

        if ( formItems.containsKey( "key" ) )
        {
            xslParams.put( "key", formItems.getString( "key" ) );
            xslParams.put( "create", "0" );
        }
        else
        {
            xslParams.put( "create", "1" );
        }

        if ( formItems.containsKey( "userstorekey" ) )
        {
            UserStoreKey userStoreKey = new UserStoreKey( formItems.getInt( "userstorekey" ) );
            UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );

            xslParams.put( "userstorekey", userStoreKey.toString() );
            xslParams.put( "userstorename", userStore.getName() );
            try
            {
                xslParams.put( "canUpdateGroup", String.valueOf( userStoreService.canUpdateGroup( userStoreKey ) ) );
            }
            catch ( final InvalidUserStoreConnectorConfigException e )
            {
                xslParams.put( "userStoreConfigError", e.getMessage() );
            }
        }

        if ( formItems.containsKey( "mode" ) )
        {
            xslParams.put( "mode", formItems.getString( "mode" ) );
        }
        if ( formItems.containsKey( "callback" ) )
        {
            xslParams.put( "callback", formItems.getString( "callback" ) );
        }
        if ( formItems.containsKey( "modeselector" ) )
        {
            xslParams.put( "modeselector", formItems.getString( "modeselector" ) );
        }
        if ( formItems.containsKey( "userstoreselector" ) )
        {
            xslParams.put( "userstoreselector", formItems.getString( "userstoreselector" ) );
        }
        if ( formItems.containsKey( "excludekey" ) )
        {
            xslParams.put( "excludekey", formItems.getString( "excludekey" ) );
        }

        xslParams.put( "referer", request.getHeader( "referer" ) );

        xslParams.put( "level", formItems.getString( "level", "site" ) );

        addCommonParameters( admin, user, request, xslParams, -1, -1 );

        transformXML( request, response, doc, "group_form.xsl", xslParams );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {
        User oldUser = securityService.getLoggedInAdminConsoleUser();
        UserEntity executor = securityService.getUser( oldUser );

        String xmlData = buildGroupXML( formItems );
        UserStoreKey userStoreKey = null;
        if ( formItems.containsKey( "userstorekey" ) )
        {
            userStoreKey = new UserStoreKey( formItems.getInt( "userstorekey" ) );
        }
        //admin.createGroup( userStoreKey, xmlData );
        XMLDocument xmlDocument = XMLDocumentFactory.create( xmlData );
        org.jdom.Document jdomDoc = xmlDocument.getAsJDOMDocument();
        org.jdom.Element groupEl = jdomDoc.getRootElement();

        org.jdom.Element nameEl = groupEl.getChild( "name" );
        org.jdom.Element descriptionEl = groupEl.getChild( "description" );

        boolean restricted = Boolean.parseBoolean( groupEl.getAttributeValue( "restricted" ) );
        String name = nameEl.getText();
        String description = descriptionEl.getText();
        GroupType type = GroupType.get( groupEl.getAttributeValue( "type" ) );

        StoreNewGroupCommand command = new StoreNewGroupCommand();
        command.setName( name );
        command.setType( type );
        command.setDescription( description );
        command.setRestriced( restricted );
        command.setExecutor( executor );
        command.setUserStoreKey( userStoreKey );
        command.setRespondWithException( true );

        org.jdom.Element membersEl = groupEl.getChild( "members" );
        org.jdom.Element groupsEl = membersEl.getChild( "groups" );
        List<org.jdom.Element> membersElList = groupsEl.getChildren( "member" );
        for ( org.jdom.Element memberEl : membersElList )
        {
            GroupKey groupKey = new GroupKey( memberEl.getAttributeValue( "key" ) );
            command.addMember( groupKey );
        }

        userStoreService.storeNewGroup( command );

        redirectClientToAbsoluteUrl( formItems.getString( "referer" ), response );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User oldUser = securityService.getLoggedInAdminConsoleUser();
        UserEntity user = securityService.getUser( oldUser );

        String groupKey = formItems.getString( "key" );
        String name = formItems.getString( "name" );
        String description = formItems.getString( "description", null );
        boolean restricted = formItems.getBoolean( "restricted", false );

        String[] members = null;
        if ( formItems.containsKey( "member" ) )
        {
            if ( isArrayFormItem( formItems, "member" ) )
            {
                members = (String[]) formItems.get( "member" );
            }
            else
            {
                members = new String[]{formItems.getString( "member" )};
            }
        }

        UpdateGroupCommand command = new UpdateGroupCommand( user.getKey(), new GroupKey( groupKey ) );
        command.setName( name );
        command.setRestricted( restricted );
        command.setDescription( description );
        if ( members != null )
        {
            for ( String member : members )
            {
                GroupEntity memberAsGroup = groupDao.findByKey( new GroupKey( member ) );
                if ( memberAsGroup != null )
                {
                    command.addMember( memberAsGroup );
                }
                UserEntity memberAsUser = userDao.findByKey( new UserKey( member ) );
                if ( memberAsUser != null )
                {
                    GroupEntity memberAsUserUserGroup = memberAsUser.getUserGroup();
                    if ( memberAsUserUserGroup != null )
                    {
                        command.addMember( memberAsUserUserGroup );
                    }
                }
            }
        }
        else
        {
            command.syncMembers();
        }

        userStoreService.updateGroup( command );

        redirectClientToURL( new URL( formItems.getString( "referer" ) ), response );
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String key )
        throws VerticalAdminException, VerticalEngineException
    {

        User oldDeleter = securityService.getLoggedInAdminConsoleUser();
        UserEntity deleter = securityService.getUser( oldDeleter );

        GroupKey groupToDelete = new GroupKey( key );

        GroupSpecification groupToDeleteSpec = new GroupSpecification();
        groupToDeleteSpec.setKey( groupToDelete );
        DeleteGroupCommand command = new DeleteGroupCommand( deleter, groupToDeleteSpec );
        command.setRespondWithException( true );
        userStoreService.deleteGroup( command );

        redirectClientToReferer( request, response );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.client;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.enonic.cms.framework.time.TimeService;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLException;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.AssignContentParams;
import com.enonic.cms.api.client.model.CreateCategoryParams;
import com.enonic.cms.api.client.model.CreateContentParams;
import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.CreateGroupParams;
import com.enonic.cms.api.client.model.DeleteContentParams;
import com.enonic.cms.api.client.model.DeleteGroupParams;
import com.enonic.cms.api.client.model.DeletePreferenceParams;
import com.enonic.cms.api.client.model.GetBinaryParams;
import com.enonic.cms.api.client.model.GetCategoriesParams;
import com.enonic.cms.api.client.model.GetContentBinaryParams;
import com.enonic.cms.api.client.model.GetContentByCategoryParams;
import com.enonic.cms.api.client.model.GetContentByQueryParams;
import com.enonic.cms.api.client.model.GetContentBySectionParams;
import com.enonic.cms.api.client.model.GetContentParams;
import com.enonic.cms.api.client.model.GetContentVersionsParams;
import com.enonic.cms.api.client.model.GetGroupParams;
import com.enonic.cms.api.client.model.GetGroupsParams;
import com.enonic.cms.api.client.model.GetMenuBranchParams;
import com.enonic.cms.api.client.model.GetMenuDataParams;
import com.enonic.cms.api.client.model.GetMenuItemParams;
import com.enonic.cms.api.client.model.GetMenuParams;
import com.enonic.cms.api.client.model.GetPreferenceParams;
import com.enonic.cms.api.client.model.GetRandomContentByCategoryParams;
import com.enonic.cms.api.client.model.GetRandomContentBySectionParams;
import com.enonic.cms.api.client.model.GetRelatedContentsParams;
import com.enonic.cms.api.client.model.GetResourceParams;
import com.enonic.cms.api.client.model.GetSubMenuParams;
import com.enonic.cms.api.client.model.GetUserParams;
import com.enonic.cms.api.client.model.GetUsersParams;
import com.enonic.cms.api.client.model.ImportContentsParams;
import com.enonic.cms.api.client.model.JoinGroupsParams;
import com.enonic.cms.api.client.model.LeaveGroupsParams;
import com.enonic.cms.api.client.model.RenderContentParams;
import com.enonic.cms.api.client.model.RenderPageParams;
import com.enonic.cms.api.client.model.SetPreferenceParams;
import com.enonic.cms.api.client.model.SnapshotContentParams;
import com.enonic.cms.api.client.model.UnassignContentParams;
import com.enonic.cms.api.client.model.UpdateContentParams;
import com.enonic.cms.api.client.model.UpdateFileContentParams;
import com.enonic.cms.api.client.model.preference.Preference;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.GetContentExecutor;
import com.enonic.cms.core.content.GetContentResult;
import com.enonic.cms.core.content.GetContentXmlCreator;
import com.enonic.cms.core.content.PageCacheInvalidatorForContent;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.imports.ImportJob;
import com.enonic.cms.core.content.imports.ImportJobFactory;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.UserParser;
import com.enonic.cms.core.security.UserStoreParser;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.service.KeyService;
import com.enonic.cms.core.service.PresentationService;
import com.enonic.cms.core.service.UserServicesService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.GroupQuery;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import com.enonic.cms.business.SitePropertiesService;

import com.enonic.cms.core.preferences.PreferenceService;

import com.enonic.cms.business.portal.cache.PageCacheService;
import com.enonic.cms.business.portal.cache.SiteCachesService;
import com.enonic.cms.business.portal.datasource.context.UserContextXmlCreator;
import com.enonic.cms.business.preview.PreviewContext;
import com.enonic.cms.business.preview.PreviewService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.imports.ImportResult;
import com.enonic.cms.domain.content.imports.ImportResultXmlCreator;
import com.enonic.cms.domain.content.index.ContentIndexQuery.SectionFilterStatus;
import com.enonic.cms.domain.content.query.ContentByCategoryQuery;
import com.enonic.cms.domain.content.query.ContentByContentQuery;
import com.enonic.cms.domain.content.query.ContentByQueryQuery;
import com.enonic.cms.domain.content.query.ContentBySectionQuery;
import com.enonic.cms.domain.content.query.RelatedChildrenContentQuery;
import com.enonic.cms.domain.content.query.RelatedContentQuery;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.domain.preference.PreferenceEntity;
import com.enonic.cms.domain.preference.PreferenceKey;
import com.enonic.cms.domain.preference.PreferenceScope;
import com.enonic.cms.domain.preference.PreferenceScopeKey;
import com.enonic.cms.domain.preference.PreferenceScopeResolver;
import com.enonic.cms.domain.preference.PreferenceScopeType;
import com.enonic.cms.domain.preference.PreferenceSpecification;
import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.resource.ResourceXmlCreator;
import com.enonic.cms.domain.security.group.AddMembershipsCommand;
import com.enonic.cms.domain.security.group.DeleteGroupCommand;
import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.group.GroupNotFoundException;
import com.enonic.cms.domain.security.group.GroupSpecification;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.group.GroupXmlCreator;
import com.enonic.cms.domain.security.group.QualifiedGroupname;
import com.enonic.cms.domain.security.group.RemoveMembershipsCommand;
import com.enonic.cms.domain.security.group.StoreNewGroupCommand;
import com.enonic.cms.domain.security.user.QualifiedUsername;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserXmlCreator;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreNotFoundException;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

/**
 * This class implements the local client.
 */
public final class InternalClientImpl
        implements InternalClient
{

    private static final Logger LOG = LoggerFactory.getLogger( InternalClientImpl.class );

    private InternalClientContentService internalClientContentService;

    private InternalClientRenderService internalClientRenderService;

    private KeyService keyService;

    private AdminService adminService;

    private UserServicesService userServicesService;

    private PresentationService presentationService;

    private DataSourceService dataSourceService;

    private PresentationInvoker invoker;

    private SecurityService securityService;

    private TimeService timeService;

    @Autowired
    private UserStoreService userStoreService;

    private ContentService contentService;

    @Autowired
    private ImportJobFactory importJobFactory;

    private ResourceService resourceService;

    private PreferenceService preferenceService;

    private UserParser userParser;

    private UserStoreParser userStoreParser;

    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private CategoryDao categoryDao;

    private ContentDao contentDao;

    @Autowired
    private ContentVersionDao contentVersionDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    private MenuItemDao menuItemDao;

    @Autowired(required = false)
    private SiteCachesService siteCachesService;

    /**
     * Vertical properties.
     */
    private Properties cmsProperties;

    /**
     * Site properties service.
     */
    private SitePropertiesService sitePropertiesService;

    private PreviewService previewService;

    public KeyService getKeyService()
    {
        return this.keyService;
    }

    public PresentationService getPresentationService()
    {
        return this.presentationService;
    }

    public DataSourceService getDataSourceService()
    {
        return this.dataSourceService;
    }

    public UserServicesService getUserServicesService()
    {
        return userServicesService;
    }

    public AdminService getAdminService()
    {
        return this.adminService;
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    public void setCmsProperties( Properties cmsProperties )
    {
        this.cmsProperties = cmsProperties;
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Deprecated
    public String getUser()
            throws ClientException
    {
        try
        {
            return securityService.getLoggedInPortalUser().getName();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Deprecated
    public String getUserName()
            throws ClientException
    {

        try
        {
            return securityService.getLoggedInPortalUser().getName();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getUser( GetUserParams params )
            throws ClientException
    {

        try
        {
            final UserEntity user = userParser.parseUser( params.user );
            final UserXmlCreator xmlCreator = new UserXmlCreator();
            xmlCreator.setIncludeUserFields( params.includeCustomUserFields );
            xmlCreator.wrappUserFieldsInBlockElement( false );
            final Document userDoc = xmlCreator.createUserDocument( user, params.includeMemberships, params.normalizeGroups );
            return userDoc;

        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getGroup( GetGroupParams params )
    {
        try
        {
            if ( params.group == null )
            {
                throw new IllegalArgumentException( "group must be specified" );
            }

            GroupEntity group = parseGroup( params.group );
            GroupXmlCreator xmlCreator = new GroupXmlCreator();
            xmlCreator.setAdminConsoleStyle( false );
            return xmlCreator.createGroupDocument( group, params.includeMemberships, params.includeMembers, params.normalizeGroups );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getUsers( GetUsersParams params )
            throws ClientException
    {

        try
        {
            if ( params.userStore == null )
            {
                throw new IllegalArgumentException( "userStore must be specified" );
            }
            if ( params.index < 0 )
            {
                throw new IllegalArgumentException( "Given index must be 0 or above" );
            }
            if ( params.count < 1 )
            {
                throw new IllegalArgumentException( "Given count must be 1 or above" );
            }

            UserStoreEntity userStore = userStoreParser.parseUserStore( params.userStore );
            List<UserEntity> users =
                    this.securityService.getUsers( userStore.getKey(), params.index, params.count, params.includeDeletedUsers );
            UserXmlCreator xmlCreator = new UserXmlCreator();
            xmlCreator.setAdminConsoleStyle( false );
            return xmlCreator.createUsersDocument( users, params.includeMemberships, params.normalizeGroups );

        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getGroups( GetGroupsParams params )
            throws ClientException
    {

        try
        {
            if ( params.index < 0 )
            {
                throw new IllegalArgumentException( "Given index must be 0 or above" );
            }
            if ( params.count < 1 )
            {
                throw new IllegalArgumentException( "Given count must be 1 or above" );
            }

            List<GroupEntity> groups;
            Collection<GroupType> groupTypes = parseGroupTypes( params.groupTypes );
            if ( params.userStore == null )
            {
                GroupQuery spec = new GroupQuery();
                spec.setGroupTypes( groupTypes );
                spec.setGlobalOnly( true );
                spec.setOrderBy( "name" );
                spec.setIncludeBuiltInGroups( params.includeBuiltInGroups );
                spec.setIncludeDeleted( params.includeDeletedGroups );
                groups = securityService.getGroups( spec );
            }
            else
            {
                UserStoreEntity userStore = userStoreParser.parseUserStore( params.userStore );
                GroupQuery spec = new GroupQuery();
                spec.setUserStoreKey( userStore.getKey() );
                spec.setGroupTypes( groupTypes );
                spec.setIndex( params.index );
                spec.setCount( params.count );
                spec.setIncludeDeleted( params.includeDeletedGroups );
                spec.setIncludeBuiltInGroups( params.includeBuiltInGroups );
                spec.setIncludeAnonymousGroups( true );
                groups = securityService.getGroups( spec );
            }

            GroupXmlCreator xmlCreator = new GroupXmlCreator();
            xmlCreator.setAdminConsoleStyle( false );
            return xmlCreator.createGroupsDocument( groups, params.includeMemberships, params.includeMembers );

        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document joinGroups( JoinGroupsParams params )
            throws ClientException
    {

        try
        {

            if ( params.group == null && params.user == null )
            {
                throw new IllegalArgumentException( "Either group or user must be specified" );
            }
            if ( params.group != null && params.user != null )
            {
                throw new IllegalArgumentException( "Specify either group or user, not both" );
            }
            if ( params.groupsToJoin == null )
            {
                throw new IllegalArgumentException( "groupsToJoin must be specified" );
            }

            GroupEntity groupToUse;
            if ( params.group != null )
            {
                groupToUse = parseGroup( params.group );
            }
            else
            {
                UserEntity user = userParser.parseUser( params.user );
                groupToUse = user.getUserGroup();
            }

            List<GroupEntity> groupsToJoin = parseGroups( params.groupsToJoin, true );
            UserEntity executor = securityService.getRunAsUser();
            GroupSpecification groupSpec = new GroupSpecification();
            groupSpec.setKey( groupToUse.getGroupKey() );

            AddMembershipsCommand command = new AddMembershipsCommand( groupSpec, executor.getKey() );
            for ( GroupEntity groupToJoin : groupsToJoin )
            {
                command.addGroupsToAddTo( groupToJoin.getGroupKey() );
            }

            List<GroupEntity> joinedGroups = userStoreService.addMembershipsToGroup( command );

            GroupXmlCreator xmlCreator = new GroupXmlCreator();
            xmlCreator.setAdminConsoleStyle( false );
            return xmlCreator.createGroupsDocument( joinedGroups, false, false );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document leaveGroups( LeaveGroupsParams params )
            throws ClientException
    {

        try
        {
            if ( params.group == null && params.user == null )
            {
                throw new IllegalArgumentException( "Either group or user must be specified" );
            }
            if ( params.group != null && params.user != null )
            {
                throw new IllegalArgumentException( "Specify either group or user, not both" );
            }
            if ( params.groupsToLeave == null )
            {
                throw new IllegalArgumentException( "groupsToLeave must be specified" );
            }

            GroupEntity groupToRemoveMembershipsFor;
            if ( params.group != null )
            {
                groupToRemoveMembershipsFor = parseGroup( params.group );
            }
            else
            {
                UserEntity user = userParser.parseUser( params.user );
                groupToRemoveMembershipsFor = user.getUserGroup();
            }

            Collection<GroupEntity> groupsToLeave = parseGroups( params.groupsToLeave, true );
            UserEntity executor = securityService.getRunAsUser();
            GroupSpecification groupToRemoveSpec = new GroupSpecification();
            groupToRemoveSpec.setKey( groupToRemoveMembershipsFor.getGroupKey() );

            RemoveMembershipsCommand command = new RemoveMembershipsCommand( groupToRemoveSpec, executor.getKey() );
            for ( GroupEntity groupToLeave : groupsToLeave )
            {
                command.addGroupToRemoveFrom( groupToLeave.getGroupKey() );
            }

            List<GroupEntity> groupsLeft = userStoreService.removeMembershipsFromGroup( command );

            GroupXmlCreator xmlCreator = new GroupXmlCreator();
            xmlCreator.setAdminConsoleStyle( false );
            return xmlCreator.createGroupsDocument( groupsLeft, false, false );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document createGroup( CreateGroupParams params )
    {
        try
        {
            if ( params.name == null )
            {
                throw new IllegalArgumentException( "name must be specified" );
            }
            if ( params.userStore == null )
            {
                throw new IllegalArgumentException( "UserStore must be specified" );
            }

            final UserStoreEntity userStore = userStoreParser.parseUserStore( params.userStore );

            UserEntity runningUser = securityService.getRunAsUser();

            StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
            storeNewGroupCommand.setName( params.name );
            storeNewGroupCommand.setRestriced( params.restricted );
            storeNewGroupCommand.setExecutor( runningUser );
            storeNewGroupCommand.setDescription( params.description );
            storeNewGroupCommand.setUserStoreKey( userStore.getKey() );
            storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
            storeNewGroupCommand.setRespondWithException( false );

            GroupKey createdGroupKey = userStoreService.storeNewGroup( storeNewGroupCommand );

            GroupXmlCreator xmlCreator = new GroupXmlCreator();
            xmlCreator.setAdminConsoleStyle( false );
            if ( createdGroupKey == null )
            {
                return xmlCreator.createEmptyGroupDocument();
            }
            else
            {
                GroupEntity createdGroup = groupDao.findByKey( createdGroupKey );
                return xmlCreator.createGroupDocument( createdGroup, false, false, false );
            }
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteGroup( DeleteGroupParams params )
            throws ClientException
    {
        try
        {
            if ( params.group == null )
            {
                throw new IllegalArgumentException( "group must be specified" );
            }

            GroupEntity group = parseGroup( params.group );

            UserEntity runningUser = securityService.getRunAsUser();

            GroupSpecification groupSpec = new GroupSpecification();
            groupSpec.setKey( group.getGroupKey() );
            groupSpec.setName( group.getName() );

            DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand( runningUser, groupSpec );
            deleteGroupCommand.setRespondWithException( true );

            userStoreService.deleteGroup( deleteGroupCommand );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Deprecated
    public String getRunAsUser()
            throws ClientException
    {
        return doGetRunAsUserName();
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String getRunAsUserName()
            throws ClientException
    {
        return doGetRunAsUserName();
    }

    private String doGetRunAsUserName()
    {
        try
        {
            UserEntity runAsUser = this.securityService.getRunAsUser();

            Assert.isTrue( runAsUser != null );

            return runAsUser.getName();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getUserContext()
            throws ClientException
    {
        try
        {
            final UserEntity userEntity = securityService.getLoggedInPortalUserAsEntity();
            UserContextXmlCreator userContextXmlCreator = new UserContextXmlCreator( groupDao );
            return userContextXmlCreator.createUserDocument( userEntity );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getRunAsUserContext()
            throws ClientException
    {
        try
        {
            final UserEntity userEntity = securityService.getRunAsUser();
            UserContextXmlCreator userContextXmlCreator = new UserContextXmlCreator( groupDao );
            return userContextXmlCreator.createUserDocument( userEntity );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String login( String user, String password )
            throws ClientException
    {
        try
        {
            this.securityService.loginClientApiUser( QualifiedUsername.parse( user ), password );
            return this.securityService.getUserName();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String impersonate( String user )
            throws ClientException
    {
        try
        {
            UserEntity impersonated = this.securityService.impersonate( QualifiedUsername.parse( user ) );

            Assert.isTrue( impersonated != null );

            return impersonated.getName();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String logout()
    {
        return logout( true );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String logout( boolean invalidateSession )
    {
        try
        {
            String userName = this.securityService.getUserName();
            this.securityService.logoutClientApiUser( invalidateSession );
            return userName;
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    private synchronized PresentationInvoker getPresentationInvoker()
    {
        if ( this.invoker == null )
        {
            this.invoker = new PresentationInvoker( this.presentationService, this.dataSourceService, securityService );
        }

        return this.invoker;
    }

    private ClientException handleException( Exception e )
    {
        if ( e instanceof ClientException )
        {
            return (ClientException) e;
        }
        else
        {
            LOG.error( "ClientException occured", e );
            return new ClientException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createCategory( CreateCategoryParams params )
    {

        try
        {
            return internalClientContentService.createCategory( params );
        }
        catch ( ClientException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createContent( CreateContentParams params )
            throws ClientException
    {
        try
        {
            return internalClientContentService.createContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateContent( UpdateContentParams params )
    {
        try
        {
            return internalClientContentService.updateContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void assignContent( AssignContentParams params )
    {
        try
        {
            internalClientContentService.assignContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unassignContent( UnassignContentParams params )
    {
        try
        {
            internalClientContentService.unassignContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void snapshotContent( SnapshotContentParams params )
    {
        try
        {
            internalClientContentService.snapshotContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createFileContent( CreateFileContentParams params )
    {
        try
        {
            return internalClientContentService.createFileContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateFileContent( UpdateFileContentParams params )
    {
        try
        {
            return internalClientContentService.updateFileContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteContent( DeleteContentParams params )
    {

        try
        {
            if ( params.contentKey == null )
            {
                throw new IllegalArgumentException( "contentKey must be specified" );
            }

            internalClientContentService.deleteContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getCategories( GetCategoriesParams params )
            throws ClientException
    {
        try
        {
            return getPresentationInvoker().getCategories( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getContent( GetContentParams params )
            throws ClientException
    {
        try
        {
            UserEntity user = securityService.getRunAsUser();

            GetContentExecutor executor =
                    new GetContentExecutor( contentService, contentDao, userDao, timeService.getNowAsDateTime(),
                                            previewService.getPreviewContext() );
            executor.user( user.getKey() );
            executor.query( params.query );
            executor.orderBy( params.orderBy );
            executor.index( params.index );
            executor.count( params.count );
            if ( params.includeOfflineContent )
            {
                executor.includeOfflineContent();
                executor.includeOfflineRelatedContent();
            }
            executor.contentFilter( ContentKey.convertToList( params.contentKeys ) );
            executor.childrenLevel( params.childrenLevel );
            executor.parentLevel( params.parentLevel );
            executor.parentChildrenLevel( 0 );

            GetContentResult getContentResult = executor.execute();

            GetContentXmlCreator getContentXmlCreator =
                    new GetContentXmlCreator( new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );

            getContentXmlCreator.user( user );
            getContentXmlCreator.startingIndex( params.index );
            getContentXmlCreator.resultLength( params.count );
            getContentXmlCreator.includeContentsContentData( params.includeData );
            getContentXmlCreator.includeRelatedContentsContentData( params.includeData );
            getContentXmlCreator.includeUserRights( params.includeUserRights );
            getContentXmlCreator.versionInfoStyle( GetContentXmlCreator.VersionInfoStyle.CLIENT );

            XMLDocument xml = getContentXmlCreator.create( getContentResult );
            return xml.getAsJDOMDocument();
        }
        catch ( XMLException e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getContentVersions( GetContentVersionsParams params )
    {
        try
        {
            if ( params == null || params.contentVersionKeys.length == 0 )
            {
                throw new IllegalArgumentException( "Missing one or more versionkeys" );
            }
            final Date now = new Date();
            UserEntity user = securityService.getRunAsUser();

            List<ContentVersionEntity> versions = new ArrayList<ContentVersionEntity>( params.contentVersionKeys.length );
            ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );
            for ( int versionKey : params.contentVersionKeys )
            {
                ContentVersionKey key = new ContentVersionKey( versionKey );
                ContentVersionEntity version = contentVersionDao.findByKey( key );
                if ( version == null )
                {
                    continue;
                }

                final boolean mainVersionOnlineCheckOK =
                        !params.contentRequiredToBeOnline || version.getContent().isOnline( now );
                final boolean accessCheckOK = contentAccessResolver.hasReadContentAccess( user, version.getContent() );
                if ( mainVersionOnlineCheckOK && accessCheckOK )
                {
                    versions.add( version );
                }
            }

            RelatedChildrenContentQuery spec = new RelatedChildrenContentQuery( now );
            spec.setChildrenLevel( params.childrenLevel );
            spec.setContentVersions( versions );
            spec.setUser( user );
            spec.setIncludeOffline();

            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( spec );

            ContentXMLCreator xmlCreator = new ContentXMLCreator();

            xmlCreator.setIncludeVersionsInfoForClient( true );
            xmlCreator.setIncludeAccessRightsInfo( true );
            xmlCreator.setIncludeUserRightsInfo( true, new CategoryAccessResolver( groupDao ), contentAccessResolver );
            xmlCreator.setIncludeOwnerAndModifierData( true );
            xmlCreator.setIncludeContentData( true );
            xmlCreator.setIncludeCategoryData( true );
            xmlCreator.setOnlineCheckDate( now );
            xmlCreator.setIncludeAssignment( true );

            return xmlCreator.createContentVersionsDocument( user, versions, relatedContent ).getAsJDOMDocument();

        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getContentByQuery( GetContentByQueryParams params )
            throws ClientException
    {
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            final Date now = new Date();
            UserEntity user = securityService.getRunAsUser();
            ContentXMLCreator xmlCreator = new ContentXMLCreator();
            ContentByQueryQuery spec = new ContentByQueryQuery();
            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );

            spec.setUser( user );
            spec.setQuery( params.query );
            spec.setOrderBy( params.orderBy );
            spec.setCount( params.count );
            spec.setIndex( params.index );
            if ( params.includeOfflineContent )
            {
                spec.setFilterIncludeOfflineContent();
                relatedContentQuery.setFilterIncludeOfflineContent();
            }
            else
            {
                spec.setFilterContentOnlineAt( now );
                relatedContentQuery.setFilterContentOnlineAt( now );
            }

            ContentResultSet contents = contentService.queryContent( spec );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( params.parentLevel );
            relatedContentQuery.setChildrenLevel( params.childrenLevel );
            relatedContentQuery.setParentChildrenLevel( 0 );
            relatedContentQuery.setIncludeOnlyMainVersions( true );

            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
            }

            xmlCreator.setIncludeContentData( params.includeData );
            xmlCreator.setIncludeRelatedContentData( params.includeData );
            xmlCreator.setResultIndexing( params.index, params.count );
            xmlCreator.setIncludeOwnerAndModifierData( true );
            xmlCreator.setIncludeUserRightsInfo( params.includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForClient( true );
            xmlCreator.setIncludeAssignment( true );

            return xmlCreator.createContentsDocument( user, contents, relatedContent ).getAsJDOMDocument();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getContentByCategory( GetContentByCategoryParams params )
            throws ClientException
    {
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            final Date now = new Date();
            UserEntity user = securityService.getRunAsUser();
            ContentXMLCreator xmlCreator = new ContentXMLCreator();
            ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );

            contentByCategoryQuery.setUser( user );
            contentByCategoryQuery.setQuery( params.query );
            contentByCategoryQuery.setOrderBy( params.orderBy );
            contentByCategoryQuery.setCount( params.count );
            contentByCategoryQuery.setIndex( params.index );
            if ( params.includeOfflineContent )
            {
                contentByCategoryQuery.setFilterIncludeOfflineContent();
                relatedContentQuery.setFilterIncludeOfflineContent();
            }
            else
            {
                contentByCategoryQuery.setFilterContentOnlineAt( now );
                relatedContentQuery.setFilterContentOnlineAt( now );
            }
            contentByCategoryQuery.setCategoryKeyFilter( CategoryKey.convertToList( params.categoryKeys ), params.levels );

            ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( params.parentLevel );
            relatedContentQuery.setChildrenLevel( params.childrenLevel );
            relatedContentQuery.setParentChildrenLevel( 0 );
            relatedContentQuery.setIncludeOnlyMainVersions( true );

            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
            }

            xmlCreator.setIncludeContentData( params.includeData );
            xmlCreator.setIncludeRelatedContentData( params.includeData );
            xmlCreator.setResultIndexing( params.index, params.count );
            xmlCreator.setIncludeUserRightsInfo( params.includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForClient( true );
            xmlCreator.setIncludeAssignment( true );

            return xmlCreator.createContentsDocument( user, contents, relatedContent ).getAsJDOMDocument();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getRandomContentByCategory( GetRandomContentByCategoryParams params )
            throws ClientException
    {
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            UserEntity user = securityService.getRunAsUser();
            ContentXMLCreator xmlCreator = new ContentXMLCreator();
            final Date now = new Date();
            ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );

            contentByCategoryQuery.setUser( user );
            contentByCategoryQuery.setIndex( 0 );
            contentByCategoryQuery.setCount( Integer.MAX_VALUE );
            contentByCategoryQuery.setQuery( params.query );
            if ( params.includeOfflineContent )
            {
                contentByCategoryQuery.setFilterIncludeOfflineContent();
                relatedContentQuery.setFilterIncludeOfflineContent();
            }
            else
            {
                contentByCategoryQuery.setFilterContentOnlineAt( now );
                relatedContentQuery.setFilterContentOnlineAt( now );
            }
            contentByCategoryQuery.setCategoryKeyFilter( CategoryKey.convertToList( params.categoryKeys ), params.levels );

            ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            ContentResultSet randomContents = contents.createRandomizedResult( params.count );
            RelatedContentResultSet relatedContent;
            if ( params.parentLevel > 0 || params.childrenLevel > 0 )
            {
                relatedContentQuery.setUser( user );
                relatedContentQuery.setContentResultSet( randomContents );
                relatedContentQuery.setParentLevel( params.parentLevel );
                relatedContentQuery.setChildrenLevel( params.childrenLevel );
                relatedContentQuery.setParentChildrenLevel( 0 );
                relatedContentQuery.setIncludeOnlyMainVersions( true );

                relatedContent = contentService.queryRelatedContent( relatedContentQuery );
                if ( previewContext.isPreviewingContent() )
                {
                    relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
                }
            }
            else
            {
                relatedContent = new RelatedContentResultSetImpl();
            }

            xmlCreator.setResultIndexing( 0, params.count );
            xmlCreator.setIncludeRelatedContentData( params.includeData );
            xmlCreator.setIncludeContentData( params.includeData );
            xmlCreator.setIncludeUserRightsInfo( params.includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForClient( true );
            xmlCreator.setIncludeAssignment( true );

            return xmlCreator.createContentsDocument( user, randomContents, relatedContent ).getAsJDOMDocument();
        }
        catch ( XMLException e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getContentBySection( GetContentBySectionParams params )
            throws ClientException
    {
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            UserEntity user = securityService.getRunAsUser();
            final Date now = new Date();
            ContentXMLCreator xmlCreator = new ContentXMLCreator();
            ContentBySectionQuery spec = new ContentBySectionQuery();
            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );

            spec.setMenuItemKeys( MenuItemKey.converToList( params.menuItemKeys ) );
            spec.setUser( user );
            spec.setSectionFilterStatus( SectionFilterStatus.APPROVED_ONLY );
            spec.setLevels( params.levels );
            spec.setIndex( params.index );
            spec.setCount( params.count );
            spec.setQuery( params.query );
            spec.setOrderBy( params.orderBy );
            if ( params.includeOfflineContent )
            {
                spec.setFilterIncludeOfflineContent();
                relatedContentQuery.setFilterIncludeOfflineContent();
            }
            else
            {
                spec.setFilterContentOnlineAt( now );
                relatedContentQuery.setFilterContentOnlineAt( now );
            }

            xmlCreator.setIncludeContentData( params.includeData );
            xmlCreator.setIncludeRelatedContentData( params.includeData );
            xmlCreator.setResultIndexing( params.index, params.count );
            xmlCreator.setIncludeUserRightsInfo( params.includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForClient( true );
            xmlCreator.setIncludeAssignment( true );

            ContentResultSet contents = contentService.queryContent( spec );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( params.parentLevel );
            relatedContentQuery.setChildrenLevel( params.childrenLevel );
            relatedContentQuery.setParentChildrenLevel( 0 );
            relatedContentQuery.setIncludeOnlyMainVersions( true );

            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            return xmlCreator.createContentsDocument( user, contents, relatedContents ).getAsJDOMDocument();
        }
        catch ( XMLException e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getRandomContentBySection( GetRandomContentBySectionParams params )
            throws ClientException
    {
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            UserEntity user = securityService.getRunAsUser();
            final Date now = new Date();
            ContentXMLCreator xmlCreator = new ContentXMLCreator();
            ContentBySectionQuery spec = new ContentBySectionQuery();
            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );

            spec.setMenuItemKeys( MenuItemKey.converToList( params.menuItemKeys ) );
            spec.setUser( user );
            spec.setSectionFilterStatus( SectionFilterStatus.APPROVED_ONLY );
            spec.setLevels( params.levels );
            spec.setIndex( 0 );
            spec.setCount( Integer.MAX_VALUE );
            spec.setQuery( params.query );
            if ( params.includeOfflineContent )
            {
                spec.setFilterIncludeOfflineContent();
                relatedContentQuery.setFilterIncludeOfflineContent();
            }
            else
            {
                spec.setFilterContentOnlineAt( now );
                relatedContentQuery.setFilterContentOnlineAt( now );
            }

            ContentResultSet contents = contentService.queryContent( spec );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }
            ContentResultSet randomContents = contents.createRandomizedResult( params.count );

            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( randomContents );
            relatedContentQuery.setParentLevel( params.parentLevel );
            relatedContentQuery.setChildrenLevel( params.childrenLevel );
            relatedContentQuery.setParentChildrenLevel( 0 );
            relatedContentQuery.setIncludeOnlyMainVersions( true );

            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
            }

            xmlCreator.setIncludeContentData( params.includeData );
            xmlCreator.setIncludeRelatedContentData( params.includeData );
            xmlCreator.setResultIndexing( 0, params.count );
            xmlCreator.setIncludeUserRightsInfo( params.includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForClient( true );
            xmlCreator.setIncludeAssignment( true );

            return xmlCreator.createContentsDocument( user, randomContents, relatedContent ).getAsJDOMDocument();
        }
        catch ( XMLException e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getMenu( GetMenuParams params )
            throws ClientException
    {
        try
        {
            return getPresentationInvoker().getMenu( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getMenuBranch( GetMenuBranchParams params )
            throws ClientException
    {
        try
        {
            return getPresentationInvoker().getMenuBranch( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getMenuData( GetMenuDataParams params )
            throws ClientException
    {
        try
        {
            return getPresentationInvoker().getMenuData( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getMenuItem( GetMenuItemParams params )
            throws ClientException
    {
        try
        {
            return getPresentationInvoker().getMenuItem( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getSubMenu( GetSubMenuParams params )
            throws ClientException
    {
        try
        {
            return getPresentationInvoker().getSubMenu( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getRelatedContent( final GetRelatedContentsParams params )
            throws ClientException
    {
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            final Date now = new Date();
            UserEntity user = securityService.getRunAsUser();

            // Get given content
            final ContentByContentQuery baseContentQuery = new ContentByContentQuery();
            baseContentQuery.setContentKeyFilter( ContentKey.convertToList( params.contentKeys ) );
            baseContentQuery.setUser( user );
            if ( params.includeOfflineContent )
            {
                baseContentQuery.setFilterIncludeOfflineContent();
            }
            else
            {
                baseContentQuery.setFilterContentOnlineAt( now );
            }
            ContentResultSet baseContent = contentService.queryContent( baseContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                baseContent = previewContext.getContentPreviewContext().applyPreviewedContentOnContentResultSet( baseContent,
                                                                                                                 params.contentKeys );
            }

            // Get the main content (related content to base content)
            final RelatedContentQuery relatedContentToBaseContentSpec = new RelatedContentQuery( now );
            relatedContentToBaseContentSpec.setUser( user );
            relatedContentToBaseContentSpec.setContentResultSet( baseContent );
            relatedContentToBaseContentSpec.setParentLevel( params.relation < 0 ? 1 : 0 );
            relatedContentToBaseContentSpec.setChildrenLevel( params.relation > 0 ? 1 : 0 );
            relatedContentToBaseContentSpec.setParentChildrenLevel( 0 );
            relatedContentToBaseContentSpec.setIncludeOnlyMainVersions( true );
            if ( params.includeOfflineContent )
            {
                relatedContentToBaseContentSpec.setFilterIncludeOfflineContent();
            }
            else
            {
                relatedContentToBaseContentSpec.setFilterContentOnlineAt( now );
            }
            final RelatedContentResultSet relatedContentToBaseContent = contentService.queryRelatedContent( relatedContentToBaseContentSpec );

            final boolean previewedContentIsAmongBaseContent = previewContext.isPreviewingContent() &&
                    baseContent.containsContent( previewContext.getContentPreviewContext().getContentPreviewed().getKey() );
            if ( previewedContentIsAmongBaseContent )
            {
                // ensuring offline related content to the previewed content to be included when previewing
                RelatedContentQuery relatedSpecForPreviewedContent = new RelatedContentQuery( relatedContentToBaseContentSpec );
                relatedSpecForPreviewedContent.setFilterIncludeOfflineContent();
                relatedSpecForPreviewedContent.setContentResultSet( new ContentResultSetNonLazy( previewContext.getContentPreviewContext().getContentAndVersionPreviewed().getContent() ) );

                RelatedContentResultSet relatedContentsForPreviewedContent = contentService.queryRelatedContent( relatedSpecForPreviewedContent );

                relatedContentToBaseContent.overwrite( relatedContentsForPreviewedContent );
                previewContext.getContentPreviewContext().registerContentToBeAvailableOnline(
                        relatedContentToBaseContent );
            }

            // Get the main result content
            final ContentByContentQuery mainResultContentQuery = new ContentByContentQuery();
            mainResultContentQuery.setUser( user );
            mainResultContentQuery.setQuery( params.query );
            mainResultContentQuery.setOrderBy( params.orderBy );
            mainResultContentQuery.setIndex( params.index );
            mainResultContentQuery.setCount( params.count );
            mainResultContentQuery.setContentKeyFilter( relatedContentToBaseContent.getContentKeys() );
            if ( params.includeOfflineContent || previewContext.isPreviewingContent() )
            {
                mainResultContentQuery.setFilterIncludeOfflineContent();
            }
            else
            {
                mainResultContentQuery.setFilterContentOnlineAt( now );
            }
            ContentResultSet mainResultContent = contentService.queryContent( mainResultContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                mainResultContent = previewContext.getContentPreviewContext().overrideContentResultSet( mainResultContent );
                previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( mainResultContent );
            }

            // Get the related content of the top level content
            final RelatedContentQuery relatedContentSpec = new RelatedContentQuery( now );
            relatedContentSpec.setUser( user );
            relatedContentSpec.setContentResultSet( mainResultContent );
            relatedContentSpec.setParentLevel( params.parentLevel );
            relatedContentSpec.setChildrenLevel( params.childrenLevel );
            relatedContentSpec.setParentChildrenLevel( 0 );
            relatedContentSpec.setIncludeOnlyMainVersions( true );
            if ( params.includeOfflineContent || previewContext.isPreviewingContent() )
            {
                relatedContentSpec.setFilterIncludeOfflineContent();
            }
            else
            {
                relatedContentSpec.setFilterContentOnlineAt( now );
            }
            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentSpec );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
                previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( relatedContent );
            }

            // Create the content xml
            final ContentXMLCreator xmlCreator = new ContentXMLCreator();
            xmlCreator.setResultIndexing( params.index, params.count );
            xmlCreator.setIncludeContentData( params.includeData );
            xmlCreator.setIncludeRelatedContentData( params.includeData );
            xmlCreator.setIncludeUserRightsInfo( params.includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForClient( true );
            xmlCreator.setIncludeAssignment( true );

            return xmlCreator.createContentsDocument( user, mainResultContent, relatedContent ).getAsJDOMDocument();
        }
        catch ( XMLException e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document renderContent( RenderContentParams params )
            throws ClientException
    {
        try
        {
            return internalClientRenderService.renderContent( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document renderPage( RenderPageParams params )
            throws ClientException
    {
        try
        {
            return internalClientRenderService.renderPage( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getBinary( GetBinaryParams params )
            throws ClientException
    {
        try
        {
            return getPresentationInvoker().getBinary( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getContentBinary( GetContentBinaryParams params )
            throws ClientException
    {
        try
        {
            return getPresentationInvoker().getContentBinary( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getResource( GetResourceParams params )
            throws ClientException
    {
        try
        {
            ResourceKey resourceKey = new ResourceKey( params.resourcePath );
            ResourceFile resourceFile = resourceService.getResourceFile( resourceKey );
            if ( resourceFile == null )
            {
                return null;
            }
            ResourceXmlCreator xmlCreator = new ResourceXmlCreator();
            xmlCreator.setIncludeData( params.includeData );
            xmlCreator.setIncludeHidden( true );
            if ( params.includeUsedBy )
            {
                xmlCreator.setUsedByMap( this.resourceService.getUsedBy( resourceFile.getResourceKey() ) );
            }
            return xmlCreator.createResourceXml( resourceFile ).getAsJDOMDocument();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document importContents( final ImportContentsParams params )
            throws ClientException
    {
        try
        {
            final CategoryEntity categoryToImportTo = categoryDao.findByKey( new CategoryKey( params.categoryKey ) );
            if ( categoryToImportTo == null )
            {
                throw new IllegalArgumentException( "Category does not exist " + params.categoryKey );
            }

            final ImportContentCommand command = new ImportContentCommand();
            command.importer = this.securityService.getRunAsUser();
            command.categoryToImportTo = categoryToImportTo;
            command.importName = params.importName;
            command.publishFrom = params.publishFrom == null ? null : new DateTime( params.publishFrom );
            command.publishTo = params.publishTo == null ? null : new DateTime( params.publishTo );
            command.inputStream = new ByteArrayInputStream( params.data.getBytes( "UTF-8" ) );

            String assigneeParamKey = params.assignee;

            if ( StringUtils.isNotBlank( assigneeParamKey ) )
            {
                final UserEntity assignee = userParser.parseUser( params.assignee );

                if ( assignee == null )
                {
                    throw new IllegalArgumentException( "Not able to find assignee with key: " + assigneeParamKey );
                }

                command.assigneeKey = assignee.getKey();
                command.assignmentDescription = params.assignmentDescription;
                command.assignmentDueDate = params.assignmentDueDate;
            }

            final ImportJob importJob = importJobFactory.createImportJob( command );
            final ImportResult report = importJob.start();

            final ImportResultXmlCreator reportCreator = new ImportResultXmlCreator();
            reportCreator.setIncludeContentInformation( true );
            return reportCreator.getReport( report ).getAsJDOMDocument();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Preference getPreference( GetPreferenceParams params )
            throws ClientException
    {
        try
        {
            PreferenceKey preferenceKey =
                    new PreferenceKey( securityService.getRunAsUser().getKey(), PreferenceScopeType.parse( params.scope.getType().toString() ),
                                       params.scope.getKey() != null ? new PreferenceScopeKey( params.scope.getKey() ) : null, params.key );

            PreferenceEntity preferenceEntity = preferenceService.getPreference( preferenceKey );

            return new Preference( params.scope, params.key,
                                   preferenceEntity != null ? preferenceEntity.getValue() : null );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<Preference> getPreferences()
            throws ClientException
    {
        try
        {
            PreferenceSpecification spec = new PreferenceSpecification( this.securityService.getRunAsUser() );
            List<PreferenceEntity> preferenceList = preferenceService.getPreferences( spec );
            List<Preference> preferences = new ArrayList<Preference>();
            for ( PreferenceEntity preference : preferenceList )
            {
                final PreferenceKey preferenceKey = preference.getKey();
                final PreferenceScope prefrenceScope = new PreferenceScope( preferenceKey.getScopeType(), preferenceKey.getScopeKey() );
                preferences.add(
                        new Preference( PreferenceScopeResolver.resolveClientScope( prefrenceScope ), preference.getKey().getBaseKey(),
                                        preference.getValue() ) );
            }
            return preferences;

        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void setPreference( SetPreferenceParams params )
            throws ClientException
    {
        if ( params.scope == null )
        {
            throw new IllegalArgumentException( "Scope cannot be null" );
        }

        try
        {
            PreferenceKey preferenceKey =
                    new PreferenceKey( securityService.getRunAsUser().getKey(), PreferenceScopeType.parse( params.scope.getType().toString() ),
                                       params.scope.getKey() != null ? new PreferenceScopeKey( params.scope.getKey() ) : null, params.key );

            PreferenceEntity preference = new PreferenceEntity();
            preference.setKey( preferenceKey );
            preference.setValue( params.value );
            preferenceService.setPreference( preference );

        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deletePreference( DeletePreferenceParams params )
            throws ClientException
    {
        try
        {
            PreferenceKey preferenceKey =
                    new PreferenceKey( securityService.getRunAsUser().getKey(), PreferenceScopeType.parse( params.scope.getType().toString() ),
                                       params.scope.getKey() != null ? new PreferenceScopeKey( params.scope.getKey() ) : null, params.key );

            PreferenceEntity preference = new PreferenceEntity();
            preference.setKey( preferenceKey );
            preference.setValue( "" );
            preferenceService.removePreference( preference );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    public void clearPageCacheForSite( Integer siteKeyInt )
    {
        try
        {
            UserEntity runningUser = securityService.getRunAsUser();
            if ( !( runningUser.isEnterpriseAdmin() || runningUser.isAdministrator() ) )
            {
                throw new IllegalAccessException(
                        "User " + runningUser.getQualifiedName() + " do not have access to this operation" );
            }

            SiteKey siteKey = new SiteKey( siteKeyInt );
            final PageCacheService pageCache = siteCachesService.getPageCacheService( siteKey );
            if ( pageCache != null )
            {
                pageCache.removeEntriesBySite();
            }
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    public void clearPageCacheForPage( Integer siteKeyInt, Integer[] menuItemKeys )
    {
        try
        {
            if ( siteKeyInt == null )
            {
                throw new IllegalArgumentException( "siteKey cannot be null" );
            }

            if ( menuItemKeys == null )
            {
                throw new IllegalArgumentException( "menuItemKeys cannot be null" );
            }

            UserEntity runningUser = securityService.getRunAsUser();
            if ( !( runningUser.isEnterpriseAdmin() || runningUser.isAdministrator() ) )
            {
                throw new IllegalAccessException(
                        "User " + runningUser.getQualifiedName() + " do not have access to this operation" );
            }

            for ( Integer menuItemKeyInt : menuItemKeys )
            {
                MenuItemKey menuItemKey = new MenuItemKey( menuItemKeyInt );

                SiteKey siteKey = new SiteKey( siteKeyInt );
                final PageCacheService pageCache = siteCachesService.getPageCacheService( siteKey );
                if ( pageCache != null )
                {
                    pageCache.removeEntriesByMenuItem( menuItemKey );
                }
            }
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    public void clearPageCacheForContent( Integer[] contentKeys )
    {
        try
        {
            UserEntity runningUser = securityService.getRunAsUser();
            if ( !( runningUser.isEnterpriseAdmin() || runningUser.isAdministrator() ) )
            {
                throw new IllegalAccessException(
                        "User " + runningUser.getQualifiedName() + " do not have access to this operation" );
            }

            for ( Integer contentKeyInt : contentKeys )
            {
                ContentKey contentKey = new ContentKey( contentKeyInt );
                ContentEntity content = contentDao.findByKey( contentKey );
                if ( content != null )
                {
                    new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContent( content );
                }
            }
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    private GroupEntity parseGroup( String string )
    {

        if ( string == null )
        {
            return null;
        }

        GroupEntity group;

        if ( string.indexOf( ":" ) > 0 || string.indexOf( "#" ) == -1 )
        {
            QualifiedGroupname qualfifiedName = parseQualifiedGroupname( string );
            group = this.securityService.getGroup( qualfifiedName );

            if ( group == null )
            {
                throw new GroupNotFoundException( qualfifiedName );
            }
        }
        else
        {
            // #F3F2A4343
            GroupKey groupKey = new GroupKey( string );
            group = this.securityService.getGroup( groupKey );

            if ( group == null )
            {
                throw new GroupNotFoundException( groupKey );
            }
        }

        return group;
    }

    private List<GroupEntity> parseGroups( String[] groups, boolean failOnNotFound )
    {

        List<GroupEntity> groupEntities = new ArrayList<GroupEntity>();
        for ( int i = 0; i < groups.length; i++ )
        {

            String group = groups[i];

            if ( group == null )
            {
                throw new IllegalArgumentException( "Given group at position " + i + " was null" );
            }
            // noinspection CaughtExceptionImmediatelyRethrown
            try
            {
                GroupEntity groupEntity = parseGroup( group );
                groupEntities.add( groupEntity );
            }
            catch ( GroupNotFoundException e )
            {
                if ( failOnNotFound )
                {
                    throw e;
                }
            }
            catch ( UserStoreNotFoundException e )
            {
                if ( failOnNotFound )
                {
                    throw e;
                }
            }
        }
        return groupEntities;
    }

    private QualifiedGroupname parseQualifiedGroupname( String string )
            throws UserStoreNotFoundException
    {

        if ( string == null )
        {
            return null;
        }

        QualifiedGroupname qualifiedGroupname = QualifiedGroupname.parse( string );

        UserStoreEntity userStore = null;
        if ( qualifiedGroupname.getUserStoreKey() != null )
        {
            userStore = userStoreDao.findByKey( qualifiedGroupname.getUserStoreKey() );
            if ( userStore == null )
            {
                throw new UserStoreNotFoundException( qualifiedGroupname.getUserStoreKey() );
            }
        }
        else
        {
            if ( qualifiedGroupname.getUserStoreName() != null )
            {
                userStore = userStoreDao.findByName( qualifiedGroupname.getUserStoreName() );
                if ( userStore == null && qualifiedGroupname.isUserStoreLocal() )
                {
                    throw new UserStoreNotFoundException( qualifiedGroupname.getUserStoreName() );
                }
            }
            else
            {
                if ( !qualifiedGroupname.isGlobal() )
                {
                    throw new IllegalArgumentException(
                            "Either UserStore key or UserStore name must be specified when group is not global." );
                }
            }
        }
        if ( userStore != null )
        {
            qualifiedGroupname.setUserStoreKey( userStore.getKey() );
        }

        return qualifiedGroupname;
    }

    private List<GroupType> parseGroupTypes( String[] groupTypes )
    {

        if ( groupTypes == null || groupTypes.length == 0 )
        {
            return null;
        }

        List<GroupType> list = new ArrayList<GroupType>();
        for ( String stringValue : groupTypes )
        {
            GroupType groupType = GroupType.get( stringValue );
            if ( groupType == null )
            {
                throw new IllegalArgumentException( "Given groupType does not exist: " + stringValue );
            }
            list.add( groupType );
        }
        return list;
    }

    public void setKeyService( KeyService value )
    {
        this.keyService = value;
    }

    public void setAdminService( AdminService value )
    {
        this.adminService = value;
    }

    public void setUserServicesService( UserServicesService value )
    {
        this.userServicesService = value;
    }

    public void setPresentationService( PresentationService value )
    {
        this.presentationService = value;
    }

    public void setResourceService( ResourceService value )
    {
        this.resourceService = value;
    }

    public void setPreferenceService( PreferenceService value )
    {
        this.preferenceService = value;
    }

    public void setDataSourceService( DataSourceService value )
    {
        this.dataSourceService = value;
    }

    public void setInternalClientRenderService( InternalClientRenderService value )
    {
        this.internalClientRenderService = value;
    }

    public void setUserParser( UserParser userParser )
    {
        this.userParser = userParser;
    }

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    public void setContentService( ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setUserStoreParser( UserStoreParser value )
    {
        this.userStoreParser = value;
    }

    public void setInternalClientContentService( InternalClientContentService internalClientContentService )
    {
        this.internalClientContentService = internalClientContentService;
    }

    @Autowired
    public void setMenuItemDao( MenuItemDao menuItemDao )
    {
        this.menuItemDao = menuItemDao;
    }

    public void setPreviewService( PreviewService previewService )
    {
        this.previewService = previewService;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    /**
     * Return the global configuration.
     */
    public Map<String, String> getConfiguration()
    {
        return toMap( this.cmsProperties );
    }

    /**
     * Return the configuration for a site.
     */
    public Map<String, String> getSiteConfiguration( int siteKey )
    {
        try
        {
            return toMap( this.sitePropertiesService.getSiteProperties( new SiteKey( siteKey ) ).getProperties() );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private Map<String, String> toMap( Properties props )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        Enumeration e = props.propertyNames();

        while ( e.hasMoreElements() )
        {
            String key = (String) e.nextElement();
            map.put( key, props.getProperty( key ) );
        }

        return map;
    }
}

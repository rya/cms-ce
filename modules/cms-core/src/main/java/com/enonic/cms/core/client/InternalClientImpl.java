/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

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

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLException;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.AssignContentParams;
import com.enonic.cms.api.client.model.CreateCategoryParams;
import com.enonic.cms.api.client.model.CreateContentParams;
import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.CreateGroupParams;
import com.enonic.cms.api.client.model.CreateImageContentParams;
import com.enonic.cms.api.client.model.CreateUserParams;
import com.enonic.cms.api.client.model.DeleteCategoryParams;
import com.enonic.cms.api.client.model.DeleteContentParams;
import com.enonic.cms.api.client.model.DeleteGroupParams;
import com.enonic.cms.api.client.model.DeletePreferenceParams;
import com.enonic.cms.api.client.model.DeleteUserParams;
import com.enonic.cms.api.client.model.GetBinaryParams;
import com.enonic.cms.api.client.model.GetCategoriesParams;
import com.enonic.cms.api.client.model.GetContentBinaryParams;
import com.enonic.cms.api.client.model.GetContentByCategoryParams;
import com.enonic.cms.api.client.model.GetContentByQueryParams;
import com.enonic.cms.api.client.model.GetContentBySectionParams;
import com.enonic.cms.api.client.model.GetContentParams;
import com.enonic.cms.api.client.model.GetContentTypeConfigXMLParams;
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
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.GetContentExecutor;
import com.enonic.cms.core.content.GetContentResult;
import com.enonic.cms.core.content.GetContentXmlCreator;
import com.enonic.cms.core.content.PageCacheInvalidatorForContent;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryService;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.category.command.DeleteCategoryCommand;
import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.imports.ImportJob;
import com.enonic.cms.core.content.imports.ImportJobFactory;
import com.enonic.cms.core.content.imports.ImportResult;
import com.enonic.cms.core.content.imports.ImportResultXmlCreator;
import com.enonic.cms.core.content.index.ContentIndexQuery.SectionFilterStatus;
import com.enonic.cms.core.content.query.ContentByCategoryQuery;
import com.enonic.cms.core.content.query.ContentByContentQuery;
import com.enonic.cms.core.content.query.ContentByQueryQuery;
import com.enonic.cms.core.content.query.ContentBySectionQuery;
import com.enonic.cms.core.content.query.RelatedChildrenContentQuery;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.core.portal.cache.SiteCachesService;
import com.enonic.cms.core.portal.datasource.context.UserContextXmlCreator;
import com.enonic.cms.core.portal.livetrace.ClientMethodExecutionTrace;
import com.enonic.cms.core.portal.livetrace.ClientMethodExecutionTracer;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.preference.PreferenceEntity;
import com.enonic.cms.core.preference.PreferenceKey;
import com.enonic.cms.core.preference.PreferenceScope;
import com.enonic.cms.core.preference.PreferenceScopeKey;
import com.enonic.cms.core.preference.PreferenceScopeResolver;
import com.enonic.cms.core.preference.PreferenceScopeType;
import com.enonic.cms.core.preference.PreferenceService;
import com.enonic.cms.core.preference.PreferenceSpecification;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.resource.ResourceXmlCreator;
import com.enonic.cms.core.security.ImpersonateCommand;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.UserParser;
import com.enonic.cms.core.security.UserStoreParser;
import com.enonic.cms.core.security.group.AddMembershipsCommand;
import com.enonic.cms.core.security.group.DeleteGroupCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupNotFoundException;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.GroupXmlCreator;
import com.enonic.cms.core.security.group.QualifiedGroupname;
import com.enonic.cms.core.security.group.RemoveMembershipsCommand;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.user.DeleteUserCommand;
import com.enonic.cms.core.security.user.DisplayNameResolver;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.user.UserXmlCreator;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreNotFoundException;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.GroupQuery;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

/**
 * This class implements the local client.
 */
public final class InternalClientImpl
    implements InternalClient
{

    private static final Logger LOG = LoggerFactory.getLogger( InternalClientImpl.class );

    private InternalClientContentService internalClientContentService;

    private InternalClientRenderService internalClientRenderService;

    private DataSourceService dataSourceService;

    private PresentationInvoker invoker;

    private SecurityService securityService;

    private TimeService timeService;

    @Autowired
    private UserStoreService userStoreService;

    private ContentService contentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImportJobFactory importJobFactory;

    private ResourceService resourceService;

    private PreferenceService preferenceService;

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

    private LivePortalTraceService livePortalTraceService;

    private boolean clientForRemoteInvocations;

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

    public void setContentTypeDao( ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
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
            return securityService.getLoggedInPortalUserAsEntity().getName();
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
            return securityService.getLoggedInPortalUserAsEntity().getName();
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
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getUser", livePortalTraceService );
        try
        {
            final UserEntity user =
                new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) ).parseUser( params.user );
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Document getGroup( GetGroupParams params )
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getGroup", livePortalTraceService );
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getUsers( GetUsersParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getUsers", livePortalTraceService );
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

            UserStoreEntity userStore = new UserStoreParser( userStoreDao ).parseUserStore( params.userStore );
            List<UserEntity> users =
                this.securityService.getUsers( userStore.getKey(), params.index, params.count, params.includeDeletedUsers );
            UserXmlCreator xmlCreator = new UserXmlCreator();
            xmlCreator.setIncludeUserFields( params.includeCustomUserFields );
            xmlCreator.wrappUserFieldsInBlockElement( false );
            xmlCreator.setAdminConsoleStyle( false );
            return xmlCreator.createUsersDocument( users, params.includeMemberships, params.normalizeGroups );

        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getGroups( GetGroupsParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getGroups", livePortalTraceService );
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
                UserStoreEntity userStore = new UserStoreParser( userStoreDao ).parseUserStore( params.userStore );
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
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
                UserEntity user =
                    new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) ).parseUser(
                        params.user );
                groupToUse = user.getUserGroup();
            }

            List<GroupEntity> groupsToJoin = parseGroups( params.groupsToJoin, true );
            UserEntity executor = securityService.getImpersonatedPortalUser();
            GroupSpecification groupSpec = new GroupSpecification();
            groupSpec.setKey( groupToUse.getGroupKey() );

            AddMembershipsCommand command = new AddMembershipsCommand( groupSpec, executor.getKey() );
            for ( GroupEntity groupToJoin : groupsToJoin )
            {
                command.addGroupToAddTo( groupToJoin.getGroupKey() );
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
                UserEntity user =
                    new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) ).parseUser(
                        params.user );
                groupToRemoveMembershipsFor = user.getUserGroup();
            }

            Collection<GroupEntity> groupsToLeave = parseGroups( params.groupsToLeave, true );
            UserEntity executor = securityService.getImpersonatedPortalUser();
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

            final UserStoreEntity userStore = new UserStoreParser( userStoreDao ).parseUserStore( params.userStore );

            UserEntity runningUser = securityService.getImpersonatedPortalUser();

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

            UserEntity runningUser = securityService.getImpersonatedPortalUser();

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
            UserEntity runAsUser = this.securityService.getImpersonatedPortalUser();

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
            final UserEntity userEntity = securityService.getImpersonatedPortalUser();
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
            return this.securityService.getLoggedInClientApiUserAsEntity().getName();
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
            UserEntity userToImpersonate =
                new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) ).parseUser( user );
            final UserEntity impersonated =
                securityService.impersonatePortalUser( new ImpersonateCommand( clientForRemoteInvocations, userToImpersonate.getKey() ) );
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
    public void removeImpersonation()
    {
        securityService.removePortalImpersonation();
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
            String userName = this.securityService.getLoggedInClientApiUserAsEntity().getName();
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
            this.invoker = new PresentationInvoker( this.dataSourceService, securityService );
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
    public String createUser( CreateUserParams params )
    {
        try
        {
            if ( StringUtils.isBlank( params.userstore ) )
            {
                throw new IllegalArgumentException( "userstore cannot be blank" );
            }
            if ( StringUtils.isBlank( params.username ) )
            {
                throw new IllegalArgumentException( "username cannot be blank" );
            }
            if ( StringUtils.isBlank( params.email ) )
            {
                throw new IllegalArgumentException( "email cannot be blank" );
            }
            if ( params.password == null )
            {
                throw new IllegalArgumentException( "password cannot be null" );
            }
            if ( params.userInfo == null )
            {
                throw new IllegalArgumentException( "userInfo cannot be null" );
            }

            UserEntity storer = securityService.getImpersonatedPortalUser();
            UserStoreEntity userStore = new UserStoreParser( userStoreDao ).parseUserStore( params.userstore );

            StoreNewUserCommand storeNewUserCommand = new StoreNewUserCommand();
            storeNewUserCommand.setUsername( params.username );
            storeNewUserCommand.setEmail( params.email );
            if ( params.displayName != null )
            {
                storeNewUserCommand.setDisplayName( params.displayName );
            }
            else
            {
                new DisplayNameResolver( userStore.getConfig() ).resolveDisplayName( params.username, params.displayName, params.userInfo );
            }
            storeNewUserCommand.setPassword( params.password );
            storeNewUserCommand.setUserInfo( params.userInfo );

            storeNewUserCommand.setType( UserType.NORMAL );
            storeNewUserCommand.setUserStoreKey( userStore.getKey() );
            storeNewUserCommand.setStorer( storer.getKey() );
            storeNewUserCommand.setAllowAnyUserAccess( false );

            return userStoreService.storeNewUser( storeNewUserCommand ).toString();
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUser( DeleteUserParams params )
    {
        try
        {
            if ( StringUtils.isBlank( params.user ) )
            {
                throw new IllegalArgumentException( "user cannot be blank" );
            }

            final UserEntity deleter = securityService.getImpersonatedPortalUser();
            final UserEntity user =
                new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) ).parseUser( params.user );

            final DeleteUserCommand deleteUserCommand =
                new DeleteUserCommand( deleter.getKey(), UserSpecification.usingKey( user.getKey() ) );
            userStoreService.deleteUser( deleteUserCommand );
        }
        catch ( Exception e )
        {
            throw handleException( e );
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createImageContent( CreateImageContentParams params )
        throws ClientException
    {
        try
        {
            return internalClientContentService.createImageContent( params );
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
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getCategories( GetCategoriesParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getCategories", livePortalTraceService );
        try
        {
            return getPresentationInvoker().getCategories( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getContent( GetContentParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getContent", livePortalTraceService );
        try
        {
            UserEntity user = securityService.getImpersonatedPortalUser();

            GetContentExecutor executor = new GetContentExecutor( contentService, contentDao, userDao, timeService.getNowAsDateTime(),
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
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getContentVersions( GetContentVersionsParams params )
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getContentVersions", livePortalTraceService );
        try
        {
            if ( params == null || params.contentVersionKeys.length == 0 )
            {
                throw new IllegalArgumentException( "Missing one or more versionkeys" );
            }
            final Date now = new Date();
            UserEntity user = securityService.getImpersonatedPortalUser();

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

                final boolean mainVersionOnlineCheckOK = !params.contentRequiredToBeOnline || version.getContent().isOnline( now );
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getContentByQuery( GetContentByQueryParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getContentByQuery", livePortalTraceService );
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            final Date now = new Date();
            UserEntity user = securityService.getImpersonatedPortalUser();
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getContentByCategory( GetContentByCategoryParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getContentByCategory", livePortalTraceService );
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            final Date now = new Date();
            UserEntity user = securityService.getImpersonatedPortalUser();
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getRandomContentByCategory( GetRandomContentByCategoryParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace =
            ClientMethodExecutionTracer.startTracing( "getRandomContentByCategory", livePortalTraceService );
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            UserEntity user = securityService.getImpersonatedPortalUser();
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getContentBySection( GetContentBySectionParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getContentBySection", livePortalTraceService );
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            UserEntity user = securityService.getImpersonatedPortalUser();
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getRandomContentBySection( GetRandomContentBySectionParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace =
            ClientMethodExecutionTracer.startTracing( "getRandomContentBySection", livePortalTraceService );
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            UserEntity user = securityService.getImpersonatedPortalUser();
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getMenu( GetMenuParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getMenu", livePortalTraceService );
        try
        {
            return getPresentationInvoker().getMenu( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getMenuBranch( GetMenuBranchParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getMenuBranch", livePortalTraceService );
        try
        {
            return getPresentationInvoker().getMenuBranch( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getMenuData( GetMenuDataParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getMenuData", livePortalTraceService );
        try
        {
            return getPresentationInvoker().getMenuData( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getMenuItem( GetMenuItemParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getMenuItem", livePortalTraceService );
        try
        {
            return getPresentationInvoker().getMenuItem( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getSubMenu( GetSubMenuParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getSubMenu", livePortalTraceService );
        try
        {
            return getPresentationInvoker().getSubMenu( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getRelatedContent( final GetRelatedContentsParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getRelatedContent", livePortalTraceService );
        try
        {
            PreviewContext previewContext = previewService.getPreviewContext();

            final Date now = new Date();
            UserEntity user = securityService.getImpersonatedPortalUser();

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
                baseContent =
                    previewContext.getContentPreviewContext().applyPreviewedContentOnContentResultSet( baseContent, params.contentKeys );
            }

            // Get the main content (related content to base content)
            final RelatedContentResultSet relatedContentToBaseContent;
            if ( params.requireAll && baseContent.getLength() > 1 )
            {
                relatedContentToBaseContent = contentService.getRelatedContentRequiresAll( user, params.relation, baseContent );
            }
            else
            {
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
                relatedContentToBaseContent = contentService.queryRelatedContent( relatedContentToBaseContentSpec );

                final boolean previewedContentIsAmongBaseContent = previewContext.isPreviewingContent() &&
                    baseContent.containsContent( previewContext.getContentPreviewContext().getContentPreviewed().getKey() );
                if ( previewedContentIsAmongBaseContent )
                {
                    // ensuring offline related content to the previewed content to be included when previewing
                    RelatedContentQuery relatedSpecForPreviewedContent = new RelatedContentQuery( relatedContentToBaseContentSpec );
                    relatedSpecForPreviewedContent.setFilterIncludeOfflineContent();
                    relatedSpecForPreviewedContent.setContentResultSet( new ContentResultSetNonLazy(
                        previewContext.getContentPreviewContext().getContentAndVersionPreviewed().getContent() ) );

                    RelatedContentResultSet relatedContentsForPreviewedContent =
                        contentService.queryRelatedContent( relatedSpecForPreviewedContent );

                    relatedContentToBaseContent.overwrite( relatedContentsForPreviewedContent );
                    previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( relatedContentToBaseContent );
                }
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
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
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

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getBinary( GetBinaryParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getBinary", livePortalTraceService );
        try
        {
            return internalClientContentService.getBinary( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getContentBinary( GetContentBinaryParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getContentBinary", livePortalTraceService );
        try
        {
            return internalClientContentService.getContentBinary( params );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getResource( GetResourceParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getResource", livePortalTraceService );
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
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
            command.importer = this.securityService.getImpersonatedPortalUser();
            command.categoryToImportTo = categoryToImportTo;
            command.importName = params.importName;
            command.publishFrom = params.publishFrom == null ? null : new DateTime( params.publishFrom );
            command.publishTo = params.publishTo == null ? null : new DateTime( params.publishTo );
            command.inputStream = new ByteArrayInputStream( params.data.getBytes( "UTF-8" ) );

            String assigneeParamKey = params.assignee;

            if ( StringUtils.isNotBlank( assigneeParamKey ) )
            {
                final UserEntity assignee =
                    new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) ).parseUser(
                        params.assignee );

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
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Preference getPreference( GetPreferenceParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getPreference", livePortalTraceService );
        try
        {
            PreferenceKey preferenceKey = new PreferenceKey( securityService.getImpersonatedPortalUser().getKey(),
                                                             PreferenceScopeType.parse( params.scope.getType().toString() ),
                                                             params.scope.getKey() != null
                                                                 ? new PreferenceScopeKey( params.scope.getKey() )
                                                                 : null, params.key );

            PreferenceEntity preferenceEntity = preferenceService.getPreference( preferenceKey );

            return new Preference( params.scope, params.key, preferenceEntity != null ? preferenceEntity.getValue() : null );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Preference> getPreferences()
        throws ClientException
    {
        final ClientMethodExecutionTrace trace = ClientMethodExecutionTracer.startTracing( "getPreferences", livePortalTraceService );
        try
        {
            PreferenceSpecification spec = new PreferenceSpecification( this.securityService.getImpersonatedPortalUser() );
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
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
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
            PreferenceKey preferenceKey = new PreferenceKey( securityService.getImpersonatedPortalUser().getKey(),
                                                             PreferenceScopeType.parse( params.scope.getType().toString() ),
                                                             params.scope.getKey() != null
                                                                 ? new PreferenceScopeKey( params.scope.getKey() )
                                                                 : null, params.key );

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
            PreferenceKey preferenceKey = new PreferenceKey( securityService.getImpersonatedPortalUser().getKey(),
                                                             PreferenceScopeType.parse( params.scope.getType().toString() ),
                                                             params.scope.getKey() != null
                                                                 ? new PreferenceScopeKey( params.scope.getKey() )
                                                                 : null, params.key );

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
            UserEntity runningUser = securityService.getImpersonatedPortalUser();
            if ( !( runningUser.isEnterpriseAdmin() || runningUser.isAdministrator() ) )
            {
                throw new IllegalAccessException( "User " + runningUser.getQualifiedName() + " do not have access to this operation" );
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

            UserEntity runningUser = securityService.getImpersonatedPortalUser();
            if ( !( runningUser.isEnterpriseAdmin() || runningUser.isAdministrator() ) )
            {
                throw new IllegalAccessException( "User " + runningUser.getQualifiedName() + " do not have access to this operation" );
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
            UserEntity runningUser = securityService.getImpersonatedPortalUser();
            if ( !( runningUser.isEnterpriseAdmin() || runningUser.isAdministrator() ) )
            {
                throw new IllegalAccessException( "User " + runningUser.getQualifiedName() + " do not have access to this operation" );
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

    public void deleteCategory( DeleteCategoryParams params )
        throws ClientException
    {
        try
        {
            if ( params.key == null )
            {
                throw new IllegalArgumentException( "key must be specified" );
            }

            UserEntity deleter = securityService.getImpersonatedPortalUser();

            DeleteCategoryCommand command = new DeleteCategoryCommand();
            command.setDeleter( deleter.getKey() );
            command.setCategoryKey( new CategoryKey( params.key ) );
            command.setIncludeContent( params.includeContent );
            command.setRecursive( params.recursive );
            categoryService.deleteCategory( command );
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

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    public void setContentService( ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setInternalClientContentService( InternalClientContentService internalClientContentService )
    {
        this.internalClientContentService = internalClientContentService;
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

    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
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

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Document getContentTypeConfigXML( GetContentTypeConfigXMLParams params )
        throws ClientException
    {
        final ClientMethodExecutionTrace trace =
            ClientMethodExecutionTracer.startTracing( "getContentTypeConfigXML", livePortalTraceService );
        try
        {
            if ( params.key == null && params.name == null )
            {
                throw new IllegalArgumentException( "Either key or name must be specified" );
            }

            if ( params.key != null )
            {
                ContentTypeEntity contentType = contentTypeDao.findByKey( params.key );
                if ( contentType == null )
                {
                    throw new IllegalArgumentException( "contentType not found, given key: " + params.key );
                }
                return contentType.getData();
            }
            else if ( params.name != null )
            {
                ContentTypeEntity contentType = contentTypeDao.findByName( params.name );
                if ( contentType == null )
                {
                    throw new IllegalArgumentException( "contentType not found, given name: " + params.name );
                }
                // renaming root element from moduledata to contenttype
                Document rawDoc = (Document) contentType.getData().clone();
                rawDoc.getRootElement().setName( "contenttype" );
                return rawDoc;
            }
            return null;
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
        finally
        {
            ClientMethodExecutionTracer.stopTracing( trace, livePortalTraceService );
        }
    }

    public void setClientForRemoteInvocations( boolean value )
    {
        this.clientForRemoteInvocations = value;
    }
}

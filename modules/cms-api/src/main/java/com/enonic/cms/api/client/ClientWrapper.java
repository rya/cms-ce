/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client;

import java.util.List;

import org.jdom.Document;

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

/**
 * This class wraps another client. It can be extended to do useful proxy functionality.
 */
public abstract class ClientWrapper
    implements Client
{

    /**
     * Client delegate.
     */
    private final Client delegate;

    public ClientWrapper( Client delegate )
    {
        this.delegate = delegate;
    }

    public String getUser()
        throws ClientException
    {
        return this.delegate.getUserName();
    }

    public String getUserName()
        throws ClientException
    {
        return this.delegate.getUserName();
    }

    public String getRunAsUser()
        throws ClientException
    {
        return this.delegate.getRunAsUserName();
    }

    public String getRunAsUserName()
        throws ClientException
    {
        return this.delegate.getRunAsUserName();
    }

    public Document getUserContext()
        throws ClientException
    {
        return this.delegate.getUserContext();
    }

    public Document getRunAsUserContext()
        throws ClientException
    {
        return this.delegate.getRunAsUserContext();
    }


    public String login( String user, String password )
        throws ClientException
    {
        return this.delegate.login( user, password );
    }

    public String impersonate( String user )
        throws ClientException
    {
        return this.delegate.impersonate( user );
    }

    public void removeImpersonation()
    {
        this.delegate.removeImpersonation();
    }

    public String logout()
        throws ClientException
    {
        return this.delegate.logout();
    }

    public String logout( boolean invalidateSession )
        throws ClientException
    {
        return this.delegate.logout( invalidateSession );
    }

    public Document getUser( GetUserParams params )
        throws ClientException
    {

        return this.delegate.getUser( params );
    }

    public Document getUsers( GetUsersParams params )
        throws ClientException
    {

        return this.delegate.getUsers( params );
    }

    public Document getGroup( GetGroupParams params )
        throws ClientException
    {
        return this.delegate.getGroup( params );
    }

    public Document getGroups( GetGroupsParams params )
        throws ClientException
    {
        return this.delegate.getGroups( params );
    }

    public Document joinGroups( JoinGroupsParams params )
        throws ClientException
    {

        return this.delegate.joinGroups( params );
    }

    public Document leaveGroups( LeaveGroupsParams params )
        throws ClientException
    {
        return delegate.leaveGroups( params );
    }

    public Document createGroup( CreateGroupParams params )
        throws ClientException
    {
        return this.delegate.createGroup( params );
    }

    public void deleteGroup( DeleteGroupParams params )
    {
        this.delegate.deleteGroup( params );
    }

    public int createContent( CreateContentParams params )
    {
        return this.delegate.createContent( params );
    }

    public String createUser( CreateUserParams params )
        throws ClientException
    {
        return delegate.createUser( params );
    }

    public void deleteUser( DeleteUserParams params )
        throws ClientException
    {
        delegate.deleteUser( params );
    }

    public int createCategory( CreateCategoryParams params )
    {
        return delegate.createCategory( params );
    }

    public int updateContent( UpdateContentParams params )
    {
        return this.delegate.updateContent( params );
    }

    public void deleteContent( DeleteContentParams params )
    {
        this.delegate.deleteContent( params );
    }

    public void deleteCategory( DeleteCategoryParams params )
    {
        this.delegate.deleteCategory( params );
    }

    public int createFileContent( CreateFileContentParams params )
    {
        return this.delegate.createFileContent( params );
    }

    public int updateFileContent( UpdateFileContentParams params )
    {
        return this.delegate.updateFileContent( params );
    }

    public int createImageContent( CreateImageContentParams params )
        throws ClientException
    {
        return this.delegate.createImageContent( params );
    }

    public void assignContent( AssignContentParams params )
        throws ClientException
    {
        this.delegate.assignContent( params );
    }

    public void unassignContent( UnassignContentParams params )
        throws ClientException
    {
        this.delegate.unassignContent( params );
    }

    public void snapshotContent( SnapshotContentParams params )
        throws ClientException
    {
        this.delegate.snapshotContent( params );
    }

    public Document getContent( GetContentParams params )
        throws ClientException
    {
        return this.delegate.getContent( params );
    }

    public Document getContentVersions( GetContentVersionsParams params )
        throws ClientException
    {
        return this.delegate.getContentVersions( params );
    }

    public Document getCategories( GetCategoriesParams params )
        throws ClientException
    {
        return this.delegate.getCategories( params );
    }

    public Document getContentByQuery( GetContentByQueryParams params )
        throws ClientException
    {
        return this.delegate.getContentByQuery( params );
    }

    public Document getContentByCategory( GetContentByCategoryParams params )
        throws ClientException
    {
        return this.delegate.getContentByCategory( params );
    }

    public Document getRandomContentByCategory( GetRandomContentByCategoryParams params )
        throws ClientException
    {
        return this.delegate.getRandomContentByCategory( params );
    }

    public Document getContentBySection( GetContentBySectionParams params )
        throws ClientException
    {
        return this.delegate.getContentBySection( params );
    }

    public Document getRandomContentBySection( GetRandomContentBySectionParams params )
        throws ClientException
    {
        return this.delegate.getRandomContentBySection( params );
    }

    public Document getMenu( GetMenuParams params )
        throws ClientException
    {
        return this.delegate.getMenu( params );
    }

    public Document getMenuBranch( GetMenuBranchParams params )
        throws ClientException
    {
        return this.delegate.getMenuBranch( params );
    }

    public Document getMenuData( GetMenuDataParams params )
        throws ClientException
    {
        return this.delegate.getMenuData( params );
    }

    public Document getMenuItem( GetMenuItemParams params )
        throws ClientException
    {
        return this.delegate.getMenuItem( params );
    }

    public Document getSubMenu( GetSubMenuParams params )
        throws ClientException
    {
        return this.delegate.getSubMenu( params );
    }

    public Document getRelatedContent( GetRelatedContentsParams params )
        throws ClientException
    {
        return this.delegate.getRelatedContent( params );
    }

    public Document renderContent( RenderContentParams params )
        throws ClientException
    {
        return this.delegate.renderContent( params );
    }

    public Document renderPage( RenderPageParams params )
        throws ClientException
    {
        return this.delegate.renderPage( params );
    }

    public Document getBinary( GetBinaryParams params )
        throws ClientException
    {
        return this.delegate.getBinary( params );
    }

    public Document getContentBinary( GetContentBinaryParams params )
        throws ClientException
    {
        return this.delegate.getContentBinary( params );
    }

    public Document getResource( GetResourceParams params )
        throws ClientException
    {
        return this.delegate.getResource( params );
    }

    public Document importContents( ImportContentsParams params )
        throws ClientException
    {
        return this.delegate.importContents( params );
    }

    public Preference getPreference( GetPreferenceParams params )
        throws ClientException
    {
        return this.delegate.getPreference( params );
    }

    public List<Preference> getPreferences()
        throws ClientException
    {
        return this.delegate.getPreferences();
    }

    public void setPreference( SetPreferenceParams params )
        throws ClientException
    {
        this.delegate.setPreference( params );
    }

    public void deletePreference( DeletePreferenceParams params )
        throws ClientException
    {
        this.delegate.deletePreference( params );
    }

    public void clearPageCacheForSite( Integer siteKey )
        throws ClientException
    {
        this.delegate.clearPageCacheForSite( siteKey );
    }

    public void clearPageCacheForPage( Integer siteKey, Integer[] menuItemKeys )
    {
        this.delegate.clearPageCacheForPage( siteKey, menuItemKeys );
    }

    public void clearPageCacheForContent( Integer[] contentKeys )
        throws ClientException
    {
        this.delegate.clearPageCacheForContent( contentKeys );
    }

    public Document getContentTypeConfigXML( GetContentTypeConfigXMLParams params )
        throws ClientException
    {
        return this.delegate.getContentTypeConfigXML( params );
    }
}
/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.mock;

import java.util.List;

import org.jdom.Document;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.AssignContentParams;
import com.enonic.cms.api.client.model.CreateCategoryParams;
import com.enonic.cms.api.client.model.CreateContentParams;
import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.CreateGroupParams;
import com.enonic.cms.api.client.model.DeleteCategoryParams;
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

/**
 * Mock implementation of the Client interface.
 */
public class MockClient
    implements Client
{

    public String getUser()
        throws ClientException
    {
        return null;
    }

    public String getUserName()
        throws ClientException
    {
        return null;
    }

    public String getRunAsUser()
        throws ClientException
    {
        return null;
    }

    public String getRunAsUserName()
        throws ClientException
    {
        return null;
    }

    public Document getUserContext()
        throws ClientException
    {
        return null;
    }

    public Document getRunAsUserContext()
        throws ClientException
    {
        return null;
    }

    public String login( String user, String password )
        throws ClientException
    {
        return null;
    }

    public String impersonate( String user )
        throws ClientException
    {
        return null;
    }

    public String logout()
        throws ClientException
    {
        return null;
    }

    public String logout( boolean invalidateSession )
        throws ClientException
    {
        return null;
    }

    public Document getUser( GetUserParams params )
        throws ClientException
    {
        return null;
    }

    public Document getUsers( GetUsersParams params )
        throws ClientException
    {
        return null;
    }

    public Document getGroup( GetGroupParams params )
        throws ClientException
    {
        return null;
    }

    public Document getGroups( GetGroupsParams params )
        throws ClientException
    {
        return null;
    }

    public Document joinGroups( JoinGroupsParams params )
        throws ClientException
    {
        return null;
    }

    public Document leaveGroups( LeaveGroupsParams params )
        throws ClientException
    {
        return null;
    }

    public Document createGroup( CreateGroupParams params )
        throws ClientException
    {
        return null;
    }

    public void deleteGroup( DeleteGroupParams params )
    {

    }

    public int createContent( CreateContentParams params )
    {
        return 0;
    }

    public int createCategory( CreateCategoryParams params )
    {
        return 0;
    }

    public int updateContent( UpdateContentParams params )
    {
        return 0;
    }

    public void deleteContent( DeleteContentParams params )
    {

    }

    public int createFileContent( CreateFileContentParams params )
    {
        return 0;
    }

    public int updateFileContent( UpdateFileContentParams params )
    {
        return 0;
    }

    public void assignContent( AssignContentParams params )
        throws ClientException
    {
    }

    public void unassignContent( UnassignContentParams params )
        throws ClientException
    {
    }

    public void snapshotContent( SnapshotContentParams params )
        throws ClientException
    {
    }

    public Document getContent( GetContentParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentVersions( GetContentVersionsParams params )
        throws ClientException
    {
        return null;
    }

    public Document getCategories( GetCategoriesParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentByQuery( GetContentByQueryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentByCategory( GetContentByCategoryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getRandomContentByCategory( GetRandomContentByCategoryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentBySection( GetContentBySectionParams params )
        throws ClientException
    {
        return null;
    }

    public Document getRandomContentBySection( GetRandomContentBySectionParams params )
        throws ClientException
    {
        return null;
    }

    public Document getMenu( GetMenuParams params )
        throws ClientException
    {
        return null;
    }

    public Document getMenuBranch( GetMenuBranchParams params )
        throws ClientException
    {
        return null;
    }

    public Document getMenuData( GetMenuDataParams params )
        throws ClientException
    {
        return null;
    }

    public Document getMenuItem( GetMenuItemParams params )
        throws ClientException
    {
        return null;
    }

    public Document getSubMenu( GetSubMenuParams params )
        throws ClientException
    {
        return null;
    }

    public Document getRelatedContent( GetRelatedContentsParams params )
        throws ClientException
    {
        return null;
    }

    public Document renderContent( RenderContentParams params )
        throws ClientException
    {
        return null;
    }

    public Document renderPage( RenderPageParams params )
        throws ClientException
    {
        return null;
    }

    public Document getBinary( GetBinaryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentBinary( GetContentBinaryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getResource( GetResourceParams params )
        throws ClientException
    {
        return null;
    }

    public Document importContents( ImportContentsParams params )
        throws ClientException
    {
        return null;
    }

    public Preference getPreference( GetPreferenceParams params )
        throws ClientException
    {
        return null;
    }

    public List<Preference> getPreferences()
        throws ClientException
    {
        return null;
    }

    public void setPreference( SetPreferenceParams params )
        throws ClientException
    {

    }

    public void deletePreference( DeletePreferenceParams params )
        throws ClientException
    {

    }

    public void clearPageCacheForSite( Integer siteKey )
        throws ClientException
    {

    }

    public void clearPageCacheForPage( Integer siteKey, Integer[] menuItemKeys )
    {

    }

    public void clearPageCacheForContent( Integer[] contentKeys )
        throws ClientException
    {

    }

    public void deleteCategory( DeleteCategoryParams params )
        throws ClientException
    {

    }
}

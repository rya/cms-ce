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
 * This interface defines the cms client.
 */
public interface Client
{

    /**
     * Login a user.
     *
     * @param username the username of the user
     * @param password the password
     * @return the username
     * @throws ClientException Whenever a problem occurs finding the user.  Be careful to check the wrapped exception for the underlying
     *                         technical reason.
     */
    public String login( String username, String password )
        throws ClientException;

    /**
     * Impersonate a user. This means that the run-as is different than the logged in user.
     *
     * @param username the username of the user
     * @return The name of the currently running user.
     * @throws ClientException Whenever a problem occurs finding the user.  Be careful to check the wrapped exception for the underlying
     *                         technical reason.
     */
    public String impersonate( String username )
        throws ClientException;

    public String logout()
        throws ClientException;

    public String logout( boolean invalidateSession )
        throws ClientException;

    /**
     * Returns the user name of the logged in user.
     *
     * @return The login name of the currently logged in user.
     * @throws ClientException Whenever a problem occurs finding the user.  Be careful to check the wrapped exception for the underlying
     *                         technical reason.
     * @deprecated Use getUserName() instead
     */

    @Deprecated
    public String getUser()
        throws ClientException;

    /**
     * Return the currently logged in user who is running the current thread.
     *
     * @return The login name of the currently logged in user.
     * @throws ClientException Whenever a problem occurs finding the user.  Be careful to check the wrapped exception for the underlying
     *                         technical reason.
     */
    public String getUserName()
        throws ClientException;

    /**
     * Return the currently logged in user who is running the current thread.
     *
     * @return The login name of the currently logged in user.
     * @throws ClientException Whenever a problem occurs finding the user.  Be careful to check the wrapped exception for the underlying
     *                         technical reason.
     * @deprecated Use getRunAsUserName() instead
     */
    @Deprecated
    public String getRunAsUser()
        throws ClientException;

    public String getRunAsUserName()
        throws ClientException;

    public Document getUserContext()
        throws ClientException;

    /**
     * Returns info on the user currently regarded as the caller of the methods.  This is normally the logged in user, but if a call
     * has been made to <code>impersonate(...)</code>, the impersonating user will be the run as user.
     *
     * @return An XML Document with all known info about the run as user.
     * @throws ClientException Whenever a problem occurs finding the user.  Be careful to check the wrapped exception for the underlying
     *                         technical reason.
     */
    public Document getRunAsUserContext()
        throws ClientException;

    /**
     * Returns the specified user. If user is not specified, the current logged in user will be returned.
     *
     * @param params The specification of which user to get.
     * @return An XML document with all data for the requested user.
     * @throws ClientException Whenever a problem occurs finding the user.  Be careful to check the wrapped exception for the underlying
     *                         technical reason.
     */
    public Document getUser( GetUserParams params )
        throws ClientException;

    public Document getUsers( GetUsersParams params )
        throws ClientException;

    public Document getGroup( GetGroupParams params )
        throws ClientException;

    public Document getGroups( GetGroupsParams params )
        throws ClientException;

    public Document joinGroups( JoinGroupsParams params )
        throws ClientException;

    public Document leaveGroups( LeaveGroupsParams params )
        throws ClientException;

    public Document createGroup( CreateGroupParams params )
        throws ClientException;

    public void deleteGroup( DeleteGroupParams params )
        throws ClientException;

    public int createCategory( CreateCategoryParams params )
        throws ClientException;

    public int createContent( CreateContentParams params )
        throws ClientException;

    public int updateContent( UpdateContentParams params )
        throws ClientException;

    public void deleteContent( DeleteContentParams params )
        throws ClientException;

    public void deleteCategory( DeleteCategoryParams params )
        throws ClientException;

    public int createFileContent( CreateFileContentParams params )
        throws ClientException;

    public int updateFileContent( UpdateFileContentParams params )
        throws ClientException;

    public void assignContent( AssignContentParams params )
        throws ClientException;

    public void unassignContent( UnassignContentParams params )
        throws ClientException;

    public void snapshotContent( SnapshotContentParams params )
        throws ClientException;

    /**
     * Finds and returns the content that is specified by the content keys in the <code>params</code> object.
     *
     * @param params This object specifies what to search for.  The <code>contentKeys</code> array is mandatory.
     * @return An XML describing the content that has been found.  The root element of the XML document will be &lt;contents&gt;
     * @throws ClientException If an error occurs looking for the content.  Check the cause carefully to find out what the problem really
     *                         is.
     */
    public Document getContent( GetContentParams params )
        throws ClientException;

    public Document getContentVersions( GetContentVersionsParams params )
        throws ClientException;

    public Document getCategories( GetCategoriesParams params )
        throws ClientException;

    public Document getContentByQuery( GetContentByQueryParams params )
        throws ClientException;

    public Document getContentByCategory( GetContentByCategoryParams params )
        throws ClientException;

    public Document getRandomContentByCategory( GetRandomContentByCategoryParams params )
        throws ClientException;

    public Document getContentBySection( GetContentBySectionParams params )
        throws ClientException;

    public Document getRandomContentBySection( GetRandomContentBySectionParams params )
        throws ClientException;

    /**
     * Finds and returns content related to the passed in content Keys, as specified by the <code>params.relation</code> parameter, where
     * any positiv number transforms this method to a getChildren method, and any negativ number makes this method a getParents method.
     * <p/>
     * The <code>params.childrenLevel</code> and <code>params.paraentLevel</code> specifies if the returned XML also should include content
     * related to the related content.  If these are set to include children when this method is in fact a get parents method, or vice
     * versa, the related content node of the returned XML may include the orignal content that was passed in, in
     * <code>params.contentKeys</code>.
     *
     * @param params Specification of what to search for.  At least one content key must be included.
     * @return An XML describing the content that has been found.
     * @throws ClientException If an error occurs looking for the content.  Check the cause carefully to find out what the problem really
     *                         is.
     */
    public Document getRelatedContent( GetRelatedContentsParams params )
        throws ClientException;

    public Document getMenu( GetMenuParams params )
        throws ClientException;

    public Document getMenuBranch( GetMenuBranchParams params )
        throws ClientException;

    public Document getMenuData( GetMenuDataParams params )
        throws ClientException;

    public Document getMenuItem( GetMenuItemParams params )
        throws ClientException;

    public Document getSubMenu( GetSubMenuParams params )
        throws ClientException;

    public Document renderContent( RenderContentParams params )
        throws ClientException;

    public Document renderPage( RenderPageParams params )
        throws ClientException;

    public Document importContents( ImportContentsParams params )
        throws ClientException;

    public Document getBinary( GetBinaryParams params )
        throws ClientException;

    public Document getContentBinary( GetContentBinaryParams params )
        throws ClientException;

    public Document getResource( GetResourceParams params )
        throws ClientException;

    public Preference getPreference( GetPreferenceParams params )
        throws ClientException;

    public List<Preference> getPreferences()
        throws ClientException;

    public void setPreference( SetPreferenceParams params )
        throws ClientException;

    public void deletePreference( DeletePreferenceParams params )
        throws ClientException;

    public void clearPageCacheForSite( Integer siteKey )
        throws ClientException;

    /**
     * Clears page cache entries for the given site and the given menu item keys, including cached windows.
     * The method does not control that the menu item keys belongs to the given site. Neither does it control that the given site
     * and menu items exist. Giving keys to entities that does exist will be silently passed.
     *
     * @param siteKey      identifying which page cache to clear page entries in.
     * @param menuItemKeys menu item keys identifying which pages to clear entries for.
     */
    public void clearPageCacheForPage( Integer siteKey, Integer[] menuItemKeys );

    public void clearPageCacheForContent( Integer[] contentKeys )
        throws ClientException;
}

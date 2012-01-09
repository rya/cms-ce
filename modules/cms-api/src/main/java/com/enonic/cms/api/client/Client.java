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
 * This interface defines the cms client.
 */
public interface Client
{

    /**
     * Logs in specified user if successfully authorized. If user origins from a remote userstore the user will be synchronized.
     *
     * @param user     Specify either by qualified name ([userStoreKey:]&lt;user name&gt;) or key. When specifying a key, prefix with a hash.
     * @param password the password
     * @return The name of the currently logged in user after the operation
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
     */
    public String login( String user, String password )
        throws ClientException;

    /**
     * Impersonate an user. All calls to Client methods after this will be done as the impersonated user instead of the currently logged in
     * user. To remove the current impersonation use the method removeImpersonation().
     *
     * @param user Specify either by qualified name ([userStoreKey:]&lt;user name&gt;) or key. When specifying a key, prefix with a hash.
     * @return The name of the currently impersonated user after the operation
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
     */
    public String impersonate( String user )
        throws ClientException;

    /**
     * Removes the currently active impersonation. All calls to Client methods after this will be done as the currently logged in user.
     */
    public void removeImpersonation()
        throws ClientException;

    /**
     * Logs out currently logged in user (including any active impersonation) and invalidates current session.
     */
    public String logout()
        throws ClientException;

    /**
     * Logs out currently logged in user (including any active impersonation) and invalidates current session only if told so.
     */
    public String logout( boolean invalidateSession )
        throws ClientException;

    /**
     * @deprecated Use getUserName() instead
     */
    @Deprecated
    public String getUser()
        throws ClientException;

    /**
     * Returns the name of the currently logged in user.
     *
     * @return The login name of the currently logged in user.
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
     */
    public String getUserName()
        throws ClientException;

    /**
     * @deprecated Use getRunAsUserName() instead
     */
    @Deprecated
    public String getRunAsUser()
        throws ClientException;

    /**
     * Returns the name of the currently impersonated user. If no impersonation is active, the name of the currently logged in user is
     * returned instead.
     *
     * @return The name of the currently impersonated user or logged in user.
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
     */
    public String getRunAsUserName()
        throws ClientException;

    /**
     * Returns info on the currently logged in user.
     *
     * @return An XML Document with info about the logged in user.
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
     */
    public Document getUserContext()
        throws ClientException;

    /**
     * Returns info on the currently impersonated user or logged in user if no impersonation is active.
     *
     * @return An XML Document with info about the impersonated user.
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
     */
    public Document getRunAsUserContext()
        throws ClientException;

    /**
     * Returns a XML document with detailed information about the specified user. If user is not specified, the current logged in user will be returned.
     *
     * @param params The specification of which user to get.
     * @return An XML document with all data for the requested user.
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
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

    public String createUser( CreateUserParams params )
        throws ClientException;

    public void deleteUser( DeleteUserParams params )
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

    public int createImageContent( CreateImageContentParams params )
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
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
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
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
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
     * @throws ClientException Whenever any internal exception occurs, only a ClientException is thrown to the user of Client, containing
     *                         the message from the internal exception. Check server log for actual exception and stack trace.
     */
    public void clearPageCacheForPage( Integer siteKey, Integer[] menuItemKeys )
        throws ClientException;

    public void clearPageCacheForContent( Integer[] contentKeys )
        throws ClientException;

    public Document getContentTypeConfigXML( GetContentTypeConfigXMLParams params )
        throws ClientException;

}

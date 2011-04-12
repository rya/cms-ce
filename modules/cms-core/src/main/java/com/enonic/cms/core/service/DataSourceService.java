/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.portal.datasource.DataSourceContext;

/**
 * This interface defines all "old" datasource methods that is available in the presentation layer.
 */
public interface DataSourceService
{

    public XMLDocument getContentByQuery( DataSourceContext context, String query, String orderBy, int index, int count,
                                          boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Get content, specified by the one of more given content keys. with the possibility to restrict it by filters, so it is only returned
     * if it belongs to a certain category or content type.  Full information about related content is not included.
     *
     * @param context       the Vertical Site context
     * @param contentKeys   The content keys
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param orderBy       A String list of one or more data items to sort the result by, like an SQL Order by clause.
     * @param index         The starting index of the search result, from which to return data.  Should be set to zero in a fresh search.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel the level of children to include
     * @param parentLevel   the level of parents to include
     * @return An XML document with the result of the search
     */
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, String query, String orderBy, int index, int count,
                                   boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Finds all the different versions of the keys presented, and includes possible children as related content, if specified. This method
     * is typically used after doing a search for getContent, to retrieve one or more of the versions of that content. If parents are needed
     * for the final presentation, they should be retrieved together with the content, while children should be retrieved with each
     * version.
     *
     * @param context       The Vertical Site context.
     * @param versionKeys   The Version Keys of the specific versions that are to be included in the response.
     * @param childrenLevel The number of levels below each version to search for children.
     * @return An XML document listing all the content versions as complete content, and their children.
     */
    public XMLDocument getContentVersion( DataSourceContext context, int[] versionKeys, int childrenLevel );


    /**
     * Finds either parents or children of the specified contents.  This set of parents or children is considered the main result of this
     * method.  Related content, as specified by <code>parentLevel</code> or <code>childrenLevel</code> are the related content of the root
     * set of children or parents, and may include the content that was passed in as content keys to the method.
     *
     * @param context       the Vertical Site context
     * @param contentKeys   The content keys
     * @param relation      the allowed values are -1 for parents and 1 for children
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param orderBy       A String list of one or more data items to sort the result by, like an SQL Order by clause.
     * @param index         The starting index of the search result, from which to return data.  Should be set to zero in a fresh search.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel the level of children to include
     * @param parentLevel   the level of parents to include
     * @return An XML document with the result of the search
     */
    public XMLDocument getRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query, String orderBy,
                                          int index, int count, boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Find content in a section specified by the given menu item keys.
     *
     * @param context       The Vertical Site context
     * @param menuItemKeys  The menu items to search for the content within.
     * @param levels        Include sub menus below the specified menu item keys, this number of levels down.
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param orderBy       A String list of one or more data items to sort the result by, like an SQL Order by clause.
     * @param index         The starting index of the search result, from which to return data.  Should be set to zero in a fresh search.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel The number of levels of children to include.
     * @param parentLevel   The number of levels of parents to include.
     * @return An XML document with the result of the search
     */
    public XMLDocument getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, String orderBy,
                                            int index, int count, boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Retrieve random content in a section specified by the given menu item keys.
     *
     * @param context       The Vertical Site context.
     * @param menuItemKeys  The menu items to search for the content within.
     * @param levels        Include sub menus below the specified menu item keys, this number of levels down.
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel The number of levels of children to include.
     * @param parentLevel   The number of levels of parents to include.
     * @return An XML document with the result of the search
     */
    public XMLDocument getRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, int count,
                                                  boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Find content in the specified categories.
     *
     * @param context       The Vertical Site context
     * @param categoryKeys  The keys of the categories to search in.
     * @param levels        Include sub categories below the specified menu item keys, this number of levels down.
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param orderBy       A String list of one or more data items to sort the result by, like an SQL Order by clause.
     * @param index         The starting index of the search result, from which to return data.  Should be set to zero in a fresh search.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel The number of levels of children to include.
     * @param parentLevel   The number of levels of parents to include.
     * @return An XML document with the result of the search
     */
    public XMLDocument getContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, String orderBy,
                                             int index, int count, boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Find content randomly in the specified categories.
     *
     * @param context       The Vertical Site context
     * @param categoryKeys  The keys of the categories to search in.
     * @param levels        Include sub categories below the specified menu item keys, this number of levels down.
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel The number of levels of children to include.
     * @param parentLevel   The number of levels of parents to include.
     * @return An XML document with the result of the search
     */
    public XMLDocument getRandomContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, int count,
                                                   boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Returns a calendar, either by specific date or relative date. If the relative parameter is true, then the year and month parameters
     * are relative to the current date. A value of zero defines the current year/month. Both positive and negative values are allowed.
     * Otherwise, when relative is false, the year and month parameters specifies absolute values (i.e.: 2004 and 1 is January 2004).
     *
     * @param context      the Vertical Site context
     * @param relative     specifies if the calendar should be relative to current date.
     * @param year         a year, relative or absolute
     * @param month        a month, relative or absolute
     * @param count        how many months to include in result
     * @param includeWeeks if true, include weeks
     * @param includeDays  if true, include days
     * @param language     according to http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt
     * @param country      according to http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
     * @return the calendar xml
     */
    public XMLDocument getCalendar( DataSourceContext context, boolean relative, int year, int month, int count, boolean includeWeeks,
                                    boolean includeDays, String language, String country );

    XMLDocument getCountries( DataSourceContext context, String[] countryCodeStr, boolean includeRegions );


    /**
     * Get one content.  Full information about related content or user rights are not included.
     *
     * @param context             the Vertical Site context
     * @param contentKey          the content key
     * @param parentLevel         the level of parents to include
     * @param childrenLevel       the level of children to include
     * @param parentChildrenLevel the level of children for parents to include
     * @param updateStatistics    update the read statistics for this content
     * @return a content xml
     */
    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics );

    /**
     * Get one content with the possibility to include information about the user rights.  Full information about related content is not
     * included.
     *
     * @param context             the Vertical Site context
     * @param contentKey          the content key
     * @param parentLevel         the level of parents to include
     * @param childrenLevel       the level of children to include
     * @param parentChildrenLevel the level of children for parents to include
     * @param updateStatistics    update the read statistics for this content
     * @param includeUserRights   include the user access rights in content
     * @return a content xml
     */
    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics, boolean includeUserRights );

    /**
     * Get one content with the possibility to include information about the user rights, and to restrict it by filters, so it is only
     * returned if it belongs to a certain category or content type.  Full information about related content is not included.
     *
     * @param context              the Vertical Site context
     * @param contentKey           the content key
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param updateStatistics     update the read statistics for this content
     * @param includeUserRights    include the user access rights in content
     * @param filterByCategories   filter by zero or more category keys
     * @param categoryRecursive    include subcategories of the category keys above
     * @param filterByContentTypes filter by zero or more content keys
     * @return a content xml
     */
    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics, boolean includeUserRights, int[] filterByCategories, boolean categoryRecursive,
                                   int[] filterByContentTypes );

    /**
     * Get one content with the possibility to include information about the user rights. Filter for categories and/or content types may be
     * applied. May also specify if full related contents should be fetched (relatedTitlesOnly == false) or not.
     *
     * @param context              the Vertical Site context
     * @param contentKey           the content key
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param updateStatistics     update the read statistics for this content
     * @param relatedTitlesOnly    if true, return only titles of related contents
     * @param includeUserRights    include the user access rights in content
     * @param filterByCategories   filter by zero or more category keys
     * @param categoryRecursive    include subcategories of the category keys above
     * @param filterByContentTypes filter by zero or more content keys
     * @return a content xml
     */
    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics, boolean relatedTitlesOnly, boolean includeUserRights, int[] filterByCategories,
                                   boolean categoryRecursive, int[] filterByContentTypes );

    /**
     * Get contents based on a selection of sections. The default ordering is specified the following rules: <ol> <li>if only one ordered
     * section, order is specified by the section's ordering</li> <li>if only one unordered section, order is specified by the 'publishfrom'
     * attribute.</li> <li>if more than one section, order is specified by the 'publishfrom' attribute.</li> </ol>
     * <p/>
     * Order by syntax (described in EBNF): <table border="0"> <tr><td>Order</td><td>::=</td><td>(|Expression)?</td></tr>
     * <tr><td>Expression</td><td>::=</td><td>(Sortelement) (Expression, Sortelement)?</td></tr>
     * <tr><td>Sortelement</td><td>::=</td><td>Identifier Operator</td></tr> <tr><td>Operator</td><td>::=</td><td>'ASC' | 'DESC'</td></tr>
     * <tr><td>Identifier</td><td>::=</td><td>'title' | 'publishfrom' | 'publishto' | 'owner' | 'modifier' | 'timestamp'</td></tr> </table>
     *
     * @param context             the Vertical Site context
     * @param menuItemKeys        one or more menuitems to get section content from
     * @param levels              The number of levels below each menu item, to look for content.
     * @param orderBy             order by string (see syntax above)
     * @param fromIndex           start from this index
     * @param count               maximum number of contents to get (related content not included)
     * @param titlesOnly          get only titles
     * @param parentLevel         the level of parents to include
     * @param childrenLevel       the level of children to include
     * @param parentChildrenLevel the level of children for parents to include
     * @param relatedTitlesOnly   get only titles for related content
     * @param includeTotalCount   include total count of contents returned excluding fromIndex and count
     * @param includeUserRights   include the user access rights in content
     * @param filterByContentType A list of specific content types which the result should be limited to.
     * @param query               A specification of which content within the section to search for.  This may be pure text, or a more
     *                            specific searches where field names are specified.
     * @return contents xml
     */
    public XMLDocument getContentBySection( DataSourceContext context, String query, int[] menuItemKeys, int levels, String orderBy,
                                            int fromIndex, int count, boolean titlesOnly, int parentLevel, int childrenLevel,
                                            int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                            boolean includeUserRights, int[] filterByContentType );

    /**
     * Get contents based on a selection of sections. The default ordering is specified the following rules: <ol> <li>if only one ordered
     * section, order is specified by the section's ordering</li> <li>if only one unordered section, order is specified by the 'publishfrom'
     * attribute.</li> <li>if more than one section, order is specified by the 'publishfrom' attribute.</li> </ol>
     * <p/>
     * Order by syntax (described in EBNF): <table border="0"> <tr><td>Order</td><td>::=</td><td>(|Expression)?</td></tr>
     * <tr><td>Expression</td><td>::=</td><td>(Sortelement) (Expression, Sortelement)?</td></tr>
     * <tr><td>Sortelement</td><td>::=</td><td>Identifier Operator</td></tr> <tr><td>Operator</td><td>::=</td><td>'ASC' | 'DESC'</td></tr>
     * <tr><td>Identifier</td><td>::=</td><td>'title' | 'publishfrom' | 'publishto' | 'owner' | 'modifier' | 'timestamp'</td></tr> </table>
     *
     * @param context              the Vertical Site context
     * @param menuItemKeys         one or more menuitems to get section content from
     * @param levels               The number of levels below each menu item, to look for content.
     * @param orderBy              order by string (see syntax above)
     * @param fromIndex            start from this index
     * @param count                maximum number of contents to get (related content not included)
     * @param titlesOnly           get only titles
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param relatedTitlesOnly    get only titles for related content
     * @param includeTotalCount    include total count of contents returned excluding fromIndex and count
     * @param includeUserRights    include the user access rights in content
     * @param filterByContentTypes filter by zero or more content type keys
     * @return contents xml
     */
    public XMLDocument getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String orderBy, int fromIndex,
                                            int count, boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                            boolean relatedTitlesOnly, boolean includeTotalCount, boolean includeUserRights,
                                            int[] filterByContentTypes );

    /**
     * Get the current date as xml. The offset attribute is the offset in days from the current date.
     *
     * @param context    the Vertical Site context
     * @param offset     number of days from current date
     * @param dateformat as specified by http://java.sun.com/products/jdk/1.2/docs/api/java/text/SimpleDateFormat.html
     * @param language   as specified by http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt
     * @param country    as specified by http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
     * @return date xml
     */
    public XMLDocument getFormattedDate( DataSourceContext context, int offset, String dateformat, String language, String country );

    /*
     * Reserved for special use.
     */

    public XMLDocument getFusionBotQuery( DataSourceContext context, String fusionBotUrl, String query, int siteNum, int page );

    /**
     * Returns a menu tree. If levels is 0, entire menu are return. If levels is non-negative number, that number of tree levels are
     * returned or the entire tree if levels is greater than or equal to the number of levels in the menu tree. Only menu items marked as
     * 'show in menu' will be included in the result.
     *
     * @param context Site context
     * @param menuKey Menu key
     * @param tagItem Menu item to tag
     * @param levels  Number of levels to return
     * @return An XML Document with all details about the requested menu.
     */
    public XMLDocument getMenu( DataSourceContext context, int menuKey, int tagItem, int levels );

    /**
     * Returns the menu tree.
     *
     * @param context Site context
     * @param menuKey Menu key
     * @param tagItem Menu item to tag
     * @param levels  Number of levels to return
     * @param details Fetch details if true
     * @return An XML document, listing all data about the menus requested.
     */
    public XMLDocument getMenu( DataSourceContext context, int menuKey, int tagItem, int levels, boolean details );

    /**
     * Returns the menu tree by menu item key.
     *
     * @param context     the Vertical Site context
     * @param menuItemKey Menu item key
     * @param levels      Number of levels to return
     * @return An XML document with all details about the menu tree.
     */
    public XMLDocument getMenu( DataSourceContext context, int menuItemKey, int levels );


    /**
     * Get the settings defined for a menu.
     *
     * @param context the Vertical Site context
     * @param menuId  a menu key
     * @return menu data xml
     */
    public XMLDocument getMenuData( DataSourceContext context, int menuId );

    /**
     * Get the settings defined for a menu.
     *
     * @param context the Vertical Site context
     * @return menu data xml
     */
    public XMLDocument getMenuData( DataSourceContext context );

    /**
     * Get a branch of a menu structure. The method will locate the top level menu item of the current menu item, and return the entire tree
     * beneath it. Only menu items marked 'show in menu' will be included in the result.
     *
     * @param context  the Vertical Site context
     * @param menuItem a menu item key
     * @param topLevel if true, all menu items at the top level are returned
     * @return menu tree xml
     */
    XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel );

    XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, int startLevel, int levels );

    XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, boolean details );

    // Not in use.

    XMLDocument getMenuItem( DataSourceContext context, int key, boolean withParents );

    /**
     * Get a menu item.
     *
     * @param context     the Vertical Site context
     * @param key         a menu item key
     * @param withParents if true, include parents up to top level (i.e.: it's path)
     * @param complete    include the full menu item
     * @return menu item xml
     */
    public XMLDocument getMenuItem( DataSourceContext context, int key, boolean withParents, boolean complete );

    /**
     * Get random content by parent content.
     *
     * @param context           the Vertical Site context
     * @param count             number of contents to get
     * @param contentKey        a parent content key
     * @param includeUserRights if true, include the user's access rights on the contents
     * @return contents xml
     */
    public XMLDocument getRandomContentByParent( DataSourceContext context, int count, int contentKey, boolean includeUserRights );

    /**
     * Get random content by section.
     *
     * @param context             The Vertical Site context.
     * @param menuItemKeys        one or more menuitems to get section content from
     * @param levels              The number of levels below each menu item, to look for content.
     * @param count               number of random contents to get
     * @param titlesOnly          get only titles
     * @param parentLevel         the level of parents to include
     * @param childrenLevel       the level of children to include
     * @param parentChildrenLevel the level of children for parents to include
     * @param relatedTitlesOnly   get only titles for related content
     * @param includeUserRights   include the user access rights in content
     * @param query               A specification of which content within the section to search for.  This may be pure text, or a more
     *                            specific searches where field names are specified.
     * @return contents xml
     */
    public XMLDocument getRandomContentBySections( DataSourceContext context, String query, int[] menuItemKeys, int levels, int count,
                                                   boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                                   boolean relatedTitlesOnly, boolean includeUserRights );

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     * @return An XML Document with all information about the sub menus.
     */
    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem );

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     * @param levels  Number of levels to fetch
     * @return An XML Document with all information about the sub menus.
     */
    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem, int levels );

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     * @param levels  Number of levels to fetch
     * @param details Fetch details if true
     * @return An XML Document with all information about the sub menus.
     */
    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem, int levels, boolean details );

    /**
     * Get a list of category forming a path to a category.
     *
     * @param context          the Vertical Site context
     * @param categoryKey      a category key
     * @param withContentCount if true, include content count for each category
     * @param includeCategory  if true, include the root category
     * @return category xml
     */
    public XMLDocument getSuperCategoryNames( DataSourceContext context, int categoryKey, boolean withContentCount,
                                              boolean includeCategory );

    /**
     * Makes a connection to a url, places it in an xml element with a CDATA block.
     *
     * @param context  A reference to the application context, for the method to get information on the user and running environment.
     * @param url      The URL to convert to text.
     * @param encoding The Encoding to use when interpreting the response that is expected from connecting to the URL.
     * @return An XML with the page or ther data retrieved from the URL.
     */
    public XMLDocument getURLAsText( DataSourceContext context, String url, String encoding );

    /**
     * Makes a connection to a url, places it in an xml element with a CDATA block.
     *
     * @param context  A reference to the application context, for the method to get information on the user and running environment.
     * @param url      The URL to convert to text.
     * @param encoding The Encoding to use when interpreting the response that is expected from connecting to the URL.
     * @param timeout  The maximum time to wait for the connection to the URL to return a result.
     * @return An XML with the page or ther data retrieved from the URL.
     */
    public XMLDocument getURLAsText( DataSourceContext context, String url, String encoding, int timeout );

    /**
     * Makes a connection to a url that has an xml as result.
     *
     * @param context A reference to the application context, for the method to get information on the user and running environment.
     * @param url     The URL to look up.
     * @return The XML document that was retrieved at the URL.
     */
    public XMLDocument getURLAsXML( DataSourceContext context, String url );

    /**
     * Makes a connection to a url that has an xml as result.
     *
     * @param context A reference to the application context, for the method to get information on the user and running environment.
     * @param url     The URL to look up.
     * @param timeout The maximum time to wait before aborting the look up.
     * @return The XML document that was retrieved at the URL.
     */
    public XMLDocument getURLAsXML( DataSourceContext context, String url, int timeout );


    /**
     * Get a section tree specified by a level indicator and a super-section key. The super-section may be included. If level is positive,
     * returns sections up to and including this level. A value of 0 returns all sections.
     *
     * @param context         the Vertical Site context
     * @param superSectionKey a super-section key
     * @param level           if > 0, return up to and including this level. if 0, return all
     * @param includeSection  include the super-section in the results
     * @return sections xml
     */
    public XMLDocument getSections( DataSourceContext context, int superSectionKey, int level, boolean includeSection );

    public XMLDocument getSections( DataSourceContext context );

    /**
     * Get related contents based on a selection of content keys.
     * <p/>
     * This variation of the method will return the full XML with full information on every content and related content.
     * <p/>
     * The integer paramater "relation" decides which relation to use. <ol> <li>if only one ordered section, order is specified by the
     * section's ordering</li> <li>if only one unordered section, order is specified by the 'publishfrom' attribute.</li> <li>if more than
     * one section, order is specified by the 'publishfrom' attribute.</li> </ol>
     * <p/>
     * Order by syntax (described in EBNF): <table border="0"> <tr><td>Order</td><td>::=</td><td>(|Expression)?</td></tr>
     * <tr><td>Expression</td><td>::=</td><td>(Sortelement) (Expression, Sortelement)?</td></tr>
     * <tr><td>Sortelement</td><td>::=</td><td>Identifier Operator</td></tr> <tr><td>Operator</td><td>::=</td><td>'ASC' | 'DESC'</td></tr>
     * <tr><td>Identifier</td><td>::=</td><td>'title' | 'publishfrom' | 'publishto' | 'owner' | 'modifier' | 'timestamp'</td></tr> </table>
     *
     * @param context              the Vertical Site context
     * @param relation             the allowed values are -1 for parents and 1 for children
     * @param contentKeys          the content keys
     * @param requireAll           if true, all content keys above are required in the relation
     * @param orderBy              order by string (see syntax above)
     * @param fromIndex            start from this index
     * @param count                maximum number of contents to get (related content not included)
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param includeTotalCount    No longer in use.
     * @param filterByCategories   if not emtpy, only content from these categories are included
     * @param categoryRecursive    if true, sub-categories of the above categories are included
     * @param filterByContentTypes if not empty, only content with these content types are included
     * @return contents xml
     */
    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String orderBy, boolean requireAll,
                                           int fromIndex, int count, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                           boolean includeTotalCount, int[] filterByCategories, boolean categoryRecursive,
                                           int[] filterByContentTypes );

    /**
     * Get related contents based on a selection of content keys. The integer paramater "relation" decides which relation to use. <ol>
     * <li>if only one ordered section, order is specified by the section's ordering</li> <li>if only one unordered section, order is
     * specified by the 'publishfrom' attribute.</li> <li>if more than one section, order is specified by the 'publishfrom' attribute.</li>
     * </ol>
     * <p/>
     * Order by syntax (described in EBNF): <table border="0"> <tr><td>Order</td><td>::=</td><td>(|Expression)?</td></tr>
     * <tr><td>Expression</td><td>::=</td><td>(Sortelement) (Expression, Sortelement)?</td></tr>
     * <tr><td>Sortelement</td><td>::=</td><td>Identifier Operator</td></tr> <tr><td>Operator</td><td>::=</td><td>'ASC' | 'DESC'</td></tr>
     * <tr><td>Identifier</td><td>::=</td><td>'title' | 'publishfrom' | 'publishto' | 'owner' | 'modifier' | 'timestamp'</td></tr> </table>
     *
     * @param context              the Vertical Site context
     * @param relation             the allowed values are -1 for parents and 1 for children
     * @param contentKeys          the content keys
     * @param requireAll           if true, all content keys above are required in the relation
     * @param orderBy              order by string (see syntax above)
     * @param fromIndex            start from this index
     * @param count                maximum number of contents to get (related content not included)
     * @param titlesOnly           if true, return only content titles
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param relatedTitlesOnly    if true, return only related content titles
     * @param includeTotalCount    No longer in use.
     * @param filterByCategories   if not emtpy, only content from these categories are included
     * @param categoryRecursive    if true, sub-categories of the above categories are included
     * @param filterByContentTypes if not empty, only content with these content types are included
     * @return contents xml
     */
    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String orderBy, boolean requireAll,
                                           int fromIndex, int count, boolean titlesOnly, int parentLevel, int childrenLevel,
                                           int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                           int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes );

    /**
     * Get related contents based on a selection of content keys.
     * <p/>
     * This method also allows the user to set a specific query to limit the search result.
     * <p/>
     * The integer paramater "relation" decides which relation to use. <ol> <li>if only one ordered section, order is specified by the
     * section's ordering</li> <li>if only one unordered section, order is specified by the 'publishfrom' attribute.</li> <li>if more than
     * one section, order is specified by the 'publishfrom' attribute.</li> </ol>
     * <p/>
     * Order by syntax (described in EBNF): <table border="0"> <tr><td>Order</td><td>::=</td><td>(|Expression)?</td></tr>
     * <tr><td>Expression</td><td>::=</td><td>(Sortelement) (Expression, Sortelement)?</td></tr>
     * <tr><td>Sortelement</td><td>::=</td><td>Identifier Operator</td></tr> <tr><td>Operator</td><td>::=</td><td>'ASC' | 'DESC'</td></tr>
     * <tr><td>Identifier</td><td>::=</td><td>'title' | 'publishfrom' | 'publishto' | 'owner' | 'modifier' | 'timestamp'</td></tr> </table>
     *
     * @param context              the Vertical Site context
     * @param relation             the allowed values are -1 for parents and 1 for children
     * @param contentKeys          the content keys
     * @param requireAll           if true, all content keys above are required in the relation
     * @param query                A textual query to specify special search criterias.
     * @param orderBy              order by string (see syntax above)
     * @param fromIndex            start from this index
     * @param count                maximum number of contents to get (related content not included)
     * @param titlesOnly           if true, return only content titles
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param relatedTitlesOnly    if true, return only related content titles
     * @param includeTotalCount    No longer in use.
     * @param filterByCategories   if not emtpy, only content from these categories are included
     * @param categoryRecursive    if true, sub-categories of the above categories are included
     * @param filterByContentTypes if not empty, only content with these content types are included
     * @return contents xml
     */
    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String query, String orderBy,
                                           boolean requireAll, int fromIndex, int count, boolean titlesOnly, int parentLevel,
                                           int childrenLevel, int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                           int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes );

    /**
     * Return index values for a specified path.
     *
     * @param context              the Vertical Site Context
     * @param path                 path to an indexed value
     * @param categories           one or more category keys
     * @param includeSubCategories include sub categories
     * @param contentTypes         filter by one or more content types
     * @param index                start from this index
     * @param count                maximum number of index values to get
     * @param distinct             return only distinct index values
     * @param order="info"
     * @return index values xml
     */
    public XMLDocument getIndexValues( DataSourceContext context, String path, int[] categories, boolean includeSubCategories,
                                       int[] contentTypes, int index, int count, boolean distinct, String order );

    /**
     * Return aggregated index values for a specified path.
     *
     * @param context              the Vertical Site Context
     * @param path                 path to an indexed value
     * @param categories           one or more category keys
     * @param includeSubCategories include sub categories
     * @param contentTypes         filter by one or more content types
     * @return index values xml
     */
    public XMLDocument getAggregatedIndexValues( DataSourceContext context, String path, int[] categories, boolean includeSubCategories,
                                                 int[] contentTypes );

    /**
     * Return content by category.
     *
     * @param context              the Vertical Site context
     * @param query                a search query (refer to the Administrator Guide for the syntax)
     * @param categories           one or more categories to search in
     * @param includeSubCategories include sub-categories of the categories before
     * @param orderBy              an order by string (refer to the Administrator Guide for the syntax)
     * @param index                start from this index
     * @param count                maximum number of contents to get
     * @param titlesOnly           if true, return only content titles
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param relatedTitlesOnly    if true, return only related content titles
     * @param includeTotalCount    if true, include total count of contents returned excluding fromIndex and count
     * @param includeUserRights    if true, include the current user's access rights to the content
     * @param contentTypes         filter by zero or more content types
     * @return contents xml
     */
    public XMLDocument getContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                             String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                             int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                             boolean includeUserRights, int[] contentTypes );

    /**
     * Return content by category for the logged in user.
     *
     * @param context              the Vertical Site context
     * @param query                a search query (refer to the Administrator Guide for the syntax)
     * @param categories           one or more categories to search in
     * @param includeSubCategories include sub-categories of the categories before
     * @param orderBy              an order by string (refer to the Administrator Guide for the syntax)
     * @param index                start from this index
     * @param count                maximum number of contents to get
     * @param titlesOnly           if true, return only content titles
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param relatedTitlesOnly    if true, return only related content titles
     * @param includeTotalCount    if true, include total count of contents returned excluding fromIndex and count
     * @param includeUserRights    if true, include the current user's access rights to the content
     * @param contentTypes         filter by zero or more content types
     * @return contents xml
     */
    public XMLDocument getMyContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                               String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                               int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                               boolean includeUserRights, int[] contentTypes );

    /**
     * Find content by category. This methods performs a free-text search in one ore more categories.
     *
     * @param context              the Vertical Site context
     * @param search               the search string
     * @param operator             the search operator: "AND" or "OR". "AND" is the default.
     * @param categories           one or more categories to search in
     * @param includeSubCategories include sub-categories of the categories before
     * @param orderBy              an order by string (refer to the Administrator Guide for the syntax)
     * @param index                start from this index
     * @param count                maximum number of contents to get
     * @param titlesOnly           if true, return only content titles
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param relatedTitlesOnly    if true, return only related content titles
     * @param includeTotalCount    if true, include total count of contents returned excluding fromIndex and count
     * @param includeUserRights    if true, include the current user's access rights to the content
     * @param contentTypes         filter by zero or more content types
     * @return contents xml
     */
    public XMLDocument findContentByCategory( DataSourceContext context, String search, String operator, int[] categories,
                                              boolean includeSubCategories, String orderBy, int index, int count, boolean titlesOnly,
                                              int childrenLevel, int parentLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                              boolean includeTotalCount, boolean includeUserRights, int[] contentTypes );

    /**
     * Get a sub tree of categories.
     *
     * @param context          the Vertical Site context
     * @param superCategoryKey root category of the category sub tree
     * @param level            how many sub tree levels to return, 0 is all
     * @param withContentCount if true, include content count for each category
     * @param includeCategory  if true, include the root category
     * @return categories xml
     */
    public XMLDocument getCategories( DataSourceContext context, int superCategoryKey, int level, boolean withContentCount,
                                      boolean includeCategory );

    /**
     * Get a category tree specified by a level indicator and a super-category key. The super-category may be included. If levels is
     * positive, returns categories up to and including this level. A value of 0 returns all categories.
     *
     * @param context      the Vertical Site context
     * @param key          a super-section key
     * @param levels       if > 0, return up to and including this level. if 0, return all
     * @param topLevel     include the super-section in the results
     * @param details      if true, return all category details
     * @param catCount     if true, include category count
     * @param contentCount if true, include content count
     * @return categories xml
     */
    public XMLDocument getCategories( DataSourceContext context, int key, int levels, boolean topLevel, boolean details, boolean catCount,
                                      boolean contentCount );

    public XMLDocument getSearchBloxQuery( DataSourceContext context, String searchBloxURL, String query, int collectionId, int page,
                                           String sort );

    public XMLDocument getSearchBloxQuery( DataSourceContext context, String searchBloxURL, String queryAll, String queryExactPhrase,
                                           String queryLeastOneWord, String queryWithoutTheWords, String language, String contentType,
                                           int startDate, String occurance, int collectionId, int page, int pageSize, String sort );


    /**
     * Get page content with the possibility to include relative contents.
     *
     * @param context             the Vertical Site context
     * @param menuItemId          the menu item id
     * @param parentLevel         the level of parents to include
     * @param childrenLevel       the level of children to include
     * @param parentChildrenLevel the level of children for parents to include
     * @param updateStatistics    update the read statistics for this content
     * @param includeUserRights   include the user access rights in content
     * @return A content xml
     */
    public XMLDocument getPageContent( DataSourceContext context, int menuItemId, int parentLevel, int childrenLevel,
                                       int parentChildrenLevel, boolean updateStatistics, boolean includeUserRights );

    /**
     * Get page content with the possibility to include relative contents.
     *
     * @param context             the Vertical Site context
     * @param menuItemId          the menu item id
     * @param parentLevel         the level of parents to include
     * @param childrenLevel       the level of children to include
     * @param parentChildrenLevel the level of children for parents to include
     * @param updateStatistics    update the read statistics for this content
     * @return A content xml
     */
    public XMLDocument getPageContent( DataSourceContext context, int menuItemId, int parentLevel, int childrenLevel,
                                       int parentChildrenLevel, boolean updateStatistics );

    public XMLDocument getUserstore( final DataSourceContext context, final String userstore );

    /**
     * Get information about the requested user.
     *
     * @param context                 the Vertical Site context
     * @param qualifiedUsername       Qualified username (userstore:username). If not set, the logged in user is returned, or the anonymous
     *                                user if no user is logged in.
     * @param includeMemberships      If set to true, the user's group memberships is also returned
     * @param normalizeGroups         If set to true, indirect group memberships is also returned
     * @param includeCustomUserFields Adds extra attributes to the returned XML, if true.
     * @return An XML document with information about the requested user.
     */
    public XMLDocument getUser( DataSourceContext context, String qualifiedUsername, boolean includeMemberships, boolean normalizeGroups,
                                boolean includeCustomUserFields );

    public XMLDocument getPreferences( DataSourceContext context, String scope, String wildCardKey, boolean uniqueMatch );

    public XMLDocument getPreferences( DataSourceContext context, String scope, String wildCardKey );

    public XMLDocument getPreferences( DataSourceContext context, String scope );

    public XMLDocument getPreferences( DataSourceContext context );

    public XMLDocument getLocales( DataSourceContext context );

    public XMLDocument getTimeZones( DataSourceContext context );

    public XMLDocument getShoppingCartContents( DataSourceContext context, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                                boolean updateStatistics, int[] filterByCategories, boolean categoryRecursive,
                                                int[] filterByContentTypes );
}

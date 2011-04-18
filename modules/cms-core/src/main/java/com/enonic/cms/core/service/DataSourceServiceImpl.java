/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.framework.time.TimeService;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.calendar.CalendarService;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.GetContentExecutor;
import com.enonic.cms.core.content.GetContentResult;
import com.enonic.cms.core.content.GetContentXmlCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery.SectionFilterStatus;
import com.enonic.cms.core.content.query.ContentByCategoryQuery;
import com.enonic.cms.core.content.query.ContentByContentQuery;
import com.enonic.cms.core.content.query.ContentByQueryQuery;
import com.enonic.cms.core.content.query.ContentBySectionQuery;
import com.enonic.cms.core.content.query.InvalidContentBySectionQueryException;
import com.enonic.cms.core.content.query.RelatedChildrenContentQuery;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.CountryXmlCreator;
import com.enonic.cms.core.locale.LocaleService;
import com.enonic.cms.core.locale.LocaleXmlCreator;
import com.enonic.cms.core.preferences.PreferenceEntity;
import com.enonic.cms.core.preferences.PreferenceKey;
import com.enonic.cms.core.preferences.PreferenceScope;
import com.enonic.cms.core.preferences.PreferenceScopeResolver;
import com.enonic.cms.core.preferences.PreferenceService;
import com.enonic.cms.core.preferences.PreferenceSpecification;
import com.enonic.cms.core.preferences.PreferenceUniqueMatchResolver;
import com.enonic.cms.core.preferences.PreferenceXmlCreator;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.UserStoreParser;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserXmlCreator;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreNotFoundException;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.UserStoreXmlCreator;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.MenuItemXMLCreatorSetting;
import com.enonic.cms.core.structure.MenuItemXmlCreator;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteXmlCreator;
import com.enonic.cms.core.structure.access.MenuItemAccessResolver;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.timezone.TimeZoneService;
import com.enonic.cms.core.timezone.TimeZoneXmlCreator;
import com.enonic.cms.portal.datasource.DataSourceContext;
import com.enonic.cms.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.InvalidKeyException;
import com.enonic.cms.domain.SiteKey;

public final class DataSourceServiceImpl
        implements DataSourceService
{
    private static final Logger LOG = LoggerFactory.getLogger( DataSourceServiceImpl.class );

    private final static int DEFAULT_CONNECTION_TIMEOUT = 2000;

    @Inject
    private CalendarService calendarService;

    @Inject
    private ContentService contentService;

    @Inject
    private PreferenceService preferenceService;

    @Inject
    private SecurityService securityService;

    @Inject
    private ContentVersionDao contentVersionDao;

    @Inject
    private ContentDao contentDao;

    @Inject
    private SiteDao siteDao;

    @Inject
    private MenuItemDao menuItemDao;

    @Inject
    private UserDao userDao;

    @Inject
    private GroupDao groupDao;

    private SitePropertiesService sitePropertiesService;

    @Inject
    private CountryService countryService;

    @Inject
    private LocaleService localeService;

    @Inject
    private TimeZoneService timeZoneService;

    private TimeService timeService;

    @Inject
    private UserStoreService userStoreService;

    @Inject
    private UserStoreParser userStoreParser;

    public Document getContentByQuery( DataSourceContext context, String query, String orderBy, int index, int count,
                                       boolean includeData, int childrenLevel, int parentLevel )
    {
        final PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        final Date now = timeService.getNowAsDateTime().toDate();

        try
        {
            ContentByQueryQuery spec = new ContentByQueryQuery();
            spec.setQuery( query );
            spec.setOrderBy( orderBy );
            spec.setIndex( index );
            spec.setCount( count );
            spec.setFilterContentOnlineAt( now );
            spec.setUser( user );
            ContentResultSet contents = contentService.queryContent( spec );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( 0 );
            relatedContentQuery.setIncludeOnlyMainVersions( true );
            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContents =
                        previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            xmlCreator.setResultIndexing( index, count );
            xmlCreator.setIncludeOwnerAndModifierData( true );
            xmlCreator.setIncludeContentData( includeData );
            xmlCreator.setIncludeCategoryData( true );
            xmlCreator.setIncludeRelatedContentData( includeData );
            xmlCreator.setIncludeUserRightsInfo( false, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForSites( false );
            xmlCreator.setIncludeAssignment( true );
            Document xml = xmlCreator.createContentsDocument( user, contents, relatedContents );
            addDataTraceInfo( xml );
            return xml;
        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    /**
     * @inheritDoc
     */
    public Document getContent( DataSourceContext context, int[] contentKeys, String query, String orderBy, int index,
                                int count, boolean includeData, int childrenLevel, int parentLevel )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        boolean includeUserRights = false;
        boolean categoryRecursive = false;
        return doGetContent( context, contentKeys, query, orderBy, index, count, parentLevel, childrenLevel, 0,
                             includeOwnerAndModifierData, includeData, includeCategoryData, includeData,
                             includeUserRights, null, categoryRecursive, null );
    }

    /**
     * @inheritDoc
     */
    public Document getContentVersion( DataSourceContext context, int[] versionKeys, int childrenLevel )
    {
        return doGetContentVersion( context, versionKeys, childrenLevel );
    }

    /**
     * @inheritDoc
     */
    public Document getRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query,
                                       String orderBy, int index, int count, boolean includeData, int childrenLevel,
                                       int parentLevel )
    {
        boolean requireAll = false;
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        boolean categoryRecursive = false;
        return doGetRelatedContent( context, contentKeys, relation, query, orderBy, requireAll, index, count,
                                    parentLevel, childrenLevel, 0, includeOwnerAndModifierData, includeData,
                                    includeCategoryData, includeData, null, categoryRecursive, null );
    }

    /**
     * @inheritDoc
     */
    public Document getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query,
                                         String orderBy, int index, int count, boolean includeData, int childrenLevel,
                                         int parentLevel )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        boolean includeUserRights = false;
        return doGetContentBySection( context, menuItemKeys, levels, query, orderBy, index, count, parentLevel,
                                      childrenLevel, 0, includeOwnerAndModifierData, includeData, includeCategoryData,
                                      includeData, includeUserRights, null );
    }

    /**
     * @inheritDoc
     */
    public Document getRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query,
                                               int count, boolean includeData, int childrenLevel, int parentLevel )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        boolean includeUserRights = false;
        return doGetRandomContentBySection( context, menuItemKeys, levels, query, count, parentLevel, childrenLevel, 0,
                                            includeOwnerAndModifierData, includeData, includeCategoryData, includeData,
                                            includeUserRights );
    }

    /**
     * @inheritDoc
     */
    public Document getContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query,
                                          String orderBy, int index, int count, boolean includeData, int childrenLevel,
                                          int parentLevel )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        boolean includeUserRights = false;
        return doGetContentByCategory( context, categoryKeys, levels, query, orderBy, index, count, childrenLevel,
                                       parentLevel, 0, includeOwnerAndModifierData, includeData, includeCategoryData,
                                       includeData, includeUserRights, null );
    }

    /**
     * @inheritDoc
     */
    public Document getRandomContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query,
                                                int count, boolean includeData, int childrenLevel, int parentLevel )
    {
        PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );

        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        Collection<CategoryKey> categoryKeySet = CategoryKey.convertToList( categoryKeys );

        final Date now = new Date();

        ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
        contentByCategoryQuery.setUser( user );
        contentByCategoryQuery.setCategoryKeyFilter( categoryKeySet, levels );
        contentByCategoryQuery.setIndex( 0 );
        contentByCategoryQuery.setQuery( query );
        contentByCategoryQuery.setCount( Integer.MAX_VALUE );
        contentByCategoryQuery.setFilterContentOnlineAt( now );

        ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );
        if ( previewContext.isPreviewingContent() )
        {
            contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
        }
        ContentResultSet randomContents = contents.createRandomizedResult( count );
        RelatedContentResultSet relatedContent;
        if ( parentLevel > 0 || childrenLevel > 0 )
        {
            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( randomContents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
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

        xmlCreator.setResultIndexing( 0, count );
        xmlCreator.setIncludeContentData( includeData );
        xmlCreator.setIncludeRelatedContentData( includeData );
        xmlCreator.setIncludeVersionsInfoForSites( false );
        xmlCreator.setIncludeAssignment( true );

        Document doc = xmlCreator.createContentsDocument( user, randomContents, relatedContent );

        addDataTraceInfo( doc );
        return doc;
    }

    /**
     * @inheritDoc
     */
    public Document getCalendar( DataSourceContext context, boolean relative, int year, int month, int count,
                                 boolean includeWeeks, boolean includeDays, String language, String country )
    {
        return calendarService.getCalendar( relative, year, month, count, includeWeeks, includeDays, language, country );
    }

    public Document getCountries( DataSourceContext context, String[] countryCodes, boolean includeRegions )
    {

        Collection<Country> countries;
        if ( countryCodes == null || countryCodes.length == 0 )
        {
            countries = countryService.getCountries();
        }
        else
        {
            List<Country> countriesList = new ArrayList<Country>();
            for ( String countryCodeStr : countryCodes )
            {
                countriesList.add( countryService.getCountry( new CountryCode( countryCodeStr ) ) );
            }
            countries = countriesList;
        }

        CountryXmlCreator countryXmlCreator = new CountryXmlCreator();
        countryXmlCreator.setIncludeRegionsInfo( includeRegions );
        return countryXmlCreator.createCountriesDocument( countries );
    }

    /**
     * @inheritDoc
     */
    public Document getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
                                int parentChildrenLevel, boolean updateStatistics )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeContentData = true;
        boolean includeCategoryData = true;
        boolean includeRelatedContentData = false;
        int[] categoriesFilter = null;
        boolean categoriesRecursive = false;
        int[] filterContentTypes = null;
        boolean includeUserRights = false;

        return doGetContent( context, contentKeys, "", "", 0, contentKeys.length, parentLevel, childrenLevel,
                             parentChildrenLevel, includeOwnerAndModifierData, includeContentData, includeCategoryData,
                             includeRelatedContentData, includeUserRights, categoriesFilter, categoriesRecursive,
                             filterContentTypes );
    }

    /**
     * @inheritDoc
     */
    public Document getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
                                int parentChildrenLevel, boolean updateStatistics, boolean includeUserRights )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeContentData = true;
        boolean includeCategoryData = true;
        boolean includeRelatedContentData = false;
        int[] categoriesFilter = null;
        boolean categoriesRecursive = false;
        int[] filterContentTypes = null;

        return doGetContent( context, contentKeys, "", "", 0, contentKeys.length, parentLevel, childrenLevel,
                             parentChildrenLevel, includeOwnerAndModifierData, includeContentData, includeCategoryData,
                             includeRelatedContentData, includeUserRights, categoriesFilter, categoriesRecursive,
                             filterContentTypes );
    }

    /**
     * @inheritDoc
     */
    public Document getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
                                int parentChildrenLevel, boolean updateStatistics, boolean includeUserRights,
                                int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeContentData = true;
        boolean includeCategoryData = true;
        boolean includeRelatedContentData = false;
        return doGetContent( context, contentKeys, "", "", 0, contentKeys.length, parentLevel, childrenLevel,
                             parentChildrenLevel, includeOwnerAndModifierData, includeContentData, includeCategoryData,
                             includeRelatedContentData, includeUserRights, filterByCategories, categoryRecursive,
                             filterByContentTypes );
    }

    /**
     * @inheritDoc
     */
    public Document getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
                                int parentChildrenLevel, boolean updateStatistics, boolean relatedTitlesOnly,
                                boolean includeUserRights, int[] filterByCategories, boolean categoryRecursive,
                                int[] filterByContentTypes )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeContentData = true;
        boolean includeCategoryData = true;
        return doGetContent( context, contentKeys, "", "", 0, contentKeys.length, parentLevel, childrenLevel,
                             parentChildrenLevel, includeOwnerAndModifierData, includeContentData, includeCategoryData,
                             !relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                             filterByContentTypes );
    }


    /**
     * @inheritDoc
     */
    public Document getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String orderBy,
                                         int fromIndex, int count, boolean titlesOnly, int parentLevel,
                                         int childrenLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                         boolean includeTotalCount, boolean includeUserRights,
                                         int[] filterByContentTypes )
    {
        return doGetContentBySection( context, menuItemKeys, levels, "", orderBy, fromIndex, count, parentLevel,
                                      childrenLevel, parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly,
                                      !relatedTitlesOnly, includeUserRights, filterByContentTypes );
    }


    /**
     * @inheritDoc
     */
    public Document getContentBySection( DataSourceContext context, String query, int[] menuItemKeys, int levels,
                                         String orderBy, int fromIndex, int count, boolean titlesOnly, int parentLevel,
                                         int childrenLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                         boolean includeTotalCount, boolean includeUserRights,
                                         int[] filterByContentTypes )
    {
        return doGetContentBySection( context, menuItemKeys, levels, query, orderBy, fromIndex, count, parentLevel,
                                      childrenLevel, parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly,
                                      !relatedTitlesOnly, includeUserRights, filterByContentTypes );

    }

    /**
     * @inheritDoc
     */
    public Document getFormattedDate( DataSourceContext context, int offset, String dateformat, String language,
                                      String country )
    {
        return calendarService.getFormattedDate( offset, dateformat, language, country );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenu( DataSourceContext context, int menuKey, int tagItem, int levels )
    {
        return doGetMenu( getUserEntity( context.getUser() ), menuKey, tagItem, levels );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenu( DataSourceContext context, int menuKey, int tagItem, int levels, boolean details )
    {
        // param "details" not in use
        return doGetMenu( getUserEntity( context.getUser() ), menuKey, tagItem, levels );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenu( DataSourceContext context, int menuItemKey, int levels )
    {
        if ( menuItemKey < 0 )
        {
            return SiteXmlCreator.createEmptyMenus();
        }
        final int siteKey = getSiteKeyByMenuItemKey( menuItemKey );
        return doGetMenu( getUserEntity( context.getUser() ), siteKey, menuItemKey, levels );
    }

    /**
     * Get the settings defined for a menu.
     *
     * @param context the Vertical Site context
     * @param menuId  a menu key
     * @return menu data xml
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenuData( DataSourceContext context, int menuId )
    {
        return doGetMenuData( menuId );
    }

    /**
     * Get the settings defined for a menu.
     *
     * @param context the Vertical Site context
     * @return menu data xml
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenuData( DataSourceContext context )
    {
        return doGetMenuData( context.getSiteKey().toInt() );
    }

    /**
     * Get a branch of a menu structure. The method will locate the top level menu item of the current menu item, and return the entire tree
     * beneath it. Only menu items marked 'show in menu' will be included in the result.
     *
     * @param context  the Vertical Site context
     * @param menuItem a menu item key
     * @param topLevel if true, all menu items at the top level are returned
     * @return menu tree xml
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel )
    {
        return doGetMenuBranch( getUserEntity( context.getUser() ), menuItem, topLevel, 0, 0 );
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, int startLevel,
                                   int levels )
    {
        return doGetMenuBranch( getUserEntity( context.getUser() ), menuItem, topLevel, startLevel, levels );
    }

    /**
     * Return the menu branch.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, boolean details )
    {
        // param "details" not in use
        return doGetMenuBranch( getUserEntity( context.getUser() ), menuItem, topLevel, 0, 0 );
    }

    /**
     * Not in use.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenuItem( DataSourceContext context, int key, boolean withParents )
    {
        return doGetMenuItem( getUserEntity( context.getUser() ), key, withParents );
    }

    /**
     * Get a menu item.
     *
     *
     * @param context     the Vertical Site context
     * @param key         a menu item key
     * @param withParents if true, include parents up to top level (i.e.: it's path)
     * @param complete    include the full menu item
     * @return menu item xml
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getMenuItem( DataSourceContext context, int key, boolean withParents, boolean complete )
    {
        // param "complete" not in use
        return doGetMenuItem( getUserEntity( context.getUser() ), key, withParents );
    }

    /**
     * @inheritDoc
     */
    public Document getRandomContentByParent( DataSourceContext context, int count, int contentKey,
                                              boolean includeUserRights )
    {
        PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        final Date now = new Date();
        ContentByContentQuery contentByContentQuery = new ContentByContentQuery();
        contentByContentQuery.setFilterContentOnlineAt( now );
        contentByContentQuery.setUser( user );
        try
        {
            contentByContentQuery.setContentKeyFilter( ContentKey.convertToList( contentKey ) );
        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid content key: " + contentKey );
        }

        ContentResultSet contents = contentService.queryContent( contentByContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
        }

        RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
        relatedContentQuery.setUser( user );
        relatedContentQuery.setContentResultSet( contents );
        relatedContentQuery.setParentLevel( 0 );
        relatedContentQuery.setChildrenLevel( 1 );
        relatedContentQuery.setParentChildrenLevel( 0 );
        relatedContentQuery.setIncludeOnlyMainVersions( true );

        RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
        ContentResultSetNonLazy relatedContentsAsContentResultSet = new ContentResultSetNonLazy( relatedContents.getDinstinctSetOfContent(), 0, relatedContents.size() );
        ContentResultSet randomizedContents = relatedContentsAsContentResultSet.createRandomizedResult( count );
        if ( previewContext.isPreviewingContent() )
        {
            randomizedContents = previewContext.getContentPreviewContext().overrideContentResultSet( randomizedContents );
        }

        xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                             new ContentAccessResolver( groupDao ) );
        xmlCreator.setResultIndexing( 0, count );
        xmlCreator.setIncludeVersionsInfoForSites( false );
        xmlCreator.setIncludeAssignment( true );
        Document doc = xmlCreator.createContentsDocument( user, randomizedContents, new RelatedContentResultSetImpl() );

        addDataTraceInfo( doc );
        return doc;
    }

    /**
     * @inheritDoc
     */
    public Document getRandomContentBySections( DataSourceContext context, String query, int[] menuItemKeys, int levels,
                                                int count, boolean titlesOnly, int parentLevel, int childrenLevel,
                                                int parentChildrenLevel, boolean relatedTitlesOnly,
                                                boolean includeUserRights )
    {
        return doGetRandomContentBySection( context, menuItemKeys, levels, query, count, parentLevel, childrenLevel,
                                            parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly,
                                            !relatedTitlesOnly, includeUserRights );
    }

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getSubMenu( DataSourceContext context, int key, int tagItem )
    {
        return doGetSubMenu( getUserEntity( context.getUser() ), key, tagItem, 0 );
        //return presentationEngine.getSubMenu( context.getUser(), key, tagItem, 0 );
    }

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     * @param levels  Number of levels to fetch
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getSubMenu( DataSourceContext context, int key, int tagItem, int levels )
    {
        return doGetSubMenu( getUserEntity( context.getUser() ), key, tagItem, levels );
        //return presentationEngine.getSubMenu( context.getUser(), key, tagItem, levels );
    }

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     * @param levels  Number of levels to fetch
     * @param details Fetch details if true
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getSubMenu( DataSourceContext context, int key, int tagItem, int levels, boolean details )
    {
        // param "details" not in use
        return doGetSubMenu( getUserEntity( context.getUser() ), key, tagItem, levels );
        //return presentationEngine.getSubMenu( context.getUser(), key, tagItem, levels );
    }

    /**
     * @inheritDoc
     */
    public Document getURLAsText( DataSourceContext context, String url, String encoding )
    {
        if ( LOG.isDebugEnabled() )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "Executing datasource method: getURLAsText\n" );
            HttpServletRequest request = ServletRequestAccessor.getRequest();
            if ( request != null )
            {
                msg.append( " requested by: " ).append( request.getRemoteAddr() ).append( "\n" );
                msg.append( " orignalUrl: " ).append( request.getAttribute( Attribute.ORIGINAL_URL ) ).append( "\n" );
            }
            msg.append( " executed at: " ).append( timeService.getNowAsDateTime() ).append( "\n" );
            msg.append( " siteKey: " ).append( context.getSiteKey() ).append( "\n" );
            msg.append( " qualifiedName: " ).append( context.getUser().getQualifiedName() ).append( "\n" );
            msg.append( " Method arguments: " ).append( "\n" );
            msg.append( "  - url: " ).append( url ).append( "\n" );
            msg.append( "  - encoding: " ).append( encoding ).append( "\n" );
            LOG.debug( msg.toString() );
        }

        return getURLAsText( context, url, encoding, -1 );
    }

    /**
     * @inheritDoc
     */
    public Document getURLAsText( DataSourceContext context, String url, String encoding, int timeout )
    {
        if ( LOG.isDebugEnabled() )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "Executing datasource method: getURLAsText\n" );
            HttpServletRequest request = ServletRequestAccessor.getRequest();
            if ( request != null )
            {
                msg.append( " requested by: " ).append( request.getRemoteAddr() ).append( "\n" );
                msg.append( " orignalUrl: " ).append( request.getAttribute( Attribute.ORIGINAL_URL ) ).append( "\n" );
            }
            msg.append( " executed at: " ).append( timeService.getNowAsDateTime() ).append( "\n" );
            msg.append( " siteKey: " ).append( context.getSiteKey() ).append( "\n" );
            msg.append( " qualifiedName: " ).append( context.getUser().getQualifiedName() ).append( "\n" );
            msg.append( " Method arguments: " ).append( "\n" );
            msg.append( "  - url: " ).append( url ).append( "\n" );
            msg.append( "  - encoding: " ).append( encoding ).append( "\n" );
            msg.append( "  - timeout: " ).append( timeout ).append( "\n" );
            LOG.debug( msg.toString() );
        }

        org.w3c.dom.Document doc = getURL( url, encoding, timeout );
        return XMLDocumentFactory.create( doc ).getAsJDOMDocument();
    }

    /**
     * Makes a connection to a url that has an xml as result.
     */
    public Document getURLAsXML( DataSourceContext context, String url )
    {
        if ( LOG.isDebugEnabled() )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "Executing datasource method: getURLAsXML\n" );
            HttpServletRequest request = ServletRequestAccessor.getRequest();
            if ( request != null )
            {
                msg.append( " requested by: " ).append( request.getRemoteAddr() ).append( "\n" );
                msg.append( " orignalUrl: " ).append( request.getAttribute( Attribute.ORIGINAL_URL ) ).append( "\n" );
            }
            msg.append( " executed at: " ).append( timeService.getNowAsDateTime() ).append( "\n" );
            msg.append( " siteKey: " ).append( context.getSiteKey() ).append( "\n" );
            msg.append( " qualifiedName: " ).append( context.getUser().getQualifiedName() ).append( "\n" );
            msg.append( " Method arguments: " ).append( "\n" );
            msg.append( "  - url: " ).append( url ).append( "\n" );
            LOG.debug( msg.toString() );
        }

        return getURLAsXML( context, url, -1 );
    }

    /**
     * Makes a connection to a url that has an xml as result.
     */
    public Document getURLAsXML( DataSourceContext context, String url, int timeout )
    {
        if ( LOG.isDebugEnabled() )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "Executing datasource method: getURLAsXML\n" );
            HttpServletRequest request = ServletRequestAccessor.getRequest();
            if ( request != null )
            {
                msg.append( " requested by: " ).append( request.getRemoteAddr() ).append( "\n" );
                msg.append( " orignalUrl: " ).append( request.getAttribute( Attribute.ORIGINAL_URL ) ).append( "\n" );
            }
            msg.append( " executed at: " ).append( timeService.getNowAsDateTime() ).append( "\n" );
            msg.append( " siteKey: " ).append( context.getSiteKey() ).append( "\n" );
            msg.append( " qualifiedName: " ).append( context.getUser().getQualifiedName() ).append( "\n" );
            msg.append( " Method arguments: " ).append( "\n" );
            msg.append( "  - url: " ).append( url ).append( "\n" );
            msg.append( "  - timeout: " ).append( timeout ).append( "\n" );
            LOG.debug( msg.toString() );
        }

        org.w3c.dom.Document doc = getURL( url, null, timeout );
        return XMLDocumentFactory.create( doc ).getAsJDOMDocument();
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getSections( DataSourceContext context, int superSectionKey, int level, boolean includeSection )
    {
        // TODO: Implement method using Hibernate
        throw new IllegalStateException( "Method not implemented" );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getSections( DataSourceContext context )
    {
        // TODO: Implement method using Hibernate
        throw new IllegalStateException( "Method not implemented" );
    }

    /**
     * @inheritDoc
     */
    public Document getContentByCategory( DataSourceContext context, String query, int[] categories,
                                          boolean includeSubCategories, String orderBy, int index, int count,
                                          boolean titlesOnly, int childrenLevel, int parentLevel,
                                          int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                          boolean includeUserRights, int[] contentTypes )
    {
        int levels = includeSubCategories ? Integer.MAX_VALUE : 1;
        return doGetContentByCategory( context, categories, levels, query, orderBy, index, count, childrenLevel,
                                       parentLevel, parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly,
                                       !relatedTitlesOnly, includeUserRights, contentTypes );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getCategories( DataSourceContext context, int superCategoryKey, int level, boolean withContentCount,
                                   boolean includeCategory )
    {
        // TODO: Implement method using Hibernate
        throw new IllegalStateException( "Method not implemented" );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getCategories( DataSourceContext context, int key, int levels, boolean topLevel, boolean details,
                                   boolean catCount, boolean contentCount )
    {
        // TODO: Implement method using Hibernate
        throw new IllegalStateException( "Method not implemented" );
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public Document getUserstore( final DataSourceContext context, final String userstore )
    {
        final UserStoreXmlCreator userStoreXmlCreator = new UserStoreXmlCreator( userStoreService.getUserStoreConnectorConfigs() );
        Document userstoreDoc;

        try
        {
            if ( StringUtils.isBlank( userstore ) )
            {
                userstoreDoc = userStoreXmlCreator.createUserStoresDocument( userStoreService.getDefaultUserStore() );
            }
            else
            {
                UserStoreEntity userStore = userStoreParser.parseUserStore( userstore );
                if ( userStore != null )
                {
                    userstoreDoc = userStoreXmlCreator.createUserStoresDocument( userStore );
                }
                else
                {
                    userstoreDoc = userStoreXmlCreator.createUserStoreNotFoundDocument( userstore );
                }
            }
        }
        catch ( UserStoreNotFoundException e )
        {
            userstoreDoc = userStoreXmlCreator.createUserStoreNotFoundDocument( userstore );
        }

        return userstoreDoc;
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getUser( DataSourceContext context, String qualifiedUsername, boolean includeMemberships,
                             boolean normalizeGroups, boolean includeCustomUserFields )
    {
        UserEntity userEntity;
        if ( qualifiedUsername != null && qualifiedUsername.length() > 0 )
        {
            QualifiedUsername qUsername = QualifiedUsername.parse( qualifiedUsername );
            userEntity = securityService.getUser( qUsername );
        }
        else
        {
            userEntity = getUserEntity( context.getUser() );
        }

        final UserXmlCreator xmlCreator = new UserXmlCreator();
        xmlCreator.setIncludeUserFields( includeCustomUserFields );
        xmlCreator.wrappUserFieldsInBlockElement( false );
        xmlCreator.setAdminConsoleStyle( false );
        Document userDoc;
        if ( userEntity == null )
        {
            userDoc = xmlCreator.createEmptyUserDocument();

        }
        else
        {
            userDoc = xmlCreator.createUserDocument( userEntity, includeMemberships, normalizeGroups );
        }

        return userDoc;
    }


    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getPreferences( DataSourceContext context, String scope, String wildCardKey, boolean uniqueMatch )
    {

        return doGetPreferences( context, scope, wildCardKey, uniqueMatch );
    }


    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getPreferences( DataSourceContext context, String scope, String wildCardKey )
    {

        return doGetPreferences( context, scope, wildCardKey, true );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getPreferences( DataSourceContext context, String scope )
    {

        return doGetPreferences( context, scope, null, true );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getPreferences( DataSourceContext context )
    {

        return doGetPreferences( context, null, null, true );
    }

    private Document doGetPreferences( DataSourceContext context, String scope, String wildCardKey,
                                       boolean uniqueMatch )
    {
        final UserEntity user = getUserEntity( context.getUser() );

        PreferenceSpecification spec = new PreferenceSpecification( user );
        if ( StringUtils.isEmpty( scope ) || "*".equals( scope ) )
        {
            spec.setPreferenceScopes( PreferenceScopeResolver.resolveAllScopes( context.getPortalInstanceKey(), context.getSiteKey() ) );
        }
        else
        {
            List<PreferenceScope> resolvedScopes =
                    PreferenceScopeResolver.resolveScopes(scope, context.getPortalInstanceKey(), context.getSiteKey());

            if ( resolvedScopes.isEmpty() )
            {
                return PreferenceXmlCreator.createEmptyPreferencesDocument(
                        "Scope " + scope + " is not a valid scope list" );
            }

            spec.setPreferenceScopes( resolvedScopes );
        }
        spec.setWildCardBaseKey( wildCardKey );

        List<PreferenceEntity> preferences = this.preferenceService.getPreferences( spec );

        if ( uniqueMatch )
        {
            preferences = getUniqueMatches( preferences );
        }

        return PreferenceXmlCreator.createPreferencesDocument( preferences );
    }


    private List<PreferenceEntity> getUniqueMatches( List<PreferenceEntity> allPreferences )
    {

        List<PreferenceEntity> uniquePreferences = new ArrayList<PreferenceEntity>();

        PreferenceUniqueMatchResolver uniqueMatchResolver = new PreferenceUniqueMatchResolver();

        for ( PreferenceEntity preference : allPreferences )
        {
            uniqueMatchResolver.addPreferenceKeyIfHigherPriority( preference.getKey() );
        }

        List<PreferenceKey> uniqueKeys = uniqueMatchResolver.getUniquePreferenceKeys();

        for ( PreferenceEntity preference : allPreferences )
        {
            if ( uniqueKeys.contains( preference.getKey() ) )
            {
                uniquePreferences.add( preference );
            }
        }

        return uniquePreferences;
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getPageContent( DataSourceContext context, int menuItemId, int parentLevel, int childrenLevel,
                                    int parentChildrenLevel, boolean updateStatistics, boolean includeUserRights )
    {
        return doGetPageContent( context, menuItemId, parentLevel, childrenLevel, parentChildrenLevel, includeUserRights );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Document getPageContent( DataSourceContext context, int menuItemId, int parentLevel, int childrenLevel,
                                    int parentChildrenLevel, boolean updateStatistics )
    {
        return doGetPageContent( context, menuItemId, parentLevel, childrenLevel, parentChildrenLevel, false );
    }

    private Document doGetPageContent( DataSourceContext context, int menuItemId, int parentLevel, int childrenLevel,
                                       int parentChildrenLevel, boolean includeUserRights )
    {
        PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        ContentResultSet content = contentService.getPageContent( menuItemId );
        if ( content.getLength() < 1 )
        {
            return xmlCreator.createEmptyDocument( "There were no content for the given menu item key." );
        }
        if ( previewContext.isPreviewingContent() )
        {
            content = previewContext.getContentPreviewContext().overrideContentResultSet( content );
        }

        final Date now = new Date();
        RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
        relatedContentQuery.setUser( user );
        relatedContentQuery.setContentResultSet( content );
        relatedContentQuery.setParentLevel( parentLevel );
        relatedContentQuery.setChildrenLevel( childrenLevel );
        relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
        relatedContentQuery.setIncludeOnlyMainVersions( true );

        RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
        }

        xmlCreator.setResultIndexing( 0, content.getLength() );
        xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                             new ContentAccessResolver( groupDao ) );
        xmlCreator.setIncludeVersionsInfoForSites( false );
        xmlCreator.setIncludeAssignment( true );

        Document xml = xmlCreator.createContentsDocument( user, content, relatedContent );
        addDataTraceInfo( xml );

        return xml;
    }

    /**
     * @inheritDoc
     */
    public Document getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String orderBy,
                                        boolean requireAll, int fromIndex, int count, int parentLevel,
                                        int childrenLevel, int parentChildrenLevel, boolean includeTotalCount,
                                        int[] filterByCategories, boolean categoryRecursive,
                                        int[] filterByContentTypes )
    {
        return doGetRelatedContent( context, contentKeys, relation, null, orderBy, requireAll, fromIndex, count,
                                    parentLevel, childrenLevel, parentChildrenLevel, true, true, true, true,
                                    filterByCategories, categoryRecursive, filterByContentTypes );
    }

    /**
     * @inheritDoc
     */
    public Document getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String orderBy,
                                        boolean requireAll, int fromIndex, int count, boolean titlesOnly,
                                        int parentLevel, int childrenLevel, int parentChildrenLevel,
                                        boolean relatedTitlesOnly, boolean includeTotalCount, int[] filterByCategories,
                                        boolean categoryRecursive, int[] filterByContentTypes )
    {
        return doGetRelatedContent( context, contentKeys, relation, null, orderBy, requireAll, fromIndex, count,
                                    parentLevel, childrenLevel, parentChildrenLevel, !titlesOnly, !titlesOnly,
                                    !titlesOnly, !relatedTitlesOnly, filterByCategories, categoryRecursive,
                                    filterByContentTypes );
    }

    /**
     * @inheritDoc
     */
    public Document getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String query,
                                        String orderBy, boolean requireAll, int fromIndex, int count,
                                        boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                        boolean relatedTitlesOnly, boolean includeTotalCount, int[] filterByCategories,
                                        boolean categoryRecursive, int[] filterByContentTypes )
    {
        return doGetRelatedContent( context, contentKeys, relation, query, orderBy, requireAll, fromIndex, count,
                                    parentLevel, childrenLevel, parentChildrenLevel, !titlesOnly, !titlesOnly,
                                    !titlesOnly, !relatedTitlesOnly, filterByCategories, categoryRecursive,
                                    filterByContentTypes );
    }


    public Document getLocales( DataSourceContext context )
    {
        Locale[] locales = localeService.getLocales();
        LocaleXmlCreator localeXmlCreator = new LocaleXmlCreator();
        return localeXmlCreator.createLocalesDocument( locales );
    }

    public Document getTimeZones( DataSourceContext context )
    {
        Collection<DateTimeZone> timeZones = timeZoneService.getTimeZones();
        DateTime now = timeService.getNowAsDateTime();
        TimeZoneXmlCreator timeZoneXmlCreator = new TimeZoneXmlCreator( now );
        return timeZoneXmlCreator.createTimeZonesDocument( timeZones );
    }

    private Document doGetRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query,
                                          String orderBy, boolean requireAll, int index, int count, int parentLevel,
                                          int childrenLevel, int parentChildrenLevel,
                                          boolean includeOwnerAndModifierData, boolean includeContentData,
                                          boolean includeCategoryData, boolean includeRelatedContentData,
                                          int[] filterByCategories, boolean categoryRecursive,
                                          int[] filterByContentTypes )
    {
        PreviewContext previewContext = context.getPreviewContext();

        final Date now = new Date();
        UserEntity user = getUserEntity( context.getUser() );

        // Get given content to get top related content for
        final ContentByContentQuery baseContentQuery = new ContentByContentQuery();
        baseContentQuery.setFilterContentOnlineAt( now );
        baseContentQuery.setUser( user );
        try
        {
            baseContentQuery.setContentKeyFilter( ContentKey.convertToList( contentKeys ) );
        }
        catch ( InvalidKeyException e )
        {
            return new ContentXMLCreator().createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
        ContentResultSet baseContent = contentService.queryContent( baseContentQuery );

        if ( previewContext.isPreviewingContent() )
        {
            baseContent = previewContext.getContentPreviewContext().applyPreviewedContentOnContentResultSet( baseContent,
                                                                                                             contentKeys );
        }

        // Get the main content (related content to base content)
        final RelatedContentResultSet relatedContentToBaseContent;
        if ( requireAll && baseContent.getLength() > 1 )
        {
            relatedContentToBaseContent = contentService.getRelatedContentRequiresAll( user, relation, baseContent );
        }
        else
        {
            RelatedContentQuery relatedContentToBaseContentSpec = new RelatedContentQuery( now );
            relatedContentToBaseContentSpec.setUser( user );
            relatedContentToBaseContentSpec.setContentResultSet( baseContent );
            relatedContentToBaseContentSpec.setParentLevel( relation < 0 ? 1 : 0 );
            relatedContentToBaseContentSpec.setChildrenLevel( relation > 0 ? 1 : 0 );
            relatedContentToBaseContentSpec.setParentChildrenLevel( 0 );
            relatedContentToBaseContentSpec.setIncludeOnlyMainVersions( true );

            relatedContentToBaseContent = contentService.queryRelatedContent( relatedContentToBaseContentSpec );

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
        }

        // Get the main result content
        final ContentByContentQuery mainResultContentQuery = new ContentByContentQuery();
        mainResultContentQuery.setUser( user );
        if ( previewContext.isPreviewingContent() )
        {
            // ensuring offline related content to be included when previewing
            mainResultContentQuery.setFilterIncludeOfflineContent();
        }
        else
        {
            mainResultContentQuery.setFilterContentOnlineAt( now );
        }
        mainResultContentQuery.setQuery( query );
        mainResultContentQuery.setOrderBy( orderBy );
        mainResultContentQuery.setIndex( index );
        mainResultContentQuery.setCount( count );
        try
        {
            mainResultContentQuery.setContentKeyFilter( relatedContentToBaseContent.getContentKeys() );
            mainResultContentQuery.setCategoryKeyFilter( CategoryKey.convertToList( filterByCategories ),
                                                         categoryRecursive ? Integer.MAX_VALUE : 1 );
            mainResultContentQuery.setContentTypeFilter( ContentTypeKey.convertToList( filterByContentTypes ) );
        }
        catch ( InvalidKeyException e )
        {
            return new ContentXMLCreator().createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
        ContentResultSet mainResultContent = contentService.queryContent( mainResultContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            mainResultContent = previewContext.getContentPreviewContext().overrideContentResultSet( mainResultContent );
            previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( mainResultContent );
        }

        // Get the related content to the main result
        final RelatedContentQuery relatedContentSpec = new RelatedContentQuery( now );
        if ( previewContext.isPreviewingContent() )
        {
            // ensuring related offline content to be included when previewing
            relatedContentSpec.setFilterIncludeOfflineContent();
        }
        relatedContentSpec.setUser( user );
        relatedContentSpec.setContentResultSet( mainResultContent );
        relatedContentSpec.setParentLevel( parentLevel );
        relatedContentSpec.setChildrenLevel( childrenLevel );
        relatedContentSpec.setParentChildrenLevel( parentChildrenLevel );
        relatedContentSpec.setIncludeOnlyMainVersions( true );
        RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentSpec );

        if ( previewContext.isPreviewingContent() )
        {
            relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
            previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( relatedContent );
        }

        // Create the content xml
        final ContentXMLCreator xmlCreator = new ContentXMLCreator();
        xmlCreator.setResultIndexing( index, count );
        xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
        xmlCreator.setIncludeContentData( includeContentData );
        xmlCreator.setIncludeCategoryData( includeCategoryData );
        xmlCreator.setIncludeRelatedContentData( includeRelatedContentData );
        xmlCreator.setIncludeVersionsInfoForSites( false );
        xmlCreator.setIncludeAssignment( true );
        Document doc = xmlCreator.createContentsDocument( user, mainResultContent, relatedContent );

        addDataTraceInfo( doc );
        return doc;
    }

    private Document doGetContent( DataSourceContext context, int[] contentKeys, String query, String orderBy,
                                   int index, int count, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean includeOwnerAndModifierData, boolean includeContentData,
                                   boolean includeCategoryData, boolean includeRelatedContentData,
                                   boolean includeUserRights, int[] filterByCategories, boolean categoryRecursive,
                                   int[] filterByContentTypes )
    {

        UserEntity user = getUserEntity( context.getUser() );

        GetContentExecutor executor =
                new GetContentExecutor( contentService, contentDao, userDao, timeService.getNowAsDateTime(),
                                        context.getPreviewContext() );
        try
        {
            executor.user( user.getKey() );
            executor.query( query );
            executor.orderBy( orderBy );
            executor.index( index );
            executor.count( count );
            executor.parentLevel( parentLevel );
            executor.childrenLevel( childrenLevel );
            executor.parentChildrenLevel( parentChildrenLevel );
            executor.contentFilter( ContentKey.convertToList( contentKeys ) );
            executor.categoryFilter( CategoryKey.convertToList( filterByCategories ),
                                     categoryRecursive ? Integer.MAX_VALUE : 1 );
            executor.contentTypeFilter( ContentTypeKey.convertToList( filterByContentTypes ) );

            GetContentResult getContentResult = executor.execute();

            GetContentXmlCreator getContentXmlCreator =
                    new GetContentXmlCreator( new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
            getContentXmlCreator.user( user );
            getContentXmlCreator.startingIndex( index );
            getContentXmlCreator.resultLength( count );
            getContentXmlCreator.includeContentsContentData( includeContentData );
            getContentXmlCreator.includeRelatedContentsContentData( includeRelatedContentData );
            getContentXmlCreator.includeUserRights( includeUserRights );
            getContentXmlCreator.versionInfoStyle( GetContentXmlCreator.VersionInfoStyle.PORTAL );
            Document xml = getContentXmlCreator.create( getContentResult );
            addDataTraceInfo( xml );
            return xml;
        }
        catch ( InvalidKeyException e )
        {
            ContentXMLCreator xmlCreator = new ContentXMLCreator();
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private Document doGetContentVersion( DataSourceContext context, int[] versionKeys, int childrenLevel )
    {
        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        try
        {
            if ( versionKeys == null || versionKeys.length == 0 )
            {
                throw new IllegalArgumentException( "Missing one or more versionkeys" );
            }
            Date now = new Date();
            List<ContentVersionEntity> versions = new ArrayList<ContentVersionEntity>( versionKeys.length );
            UserEntity user = getUserEntity( context.getUser() );
            ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );
            for ( int versionKey : versionKeys )
            {
                ContentVersionKey key = new ContentVersionKey( versionKey );
                ContentVersionEntity version = contentVersionDao.findByKey( key );
                if ( version == null )
                {
                    continue;
                }
                final boolean mainVersionOnline = version.getContent().isOnline( now );
                final boolean versionCheckOK = version.isApproved() || version.isArchived() || version.isSnapshot();
                final boolean accessCheckOK = contentAccessResolver.hasReadContentAccess( user, version.getContent() );

                if ( mainVersionOnline && versionCheckOK && accessCheckOK )
                {
                    versions.add( version );
                }
            }

            RelatedChildrenContentQuery spec = new RelatedChildrenContentQuery( now );
            spec.setChildrenLevel( childrenLevel );
            spec.setContentVersions( versions );
            spec.setUser( user );

            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( spec );

            xmlCreator.setIncludeVersionsInfoForSites( true );
            xmlCreator.setIncludeAccessRightsInfo( true );
            xmlCreator.setIncludeUserRightsInfo( true, new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeOwnerAndModifierData( true );
            xmlCreator.setIncludeContentData( true );
            xmlCreator.setIncludeCategoryData( true );
            xmlCreator.setIncludeAssignment( true );

            return xmlCreator.createContentVersionsDocument( user, versions, relatedContent );

        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private Document doGetContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query,
                                             String orderBy, int index, int count, int childrenLevel, int parentLevel,
                                             int parentChildrenLevel, boolean includeOwnerAndModifierData,
                                             boolean includeContentData, boolean includeCategoryData,
                                             boolean includeRelatedContentData, boolean includeUserRights,
                                             int[] contentTypes )
    {
        final PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        Date now = timeService.getNowAsDateTime().toDate();
        ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
        try
        {
            contentByCategoryQuery.setCategoryKeyFilter( CategoryKey.convertToList( categoryKeys ), levels );
            contentByCategoryQuery.setContentTypeFilter( ContentTypeKey.convertToList( contentTypes ) );
            contentByCategoryQuery.setUser( user );
            contentByCategoryQuery.setQuery( query );
            contentByCategoryQuery.setOrderBy( orderBy );
            contentByCategoryQuery.setCount( count );
            contentByCategoryQuery.setIndex( index );
            contentByCategoryQuery.setFilterContentOnlineAt( now );

            ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
            relatedContentQuery.setIncludeOnlyMainVersions( true );
            relatedContentQuery.setOnlineCheckDate( now );

            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
            }

            xmlCreator.setResultIndexing( index, count );
            xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
            xmlCreator.setIncludeContentData( includeContentData );
            xmlCreator.setIncludeCategoryData( includeCategoryData );
            xmlCreator.setIncludeRelatedContentData( includeRelatedContentData );
            xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForSites( false );
            xmlCreator.setIncludeAssignment( true );

            Document doc = xmlCreator.createContentsDocument( user, contents, relatedContent );
            addDataTraceInfo( doc );
            return doc;

        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private Document doGetContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query,
                                            String orderBy, int fromIndex, int count, int parentLevel,
                                            int childrenLevel, int parentChildrenLevel,
                                            boolean includeOwnerAndModifierData, boolean includeContentData,
                                            boolean includeCategoryData, boolean includeRelatedContentData,
                                            boolean includeUserRights, int[] filterByContentTypes )
    {
        PreviewContext previewContext = context.getPreviewContext();
        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        ContentBySectionQuery spec = new ContentBySectionQuery();
        final Date now = new Date();

        try
        {
            spec.setMenuItemKeys( MenuItemKey.converToList( menuItemKeys ) );
            spec.setContentTypeFilter( ContentTypeKey.convertToList( filterByContentTypes ) );

            spec.setUser( user );
            //spec.setApprovedSectionContentOnly( true );
            spec.setSectionFilterStatus( SectionFilterStatus.APPROVED_ONLY );
            spec.setLevels( levels );
            spec.setIndex( fromIndex );
            spec.setCount( count );
            spec.setQuery( query );
            spec.setOrderBy( orderBy );
            spec.setFilterContentOnlineAt( now );

            xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
            xmlCreator.setIncludeContentData( includeContentData );
            xmlCreator.setIncludeCategoryData( includeCategoryData );
            xmlCreator.setIncludeRelatedContentData( includeRelatedContentData );
            xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setResultIndexing( fromIndex, count );
            xmlCreator.setIncludeVersionsInfoForSites( false );
            xmlCreator.setIncludeAssignment( true );

            ContentResultSet contents = contentService.queryContent( spec );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
            relatedContentQuery.setIncludeOnlyMainVersions( true );
            relatedContentQuery.setFilterContentOnlineAt( now );

            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            Document document = xmlCreator.createContentsDocument( user, contents, relatedContents );
            addDataTraceInfo( document );
            return document;
        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
        catch ( InvalidContentBySectionQueryException e )
        {
            return xmlCreator.createEmptyDocument( e.getMessage() );
        }
    }

    private Document doGetRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels,
                                                  String query, int count, int parentLevel, int childrenLevel,
                                                  int parentChildrenLevel, boolean includeOwnerAndModifierData,
                                                  boolean includeContentData, boolean includeCategoryData,
                                                  boolean includeRelatedContentData, boolean includeUserRights )
    {
        PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        final Date now = new Date();
        ContentBySectionQuery spec = new ContentBySectionQuery();
        try
        {
            spec.setMenuItemKeys( MenuItemKey.converToList( menuItemKeys ) );
        }
        catch ( Exception e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
        spec.setUser( user );
        spec.setSectionFilterStatus( SectionFilterStatus.APPROVED_ONLY );
        spec.setLevels( levels );
        spec.setIndex( 0 );
        spec.setCount( Integer.MAX_VALUE );
        spec.setQuery( query );
        spec.setFilterContentOnlineAt( now );

        ContentResultSet contents = contentService.queryContent( spec );
        if ( previewContext.isPreviewingContent() )
        {
            contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
        }
        ContentResultSet randomContent = contents.createRandomizedResult( count );

        RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
        relatedContentQuery.setUser( user );
        relatedContentQuery.setContentResultSet( randomContent );
        relatedContentQuery.setParentLevel( parentLevel );
        relatedContentQuery.setChildrenLevel( childrenLevel );
        relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
        relatedContentQuery.setIncludeOnlyMainVersions( true );
        relatedContentQuery.setFilterContentOnlineAt( now );

        RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
        }

        xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
        xmlCreator.setIncludeContentData( includeContentData );
        xmlCreator.setIncludeCategoryData( includeCategoryData );
        xmlCreator.setIncludeRelatedContentData( includeRelatedContentData );
        xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                             new ContentAccessResolver( groupDao ) );
        xmlCreator.setResultIndexing( 0, count );
        xmlCreator.setIncludeVersionsInfoForSites( false );
        xmlCreator.setIncludeAssignment( true );

        Document doc = xmlCreator.createContentsDocument( user, randomContent, relatedContent );
        addDataTraceInfo( doc );
        return doc;
    }

    private int getSiteKeyByMenuItemKey( int menuItemKey )
    {
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        if ( menuItem != null )
        {
            return menuItem.getSite().getKey().toInt();
        }
        return -1;
    }

    private Document doGetMenu( UserEntity user, int siteKey, int menuItemKey, int levels )
    {
        SiteEntity site = siteDao.findByKey( new SiteKey( siteKey ) );

        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( new MenuItemAccessResolver( groupDao ) );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
        siteXmlCreator.setUser( user );
        siteXmlCreator.setActiveMenuItem( menuItemDao.findByKey( menuItemKey ) );
        siteXmlCreator.setMenuItemLevels( levels );

        return siteXmlCreator.createLegacyGetMenu( site, sitePropertiesService.getSiteProperties( site.getKey() ) );
    }

    private Document doGetMenuData( int siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey );
        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( new MenuItemAccessResolver( groupDao ) );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
        siteXmlCreator.setIncludeDeviceClassResolverInfo( true );
        return siteXmlCreator.createLegacyGetMenuData( site, sitePropertiesService.getSiteProperties( site.getKey() ) );
    }

    private Document doGetMenuBranch( UserEntity user, int menuItemKey, boolean topLevel, int startLevel, int levels )
    {
        if ( menuItemKey < 0 )
        {
            return SiteXmlCreator.createEmptyMenuBranch();
        }

        int siteKey = getSiteKeyByMenuItemKey( menuItemKey );
        SiteEntity site = siteDao.findByKey( siteKey );
        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( new MenuItemAccessResolver( groupDao ) );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );

        siteXmlCreator.setMenuItemInBranch( menuItemDao.findByKey( menuItemKey ) );
        siteXmlCreator.setActiveMenuItem( menuItemDao.findByKey( menuItemKey ) );
        siteXmlCreator.setMenuItemLevels( levels );
        siteXmlCreator.setBranchStartLevel( startLevel );
        siteXmlCreator.setIncludeTopLevel( topLevel );
        siteXmlCreator.setUser( user );

        return siteXmlCreator.createLegacyGetMenuBranch( site );
    }

    private Document doGetMenuItem( UserEntity user, int menuItemKey, boolean includeParents )
    {
        MenuItemXMLCreatorSetting setting = new MenuItemXMLCreatorSetting();
        setting.user = user;
        setting.includeParents = includeParents;
        MenuItemXmlCreator creator = new MenuItemXmlCreator( setting, new MenuItemAccessResolver( groupDao ) );
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        return creator.createLegacyGetMenuItem( menuItem );
    }

    private Document doGetSubMenu( UserEntity user, int menuItemKey, int activeMenuItemKey, int levels )
    {
        if ( menuItemKey < 0 )
        {
            return SiteXmlCreator.createEmptyMenuBranch();
        }

        int siteKey = getSiteKeyByMenuItemKey( menuItemKey );
        SiteEntity site = siteDao.findByKey( siteKey );
        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( new MenuItemAccessResolver( groupDao ) );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
        siteXmlCreator.setUser( user );
        siteXmlCreator.setMenuItemInBranch( menuItemDao.findByKey( menuItemKey ) );
        siteXmlCreator.setMenuItemLevels( levels );
        if ( activeMenuItemKey > -1 )
        {
            siteXmlCreator.setActiveMenuItem( menuItemDao.findByKey( activeMenuItemKey ) );
        }

        return siteXmlCreator.createLegacyGetSubMenu( site );
    }

    /**
     * Adds traceInfo to a content document before it's returned.
     *
     * @param doc A JDom document with all information that needs to be traced.
     */
    @SuppressWarnings("unchecked")
    private void addDataTraceInfo( Document doc )
    {
        DataTraceInfo traceInfo = RenderTrace.getCurrentDataTraceInfo();
        if ( traceInfo != null )
        {
            Element root = doc.getRootElement();
            List<Element> contentNodes = root.getChildren( "content" );
            for ( Element e : contentNodes )
            {
                Integer key = Integer.parseInt( e.getAttributeValue( "key" ) );
                Element firstChild = (Element) e.getChildren( "title" ).get( 0 );
                String title = firstChild.getText();
                traceInfo.addContentInfo( key, title );
            }
            Element relatedContentsNode = root.getChild( "relatedcontents" );

            if ( relatedContentsNode != null )
            {
                List<Element> relatedContentNodes = relatedContentsNode.getChildren( "content" );
                if ( relatedContentsNode != null )
                {
                    for ( Element e : relatedContentNodes )
                    {
                        Integer key = Integer.parseInt( e.getAttributeValue( "key" ) );
                        Element firstChild = (Element) e.getChildren( "title" ).get( 0 );
                        String title = firstChild.getText();
                        traceInfo.addRelatedContentInfo( key, title );
                    }
                }
            }
        }
    }

    private UserEntity getUserEntity( User user )
    {
        return userDao.findByKey( user.getKey() );
    }

    public void setContentService( ContentService service )
    {
        this.contentService = service;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public void setPreferenceService( PreferenceService preferenceService )
    {
        this.preferenceService = preferenceService;
    }

    public void setCalendarService( CalendarService service )
    {
        calendarService = service;
    }

    public void setCountryService( CountryService countryService )
    {
        this.countryService = countryService;
    }

    public void setLocaleService( LocaleService localeService )
    {
        this.localeService = localeService;
    }

    public void setTimeZoneService( TimeZoneService timeZoneService )
    {
        this.timeZoneService = timeZoneService;
    }

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setUserStoreService( UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }

    public void setUserStoreParser( UserStoreParser userStoreParser )
    {
        this.userStoreParser = userStoreParser;
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    private org.w3c.dom.Document getURL( String address, String encoding, int timeoutMs )
    {
        InputStream in = null;
        BufferedReader reader = null;
        org.w3c.dom.Document result;
        try
        {
            URL url = new URL( address );
            URLConnection urlConn = url.openConnection();
            urlConn.setConnectTimeout( timeoutMs > 0 ? timeoutMs : DEFAULT_CONNECTION_TIMEOUT );
            urlConn.setRequestProperty( "User-Agent", VerticalProperties.getVerticalProperties().getDataSourceUserAgent() );
            String userInfo = url.getUserInfo();
            if ( StringUtils.isNotBlank( userInfo ) )
            {
                String userInfoBase64Encoded = new String( Base64.encodeBase64( userInfo.getBytes() ) );
                urlConn.setRequestProperty( "Authorization", "Basic " + userInfoBase64Encoded );
            }
            in = urlConn.getInputStream();

            // encoding == null: XML file
            if ( encoding == null )
            {
                result = XMLTool.domparse( in );
            }
            else
            {
                StringBuffer sb = new StringBuffer( 1024 );
                reader = new BufferedReader( new InputStreamReader( in, encoding ) );
                char[] line = new char[1024];
                int charCount = reader.read( line );
                while ( charCount > 0 )
                {
                    sb.append( line, 0, charCount );
                    charCount = reader.read( line );
                }

                result = XMLTool.createDocument( "urlresult" );
                org.w3c.dom.Element root = result.getDocumentElement();
                XMLTool.createCDATASection( result, root, sb.toString() );
            }
        }
        catch ( SocketTimeoutException ste )
        {
            String message = "Socket timeout when trying to get url: " + address;
            LOG.warn( message );
            result = null;
        }
        catch ( IOException ioe )
        {
            String message = "Failed to get URL: %t";
            LOG.warn( StringUtil.expandString( message, null, ioe ), ioe );
            result = null;
        }
        catch ( RuntimeException re )
        {
            String message = "Failed to get URL: %t";
            LOG.warn( StringUtil.expandString( message, null, re ), re );
            result = null;
        }
        finally
        {
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
                else if ( in != null )
                {
                    in.close();
                }
            }
            catch ( IOException ioe )
            {
                String message = "Failed to close URL connection: %t";
                LOG.warn( StringUtil.expandString( message, null, ioe ), ioe );
            }
        }

        if ( result == null )
        {
            result = XMLTool.createDocument( "noresult" );
        }

        return result;
    }
}
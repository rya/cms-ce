/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.vertical.engine.PresentationEngine;
import com.enonic.vertical.engine.filters.ContentFilter;

import com.enonic.cms.framework.time.TimeService;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.CountryXmlCreator;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.core.calendar.CalendarService;
import com.enonic.cms.business.core.content.ContentService;
import com.enonic.cms.business.core.content.ContentXMLCreator;
import com.enonic.cms.business.core.content.GetContentExecutor;
import com.enonic.cms.business.core.content.GetContentResult;
import com.enonic.cms.business.core.content.GetContentXmlCreator;
import com.enonic.cms.business.core.content.access.ContentAccessResolver;
import com.enonic.cms.business.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.business.core.preferences.PreferenceService;
import com.enonic.cms.business.core.security.SecurityService;
import com.enonic.cms.business.core.security.UserStoreParser;
import com.enonic.cms.business.core.security.userstore.UserStoreService;
import com.enonic.cms.business.core.structure.MenuItemXMLCreatorSetting;
import com.enonic.cms.business.core.structure.MenuItemXmlCreator;
import com.enonic.cms.business.core.structure.SiteXmlCreator;
import com.enonic.cms.business.core.structure.access.MenuItemAccessResolver;
import com.enonic.cms.business.locale.LocaleService;
import com.enonic.cms.business.locale.LocaleXmlCreator;
import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.business.preview.PreviewContext;
import com.enonic.cms.business.timezone.TimeZoneService;
import com.enonic.cms.business.timezone.TimeZoneXmlCreator;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.InvalidKeyException;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.contenttype.ContentTypeKey;
import com.enonic.cms.domain.content.index.ContentIndexQuery.SectionFilterStatus;
import com.enonic.cms.domain.content.query.ContentByCategoryQuery;
import com.enonic.cms.domain.content.query.ContentByContentQuery;
import com.enonic.cms.domain.content.query.ContentByQueryQuery;
import com.enonic.cms.domain.content.query.ContentBySectionQuery;
import com.enonic.cms.domain.content.query.InvalidContentBySectionQueryException;
import com.enonic.cms.domain.content.query.RelatedChildrenContentQuery;
import com.enonic.cms.domain.content.query.RelatedContentQuery;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.domain.portal.ShoppingCart;
import com.enonic.cms.domain.portal.datasource.DataSourceContext;
import com.enonic.cms.domain.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.domain.preference.PreferenceEntity;
import com.enonic.cms.domain.preference.PreferenceKey;
import com.enonic.cms.domain.preference.PreferenceScope;
import com.enonic.cms.domain.preference.PreferenceScopeResolver;
import com.enonic.cms.domain.preference.PreferenceSpecification;
import com.enonic.cms.domain.preference.PreferenceUniqueMatchResolver;
import com.enonic.cms.domain.preference.PreferenceXmlCreator;
import com.enonic.cms.domain.security.user.QualifiedUsername;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserXmlCreator;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreNotFoundException;
import com.enonic.cms.domain.security.userstore.UserStoreXmlCreator;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

public final class DataSourceServiceImpl
        implements DataSourceService
{
    private static final Logger LOG = LoggerFactory.getLogger( DataSourceServiceImpl.class );

    private CalendarService calendarService;

    private ContentService contentService;

    private PreferenceService preferenceService;

    private PresentationEngine presentationEngine;

    private SecurityService securityService;

    @Autowired
    private ContentVersionDao contentVersionDao;

    private ContentDao contentDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private MenuItemDao menuItemDao;

    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    private SitePropertiesService sitePropertiesService;

    private CountryService countryService;

    private LocaleService localeService;

    private TimeZoneService timeZoneService;

    private TimeService timeService;

    private UserStoreService userStoreService;

    private UserStoreParser userStoreParser;

    public XMLDocument getContentByQuery( DataSourceContext context, String query, String orderBy, int index, int count,
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
                relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            xmlCreator.setResultIndexing( index, count );
            xmlCreator.setIncludeOwnerAndModifierData( true );
            xmlCreator.setIncludeContentData( includeData );
            xmlCreator.setIncludeCategoryData( true );
            xmlCreator.setIncludeRelatedContentData( includeData );
            xmlCreator.setIncludeUserRightsInfo( false, new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForSites( false );
            xmlCreator.setIncludeAssignment( true );
            XMLDocument xml = xmlCreator.createContentsDocument( user, contents, relatedContents );
            addDataTraceInfo( xml.getAsJDOMDocument() );
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
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, String query, String orderBy, int index, int count,
                                   boolean includeData, int childrenLevel, int parentLevel )
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
    public XMLDocument getContentVersion( DataSourceContext context, int[] versionKeys, int childrenLevel )
    {
        return doGetContentVersion( context, versionKeys, childrenLevel );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query,
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
    public XMLDocument getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query,
                                            String orderBy, int index, int count, boolean includeData, int childrenLevel, int parentLevel )
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
    public XMLDocument getRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, int count,
                                                  boolean includeData, int childrenLevel, int parentLevel )
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
    public XMLDocument getContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query,
                                             String orderBy, int index, int count, boolean includeData, int childrenLevel, int parentLevel )
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
    public XMLDocument getRandomContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, int count,
                                                   boolean includeData, int childrenLevel, int parentLevel )
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

        XMLDocument doc = xmlCreator.createContentsDocument( user, randomContents, relatedContent );

        addDataTraceInfo( doc.getAsJDOMDocument() );
        return doc;
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getCalendar( DataSourceContext context, boolean relative, int year, int month, int count, boolean includeWeeks,
                                    boolean includeDays, String language, String country )
    {
        return XMLDocumentFactory.create(
                calendarService.getCalendar( relative, year, month, count, includeWeeks, includeDays, language, country ) );
    }

    public XMLDocument getCountries( DataSourceContext context, String[] countryCodes, boolean includeRegions )
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
        return XMLDocumentFactory.create( countryXmlCreator.createCountriesDocument( countries ) );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
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
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
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
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
                                   int parentChildrenLevel, boolean updateStatistics, boolean includeUserRights, int[] filterByCategories,
                                   boolean categoryRecursive, int[] filterByContentTypes )
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
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
                                   int parentChildrenLevel, boolean updateStatistics, boolean relatedTitlesOnly, boolean includeUserRights,
                                   int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes )
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
    public XMLDocument getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String orderBy,
                                            int fromIndex, int count, boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                            boolean relatedTitlesOnly, boolean includeTotalCount, boolean includeUserRights,
                                            int[] filterByContentTypes )
    {
        return doGetContentBySection( context, menuItemKeys, levels, "", orderBy, fromIndex, count, parentLevel,
                                      childrenLevel, parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly, !relatedTitlesOnly, includeUserRights,
                                      filterByContentTypes );
    }


    /**
     * @inheritDoc
     */
    public XMLDocument getContentBySection( DataSourceContext context, String query, int[] menuItemKeys, int levels,
                                            String orderBy, int fromIndex, int count, boolean titlesOnly, int parentLevel, int childrenLevel,
                                            int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                            boolean includeUserRights, int[] filterByContentTypes )
    {
        return doGetContentBySection( context, menuItemKeys, levels, query, orderBy, fromIndex, count, parentLevel,
                                      childrenLevel, parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly, !relatedTitlesOnly, includeUserRights,
                                      filterByContentTypes );

    }

    /**
     * @inheritDoc
     */
    public XMLDocument getFormattedDate( DataSourceContext context, int offset, String dateformat, String language,
                                         String country )
    {
        return presentationEngine.getFormattedDate( offset, dateformat, language, country );
    }

    /**
     * Reserved for special use.
     */
    public XMLDocument getFusionBotQuery( DataSourceContext context, String fusionBotUrl, String query, int siteNum, int page )
    {
        return XMLDocumentFactory.create( presentationEngine.getFusionBotQuery( fusionBotUrl, query, siteNum, page ) );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getMenu( DataSourceContext context, int menuKey, int tagItem, int levels )
    {
        return doGetMenu( getUserEntity( context.getUser() ), menuKey, tagItem, levels );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getMenu( DataSourceContext context, int menuKey, int tagItem, int levels, boolean details )
    {
        // param "details" not in use
        return doGetMenu( getUserEntity( context.getUser() ), menuKey, tagItem, levels );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getMenu( DataSourceContext context, int menuItemKey, int levels )
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
    public XMLDocument getMenuData( DataSourceContext context, int menuId )
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
    public XMLDocument getMenuData( DataSourceContext context )
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
    public XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel )
    {
        return doGetMenuBranch( getUserEntity( context.getUser() ), menuItem, topLevel, 0, 0 );
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, int startLevel, int levels )
    {
        return doGetMenuBranch( getUserEntity( context.getUser() ), menuItem, topLevel, startLevel, levels );
    }

    /**
     * Return the menu branch.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, boolean details )
    {
        // param "details" not in use
        return doGetMenuBranch( getUserEntity( context.getUser() ), menuItem, topLevel, 0, 0 );
    }

    /**
     * Not in use.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getMenuItem( DataSourceContext context, int key, boolean withParents )
    {
        return doGetMenuItem( getUserEntity( context.getUser() ), key, withParents );
    }

    /**
     * Get a menu item.
     *
     * @param context     the Vertical Site context
     * @param key         a menu item key
     * @param withParents if true, include parents up to top level (i.e.: it's path)
     * @param complete    include the full menu item
     * @return menu item xml
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getMenuItem( DataSourceContext context, int key, boolean withParents, boolean complete )
    {
        // param "complete" not in use
        return doGetMenuItem( getUserEntity( context.getUser() ), key, withParents );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getRandomContentByParent( DataSourceContext context, int count, int contentKey, boolean includeUserRights )
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
        XMLDocument doc = xmlCreator.createContentsDocument( user, randomizedContents, new RelatedContentResultSetImpl() );

        addDataTraceInfo( doc.getAsJDOMDocument() );
        return doc;
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getRandomContentBySections( DataSourceContext context, String query, int[] menuItemKeys, int levels, int count,
                                                   boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                                   boolean relatedTitlesOnly, boolean includeUserRights )
    {
        return doGetRandomContentBySection( context, menuItemKeys, levels, query, count, parentLevel, childrenLevel,
                                            parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly, !relatedTitlesOnly, includeUserRights );
    }

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem )
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
    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem, int levels )
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
    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem, int levels, boolean details )
    {
        // param "details" not in use
        return doGetSubMenu( getUserEntity( context.getUser() ), key, tagItem, levels );
        //return presentationEngine.getSubMenu( context.getUser(), key, tagItem, levels );
    }

    /**
     * Get a list of category forming a path to a category.
     *
     * @param context          the Vertical Site context
     * @param categoryKey      a category key
     * @param withContentCount if true, include content count for each category
     * @param includeCategory  if true, include the root category
     * @return category xml
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getSuperCategoryNames( DataSourceContext context, int categoryKey, boolean withContentCount,
                                              boolean includeCategory )
    {
        return XMLDocumentFactory.create( presentationEngine.getSuperCategoryNames( categoryKey, withContentCount, includeCategory ) );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getURLAsText( DataSourceContext context, String url, String encoding )
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
    public XMLDocument getURLAsText( DataSourceContext context, String url, String encoding, int timeout )
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

        return XMLDocumentFactory.create( presentationEngine.getURLAsText( url, encoding, timeout ) );
    }

    /**
     * Makes a connection to a url that has an xml as result.
     */
    public XMLDocument getURLAsXML( DataSourceContext context, String url )
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
    public XMLDocument getURLAsXML( DataSourceContext context, String url, int timeout )
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

        return XMLDocumentFactory.create( presentationEngine.getURLAsXML( url, timeout ) );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getSections( DataSourceContext context, int superSectionKey, int level, boolean includeSection )
    {
        return XMLDocumentFactory.create( presentationEngine.getSections( context.getUser(), superSectionKey, level, includeSection ) );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getSections( DataSourceContext context )
    {
        return XMLDocumentFactory.create( presentationEngine.getSections( context.getUser(), context.getSiteKey() ) );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getIndexValues( DataSourceContext context, String path, int[] categories, boolean includeSubCategories,
                                       int[] contentTypes, int index, int count, boolean distinct, String order )
    {
        UserEntity user = getUserEntity( context.getUser() );
        boolean descOrder = order != null && order.equalsIgnoreCase( "desc" );
        Collection<CategoryKey> categoryFilter = CategoryKey.convertToList( categories );
        Collection<ContentTypeKey> contentTypeFilter = ContentTypeKey.convertToList( contentTypes );
        return contentService.getIndexValues( user, path, categoryFilter, includeSubCategories, contentTypeFilter, index, count,
                                              descOrder );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getAggregatedIndexValues( DataSourceContext context, String path, int[] categories, boolean includeSubCategories,
                                                 int[] contentTypes )
    {
        UserEntity user = getUserEntity( context.getUser() );

        Collection<CategoryKey> categoryFilter = CategoryKey.convertToList( categories );
        Collection<ContentTypeKey> contentTypeFilter = ContentTypeKey.convertToList( contentTypes );

        return contentService.getAggregatedIndexValues( user, path, categoryFilter, includeSubCategories,
                                                        contentTypeFilter );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                             String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                             int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                             boolean includeUserRights, int[] contentTypes )
    {
        int levels = includeSubCategories ? Integer.MAX_VALUE : 1;
        return doGetContentByCategory( context, categories, levels, query, orderBy, index, count, childrenLevel,
                                       parentLevel, parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly, !relatedTitlesOnly, includeUserRights,
                                       contentTypes );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getMyContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                               String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                               int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                               boolean includeUserRights, int[] contentTypes )
    {
        PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        if ( ( user != null ) && ( !user.isAnonymous() ) )
        {
            String ownerQuery = "owner/@key = '" + user.getKey() + "'";
            if ( ( query == null ) || ( query.trim().length() == 0 ) )
            {
                query = ownerQuery;
            }
            else
            {
                query = "(" + query + ") AND " + ownerQuery;
            }
        }
        else
        {
            count = 0;
        }

        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        XMLDocument doc;
        if ( count == 0 )
        {
            doc = xmlCreator.createEmptyDocument( "My content is not available for anonymous" );
        }
        else
        {
            Collection<CategoryKey> categoryFilter = CategoryKey.convertToList( categories );
            Collection<ContentTypeKey> contentTypeFilter = ContentTypeKey.convertToList( contentTypes );
            final Date now = new Date();

            ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
            contentByCategoryQuery.setUser( user );
            contentByCategoryQuery.setCategoryKeyFilter( categoryFilter, includeSubCategories ? Integer.MAX_VALUE : 1 );
            contentByCategoryQuery.setQuery( query );
            contentByCategoryQuery.setOrderBy( orderBy );
            contentByCategoryQuery.setContentTypeFilter( contentTypeFilter );
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

            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            xmlCreator.setIncludeContentData( !titlesOnly );
            xmlCreator.setIncludeRelatedContentData( !relatedTitlesOnly );
            xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setResultIndexing( index, count );
            xmlCreator.setIncludeVersionsInfoForSites( false );
            xmlCreator.setIncludeAssignment( true );
            doc = xmlCreator.createContentsDocument( user, contents, relatedContents );
        }
        addDataTraceInfo( doc.getAsJDOMDocument() );
        return doc;
    }

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
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument findContentByCategory( DataSourceContext context, String search, String operator, int[] categories,
                                              boolean includeSubCategories, String orderBy, int index, int count, boolean titlesOnly,
                                              int childrenLevel, int parentLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                              boolean includeTotalCount, boolean includeUserRights, int[] contentTypes )
    {
        PreviewContext previewContext = context.getPreviewContext();

        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        if ( ( search == null ) || ( search.length() == 0 ) )
        {
            return xmlCreator.createEmptyDocument( "Need a search string to find content." );
        }

        UserEntity user = getUserEntity( context.getUser() );
        Collection<CategoryKey> categoryKeys = CategoryKey.convertToList( categories );
        Collection<ContentTypeKey> contentTypeKeys = ContentTypeKey.convertToList( contentTypes );
        boolean opAnd = ( operator != null ) && "AND".equalsIgnoreCase( operator );
        String query = convertSimpleSearch( search, opAnd );
        final Date now = new Date();

        ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
        contentByCategoryQuery.setUser( user );
        contentByCategoryQuery.setCategoryKeyFilter( categoryKeys, includeSubCategories ? Integer.MAX_VALUE : 1 );
        contentByCategoryQuery.setQuery( query );
        contentByCategoryQuery.setOrderBy( orderBy );
        contentByCategoryQuery.setContentTypeFilter( contentTypeKeys );
        contentByCategoryQuery.setIndex( index );
        contentByCategoryQuery.setCount( count );
        contentByCategoryQuery.setFilterContentOnlineAt( now );
        contentByCategoryQuery.setFilterAdminBrowseOnly( false );

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

        RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
        }

        xmlCreator.setResultIndexing( index, count );
        xmlCreator.setIncludeContentData( !titlesOnly );
        xmlCreator.setIncludeRelatedContentData( !relatedTitlesOnly );
        xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                             new ContentAccessResolver( groupDao ) );
        xmlCreator.setIncludeVersionsInfoForSites( false );
        xmlCreator.setIncludeAssignment( true );
        return xmlCreator.createContentsDocument( user, contents, relatedContents );
    }

    private String convertSimpleSearch( String search, boolean opAnd )
    {
        String operator = opAnd ? " AND " : " OR ";
        StringBuffer query = new StringBuffer();

        if ( search != null )
        {
            HashSet<String> params = new HashSet<String>();
            StringTokenizer tok = new StringTokenizer( search, " " );

            while ( tok.hasMoreTokens() )
            {
                String param = tok.nextToken();
                if ( param.length() > 0 )
                {
                    params.add( param );
                }
            }

            for ( Iterator<String> i = params.iterator(); i.hasNext(); )
            {
                String param = i.next();
                query.append( "((title CONTAINS \"" ).append( param ).append( "\") OR " );
                query.append( "(* CONTAINS \"" ).append( param ).append( "\"))" );
                if ( i.hasNext() )
                {
                    query.append( operator );
                }
            }
        }

        return query.toString();
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getCategories( DataSourceContext context, int superCategoryKey, int level, boolean withContentCount,
                                      boolean includeCategory )
    {
        org.w3c.dom.Document doc =
                presentationEngine.getCategories( context.getUser(), superCategoryKey, level, includeCategory, true,
                                                  true, withContentCount );

        DataSourceServiceCompabilityKeeper.fixCategoriesCompability( doc );
        return XMLDocumentFactory.create( doc );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getCategories( DataSourceContext context, int key, int levels, boolean topLevel, boolean details,
                                      boolean catCount, boolean contentCount )
    {
        return XMLDocumentFactory.create(
                presentationEngine.getCategories( context.getUser(), key, levels, topLevel, details, catCount,
                                                  contentCount ) );
    }


    public XMLDocument getSearchBloxQuery( DataSourceContext context, String searchBloxURL, String query, int collectionId, int page,
                                           String sort )
    {
        return XMLDocumentFactory.create( presentationEngine.getSearchBloxQuery( searchBloxURL, query, collectionId, page, sort ) );
    }

    public XMLDocument getSearchBloxQuery( DataSourceContext context, String searchBloxURL, String queryAll, String queryExactPhrase,
                                           String queryLeastOneWord, String queryWithoutTheWords, String language, String contentType,
                                           int startDate, String occurance, int collectionId, int page, int pageSize,
                                           String sort )
    {
        return XMLDocumentFactory.create(
                presentationEngine.getSearchBloxQuery( searchBloxURL, queryAll, queryExactPhrase, queryLeastOneWord,
                                                       queryWithoutTheWords, language, contentType, startDate, occurance, collectionId, page, pageSize, sort ) );
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public XMLDocument getUserstore( final DataSourceContext context, final String userstore )
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

        return XMLDocumentFactory.create( userstoreDoc );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getUser( DataSourceContext context, String qualifiedUsername, boolean includeMemberships, boolean normalizeGroups,
                                boolean includeCustomUserFields )
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

        return XMLDocumentFactory.create( userDoc );
    }


    /**
     * @inheritDoc
     */
    public XMLDocument getShoppingCartContents( DataSourceContext context, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                                boolean updateStatistics, int[] filterByCategories, boolean categoryRecursive,
                                                int[] filterByContentTypes )
    {

        ShoppingCart cart = context.getShoppingCart();
        if ( cart != null )
        {
            int[] contentKeys = cart.toIdArray();
            if ( contentKeys.length > 0 )
            {
                ContentFilter contentFilter = new ContentFilter( filterByCategories, categoryRecursive, filterByContentTypes );
                return XMLDocumentFactory.create(
                        presentationEngine.getContents( context.getUser(), contentKeys, parentLevel, childrenLevel,
                                                        parentChildrenLevel, false, false, contentFilter ) );
            }
        }

        return XMLDocumentFactory.create( "<contents/>" );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getPreferences( DataSourceContext context, String scope, String wildCardKey, boolean uniqueMatch )
    {

        return doGetPreferences( context, scope, wildCardKey, uniqueMatch );
    }


    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getPreferences( DataSourceContext context, String scope, String wildCardKey )
    {

        return doGetPreferences( context, scope, wildCardKey, true );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getPreferences( DataSourceContext context, String scope )
    {

        return doGetPreferences( context, scope, null, true );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getPreferences( DataSourceContext context )
    {

        return doGetPreferences( context, null, null, true );
    }

    private XMLDocument doGetPreferences( DataSourceContext context, String scope, String wildCardKey, boolean uniqueMatch )
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
                    PreferenceScopeResolver.resolveScopes( scope, context.getPortalInstanceKey(), context.getSiteKey() );

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
    public XMLDocument getPageContent( DataSourceContext context, int menuItemId, int parentLevel, int childrenLevel,
                                       int parentChildrenLevel, boolean updateStatistics, boolean includeUserRights )
    {
        return doGetPageContent( context, menuItemId, parentLevel, childrenLevel, parentChildrenLevel, includeUserRights );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public XMLDocument getPageContent( DataSourceContext context, int menuItemId, int parentLevel, int childrenLevel,
                                       int parentChildrenLevel, boolean updateStatistics )
    {
        return doGetPageContent( context, menuItemId, parentLevel, childrenLevel, parentChildrenLevel, false );
    }

    private XMLDocument doGetPageContent( DataSourceContext context, int menuItemId, int parentLevel, int childrenLevel,
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

        XMLDocument xml = xmlCreator.createContentsDocument( user, content, relatedContent );
        addDataTraceInfo( xml.getAsJDOMDocument() );

        return xml;
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String orderBy,
                                           boolean requireAll, int fromIndex, int count, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                           boolean includeTotalCount, int[] filterByCategories, boolean categoryRecursive,
                                           int[] filterByContentTypes )
    {
        return doGetRelatedContent( context, contentKeys, relation, null, orderBy, requireAll, fromIndex, count,
                                    parentLevel, childrenLevel, parentChildrenLevel, true, true, true, true,
                                    filterByCategories, categoryRecursive, filterByContentTypes );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String orderBy,
                                           boolean requireAll, int fromIndex, int count, boolean titlesOnly, int parentLevel, int childrenLevel,
                                           int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                           int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes )
    {
        return doGetRelatedContent( context, contentKeys, relation, null, orderBy, requireAll, fromIndex, count,
                                    parentLevel, childrenLevel, parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly, !relatedTitlesOnly, filterByCategories,
                                    categoryRecursive, filterByContentTypes );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String query,
                                           String orderBy, boolean requireAll, int fromIndex, int count, boolean titlesOnly, int parentLevel,
                                           int childrenLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                           boolean includeTotalCount, int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes )
    {
        return doGetRelatedContent( context, contentKeys, relation, query, orderBy, requireAll, fromIndex, count,
                                    parentLevel, childrenLevel, parentChildrenLevel, !titlesOnly, !titlesOnly, !titlesOnly, !relatedTitlesOnly,
                                    filterByCategories, categoryRecursive, filterByContentTypes );
    }


    public XMLDocument getLocales( DataSourceContext context )
    {
        Locale[] locales = localeService.getLocales();
        LocaleXmlCreator localeXmlCreator = new LocaleXmlCreator();
        return XMLDocumentFactory.create( localeXmlCreator.createLocalesDocument( locales ) );
    }

    public XMLDocument getTimeZones( DataSourceContext context )
    {
        Collection<DateTimeZone> timeZones = timeZoneService.getTimeZones();
        DateTime now = timeService.getNowAsDateTime();
        TimeZoneXmlCreator timeZoneXmlCreator = new TimeZoneXmlCreator( now );
        return XMLDocumentFactory.create( timeZoneXmlCreator.createTimeZonesDocument( timeZones ) );
    }

    private XMLDocument doGetRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query,
                                             String orderBy, boolean requireAll, int index, int count, int parentLevel,
                                             int childrenLevel, int parentChildrenLevel, boolean includeOwnerAndModifierData, boolean includeContentData,
                                             boolean includeCategoryData, boolean includeRelatedContentData, int[] filterByCategories,
                                             boolean categoryRecursive, int[] filterByContentTypes )
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
        XMLDocument doc = xmlCreator.createContentsDocument( user, mainResultContent, relatedContent );

        addDataTraceInfo( doc.getAsJDOMDocument() );
        return doc;
    }

    private XMLDocument doGetContent( DataSourceContext context, int[] contentKeys, String query, String orderBy, int index, int count,
                                      int parentLevel, int childrenLevel, int parentChildrenLevel, boolean includeOwnerAndModifierData,
                                      boolean includeContentData, boolean includeCategoryData, boolean includeRelatedContentData,
                                      boolean includeUserRights, int[] filterByCategories, boolean categoryRecursive,
                                      int[] filterByContentTypes )
    {

        UserEntity user = getUserEntity( context.getUser() );

        GetContentExecutor executor =
                new GetContentExecutor( contentService, contentDao, userDao, timeService.getNowAsDateTime(), context.getPreviewContext() );
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
            XMLDocument xml = getContentXmlCreator.create( getContentResult );
            addDataTraceInfo( xml.getAsJDOMDocument() );
            return xml;
        }
        catch ( InvalidKeyException e )
        {
            ContentXMLCreator xmlCreator = new ContentXMLCreator();
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private XMLDocument doGetContentVersion( DataSourceContext context, int[] versionKeys, int childrenLevel )
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

    private XMLDocument doGetContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query,
                                                String orderBy, int index, int count, int childrenLevel, int parentLevel, int parentChildrenLevel,
                                                boolean includeOwnerAndModifierData, boolean includeContentData,
                                                boolean includeCategoryData, boolean includeRelatedContentData, boolean includeUserRights,
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

            XMLDocument doc = xmlCreator.createContentsDocument( user, contents, relatedContent );
            addDataTraceInfo( doc.getAsJDOMDocument() );
            return doc;

        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private XMLDocument doGetContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query,
                                               String orderBy, int fromIndex, int count, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                               boolean includeOwnerAndModifierData, boolean includeContentData, boolean includeCategoryData,
                                               boolean includeRelatedContentData, boolean includeUserRights, int[] filterByContentTypes )
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

            XMLDocument document = xmlCreator.createContentsDocument( user, contents, relatedContents );
            addDataTraceInfo( document.getAsJDOMDocument() );
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

    private XMLDocument doGetRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, int count,
                                                     int parentLevel, int childrenLevel, int parentChildrenLevel,
                                                     boolean includeOwnerAndModifierData, boolean includeContentData,
                                                     boolean includeCategoryData, boolean includeRelatedContentData,
                                                     boolean includeUserRights )
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

        XMLDocument doc = xmlCreator.createContentsDocument( user, randomContent, relatedContent );
        addDataTraceInfo( doc.getAsJDOMDocument() );
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

    private XMLDocument doGetMenu( UserEntity user, int siteKey, int menuItemKey, int levels )
    {
        SiteEntity site = siteDao.findByKey( new SiteKey( siteKey ) );

        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( new MenuItemAccessResolver( groupDao ) );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
        siteXmlCreator.setUser( user );
        siteXmlCreator.setActiveMenuItem( menuItemDao.findByKey( menuItemKey ) );
        siteXmlCreator.setMenuItemLevels( levels );

        return siteXmlCreator.createLegacyGetMenu( site, sitePropertiesService.getSiteProperties( site.getKey() ) );
    }

    private XMLDocument doGetMenuData( int siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey );
        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( new MenuItemAccessResolver( groupDao ) );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
        siteXmlCreator.setIncludeDeviceClassResolverInfo( true );
        return siteXmlCreator.createLegacyGetMenuData( site, sitePropertiesService.getSiteProperties( site.getKey() ) );
    }

    private XMLDocument doGetMenuBranch( UserEntity user, int menuItemKey, boolean topLevel, int startLevel, int levels )
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

    private XMLDocument doGetMenuItem( UserEntity user, int menuItemKey, boolean includeParents )
    {
        MenuItemXMLCreatorSetting setting = new MenuItemXMLCreatorSetting();
        setting.user = user;
        setting.includeParents = includeParents;
        MenuItemXmlCreator creator = new MenuItemXmlCreator( setting, new MenuItemAccessResolver( groupDao ) );
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        return creator.createLegacyGetMenuItem( menuItem );
    }

    private XMLDocument doGetSubMenu( UserEntity user, int menuItemKey, int activeMenuItemKey, int levels )
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

    public void setPresentationEngine( PresentationEngine presentationEngine )
    {
        this.presentationEngine = presentationEngine;
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
}
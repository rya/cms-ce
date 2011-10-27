/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.ajax;

import org.springframework.web.util.HtmlUtils;

import com.enonic.esl.util.Base64Util;

import com.enonic.cms.core.preference.PreferenceKey;
import com.enonic.cms.core.preference.PreferenceScopeKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.server.service.admin.ajax.dto.PreferenceDto;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.PreferenceDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.core.preference.PreferenceEntity;
import com.enonic.cms.core.preference.PreferenceScopeType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

public class PreferenceDtoCreator
{

    private PortletDao portletDao;

    private PreferenceDao preferenceDao;

    private SiteDao siteDao;

    private MenuItemDao menuItemDao;

    public PreferenceDtoCreator( PortletDao portletDao, PreferenceDao preferenceDao, SiteDao siteDao, MenuItemDao menuItemDao )
    {
        this.portletDao = portletDao;
        this.preferenceDao = preferenceDao;
        this.siteDao = siteDao;
        this.menuItemDao = menuItemDao;
    }

    public PreferenceDto createPreferenceDto( PreferenceEntity preferenceEntity )
    {

        PreferenceDto preferenceDto = new PreferenceDto();

        final PreferenceKey preferenceKey = preferenceEntity.getKey();

        PreferenceScopeType scopeType = preferenceKey.getScopeType();

        preferenceDto.setKey( preferenceKey.getBaseKey() );

        String value = HtmlUtils.htmlEscape( preferenceEntity.getValue() );

        switch ( scopeType )
        {
            case GLOBAL:
                return createTypeGlobalDto( preferenceKey, value, scopeType );

            case SITE:
                return createTypeSiteDto( preferenceKey, value, scopeType );

            case PAGE:
                return createTypePageDto( preferenceKey, value, scopeType );

            case PORTLET:
                return createTypePortletDto( preferenceKey, value, scopeType );

            case WINDOW:
                return createTypeWindowDto( preferenceKey, value, scopeType );
        }

        return preferenceDto;
    }

    private PreferenceDto createTypeGlobalDto( PreferenceKey preferenceKey, String preferenceValue, PreferenceScopeType scopeType )
    {
        PreferenceDto preferenceDto = createCommonTypePortletDto( preferenceKey, preferenceValue, scopeType );

        preferenceDto.setPath( preferenceKey.getScopeType().getName() );
        preferenceDto.setSiteName( "" );
        preferenceDto.setMenuItemPath( "" );
        preferenceDto.setPortletName( "" );

        return preferenceDto;
    }

    private PreferenceDto createTypeSiteDto( PreferenceKey preferenceKey, String preferenceValue, PreferenceScopeType scopeType )
    {
        PreferenceScopeKey scopeKey = preferenceKey.getScopeKey();

        Integer siteKey = scopeKey.getFirstKey();

        SiteEntity site = siteDao.findByKey( siteKey );
        if ( site == null )
        {
            return null;
        }

        PreferenceDto preferenceDto = createCommonTypePortletDto( preferenceKey, preferenceValue, scopeType );

        String siteName = site.getName();
        
        preferenceDto.setPath( siteName );
        preferenceDto.setSiteName( siteName );
        preferenceDto.setMenuItemPath( "" );
        preferenceDto.setPortletName( "" );

        return preferenceDto;
    }

    private PreferenceDto createTypePageDto( PreferenceKey preferenceKey, String preferenceValue, PreferenceScopeType scopeType )
    {
        PreferenceScopeKey scopeKey = preferenceKey.getScopeKey();

        Integer menuItemKey = scopeKey.getFirstKey();

        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        if ( menuItem == null )
        {
            return null;
        }

        PreferenceDto preferenceDto = createCommonTypePortletDto( preferenceKey, preferenceValue, scopeType );

        String siteName = menuItem.getSite().getName();
        String memuItemPath = menuItem.getPathAsString();

        preferenceDto.setPath( memuItemPath );
        preferenceDto.setSiteName( siteName );
        preferenceDto.setMenuItemPath( memuItemPath );
        preferenceDto.setPortletName( "" );

        return preferenceDto;
    }

    private PreferenceDto createTypePortletDto( PreferenceKey preferenceKey, String preferenceValue, PreferenceScopeType scopeType )
    {
        PreferenceScopeKey scopeKey = preferenceKey.getScopeKey();
        Integer portletKey = scopeKey.getFirstKey();

        PortletEntity portlet = portletDao.findByKey( portletKey );
        if ( portlet == null )
        {
            return null;
        }

        PreferenceDto preferenceDto = createCommonTypePortletDto( preferenceKey, preferenceValue, scopeType );

        String portletName = portlet.getName();

        String siteName = portlet.getSite().getName();

        preferenceDto.setPath( siteName + " : " + portletName );
        preferenceDto.setSiteName( siteName );
        preferenceDto.setMenuItemPath( "" );
        preferenceDto.setPortletName( portletName );

        return preferenceDto;
    }

    private PreferenceDto createTypeWindowDto( PreferenceKey preferenceKey, String preferenceValue, PreferenceScopeType scopeType )
    {
        PreferenceScopeKey scopeKey = preferenceKey.getScopeKey();
        Integer menuItemKey = scopeKey.getFirstKey();
        Integer portletKey = scopeKey.getSecondKey();
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        PortletEntity portlet = portletDao.findByKey( portletKey );

        if ( menuItem == null ||  portlet == null )
        {
            return null;
        }

        PreferenceDto preferenceDto = createCommonTypePortletDto( preferenceKey, preferenceValue, scopeType );

        String memuItemPath = menuItem.getPathAsString();
        String siteName = portlet.getSite().getName();
        String portletName = portlet.getName();

        preferenceDto.setPath( memuItemPath + " : " + portletName );
        preferenceDto.setSiteName( siteName );
        preferenceDto.setMenuItemPath( memuItemPath );
        preferenceDto.setPortletName( portletName );

        return preferenceDto;
    }
    
    private PreferenceDto createCommonTypePortletDto( PreferenceKey preferenceKey, String preferenceValue, PreferenceScopeType scopeType )
    {
        PreferenceDto preferenceDto = new PreferenceDto();

        String value = HtmlUtils.htmlEscape( preferenceValue );
        String valueBase64Encoded = Base64Util.encode( value.getBytes() );

        preferenceDto.setKey( preferenceKey.getBaseKey() );
        preferenceDto.setValue( valueBase64Encoded );
        preferenceDto.setScope( scopeType.getName() );

        return preferenceDto;
    }
}

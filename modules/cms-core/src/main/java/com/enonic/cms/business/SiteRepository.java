/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

public class SiteRepository
{

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private MenuItemDao menuItemDao;


    public LanguageKey getMenuLanguageKey( int siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey );
        if ( site != null )
        {
            return site.getLanguage().getKey();
        }
        else
        {
            return null;
        }
    }

    public LanguageKey getMenuItemLanguageKey( int menuItemKey )
    {
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        if ( menuItem != null )
        {
            LanguageEntity language = menuItem.getLanguage();
            if ( language != null )
            {
                return language.getKey();
            }
            else
            {
                return null;
            }

        }
        else
        {
            return null;
        }
    }

}

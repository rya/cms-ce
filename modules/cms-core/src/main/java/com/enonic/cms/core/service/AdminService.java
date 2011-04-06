/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import java.util.Date;
import java.util.Map;

import org.w3c.dom.Element;

import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.ContentAccessRight;
import com.enonic.vertical.engine.MenuAccessRight;
import com.enonic.vertical.engine.MenuGetterSettings;
import com.enonic.vertical.engine.MenuItemAccessRight;
import com.enonic.vertical.engine.SectionCriteria;
import com.enonic.vertical.engine.VerticalCopyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.criteria.Criteria;
import com.enonic.vertical.engine.filters.Filter;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.userstore.UserStoreKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;

public interface AdminService
{

    //public String getMenuData(User user);

    public boolean initializeDatabaseSchema()
        throws Exception;

    public boolean initializeDatabaseValues()
        throws Exception;


    public Map getMenuMap()
        throws Exception;

    public long getArchiveSizeByCategory( int categoryKey );

    public long getArchiveSizeByUnit( int unitKey );

}

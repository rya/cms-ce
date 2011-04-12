/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

import java.util.Collection;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexQuery.SectionFilterStatus;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

public class ContentBySectionQuery
    extends AbstractContentQuery
{
    private Collection<MenuItemKey> menuItemKeys;

    private SectionFilterStatus sectionFilterStatus = SectionFilterStatus.APPROVED_ONLY;

    private boolean searchInAllSections = false;

    public Collection<MenuItemKey> getMenuItemKeys()
    {
        return menuItemKeys;
    }

    /**
     * @param menuItemKeys The menus to search for content.
     */
    public void setMenuItemKeys( Collection<MenuItemKey> menuItemKeys )
    {
        if ( menuItemKeys == null )
        {
            throw new IllegalArgumentException( "menuItemKeys cannot be NULL." );
        }
        this.menuItemKeys = menuItemKeys;
        this.searchInAllSections = false;
    }

    public ContentIndexQuery createAndSetupContentQuery( Collection<MenuItemEntity> sections, Collection<GroupKey> securityFilter )
    {

        // Apply default sorting if no order set and the one given section is not ordered
        if ( this.getOrderBy() == null || this.getOrderBy().length() == 0 )
        {
            if ( !( sections.size() == 1 && sections.iterator().next().isOrderedSection() ) )
            {
                this.setOrderBy( "@publishfrom DESC" );
            }
        }

        ContentIndexQuery query = new ContentIndexQuery( this.getQuery(), this.getOrderBy() );
        if ( this.useContentTypeFilter() )
        {
            query.setContentTypeFilter( this.getContentTypeFilter() );
        }
        if ( isSearchInAllSections() )
        {
            query.setSectionFilter( null, sectionFilterStatus );
        }
        else
        {
            query.setSectionFilter( sections, sectionFilterStatus );
        }
        query.setSecurityFilter( securityFilter );
        query.setIndex( this.getIndex() );
        query.setCount( this.getCount() );
        query.setCategoryAccessTypeFilter( getCategoryAccessTypeFilter(), getCategoryAccessTypeFilterPolicy() );
        checkAndApplyPublishedOnlyFilter( query );

        return query;
    }

    public void setSectionFilterStatus( SectionFilterStatus sectionFilterStatus )
    {
        this.sectionFilterStatus = sectionFilterStatus;
    }

    public boolean isSearchInAllSections()
    {
        return searchInAllSections;
    }

    public void setSearchInAllSections()
    {
        if ( menuItemKeys != null )
        {
            throw new IllegalStateException( "Searching in all sections is not possible when menuItemKeys have been set." );
        }
        searchInAllSections = true;
    }

    public boolean hasSectionFilter()
    {
        return ( menuItemKeys != null && menuItemKeys.size() > 0 ) || searchInAllSections;
    }

    @Override
    public void validate()
    {
        super.validate();

        if ( !hasSectionFilter() )
        {
            throw new InvalidContentBySectionQueryException( "Required section filter missing" );
        }
    }
}

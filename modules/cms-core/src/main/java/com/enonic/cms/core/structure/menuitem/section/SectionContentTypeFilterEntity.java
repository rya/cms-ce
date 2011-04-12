/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem.section;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;


public class SectionContentTypeFilterEntity
{


    private int key;

    private MenuItemEntity section;

    private ContentTypeEntity contentType;

    public SectionContentTypeFilterEntity()
    {
        // for Hibernate
    }

    public int getKey()
    {
        return key;
    }

    public void setKey( int value )
    {
        this.key = value;
    }

    public MenuItemEntity getSection()
    {
        return section;
    }

    public void setSection( MenuItemEntity value )
    {
        this.section = value;
    }

    public ContentTypeEntity getContentType()
    {
        return contentType;
    }

    public void setContentType( ContentTypeEntity value )
    {
        this.contentType = value;
    }
}



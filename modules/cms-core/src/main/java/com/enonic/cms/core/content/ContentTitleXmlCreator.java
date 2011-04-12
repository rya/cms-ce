/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Date;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.jdom.Element;

import com.enonic.cms.domain.CmsDateAndTimeFormats;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

/**
 * Jun 17, 2009
 */
public class ContentTitleXmlCreator
{
    private Date onlineCheckDate;

    private boolean includeSectionInfo = false;

    private MenuItemEntity currentSection;

    public ContentTitleXmlCreator( Date onlineCheckDate )
    {
        this.onlineCheckDate = onlineCheckDate;
    }

    public void setIncludeIsInHomeSectionInfo( boolean includeIsInHomeSectionInfo, MenuItemEntity currentSection )
    {
        this.includeSectionInfo = includeIsInHomeSectionInfo;
        this.currentSection = currentSection;
    }

    public Element createContentTitleElement( ContentEntity content )
    {
        return doCreateContentTitleElement( content );
    }

    private Element doCreateContentTitleElement( ContentEntity content )
    {
        ContentVersionEntity mainVersion = content.getMainVersion();
        CategoryEntity category = content.getCategory();
        UnitEntity unit = category.getUnitExcludeDeleted();
        ContentTypeEntity contentType = category.getContentType();
        ContentHandlerEntity contentHandler = contentType.getHandler();

        Element el = new Element( "contenttitle" );
        el.setAttribute( "key", content.getKey().toString() );
        el.setAttribute( "categorykey", category.getKey().toString() );
        el.setAttribute( "unitkey", String.valueOf( unit.getKey() ) );
        el.setAttribute( "contenttypekey", String.valueOf( contentType.getKey() ) );
        el.setAttribute( "contenthandlerkey", String.valueOf( contentHandler.getKey() ) );
        el.setAttribute( "contenthandler-class-name", contentHandler.getClassName() );
        setDateAttributeConditional( el, "publishfrom", content.getAvailableFrom() );
        setDateAttributeConditional( el, "publishto", content.getAvailableTo() );
        setDateAttributeConditional( el, "timestamp", mainVersion.getModifiedAt() );
        el.setAttribute( "state", Integer.toString( mainVersion.getState( onlineCheckDate ) ) );
        el.setAttribute( "status", Integer.toString( mainVersion.getStatus().getKey() ) );

        el.setAttribute( "has-draft", Boolean.toString( content.hasDraft() ) );
        el.setAttribute( "draft-key", content.hasDraft() ? content.getDraftVersion().getKey().toString() : "" );

        if ( includeSectionInfo )
        {
            boolean isActivatedInSection = content.isActiviatedInSection( currentSection );

            ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
            contentLocationSpecification.setSiteKey( currentSection.getSite().getKey() );
            contentLocationSpecification.setIncludeInactiveLocationsInSection( true );
            ContentLocations contentLocations = content.getLocations( contentLocationSpecification );
            ContentLocation homeLocation = contentLocations.getHomeLocation( currentSection.getSite().getKey() );
            if ( homeLocation != null && homeLocation.isLocationFor( currentSection ) )
            {
                el.setAttribute( "is-home", "true" );
            }
            else
            {
                el.setAttribute( "is-home", "false" );
                if ( homeLocation != null && homeLocation.getType() == ContentLocationType.MENUITEM )
                {
                    el.setAttribute( "is-link-to-menuitem", "true" );
                }
            }

            el.setAttribute( "approved", Boolean.toString( isActivatedInSection ) );
        }

        el.setText( mainVersion.getTitle() );
        return el;
    }

    private static void setDateAttributeConditional( Element el, String attribute, Date date )
    {
        if ( date != null )
        {
            el.setAttribute( attribute, CmsDateAndTimeFormats.printAs_XML_DATE( date ) );
        }
    }


}

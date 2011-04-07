/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.category.CategoryXmlCreator;

import com.enonic.cms.domain.content.ContentAndVersion;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.SiteEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Apr 30, 2010
 * Time: 11:43:30 AM
 */
public class AssigneeFormModel
{
    UserEntity user;

    ContentAndVersion contentAndVersion;

    SiteEntity site;

    List<UserEntity> possibleAssignees;

    private static final String ROOT_XML_NAME = "model";

    public SiteEntity getSite()
    {
        return site;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public void setContentAndVersion( ContentAndVersion contentAndVersion )
    {
        this.contentAndVersion = contentAndVersion;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public XMLDocument toXML()
    {
        Element modelEl = new Element( ROOT_XML_NAME );

        Document doc = new Document( modelEl );

        modelEl.addContent( createContentElement() );
        modelEl.addContent( createCategoryPathElement() );

        return XMLDocumentFactory.create( doc );
    }

    private Element createCategoryPathElement()
    {
        ContentEntity content = contentAndVersion.getContent();

        CategoryEntity currentCategory = content.getCategory();

        List<CategoryEntity> categoryPathList = createCategoryPathList( currentCategory );

        Element categoryPathEl = new Element( "categorynames" );

        CategoryXmlCreator categoryXmlCreator = new CategoryXmlCreator();
        categoryXmlCreator.setIncludeAccessRightsInfo( false );
        categoryXmlCreator.setIncludeAutoApproveInfo( false );
        categoryXmlCreator.setIncludeCreatedAndTimestampInfo( false );
        categoryXmlCreator.setIncludeOwnerAndModiferInfo( false );
        categoryXmlCreator.setIncludeDescriptionInfo( false );
        categoryXmlCreator.setIncludeDisabledInfo( false );

        for ( CategoryEntity category : categoryPathList )
        {
            categoryPathEl.addContent( categoryXmlCreator.createCategoryElement( category, "categoryname" ) );
        }

        return categoryPathEl;
    }

    private List<CategoryEntity> createCategoryPathList( CategoryEntity currentCategory )
    {
        List<CategoryEntity> categoryPathList = new ArrayList<CategoryEntity>();

        categoryPathList.add( currentCategory );

        while ( currentCategory.getParent() != null )
        {
            currentCategory = currentCategory.getParent();

            if ( categoryPathList.contains( currentCategory ) )
            {
                throw new IllegalArgumentException( "Category-path contains loop" );
            }

            categoryPathList.add( currentCategory );
        }

        Collections.reverse( categoryPathList );
        return categoryPathList;
    }

    private Element createContentElement()
    {
        ContentXMLCreator contentXMLCreator = new ContentXMLCreator();
        contentXMLCreator.setIncludeRelatedContentData( false );
        contentXMLCreator.setIncludeVersionsInfoForAdmin( true );
        contentXMLCreator.setIncludeAssignment( true );
        contentXMLCreator.setIncludeDraftInfo( true );
        return contentXMLCreator.createContentElement( user, contentAndVersion );
    }
}

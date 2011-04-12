/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;

import com.enonic.cms.core.security.user.UserEntity;

/**
 * Nov 16, 2010
 */
public class GetContentXmlCreator
{
    private CategoryAccessResolver categoryAccessResolver;

    private ContentAccessResolver contentAccessResolver;

    private UserEntity user;

    private int startingIndex;

    private int resultLength;

    private boolean includeContentsContentData;

    private boolean includeRelatedContentsContentData;

    private boolean includeUserRights = false;

    private VersionInfoStyle versionInfoStyle = null;

    public GetContentXmlCreator( CategoryAccessResolver categoryAccessResolver, ContentAccessResolver contentAccessResolver )
    {
        this.categoryAccessResolver = categoryAccessResolver;
        this.contentAccessResolver = contentAccessResolver;
    }

    public GetContentXmlCreator user( UserEntity value )
    {
        this.user = value;
        return this;
    }

    public GetContentXmlCreator startingIndex( int value )
    {
        this.startingIndex = value;
        return this;
    }

    public GetContentXmlCreator resultLength( int value )
    {
        this.resultLength = value;
        return this;
    }

    public GetContentXmlCreator includeContentsContentData( boolean value )
    {
        this.includeContentsContentData = value;
        return this;
    }

    public GetContentXmlCreator includeRelatedContentsContentData( boolean value )
    {
        this.includeRelatedContentsContentData = value;
        return this;
    }

    public GetContentXmlCreator includeUserRights( boolean value )
    {
        this.includeUserRights = value;
        return this;
    }

    public GetContentXmlCreator versionInfoStyle( VersionInfoStyle value )
    {
        this.versionInfoStyle = value;
        return this;
    }


    public XMLDocument create( GetContentResult result )
    {
        Preconditions.checkNotNull( result );
        Preconditions.checkNotNull( user );
        Preconditions.checkNotNull( versionInfoStyle );
        Preconditions.checkNotNull( categoryAccessResolver );
        Preconditions.checkNotNull( contentAccessResolver );

        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        xmlCreator.setResultIndexing( startingIndex, resultLength );
        xmlCreator.setIncludeContentData( includeContentsContentData );
        xmlCreator.setIncludeRelatedContentData( includeRelatedContentsContentData );
        xmlCreator.setIncludeUserRightsInfo( includeUserRights, categoryAccessResolver, contentAccessResolver );
        xmlCreator.setIncludeAssignment( true );
        xmlCreator.setIncludeOwnerAndModifierData( true );
        xmlCreator.setIncludeCategoryData( true );
        if ( versionInfoStyle == VersionInfoStyle.CLIENT )
        {
            xmlCreator.setIncludeVersionsInfoForClient( true );
        }
        else if ( versionInfoStyle == VersionInfoStyle.PORTAL )
        {
            xmlCreator.setIncludeVersionsInfoForSites( true );
        }
        else if ( versionInfoStyle == VersionInfoStyle.ADMIN )
        {
            xmlCreator.setIncludeVersionsInfoForAdmin( true );
        }

        return xmlCreator.createContentsDocument( user, result.getContentResultSet(), result.getRelatedContentResultSet() );
    }

    public enum VersionInfoStyle
    {
        PORTAL,
        CLIENT,
        ADMIN
    }
}

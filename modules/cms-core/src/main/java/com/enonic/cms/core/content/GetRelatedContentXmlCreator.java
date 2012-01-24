package com.enonic.cms.core.content;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * Nov 16, 2010
 */
public class GetRelatedContentXmlCreator
{
    private CategoryAccessResolver categoryAccessResolver;

    private ContentAccessResolver contentAccessResolver;

    private UserEntity user;

    private int startingIndex;

    private int resultLength;

    private boolean includeContentsContentData = false;

    private boolean includeRelatedContentsContentData = false;

    private boolean includeUserRights = false;

    private boolean includeOwnerAndModifierData = false;

    private boolean includeCategoryData = false;

    private VersionInfoStyle versionInfoStyle = null;

    public GetRelatedContentXmlCreator( CategoryAccessResolver categoryAccessResolver, ContentAccessResolver contentAccessResolver )
    {
        Preconditions.checkNotNull( categoryAccessResolver );
        Preconditions.checkNotNull( contentAccessResolver );

        this.categoryAccessResolver = categoryAccessResolver;
        this.contentAccessResolver = contentAccessResolver;
    }

    public GetRelatedContentXmlCreator user( UserEntity value )
    {
        this.user = value;
        return this;
    }

    public GetRelatedContentXmlCreator startingIndex( int value )
    {
        this.startingIndex = value;
        return this;
    }

    public GetRelatedContentXmlCreator resultLength( int value )
    {
        this.resultLength = value;
        return this;
    }

    public GetRelatedContentXmlCreator includeContentsContentData( boolean value )
    {
        this.includeContentsContentData = value;
        return this;
    }

    public GetRelatedContentXmlCreator includeRelatedContentsContentData( boolean value )
    {
        this.includeRelatedContentsContentData = value;
        return this;
    }

    public GetRelatedContentXmlCreator includeUserRights( boolean value )
    {
        this.includeUserRights = value;
        return this;
    }

    public GetRelatedContentXmlCreator includeOwnerAndModifierData( boolean value )
    {
        this.includeOwnerAndModifierData = value;
        return this;
    }

    public GetRelatedContentXmlCreator includeCategoryData( boolean value )
    {
        this.includeCategoryData = value;
        return this;
    }

    public GetRelatedContentXmlCreator versionInfoStyle( VersionInfoStyle value )
    {
        this.versionInfoStyle = value;
        return this;
    }

    public XMLDocument create( GetRelatedContentResult result )
    {
        Preconditions.checkNotNull( result );
        Preconditions.checkNotNull( user );

        // Create the content xml
        final ContentXMLCreator xmlCreator = new ContentXMLCreator();
        xmlCreator.setResultIndexing( startingIndex, resultLength );
        xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
        xmlCreator.setIncludeContentData( includeContentsContentData );
        xmlCreator.setIncludeCategoryData( includeCategoryData );
        xmlCreator.setIncludeRelatedContentData( includeRelatedContentsContentData );
        xmlCreator.setIncludeAssignment( true );
        xmlCreator.setIncludeUserRightsInfo( includeUserRights, categoryAccessResolver, contentAccessResolver );

        if ( versionInfoStyle != null )
        {
            if ( versionInfoStyle == VersionInfoStyle.CLIENT )
            {
                xmlCreator.setIncludeVersionsInfoForClient( true );
            }
            else if ( versionInfoStyle == VersionInfoStyle.PORTAL )
            {
                xmlCreator.setIncludeVersionsInfoForPortal( true );
            }
            else if ( versionInfoStyle == VersionInfoStyle.ADMIN )
            {
                xmlCreator.setIncludeVersionsInfoForAdmin( true );
            }
        }

        return xmlCreator.createContentsDocument( user, result.getContent(), result.getRelatedContent() );
    }

    public enum VersionInfoStyle
    {
        PORTAL,
        CLIENT,
        ADMIN
    }
}
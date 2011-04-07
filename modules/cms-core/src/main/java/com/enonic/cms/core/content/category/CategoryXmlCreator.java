/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.category.access.CategoryAccessResolver;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.content.ContentHandlerEntity;
import com.enonic.cms.domain.content.category.CategoryAccessType;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.content.contenttype.ContentTypeKey;
import com.enonic.cms.domain.security.user.UserEntity;

public class CategoryXmlCreator
{
    private UserEntity user;

    private UserEntity anonymousUser;

    private CategoryAccessResolver categoryAccessResolver;

    private Set<ContentTypeKey> allowedContentTypes;

    private boolean includeAutoApproveInfo = true;

    private boolean includeCreatedAndTimestampInfo = true;

    private boolean includeAccessRightsInfo = true;

    private boolean includeOwnerAndModiferInfo = true;

    private boolean includeDisabledInfo = true;

    private boolean includeDescriptionInfo = true;

    private boolean includeSuperCategoryKeyInfo = true;

    private boolean rootAccess = true;

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public void setAnonymousUser( UserEntity user )
    {
        this.anonymousUser = user;
    }

    public void setCategoryAccessResolver( CategoryAccessResolver resolver )
    {
        this.categoryAccessResolver = resolver;
    }

    public void setIncludeAccessRightsInfo( boolean value )
    {
        this.includeAccessRightsInfo = value;
    }

    public void setIncludeSuperCategoryKeyInfo( boolean value )
    {
        this.includeSuperCategoryKeyInfo = value;
    }

    public void setIncludeDescriptionInfo( boolean value )
    {
        this.includeDescriptionInfo = value;
    }

    public void setIncludeAutoApproveInfo( boolean value )
    {
        this.includeAutoApproveInfo = value;
    }

    public void setIncludeCreatedAndTimestampInfo( boolean value )
    {
        this.includeCreatedAndTimestampInfo = value;
    }

    public void setIncludeOwnerAndModiferInfo( boolean value )
    {
        this.includeOwnerAndModiferInfo = value;
    }

    public void setIncludeDisabledInfo( boolean value )
    {
        this.includeDisabledInfo = value;
    }

    public void setAllowedContentType( ContentTypeKey contentType )
    {
        allowedContentTypes = new HashSet<ContentTypeKey>();
        allowedContentTypes.add( contentType );
    }

    public void setAllowedContentTypes( Set<ContentTypeKey> contentTypes )
    {
        allowedContentTypes = contentTypes;
    }

    public void setRootAccess( boolean rootAccess )
    {
        this.rootAccess = rootAccess;
    }

    public XMLDocument createEmptyCategoriesDocument( String message )
    {
        Element root = new Element( "categories" );
        Document doc = new Document( root );

        if ( ( message != null ) && ( message.length() > 0 ) )
        {
            root.setAttribute( "message", message );
        }
        return XMLDocumentFactory.create( doc );
    }

    public XMLDocument createCategoryBranch( List<CategoryEntity> categories, CategoryKey selectedTopCategory )
    {
        final Element root = new Element( "categories" );
        root.setAttribute( "disabled", !rootAccess ? "true" : "false" );

        for ( CategoryEntity category : categories )
        {
            if ( isCategoryOrAnyDescendantsOfAllowedContentTypes( category ) )
            {
                boolean includeChildren = category.getKey().equals( selectedTopCategory ) | categories.size() == 1;
                root.addContent( doCreateCategoryElement( category, includeChildren, null ) );
            }
        }
        return XMLDocumentFactory.create( new Document( root ) );
    }

    public XMLDocument createCategoryBranch( CategoryEntity category )
    {
        final Element root = new Element( "categories" );

        root.addContent( doCreateCategoryElement( category, true, null ) );

        return XMLDocumentFactory.create( new Document( root ) );
    }


    public XMLDocument createEmptyCategoryNamesDocument( String message )
    {
        Element root = new Element( "categorynames" );
        Document doc = new Document( root );

        if ( ( message != null ) && ( message.length() > 0 ) )
        {
            root.setAttribute( "message", message );
        }
        return XMLDocumentFactory.create( doc );
    }

    public XMLDocument createCategoryNames( CategoryEntity category )
    {
        return createCategoryNames( category, null );
    }

    public XMLDocument createCategoryNames( CategoryEntity category, Map<CategoryEntity, Integer> contentCountMap )
    {
        List<CategoryEntity> categories = new ArrayList<CategoryEntity>();
        categories.add( category );

        return createCategoryNames( categories, contentCountMap );
    }

    public XMLDocument createCategoryNames( List<CategoryEntity> categories, Map<CategoryEntity, Integer> contentCountMap )
    {
        final Element root = new Element( "categorynames" );

        boolean includeContentCount = false;

        if ( contentCountMap != null )
        {
            includeContentCount = true;
        }

        for ( CategoryEntity category : categories )
        {
            root.addContent( createCategoryNameElement( category, includeContentCount ? contentCountMap.get( category ) : null ) );
        }

        return XMLDocumentFactory.create( new Document( root ) );
    }

    private Element createCategoryNameElement( CategoryEntity category, Integer contentCount )
    {
        Element element = new Element( "categoryname" );

        element.setText( category.getName() );

        element.setAttribute( "categorykey", category.getKey().toString() );

        if ( category.getContentType() != null )
        {
            element.setAttribute( "contenttypekey", Integer.toString( category.getContentType().getKey() ) );
        }

        if ( category.getParent() != null )
        {
            element.setAttribute( "supercategorykey", category.getParent().getKey().toString() );
        }

        if ( category.getUnitExcludeDeleted() != null )
        {
            element.setAttribute( "unitkey", Integer.toString( category.getUnitExcludeDeleted().getKey() ) );
        }

        element.setAttribute( "subcategories", category.hasChildren() ? "true" : "false" );

        if ( contentCount != null )
        {
            element.setAttribute( "contentcount", contentCount.toString() );
        }

        return element;
    }

    public XMLDocument createCategory( CategoryEntity category )
    {
        if ( category == null )
        {
            throw new IllegalArgumentException( "Given category cannot be null" );
        }

        final Element root = new Element( "categories" );

        if ( categoryAccessResolver.hasAccess( user, category, CategoryAccessType.READ ) )
        {
            root.addContent( doCreateCategoryElement( category, false, null ) );
        }

        return XMLDocumentFactory.create( new Document( root ) );
    }


    public Element createCategoryElement( CategoryEntity category, String elementName )
    {
        if ( category == null )
        {
            throw new IllegalArgumentException( "Given category cannot be null" );
        }

        return doCreateCategoryElement( category, false, elementName );
    }


    public Element createCategoryElement( CategoryEntity category )
    {
        if ( category == null )
        {
            throw new IllegalArgumentException( "Given category cannot be null" );
        }

        return doCreateCategoryElement( category, false, null );
    }

    private Element doCreateCategoryElement( CategoryEntity category, boolean includeChildren, String name )
    {
        final Element categoryEl = new Element( StringUtils.isBlank( name ) ? "category" : name );

        categoryEl.setAttribute( "key", category.getKey().toString() );
        categoryEl.setAttribute( "name", category.getName() );
        categoryEl.setAttribute( "language", category.getUnitExcludeDeleted().getLanguage().getCode() );
        categoryEl.setAttribute( "unitkey", String.valueOf( category.getUnitExcludeDeleted().getKey() ) );

        if ( includeCreatedAndTimestampInfo )
        {
            categoryEl.setAttribute( "created", CalendarUtil.formatTimestamp( category.getCreated(), false ) );
            categoryEl.setAttribute( "timestamp", CalendarUtil.formatTimestamp( category.getTimestamp(), false ) );
        }

        if ( includeAutoApproveInfo )
        {
            categoryEl.setAttribute( "autoApprove", String.valueOf( category.getAutoMakeAvailableAsBoolean() ) );
        }

        applyContentTypeInfo( category, categoryEl );

        if ( includeSuperCategoryKeyInfo )
        {
            applySuperCategoryKey( category, categoryEl );
        }
        if ( includeDescriptionInfo )
        {
            applyDescription( category, categoryEl );
        }

        if ( includeDisabledInfo )
        {
            applyDisabledInfo( category, categoryEl );
        }

        if ( includeOwnerAndModiferInfo )
        {
            categoryEl.addContent( createUserElement( "owner", category.getOwner() ) );
            categoryEl.addContent( createUserElement( "modifier", category.getModifier() ) );
        }
        if ( includeAccessRightsInfo )
        {
            applyAccessRights( category, categoryEl );
        }

        if ( includeChildren )
        {
            int addedChildren = 0;
            for ( CategoryEntity childCategory : category.getChildren() )
            {
                boolean hasAdminBrowseAccessOnAnyChild =
                    categoryAccessResolver.hasAdminBrowseAccessWithDescendantsCheck( user, childCategory );
                boolean childCategoryIsOfAllowedContentTypes = isCategoryOrAnyDescendantsOfAllowedContentTypes( childCategory );

                if ( hasAdminBrowseAccessOnAnyChild && childCategoryIsOfAllowedContentTypes )
                {
                    Element childCategoryEl = doCreateCategoryElement( childCategory, includeChildren, null );
                    categoryEl.addContent( childCategoryEl );
                    addedChildren++;
                }
            }
            categoryEl.setAttribute( "haschildren", addedChildren > 0 ? "true" : "false" );
        }
        else
        {
            categoryEl.setAttribute( "haschildren", category.hasChildren() ? "true" : "false" );
        }
        return categoryEl;
    }

    private void applyDisabledInfo( CategoryEntity category, Element categoryEl )
    {
        if ( allowedContentTypes == null )
        {
            categoryEl.setAttribute( "disabled", "false" );
        }
        else
        {
            boolean categoryIsOfAllowedContentTypes = isCategoryOfAllowedContentTypes( category );
            categoryEl.setAttribute( "disabled", !categoryIsOfAllowedContentTypes ? "true" : "false" );
        }
    }


    private void applyContentTypeInfo( CategoryEntity category, Element categoryEl )
    {
        ContentTypeEntity contentType = category.getContentType();
        if ( contentType != null )
        {
            categoryEl.setAttribute( "contenttypekey", contentType.getContentTypeKey().toString() );
            ContentHandlerEntity contentHandler = contentType.getHandler();
            if ( contentHandler != null )
            {
                categoryEl.setAttribute( "handler", contentHandler.getClassName() );
            }
        }
    }

    private void applyDescription( CategoryEntity category, Element categoryEl )
    {
        String description = category.getDescription();
        if ( description != null )
        {
            categoryEl.addContent( createCDataSection( "description", description ) );
        }
    }

    private void applySuperCategoryKey( CategoryEntity category, Element categoryEl )
    {
        CategoryEntity parent = category.getParent();
        if ( parent != null )
        {
            categoryEl.setAttribute( "supercategorykey", parent.getKey().toString() );
        }
    }

    private Element createCDataSection( String name, String data )
    {
        final Element element = new Element( name );
        element.addContent( new CDATA( data ) );
        return element;
    }

    private Element createUserElement( String name, UserEntity user )
    {
        final Element element = new Element( name );
        element.setText( user.getDisplayName() );
        element.setAttribute( "key", user.getKey().toString() );
        element.setAttribute( "uid", user.getName() );
        element.setAttribute( "qualifiedName", user.getQualifiedName().toString() );
        return element;
    }


    private void applyAccessRights( CategoryEntity category, Element categoryEl )
    {
        boolean anonymousReadAccess = categoryAccessResolver.hasAccess( anonymousUser, category, CategoryAccessType.READ );

        boolean administrateAccess = categoryAccessResolver.hasAccess( user, category, CategoryAccessType.ADMINISTRATE );
        boolean createAccess = categoryAccessResolver.hasAccess( user, category, CategoryAccessType.CREATE );
        boolean adminBrowseAccess = categoryAccessResolver.hasAccess( user, category, CategoryAccessType.ADMIN_BROWSE );
        categoryEl.setAttribute( "anonaccess", anonymousReadAccess ? "true" : "false" );
        categoryEl.setAttribute( "useraccess", adminBrowseAccess ? "true" : "false" );
        categoryEl.setAttribute( "useradministrate", administrateAccess ? "true" : "false" );
        categoryEl.setAttribute( "usercreate", createAccess ? "true" : "false" );
    }

    private boolean isCategoryOfAllowedContentTypes( CategoryEntity category )
    {
        if ( allowedContentTypes == null )
        {
            return true;
        }

        ContentTypeEntity contentType = category.getContentType();
        if ( contentType == null )
        {
            return allowedContentTypes.isEmpty();
        }

        return allowedContentTypes.contains( contentType.getContentTypeKey() );

    }

    private boolean isCategoryOrAnyDescendantsOfAllowedContentTypes( CategoryEntity category )
    {
        if ( allowedContentTypes == null )
        {
            return true;
        }

        if ( isCategoryOfAllowedContentTypes( category ) )
        {
            return true;
        }
        for ( CategoryEntity child : category.getChildren() )
        {
            if ( isCategoryOrAnyDescendantsOfAllowedContentTypes( child ) )
            {
                return true;
            }
        }
        return false;
    }
}
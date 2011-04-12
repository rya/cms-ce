/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.enonic.cms.core.content.binary.BinaryDataXmlCreator;
import com.enonic.cms.core.content.category.CategoryAccessRightsAccumulated;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentXmlCreator;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;

import com.enonic.cms.domain.CmsDateAndTimeFormats;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentVersionResultSet;
import com.enonic.cms.core.content.resultset.RelatedContent;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public class ContentXMLCreator
{
    private static final DateTimeFormatter VERSIONS_DATETIME_FORMAT = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss.S" );

    public static final String DEFAULT_RESULTROOTNAME = "contents";

    private CategoryAccessResolver categoryAccessResolver;

    private ContentAccessResolver contentAccessResolver;

    private BinaryDataXmlCreator binaryDataXmlCreator = new BinaryDataXmlCreator();

    private SectionContentXmlCreator sectionContentXmlCreator = new SectionContentXmlCreator();

    private ContentAccessXmlCreator contentAccessXmlCreator = new ContentAccessXmlCreator();

    private RelatedContentKeysXmlCreator relatedContentKeysXmlCreator;

    private ContentLocationXmlCreator contentLocationXmlCreator = new ContentLocationXmlCreator();

    /**
     * The starting index of the results in the XML to be created.
     */
    private int index = 0;

    /**
     * The length of the result set in the XML to be created.
     */
    private int count = 1;

    /**
     * Whether or not to include related content in the resulting XML.
     */
    private boolean includeRelatedContentsInfo = true;

    /**
     * Specify whether or not to include information about the category of the content in the generated XML.
     */
    private boolean includeCategoryData = true;

    /**
     * Specify whether or not to include information about the owner and modifier of the content in the generated XML.
     */
    private boolean includeOwnerAndModifierData = true;


    private boolean includeAssignment = false;

    /**
     * Specify whether or not to include the main content data in the generated XML.
     */
    private boolean includeContentData = true;

    /**
     * Specify whether or not to include the main content data for related content in the generated XML.  Setting this parameter to true
     * makes no sense, unless also <code>includeRelatedContentsInfo</code> is true.
     */
    private boolean includeRelatedContentData = true;

    /**
     * Whether or not to include the access information on each content in the returned document.
     */
    private boolean includeAccessRightsInfo = false;

    /**
     * Whether or not to include the user rights information for the given content.
     */
    private boolean includeUserRightsInfo = false;

    /**
     * Whether or not to include versions information for the given content in a minimal format suited for the admin GUI.
     * <p/>
     * This value can not be true at the same time as <code>includeVersionsInfoForSites</code> or <code>includeVersionsInfoForClient</code>
     * is true.
     */
    private boolean includeVersionsInfoForAdmin = false;

    /**
     * Whether or not to include a list of versions for the content, in a format that is suitable for template developers using the data
     * sources. This means that versions of status 2 or 3 will be included in the result.
     * <p/>
     * This value can not be true at the same time as <code>includeVersionsInfoForAdmin</code> or <code>includeVersionsInfoForClient</code>
     * is true.
     */
    private boolean includeVersionsInfoForSites = false;

    /**
     * Whether or not to include a list of versions for the content, in a format that is suitable for template developers using the data
     * sources. This means that versions of any status will be included in the result.
     * <p/>
     * This value can not be true at the same time as <code>includeVersionsInfoForAdmin</code> or <code>includeVersionsInfoForSites</code>
     * is true.
     */
    private boolean includeVersionsInfoForClient = false;

    private Date onlineCheckDate = new Date();

    private boolean includeRepositoryPathInfo = false;

    private boolean includeSectionActivationInfo = false;

    private boolean includeUserRightsInfoForRelated = false;

    private boolean orderByCreatedAtDescending = false;

    private boolean includeDraftInfo = false;

    private String resultRootName;

    private static final String ASSIGNMENT_DUEDATE_XML_KEY = "assignment-due-date";

    private static final String ASSIGNEE_XML_KEY = "assignee";

    private static final String ASSIGNER_XML_KEY = "assigner";

    private static final String ASSIGNMENT_DESCRIPTION_XML_KEY = "assignment-description";

    private static final String COMMENT_XML_KEY = "comment";

    public ContentXMLCreator()
    {
        // nothing
    }

    public Element createSingleContentVersionElement( UserEntity runningUser, ContentVersionEntity version )
    {
        ContentEntity content = version.getContent();
        return doCreateContentElement( content, version, runningUser, includeContentData, false );
    }

    public XMLDocument createContentsDocument( UserEntity runningUser, ContentAndVersion contentAndVersion,
                                               RelatedContentResultSet children )
    {
        Element contentsEl = new Element( "contents" );

        relatedContentKeysXmlCreator = new RelatedContentKeysXmlCreator( children );

        final Element contentEl =
            doCreateContentElement( contentAndVersion.getContent(), contentAndVersion.getVersion(), runningUser, includeContentData,
                                    false );
        contentEl.addContent( relatedContentKeysXmlCreator.createForContent( contentAndVersion.getVersion() ) );

        contentsEl.addContent( contentEl );

        if ( includeRelatedContentsInfo )
        {
            contentsEl.addContent( doCreateRelatedContentsElement( runningUser, children ) );
        }

        return XMLDocumentFactory.create( new Document( contentsEl ) );
    }

    public Element createContentElement( UserEntity runningUser, ContentAndVersion contentAndVersion )
    {
        return doCreateContentElement( contentAndVersion.getContent(), contentAndVersion.getVersion(), runningUser, includeContentData,
                                       false );
    }

    public XMLDocument createContentVersionsDocument( UserEntity user, Collection<ContentVersionEntity> versions,
                                                      RelatedContentResultSet children )
    {
        if ( versions == null )
        {
            throw new IllegalArgumentException( "Given versions cannot be null" );
        }

        Element contentsElement = createContentsElement();
        relatedContentKeysXmlCreator = new RelatedContentKeysXmlCreator( children );

        for ( ContentVersionEntity version : versions )
        {
            Element contentElement = doCreateContentElement( version.getContent(), version, user, includeContentData, false );
            contentElement.addContent( relatedContentKeysXmlCreator.createForContent( version ) );
            contentsElement.addContent( contentElement );
        }

        if ( includeRelatedContentsInfo )
        {
            contentsElement.addContent( doCreateRelatedContentsElement( user, children ) );
        }

        return XMLDocumentFactory.create( new Document( contentsElement ) );
    }

    public XMLDocument createContentsDocument( final UserEntity runningUser, final ContentVersionEntity version,
                                               final RelatedContentResultSet relatedContents )
    {
        final Element contentsEl = createContentsElement();

        final ContentEntity content = version.getContent();
        final Element contentEl = doCreateContentElement( content, version, runningUser, includeContentData, false );
        contentsEl.addContent( contentEl );

        if ( includeRelatedContentsInfo && relatedContents != null )
        {
            relatedContentKeysXmlCreator = new RelatedContentKeysXmlCreator( relatedContents );
            contentEl.addContent( relatedContentKeysXmlCreator.createForContent( version ) );

            Element relatedContentsEl = new Element( "relatedcontents" );
            relatedContentsEl.setAttribute( "count", Integer.toString( relatedContents.size() ) );

            contentsEl.addContent( doCreateRelatedContentsElement( runningUser, relatedContents ) );
        }

        return XMLDocumentFactory.create( new Document( contentsEl ) );
    }

    public XMLDocument createContentsDocument( UserEntity user, ContentResultSet contentResultSet, RelatedContentResultSet relatedContents )
    {
        if ( contentResultSet == null )
        {
            throw new IllegalArgumentException( "The ContentResultSet argument may not be NULL" );
        }
        if ( relatedContents == null )
        {
            throw new IllegalArgumentException( "The RelatedContentResultSet argument may not be NULL" );
        }

        if ( contentResultSet.hasErrors() )
        {
            return doCreateErrorDocument( contentResultSet.getErrors(), index, count );
        }

        relatedContentKeysXmlCreator = new RelatedContentKeysXmlCreator( relatedContents );

        final Element contentsEl = createContentsElement();

        for ( int i = 0; i < contentResultSet.getLength(); i++ )
        {
            final ContentEntity content = contentResultSet.getContent( i );
            final Element contentEl = doCreateContentElement( content, content.getMainVersion(), user, includeContentData, false );
            contentEl.addContent( relatedContentKeysXmlCreator.createForContent( content.getMainVersion() ) );

            contentsEl.addContent( contentEl );
        }

        contentsEl.setAttribute( "totalcount", Integer.toString( contentResultSet.getTotalCount() ) );
        // searchcount is deprecated, since it is the same as totalCount - to be removed some beautiful day
        // (jvs@2008-07-01)
        contentsEl.setAttribute( "searchcount", Integer.toString( contentResultSet.getTotalCount() ) );
        contentsEl.setAttribute( "index", Integer.toString( index ) );
        contentsEl.setAttribute( "count", Integer.toString( count ) );
        contentsEl.setAttribute( "resultcount", Integer.toString( contentResultSet.getLength() ) );

        if ( includeRelatedContentsInfo )
        {
            final Element relatedContentsElement = doCreateRelatedContentsElement( user, relatedContents );
            contentsEl.addContent( relatedContentsElement );
        }

        return XMLDocumentFactory.create( new Document( contentsEl ) );

    }

    public XMLDocument createContentVersionsDocument( UserEntity user, ContentVersionResultSet contentVersionResultSet,
                                                      RelatedContentResultSet relatedContents )
    {
        if ( contentVersionResultSet == null )
        {
            throw new IllegalArgumentException( "The ContentVersionResultSet argument may not be NULL" );
        }
        if ( relatedContents == null )
        {
            throw new IllegalArgumentException( "The RelatedContentResultSet argument may not be NULL" );
        }

        if ( contentVersionResultSet.hasErrors() )
        {
            return doCreateErrorDocument( contentVersionResultSet.getErrors(), index, count );
        }

        relatedContentKeysXmlCreator = new RelatedContentKeysXmlCreator( relatedContents );

        final Element contentsEl = createContentsElement();

        for ( int i = 0; i < contentVersionResultSet.getLength(); i++ )
        {
            final ContentVersionEntity contentVersion = contentVersionResultSet.getContent( i );
            final Element contentEl =
                doCreateContentElement( contentVersion.getContent(), contentVersion, user, includeContentData, false );
            contentEl.addContent( relatedContentKeysXmlCreator.createForContent( contentVersion ) );

            contentsEl.addContent( contentEl );
        }

        contentsEl.setAttribute( "totalcount", Integer.toString( contentVersionResultSet.getTotalCount() ) );
        // searchcount is deprecated, since it is the same as totalCount - to be removed some beautiful day
        // (jvs@2008-07-01)
        contentsEl.setAttribute( "searchcount", Integer.toString( contentVersionResultSet.getTotalCount() ) );
        contentsEl.setAttribute( "index", Integer.toString( index ) );
        contentsEl.setAttribute( "count", Integer.toString( count ) );
        contentsEl.setAttribute( "resultcount", Integer.toString( contentVersionResultSet.getLength() ) );

        if ( includeRelatedContentsInfo )
        {
            final Element relatedContentsElement = doCreateRelatedContentsElement( user, relatedContents );
            contentsEl.addContent( relatedContentsElement );
        }

        return XMLDocumentFactory.create( new Document( contentsEl ) );

    }

    public XMLDocument createContentVersionsDocument( UserEntity user, ContentResultSet contentResultSet,
                                                      RelatedContentResultSet relatedContents )
    {
        if ( contentResultSet == null )
        {
            throw new IllegalArgumentException( "The contentResultSet argument may not be NULL" );
        }
        if ( relatedContents == null )
        {
            throw new IllegalArgumentException( "The contentResultSet argument may not be NULL" );
        }

        if ( contentResultSet.hasErrors() )
        {
            return doCreateErrorDocument( contentResultSet.getErrors(), index, count );
        }

        relatedContentKeysXmlCreator = new RelatedContentKeysXmlCreator( relatedContents );

        final Element contentsEl = createContentsElement();

        for ( int i = 0; i < contentResultSet.getLength(); i++ )
        {
            final ContentEntity content = contentResultSet.getContent( i );
            ContentVersionEntity contentVersion;

            contentVersion = content.getMainVersion();

            final Element contentEl = doCreateContentElement( content, contentVersion, user, includeContentData, false );

            contentEl.addContent( relatedContentKeysXmlCreator.createForContent( contentVersion ) );

            contentsEl.addContent( contentEl );
        }

        contentsEl.setAttribute( "totalcount", Integer.toString( contentResultSet.getTotalCount() ) );
        // searchcount is deprecated, since it is the same as totalCount - to be removed some beautiful day
        // (jvs@2008-07-01)
        contentsEl.setAttribute( "searchcount", Integer.toString( contentResultSet.getTotalCount() ) );
        contentsEl.setAttribute( "index", Integer.toString( index ) );
        contentsEl.setAttribute( "count", Integer.toString( count ) );
        contentsEl.setAttribute( "resultcount", Integer.toString( contentResultSet.getLength() ) );

        if ( includeRelatedContentsInfo )
        {
            final Element relatedContentsElement = doCreateRelatedContentsElement( user, relatedContents );
            contentsEl.addContent( relatedContentsElement );
        }

        return XMLDocumentFactory.create( new Document( contentsEl ) );

    }

    public XMLDocument createEmptyDocument( String message )
    {
        Element root = createContentsElement();
        Document doc = new Document( root );

        root.setAttribute( "totalcount", "0" );

        if ( ( message != null ) && ( message.length() > 0 ) )
        {
            root.setAttribute( "message", message );
        }
        return XMLDocumentFactory.create( doc );
    }

    private Element createContentsElement()
    {
        if ( resultRootName != null && resultRootName.length() > 0 )
        {
            return new Element( resultRootName );
        }
        return new Element( DEFAULT_RESULTROOTNAME );
    }

    private XMLDocument doCreateErrorDocument( Collection<String> errors, int index, int count )
    {
        Element root = createContentsElement();
        root.setAttribute( "index", String.valueOf( index ) );
        root.setAttribute( "count", String.valueOf( count ) );
        Document doc = new Document( root );

        Element errorsEl = new Element( "_errors" );
        root.addContent( errorsEl );

        for ( String error : errors )
        {
            Element errorEl = new Element( "_error" );
            String messageWithoutLineBreaks = error.replaceAll( "\n", " " );
            errorEl.setAttribute( "message", messageWithoutLineBreaks );
            errorsEl.addContent( errorEl );
        }

        return XMLDocumentFactory.create( doc );
    }

    private Element doCreateRelatedContentsElement( final UserEntity user, final RelatedContentResultSet relatedContents )
    {

        final Element relatedContentsEl = new Element( "relatedcontents" );

        if ( !relatedContents.isEmpty() )
        {
            final Collection<RelatedContent> relatedContentCollection = relatedContents.getDistinctCollectionOfRelatedContent();
            for ( final RelatedContent relatedContent : relatedContentCollection )
            {
                final Element el = doCreateRelatedContentElement( relatedContent, user );
                relatedContentsEl.addContent( el );
            }
        }
        relatedContentsEl.setAttribute( "count", String.valueOf( relatedContents.size() ) );
        return relatedContentsEl;
    }

    private Element doCreateRelatedContentElement( final RelatedContent relatedContent, final UserEntity user )
    {
        final ContentEntity content = relatedContent.getContent();
        final ContentVersionEntity version = content.getMainVersion();
        Element contentEl = doCreateContentElement( content, version, user, includeRelatedContentData, true );

        contentEl.addContent( relatedContentKeysXmlCreator.createForRelatedContent( relatedContent ) );

        return contentEl;
    }

    private Element doCreateContentElement( final ContentEntity content, final ContentVersionEntity contentVersion, final UserEntity user,
                                            final boolean includeContentData, final boolean isRelatedContent )
    {
        final CategoryEntity category = content.getCategory();
        final UnitEntity unit = category.getUnitExcludeDeleted();
        final ContentTypeEntity contentType = category.getContentType();
        final LanguageEntity language = content.getLanguage();

        Element contentEl = new Element( "content" );
        contentEl.setAttribute( "unitkey", Integer.toString( unit.getKey() ) );
        contentEl.setAttribute( "approved", Boolean.toString( contentVersion.isApproved() ) );
        contentEl.setAttribute( "state", Integer.toString( contentVersion.getState( onlineCheckDate ) ) );
        contentEl.setAttribute( "status", Integer.toString( contentVersion.getStatus().getKey() ) );
        contentEl.setAttribute( "contenttype", contentType.getName() );
        contentEl.setAttribute( "contenttypekey", Integer.toString( contentType.getKey() ) );
        contentEl.setAttribute( "created", CmsDateAndTimeFormats.printAs_XML_DATE( content.getCreatedAt() ) );
        contentEl.setAttribute( "key", content.getKey().toString() );

        if ( includeDraftInfo )
        {
            contentEl.setAttribute( "has-draft", Boolean.toString( content.hasDraft() ) );
            contentEl.setAttribute( "draft-key", content.hasDraft() ? content.getDraftVersion().getKey().toString() : "" );
        }

        if ( includeVersionsInfoForAdmin || includeVersionsInfoForClient || includeVersionsInfoForSites )
        {
            contentEl.setAttribute( "versionkey", String.valueOf( contentVersion.getKey() ) );
            contentEl.setAttribute( "current", Boolean.toString( contentVersion.isMainVersion() ) );
        }
        contentEl.setAttribute( "languagecode", language.getCode() );

        LanguageKey languageKey = language.getKey();
        contentEl.setAttribute( "languagekey", languageKey.toString() );

        contentEl.setAttribute( "priority", Integer.toString( content.getPriority() ) );

        setDateAttributeConditional( contentEl, "publishfrom", content.getAvailableFrom() );
        setDateAttributeConditional( contentEl, "publishto", content.getAvailableTo() );
        setDateAttributeConditional( contentEl, "timestamp", contentVersion.getModifiedAt() );

        if ( includeOwnerAndModifierData )
        {
            addOwnerAndModifier( content, contentVersion, contentEl );
        }

        if ( includeAssignment )
        {
            contentEl.setAttribute( "is-assigned", Boolean.toString( content.isAssigned() ) );

            addAssignee( content, contentEl );
            addAssigner( content, contentEl );
            addAssignmentDueDate( content, contentEl );
            addAssignmentDescription( content, contentEl );
        }

        contentEl.addContent( new Element( "name" ).addContent( content.getName() ) );

        final String contentVersionTitle = contentVersion.getTitle();
        final Element titleEl = new Element( "title" );
        titleEl.setAttribute( "deprecated", "Use display-name instead" );
        titleEl.addContent( contentVersionTitle );
        contentEl.addContent( titleEl );

        contentEl.addContent( new Element( "display-name" ).addContent( contentVersionTitle ) );

        if ( includeContentData )
        {
            contentEl.addContent( createElement( contentVersion.getContentDataAsJDomDocument() ) );
        }

        if ( includeCategoryData )
        {
            addCategoryData( category, contentEl );
        }

        final Element locationElement = contentLocationXmlCreator.createLocationElement( content, includeSectionActivationInfo );
        contentEl.addContent( locationElement );

        final Element sectionNamesEl = sectionContentXmlCreator.createSectionNamesElement( content, includeSectionActivationInfo );
        contentEl.addContent( sectionNamesEl );

        final Element binariesEl = binaryDataXmlCreator.createBinariesElement( contentVersion );
        contentEl.addContent( binariesEl );

        if ( includeAccessRightsInfo && !isRelatedContent )
        {
            addAccessRightsInfo( content, contentEl );
        }

        if ( includeUserRightsInfo && !isRelatedContent )
        {
            addUserRightsInfo( content, user, contentEl );
        }

        if ( includeUserRightsInfoForRelated && isRelatedContent )
        {
            addUserRightsInfo( content, user, contentEl );
        }

        if ( includeVersionsInfoForAdmin && !isRelatedContent )
        {
            final Element versionsEl = doCreateVersionsElementForAdmin( content );
            contentEl.addContent( versionsEl );
        }

        if ( ( includeVersionsInfoForSites || includeVersionsInfoForClient ) && !isRelatedContent )
        {
            final Element versionsEl = doCreateVersionsElementForSite( content );
            contentEl.addContent( versionsEl );
        }

        if ( includeRepositoryPathInfo )
        {
            contentEl.setAttribute( "repositorypath", content.getCategory().getPathAsString() );
        }

        return contentEl;
    }

    private void addAssigner( ContentEntity content, Element contentEl )
    {
        Element assigner = new Element( ASSIGNER_XML_KEY );

        if ( content.getAssigner() != null )
        {
            addUserData( assigner, content.getAssigner() );
        }

        contentEl.addContent( assigner );
    }

    private void addAssignmentDescription( ContentEntity content, Element contentEl )
    {
        String assignmentDescription = StringUtils.isEmpty( content.getAssignmentDescription() ) ? "" : content.getAssignmentDescription();

        contentEl.addContent( new Element( ASSIGNMENT_DESCRIPTION_XML_KEY ).addContent( assignmentDescription ) );
    }

    private void addUserRightsInfo( ContentEntity content, UserEntity user, Element contentEl )
    {
        final Element userRightEl = doCreateUserRightElement( content, user );
        contentEl.addContent( userRightEl );
    }

    private void addAccessRightsInfo( ContentEntity content, Element contentEl )
    {
        final Collection<ContentAccessEntity> accessEntities = content.getContentAccessRights();
        final Element contentAccessRightsEl = contentAccessXmlCreator.createAccessRightsElement( accessEntities );
        contentEl.addContent( contentAccessRightsEl );
    }

    private void addCategoryData( CategoryEntity category, Element contentEl )
    {
        final Element categoryNameEl = new Element( "categoryname" );
        categoryNameEl.setAttribute( "key", category.getKey().toString() );
        categoryNameEl.setAttribute( "deprecated", "Use category instead" );
        categoryNameEl.setText( category.getName() );
        contentEl.addContent( categoryNameEl );
        contentEl.addContent( doCreateCategoryElement( category ) );
    }

    private void addOwnerAndModifier( ContentEntity content, ContentVersionEntity contentVersion, Element contentEl )
    {
        Element owner = new Element( "owner" );
        addUserData( owner, content.getOwner() );
        contentEl.addContent( owner );
        Element modifier = new Element( "modifier" );
        addUserData( modifier, contentVersion.getModifiedBy() );
        contentEl.addContent( modifier );
    }

    private void addAssignee( ContentEntity content, Element contentEl )
    {
        Element assignee = new Element( ASSIGNEE_XML_KEY );

        if ( content.getAssignee() != null )
        {
            addUserData( assignee, content.getAssignee() );
        }

        contentEl.addContent( assignee );
    }

    private void addAssignmentDueDate( ContentEntity content, Element contentEl )
    {
        Element dueDate = new Element( ASSIGNMENT_DUEDATE_XML_KEY );

        if ( content.getAssignmentDueDate() != null )
        {
            dueDate.setAttribute( "is-overdue", Boolean.toString( content.isAssignmentOverdue() ) );
            dueDate.addContent( CmsDateAndTimeFormats.printAs_XML_DATE( content.getAssignmentDueDate() ) );
        }
        contentEl.addContent( dueDate );
    }

    private Element doCreateVersionsElementForAdmin( ContentEntity content )
    {
        final Element versionsEl = new Element( "versions" );

        List<ContentVersionEntity> allVersions = new ArrayList<ContentVersionEntity>( content.getVersions() );

        if ( orderByCreatedAtDescending )
        {
            Collections.sort( allVersions, new ContentVersionCreatedAtDescendingComparator() );
        }

        for ( ContentVersionEntity version : allVersions )
        {
            final Element versionEl = new Element( "version" );
            final UserEntity modifier = version.getModifiedBy();
            versionEl.setAttribute( "key", String.valueOf( version.getKey() ) );
            versionEl.setAttribute( "created", VERSIONS_DATETIME_FORMAT.print( version.getCreatedAt().getTime() ) );
            versionEl.setAttribute( "timestamp", VERSIONS_DATETIME_FORMAT.print( version.getModifiedAt().getTime() ) );
            versionEl.setAttribute( "modified", VERSIONS_DATETIME_FORMAT.print( version.getModifiedAt().getTime() ) );
            versionEl.setAttribute( "modifierFullName", modifier.getDisplayName() );
            versionEl.setAttribute( "modifierQualifiedName", modifier.getQualifiedName().toString() );
            versionEl.setAttribute( "title", version.getTitle() );
            versionEl.setAttribute( "status", String.valueOf( version.getStatus().getKey() ) );
            versionEl.setAttribute( "state", String.valueOf( version.getState( onlineCheckDate ) ) );
            versionEl.setAttribute( "current", Boolean.toString( version.isMainVersion() ) );
            versionEl.setAttribute( "snapshotSource",
                                    version.getSnapshotSource() == null ? "" : version.getSnapshotSource().getKey().toString() );

            versionEl.setAttribute( COMMENT_XML_KEY, StringUtils.isBlank( version.getChangeComment() ) ? "" : version.getChangeComment() );
            versionsEl.addContent( versionEl );
        }

        return versionsEl;
    }

    private Element doCreateVersionsElementForSite( ContentEntity content )
    {
        final Element versionsEl = new Element( "versions" );

        ArrayList<ContentVersionEntity> versions = new ArrayList<ContentVersionEntity>( content.getVersionCount() );

        for ( ContentVersionEntity version : content.getVersions() )
        {
            versions.add( version );
        }
        Collections.sort( versions, new ContentVersionComparator() );

        for ( ContentVersionEntity version : versions )
        {
            if ( includeVersionsInfoForClient || version.isApproved() || version.isArchived() )
            {
                createSpecificVersionElement( version, versionsEl );
            }
        }

        return versionsEl;
    }

    private void createSpecificVersionElement( ContentVersionEntity version, Element statusElement )
    {
        final Element versionElement = new Element( "version" );
        versionElement.setAttribute( "key", String.valueOf( version.getKey() ) );
        versionElement.setAttribute( "status-key", String.valueOf( version.getStatus().getKey() ) );
        versionElement.setAttribute( "status", version.getStatusName() );
        versionElement.setAttribute( "timestamp", VERSIONS_DATETIME_FORMAT.print( version.getModifiedAt().getTime() ) );
        if ( version.isMainVersion() )
        {
            versionElement.setAttribute( "current", "true" );
        }
        Element titleElement = new Element( "title" );
        titleElement.setText( version.getTitle() );
        versionElement.addContent( titleElement );

        Element modifierElement = new Element( "modifier" );
        addUserData( modifierElement, version.getModifiedBy() );
        versionElement.addContent( modifierElement );

        Element commentElement = new Element( COMMENT_XML_KEY );
        commentElement.setText( version.getChangeComment() );
        versionElement.addContent( commentElement );

        statusElement.addContent( versionElement );
    }


    /**
     * If the given date is a valid date, it is set as an attribute in the content, overriding any previous values if they exist. If the
     * given date is not valid, no changes are performed.
     *
     * @param content   The Element of which to add this attribute.
     * @param attribute The name of the attribute.
     * @param d         The date containing the value to set.
     */
    private static void setDateAttributeConditional( Element content, String attribute, Date d )
    {
        if ( d != null )
        {
            content.setAttribute( attribute, CmsDateAndTimeFormats.printAs_XML_DATE( d ) );
        }
    }

    private static void addUserData( Element elem, UserEntity user )
    {
        elem.setAttribute( "key", user.getKey().toString() );
        elem.setAttribute( "deleted", Boolean.toString( user.isDeleted() ) );
        elem.setAttribute( "qualified-name", user.getQualifiedName().toString() );
        UserStoreEntity userStore = user.getUserStore();
        if ( userStore != null )
        {
            elem.addContent( new Element( "userstore" ).setText( userStore.getName() ) );
        }

        elem.addContent( new Element( "name" ).setText( user.getName() ) );
        elem.addContent( new Element( "display-name" ).setText( user.getDisplayName() ) );
        elem.addContent( new Element( "email" ).setText( user.getEmail() ) );

        Element photo = new Element( "photo" );
        photo.setAttribute( "exists", Boolean.toString( user.hasPhoto() ) );
        elem.addContent( photo );
    }

    private static Element createElement( Document doc )
    {
        Element element = doc.getRootElement();
        element.detach();
        return element;
    }

    private Element doCreateUserRightElement( final ContentEntity content, final UserEntity user )
    {

        final Element userRightEl = new Element( "userright" );
        final CategoryEntity category = content.getCategory();

        final CategoryAccessRightsAccumulated categoryAccess = categoryAccessResolver.getAccumulatedAccessRights( user, category );
        if ( categoryAccess.isCreate() )
        {
            userRightEl.setAttribute( "categorycreate", "true" );
        }
        if ( categoryAccess.isPublish() )
        {
            userRightEl.setAttribute( "categorypublish", "true" );
        }

        if ( categoryAccess.isPublish() || categoryAccess.isAdministrate() )
        {
            userRightEl.setAttribute( "read", "true" );
            userRightEl.setAttribute( "update", "true" );
            userRightEl.setAttribute( "delete", "true" );
        }
        else
        {
            final ContentAccessRightsAccumulated contentAccess = contentAccessResolver.getAccumulatedAccessRights( user, content );
            userRightEl.setAttribute( "read", Boolean.toString( contentAccess.isReadAccess() ) );
            userRightEl.setAttribute( "update", Boolean.toString( contentAccess.isUpdateAccess() ) );
            userRightEl.setAttribute( "delete", Boolean.toString( contentAccess.isDeleteAccess() ) );
        }
        return userRightEl;
    }

    private static Element doCreateCategoryElement( CategoryEntity category )
    {
        Element categoryEl = new Element( "category" );
        categoryEl.setAttribute( "key", String.valueOf( category.getKey() ) );
        categoryEl.setAttribute( "name", category.getName() );
        return categoryEl;
    }

    private class ContentVersionCreatedAtDescendingComparator
        implements Comparator<ContentVersionEntity>
    {
        public int compare( ContentVersionEntity version1, ContentVersionEntity version2 )
        {
            boolean ver1IsNewest = version1.getCreatedAt().before( version2.getCreatedAt() );

            if ( ver1IsNewest )
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }


    }

    private class ContentVersionComparator
        implements Comparator<ContentVersionEntity>
    {

        /**
         * Sorts 2 <code>ContentVersionEntity</code> object such that all drafts come first, then snapshot, approved and
         * archived.
         * <p/>
         * If the objects have the same status, then the object that is most recently modified will be considered the least, and come
         * first.
         *
         * @param entity1 The first entity to compare. If this should come first, the returned value is negative.
         * @param entity2 The second entity to compare. If this should come first, the returned value is positive.
         * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the
         *         second.
         */
        public int compare( ContentVersionEntity entity1, ContentVersionEntity entity2 )
        {
            if ( !entity1.getStatus().equals( entity2.getStatus() ) )
            {
                return entity2.getStatus().getKey() - entity1.getStatus().getKey();
            }

            if ( entity1.getModifiedAt().before( entity2.getModifiedAt() ) )
            {
                return -1;
            }
            else if ( entity1.getModifiedAt().after( entity2.getModifiedAt() ) )
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }

    public void setIncludeOwnerAndModifierData( boolean value )
    {
        includeOwnerAndModifierData = value;
    }

    public void setIncludeAssignment( boolean includeAssignment )
    {
        this.includeAssignment = includeAssignment;
    }

    public void setIncludeContentData( boolean value )
    {
        this.includeContentData = value;
    }

    public void setIncludeCategoryData( boolean value )
    {
        includeCategoryData = value;
    }

    public void setIncludeRelatedContentData( boolean value )
    {
        includeRelatedContentData = value;
    }

    public void setResultIndexing( int startingIndex, int resultLength )
    {
        index = startingIndex;
        count = resultLength;
    }

    public void setIncludeUserRightsInfo( boolean value, CategoryAccessResolver categoryAccessResolver,
                                          ContentAccessResolver contentAccessResolver )
    {
        includeUserRightsInfo = value;
        this.categoryAccessResolver = categoryAccessResolver;
        this.contentAccessResolver = contentAccessResolver;
    }

    public void setIncludeAccessRightsInfo( boolean value )
    {
        includeAccessRightsInfo = value;
    }

    public void setIncludeVersionsInfoForAdmin( boolean value )
    {
        includeVersionsInfoForAdmin = value;
        if ( includeVersionsInfoForAdmin )
        {
            includeVersionsInfoForSites = false;
            includeVersionsInfoForClient = false;
        }
    }

    public void setIncludeVersionsInfoForSites( boolean value )
    {
        includeVersionsInfoForSites = value;
        if ( includeVersionsInfoForSites )
        {
            includeVersionsInfoForAdmin = false;
            includeVersionsInfoForClient = false;
        }
    }

    public void setIncludeVersionsInfoForClient( boolean value )
    {
        includeVersionsInfoForClient = value;
        if ( includeVersionsInfoForClient )
        {
            includeVersionsInfoForAdmin = false;
            includeVersionsInfoForSites = false;
        }
    }

    public void setIncludeRelatedContentsInfo( boolean value )
    {
        includeRelatedContentsInfo = value;
    }

    public boolean isIncludeRepositoryPathInfo()
    {
        return includeRepositoryPathInfo;
    }

    public void setIncludeRepositoryPathInfo( boolean includeRepositoryPathInfo )
    {
        this.includeRepositoryPathInfo = includeRepositoryPathInfo;
    }

    public void setIncludeSectionActivationInfo( boolean includeSectionActivationInfo )
    {
        this.includeSectionActivationInfo = includeSectionActivationInfo;
    }

    public void setResultRootName( final String value )
    {
        resultRootName = value;
    }

    public void setOnlineCheckDate( Date onlineCheckDate )
    {
        this.onlineCheckDate = onlineCheckDate;
    }

    public boolean isIncludeUserRightsInfoForRelated()
    {
        return includeUserRightsInfoForRelated;
    }

    public void setIncludeUserRightsInfoForRelated( final boolean includeUserRightsInfoForRelated,
                                                    CategoryAccessResolver categoryAccessResolver,
                                                    ContentAccessResolver contentAccessResolver )
    {
        this.includeUserRightsInfoForRelated = includeUserRightsInfoForRelated;
        this.categoryAccessResolver = categoryAccessResolver;
        this.contentAccessResolver = contentAccessResolver;
    }

    public void setOrderByCreatedAtDescending( boolean orderByCreatedAtDescending )
    {
        this.orderByCreatedAtDescending = orderByCreatedAtDescending;
    }

    public boolean isIncludeDraftInfo()
    {
        return includeDraftInfo;
    }

    public void setIncludeDraftInfo( boolean includeDraftInfo )
    {
        this.includeDraftInfo = includeDraftInfo;
    }
}

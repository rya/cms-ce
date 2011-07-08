/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;

import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.ContentDataParser;
import com.enonic.cms.domain.content.contentdata.ContentDataXmlCreator;
import com.enonic.cms.domain.content.contentdata.custom.TitleDataEntryNotFoundException;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.security.user.UserEntity;

public class ContentVersionEntity
    implements Serializable, Cloneable
{

    public static final int STATE_PUBLISH_WAITING = 4;

    public static final int STATE_PUBLISHED = 5;

    public static final int STATE_PUBLISH_EXPIRED = 6;

    private ContentVersionKey key;

    private Integer status;

    private String title;

    private String changeComment;

    private Date createdAt;

    private Date modifiedAt;

    private UserEntity modifiedBy;

    private LazyInitializedJDOMDocument contentDataXml;

    private transient Document contentDataXmlAsDocument;

    private ContentEntity content;

    private ContentVersionEntity snapshotSource;

    private Set<ContentVersionEntity> snapshots = new LinkedHashSet<ContentVersionEntity>();

    @SuppressWarnings({"JpaModelErrorInspection"})
    private Set<ContentEntity> relatedChildren = new LinkedHashSet<ContentEntity>();

    private Set<ContentBinaryDataEntity> contentBinaryData = new LinkedHashSet<ContentBinaryDataEntity>();

    private transient ContentData contentData;

    public ContentVersionEntity()
    {
        // Default constructor used by Hibernate.
    }

    public ContentVersionEntity( ContentVersionEntity source )
    {
        this();

        this.key = source.getKey();
        this.status = source.getStatus().getKey();
        this.title = source.getTitle();
        this.changeComment = source.getChangeComment();
        this.createdAt = source.getCreatedAt();
        this.modifiedAt = source.getModifiedAt();
        this.modifiedBy = source.getModifiedBy() != null ? new UserEntity( source.getModifiedBy() ) : null;
        this.contentDataXml =
            source.getContentDataAsXmlString() != null ? new LazyInitializedJDOMDocument( source.getContentDataAsXmlString() ) : null;
        this.content = source.getContent();
        this.snapshotSource = source.getSnapshotSource();
        this.snapshots = source.getSnapshots() != null ? Sets.newLinkedHashSet( source.getSnapshots() ) : null;
        this.relatedChildren = source.getRelatedChildren() != null ? Sets.newLinkedHashSet( source.getRelatedChildren() ) : null;
        this.contentBinaryData = source.getContentBinaryData() != null ? Sets.newLinkedHashSet( source.getContentBinaryData() ) : null;
    }

    public ContentVersionKey getKey()
    {
        return key;
    }

    public ContentStatus getStatus()
    {
        return ContentStatus.get( status );
    }

    public boolean hasStatus( ContentStatus value )
    {
        return status == value.getKey();
    }

    public String getStatusName()
    {
        if ( status == null )
        {
            return null;
        }
        return ContentStatus.get( status ).getName();
    }

    public boolean isDraft()
    {
        return status == ContentStatus.DRAFT.getKey();
    }

    public boolean isSnapshot()
    {
        return status == ContentStatus.SNAPSHOT.getKey();
    }

    public boolean isApproved()
    {
        return status == ContentStatus.APPROVED.getKey();
    }

    public boolean isArchived()
    {
        return status == ContentStatus.ARCHIVED.getKey();
    }

    public int getState( Date now )
    {
        int state;

        if ( isApproved() && isMainVersion() )
        {
            Date from = content.getAvailableFrom();
            Date to = content.getAvailableTo();

            if ( from == null && to == null )
            {
                state = ContentStatus.APPROVED.getKey();
            }
            else if ( from == null )
            {
                if ( now.after( to ) || now.equals( to ) )
                {
                    state = ContentVersionEntity.STATE_PUBLISH_EXPIRED;
                }
                else
                {
                    state = ContentStatus.APPROVED.getKey();
                }
            }
            else if ( from.after( now ) )
            {
                state = ContentVersionEntity.STATE_PUBLISH_WAITING;
            }
            else
            { // from.before(now) == true
                if ( to == null || to.after( now ) )
                {
                    state = ContentVersionEntity.STATE_PUBLISHED;
                }
                else
                {
                    state = ContentVersionEntity.STATE_PUBLISH_EXPIRED;
                }
            }
        }
        else
        {
            state = status;
        }

        return state;
    }

    public String getTitle()
    {
        return title;
    }

    public String getChangeComment()
    {
        return changeComment;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public Date getModifiedAt()
    {
        return modifiedAt;
    }

    public ContentEntity getContent()
    {
        return content;
    }

    public UserEntity getModifiedBy()
    {
        return modifiedBy;
    }

    public boolean hasRelatedChild( ContentEntity content )
    {
        return relatedChildren.contains( content );
    }

    public Set<ContentEntity> getRelatedChildren()
    {
        return relatedChildren;
    }

    public Collection<ContentEntity> getRelatedChildren( boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return relatedChildren;
        }

        List<ContentEntity> notDeletedChildren = new ArrayList<ContentEntity>();
        for ( ContentEntity relatedChild : relatedChildren )
        {
            if ( !relatedChild.isDeleted() )
            {
                notDeletedChildren.add( relatedChild );
            }
        }
        return notDeletedChildren;
    }

    public boolean hasContentBinaryData()
    {
        return !contentBinaryData.isEmpty();
    }

    public Set<ContentBinaryDataEntity> getContentBinaryData()
    {
        return contentBinaryData;
    }

    public Set<BinaryDataKey> getContentBinaryDataKeys()
    {
        Set<BinaryDataKey> keys = new HashSet<BinaryDataKey>();
        for ( ContentBinaryDataEntity contentBinaryData : this.contentBinaryData )
        {
            keys.add( contentBinaryData.getBinaryData().getBinaryDataKey() );
        }
        return keys;
    }

    public void setKey( ContentVersionKey key )
    {
        this.key = key;
    }

    public void setStatus( ContentStatus status )
    {
        this.status = status.getKey();
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public void setChangeComment( String changeComment )
    {
        this.changeComment = changeComment;
    }

    public void setCreatedAt( Date created )
    {
        this.createdAt = created;
    }

    public void setModifiedAt( Date value )
    {
        this.modifiedAt = value;
    }

    public void setContentDataXml( String value )
    {
        contentDataXml = new LazyInitializedJDOMDocument( value );
        contentDataXmlAsDocument = null;
    }

    public String getContentDataAsXmlString()
    {
        if ( contentDataXml == null )
        {
            return null;
        }

        return contentDataXml.getDocumentAsString();
    }

    /**
     * Returns always a new JDOM Document.
     *
     * @return
     */
    public Document getContentDataAsJDomDocument()
    {
        if ( contentDataXml == null )
        {
            return null;
        }

        if ( contentDataXmlAsDocument == null )
        {
            contentDataXmlAsDocument = contentDataXml.getDocument();
        }

        return (Document) contentDataXmlAsDocument.clone();
    }

    public ContentData getContentData()
    {
        if ( contentDataXml == null )
        {
            return null;
        }

        if ( contentData == null )
        {
            final ContentTypeEntity conentType = getContent().getCategory().getContentType();
            contentData = ContentDataParser.parse( getContentDataAsJDomDocument(), conentType, null );
        }

        return contentData;
    }

    public void setContentData( ContentData contentData )
    {
        if ( contentData == null )
        {
            throw new IllegalArgumentException( "contentData cannot be null" );
        }

        this.contentData = contentData;
        try
        {
            this.title = this.contentData.getTitle();
        }
        catch ( TitleDataEntryNotFoundException e )
        {
            this.title = null;
        }

        setXmlDataFromContentData();
    }

    /**
     * Updates the persisted field contentDataAsXml with from transient field contentData.
     */
    public void setXmlDataFromContentData()
    {
        if ( contentData != null )
        {
            Document contentdataDoc = ContentDataXmlCreator.createContentDataDocument( contentData );
            this.contentDataXml = LazyInitializedJDOMDocument.parse( contentdataDoc );
            this.contentDataXmlAsDocument = null;
        }
    }

    public void setContent( ContentEntity value )
    {
        this.content = value;
    }

    public void setModifiedBy( UserEntity value )
    {
        this.modifiedBy = value;
    }

    public void addRelatedChild( ContentEntity value )
    {
        relatedChildren.add( value );
    }

    public void addRelatedChildren( Collection<ContentEntity> values )
    {
        relatedChildren.addAll( values );
    }

    public void removeRelatedChild( ContentEntity content )
    {
        relatedChildren.remove( content );
    }

    public void addContentBinaryData( ContentBinaryDataEntity contentBinaryData )
    {
        contentBinaryData.setContentVersion( this );
        this.contentBinaryData.add( contentBinaryData );
    }

    public BinaryDataEntity getBinaryData( final String label )
    {
        ContentBinaryDataEntity contentBinaryData = getContentBinaryData( label );
        if ( contentBinaryData == null )
        {
            return null;
        }
        return contentBinaryData.getBinaryData();
    }

    public BinaryDataEntity getOneAndOnlyBinaryData()
    {
        ContentBinaryDataEntity cbd = getOneAndOnlyContentBinaryData();
        if ( cbd == null )
        {
            return null;
        }
        return cbd.getBinaryData();
    }

    public ContentBinaryDataEntity getOneAndOnlyContentBinaryData()
    {
        Set<ContentBinaryDataEntity> contentBinaryDataSet = getContentBinaryData();
        Preconditions.checkArgument( contentBinaryDataSet.size() < 2,
                                     "Expected not more than one content binary for this contentVersion: " + getKey() );

        if ( contentBinaryDataSet.size() == 0 )
        {
            return null;
        }

        return contentBinaryDataSet.iterator().next();
    }

    public ContentBinaryDataEntity getContentBinaryData( final String label )
    {
        Preconditions.checkNotNull( label, "Given label cannot be null" );

        for ( ContentBinaryDataEntity contentBinaryData : getContentBinaryData() )
        {
            if ( label.equals( contentBinaryData.getLabel() ) )
            {
                return contentBinaryData;
            }
        }
        return null;
    }

    public BinaryDataEntity getBinaryData( final BinaryDataKey binaryDataKey )
    {
        ContentBinaryDataEntity contentBinaryData = getContentBinaryData( binaryDataKey );
        if ( contentBinaryData == null )
        {
            return null;
        }
        return contentBinaryData.getBinaryData();
    }

    public ContentBinaryDataEntity getContentBinaryData( final BinaryDataKey binaryDataKey )
    {
        Preconditions.checkNotNull( binaryDataKey, "Given binaryDataKey cannot be null" );

        for ( ContentBinaryDataEntity contentBinaryData : getContentBinaryData() )
        {
            if ( contentBinaryData.getBinaryData().getBinaryDataKey().equals( binaryDataKey ) )
            {
                return contentBinaryData;
            }
        }
        return null;
    }

    public boolean isMainVersion()
    {
        final ContentVersionKey mainVersionKey = getContent().getMainVersion().getKey();
        return mainVersionKey.equals( key );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentVersionEntity ) )
        {
            return false;
        }

        ContentVersionEntity that = (ContentVersionEntity) o;

        if ( key != null ? !key.equals( that.getKey() ) : that.getKey() != null )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 645;
        final int multiplierNonZeroOddNumber = 235;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append( "key = " ).append( key ).append( ", name = '" ).append( title ).append( "'" );
        return b.toString();
    }

    public boolean removeContentBinaryDataByBinaryDataKeys( final Set<BinaryDataKey> binaryDataToRemove )
    {
        boolean anyRemoved = false;

        if ( binaryDataToRemove.size() == 0 )
        {
            return anyRemoved;
        }

        // First: find the content binary datas to remove...
        List<ContentBinaryDataEntity> contentBinaryDatasToRemove = new ArrayList<ContentBinaryDataEntity>();
        for ( ContentBinaryDataEntity contentBinaryData : this.contentBinaryData )
        {
            if ( binaryDataToRemove.contains( contentBinaryData.getBinaryData().getBinaryDataKey() ) )
            {
                contentBinaryDatasToRemove.add( contentBinaryData );
                anyRemoved = true;
            }
        }

        this.contentBinaryData.removeAll( contentBinaryDatasToRemove );

        return anyRemoved;
    }

    public List<BinaryDataKey> removeContentBinaryData()
    {
        List<BinaryDataKey> removedBinaryDataKeys = new ArrayList<BinaryDataKey>();
        for ( ContentBinaryDataEntity contentBinaryData : this.contentBinaryData )
        {
            removedBinaryDataKeys.add( contentBinaryData.getBinaryData().getBinaryDataKey() );
        }

        this.contentBinaryData.clear();
        return removedBinaryDataKeys;
    }


    public ContentVersionEntity getSnapshotSource()
    {
        return snapshotSource;
    }

    public void setSnapshotSource( ContentVersionEntity snapshotSource )
    {
        this.snapshotSource = snapshotSource;
    }

    public Set<ContentVersionEntity> getSnapshots()
    {
        return snapshots;
    }

    public void addSnapshot( ContentVersionEntity snapshot )
    {
        snapshots.add( snapshot );

    }
}

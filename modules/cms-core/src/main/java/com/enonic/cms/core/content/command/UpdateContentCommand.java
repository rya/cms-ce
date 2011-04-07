/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;

public class UpdateContentCommand
    extends BaseContentCommand
{
    private Boolean updateAsNewVersion;

    private Boolean forceNewVersionEventIfUnchanged = Boolean.FALSE;

    private ContentVersionKey versionKeyToBaseNewVersionOn;

    private ContentVersionKey versionKeyToUpdate;

    private boolean updateAsMainVersion = false;

    private UpdateStrategy updateStrategy = UpdateStrategy.UPDATE;

    private boolean syncAccessRights = true;

    private boolean syncRelatedContent = true;

    private ContentKey contentKey;

    private UserKey modifier;

    private UserKey owner;

    private ContentStatus status;

    private String contentName;

    private ContentVersionEntity snapshotSource;

    private Set<BinaryDataKey> binaryDataToRemove = new HashSet<BinaryDataKey>();

    private boolean useCommandsBinaryDataToRemove = false;

    private Set<String> blockGroupsToPurgeByName = new HashSet<String>();

    private UserKey assignerKey;

    public void populateContentValuesFromContent( ContentEntity content )
    {
        setContentName( content.getName() );
        setContentKey( content.getKey() );
        setAvailableFrom( content.getAvailableFrom() );
        setAvailableTo( content.getAvailableTo() );
        setLanguage( content.getLanguage() );

        if ( content.getOwner() != null )
        {
            setOwner( content.getOwner().getKey() );
        }

        setPriority( content.getPriority() );

        addContentAccessRights( content.getContentAccessRights(), content );
    }

    public void populateContentVersionValuesFromContentVersion( ContentVersionEntity version )
    {
        setStatus( version.getStatus() );
        setChangeComment( version.getChangeComment() );
        setSnapshotSource( version.getSnapshotSource() );
        setContentData( version.getContentData() );
    }

    public void setModifier( UserEntity value )
    {
        Assert.notNull( value );
        this.modifier = value.getKey();
    }

    public void setModifier( UserKey value )
    {
        Assert.notNull( value );
        this.modifier = value;
    }

    public UserKey getOwner()
    {
        return owner;
    }

    public String getContentName()
    {
        return contentName;
    }

    public void setContentName( String contentName )
    {
        this.contentName = contentName;
    }

    public void setOwner( UserKey owner )
    {
        this.owner = owner;
    }

    public void setUpdateAsMainVersion( Boolean value )
    {
        if ( value != null )
        {
            this.updateAsMainVersion = value;
        }
    }

    public static UpdateContentCommand updateExistingVersion2( ContentVersionKey versionKeyToUpdate )
    {
        UpdateContentCommand command = new UpdateContentCommand();
        command.versionKeyToUpdate = versionKeyToUpdate;
        command.updateAsNewVersion = false;
        return command;
    }

    public static UpdateContentCommand storeNewVersionEvenIfUnchanged( ContentVersionKey versionKeyToBaseNewVersionOn )
    {
        UpdateContentCommand command = new UpdateContentCommand();
        command.updateAsNewVersion = true;
        command.versionKeyToBaseNewVersionOn = versionKeyToBaseNewVersionOn;
        command.forceNewVersionEventIfUnchanged = true;
        return command;
    }

    public static UpdateContentCommand storeNewVersionIfChanged( ContentVersionKey versionKeyToBaseNewVersionOn )
    {
        UpdateContentCommand command = new UpdateContentCommand();
        command.updateAsNewVersion = true;
        command.versionKeyToBaseNewVersionOn = versionKeyToBaseNewVersionOn;
        command.forceNewVersionEventIfUnchanged = false;
        return command;
    }

    public void setBinaryDataToAdd( List<BinaryDataAndBinary> list )
    {
        if ( list != null )
        {
            this.binaryDatas = list;
        }
    }

    public void setBinaryDataToRemove( Collection<BinaryDataKey> list )
    {
        if ( list != null )
        {
            binaryDataToRemove.clear();
            binaryDataToRemove.addAll( list );
        }
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public Boolean getUpdateAsNewVersion()
    {
        return updateAsNewVersion;
    }

    public Boolean forceNewVersionEventIfUnchanged()
    {
        return forceNewVersionEventIfUnchanged;
    }

    public Boolean getUpdateAsMainVersion()
    {
        return updateAsMainVersion;
    }

    public List<BinaryDataAndBinary> getBinaryDataToAdd()
    {
        return binaryDatas;
    }

    public Set<BinaryDataKey> getBinaryDataToRemove()
    {
        return binaryDataToRemove;
    }

    public Boolean getSyncAccessRights()
    {
        return syncAccessRights;
    }

    public void setSyncAccessRights( boolean value )
    {
        this.syncAccessRights = value;
    }

    public Boolean getSyncRelatedContent()
    {
        return syncRelatedContent;
    }

    public void setSyncRelatedContent( boolean value )
    {
        this.syncRelatedContent = value;
    }

    public ContentVersionKey getVersionKeyToBaseNewVersionOn()
    {
        return versionKeyToBaseNewVersionOn;
    }

    public ContentVersionKey getVersionKeyToUpdate()
    {
        return versionKeyToUpdate;
    }

    /**
     * @return the updateStrategy
     */
    public UpdateStrategy getUpdateStrategy()
    {
        return updateStrategy;
    }

    /**
     * @param updateStrategy the updateStrategy to set
     */
    public void setUpdateStrategy( final UpdateStrategy updateStrategy )
    {
        this.updateStrategy = updateStrategy;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public ContentStatus getStatus()
    {
        return status;
    }

    public void setStatus( ContentStatus status )
    {
        this.status = status;
    }

    public enum UpdateStrategy
    {
        UPDATE,
        MODIFY
    }

    public boolean useCommandsBinaryDataToRemove()
    {
        return useCommandsBinaryDataToRemove;
    }

    public void setUseCommandsBinaryDataToRemove( boolean useCommandsBinaryDataToRemove )
    {
        this.useCommandsBinaryDataToRemove = useCommandsBinaryDataToRemove;
    }

    public ContentVersionEntity getSnapshotSource()
    {
        return snapshotSource;
    }

    public void setSnapshotSource( ContentVersionEntity snapshotSource )
    {
        this.snapshotSource = snapshotSource;
    }

    public void addBlockGroupToPurge( String name )
    {
        this.blockGroupsToPurgeByName.add( name );
    }

    public Set<String> getBlockGroupsToPurgeByName()
    {
        return blockGroupsToPurgeByName;
    }
}


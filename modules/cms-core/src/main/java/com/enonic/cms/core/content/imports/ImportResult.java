/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.core.content.ContentKey;
import org.springframework.util.StopWatch;

import com.enonic.cms.core.content.ContentEntity;

public class ImportResult
{
    private final Map<ContentKey, String> inserted = new HashMap<ContentKey, String>();

    private final Map<ContentKey, String> updated = new HashMap<ContentKey, String>();

    private final Map<ContentKey, String> skipped = new HashMap<ContentKey, String>();

    private final Map<ContentKey, String> deleted = new HashMap<ContentKey, String>();

    private final Map<ContentKey, String> archived = new HashMap<ContentKey, String>();

    private final Map<ContentKey, String> remaining = new HashMap<ContentKey, String>();

    private final Map<ContentKey, String> alreadyArchived = new HashMap<ContentKey, String>();

    private final Map<ContentKey, String> assigned = new HashMap<ContentKey, String>();

    private final StopWatch elapsedTime = new StopWatch();

    public void startTimer()
    {
        if ( elapsedTime.isRunning() )
        {
            elapsedTime.stop();
        }
        elapsedTime.start();
    }

    public void stopTimer()
    {
        if ( elapsedTime.isRunning() )
        {
            elapsedTime.stop();
        }
    }

    public double getElapsedTimeInSeconds()
    {
        if ( elapsedTime.isRunning() )
        {
            elapsedTime.stop();
        }
        return elapsedTime.getTotalTimeSeconds();
    }

    public void addInserted( final ContentEntity content )
    {
        inserted.put( content.getKey(), content.getMainVersion().getTitle() );
    }

    public void addUpdated( final ContentEntity content )
    {
        updated.put( content.getKey(), content.getMainVersion().getTitle() );
    }

    public void addUnchanged( final ContentEntity content )
    {
        skipped.put( content.getKey(), content.getMainVersion().getTitle() );
    }

    public void addDeleted( final ContentEntity content )
    {
        deleted.put( content.getKey(), content.getMainVersion().getTitle() );
    }

    public void addArchived( final ContentEntity content )
    {
        archived.put( content.getKey(), content.getMainVersion().getTitle() );
    }

    public void addRemaining( final ContentEntity content )
    {
        remaining.put( content.getKey(), content.getMainVersion().getTitle() );
    }

    public void addAlreadyArchived( final ContentEntity content )
    {
        alreadyArchived.put( content.getKey(), content.getMainVersion().getTitle() );
    }

    public void addAssigned( final ContentEntity content )
    {
        assigned.put( content.getKey(), content.getMainVersion().getTitle() );
    }

    public Map<ContentKey, String> getInserted()
    {
        return inserted;
    }

    public Map<ContentKey, String> getUpdated()
    {
        return updated;
    }

    public Map<ContentKey, String> getSkipped()
    {
        return skipped;
    }

    public Map<ContentKey, String> getDeleted()
    {
        return deleted;
    }

    public Map<ContentKey, String> getArchived()
    {
        return archived;
    }

    public Map<ContentKey, String> getRemaining()
    {
        return remaining;
    }

    public Map<ContentKey, String> getAlreadyArchived()
    {
        return alreadyArchived;
    }

    public Map<ContentKey, String> getAssigned()
    {
        return assigned;
    }
}

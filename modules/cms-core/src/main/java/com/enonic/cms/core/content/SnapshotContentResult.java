/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: May 7, 2010
 * Time: 1:44:11 PM
 */
public class SnapshotContentResult
{
    ContentVersionEntity storedSnapshotContentVersion;

    public ContentVersionEntity getStoredSnapshotContentVersion()
    {
        return storedSnapshotContentVersion;
    }

    public void setStoredSnapshotContentVersion( ContentVersionEntity storedSnapshotContentVersion )
    {
        this.storedSnapshotContentVersion = storedSnapshotContentVersion;
    }

}

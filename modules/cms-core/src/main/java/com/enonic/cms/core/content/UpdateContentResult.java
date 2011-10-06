/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;


public class UpdateContentResult
{
    private ContentVersionEntity targetedVersion;

    private boolean contentChanged = false;

    private boolean targetedVersionChanged = false;

    private boolean accessRightsChanged = false;


    public ContentVersionEntity getTargetedVersion()
    {
        return targetedVersion;
    }

    public ContentVersionKey getTargetedVersionKey()
    {
        if ( targetedVersion == null )
        {
            return null;
        }
        return targetedVersion.getKey();
    }

    public boolean isAnyChangesMade()
    {
        return targetedVersionChanged || contentChanged || accessRightsChanged;
    }

    /*public boolean isContentChanged()
    {
        return contentChanged;
    }*/

    /*public boolean isTargetedVersionChanged()
    {
        return targetedVersionChanged;
    }*/

    /*public boolean isAccessRightsChanged()
    {
        return accessRightsChanged;
    }*/

    public void markTargetedVersionAsChanged()
    {
        targetedVersionChanged = true;
    }

    public void markContentAsChanged()
    {
        contentChanged = true;
    }

    public void markAccessRightsAsChanged()
    {
        accessRightsChanged = true;
    }

    public void setTargetedVersion( ContentVersionEntity value )
    {
        targetedVersion = value;
    }
}

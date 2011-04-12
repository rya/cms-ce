/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Oct 29, 2010
 * Time: 10:03:46 AM
 */
public class PathToContentElements
{
    String menuItemPath;

    String contentPathPostFix;

    Boolean doIncludeContentName = Boolean.TRUE;

    public String getMenuItemPath()
    {
        return menuItemPath;
    }

    public void setMenuItemPath( String menuItemPath )
    {
        this.menuItemPath = menuItemPath;
    }

    public String getContentPathPostFix()
    {
        return contentPathPostFix;
    }

    public void setContentPathPostFix( String contentPathPostFix )
    {
        this.contentPathPostFix = contentPathPostFix;
    }


    public Boolean getDoIncludeContentName()
    {
        return doIncludeContentName;
    }

    public void setDoIncludeContentName( boolean doIncludeContentName )
    {
        this.doIncludeContentName = doIncludeContentName;
    }
}

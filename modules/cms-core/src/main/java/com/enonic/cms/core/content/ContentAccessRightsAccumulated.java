/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

public class ContentAccessRightsAccumulated
{

    private boolean read = false;

    private boolean update = false;

    private boolean delete = false;

    public ContentAccessRightsAccumulated( boolean initialValue )
    {
        setAllTo( initialValue );
    }

    public boolean isReadAccess()
    {
        return read;
    }

    public boolean isUpdateAccess()
    {
        return update;
    }

    public boolean isDeleteAccess()
    {
        return delete;
    }

    public void setAllTo( boolean value )
    {
        read = value;
        update = value;
        delete = value;
    }

    public boolean isAllTrue()
    {
        return read && update && delete;
    }


    public void accumulate( ContentAccessEntity contentAccess )
    {

        read = read || contentAccess.isReadAccess();
        update = update || contentAccess.isUpdateAccess();
        delete = delete || contentAccess.isDeleteAccess();
    }
}

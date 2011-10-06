/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

/**
 * Accumulator class for category access rights.
 */
public final class CategoryAccessRightsAccumulated
{

    private boolean read;

    private boolean create;

    private boolean publish;

    private boolean administrate;

    private boolean adminRead;

    /**
     * @param initialValue A value that all member rights are set to.
     */
    public CategoryAccessRightsAccumulated( boolean initialValue )
    {
        setAllTo( initialValue );
    }

    public boolean isRead()
    {
        return read;
    }

    public boolean isCreate()
    {
        return create;
    }

    public boolean isPublish()
    {
        return publish;
    }

    public boolean isAdministrate()
    {
        return administrate;
    }

    public boolean isAdminRead()
    {
        return adminRead;
    }

    /**
     * @param value A value that all member rights are set to.
     */
    public void setAllTo( boolean value )
    {
        read = value;
        create = value;
        publish = value;
        administrate = value;
        adminRead = value;
    }

    public boolean isAllTrue()
    {
        return read && create && publish && administrate && adminRead;
    }

    public void accumulate( CategoryAccessEntity categoryAccess )
    {
        read = read || categoryAccess.givesRead();
        adminRead = adminRead || categoryAccess.givesAdminBrowse();
        create = create || categoryAccess.givesCreate();
        publish = publish || categoryAccess.givesApprove();
        administrate = administrate || categoryAccess.givesAdministrate();
    }
}

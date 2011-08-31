package com.enonic.cms.core.jcr.migrate;

import com.enonic.cms.core.jdbc.JdbcDynaRow;

public final class UserTask
    extends OneToOneTask
{
    public UserTask()
    {
        super("User", "tDomain where dom_deleted = 0");
    }

    @Override
    protected void importRow(final JdbcDynaRow row)
    {
    }
}

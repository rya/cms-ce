package com.enonic.cms.core.jcr.migrate;

import com.enonic.cms.core.jdbc.JdbcDynaRow;

public abstract class OneToOneTask
    extends AbstractTask
{
    private final String entityName;
    private final String countQuery;
    private final String itemQuery;

    public OneToOneTask(final String entityName, final String sqlPart)
    {
        this.entityName = entityName;
        this.countQuery = "SELECT count(*) FROM " + sqlPart;
        this.itemQuery = "SELECT * FROM " + sqlPart;
    }

    @Override
    public final void execute()
    {
        final int count = jdbcQueryInt(this.countQuery);
        logStart("Importing {0} {1}s...", count, this.entityName);

        for (final JdbcDynaRow row : jdbcQuery(this.itemQuery)) {
            importRow(row);
        }

        logStop("Imported {0} {1}s", count, this.entityName);
    }

    protected abstract void importRow(final JdbcDynaRow row);
}

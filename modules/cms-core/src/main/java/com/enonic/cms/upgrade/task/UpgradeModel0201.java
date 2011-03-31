/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

public class UpgradeModel0201
    extends AbstractUpgradeTask
{
    public UpgradeModel0201()
    {
        super( 201 );
    }

    public void upgrade( UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Removing quartz tables..." );

        context.dropTableConstraints( "QRTZ_CALENDARS", false );
        context.dropTableConstraints( "QRTZ_CRON_TRIGGERS", false );
        context.dropTableConstraints( "QRTZ_FIRED_TRIGGERS", false );
        context.dropTableConstraints( "QRTZ_PAUSED_TRIGGER_GRPS", false );
        context.dropTableConstraints( "QRTZ_SCHEDULER_STATE", false );
        context.dropTableConstraints( "QRTZ_LOCKS", false );
        context.dropTableConstraints( "QRTZ_JOB_DETAILS", false );
        context.dropTableConstraints( "QRTZ_JOB_LISTENERS", false );
        context.dropTableConstraints( "QRTZ_SIMPLE_TRIGGERS", false );
        context.dropTableConstraints( "QRTZ_BLOB_TRIGGERS", false );
        context.dropTableConstraints( "QRTZ_TRIGGER_LISTENERS", false );
        context.dropTableConstraints( "QRTZ_TRIGGERS", false );

        context.dropTable( "QRTZ_CALENDARS" );
        context.dropTable( "QRTZ_CRON_TRIGGERS" );
        context.dropTable( "QRTZ_FIRED_TRIGGERS" );
        context.dropTable( "QRTZ_PAUSED_TRIGGER_GRPS" );
        context.dropTable( "QRTZ_SCHEDULER_STATE" );
        context.dropTable( "QRTZ_LOCKS" );
        context.dropTable( "QRTZ_JOB_DETAILS" );
        context.dropTable( "QRTZ_JOB_LISTENERS" );
        context.dropTable( "QRTZ_SIMPLE_TRIGGERS" );
        context.dropTable( "QRTZ_BLOB_TRIGGERS" );
        context.dropTable( "QRTZ_TRIGGER_LISTENERS" );
        context.dropTable( "QRTZ_TRIGGERS" );
    }
}

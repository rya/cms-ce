/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MigrationProcess
    implements Callable<Boolean>
{

    private Log log;

    private RepositoryBootstrap repositoryBootstrap;

    private UserStoreTask userStoreTask;

    private GroupTask groupTask;

    private UserTask userTask;

    private UserFieldTask userFieldsTask;

    private GroupMembershipTask groupMembershipTask;

    private CleanUpTask cleanUpTask;

    private DataSource dataSource;

    MigrationProcess()
    {
    }

    private void executeTasks()
    {
        final AbstractTask[] tasks = new AbstractTask[]{userStoreTask, userTask, groupTask, userFieldsTask, groupMembershipTask};
        for ( AbstractTask task : tasks )
        {
            task.setTemplate( new JdbcTemplate( dataSource ) );
            task.setLog( log );
            task.execute();
        }
        cleanUpTask.setLog( log );
        cleanUpTask.execute();
    }

    @Override
    public Boolean call()
        throws Exception
    {
        try
        {
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            repositoryBootstrap.setLog( log );
            repositoryBootstrap.initializeRepository();

            executeTasks();

            log.logInfo( "JCR migration completed. Total time " + stopWatch.toString() );

            return true;
        }
        catch ( Exception e )
        {
            log.logError( "JCR migration failed", e );
            return false;
        }
    }

    @Autowired
    public void setDataSource( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    @Autowired
    public void setUserStoreTask( UserStoreTask userStoreTask )
    {
        this.userStoreTask = userStoreTask;
    }

    @Autowired
    public void setGroupTask( GroupTask groupTask )
    {
        this.groupTask = groupTask;
    }

    @Autowired
    public void setRepositoryBootstrap( RepositoryBootstrap repositoryBootstrap )
    {
        this.repositoryBootstrap = repositoryBootstrap;
    }

    @Autowired
    public void setUserTask( UserTask userTask )
    {
        this.userTask = userTask;
    }

    @Autowired
    public void setUserFieldsTask( UserFieldTask userFieldsTask )
    {
        this.userFieldsTask = userFieldsTask;
    }

    @Autowired
    public void setGroupMembershipTask( GroupMembershipTask groupMembershipTask )
    {
        this.groupMembershipTask = groupMembershipTask;
    }

    @Autowired
    public void setCleanUpTask( CleanUpTask cleanUpTask )
    {
        this.cleanUpTask = cleanUpTask;
    }

    public void setLog( Log log )
    {
        this.log = log;
    }

}

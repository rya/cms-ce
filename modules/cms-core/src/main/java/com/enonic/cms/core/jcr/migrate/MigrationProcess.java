/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MigrationProcess
    implements Callable<Boolean>
{
    private static final Logger LOG = LoggerFactory.getLogger( MigrationProcess.class );

    private RepositoryBootstrap repositoryBootstrap;

    private UserStoreTask userStoreTask;

    private GroupTask groupTask;

    private UserTask userTask;

    private UserFieldTask userFieldsTask;

    private GroupMembershipTask groupMembershipTask;

    private DataSource dataSource;

    MigrationProcess()
    {
    }

    @PostConstruct
    private void init()
    {
        userStoreTask.setTemplate( new JdbcTemplate( dataSource ) );
        groupTask.setTemplate( new JdbcTemplate( dataSource ) );
        userTask.setTemplate( new JdbcTemplate( dataSource ) );
        userFieldsTask.setTemplate( new JdbcTemplate( dataSource ) );
        groupMembershipTask.setTemplate( new JdbcTemplate( dataSource ) );
    }

    @Override
    public Boolean call()
        throws Exception
    {
        try
        {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            repositoryBootstrap.initializeRepository();

            userStoreTask.execute();

            userTask.execute();

            groupTask.execute();

            userFieldsTask.execute();

            groupMembershipTask.execute();

            LOG.info( "JCR migration completed. Total time " + stopWatch.toString());

            return true;
        }
        catch ( Exception e )
        {
            LOG.error( "JCR migration failed", e );
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
}

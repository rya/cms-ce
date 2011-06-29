package com.enonic.cms.core.plugin.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.api.plugin.ext.TaskHandler;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 6/21/11
 * Time: 11:47 AM
 */
public class TaskManagerTest
    extends TestCase
{
    private static final String CRON_EVERY_SECOND = "*/1 * * * * *";

    private static final String CRON_2_SECONDS = "*/2 * * * * *";

    private TaskManager taskManager;

    @Before
    public void setUp()
    {
        taskManager = new TaskManager();
    }

    @Test
    public void testAddTasks()
    {
        taskManager.extensionAdded( new TestTaskHandler( "task1", CRON_EVERY_SECOND ) );

        assertEquals( taskManager.numberOfTasks(), 1 );

        taskManager.extensionAdded( new TestTaskHandler( "task1", CRON_EVERY_SECOND ) );

        assertEquals( taskManager.numberOfTasks(), 1 );
        taskManager.extensionAdded( new TestTaskHandler( "task2", CRON_EVERY_SECOND ) );

        assertEquals( taskManager.numberOfTasks(), 2 );
    }

    @Test
    public void testAddNonTaskHandler()
    {
        taskManager.extensionAdded( new FunctionLibraryTask() );
        assertEquals( taskManager.numberOfTasks(), 0 );
    }

    @Test
    public void testRemoveTasks()
    {

        taskManager.extensionAdded( new TestTaskHandler( "task1", CRON_EVERY_SECOND ) );

        assertEquals( taskManager.numberOfTasks(), 1 );

        taskManager.extensionRemoved( new TestTaskHandler( "task1", CRON_EVERY_SECOND ) );

        assertEquals( taskManager.numberOfTasks(), 0 );
        taskManager.extensionRemoved( new TestTaskHandler( "task2", CRON_EVERY_SECOND ) );

        assertEquals( taskManager.numberOfTasks(), 0 );
    }

    @Test
    public void testRemoveNonTaskHandler()
    {
        taskManager.extensionRemoved( new FunctionLibraryTask() );
        assertEquals( taskManager.numberOfTasks(), 0 );
    }

    @Test
    public void testSingleInvocation()
    {
        final TestTaskHandler task1 = new TestTaskHandler( "task1", CRON_EVERY_SECOND );
        taskManager.extensionAdded( task1 );

        sleep( 1000 );

        assertTrue( task1.invokations >= 1 );
    }

    @Test
    public void testTaskInvocation_add_remove_add()
    {
        final TestTaskHandler task1 = new TestTaskHandler( "task1", CRON_EVERY_SECOND );
        taskManager.extensionAdded( task1 );

        sleep( 1000 );

        taskManager.extensionRemoved( task1 );

        int numberOfInvocations = task1.invokations;

        sleep( 1000 );

        assertEquals( numberOfInvocations, task1.invokations );

        taskManager.extensionAdded( task1 );

        sleep( 1000 );

        assertTrue( numberOfInvocations < task1.invokations );
    }


    @Test
    public void testTaskInvocation_add_remove_add_several()
    {
        final TestTaskHandler task1 = new TestTaskHandler( "task1", CRON_EVERY_SECOND );
        final TestTaskHandler task2 = new TestTaskHandler( "task2", CRON_2_SECONDS );
        final TestTaskHandler task3 = new TestTaskHandler( "task3", CRON_2_SECONDS );

        taskManager.extensionAdded( task1 );
        taskManager.extensionAdded( task2 );
        taskManager.extensionAdded( task3 );

        sleep( 2000 );

        taskManager.extensionRemoved( task1 );
        taskManager.extensionRemoved( task2 );

        int task1Invovations = task1.invokations;
        int task2Invovations = task2.invokations;
        int task3Invovations = task3.invokations;

        assertTrue( "Task1 should have been invoced more times than task2", task1Invovations > task2Invovations );

        sleep( 2000 );

        assertEquals( task1Invovations, task1.invokations );
        assertEquals( task2Invovations, task2.invokations );
        assertTrue( "Task3 should have been executed again since last measure", task3Invovations < task3.invokations );

        taskManager.extensionAdded( task1 );
        taskManager.extensionAdded( task2 );

        sleep( 2000 );

        assertTrue( "Task1 should have been executed again since last measure", task1Invovations < task1.invokations );
        assertTrue( "Task2 should have been executed again since last measure", task2Invovations < task2.invokations );
    }

    @Test
    public void testEnsureScheduledOnceOnly()
    {
        final TestTaskHandler task1 = new TestTaskHandler( "task1", CRON_2_SECONDS );

        taskManager.extensionAdded( task1 );

        sleep( 2000 );

        assertEquals( 1, task1.invokations );

        taskManager.extensionRemoved( task1 );
        taskManager.extensionAdded( task1 );
        taskManager.extensionRemoved( task1 );
        taskManager.extensionAdded( task1 );

        sleep( 2000 );

        assertEquals( 2, task1.invokations );

        sleep( 2000 );

        assertEquals( 3, task1.invokations );

    }

    @Test
    public void testIllegalCronDefinition()
    {
        final TestTaskHandler task1 = new TestTaskHandler( "task1", "This is not a valid cron" );
        taskManager.extensionAdded( task1 );

    }

    @Test
    public void testEmptyCronDefinition()
    {
        final TestTaskHandler task1 = new TestTaskHandler( "task1", "" );
        taskManager.extensionAdded( task1 );
    }


    @Test
    public void testFailingTaskHandler()
    {
        final FailingTestTaskHandler task1 = new FailingTestTaskHandler( "task1", CRON_EVERY_SECOND );
        taskManager.extensionAdded( task1 );
        sleep( 1000 );
    }


    private void sleep( long timeout )
    {
        try
        {
            Thread.sleep( timeout );
        }
        catch ( InterruptedException e )
        {
        }
    }

    private class TestTaskHandler
        extends TaskHandler
    {
        private int invokations = 0;

        private TestTaskHandler( String name, String cron )
        {
            super();
            this.setName( name );
            this.setCron( cron );
        }

        @Override
        public void execute()
            throws Exception
        {
            invokations++;
            // System.out.println( "Invoking task: " + this.getName() + " (" + invokations + ")" );
        }
    }

    private class FunctionLibraryTask
        extends FunctionLibrary
    {
    }

    private class FailingTestTaskHandler
        extends TaskHandler
    {
        private int invokations = 0;

        private FailingTestTaskHandler( String name, String cron )
        {
            super();
            this.setName( name );
            this.setCron( cron );
        }

        @Override
        public void execute()
            throws Exception
        {
            throw new RuntimeException( "Expected exception!" );
        }
    }


    @After
    public void tearDown()
        throws Exception
    {
        taskManager.destroy();
    }
}

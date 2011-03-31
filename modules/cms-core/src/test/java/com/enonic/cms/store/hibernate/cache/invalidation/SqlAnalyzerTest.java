/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class SqlAnalyzerTest
{
    @Test
    public void testInsertQueries()
    {
        Map<String, Boolean> tests = new HashMap<String, Boolean>();

        tests.put( "INSERT INTO tTestTable(a, b) values(1, 2)", true );
        tests.put( "insert into tTestTable(a, b) values(1, 2)", true );
        tests.put( "insert into tTestTable (a, b) values(1, 2)", true );
        tests.put( "insert into tTestTable  (a, b) values(1, 2)", true );
        tests.put( "insert into tTestTable(1, 2)", true );
        tests.put( "insert into tTestTable (1, 2)", true );
        tests.put( "insert into tTestTable  (1, 2)", true );
        tests.put( "insert into tTestTable(select a, b)", true );

        testQueries( tests );
    }

    @Test
    public void testUpdateQueries()
    {
        Map<String, Boolean> tests = new HashMap<String, Boolean>();

        tests.put( "DELETE FROM tTestTable", false );
        tests.put( "delete from tTestTable", false );
        tests.put( "delete from tTestTable where a = b", false );

        testQueries( tests );
    }

    @Test
    public void testDelereQueries()
    {
        Map<String, Boolean> tests = new HashMap<String, Boolean>();

        tests.put( "UPDATE tTestTable set a = b", false );
        tests.put( "update tTestTable set a = b", false );

        testQueries( tests );
    }


    private void testQueries( Map<String, Boolean> tests )
    {
        for ( Map.Entry<String, Boolean> test : tests.entrySet() )
        {
            SqlAnalyzer analyzer = new SqlAnalyzer( test.getKey() );
            assertEquals( "tTestTable", analyzer.resolveTableName() );
            assertEquals( test.getValue().booleanValue(), analyzer.isInsertType() );
        }
    }
}

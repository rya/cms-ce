/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;


public class PrimaryKeyResolverTest
{

    private PrimaryKeyResolver primaryKeyResolver = new PrimaryKeyResolver();


    @Test
    public void testResolveIntegerWithHardcodedValues()
    {

        String sql = "update tmenuitem set mei_pag_lkey = 4, mei_mid_lkey = 3 " + "where mei_lkey = 43";

        String columnName = "mei_lkey";

        assertEquals( 43, primaryKeyResolver.resolveIntegerValue( sql, null, columnName ) );

    }

    @Test
    public void testResolveStringWithHardCodedValues()
    {

        String columnName = "grp_hkey";

        assertEquals( "ABC123",
                      primaryKeyResolver.resolveStringValue( "update tgroup set grp_sname = 'name' where grp_hkey = 'ABC123'", null,
                                                             columnName ) );

        assertEquals( "ABC123",
                      primaryKeyResolver.resolveStringValue( "update tgroup set grp_sname = 'name' where grp_hkey ='ABC123'", null,
                                                             columnName ) );

        assertEquals( "ABC123",
                      primaryKeyResolver.resolveStringValue( "update tgroup set grp_sname = 'name' where grp_hkey ='ABC123' and x = 3",
                                                             null, columnName ) );

    }

    /* These are not working at the moment...but not really needed yet either
    @Test
    public void testResolveStringSpecialWithHardCodedValues() {

        assertEquals("ABC#.$&?ÆØÅOK^", primaryKeyResolver.resolveStringValue(
                "update tgroup set grp_sname = 'name' where grp_hkey = 'ABC#.$&?ÆØÅOK^'", null, "grp_hkey"));

        assertEquals("ABC#.$&?ÆØÅOK^", primaryKeyResolver.resolveStringValue(
                "update tgroup set grp_sname = 'name' where grp_hkey = 'ABC#.$&?ÆØÅOK^' and b=3", null, "grp_hkey"));

    }*/

    /* These are not working at the moment...but not really needed yet either
    @Test
    public void testResolveStringSpecialWithHardCodedValues2() {

        assertEquals("ABC#.$&?ÆØÅOK^", primaryKeyResolver.resolveStringValue(
                "update tgroup set grp_sname = 'name' where grp_hkey = ABC#.$&?ÆØÅOK^", null, "grp_hkey"));

        assertEquals("ABC#.$&?ÆØÅOK^", primaryKeyResolver.resolveStringValue(
                "update tgroup set grp_sname = 'name' where grp_hkey = ABC#.$&?ÆØÅOK^ and b=3", null, "grp_hkey"));

    }
    */

    @Test
    public void testResolveAsInteger1()
    {

        String sql = "update tmenuitem set mei_pag_lkey = ?, mei_mid_lkey = ? where mei_lkey = ?";
        List paramList = new ArrayList();
        paramList.add( 13 );
        paramList.add( 4 );
        paramList.add( 132 );
        String columnName = "mei_lkey";

        assertEquals( 132, primaryKeyResolver.resolveIntegerValue( sql, paramList, columnName ) );

    }

    @Test
    public void testResolveAsInteger2()
    {

        String sql =
            "update tmenuitem set mei_sname = ?, mei_lparent = ?, mei_lorder = ?, mei_dtetimestamp = current_timestamp, mei_ssubtitle = ?, mei_bhidden = ?, mei_sdescription = ?, mei_skeywords = ?, mei_lan_lkey = ?, mei_usr_howner = ?, mei_usr_hmodifier = ?, mei_xmldata = ? where mei_lkey = ?";
        ArrayList paramList = new ArrayList();
        paramList.add( 0, null );
        paramList.add( 1, null );
        paramList.add( 2, 0 );
        paramList.add( 3, null );
        paramList.add( 4, 0 );
        paramList.add( 5, null );
        paramList.add( 6, null );
        paramList.add( 7, null );
        paramList.add( 8, null );
        paramList.add( 9, null );
        paramList.add( 10, null );
        paramList.add( 11, 133 );

        String columnName = "mei_lkey";

        assertEquals( 133, primaryKeyResolver.resolveIntegerValue( sql, paramList, columnName ) );

    }

    @Test
    public void testResolveAsInteger3()
    {

        String sql =
            "update tmenuitem set mei_sname = ?, mei_lparent = ?, mei_lorder = ?, mei_dtetimestamp = current_timestamp, mei_ssubtitle = ?, mei_bhidden = ?, mei_sdescription = ?, mei_skeywords = ?, mei_lan_lkey = ?, mei_usr_howner = ?, mei_usr_hmodifier = ?, mei_xmldata = ? where mei_lkey = ?";
        ArrayList paramList = new ArrayList();
        paramList.add( 0, 34 );
        paramList.add( 1, 64 );
        paramList.add( 2, 88 );
        paramList.add( 3, 23 );
        paramList.add( 4, 867 );
        paramList.add( 5, 33 );
        paramList.add( 6, 46 );
        paramList.add( 7, 98 );
        paramList.add( 8, 443 );
        paramList.add( 9, 22 );
        paramList.add( 10, 43 );
        paramList.add( 11, 133 );

        String columnName = "mei_lkey";

        assertEquals( 133, primaryKeyResolver.resolveIntegerValue( sql, paramList, columnName ) );

    }


    @Test
    public void testResolveAsString1()
    {

        String sql = "update tgroup set grp_sname = ?, grp_brestricted = ?, grp_sdescription = ? where grp_hkey = ?";

        List paramList = new ArrayList();
        paramList.add( "name" );
        paramList.add( 1 );
        paramList.add( "description" );
        paramList.add( "7258C487F730945C0049A108E88D13CBB745211F" );
        String columnName = "grp_hkey";

        assertEquals( "7258C487F730945C0049A108E88D13CBB745211F", primaryKeyResolver.resolveStringValue( sql, paramList, columnName ) );

    }


}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.portal.ContentPath;

public class SitePathTest
    extends TestCase
{

    private SiteKey siteKey_0 = new SiteKey( 0 );

    public void testCreate()
    {

        SitePath sitePath = new SitePath( siteKey_0, new Path( "ballerusk" ) );
        assertEquals( siteKey_0, sitePath.getSiteKey() );
        assertEquals( "/ballerusk", sitePath.getLocalPath().toString() );

        sitePath = new SitePath( new SiteKey( 1234 ), new Path( "/ballerusk" ) );
        assertEquals( new SiteKey( 1234 ), sitePath.getSiteKey() );
        assertEquals( "/ballerusk", sitePath.getLocalPath().toString() );

    }

    public void testGetSiteKey()
    {

        SitePath sitePath = new SitePath( siteKey_0, new Path( "/" ) );
        assertEquals( siteKey_0, sitePath.getSiteKey() );
    }

    public void testGetLocalPath()
    {

        SitePath sitePath;

        sitePath = new SitePath( siteKey_0, new Path( "/" ) );
        assertEquals( "/", sitePath.getLocalPath().toString() );

        sitePath = new SitePath( siteKey_0, new Path( "" ) );
        assertEquals( "", sitePath.getLocalPath().toString() );
    }

    public void testCreateNewInSameSite()
    {

        SitePath sitePath = new SitePath( siteKey_0, new Path( "/" ) );

        sitePath = sitePath.createNewInSameSite( new Path( "/kurt/" ) );
        assertEquals( "/kurt/", sitePath.getLocalPath().toString() );
        assertEquals( siteKey_0, sitePath.getSiteKey() );
    }

    public void testGetLocalPathElements()
    {

        SitePath sitePath = new SitePath( siteKey_0, new Path( "/News" ) );

        List<String> expectedElements = new ArrayList<String>();
        expectedElements.add( "News" );

        assertEquals( expectedElements, sitePath.getLocalPathElements() );
    }

    public void testGetLocalPathElements2()
    {

        SitePath sitePath = new SitePath( siteKey_0, new Path( "/News/Products" ) );

        List<String> expectedElements = new ArrayList<String>();
        expectedElements.add( "News" );
        expectedElements.add( "Products" );

        assertEquals( expectedElements, sitePath.getLocalPathElements() );
    }

    public void testGetLocalPathElements3()
    {

        SitePath sitePath = new SitePath( siteKey_0, new Path( "/" ) );

        List<String> expectedElements = new ArrayList<String>();

        assertEquals( expectedElements, sitePath.getLocalPathElements() );
    }

    public void testGetParamsAsString1()
    {

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put( "balle", new String[]{"rusk"} );
        params.put( "en", new String[]{"to"} );
        SitePath sitePath = new SitePath( siteKey_0, "/forside", params );

        assertEquals( "balle=rusk&en=to", sitePath.getParamsAsString() );

    }

    public void testGetParamsAsString2()
    {

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put( "balle", new String[]{"rusk"} );
        SitePath sitePath = new SitePath( siteKey_0, "/forside", params );

        assertEquals( "balle=rusk", sitePath.getParamsAsString() );

    }

    public void testEmptyOldStyleContentKey()
    {
        SitePath sitePath = new SitePath( siteKey_0, "/testcontent..cms" );

        assertEquals( null, sitePath.getContentPath() );
    }

    public void testInvalidOldStyleContentKeys()
    {
        SitePath sitePath = new SitePath( siteKey_0, "/testcontent.abc.cms" );

        assertEquals( null, sitePath.getContentPath() );

        sitePath = new SitePath( siteKey_0, "/testcontent.abc123.cms" );

        assertEquals( null, sitePath.getContentPath() );

        sitePath = new SitePath( siteKey_0, "/testcontent.12345.cms.1234abc123.cms" );

        assertEquals( null, sitePath.getContentPath() );
    }

    public void testGetParamsAsStringWithDoubleNames1()
    {

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put( "balle", new String[]{"rusk", "sten"} );
        params.put( "en", new String[]{"to"} );
        SitePath sitePath = new SitePath( siteKey_0, "/forside", params );

        assertEquals( "balle=rusk&balle=sten&en=to", sitePath.getParamsAsString() );

    }

    public void testGetParamsAsStringWithDoubleNames2()
    {

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put( "balle", new String[]{"rusk", "sten"} );
        SitePath sitePath = new SitePath( siteKey_0, "/forside", params );

        assertEquals( "balle=rusk&balle=sten", sitePath.getParamsAsString() );

    }

    public void testContentKeyWithoutTitle()
    {
        SitePath sitePath = new SitePath( siteKey_0, "/1234.cms" );
        assertNotNull( sitePath.getContentPath() );
        assertEquals( "1234", sitePath.getContentPath().getContentKey().toString() );
    }

    public void testAppendPath()
    {

        SitePath sitePath1 = new SitePath( siteKey_0, new Path( "/Registrer+medlem" ) );
        assertEquals( "/Registrer+medlem/112.action", sitePath1.appendPath( new Path( "112.action" ) ).getLocalPath().toString() );
        assertEquals( "/Registrer+medlem/112.action", sitePath1.appendPath( new Path( "/112.action" ) ).getLocalPath().toString() );

        SitePath sitePath2 = new SitePath( siteKey_0, new Path( "/Registrer+medlem/" ) );
        assertEquals( "/Registrer+medlem/112.action", sitePath2.appendPath( new Path( "112.action" ) ).getLocalPath().toString() );
        assertEquals( "/Registrer+medlem/112.action", sitePath2.appendPath( new Path( "/112.action" ) ).getLocalPath().toString() );
    }

    public void testWithContentPath()
    {
        SitePath sitePath = new SitePath( siteKey_0, new Path( "/Registrer-medlem/content-title--1234" ) );

        ContentPath contentPath = new ContentPath( new ContentKey( "1234" ), "content-title", new Path( "/Registrer-medlem" ) );

        assertEquals( contentPath, sitePath.getContentPath() );


    }

    public void testRemoveWindowReference()
    {
        // with window reference
        assertEquals( "/0/mypage", new SitePath( siteKey_0, "/mypage/_window/myportlet" ).removeWindowReference().asString() );

        // with window reference and params
        assertEquals( "/0/mypage?myparam=myvalue", new SitePath( siteKey_0, "/mypage/_window/myportlet" ).addParam( "myparam",
                                                                                                                    "myvalue" ).removeWindowReference().asString() );

        // no window reference
        assertEquals( "/0/mypage", new SitePath( siteKey_0, "/mypage" ).removeWindowReference().asString() );

        // with window reference and fragment
        assertEquals( "/0/mypage#myfragment",
                      new SitePath( siteKey_0, "/mypage/_window/myportlet#myfragment" ).removeWindowReference().asString() );

        // with window reference and fragment and params
        SitePath path =
            new SitePath( siteKey_0, "/mypage/_window/myportlet#myfragment" ).addParam( "myparam", "myvalue" ).removeWindowReference();
        assertEquals( "/0/mypage?myparam=myvalue#myfragment", path.asString() );
    }

}

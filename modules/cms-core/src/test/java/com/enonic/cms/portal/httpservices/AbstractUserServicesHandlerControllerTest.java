/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.httpservices;

import com.enonic.cms.portal.httpservices.AbstractUserServicesHandlerController;
import org.junit.Test;

import com.enonic.esl.containers.ExtendedMap;

import static org.junit.Assert.*;

public class AbstractUserServicesHandlerControllerTest
{

    @Test
    public void testUtilReplaceKeys()
    {
        ExtendedMap keys = new ExtendedMap();
        keys.put( "uid", "aab" );
        keys.put( "password", "hemmelig" );
        String inText = "Mail body uid:%uid% password:%password% notfound:%notfound%";

        String result = AbstractUserServicesHandlerController.Util.replaceKeys( keys, inText, null );
        result = AbstractUserServicesHandlerController.Util.removeTokens( result );
        assertEquals( "Mail body uid:aab password:hemmelig notfound:", result );

        result = AbstractUserServicesHandlerController.Util.replaceKeys( keys, inText, new String[]{"password"} );
        result = AbstractUserServicesHandlerController.Util.removeTokens( result );
        assertEquals( "Mail body uid:aab password: notfound:", result );

        inText = "Mail body\nuid:%uid%\npassword:%password%\nnotfound:%notfound%";
        result = AbstractUserServicesHandlerController.Util.replaceKeys( keys, inText, null );
        result = AbstractUserServicesHandlerController.Util.removeTokens( result );
        assertEquals( "Mail body\nuid:aab\npassword:hemmelig\nnotfound:", result );
    }

    @Test
    public void testRemoveTokens()
    {
        String inText = "skipp %abc!-19$% ohoi %hey% og ti flasker rom";
        String result = AbstractUserServicesHandlerController.Util.removeTokens( inText );
        assertEquals( "skipp  ohoi  og ti flasker rom", result );
    }


    @Test
    public void testFindMissingParameters()
    {
        ExtendedMap formItems = new ExtendedMap();

        formItems.put( "p1", "v1" );
        formItems.put( "p2", "v2" );
        formItems.put( "p3", "" );

        assertEquals( "Should have all required parameters", 0,
                      AbstractUserServicesHandlerController.findMissingRequiredParameters( new String[]{"p1", "p2"}, formItems,
                                                                                           false ).size() );

        assertEquals( "Should miss one required parameters", 1,
                      AbstractUserServicesHandlerController.findMissingRequiredParameters( new String[]{"p1", "p3"}, formItems,
                                                                                           false ).size() );

        assertEquals( "Should have all required parameters", 0,
                      AbstractUserServicesHandlerController.findMissingRequiredParameters( new String[]{"p1", "p3"}, formItems,
                                                                                           true ).size() );

        assertEquals( "Should miss one required parameters", 1,
                      AbstractUserServicesHandlerController.findMissingRequiredParameters(new String[]{"p1", "p2", "p5"}, formItems,
                              false).size() );
    }


}

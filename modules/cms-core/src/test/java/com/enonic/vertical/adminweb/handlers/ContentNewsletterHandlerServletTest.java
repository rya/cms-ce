/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.enonic.esl.containers.ExtendedMap;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Nov 10, 2009
 */
public class ContentNewsletterHandlerServletTest
{

    private ContentNewsletterHandlerServlet newsLetterHandlerServlet;


    @Before
    public void setUp()
    {
        newsLetterHandlerServlet = new ContentNewsletterHandlerServlet();
    }

    @Test
    public void testParseOtherRecipients()
    {
        ExtendedMap formItems = createFormItems();

        Map<String, Map<String, String>> emailMap = newsLetterHandlerServlet.parseOtherRecipients( formItems );

        assertEquals( "Wrong number of recipients parsed", 9, emailMap.keySet().size() );
    }

    private ExtendedMap createFormItems()
    {
        ExtendedMap formItems = new ExtendedMap();

        String emailAddresses = createEmailAdresses();

        formItems.put( ContentNewsletterHandlerServlet.FORM_ITEM_KEY_OTHER_RECIPIENTS, emailAddresses );

        return formItems;
    }

    private String createEmailAdresses()
    {
        StringBuffer buff = new StringBuffer();

        buff.append( "\"Runar Myklebust\" <runar@myklebust.me>" );
        buff.append( "runar2@myklebust.me\n" );
        buff.append( "runar3@myklebust.me\n" );
        buff.append( "runar4@myklebust.me runar5@myklebust.me," );
        buff.append(
            "\"Thomas Andersen\" <runar6@myklebust.me>; \"Jan-Arne Moen\" <runar7@myklebust.me>; runar8@myklebust.me,runar9@myklebust.me\n" );

        return buff.toString();
    }
}

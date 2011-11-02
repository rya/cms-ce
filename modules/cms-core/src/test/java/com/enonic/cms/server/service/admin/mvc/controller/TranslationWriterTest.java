/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.FileCopyUtils;

import junit.framework.TestCase;

public class TranslationWriterTest
    extends TestCase
{

    public void testTranslation()
        throws Exception
    {

        Map translationMap = new HashMap();
        translationMap.put( "%tekst%", "text" );
        translationMap.put( "%oversettes%", "translates" );

        StringWriter result = new StringWriter();
        StringReader source = new StringReader( "litt %tekst% som skal %oversettes%" );
        TranslationWriter dest = new TranslationWriter( translationMap, result );
        FileCopyUtils.copy( source, dest );

        assertEquals( "litt text som skal translates", result.toString() );
    }

    public void testTranslationMedNorskTegn()
        throws Exception
    {

        Map translationMap = new HashMap();
        translationMap.put( "%norskeTegn%", "Fullfør" );

        StringWriter result = new StringWriter();
        StringReader source = new StringReader( "%norskeTegn%" );
        TranslationWriter dest = new TranslationWriter( translationMap, result );
        FileCopyUtils.copy( source, dest );

        assertEquals( "Fullfør", result.toString() );
    }
}

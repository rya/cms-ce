/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.VerticalAdminLogger;

public class AdminConsoleTranslationService
{
    /**
     * Language map singleton.
     */
    private static AdminConsoleTranslationService languageMap;

    /**
     * Internal map.
     */
    private Map<String, Map<String, String>> internalMap;

    /**
     * Default translation map
     */
    private Map<String, String> defaultTranslationMap;

    /**
     * Default language *
     */
    private String defaultLanguageCode;

    public static AdminConsoleTranslationService getInstance()
    {
        return languageMap;
    }

    public AdminConsoleTranslationService()
    {
        languageMap = this;
    }

    public void setDefaultLanguageCode( String value )
    {
        this.defaultLanguageCode = value;
    }

    protected void init()
    {
        // the internal language map to build
        Map<String, Map<String, String>> languageMap = new HashMap<String, Map<String, String>>();

        BufferedReader in = null;
        try
        {
            // langfile.csv contains the language configuration
            in = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( "langfile.csv" ), "UTF-8" ) );

            ArrayList<Map<String, String>> translationMapList = new ArrayList<Map<String, String>>();
            String line = in.readLine();
            StringTokenizer st = new StringTokenizer( line, "|" );
            String variable = st.nextToken();

            // the first line will always contain the %languageCode% variable and
            // this code section initializes the map for each language
            while ( st.hasMoreTokens() )
            {
                String languageCode = st.nextToken();
                Map<String, String> translationMap = new HashMap<String, String>();
                translationMap.put( variable, languageCode );

                languageMap.put( languageCode, translationMap );
                translationMapList.add( translationMap );
            }

            // read each variable one by one with translations for each language
            line = in.readLine();
            while ( line != null && line.length() > 0 )
            {
                st = new StringTokenizer( line, "|" );
                variable = st.nextToken();

                for ( int i = 0; st.hasMoreTokens(); i++ )
                {
                    String value = st.nextToken();

                    try
                    {
                        translationMapList.get( i ).put( variable, value );
                    }
                    catch ( IndexOutOfBoundsException e )
                    {
                        throw new IllegalStateException( "Error parsing line: " + line );
                    }
                }

                line = in.readLine();
            }

            // close the langfile.csv stream
            in.close();
            in = null;

            // the language map are only available for read-only access, so each translationMap
            // are made read-only as well as the internal language map itself
            for ( Iterator<Map.Entry<String, Map<String, String>>> iter = languageMap.entrySet().iterator(); iter.hasNext(); )
            {
                Map.Entry<String, Map<String, String>> entry = iter.next();
                entry.setValue( Collections.unmodifiableMap( entry.getValue() ) );
            }
            this.internalMap = Collections.unmodifiableMap( languageMap );
            this.defaultTranslationMap = languageMap.get( defaultLanguageCode );
        }
        catch ( IOException ioe )
        {
            String msg = "Failed to read language file: %t";
            VerticalAdminLogger.errorAdmin(msg, ioe);
        }
        finally
        {
            try
            {
                if ( in != null )
                {
                    in.close();
                }
            }
            catch ( IOException ioe )
            {
                String msg = "Failed to close language file: %t";
                VerticalAdminLogger.warn(msg, ioe );
            }
        }
    }

    /**
     * @param languageCode
     * @return
     */
    public Map<String, String> getTranslationMap( String languageCode )
    {
        if ( languageCode == null )
        {
            return defaultTranslationMap;
        }
        else
        {
            if ( !internalMap.containsKey( languageCode ) )
            {
                throw new IllegalArgumentException( "Unknown language code: " + languageCode );
            }
            return internalMap.get( languageCode );
        }
    }


    public String getTranslation( final String key, final String languageCode )
    {
        Map<String, String> translationMap = getTranslationMap( languageCode );
        return translationMap.get( key );
    }

    /**
     * Get the default language code.
     *
     * @return
     */
    public String getDefaultLanguageCode()
    {
        return defaultLanguageCode;
    }

    public void toDoc( Document doc, String languageCode )
    {
        Element languagesElem = XMLTool.createElement( doc, doc.getDocumentElement(), "languages" );
        for ( Iterator<String> iter = internalMap.keySet().iterator(); iter.hasNext(); )
        {
            String code = iter.next();
            Element languageElem = XMLTool.createElement( doc, languagesElem, "language" );
            languageElem.setAttribute( "code", code );

            Map<String, String> translationMap = getTranslationMap( languageCode );
            String description = translationMap.get( "%lang" + code.toUpperCase() + "%" );
            languageElem.setAttribute( "description", description );
        }
    }

    public void toDoc( org.jdom.Document doc, String languageCode )
    {
        org.jdom.Element rootElem = doc.getRootElement();
        org.jdom.Element languagesElem = new org.jdom.Element( "languages" );
        rootElem.addContent( languagesElem );
        for ( Iterator<String> iter = internalMap.keySet().iterator(); iter.hasNext(); )
        {
            String code = iter.next();
            org.jdom.Element languageElem = new org.jdom.Element( "language" );
            languagesElem.addContent( languageElem );
            languageElem.setAttribute( "code", code );

            Map<String, String> translationMap = getTranslationMap( languageCode );
            String description = translationMap.get( "%lang" + code.toUpperCase() + "%" );
            languageElem.setAttribute( "description", description );
        }
    }
}
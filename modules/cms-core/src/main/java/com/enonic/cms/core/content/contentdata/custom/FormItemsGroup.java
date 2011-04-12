/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.esl.containers.ExtendedMap;

/**
 * Created by rmy - Date: Jun 16, 2009
 */
public class FormItemsGroup
{
    private Pattern groupPattern;

    private Map<Integer, List<String>> groupedFormEntries = new TreeMap<Integer, List<String>>();

    public FormItemsGroup( String groupName, ExtendedMap formItems )
    {
        groupPattern = Pattern.compile( groupName + "\\[(\\d+)\\]\\.(\\w+)" );

        for ( Object entry : formItems.keySet() )
        {
            String entryName = (String) entry;

            Matcher matcher = groupPattern.matcher( entryName );

            if ( matcher.matches() )
            {
                String foundEntryFormItemName = matcher.group( 0 );
                Integer groupIndex = new Integer( matcher.group( 1 ) );

                if ( groupedFormEntries.get( groupIndex ) == null )
                {
                    groupedFormEntries.put( groupIndex, new ArrayList<String>() );
                }

                groupedFormEntries.get( groupIndex ).add( foundEntryFormItemName );
            }
        }
    }

    public Set<Integer> getGroupIndexes()
    {
        return groupedFormEntries.keySet();
    }

    public List<String> getFormEntriesInGroup( Integer groupIndex )
    {
        return groupedFormEntries.get( groupIndex );
    }

    public String getFormEntryConfigName( String formEntryName )
    {
        Matcher m = groupPattern.matcher( formEntryName );

        if ( m.matches() )
        {
            return m.group( 2 );
        }

        return null;
    }
}

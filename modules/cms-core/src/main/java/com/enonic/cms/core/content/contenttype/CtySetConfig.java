/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfigType;

public class CtySetConfig
    implements CtySet
{

    private CtyFormConfig parentForm;

    private String name;

    private String groupXpath;

    private String relativeXPath;

    private List<DataEntryConfig> inputList = new ArrayList<DataEntryConfig>();

    private Map<String, DataEntryConfig> inputMap = new HashMap<String, DataEntryConfig>();

    private Map<String, DataEntryConfig> inputMapByRelativeXPath = new HashMap<String, DataEntryConfig>();

    public CtySetConfig( CtyFormConfig parentForm, String name, String groupXpath )
    {
        this.parentForm = parentForm;
        this.name = resolveName( name, groupXpath );
        this.groupXpath = groupXpath;

        if ( groupXpath != null )
        {
            this.relativeXPath = groupXpath;
        }
    }

    private static String resolveName( String name, String groupXpath )
    {
        if ( name == null || name.trim().length() == 0 )
        {
            return groupXpath;
        }

        return name;
    }

    public String getName()
    {
        return name;
    }

    public String getGroupXPath()
    {
        return groupXpath;
    }

    public void addInput( DataEntryConfig input )
    {
        String name = input.getName();

        checkTitleInputIsOfCorrectType( input );
        checkInputNameIsNotTaken( input );
        checkInputXPathIsNotTaken( input );

        inputMap.put( name, input );
        inputList.add( input );

        StringBuffer relativeXPath = new StringBuffer();
        relativeXPath.append( input.getRelativeXPath() );
        inputMapByRelativeXPath.put( relativeXPath.toString(), input );

        input.setSetConfig( this );
    }

    public DataEntryConfig getInput( int index )
    {
        return inputList.get( index );
    }


    public CtySetConfig getSetConfig( String name )
    {
        return null;
    }

    public CtySetConfig getSetConfigByRelativeXPath( String path )
    {
        return null;
    }

    public List<CtySetConfig> getSetConfig()
    {
        return null;
    }

    public DataEntryConfig getInputConfig( String name )
    {
        return inputMap.get( name );
    }

    public boolean hasInput( String name )
    {
        return inputMap.containsKey( name );
    }

    public DataEntryConfig getInputConfigByRelativeXPath( String path )
    {
        return inputMapByRelativeXPath.get( path );
    }

    public List<DataEntryConfig> getInputConfigs()
    {
        return Collections.synchronizedList( inputList );
    }

    public CtyFormConfig getParentForm()
    {
        return parentForm;
    }

    public ContentTypeConfig getContentTypeConfig()
    {
        return getParentForm().getContentTypeConfig();
    }

    public String getRelativeXPath()
    {
        return relativeXPath;
    }

    public String toString()
    {
        return getName();
    }

    private void checkTitleInputIsOfCorrectType( DataEntryConfig input )
    {
        String titleInputName = parentForm.getTitleInputName();
        if ( name.equals( titleInputName ) && input.getType() != DataEntryConfigType.TEXT )
        {
            throw new InvalidContentTypeConfigException( input, "a title input can only be of type '" + DataEntryConfigType.TEXT.getName() +
                "', found '" + input.getType().getName() + "'" );

        }
    }

    private void checkInputNameIsNotTaken( DataEntryConfig input )
    {
        if ( parentForm.hasInput( input.getName() ) )
        {
            throw new InvalidContentTypeConfigException( input, "name '" + input.getName() + "' is already taken" );
        }
    }

    private void checkInputXPathIsNotTaken( DataEntryConfig input )
    {
        if ( inputMapByRelativeXPath.containsKey( input.getXpath() ) )
        {
            throw new InvalidContentTypeConfigException( input, "xpath '" + input.getXpath() + "' is already taken" );
        }
    }

    public boolean hasGroupXPath()
    {
        return groupXpath != null;
    }
}

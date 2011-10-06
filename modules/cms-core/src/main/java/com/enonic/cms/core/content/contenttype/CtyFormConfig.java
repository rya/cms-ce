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

public class CtyFormConfig
    implements CtySet
{

    private ContentTypeConfig contentTypeConfig;

    private String titleInputName;

    private List<CtySetConfig> setConfigList = new ArrayList<CtySetConfig>();

    private Map<String, CtySetConfig> setConfigMapByName = new HashMap<String, CtySetConfig>();

    public CtyFormConfig( ContentTypeConfig contentTypeConfig )
    {
        this.contentTypeConfig = contentTypeConfig;
    }

    public String getRelativeXPath()
    {
        return null;
    }

    public String getTitleInputName()
    {
        return titleInputName;
    }

    public void setTitleInputName( String titleInputName )
    {
        this.titleInputName = titleInputName;
    }

    public CtySetConfig addBlock( CtySetConfig block )
    {
        if ( setConfigMapByName.containsKey( block.getName() ) )
        {
            throw new InvalidContentTypeConfigException(
                "Invalid block '" + block.getName() + "': name '" + block.getName() + "' is already taken" );
        }
        setConfigList.add( block );
        setConfigMapByName.put( block.getName(), block );
        return block;
    }

    public CtySetConfig getSetConfig( String name )
    {

        for ( CtySetConfig setConfig : setConfigList )
        {
            if ( name.equals( setConfig.getName() ) )
            {
                return setConfig;
            }
        }
        return null;
    }

    public CtySetConfig getSetConfigByRelativeXPath( String path )
    {
        for ( CtySetConfig setConfig : setConfigList )
        {
            if ( path.equals( setConfig.getRelativeXPath() ) )
            {
                return setConfig;
            }
        }
        return null;
    }

    public List<CtySetConfig> getSetConfig()
    {
        return Collections.unmodifiableList( setConfigList );
    }

    public DataEntryConfig getInputConfig( String name )
    {

        for ( CtySetConfig setConfig : setConfigList )
        {

            DataEntryConfig inputConfig = setConfig.getInputConfig( name );
            if ( inputConfig != null )
            {
                return inputConfig;
            }
        }
        return null;
    }

    public DataEntryConfig getInputConfigByRelativeXPath( String path )
    {
        for ( CtySetConfig setConfig : setConfigList )
        {
            if ( setConfig.getGroupXPath() != null )
            {
                // do not look thru group blocks
                continue;
            }

            DataEntryConfig inputConfig = setConfig.getInputConfigByRelativeXPath( path );
            if ( inputConfig != null )
            {
                return inputConfig;
            }
        }
        return null;
    }

    public List<DataEntryConfig> getInputConfigs()
    {
        List<DataEntryConfig> inputConfigs = new ArrayList<DataEntryConfig>();
        for ( CtySetConfig setConfig : setConfigList )
        {
            if ( setConfig.getGroupXPath() == null )
            {
                inputConfigs.addAll( setConfig.getInputConfigs() );
            }
        }

        return inputConfigs;
    }

    public CtySetConfig getBlock( int index )
    {
        return setConfigList.get( index );
    }

    public ContentTypeConfig getContentTypeConfig()
    {
        return contentTypeConfig;
    }

    public DataEntryConfig getTitleInput()
    {

        String name = getTitleInputName();

        for ( CtySetConfig block : setConfigList )
        {
            DataEntryConfig input = block.getInputConfig( name );
            if ( input != null )
            {
                return input;
            }
        }
        return null;
    }

    public boolean hasInput( String name )
    {
        for ( CtySetConfig ctySetConfig : setConfigList )
        {
            if ( ctySetConfig.hasInput( name ) )
            {
                return true;
            }
        }

        return false;
    }
}

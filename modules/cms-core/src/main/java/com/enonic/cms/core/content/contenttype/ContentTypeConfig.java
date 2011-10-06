/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.List;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class ContentTypeConfig
    implements CtySet
{

    private ContentHandlerName contentHandlerName;

    private String name;

    private CtyFormConfig form;

    private List<CtyImportConfig> imports;

    public ContentTypeConfig( ContentHandlerName contentHandlerName, String name )
    {

        this.contentHandlerName = contentHandlerName;
        this.name = name;
    }

    public String getRelativeXPath()
    {
        return null;
    }

    public CtyFormConfig getForm()
    {
        return form;
    }

    public void setForm( CtyFormConfig form )
    {
        this.form = form;
    }

    public CtyImportConfig getImport( final String importName )
    {
        if ( imports == null )
        {
            return null;
        }
        for ( CtyImportConfig importConfig : imports )
        {
            if ( importConfig.getName().equals( importName ) )
            {
                return importConfig;
            }
        }
        return null;
    }

    public void setImports( final List<CtyImportConfig> imports )
    {
        this.imports = imports;
    }

    public String getName()
    {
        return name;
    }

    public DataEntryConfig getInputConfig( String name )
    {
        return form.getInputConfig( name );
    }

    public DataEntryConfig getInputConfigByRelativeXPath( String path )
    {
        return form.getInputConfigByRelativeXPath( path );
    }

    public List<DataEntryConfig> getInputConfigs()
    {
        return null;
    }

    public List<CtySetConfig> getSetConfig()
    {
        return form.getSetConfig();
    }

    public CtySetConfig getSetConfig( String name )
    {
        return form.getSetConfig( name );
    }

    public CtySetConfig getSetConfigByRelativeXPath( String path )
    {
        return form.getSetConfigByRelativeXPath( path );
    }

    public ContentHandlerName getContentHandlerName()
    {
        return contentHandlerName;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "name = " ).append( name ).append( ", contentHandlerName = " ).append( contentHandlerName );
        return s.toString();
    }
}

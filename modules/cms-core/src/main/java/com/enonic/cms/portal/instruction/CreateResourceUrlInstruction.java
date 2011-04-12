/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

/**
 * Created by rmy - Date: Nov 18, 2009
 */
public class CreateResourceUrlInstruction
    extends PostProcessInstruction
{

    private String resolvedPath = "";

    private String[] params = new String[0];

    public CreateResourceUrlInstruction()
    {
        super( PostProcessInstructionType.CREATE_RESOURCEURL );
    }

    public void writeExternal( ObjectOutput out )
        throws IOException
    {
        writeString( out, resolvedPath );
        writeStringArray( out, params );
    }

    public void readExternal( ObjectInput in )
        throws IOException, ClassNotFoundException
    {
        resolvedPath = readString( in );
        params = readStringArray( in );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        CreateResourceUrlInstruction that = (CreateResourceUrlInstruction) o;

        if ( !Arrays.equals( params, that.params ) )
        {
            return false;
        }
        if ( resolvedPath != null ? !resolvedPath.equals( that.resolvedPath ) : that.resolvedPath != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = resolvedPath != null ? resolvedPath.hashCode() : 0;
        result = 31 * result + ( params != null ? Arrays.hashCode( params ) : 0 );
        return result;
    }

    public String[] getParams()
    {
        return params;
    }

    public void setParams( String[] params )
    {
        if ( params != null )
        {
            this.params = params;
        }
    }

    public String getResolvedPath()
    {
        return resolvedPath;
    }

    public void setResolvedPath( String resolvedPath )
    {
        if ( StringUtils.isNotEmpty( resolvedPath ) )
        {
            this.resolvedPath = resolvedPath;
        }
    }
}

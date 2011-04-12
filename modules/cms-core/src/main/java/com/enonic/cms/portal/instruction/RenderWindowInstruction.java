/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

/**
 * Created by rmy - Date: Nov 18, 2009
 */
public class RenderWindowInstruction
    extends PostProcessInstruction
{
    private String portletWindowKey = "";

    private String[] params = new String[0];

    public RenderWindowInstruction()
    {
        super( PostProcessInstructionType.CREATE_WINDOWPLACEHOLDER );
    }

    public String getPortletWindowKey()
    {
        return portletWindowKey;
    }

    public void setPortletWindowKey( String portletWindowKey )
    {
        if ( portletWindowKey != null )
        {
            this.portletWindowKey = portletWindowKey;
        }
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

    public void writeExternal( ObjectOutput out )
        throws IOException
    {
        writeString( out, portletWindowKey );
        writeStringArray( out, params );
    }

    public void readExternal( ObjectInput in )
        throws IOException, ClassNotFoundException
    {
        portletWindowKey = readString( in );
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

        RenderWindowInstruction that = (RenderWindowInstruction) o;

        if ( !Arrays.equals( params, that.params ) )
        {
            return false;
        }
        if ( portletWindowKey != null ? !portletWindowKey.equals( that.portletWindowKey ) : that.portletWindowKey != null )
        {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode()
    {
        int result = portletWindowKey != null ? portletWindowKey.hashCode() : 0;
        result = 31 * result + ( params != null ? Arrays.hashCode( params ) : 0 );
        return result;
    }
}

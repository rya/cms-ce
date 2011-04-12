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
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Oct 28, 2010
 * Time: 9:15:51 PM
 */
public class CreateContentUrlInstruction
    extends PostProcessInstruction
{
    private String contentKey = "";

    private String[] params = new String[0];

    private boolean createAsPermalink = false;

    public CreateContentUrlInstruction()
    {
        super( PostProcessInstructionType.CREATE_CONTENTURL );
    }

    public void writeExternal( ObjectOutput out )
        throws IOException
    {
        writeString( out, contentKey );
        writeBoolean( out, createAsPermalink );
        writeStringArray( out, params );

    }

    public void readExternal( ObjectInput in )
        throws IOException, ClassNotFoundException
    {
        contentKey = readString( in );
        createAsPermalink = readBoolean( in );
        params = readStringArray( in );

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

    public String getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( String contentKey )
    {
        if ( StringUtils.isNotEmpty( contentKey ) )
        {
            this.contentKey = contentKey;
        }
    }

    public boolean isCreateAsPermalink()
    {
        return createAsPermalink;
    }

    public void setCreateAsPermalink( boolean createAsPermalink )
    {
        this.createAsPermalink = createAsPermalink;
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

        CreateContentUrlInstruction that = (CreateContentUrlInstruction) o;

        if ( createAsPermalink != that.createAsPermalink )
        {
            return false;
        }
        if ( contentKey != null ? !contentKey.equals( that.contentKey ) : that.contentKey != null )
        {
            return false;
        }
        if ( !Arrays.equals( params, that.params ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = contentKey != null ? contentKey.hashCode() : 0;
        result = 31 * result + ( params != null ? Arrays.hashCode( params ) : 0 );
        result = 31 * result + ( createAsPermalink ? 1 : 0 );
        return result;
    }
}

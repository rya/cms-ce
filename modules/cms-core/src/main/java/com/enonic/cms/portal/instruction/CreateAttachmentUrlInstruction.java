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
 * Date: Nov 23, 2009
 * Time: 9:45:41 AM
 */
public class CreateAttachmentUrlInstruction
    extends PostProcessInstruction
{
    private String nativeLinkKey = "";

    private String[] params = new String[0];

    private String requestedMenuItemKey = "";

    public CreateAttachmentUrlInstruction()
    {
        super( PostProcessInstructionType.CREATE_ATTACHMENTURL );
    }

    public void writeExternal( ObjectOutput out )
        throws IOException
    {
        writeString( out, nativeLinkKey );
        writeStringArray( out, params );
        writeString( out, requestedMenuItemKey );

    }

    public void readExternal( ObjectInput in )
        throws IOException, ClassNotFoundException
    {
        nativeLinkKey = readString( in );
        params = readStringArray( in );
        requestedMenuItemKey = readString( in );
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

        CreateAttachmentUrlInstruction that = (CreateAttachmentUrlInstruction) o;

        if ( requestedMenuItemKey != null ? !requestedMenuItemKey.equals( that.requestedMenuItemKey ) : that.requestedMenuItemKey != null )
        {
            return false;
        }
        if ( nativeLinkKey != null ? !nativeLinkKey.equals( that.nativeLinkKey ) : that.nativeLinkKey != null )
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
        int result = nativeLinkKey != null ? nativeLinkKey.hashCode() : 0;
        result = 31 * result + ( params != null ? Arrays.hashCode( params ) : 0 );
        result = 31 * result + ( requestedMenuItemKey != null ? requestedMenuItemKey.hashCode() : 0 );
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

    public String getNativeLinkKey()
    {
        return nativeLinkKey;
    }

    public void setNativeLinkKey( String nativeLinkKey )
    {
        if ( nativeLinkKey != null )
        {
            this.nativeLinkKey = nativeLinkKey;
        }
    }

    public String getRequestedMenuItemKey()
    {
        return requestedMenuItemKey;
    }

    public void setRequestedMenuItemKey( String requestedMenuItemKey )
    {
        if ( StringUtils.isNotEmpty( requestedMenuItemKey ) )
        {
            this.requestedMenuItemKey = requestedMenuItemKey;
        }
    }


}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 25, 2009
 * Time: 11:18:24 AM
 */
public class CreateImageUrlInstruction
    extends PostProcessInstruction
{
    private String key = "";

    private String filter = "";

    private String background = "";

    private String format = "";

    private String quality = "";

    private String requestedMenuItemKey = "";

    public CreateImageUrlInstruction()
    {
        super( PostProcessInstructionType.CREATE_IMAGEURL );
    }

    public void writeExternal( ObjectOutput out )
        throws IOException
    {
        writeString( out, key );
        writeString( out, filter );
        writeString( out, background );
        writeString( out, format );
        writeString( out, quality );
        writeString( out, requestedMenuItemKey );
    }

    public void readExternal( ObjectInput in )
        throws IOException, ClassNotFoundException
    {
        key = readString( in );
        filter = readString( in );
        background = readString( in );
        format = readString( in );
        quality = readString( in );
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

        CreateImageUrlInstruction that = (CreateImageUrlInstruction) o;

        if ( background != null ? !background.equals( that.background ) : that.background != null )
        {
            return false;
        }
        if ( filter != null ? !filter.equals( that.filter ) : that.filter != null )
        {
            return false;
        }
        if ( format != null ? !format.equals( that.format ) : that.format != null )
        {
            return false;
        }
        if ( key != null ? !key.equals( that.key ) : that.key != null )
        {
            return false;
        }
        if ( quality != null ? !quality.equals( that.quality ) : that.quality != null )
        {
            return false;
        }
        if ( requestedMenuItemKey != null ? !requestedMenuItemKey.equals( that.requestedMenuItemKey ) : that.requestedMenuItemKey != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + ( filter != null ? filter.hashCode() : 0 );
        result = 31 * result + ( background != null ? background.hashCode() : 0 );
        result = 31 * result + ( format != null ? format.hashCode() : 0 );
        result = 31 * result + ( quality != null ? quality.hashCode() : 0 );
        result = 31 * result + ( requestedMenuItemKey != null ? requestedMenuItemKey.hashCode() : 0 );
        return result;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        if ( StringUtils.isNotEmpty( key ) )
        {
            this.key = key;
        }
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter( String filter )
    {
        if ( StringUtils.isNotEmpty( filter ) )
        {
            this.filter = filter;
        }
    }

    public String getBackground()
    {
        return background;
    }

    public void setBackground( String background )
    {
        if ( StringUtils.isNotEmpty( background ) )
        {
            this.background = background;
        }
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat( String format )
    {
        if ( StringUtils.isNotEmpty( format ) )
        {
            this.format = format;
        }
    }

    public String getQuality()
    {
        return quality;
    }

    public void setQuality( String quality )
    {
        if ( StringUtils.isNotEmpty( quality ) )
        {
            this.quality = quality;
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

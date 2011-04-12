/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by rmy - Date: Nov 18, 2009
 */
public abstract class PostProcessInstruction
    implements Externalizable
{

    private final PostProcessInstructionType type;

    private boolean disableOutputEscaping = false;

    private boolean doUrlEncodeResult = false;

    public PostProcessInstruction( PostProcessInstructionType type )
    {
        this.type = type;
    }

    public final String serialize()
        throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream dout = new ObjectOutputStream( out );

        writeExternal( dout );

        dout.close();
        out.close();

        return new String( Base64.encodeBase64( out.toByteArray() ) );
    }


    public final void deserialize( String value )
        throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream in = new ByteArrayInputStream( Base64.decodeBase64( value.getBytes() ) );

        ObjectInputStream din = new ObjectInputStream( in );

        readExternal( din );

        in.close();
        din.close();
    }


    public final PostProcessInstructionType getType()
    {
        return type;
    }

    protected void writeString( ObjectOutput out, String value )
        throws IOException
    {
        out.writeUTF( value );
    }

    protected void writeBoolean( ObjectOutput out, boolean value )
        throws IOException
    {
        out.writeBoolean( value );
    }

    protected void writeStringArray( ObjectOutput out, String[] array )
        throws IOException
    {
        out.writeInt( array.length );

        for ( String element : array )
        {
            out.writeUTF( element );
        }
    }

    protected String readString( ObjectInput in )
        throws IOException
    {
        return in.readUTF();
    }

    protected boolean readBoolean( ObjectInput in )
        throws IOException
    {
        return in.readBoolean();
    }

    protected String[] readStringArray( ObjectInput in )
        throws IOException
    {
        String[] array = new String[in.readInt()];

        for ( int i = 0; i < array.length; i++ )
        {
            array[i] = in.readUTF();
        }

        return array;
    }

    public boolean doDisableOutputEscaping()
    {
        return disableOutputEscaping;
    }

    public void setDisableOutputEscaping( boolean disableOutputEscaping )
    {
        this.disableOutputEscaping = disableOutputEscaping;
    }

    public boolean doUrlEncodeResult()
    {
        return doUrlEncodeResult;
    }

    public void setDoUrlEncodeResult( boolean doUrlEncodeResult )
    {
        this.doUrlEncodeResult = doUrlEncodeResult;
    }
}

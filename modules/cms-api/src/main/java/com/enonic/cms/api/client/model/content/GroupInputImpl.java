/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupInputImpl
    implements Serializable, Input, GroupInput
{

    private static final long serialVersionUID = -2748642816305029863L;

    private String name;

    private List<Input> inputs = new ArrayList<Input>();

    protected GroupInputImpl( String name )
    {
        this.name = name;
    }

    public InputType getType()
    {
        return InputType.SET;
    }

    public String getName()
    {
        return name;
    }

    public Input add( Input input )
    {
        inputs.add( input );
        return input;
    }

    public GroupInput addGroup( String name )
    {
        throw new UnsupportedOperationException( "Groups in groups, not allowed" );
    }

    public List<Input> getInputs()
    {
        return inputs;
    }

    public Input getInput( String name )
    {
        for ( Input input : inputs )
        {
            if ( input.getName().equals( name ) )
            {
                return input;
            }
        }
        return null;
    }

    public List<BinaryInput> getBinaryInputs()
    {
        List<BinaryInput> list = new ArrayList<BinaryInput>();
        for ( Input input : inputs )
        {
            if ( input instanceof BinaryInput )
            {
                list.add( (BinaryInput) input );
            }
            else if ( input instanceof GroupInput )
            {
                GroupInput groupInput = (GroupInput) input;
                List<BinaryInput> binaryInputs = groupInput.getBinaryInputs();
                list.addAll( binaryInputs );
            }
        }
        return list;
    }
}

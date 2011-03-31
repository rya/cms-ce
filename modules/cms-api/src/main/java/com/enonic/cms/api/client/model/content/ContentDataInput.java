/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContentDataInput
    implements Serializable, GroupInput
{


    private static final long serialVersionUID = 4397306476712457263L;

    private String contentTypeName;

    private List<Input> inputs = new ArrayList<Input>();

    public ContentDataInput( String contentTypeName )
    {
        this.contentTypeName = contentTypeName;
    }

    public String getContentTypeName()
    {
        return contentTypeName;
    }

    public Input add( Input input )
    {
        inputs.add( input );
        return input;
    }

    public GroupInput addGroup( String name )
    {
        GroupInputImpl setInput = new GroupInputImpl( name );
        inputs.add( setInput );
        return setInput;
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

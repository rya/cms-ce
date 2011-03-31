/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.util.List;

public interface GroupInput
{

    public Input add( Input input );

    public GroupInput addGroup( String name );

    public List<Input> getInputs();

    public Input getInput( String name );

    public List<BinaryInput> getBinaryInputs();
}
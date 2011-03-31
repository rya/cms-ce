/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.util.List;

public interface InputSet
{

    public Input add( Input input );

    List<Input> getInputs();

}

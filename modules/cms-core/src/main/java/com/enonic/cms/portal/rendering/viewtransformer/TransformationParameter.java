/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.viewtransformer;


public interface TransformationParameter
{
    String getName();

    Object getValue();

    TransformationParameterOrigin getOrigin();
}

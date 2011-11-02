/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.param;

public class EncryptedParameterSerializerTest
    extends AbstractParameterSerializerTest
{
    public EncryptedParameterSerializerTest()
    {
        super( new EncryptedParameterSerializer() );
    }
}
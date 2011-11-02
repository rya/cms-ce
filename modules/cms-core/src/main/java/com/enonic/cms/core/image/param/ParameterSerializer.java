/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.param;

import java.util.List;
import java.util.Map;

/**
 * This interface defines a serializer to be used to encrypt/decrypt parameters.
 */
public interface ParameterSerializer
{
    public String serialize( String value );

    public String serializeList( List<String> value );

    public String serializeMap( Map<String, String> map );

    public String deserialize( String value );

    public List<String> deserializeList( String value );

    public Map<String, String> deserializeMap( String value );
}

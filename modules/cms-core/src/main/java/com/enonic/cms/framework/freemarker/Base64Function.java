/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.freemarker;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * This is a class representing a Freemarker function.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Base64Function
    implements TemplateMethodModel
{
    public Object exec( List arguments )
        throws TemplateModelException
    {
        if ( arguments.size() != 1 )
        {
            throw new TemplateModelException( "This function requires one argument." );
        }

        Object argument = arguments.get( 0 );
        if ( argument instanceof String )
        {
            String argumentAsString = (String) argument;
            return encodeAsBase64( argumentAsString );
        }
        else
        {
            throw new TemplateModelException( "This function requires one argument that is a string." );
        }
    }

    private Object encodeAsBase64( String input )
    {
        try
        {
            return new String( Base64.encodeBase64( input.getBytes( "UTF-8" ) ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( "Failed to encode string: ", e );
        }
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.locale;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.resolver.AbstractXsltScriptResolver;

import com.enonic.cms.domain.resolver.ScriptResolverResult;
import com.enonic.cms.domain.resolver.locale.LocaleParser;

/**
 * Created by rmy - Date: Apr 29, 2009
 */
public class LocaleXsltScriptResolver
        extends AbstractXsltScriptResolver
{
    private static final Logger LOG = LoggerFactory.getLogger( LocaleXsltScriptResolver.class );

    public final static String LOCALE_RETURN_VALUE_KEY = "locale";

    protected ScriptResolverResult populateScriptResolverResult( String resolvedValue )
    {
        ScriptResolverResult result = new ScriptResolverResult();

        if ( StringUtils.isNotEmpty( resolvedValue ) )
        {
            Locale locale = null;
            try
            {
                locale = LocaleParser.parseLocale( resolvedValue );
            }
            catch ( Exception e )
            {
                LOG.warn( "Could not parse script-result: '" + resolvedValue + "' to a valid locale" );
            }

            result.getResolverReturnValues().put( LOCALE_RETURN_VALUE_KEY, locale );
        }

        return result;
    }
}

package com.enonic.cms.server.service.tools;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Joiner;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.ExtensionBase;
import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.plugin.ext.TextExtractor;
import com.enonic.cms.api.plugin.ext.http.HttpProcessor;

public final class ExtensionWrapper
{
    private final Extension extension;

    public ExtensionWrapper( final Extension extension )
    {
        this.extension = extension;
    }

    public String getHtml()
    {
        if ( this.extension instanceof FunctionLibrary )
        {
            return toHtml( (FunctionLibrary) this.extension );
        }
        else if ( this.extension instanceof TaskHandler )
        {
            return toHtml( (TaskHandler) this.extension );
        }
        else if ( this.extension instanceof TextExtractor )
        {
            return toHtml( (TextExtractor) this.extension );
        }
        else if ( this.extension instanceof HttpProcessor )
        {
            return toHtml( (HttpProcessor) this.extension );
        }
        else
        {
            return composeHtml( this.extension );
        }
    }

    private static String toHtml( final FunctionLibrary ext )
    {
        return composeHtml( ext, "name", ext.getName(), "target", ext.getTargetClass().getName() );
    }

    private static String toHtml( final TaskHandler ext )
    {
        return composeHtml( ext, "name", ext.getName() );
    }

    private static String toHtml( final TextExtractor ext )
    {
        return composeHtml( ext );
    }

    private static String toHtml( final HttpProcessor ext )
    {
        return composeHtml( ext, "priority", ext.getPriority(), "urlPatterns", Joiner.on( ", " ).skipNulls().join( ext.getUrlPatterns() ) );
    }

    private static String composeHtml( final Extension ext, final Object... props )
    {
        final StringBuffer str = new StringBuffer();
        str.append( "<span title=\"" ).append( ext.getClass().getName() ).append( "\"><strong>" );
        str.append( getDisplayName( ext ) );
        str.append( "</strong></span>" );

        if ( props != null )
        {
            str.append( "<ul>" );

            for ( int i = 0; i < props.length; i += 2 )
            {
                final String key = String.valueOf( props[i] );
                final String value = ( i < ( props.length - 1 ) ) ? String.valueOf( props[i + 1] ) : "";
                str.append( "<li>" ).append( key ).append( " : " ).append( value ).append( "</li>" );
            }

            str.append( "</ul>" );
        }

        return str.toString();
    }

    private static String getDisplayName( final Extension extension )
    {
        if ( extension instanceof ExtensionBase )
        {
            return ( (ExtensionBase) extension ).getDisplayName();
        }

        return extension.getClass().getSimpleName();
    }

    public static Collection<ExtensionWrapper> toWrapperList( final Collection<? extends Extension> list )
    {
        final ArrayList<ExtensionWrapper> target = new ArrayList<ExtensionWrapper>();
        for ( final Extension ext : list )
        {
            target.add( new ExtensionWrapper( ext ) );
        }

        return target;
    }
}

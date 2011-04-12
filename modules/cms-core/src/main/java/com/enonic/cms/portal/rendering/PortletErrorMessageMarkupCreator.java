/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;

import com.enonic.cms.framework.util.HtmlEncoder;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;

import com.enonic.cms.portal.PortletXsltViewTransformationException;

/**
 * Apr 30, 2009
 */
public class PortletErrorMessageMarkupCreator
{
    private Set<Throwable> alreadyPrintedThrows = new HashSet<Throwable>();

    private int count = 0;

    public String createMarkup( String message, Exception exception )
    {
        StringBuffer str = new StringBuffer();
        str.append( "<div class=\"-cms-portlet-error\"" );
        str.append( " style=\"" );
        str.append( " border-style: solid;" );
        str.append( " border-width: 2px;" );
        str.append( " border-color: #FF0000;" );
        str.append( " background-color: #FFC0C0;" );
        str.append( " color: black;" );
        str.append( " font-size: medium;" );
        str.append( " padding: 4px;" );
        str.append( " text-align: left" );
        str.append( "\"" );
        str.append( ">" );
        str.append( "<strong>" ).append( message ).append( "</strong>" );

        if ( exception instanceof PortletXsltViewTransformationException )
        {
            printXsltErrors( (PortletXsltViewTransformationException) exception, str );
        }

        str.append( "<ul>" );
        str.append( "<p>" );
        str.append( "Stack trace messages:" );
        str.append( "</p>" );
        printThrowable( exception, str );
        str.append( "</ul>" );
        str.append( "</div>" );
        return str.toString();
    }

    private void printXsltErrors( PortletXsltViewTransformationException pxvte, StringBuffer str )
    {
        XsltProcessorErrors xsltErros = pxvte.getErrors();
        if ( xsltErros.hasErrors() )
        {
            str.append( "<p>" );
            str.append( "XSLT errors:" );
            str.append( "</p>" );
            str.append( "<ul>" );
            for ( TransformerException tranfomerException : xsltErros.getFatalErrors() )
            {
                str.append( "<li>" );
                str.append( tranfomerException.getMessageAndLocation() );
                str.append( "</li>" );
            }
            for ( TransformerException tranfomerException : xsltErros.getErrors() )
            {
                str.append( "<li>" );
                str.append( tranfomerException.getMessageAndLocation() );
                str.append( "</li>" );
            }
            str.append( "</ul>" );
        }
    }

    private void printThrowable( Throwable t, StringBuffer str )
    {
        if ( alreadyPrintedThrows.contains( t ) )
        {
            return;
        }

        count++;

        alreadyPrintedThrows.add( t );

        if ( t instanceof XsltProcessorException )
        {
            printXsltProcessorException( (XsltProcessorException) t, str );
        }
        else
        {
            printDefaultThrowable( t, str );
            Throwable nextCause = t.getCause();
            if ( nextCause != null )
            {
                printThrowable( nextCause, str );
            }
        }
    }

    private void printDefaultThrowable( Throwable t, StringBuffer str )
    {
        str.append( "<li>" );
        if ( count > 1 )
        {
            str.append( "Caused by: " );
        }
        str.append( HtmlEncoder.encode( t.getMessage() ) );
        str.append( "</li>" );
    }

    private void printXsltProcessorException( XsltProcessorException xpe, StringBuffer str )
    {
        str.append( "<li>" );
        if ( count > 1 )
        {
            str.append( "Caused by: " );
        }
        str.append( HtmlEncoder.encode( xpe.getMessage() ) );

        str.append( "<ul>" );
        for ( TransformerException e : xpe.getErrors().getAllErrors() )
        {
            int lineNumber = e.getLocator() != null ? e.getLocator().getLineNumber() : -1;
            str.append( "<li>" );
            str.append( HtmlEncoder.encode( e.getMessage() ) ).append( " (line " ).append( lineNumber );
            str.append( "</li>" );
        }
        str.append( "</ul>" );
        str.append( "</li>" );
    }
}

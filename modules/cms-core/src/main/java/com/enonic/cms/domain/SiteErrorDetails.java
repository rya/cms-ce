/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.framework.util.HtmlEncoder;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;

import com.enonic.cms.api.Version;

/**
 * This class implements site error details.
 */
public final class SiteErrorDetails
{
    private final Request request;

    private final Throwable error;

    private final int statusCode;

    private final List<Throwable> allErrors;

    private final String NO_MESSAGE = "No message";

    /**
     * Constructs the error details.
     */
    public SiteErrorDetails( HttpServletRequest request, Throwable throwable, int statusCode )
    {
        this.request = new Request( request );
        this.error = throwable;
        this.statusCode = statusCode;
        this.allErrors = findAllExceptions( this.error );
    }

    /**
     * Return the error title.
     */
    public String getTitle()
    {
        return "An Error Occured (" + this.statusCode + ")";
    }

    /**
     * Return the message.
     */
    public String getMessage()
    {
        String message = this.error.getMessage();
        if ( message == null )
        {
            return NO_MESSAGE;
        }
        return message;
    }

    /**
     * Return the request.
     */
    public Request getRequest()
    {
        return this.request;
    }

    /**
     * Return the response error.
     */
    public int getStatusCode()
    {
        return this.statusCode;
    }

    /**
     * Return the exception.
     */
    public Throwable getException()
    {
        return this.error;
    }

    /**
     * Return number of stack traces.
     */
    public int getNumExceptions()
    {
        return this.allErrors.size();
    }

    /**
     * Return the error title of error number.
     */
    public String getExceptionMessage( int num )
    {
        if ( ( num >= 0 ) && ( num < this.allErrors.size() ) )
        {
            String message = this.allErrors.get( num ).getMessage();
            if ( message != null )
            {
                return message;
            }
            else
            {
                return NO_MESSAGE;
            }
        }
        else
        {
            return "";
        }
    }

    /**
     * Return the exception stack trace.
     */
    public String getExceptionStackTrace( int num )
    {
        return getExceptionStackTrace( num, -1 );
    }

    /**
     * Return the exception stack trace.
     */
    public String getExceptionStackTrace( int num, int numLines )
    {
        if ( ( num >= 0 ) && ( num < this.allErrors.size() ) )
        {
            return getExceptionStackTrace( this.allErrors.get( num ), numLines );
        }
        else
        {
            return "";
        }
    }

    /**
     * Return the exception stack trace.
     */
    private static String getExceptionStackTrace( Throwable error, int numLines )
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( error.getClass().getName() );

        if ( error.getMessage() != null )
        {
            buffer.append( ": " ).append( error.getMessage() );
        }

        buffer.append( "\n" );
        StackTraceElement[] elements = error.getStackTrace();

        int numLinesToPrint = numLines >= 0 ? Math.min( elements.length, numLines ) : elements.length;
        for ( int i = 0; i < numLinesToPrint; i++ )
        {
            buffer.append( "  at " ).append( elements[i].toString() ).append( "\n" );
        }

        int numMore = elements.length - numLinesToPrint;
        if ( numMore > 0 )
        {
            buffer.append( "  ... (" ).append( numMore ).append( " more) ..." );
        }

        return buffer.toString();
    }

    /**
     * Find all exceptions.
     */
    private static List<Throwable> findAllExceptions( Throwable error )
    {
        ArrayList<Throwable> list = new ArrayList<Throwable>();
        findAllExceptions( list, error );
        return list;
    }

    /**
     * Find all exceptions.
     */
    private static void findAllExceptions( List<Throwable> list, Throwable error )
    {
        if ( ( error != null ) && !list.contains( error ) )
        {
            if ( error instanceof XsltProcessorException )
            {
                XsltProcessorErrors errors = ( (XsltProcessorException) error ).getErrors();
                if ( errors != null )
                {
                    for ( Exception e : errors.getAllErrors() )
                    {
                        findAllExceptions( list, e );
                    }
                }
                else
                {
                    list.add( error );
                }
            }
            else
            {
                list.add( error );
            }

            findAllExceptions( list, error.getCause() );
        }
    }

    /**
     * Return generated on string.
     */
    public String getGeneratedOnString()
    {
        return "Generated by " + Version.getTitleAndVersion() + " on " + ( new Date() ).toString();
    }

    /**
     * A wrapper of the HttpServletRequest that translates null-returns to empty strings.
     */
    public class Request
    {

        private final HttpServletRequest request;

        public Request( HttpServletRequest request )
        {
            this.request = request;
        }

        public String getQueryString()
        {
            return getAsSafe( HtmlEncoder.encode( request.getQueryString() ) );
        }

        public String getServletPath()
        {
            return getAsSafe( request.getServletPath() );
        }

        public String getRemoteAddr()
        {
            return getAsSafe( request.getRemoteAddr() );
        }

        public String getRequestURI()
        {
            return getAsSafe( request.getRequestURI() );
        }

        public String getPathInfo()
        {
            return getAsSafe( request.getPathInfo() );
        }

        public String getContextPath()
        {
            return getAsSafe( request.getContextPath() );
        }

        public String getMethod()
        {
            return getAsSafe( request.getMethod() );
        }

        public String getProtocol()
        {
            return getAsSafe( request.getProtocol() );
        }

        public String getScheme()
        {
            return getAsSafe( request.getScheme() );
        }

        public String getServerName()
        {
            return getAsSafe( request.getServerName() );
        }

        public Enumeration getParameterNames()
        {
            return request.getParameterNames();
        }

        public String getParameterValuesAsCommaSeparatedString( String name )
        {
            StringBuffer s = new StringBuffer( "" );
            String[] values = request.getParameterValues( name );
            for ( int i = 0; i < values.length; i++ )
            {
                String v = values[i];
                v = HtmlEncoder.encode( v );
                s.append( v );
                if ( i < values.length - 1 )
                {
                    s.append( "," );
                }
            }
            return s.toString();
        }

        private String getAsSafe( String value )
        {
            if ( value == null )
            {
                return "";
            }
            return value;
        }

    }
}

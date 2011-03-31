/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;

/**
 * This class implements the base render functions.
 */
public final class DataSourceFailedXmlCreator
{
    /**
     * Build exception document.
     */
    public static Document buildExceptionDocument( final String resultRootName, Throwable e )
    {
        Document doc = new Document( new Element( resultRootName ) );
        Element root = doc.getRootElement();

        for ( Throwable tmp : findAllExceptions( e ) )
        {
            root.addContent( buildExceptionElement( tmp ) );
        }

        return doc;
    }

    /**
     * Build exception exception.
     */
    private static Element buildExceptionElement( Throwable e )
    {
        Element root = new Element( "exception" );
        String clzName = e.getClass().getName();
        String message = e.getMessage();
        root.addContent( new Element( "class" ).setText( clzName ) );

        if ( message != null )
        {
            root.addContent( new Element( "message" ).setText( message ) );
        }

        Element trace = new Element( "trace" );
        root.addContent( trace );
        for ( StackTraceElement traceElem : e.getStackTrace() )
        {
            Element elem = new Element( "element" );
            elem.setAttribute( "class", traceElem.getClassName() );
            elem.setAttribute( "method", traceElem.getMethodName() );
            elem.setAttribute( "line", "" + traceElem.getLineNumber() );
            trace.addContent( elem );
        }

        Element causes = new Element( "causes" );
        root.addContent( causes );

        for ( Throwable cause : getCauses( e ) )
        {
            causes.addContent( buildExceptionElement( cause ) );
        }

        return root;
    }

    /**
     * Return the causes.
     */
    private static List<Throwable> getCauses( Throwable e )
    {
        ArrayList<Throwable> causes = new ArrayList<Throwable>();
        Throwable cause = e.getCause();

        while ( cause != null )
        {
            causes.add( cause );
            cause = cause.getCause();
        }

        return causes;
    }

    /**
     * Find all exceptions.
     */
    private static List<Throwable> findAllExceptions( Throwable ex )
    {
        LinkedList<Throwable> list = new LinkedList<Throwable>();
        findAllExceptions( list, ex );
        return list;
    }

    /**
     * Find all exceptions.
     */
    private static void findAllExceptions( List<Throwable> list, Throwable ex )
    {
        if ( ex instanceof XsltProcessorException )
        {
            XsltProcessorErrors errors = ( (XsltProcessorException) ex ).getErrors();
            list.addAll( errors.getAllErrors() );
        }
        else
        {
            list.add( ex );

            if ( ex.getCause() != null )
            {
                findAllExceptions( list, ex.getCause() );
            }
        }
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.PrintWriter;

import javax.servlet.ServletException;

import com.enonic.esl.util.VectorWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalRuntimeException;

public final class ThrowableUtil
{
    /**
     * ExceptionUtil constructor comment.
     */
    private ThrowableUtil()
    {
    }

    public static Document throwableToDoc( Element parentElem, Throwable t )
    {

        Document doc;
        Element throwablesElem;
        if ( parentElem == null )
        {
            doc = XMLTool.createDocument( "throwables" );
            throwablesElem = doc.getDocumentElement();
        }
        else
        {
            doc = parentElem.getOwnerDocument();
            throwablesElem = XMLTool.createElement( doc, parentElem, "throwables" );
        }

        if ( t != null )
        {
            Element root = throwablesElem;

            Element prevThrowable = null;
            String[] throwableStackTrace = null;
            do
            {
                Element throwable = XMLTool.createElement( doc, "throwable" );
                XMLTool.createElement( doc, throwable, "class", t.getClass().getName() );

                if ( t instanceof VerticalException )
                {
                    VerticalException ve = (VerticalException) t;
                    XMLTool.createElement( doc, throwable, "message", ve.getMessage() );
                    StackTraceElement[] stackTrace = ve.getStackTrace();
                    Element stacktraceElem = XMLTool.createElement( doc, throwable, "stacktrace" );
                    for ( int i = 0; i < stackTrace.length; i++ )
                    {
                        XMLTool.createElement( doc, stacktraceElem, "stacktraceitem", stackTrace[i].toString() );
                    }

                    t = ve.getCause();
                }
                else if ( t instanceof VerticalRuntimeException )
                {
                    VerticalRuntimeException vre = (VerticalRuntimeException) t;
                    XMLTool.createElement( doc, throwable, "message", vre.getMessage() );
                    StackTraceElement[] stackTrace = vre.getStackTrace();
                    Element stacktraceElem = XMLTool.createElement( doc, throwable, "stacktrace" );
                    for ( int i = 0; i < stackTrace.length; i++ )
                    {
                        XMLTool.createElement( doc, stacktraceElem, "stacktraceitem", stackTrace[i].toString() );
                    }

                    t = vre.getCause();
                }
                else
                {
                    String message = t.getMessage();
                    if ( message != null )
                    {
                        XMLTool.createElement( doc, throwable, "message", message );
                    }
                    else
                    {
                        XMLTool.createElement( doc, throwable, "message", "null" );
                    }
                    Element stacktraceElem = XMLTool.createElement( doc, throwable, "stacktrace" );

                    if ( throwableStackTrace != null )
                    {
                        for ( int i = 0; i < throwableStackTrace.length; i++ )
                        {
                            XMLTool.createElement( doc, stacktraceElem, "stacktraceitem", throwableStackTrace[i] );
                        }
                    }
                    else
                    {
                        VectorWriter vw = new VectorWriter();
                        t.printStackTrace( new PrintWriter( vw ) );
                        String[] stackTrace = vw.toStringArray();
                        for ( int i = 0; i < stackTrace.length; i++ )
                        {
                            XMLTool.createElement( doc, stacktraceElem, "stacktraceitem", stackTrace[i] );
                        }
                    }

                    if ( t instanceof ServletException )
                    {
                        t = ( (ServletException) t ).getRootCause();
                    }
                    else
                    {
                        t = null;
                    }
                }

                root.appendChild( throwable );
                prevThrowable = throwable;
            }
            while ( t != null );
        }

        return doc;
    }
}
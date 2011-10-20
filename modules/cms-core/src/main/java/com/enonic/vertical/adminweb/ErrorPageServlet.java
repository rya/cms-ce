/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.utilities.ThrowableUtil;

import com.enonic.cms.core.xslt.XsltProcessorHelper;

public class ErrorPageServlet
    implements Controller
{

    public static interface Error
    {
        Document toDoc();
    }

    public final static class ThrowableError
        implements Error
    {
        private Throwable t;

        public ThrowableError( Throwable t )
        {
            this.t = t;
        }

        public Document toDoc()
        {
            Document doc = XMLTool.createDocument( "error" );
            return ThrowableUtil.throwableToDoc( doc.getDocumentElement(), t, false );
        }
    }

    public final static class MessageError
        implements Error
    {
        private Object message;

        public MessageError( Object message )
        {
            this.message = message;
        }

        public Document toDoc()
        {
            Document doc = XMLTool.createDocument( "error" );
            Element root = doc.getDocumentElement();
            if ( message != null )
            {
                XMLTool.createElement( doc, root, "message", message.toString() );
            }
            else
            {
                XMLTool.createElement( doc, root, "message" );
            }
            return doc;
        }
    }

    public final static class TypeError
        implements Error
    {
        private Object type;

        private Object message;

        public TypeError( Object type, Object message )
        {
            this.type = type;
            this.message = message;
        }

        public Document toDoc()
        {
            String fullMessage = type + ": " + message;
            Document doc = XMLTool.createDocument( "error" );
            Element root = doc.getDocumentElement();
            XMLTool.createElement( doc, root, "message", fullMessage );
            return doc;
        }
    }

    public final static class CodeError
        implements Error
    {
        private Object code;

        private Object message;

        public CodeError( Object code, Object message )
        {
            this.code = code;
            this.message = message;
        }

        public Document toDoc()
        {
            String fullMessage = code + ": " + message;
            Document doc = XMLTool.createDocument( "error" );
            Element root = doc.getDocumentElement();
            XMLTool.createElement( doc, root, "message", fullMessage );
            return doc;
        }
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        performTask( request, response );
        return null;
    }

    /**
     * Process incoming requests for information
     *
     * @param request  Object that encapsulates the request to the servlet
     * @param response Object that encapsulates the response from the servlet
     */
    public void performTask( HttpServletRequest request, HttpServletResponse response )
    {

        HttpSession session = request.getSession( true );
        Source xslSource = AdminStore.getStylesheet( session, "error_display.xsl" );

        response.setContentType( "text/html;charset=UTF-8" );

        // First check if we are used as standard error page for servlet container
        Object codeObj, messageObj, typeObj, exceptionObj;
        codeObj = request.getAttribute( "javax.servlet.error.status_code" );
        messageObj = request.getAttribute( "javax.servlet.error.message" );
        typeObj = request.getAttribute( "javax.servlet.error.exception_type" );
        exceptionObj = request.getAttribute( "javax.servlet.error.exception" );

        Error error;
        if ( exceptionObj != null )
        {
            Throwable t = (Throwable) exceptionObj;
            error = new ThrowableError( t );
            session.setAttribute( "com.enonic.vertical.error", error );
        }
        else if ( typeObj != null )
        {
            error = new TypeError( typeObj, messageObj );
            session.setAttribute( "com.enonic.vertical.error", error );
        }
        else if ( codeObj != null )
        {
            error = new CodeError( codeObj, messageObj );
            session.setAttribute( "com.enonic.vertical.error", error );
        }
        else
        {
            error = (Error) session.getAttribute( "com.enonic.vertical.error" );
            if ( error == null )
            {
                error = new MessageError( "No message or throwable given!" );
            }
        }
        DOMSource xmlSource = new DOMSource( error.toDoc() );

        Hashtable parameters = new Hashtable();
        String showDetails = (String) request.getParameter( "show_details" );
        if ( showDetails != null && showDetails.length() > 0 )
        {
            parameters.put( "show_details", showDetails );
        }
        else
        {
            parameters.put( "show_details", "false" );
        }

        String admin_email = VerticalProperties.getVerticalProperties().getAdminEmail();
        if ( admin_email != null )
        {
            parameters.put( "admin_email", admin_email );
        }

        try
        {

            PrintWriter out = response.getWriter();
            out.print( transformXML( xmlSource, xslSource, parameters ) );
            out.close();
        }
        catch ( Exception e )
        {
            String message = "Failed to transform & print output";
            VerticalAdminLogger.errorAdmin(message, e);
        }
    }

    public String transformXML( Source xmlSource, Source xslSource, Hashtable parameters )
    {
        return new XsltProcessorHelper()
                .stylesheet( xslSource, null )
                .input( xmlSource )
                .params( parameters )
                .process();
    }
}
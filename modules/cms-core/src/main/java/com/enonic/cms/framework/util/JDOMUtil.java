/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;

import com.google.common.base.Strings;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;

/**
 * This implements common JDOM utility methods.
 */
public final class JDOMUtil
{
    private final static ThreadLocal<SAXBuilder> threadLocalSAXBuilder = new ThreadLocal<SAXBuilder>()
    {
        @Override
        protected SAXBuilder initialValue()
        {
            SAXBuilder builder = new SAXBuilder();
            builder.setFastReconfigure( true );
            return builder;
        }
    };

    /**
     * Parse document.
     */
    public static Document parseDocument( InputStream in )
        throws IOException, JDOMException
    {
        SAXBuilder builder = getSAXBuilder();
        return builder.build( in );
    }

    /**
     * Parse document.
     */
    public static Document parseDocument( String xml )
        throws IOException, JDOMException
    {
        SAXBuilder builder = getSAXBuilder();
        return builder.build( new StringReader( xml ) );
    }

    /**
     * Convert org.w3c.dom.Document to JDOM document.
     */
    public static Document toDocument( org.w3c.dom.Node node )
    {
        if ( node instanceof org.w3c.dom.Document )
        {
            return toDocument( (org.w3c.dom.Document) node );
        }
        else if ( node instanceof org.w3c.dom.Element )
        {
            return toDocument( (org.w3c.dom.Element) node );
        }
        else
        {
            return toDocument( node.getOwnerDocument() );
        }
    }

    /**
     * Convert org.w3c.dom.Document to JDOM document.
     */
    private static Document toDocument( org.w3c.dom.Document doc )
    {
        DOMBuilder builder = new DOMBuilder();
        return builder.build( doc );
    }

    /**
     * Convert org.w3c.dom.Element to JDOM document.
     */
    private static Document toDocument( org.w3c.dom.Element element )
    {
        DOMBuilder builder = new DOMBuilder();
        return new Document( (Element) builder.build( element ).detach() );
    }

    /**
     * Convert to w3c dom.
     */
    public static org.w3c.dom.Document toW3CDocument( Document doc )
        throws JDOMException
    {
        DOMOutputter builder = new DOMOutputter();
        return builder.output( doc );
    }

    /**
     * Return first child element.
     */
    public static Element getFirstElement( Element root )
    {
        if ( root != null )
        {
            List list = root.getChildren();
            if ( !list.isEmpty() )
            {
                return (Element) list.get( 0 );
            }
        }

        return null;
    }

    /**
     * Return named sub element.
     */
    public static Element getElement( Element root, String name )
    {
        if ( root != null )
        {
            List list = root.getChildren( name );
            if ( !list.isEmpty() )
            {
                return (Element) list.get( 0 );
            }
        }

        return null;
    }

    /**
     * Return the child elements of an element.
     */
    public static Element[] getElements( Element root )
    {
        if ( root != null )
        {
            List list = root.getChildren();
            return (Element[]) list.toArray( new Element[list.size()] );
        }
        else
        {
            return new Element[0];
        }
    }

    @SuppressWarnings({"unchecked"})
    public static List<Element> getChildren( final Element parentEl, final String name )
    {
        final List list = parentEl.getChildren( name );
        if ( list == null )
        {
            return new ArrayList<Element>();
        }
        return list;
    }

    /**
     * Return the element text.
     */
    public static String getElementText( Element root )
    {
        if ( root != null )
        {
            String text = root.getText();
            if ( "".equals( text ) )
            {
                return null;
            }
            else
            {
                return root.getText();
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Create element and adds it to parent.
     */
    private static Element createElement( Element parent, String name )
    {
        Element elem = new Element( name );
        parent.addContent( elem );
        return elem;
    }

    /**
     * Create element and adds it to parent.
     */
    public static Element createElement( Element parent, String name, String text )
    {
        Element elem = createElement( parent, name );
        elem.setText( text );
        return elem;
    }

    public static String printDocument( Document doc )
    {
        StringWriter sw = new StringWriter();
        XMLOutputter outputter = new XMLOutputter();
        try
        {
            outputter.output( doc, sw );
            return sw.getBuffer().toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to print document", e );
        }
    }

    public static String printElement( Element element )
    {
        StringWriter sw = new StringWriter();
        XMLOutputter outputter = new XMLOutputter();
        try
        {
            outputter.output( element, sw );
            return sw.getBuffer().toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to print element", e );
        }
    }

    public static String prettyPrintDocument( Document doc, String indent )
    {
        return prettyPrintDocument( doc, indent, false );
    }

    public static String prettyPrintDocument( Document doc )
    {
        return prettyPrintDocument( doc, "  ", false );
    }

    public static String serialize( Document doc, int indent, boolean omitDecl )
    {
        return serialize( doc, indent, omitDecl, true );
    }

    public static String serializeChildren( Document doc, int indent )
    {
        return serialize( doc, indent, true, false );
    }

    private static String serialize( Document doc, int indent, boolean omitDecl, boolean includeSelf )
    {
        final Format format =
            Format.getPrettyFormat().setIndent( Strings.repeat( " ", indent ) ).setOmitDeclaration( omitDecl ).setTextMode(
                Format.TextMode.PRESERVE );
        return doSerialize( doc, format, includeSelf );
    }


    private static String doSerialize( Document doc, Format format, boolean includeSelf )
    {
        final XMLOutputter out = new XMLOutputter( format );

        if ( includeSelf )
        {
            return out.outputString( doc );
        }
        else
        {
            return out.outputString( doc.getRootElement().getChildren() );
        }
    }

    public static String prettyPrintDocument( Document doc, String indent, boolean sortAttributes )
    {
        StringWriter sw = new StringWriter();
        Format format = Format.getPrettyFormat();
        format.setIndent( indent );
        format.setOmitDeclaration( false );
        ExtXMLOutputter outputter = new ExtXMLOutputter( format, sortAttributes );
        try
        {
            outputter.output( doc, sw );
            return sw.getBuffer().toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to print document", e );
        }
    }

    public static String evaluateSingleXPathValueAsString( String xpath, Document doc )
    {
        try
        {
            XPathEvaluator xpathEvaluator = new XPathEvaluator();
            XPathExpression expr = xpathEvaluator.createExpression( xpath );

            final JDOMSource docAsDomSource = new JDOMSource( doc );

            List nodes = expr.evaluate( docAsDomSource );
            if ( nodes.size() > 1 )
            {
                throw new IllegalArgumentException( "Did not expected more than one value at xpath" );
            }

            Object valueAsObject = expr.evaluateSingle( docAsDomSource );

            String valueAsString;

            if ( valueAsObject == null )
            {
                return null;
            }
            else if ( valueAsObject instanceof NodeInfo )
            {
                NodeInfo valueAsNodeInfo = (NodeInfo) valueAsObject;
                valueAsString = valueAsNodeInfo.getStringValue();
            }
            else
            {
                valueAsString = String.valueOf( valueAsObject );
            }

            return valueAsString;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private static SAXBuilder getSAXBuilder()
    {
        return threadLocalSAXBuilder.get();
    }

    /**
     * Extended xml outputter.
     */
    private final static class ExtXMLOutputter
        extends XMLOutputter
    {
        /**
         * Sort attributes.
         */
        private final boolean sortAttributes;

        /**
         * Construct the outputter.
         */
        public ExtXMLOutputter( Format format, boolean sortAttributes )
        {
            super( format );
            this.sortAttributes = sortAttributes;
        }

        /**
         * Process the attributes.
         */
        private List<Attribute> processAttributes( List<Attribute> attributes )
        {
            if ( this.sortAttributes )
            {
                ArrayList<Attribute> tmpList = new ArrayList<Attribute>( attributes );
                Collections.sort( tmpList, new Comparator<Attribute>()
                {
                    public int compare( Attribute a1, Attribute a2 )
                    {
                        return a1.getName().compareTo( a2.getName() );
                    }
                } );

                return tmpList;
            }

            return attributes;
        }

        /**
         * Print the attributes.
         */
        protected void printAttributes( Writer out, List attributes, Element parent, XMLOutputter.NamespaceStack namespaces )
            throws IOException
        {
            super.printAttributes( out, processAttributes( attributes ), parent, namespaces );
        }
    }
}

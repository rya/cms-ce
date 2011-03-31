/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.junit.Assert;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;

import com.enonic.cms.framework.util.JDOMUtil;

/**
 * Mar 5, 2010
 */
public class AssertTool
{
    public static void assertSingleXPathValueEquals( String xpathString, Document doc, String singleExpectedValue )
    {
        String actualValue = JDOMUtil.evaluateSingleXPathValueAsString( xpathString, doc );
        Assert.assertEquals( xpathString, singleExpectedValue, actualValue );
    }

    public static void assertXPathExist( String xpathString, Document doc )
    {
        Assert.assertTrue( "xpath '" + xpathString + "' does not exist in document: \n" + JDOMUtil.prettyPrintDocument( doc, "   " ),
                           xpathExists( xpathString, doc ) );
    }

    public static void assertXPathNotExist( String xpathString, Document doc )
    {
        Assert.assertTrue( "xpath '" + xpathString + "' does exist in document: \n" + JDOMUtil.prettyPrintDocument( doc, "   " ),
                           !xpathExists( xpathString, doc ) );
    }

    private static boolean xpathExists( String xpathString, Document doc )
    {
        try
        {
            List nodes = XPath.selectNodes( doc.getRootElement(), xpathString );
            return nodes.size() > 0;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    public static void assertXPathEquals( String xpathString, Document doc, Object... expectedValues )
    {
        try
        {
            XPathEvaluator xpathEvaluator = new XPathEvaluator();
            XPathExpression expr = xpathEvaluator.createExpression( xpathString );

            final JDOMSource docAsDomSource = new JDOMSource( doc );

            List nodes = expr.evaluate( docAsDomSource );

            if ( nodes.size() != expectedValues.length )
            {
                org.junit.Assert.fail( "expected " + expectedValues.length + " values at xpath: " + xpathString );
            }

            String[] actualValues = new String[nodes.size()];
            for ( int i = 0; i < expectedValues.length; i++ )
            {
                Object node = nodes.get( i );
                if ( node instanceof NodeInfo )
                {
                    NodeInfo nodeInfo = (NodeInfo) node;
                    actualValues[i] = nodeInfo.getStringValue();
                }
                else
                {
                    actualValues[i] = String.valueOf( node );
                }
            }

            String[] expectedValuesAsString = new String[expectedValues.length];
            for ( int i = 0; i < expectedValues.length; i++ )
            {
                expectedValuesAsString[i] = String.valueOf( expectedValues[i] );
            }

            org.junit.Assert.assertArrayEquals( expectedValuesAsString, actualValues );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public static void assertSet( Set expectedSet, Set actualSet )
    {
        org.junit.Assert.assertEquals( "sets do not have same size", expectedSet.size(), actualSet.size() );
        for ( Object expectedItem : expectedSet )
        {
            org.junit.Assert.assertTrue( "actual set " + printIterable( actualSet ) + " does not contain: " + expectedItem,
                                         actualSet.contains( expectedItem ) );
        }
    }

    private static String printIterable( Iterable it )
    {
        StringBuffer s = new StringBuffer();
        s.append( "<" );
        Iterator iterator = it.iterator();
        while ( iterator.hasNext() )
        {
            Object o = iterator.next();
            s.append( o );
            if ( iterator.hasNext() )
            {
                s.append( "," );
            }
        }
        s.append( ">" );
        return s.toString();
    }
}

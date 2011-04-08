/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.processor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.portal.datasource.methodcall.MethodCall;
import com.enonic.cms.portal.datasource.methodcall.MethodCallParameter;

import com.enonic.cms.domain.CalendarUtil;

/**
 * Apr 30, 2009
 */
public class ContentProcessor
    implements DataSourceProcessor
{
    private Document contentDoc;

    public ContentProcessor( Document contentDoc )
    {
        this.contentDoc = contentDoc;
    }

    public void postProcess( Document resultDoc, MethodCall methodCall )
    {
        String methodName = methodCall.getMethodName();
        if ( "getContent".equals( methodName ) || "getPageContent".equals( methodName ) )
        {
            MethodCallParameter[] parameters = methodCall.getParameters();
            if ( !"false".equals( parameters[1].getOverride() ) )
            {
                XMLTool.removeChildNodes( resultDoc );
                resultDoc.appendChild( resultDoc.importNode( contentDoc.getDocumentElement(), true ) );
                Element root = resultDoc.getDocumentElement();
                root.setAttribute( "totalcount", "1" );
                Element contentElem = XMLTool.getFirstElement( root );
                int childrenLevel = getChildrenLevel( parameters );
                if ( childrenLevel < 1 )
                {
                    Element relatedcontentsElem = XMLTool.getElement( contentElem, "relatedcontents" );
                    XMLTool.removeChildNodes( relatedcontentsElem, false );
                }

                String publishFrom = contentElem.getAttribute( "publishfrom" );
                if ( publishFrom == null || publishFrom.length() == 0 )
                {
                    publishFrom = CalendarUtil.formatCurrentDate();
                    contentElem.setAttribute( "publishfrom", publishFrom );
                }
            }
        }
    }

    public void preProcess( MethodCall methodCall )
    {
    }

    private int getChildrenLevel( MethodCallParameter[] parameters )
    {
        if ( parameters.length == 9 )
        {
            return (Integer) parameters[7].getArgument();
        }
        else
        {
            return (Integer) parameters[3].getArgument();
        }
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import org.jdom.Content;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLBuilder;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserXmlCreator;

/**
 * Created by rmy - Date: Aug 24, 2009
 */
public class ResolverInputXMLCreator
{
    protected ResolverHttpRequestInputCreator resolverHttpRequestInputCreator;

    protected ResolverHttpRequestInputXMLCreator resolverHttpRequestInputXMLCreator;

    protected final static String ROOT_ELEMENT_NAME = "context";


    public XMLDocument buildResolverInputXML( ResolverContext context )
    {
        XMLBuilder xmlDoc = new XMLBuilder();

        xmlDoc.startElement( ROOT_ELEMENT_NAME );

        addHttpRequestInput( context, xmlDoc );

        addUserInput( context, xmlDoc );

        xmlDoc.endElement();

        return xmlDoc.getDocument();
    }

    private void addHttpRequestInput( ResolverContext context, XMLBuilder xmlDoc )
    {
        xmlDoc.importElement( getRequestXMLInput( context ) );
    }

    private XMLDocument getRequestXMLInput( ResolverContext context )
    {
        ResolverHttpRequestInput httpRequestInput = resolverHttpRequestInputCreator.createResolverHttpRequestInput( context.getRequest() );

        return resolverHttpRequestInputXMLCreator.buildResolverInputXML( httpRequestInput );
    }

    private void addUserInput( ResolverContext context, XMLBuilder xmlDoc )
    {
        xmlDoc.getCurrentElement().addContent( getUserXMLAsContent( context ) );
    }

    private Content getUserXMLAsContent( ResolverContext context )
    {
        UserXmlCreator userXmlCreator = new UserXmlCreator();
        userXmlCreator.setAdminConsoleStyle( false );
        userXmlCreator.setIncludeUserFields( true );
        userXmlCreator.wrappUserFieldsInBlockElement( false );

        UserEntity user = context.getUser();

        Document userXMLInput;

        if ( user == null )
        {
            userXMLInput = userXmlCreator.createEmptyUserDocument();
        }
        else
        {
            userXMLInput = userXmlCreator.createUserDocument( user, false, false );
        }

        return userXMLInput.getRootElement().detach();
    }

    public void setResolverHttpRequestInputCreator( ResolverHttpRequestInputCreator resolverHttpRequestInputCreator )
    {
        this.resolverHttpRequestInputCreator = resolverHttpRequestInputCreator;
    }

    @Autowired
    public void setResolverHttpRequestInputXMLCreator( ResolverHttpRequestInputXMLCreator resolverHttpRequestInputXMLCreator )
    {
        this.resolverHttpRequestInputXMLCreator = resolverHttpRequestInputXMLCreator;
    }

    @Autowired
    public void setResolverInputCreator( ResolverHttpRequestInputCreator resolverHttpRequestInputCreator )
    {
        this.resolverHttpRequestInputCreator = resolverHttpRequestInputCreator;
    }

}

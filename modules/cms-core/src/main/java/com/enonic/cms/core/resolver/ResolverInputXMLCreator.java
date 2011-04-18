/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import javax.inject.Inject;

import org.jdom.Content;
import org.jdom.Document;

import com.enonic.cms.framework.xml.XMLBuilder;

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


    public Document buildResolverInputXML( ResolverContext context )
    {
        XMLBuilder builder = new XMLBuilder();

        builder.startElement( ROOT_ELEMENT_NAME );

        addHttpRequestInput( context, builder );

        addUserInput( context, builder );

        builder.endElement();

        return builder.getDocument();
    }

    private void addHttpRequestInput( ResolverContext context, XMLBuilder xmlDoc )
    {
        xmlDoc.importElement( getRequestXMLInput( context ) );
    }

    private Document getRequestXMLInput( ResolverContext context )
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

    @Inject
    public void setResolverHttpRequestInputXMLCreator( ResolverHttpRequestInputXMLCreator resolverHttpRequestInputXMLCreator )
    {
        this.resolverHttpRequestInputXMLCreator = resolverHttpRequestInputXMLCreator;
    }

    @Inject
    public void setResolverInputCreator( ResolverHttpRequestInputCreator resolverHttpRequestInputCreator )
    {
        this.resolverHttpRequestInputCreator = resolverHttpRequestInputCreator;
    }

}

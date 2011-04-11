/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.rmi.RemoteException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;

import com.enonic.cms.core.DeploymentPathResolver;
import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.security.user.User;

public class PollHandlerController
    extends ContentHandlerBaseController
{
    private static final Logger LOG = LoggerFactory.getLogger( PollHandlerController.class.getName() );

    // error codes
    public final static int ERR_UNKNOWN_POLL_SELECTION = 100;

    public PollHandlerController()
    {
        super();
    }

    @Override
    protected void buildContentTypeXML( UserServicesService userServices, Element contentdataElem, ExtendedMap formItems,
                                        boolean skipElements )
        throws VerticalUserServicesException
    {
    }

    protected void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalUpdateException, VerticalSecurityException, RemoteException
    {

        int contentKey = formItems.getInt( "key" );
        User user = securityService.getOldUserObject();
        String xml = userServices.getContent( user, contentKey, true, 0, 0, 0 );

        Document doc = XMLTool.domparse( xml, "contents" );
        Element contentsElement = doc.getDocumentElement();
        Element contentElement = XMLTool.getElement( contentsElement, "content" );
        Element contentDataElement = XMLTool.getElement( contentElement, "contentdata" );
        Element alternativesElement = XMLTool.getElement( contentDataElement, "alternatives" );

        // Find out if the user has already polled:
        String tmp = "poll" + String.valueOf( contentKey );
        Cookie cookie = CookieUtil.getCookie( request, tmp );
        if ( cookie != null && cookie.getValue().equals( "done" ) )
        {
            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "status", "alreadyanswered" );
            redirectToPage( request, response, formItems, queryParams );
            return;
        }

        boolean voted = false;

        String multipleChoiceStr = alternativesElement.getAttribute( "multiplechoice" );
        boolean multipleChoice = ( "yes".equals( multipleChoiceStr ) );
        if ( !multipleChoice )
        {
            String selected = formItems.getString( "choice" );
            LOG.info( StringUtil.expandString( "the selection was: %0", selected, null ) );

            Map alternativesMap = XMLTool.filterElementsWithAttributeAsKey( alternativesElement.getChildNodes(), "id" );
            Element alternativeElem = (Element) alternativesMap.get( selected );
            if ( alternativeElem != null )
            {
                tmp = alternativeElem.getAttribute( "count" );
                if ( tmp.length() > 0 )
                {
                    alternativeElem.setAttribute( "count", String.valueOf( Integer.parseInt( tmp ) + 1 ) );
                }
                else
                {
                    alternativeElem.setAttribute( "count", String.valueOf( 1 ) );
                }
                voted = true;
            }
            else
            {
                redirectToErrorPage( request, response, formItems, ERR_UNKNOWN_POLL_SELECTION, null );
                return;
            }
        }
        else
        {
            Element[] alternatives = XMLTool.getElements( alternativesElement );
            for ( int i = 0; i < alternatives.length; i++ )
            {
                String id = alternatives[i].getAttribute( "id" );
                if ( String.valueOf( i ).equals( formItems.get( "poll" + id, null ) ) )
                {
                    tmp = alternatives[i].getAttribute( "count" );
                    if ( tmp.length() > 0 )
                    {
                        alternatives[i].setAttribute( "count", String.valueOf( Integer.parseInt( tmp ) + 1 ) );
                    }
                    else
                    {
                        alternatives[i].setAttribute( "count", "1" );
                    }
                    voted = true;
                }
            }
        }

        if ( voted )
        {
            // Increment the user counter:
            tmp = alternativesElement.getAttribute( "count" );
            if ( tmp.length() > 0 )
            {
                alternativesElement.setAttribute( "count", String.valueOf( Integer.parseInt( tmp ) + 1 ) );
            }
            else
            {
                alternativesElement.setAttribute( "count", "1" );
            }

            // Update the poll:
            Document newdoc = XMLTool.createDocument();
            newdoc.appendChild( newdoc.importNode( contentElement, true ) );
            String xmlData = XMLTool.documentToString( newdoc );
            updateContent( user, xmlData, null, null, false );

            // Set cookie to prevent user from polling a second time:
            String deploymentPath = DeploymentPathResolver.getSiteDeploymentPath( request );
            CookieUtil.setCookie( response, "poll" + String.valueOf( contentKey ), "done", SECONDS_IN_WEEK, deploymentPath );
        }

        // redirect
        redirectToPage( request, response, formItems );
    }
}
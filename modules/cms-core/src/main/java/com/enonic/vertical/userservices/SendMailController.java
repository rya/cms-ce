/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.enonic.esl.ESLException;
import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.Mail;
import com.enonic.esl.util.StringUtil;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.domain.SiteKey;

@Controller
@RequestMapping(value = "/site/**/_services/mail")
public class SendMailController
    extends ContentHandlerBaseController
{
    private static final Logger LOG = LoggerFactory.getLogger( SendMailController.class.getName() );


    public final static int ERR_RECIPIENT_HAS_NO_EMAIL_ADDRESS = 100;

    public final static int ERR_RECIPIENT_HAS_WRONG_ADDRESS_NO_ALPHA = 101;

    public final static int ERR_RECIPIENT_HAS_WRONG_ADDRESS_MISSING_DOT = 102;

    public final static int ERR_MISSING_FROM_FIELDS = 103;

    public final static int ERR_MISSING_TO_FIELD = 104;

    public final static int ERR_MISSING_SUBJECT_FIELD = 105;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public void send( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {
        if ( operation.equals( "send" ) )
        {
            try
            {

                Mail mail = new Mail();
                mail.setSMTPHost( verticalProperties.getSMTPHost() );

                // from field
                String fromName = formItems.getString( "from_name", "" );
                String fromEmail = formItems.getString( "from_email", "" );
                if ( StringUtils.isEmpty( fromName ) && StringUtils.isEmpty( fromEmail ) )
                {
                    String message = "No \"from\" fields given. " + "At least one of \"from_name\" and \"from_email\" is required.";
                    LOG.warn( StringUtil.expandString( message, null, null ) );
                    redirectToErrorPage( request, response, formItems, ERR_MISSING_FROM_FIELDS, null );
                    return;
                }
                mail.setFrom( fromName, fromEmail );

                // to field
                String[] recipients = formItems.getStringArray( "to" );
                if ( recipients.length == 0 )
                {
                    String message = "No \"to\" fields given. At least one is required.";
                    LOG.warn( StringUtil.expandString( message, null, null ) );
                    redirectToErrorPage( request, response, formItems, ERR_MISSING_TO_FIELD, null );
                    return;
                }
                else
                {
                    int error = addRecipients( mail, recipients, Mail.TO_RECIPIENT );
                    if ( error >= 0 )
                    {
                        redirectToErrorPage( request, response, formItems, error, null );
                        return;
                    }
                }

                // bcc field
                recipients = formItems.getStringArray( "bcc" );
                if ( recipients.length > 0 )
                {
                    int error = addRecipients( mail, recipients, Mail.BCC_RECIPIENT );
                    if ( error >= 0 )
                    {
                        redirectToErrorPage( request, response, formItems, error, null );
                        return;
                    }
                }

                // cc field
                recipients = formItems.getStringArray( "cc" );
                if ( recipients.length > 0 )
                {
                    int error = addRecipients( mail, recipients, Mail.CC_RECIPIENT );
                    if ( error >= 0 )
                    {
                        redirectToErrorPage( request, response, formItems, error, null );
                        return;
                    }
                }

                // subject
                String subject = formItems.getString( "subject" );
                if ( subject == null || subject.length() == 0 )
                {
                    String message = "No \"subject\" field given. A subject field is required.";
                    LOG.warn( StringUtil.expandString( message, null, null ) );
                    redirectToErrorPage( request, response, formItems, ERR_MISSING_SUBJECT_FIELD, null );
                    return;
                }
                else
                {
                    mail.setSubject( subject );
                }

                // body
                StringBuffer body = new StringBuffer( 40 * formItems.size() );
                String sortOrder = formItems.getString( "sort_order" );
                if ( sortOrder != null && sortOrder.length() > 0 )
                {
                    StringTokenizer st = new StringTokenizer( sortOrder, "," );
                    while ( st.hasMoreTokens() )
                    {
                        String key = st.nextToken();
                        if ( formItems.containsKey( key ) )
                        {
                            Object value = formItems.get( key );
                            if ( value instanceof String )
                            {
                                body.append( key );
                                body.append( ": " );
                                if ( value.toString().indexOf( '\n' ) >= 0 )
                                {
                                    body.append( '\n' );
                                }
                                body.append( value );
                                body.append( '\n' );
                            }
                            else if ( value instanceof String[] )
                            {
                                String[] values = (String[]) value;
                                for ( int i = 0; i < values.length; i++ )
                                {
                                    body.append( key );
                                    body.append( ": " );
                                    if ( values[i].indexOf( '\n' ) >= 0 )
                                    {
                                        body.append( '\n' );
                                    }
                                    body.append( values[i] );
                                    body.append( '\n' );
                                }
                            }
                            else if ( value instanceof Boolean )
                            {
                                body.append( key );
                                body.append( ": " );
                                if ( value.toString().indexOf( '\n' ) >= 0 )
                                {
                                    body.append( '\n' );
                                }
                                body.append( value );
                                body.append( '\n' );
                            }
                        }
                    }
                }
                mail.setMessage( body.toString() );

                // attachments?
                if ( formItems.hasFileItems() )
                {
                    FileItem[] fileItems = formItems.getFileItems();
                    for ( int i = 0; i < fileItems.length; i++ )
                    {
                        mail.addAttachment( fileItems[i] );
                    }
                }

                mail.send();

                redirectToPage( request, response, formItems );
            }
            catch ( ESLException esle )
            {
                String message = "Failed to send email: %t";
                LOG.error( StringUtil.expandString( message, (Object) null, esle ), esle );
                redirectToErrorPage( request, response, formItems, ERR_EMAIL_SEND_FAILED, null );
            }
        }
        else
        {
            super.handlerCustom( request, response, session, formItems, userServices, siteKey, operation );
        }
    }

    private int addRecipients( Mail mail, String[] recipients, short type )
    {

        for ( int i = 0; i < recipients.length; i++ )
        {

            // skip empty recipients when recipient type is bcc or cc
            if ( ( type == Mail.BCC_RECIPIENT || type == Mail.CC_RECIPIENT ) && recipients[i].trim().length() == 0 )
            {
                continue;
            }

            String name, email;

            int scIdx = recipients[i].indexOf( ';' );
            if ( scIdx >= 0 )
            {
                name = recipients[i].substring( 0, scIdx );
                email = recipients[i].substring( scIdx + 1 );
            }
            else
            {
                name = null;
                email = recipients[i];
            }

            // simple validation of email address:
            // 1. cannot be null
            // 2. must include an '@' and at least one '.' after the '@'
            if ( email == null || email.trim().length() == 0 )
            {
                String message = "%0 email address not given.";
                String addressType = null;
                switch ( type )
                {
                    case Mail.TO_RECIPIENT:
                        addressType = "To";
                        break;
                    case Mail.BCC_RECIPIENT:
                        addressType = "Bcc";
                        break;
                    case Mail.CC_RECIPIENT:
                        addressType = "Cc";
                        break;
                }
                LOG.warn( StringUtil.expandString( message, addressType, null ) );
                return ERR_RECIPIENT_HAS_NO_EMAIL_ADDRESS;
            }
            else
            {
                int idx = email.indexOf( '@' );
                if ( idx <= 0 )
                {
                    String message = "%0 email address in wrong format. Does not include an '@': %1";
                    Object[] ojbs = new Object[]{null, email};
                    switch ( type )
                    {
                        case Mail.TO_RECIPIENT:
                            ojbs[0] = "To";
                            break;
                        case Mail.BCC_RECIPIENT:
                            ojbs[0] = "Bcc";
                            break;
                        case Mail.CC_RECIPIENT:
                            ojbs[0] = "Cc";
                            break;
                    }
                    LOG.warn( StringUtil.expandString( message, ojbs, null ) );
                    return ERR_RECIPIENT_HAS_WRONG_ADDRESS_NO_ALPHA;
                }
                else if ( email.indexOf( '.', idx ) < 0 )
                {
                    String message = "%0 email address in wrong format. Does not include at least one '.': %1";
                    Object[] ojbs = new Object[]{null, email};
                    switch ( type )
                    {
                        case Mail.TO_RECIPIENT:
                            ojbs[0] = "To";
                            break;
                        case Mail.BCC_RECIPIENT:
                            ojbs[0] = "Bcc";
                            break;
                        case Mail.CC_RECIPIENT:
                            ojbs[0] = "Cc";
                            break;
                    }
                    LOG.warn( StringUtil.expandString( message, ojbs, null ) );
                    return ERR_RECIPIENT_HAS_WRONG_ADDRESS_MISSING_DOT;
                }

                mail.addRecipient( name, email, type );
            }
        }

        return -1;
    }
}

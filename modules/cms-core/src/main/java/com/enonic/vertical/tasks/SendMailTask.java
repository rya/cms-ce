/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.tasks;

import java.util.Properties;

import com.enonic.esl.net.Mail;
import com.enonic.esl.util.StringUtil;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.api.Work;

public class SendMailTask
    implements Work
{

    public void execute( Properties props )
        throws Exception
    {
        String fromName = props.getProperty( "from_name" );
        String fromEmail = props.getProperty( "from_email" );
        String subject = props.getProperty( "subject" );
        boolean htmlMail = Boolean.valueOf( props.getProperty( "htmlmail", "false" ) ).booleanValue();
        String mailBody = props.getProperty( "body" );

        String[] recipients = StringUtil.splitString( props.getProperty( "to" ), "," );
        String[] ccRecipients = StringUtil.splitString( props.getProperty( "cc" ), "," );
        String[] bccRecipients = StringUtil.splitString( props.getProperty( "bcc" ), "," );

        Mail mail = new Mail();
        mail.setSMTPHost( VerticalProperties.getVerticalProperties().getSMTPHost() );
        mail.setFrom( fromName, fromEmail );
        mail.setSubject( subject );
        mail.setMessage( mailBody, htmlMail );

        for ( int i = 0; i < recipients.length; i++ )
        {
            mail.addRecipient( null, recipients[i], Mail.TO_RECIPIENT );
        }
        for ( int i = 0; i < ccRecipients.length; i++ )
        {
            mail.addRecipient( null, ccRecipients[i], Mail.CC_RECIPIENT );
        }
        for ( int i = 0; i < bccRecipients.length; i++ )
        {
            mail.addRecipient( null, bccRecipients[i], Mail.BCC_RECIPIENT );
        }

        mail.send();
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.net;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.esl.ESLException;
import com.enonic.esl.activation.ByteArrayDataSource;
import com.enonic.esl.activation.FileItemDataSource;
import com.enonic.esl.util.RegexpUtil;

public class Mail
{
    /**
     * Logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger( Mail.class );

    final public static short TO_RECIPIENT = 0;

    final public static short CC_RECIPIENT = 1;

    final public static short BCC_RECIPIENT = 2;

    private String from_name;

    private String from_email;

    private String subject;

    private String message;

    private String htmlMessage;

    ArrayList<String[]> bcc = new ArrayList<String[]>();

    ArrayList<String[]> cc = new ArrayList<String[]>();

    ArrayList<String[]> to = new ArrayList<String[]>();

    private final static String DEFAULT_SMTPHOST = "localhost";

    private String smtpHost;

    private String ENCODING = "UTF-8";

    private ArrayList<Object> attachments = new ArrayList<Object>();

    /**
     * Mail constructor comment.
     */
    public Mail()
    {
        super();
    }

    /**
     * Get the hostname used as the mail server. Outgoing mail is sent using the SMTP server at port 25 on this host.
     */
    public String getSMTPHost()
    {
        return smtpHost;
    }

    /**
     * @deprecated Use {@link #addRecipient(String, String, short)} instead.
     */
    public void addBcc( String name, String email )
    {
        bcc.add( new String[]{name, email} );
    }

    /**
     * <p/> Send the mail. The SMTP host is contacted and the mail is sent according to the parameters set. </p> <p/> If it fails, it is
     * considered a runtime exception. Note that this doesn't make it very failsafe, so care should be taken when one wants fault tolerance.
     * One solution could be to catch the exception thrown. Another solution could be to use the JavaMail API directly. </p>
     */
    public void send()
            throws ESLException
    {
        // smtp server
        Properties smtpProperties = new Properties();
        if ( smtpHost != null )
        {
            smtpProperties.put( "mail.smtp.host", smtpHost );
            System.setProperty( "mail.smtp.host", smtpHost );
        }
        else
        {
            smtpProperties.put( "mail.smtp.host", DEFAULT_SMTPHOST );
            System.setProperty( "mail.smtp.host", DEFAULT_SMTPHOST );
        }
        Session session = Session.getDefaultInstance( smtpProperties, null );

        try
        {
            // create message
            Message msg = new MimeMessage( session );
            // set from address
            InternetAddress addressFrom = new InternetAddress();
            if ( from_email != null )
            {
                addressFrom.setAddress( from_email );
            }
            if ( from_name != null )
            {
                addressFrom.setPersonal( from_name, ENCODING );
            }
            ( (MimeMessage) msg ).setFrom( addressFrom );

            if ( ( to.size() == 0 && bcc.size() == 0 ) || subject == null || ( message == null && htmlMessage == null ) )
            {
                LOG.error( "Missing data. Unable to send mail." );
                throw new ESLException( "Missing data. Unable to send mail." );
            }

            // set to:
            for ( int i = 0; i < to.size(); ++i )
            {
                String[] recipient = to.get( i );
                InternetAddress addressTo = new InternetAddress( recipient[1] );
                if ( recipient[0] != null )
                {
                    addressTo.setPersonal( recipient[0], ENCODING );
                }
                ( (MimeMessage) msg ).addRecipient( Message.RecipientType.TO, addressTo );
            }

            // set bcc:
            for ( int i = 0; i < bcc.size(); ++i )
            {
                String[] recipient = bcc.get( i );
                InternetAddress addressTo = null;
                try
                {
                    addressTo = new InternetAddress( recipient[1] );
                }
                catch ( Exception e )
                {
                    System.err.println( "exception on address: " + recipient[1] );
                    continue;
                }
                if ( recipient[0] != null )
                {
                    addressTo.setPersonal( recipient[0], ENCODING );
                }
                ( (MimeMessage) msg ).addRecipient( Message.RecipientType.BCC, addressTo );
            }

            // set cc:
            for ( int i = 0; i < cc.size(); ++i )
            {
                String[] recipient = cc.get( i );
                InternetAddress addressTo = new InternetAddress( recipient[1] );
                if ( recipient[0] != null )
                {
                    addressTo.setPersonal( recipient[0], ENCODING );
                }
                ( (MimeMessage) msg ).addRecipient( Message.RecipientType.CC, addressTo );
            }

            // Setting subject and content type
            ( (MimeMessage) msg ).setSubject( subject, ENCODING );

            if ( message != null )
            {
                message = RegexpUtil.substituteAll( "\\\\n", "\n", message );
            }

            // if there are any attachments, treat this as a multipart message.
            if ( attachments.size() > 0 )
            {
                BodyPart messageBodyPart = new MimeBodyPart();
                if ( message != null )
                {
                    ( (MimeBodyPart) messageBodyPart ).setText( message, ENCODING );
                }
                else
                {
                    DataHandler dataHandler = new DataHandler( new ByteArrayDataSource( htmlMessage, "text/html", ENCODING ) );
                    ( (MimeBodyPart) messageBodyPart ).setDataHandler( dataHandler );
                }
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart( messageBodyPart );

                // add all attachments
                for ( int i = 0; i < attachments.size(); ++i )
                {
                    Object obj = attachments.get( i );
                    if ( obj instanceof String )
                    {
                        System.err.println( "attachment is String" );
                        messageBodyPart = new MimeBodyPart();
                        ( (MimeBodyPart) messageBodyPart ).setText( (String) obj, ENCODING );
                        multipart.addBodyPart( messageBodyPart );
                    }
                    else if ( obj instanceof File )
                    {
                        messageBodyPart = new MimeBodyPart();
                        FileDataSource fds = new FileDataSource( (File) obj );
                        messageBodyPart.setDataHandler( new DataHandler( fds ) );
                        messageBodyPart.setFileName( fds.getName() );
                        multipart.addBodyPart( messageBodyPart );
                    }
                    else if ( obj instanceof FileItem )
                    {
                        FileItem fileItem = (FileItem) obj;
                        messageBodyPart = new MimeBodyPart();
                        FileItemDataSource fds = new FileItemDataSource( fileItem );
                        messageBodyPart.setDataHandler( new DataHandler( fds ) );
                        messageBodyPart.setFileName( fds.getName() );
                        multipart.addBodyPart( messageBodyPart );
                    }
                    else
                    {
                        // byte array
                        messageBodyPart = new MimeBodyPart();
                        ByteArrayDataSource bads = new ByteArrayDataSource( (byte[]) obj, "text/html", ENCODING );
                        messageBodyPart.setDataHandler( new DataHandler( bads ) );
                        messageBodyPart.setFileName( bads.getName() );
                        multipart.addBodyPart( messageBodyPart );
                    }
                }

                msg.setContent( multipart );
            }
            else
            {
                if ( message != null )
                {
                    ( (MimeMessage) msg ).setText( message, ENCODING );
                }
                else
                {
                    DataHandler dataHandler = new DataHandler( new ByteArrayDataSource( htmlMessage, "text/html", ENCODING ) );
                    ( (MimeMessage) msg ).setDataHandler( dataHandler );
                }
            }

            // send message
            Transport.send( msg );
        }
        catch ( AddressException e )
        {
            String MESSAGE_30 = "Error in email address: " + e.getMessage();
            LOG.warn( MESSAGE_30 );
            throw new ESLException( MESSAGE_30, e );
        }
        catch ( UnsupportedEncodingException e )
        {
            String MESSAGE_40 = "Unsupported encoding: " + e.getMessage();
            LOG.error( MESSAGE_40, e );
            throw new ESLException( MESSAGE_40, e );
        }
        catch ( SendFailedException sfe )
        {
            Throwable t = null;
            Exception e = sfe.getNextException();
            while ( e != null )
            {
                t = e;
                if ( t instanceof SendFailedException )
                {
                    e = ( (SendFailedException) e ).getNextException();
                }
                else
                {
                    e = null;
                }
            }
            if ( t != null )
            {
                String MESSAGE_50 = "Error sending mail: " + t.getMessage();
                throw new ESLException( MESSAGE_50, t );
            }
            else
            {
                String MESSAGE_50 = "Error sending mail: " + sfe.getMessage();
                throw new ESLException( MESSAGE_50, sfe );
            }
        }
        catch ( MessagingException e )
        {
            String MESSAGE_50 = "Error sending mail: " + e.getMessage();
            LOG.error( MESSAGE_50, e );
            throw new ESLException( MESSAGE_50, e );
        }
    }

    /**
     * Set the sender name and email address.
     */
    public void setFrom( String name, String email )
    {
        from_name = name;
        from_email = email;
    }

    /**
     * Set the message body that will be used in the mail. The message is plain text.
     */
    public void setMessage( String message )
    {
        setMessage( message, false );
    }

    /**
     * Set the message body that will be used in the mail. If html is <strong>true</strong>, then message must be a html message.
     */
    public void setMessage( String message, boolean html )
    {
        if ( html )
        {
            this.message = null;
            this.htmlMessage = message;
        }
        else
        {
            this.message = message;
            this.htmlMessage = null;
        }
    }

    /**
     * Get the hostname used as the mail server. Outgoing mail is sent using the SMTP server at port 25 on this host.
     */
    public void setSMTPHost( String newHost )
    {
        smtpHost = newHost;
    }

    /**
     * Set the subject of the mail.
     */
    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    /**
     * Set the recipient name and email address.
     *
     * @deprecated Use {@link #addRecipient(String, String, short)} instead.
     */
    public void setTo( String name, String email )
    {
        to.add( new String[]{name, email} );
    }

    /**
     * Add a recipient. Duh..
     */
    public void addRecipient( String name, String email, short type )
    {
        if ( type == TO_RECIPIENT )
        {
            to.add( new String[]{name, email} );
        }
        else if ( type == BCC_RECIPIENT )
        {
            bcc.add( new String[]{name, email} );
        }
        else if ( type == CC_RECIPIENT )
        {
            cc.add( new String[]{name, email} );
        }
    }

    public void clearRecipients()
    {
        to.clear();
        cc.clear();
        bcc.clear();
    }

    public void addAttachment( byte[] attch )
    {
        attachments.add( attch );
    }

    public void addAttachment( File f )
    {
        attachments.add( f );
    }

    public void addAttachment( FileItem fi )
    {
        attachments.add( fi );
    }

    public void addAttachment( String attch )
    {
        attachments.add( attch );
    }
}

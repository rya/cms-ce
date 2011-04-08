/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.util.RegexpUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.portal.ShoppingCart;
import com.enonic.cms.domain.security.user.User;

public class OrderHandlerController
    extends ContentHandlerBaseController
{
    private static final Logger LOG = LoggerFactory.getLogger( OrderHandlerController.class.getName() );

    private static final int contentTypeKey = 45;

    // error codes

    public final static int ERR_SHOPPING_CART_EMPTY = 100;

    public final static int ERR_FAILED_TO_GET_PRODUCT = 101;

    public OrderHandlerController()
    {
        super();
    }

    @Override
    protected void buildContentTypeXML( UserServicesService userServices, Element contentdataElem, ExtendedMap formItems,
                                        boolean skipElements )
        throws VerticalUserServicesException
    {

        Document doc = contentdataElem.getOwnerDocument();
        contentdataElem.setAttribute( "version", "1.0" );

        // guid
        XMLTool.createElement( doc, contentdataElem, "guid", formItems.getString( "_guid" ) );

        // Status
        XMLTool.createElement( doc, contentdataElem, "status", "Submitted" );

        // Order reference
        if ( formItems.containsKey( "order_reference" ) )
        {
            String ref = (String) formItems.get( "order_reference" );
            XMLTool.createElement( doc, contentdataElem, "orderreference", ref );
        }

        // Customer
        Element customer = XMLTool.createElement( doc, contentdataElem, "customer" );
        XMLTool.createElement( doc, customer, "firstname", formItems.getString( "customer_firstname", "" ) );
        XMLTool.createElement( doc, customer, "surname", formItems.getString( "customer_surname", "" ) );
        XMLTool.createElement( doc, customer, "company", formItems.getString( "customer_company", "" ) );
        XMLTool.createElement( doc, customer, "email", formItems.getString( "customer_email", "" ) );
        XMLTool.createElement( doc, customer, "telephone", formItems.getString( "customer_telephone", "" ) );
        XMLTool.createElement( doc, customer, "mobile", formItems.getString( "customer_mobile", "" ) );
        XMLTool.createElement( doc, customer, "fax", formItems.getString( "customer_fax", "" ) );
        if ( formItems.containsKey( "customer_refno" ) )
        {
            String ref = (String) formItems.get( "customer_refno" );
            customer.setAttribute( "refno", ref );
        }

        // Customer - billing address
        Element billingaddress = XMLTool.createElement( doc, customer, "billingaddress" );
        XMLTool.createElement( doc, billingaddress, "postaladdress", formItems.getString( "billing_postaladdress", "" ) );
        XMLTool.createElement( doc, billingaddress, "postalcode", formItems.getString( "billing_postalcode", "" ) );
        XMLTool.createElement( doc, billingaddress, "location", formItems.getString( "billing_location", "" ) );
        if ( formItems.containsKey( "billing_state" ) )
        {
            String state = (String) formItems.get( "billing_state" );
            XMLTool.createElement( doc, billingaddress, "state", state );
        }
        XMLTool.createElement( doc, billingaddress, "country", formItems.getString( "billing_country", "" ) );

        // Customer - shipping address
        Element shippingaddress = XMLTool.createElement( doc, customer, "shippingaddress" );
        XMLTool.createElement( doc, shippingaddress, "postaladdress", formItems.getString( "shipping_postaladdress", "" ) );
        XMLTool.createElement( doc, shippingaddress, "postalcode", formItems.getString( "shipping_postalcode", "" ) );
        XMLTool.createElement( doc, shippingaddress, "location", formItems.getString( "shipping_location", "" ) );
        if ( formItems.containsKey( "shipping_state" ) )
        {
            String state = (String) formItems.get( "shipping_state" );
            XMLTool.createElement( doc, shippingaddress, "state", state );
        }
        XMLTool.createElement( doc, shippingaddress, "country", formItems.getString( "shipping_country", "" ) );

        // Details
        Element details = XMLTool.createElement( doc, contentdataElem, "details" );
        if ( formItems.containsKey( "details_comments" ) )
        {
            String comments = (String) formItems.get( "details_comments" );
            XMLTool.createElement( doc, details, "comments", comments );
        }
        if ( formItems.containsKey( "details_shippingoptions" ) )
        {
            String shippingoptions = (String) formItems.get( "details_shippingoptions" );
            XMLTool.createElement( doc, details, "shippingoptions", shippingoptions );
        }

        ShoppingCart cart = (ShoppingCart) formItems.get( "_shoppingcart" );
        cart.toDoc( doc, contentdataElem, false );
    }

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, RemoteException
    {

        MultiValueMap queryParams = new MultiValueMap();

        // NB! Operations must be sorted alphabetically!
        String[] operations = new String[]{"cart_add", "cart_checkout", "cart_empty", "cart_remove", "cart_update"};
        if ( operation != null && Arrays.binarySearch( operations, operation ) >= 0 )
        {
            ShoppingCart cart = (ShoppingCart) session.getAttribute( "shoppingcart" );
            if ( cart == null )
            {
                cart = new ShoppingCart();
                session.setAttribute( "shoppingcart", cart );
            }

            try
            {
                if ( "cart_add".equals( operation ) )
                {
                    int productId = formItems.getInt( "productid" );
                    int count = formItems.getInt( "count", 1 );

                    if ( count > 0 )
                    {
                        User user = securityService.getOldUserObject();
                        String xml = userServices.getContent( user, productId, true, 0, 0, 0 );
                        if ( xml == null || xml.length() == 0 )
                        {
                            String message = "Failed to get product: %0";
                            LOG.warn( StringUtil.expandString( message, productId, null ) );
                            redirectToErrorPage( request, response, formItems, ERR_FAILED_TO_GET_PRODUCT, null );
                            return;
                        }

                        Document doc = XMLTool.domparse( xml );
                        Element root = doc.getDocumentElement();
                        if ( XMLTool.getFirstElement( root ) == null )
                        {
                            redirectToErrorPage( request, response, formItems, ERR_FAILED_TO_GET_PRODUCT, null );
                            return;
                        }

                        String productNumber = XMLTool.getElementText( doc, "/contents/content/contentdata/number" );
                        String productName = XMLTool.getElementText( doc, "/contents/content/title" );
                        String priceStr = XMLTool.getElementText( doc, "/contents/content/contentdata/price" );
                        double price;
                        if ( priceStr != null )
                        {
                            priceStr = priceStr.replace( ',', '.' );
                            price = Double.parseDouble( priceStr );
                        }
                        else
                        {
                            price = 0.0D;
                        }

                        String[] keyFilter =
                            new String[]{"count", "handler", "_handler", "op", "_op", "productid", "redirect", "_redirect"};
                        Map<String, String> customValues = new HashMap<String, String>();
                        for ( Object o : formItems.keySet() )
                        {
                            String key = (String) o;
                            if ( Arrays.binarySearch( keyFilter, key ) < 0 )
                            {
                                customValues.put( key, formItems.get( key ).toString() );
                            }
                        }

                        cart.addItem( productId, productNumber, productName, price, count, customValues );
                    }
                }
                else if ( "cart_remove".equals( operation ) )
                {
                    int productId = formItems.getInt( "productid" );
                    cart.removeItem( productId );
                }
                else if ( "cart_update".equals( operation ) )
                {
                    int productId = formItems.getInt( "productid" );
                    int count = formItems.getInt( "count", 1 );
                    if ( count > 0 )
                    {
                        cart.updateItem( productId, count );
                    }
                    else
                    {
                        cart.removeItem( productId );
                    }
                }
                else if ( "cart_checkout".equals( operation ) )
                {
                    if ( cart.isEmpty() )
                    {
                        redirectToErrorPage( request, response, formItems, ERR_SHOPPING_CART_EMPTY, null );
                        return;
                    }

                    User user = securityService.getOldUserObject();
                    String guid = new UID().toString();
                    formItems.put( "_guid", guid );
                    formItems.put( "_shoppingcart", cart );

                    // customer name
                    String customerFirstname = formItems.getString( "customer_firstname" );
                    String customerSurname = formItems.getString( "customer_surname" );
                    StringBuffer customerName = new StringBuffer( customerFirstname );
                    if ( customerName.length() > 0 )
                    {
                        customerName.append( ' ' );
                    }
                    customerName.append( customerSurname );

                    String xmlData = buildXML( userServices, user, formItems, siteKey, contentTypeKey, customerName.toString(), false );

                    ContentKey orderKey = storeNewContent( user, null, xmlData );

                    // send mail
                    // mail header
                    String[] shopManagerEmail = formItems.getStringArray( "shopmanager_email" );
                    //String shopManagerName = formItems.getString("shopmanager_name");
                    String sender_email = formItems.getString( "mail_sender_email" );
                    String sender_name = formItems.getString( "mail_sender_name" );
                    String customerEmail = formItems.getString( "customer_email" );
                    String receiver_name = customerFirstname + ' ' + customerSurname;
                    String subject = formItems.getString( "mail_subject" );
                    String message = formItems.getString( "mail_message" );

                    // order info
                    String orderId = orderKey.toString();
                    String orderDate = CalendarUtil.formatCurrentDate();
                    String orderStatus = "Submitted";
                    String orderReference = formItems.getString( "order_reference", "" );
                    String orderUrl =
                        formItems.getString( "showorderurl", "" ) + "page?id=" + formItems.getString( "showorderpage", "" ) + "&key=" +
                            orderKey.toString() + "&guid=" + guid;
                    subject = RegexpUtil.substituteAll( "\\%order_id\\%", orderId, subject );
                    message = RegexpUtil.substituteAll( "\\%order_id\\%", orderId, message );
                    message = RegexpUtil.substituteAll( "\\%order_reference\\%", orderReference, message );
                    message = RegexpUtil.substituteAll( "\\%order_date\\%", orderDate, message );
                    message = RegexpUtil.substituteAll( "\\%order_status\\%", orderStatus, message );
                    message = RegexpUtil.substituteAll( "\\%order_url\\%", orderUrl, message );

                    // customer info
                    String customerRefNo = formItems.getString( "customer_refno", "" );
                    String customerCompany = formItems.getString( "customer_company", "" );
                    String customerTelephone = formItems.getString( "customer_telephone", "" );
                    String customerMobile = formItems.getString( "customer_mobile", "" );
                    String customerFax = formItems.getString( "customer_fax", "" );
                    message = RegexpUtil.substituteAll( "\\%customer_firstname\\%", customerFirstname, message );
                    message = RegexpUtil.substituteAll( "\\%customer_surname\\%", customerSurname, message );
                    message = RegexpUtil.substituteAll( "\\%customer_email\\%", customerEmail, message );
                    message = RegexpUtil.substituteAll( "\\%customer_refno\\%", customerRefNo, message );
                    message = RegexpUtil.substituteAll( "\\%customer_company\\%", customerCompany, message );
                    message = RegexpUtil.substituteAll( "\\%customer_telephone\\%", customerTelephone, message );
                    message = RegexpUtil.substituteAll( "\\%customer_mobile\\%", customerMobile, message );
                    message = RegexpUtil.substituteAll( "\\%customer_fax\\%", customerFax, message );

                    // shipping address
                    String shippingPostalAddress = formItems.getString( "shipping_postaladdress", "" );
                    String shippingPostalCode = formItems.getString( "shipping_postalcode", "" );
                    String shippingLocation = formItems.getString( "shipping_location", "" );
                    String shippingCountry = formItems.getString( "shipping_country", "" );
                    String shippingState = formItems.getString( "shipping_state", "" );
                    message = RegexpUtil.substituteAll( "\\%shipping_postaladdress\\%", shippingPostalAddress, message );
                    message = RegexpUtil.substituteAll( "\\%shipping_postalcode\\%", shippingPostalCode, message );
                    message = RegexpUtil.substituteAll( "\\%shipping_location\\%", shippingLocation, message );
                    message = RegexpUtil.substituteAll( "\\%shipping_country\\%", shippingCountry, message );
                    message = RegexpUtil.substituteAll( "\\%shipping_state\\%", shippingState, message );

                    // billing address
                    String billingPostalAddress = formItems.getString( "billing_postaladdress", "" );
                    String billingPostalCode = formItems.getString( "billing_postalcode", "" );
                    String billingLocation = formItems.getString( "billing_location", "" );
                    String billingCountry = formItems.getString( "billing_country", "" );
                    String billingState = formItems.getString( "billing_state", "" );
                    message = RegexpUtil.substituteAll( "\\%billing_postaladdress\\%", billingPostalAddress, message );
                    message = RegexpUtil.substituteAll( "\\%billing_postalcode\\%", billingPostalCode, message );
                    message = RegexpUtil.substituteAll( "\\%billing_location\\%", billingLocation, message );
                    message = RegexpUtil.substituteAll( "\\%billing_country\\%", billingCountry, message );
                    message = RegexpUtil.substituteAll( "\\%billing_state\\%", billingState, message );

                    String regexp = "\\%details_(comments|shippingoptions)\\%";
                    String substRegexpStart = "\\%details_";
                    String substRegexpEnd = "\\%";

                    Matcher results = RegexpUtil.match( message, regexp );
                    while ( results.find() )
                    {
                        String orderDetail = "";
                        StringBuffer substRegexp = new StringBuffer( substRegexpStart );
                        substRegexp.append( results.group( 1 ) );
                        substRegexp.append( substRegexpEnd );
                        if ( "comments".equals( results.group( 1 ) ) )
                        {
                            if ( formItems.containsKey( "details_comments" ) )
                            {
                                orderDetail = (String) formItems.get( "details_comments" );
                            }
                            else
                            {
                                orderDetail = "";
                            }
                        }
                        else if ( "shippingoptions".equals( results.group( 1 ) ) )
                        {
                            if ( formItems.containsKey( "details_shippingoptions" ) )
                            {
                                orderDetail = (String) formItems.get( "details_shippingoptions" );
                            }
                            else
                            {
                                orderDetail = "";
                            }
                        }
                        message = RegexpUtil.substituteAll( substRegexp.toString(), orderDetail, message );
                    }

                    String orderItem = formItems.getString( "mail_order_item" );
                    message = cart.addItemsToMailMessage( message, orderItem );
                    message = cart.addTotalToMailMessage( message );

                    sendMail( customerEmail, receiver_name, sender_email, sender_name, subject, message );

                    if ( shopManagerEmail.length > 0 )
                    {
                        for ( String aShopManagerEmail : shopManagerEmail )
                        {
                            if ( StringUtils.isNotEmpty( aShopManagerEmail ) )
                            {
                                sendMail( aShopManagerEmail, null, sender_email, sender_name, subject, message );
                            }
                        }
                    }

                    cart.clear();

                    String showOrder = formItems.getString( "showorderonredirect", null );
                    if ( "true".equals( showOrder ) )
                    {
                        queryParams.put( "key", orderKey.toString() );
                        queryParams.put( "guid", guid );
                    }
                }
                else if ( "cart_empty".equals( operation ) )
                {
                    cart.clear();
                }

                redirectToPage( request, response, formItems, queryParams );
            }
            catch ( UnsupportedEncodingException uee )
            {
                String message = "Un-supported encoding: %t";
                LOG.error( StringUtil.expandString( message, (Object) null, uee ), uee );
                redirectToErrorPage( request, response, formItems, ERR_EMAIL_SEND_FAILED, null );
            }
            catch ( MessagingException me )
            {
                String message = "Failed to send order received mail: " + operation;
                LOG.error( StringUtil.expandString( message, (Object) null, me ), me );
                redirectToErrorPage( request, response, formItems, ERR_EMAIL_SEND_FAILED, null );
            }
        }
        else
        {
            String message = "Unknown operation: %0";
            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, operation, null ) );
        }
    }

    private void sendMail( String receiverEmail, String receiverName, String senderEmail, String senderName, String subject,
                           String message )
        throws MessagingException, UnsupportedEncodingException
    {

        // smtp server
        Properties smtpProperties = new Properties();
        smtpProperties.put( "mail.smtp.host", verticalProperties.getMailSmtpHost() );
        Session session = Session.getDefaultInstance( smtpProperties, null );

        // create message
        Message msg = new MimeMessage( session );

        // set from address
        if ( senderEmail != null && !senderEmail.equals( "" ) )
        {
            InternetAddress addressFrom = new InternetAddress( senderEmail );
            if ( senderName != null && !senderName.equals( "" ) )
            {
                addressFrom.setPersonal( senderName );
            }
            msg.setFrom( addressFrom );
        }

        // set to address
        InternetAddress addressTo = new InternetAddress( receiverEmail );
        if ( receiverName != null )
        {
            addressTo.setPersonal( receiverName );
        }
        msg.setRecipient( Message.RecipientType.TO, addressTo );

        // Setting subject and content type
        msg.setSubject( subject );

        message = RegexpUtil.substituteAll( "(\\\\n)", "\n", message );
        msg.setContent( message, "text/plain; charset=UTF-8" );

        // send message
        Transport.send( msg );
    }
}

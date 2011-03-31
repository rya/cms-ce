/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import java.rmi.server.UID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.VerticalAdminException;

import com.enonic.cms.domain.security.user.User;

public class ContentOrderXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        Element customerElem = XMLTool.getElement( contentDataElem, "customer" );

        StringBuffer title = new StringBuffer();
        String firstName = XMLTool.getElementText( XMLTool.getElement( customerElem, "firstname" ) );
        if ( firstName != null && firstName.length() > 0 )
        {
            title.append( firstName );
            title.append( " " );
        }

        String surName = XMLTool.getElementText( XMLTool.getElement( customerElem, "surname" ) );
        if ( surName != null && surName.length() > 0 )
        {
            title.append( surName );
        }

        return title.toString();
    }

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // guid
        String guid = formItems.getString( "guid", new UID().toString() );
        XMLTool.createElement( doc, contentdata, "guid", guid );

        // Status
        XMLTool.createElement( doc, contentdata, "status", formItems.getString( "status" ) );

        // Customer
        Element customer = XMLTool.createElement( doc, contentdata, "customer" );
        XMLTool.createElement( doc, customer, "firstname", formItems.getString( "customer_firstname", "" ) );
        XMLTool.createElement( doc, customer, "surname", formItems.getString( "customer_surname" ) );
        XMLTool.createElement( doc, customer, "company", formItems.getString( "customer_company", "" ) );
        XMLTool.createElement( doc, customer, "email", formItems.getString( "customer_email" ) );
        XMLTool.createElement( doc, customer, "telephone", formItems.getString( "customer_telephone", "" ) );
        XMLTool.createElement( doc, customer, "mobile", formItems.getString( "customer_mobile", "" ) );
        XMLTool.createElement( doc, customer, "fax", formItems.getString( "customer_fax", "" ) );

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
        Element details = XMLTool.createElement( doc, contentdata, "details" );
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

        // Items
        Element items = XMLTool.createElement( doc, contentdata, "items" );
        if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, "item_productid" ) )
        {
            String productid[] = (String[]) formItems.get( "item_productid" );
            String productnumber[] = (String[]) formItems.get( "item_productnumber" );
            String title[] = (String[]) formItems.get( "item_title" );
            String price[] = (String[]) formItems.get( "item_price" );
            String count[] = (String[]) formItems.get( "item_count" );
            String subtotal[] = (String[]) formItems.get( "item_subtotal" );

            double total = 0.0D;
            int totalcount = 0;
            for ( int i = 0; i < productid.length; i++ )
            {
                int c = Integer.parseInt( count[i] );
                if ( title[i] != null && title[i].length() > 0 && c > 0 )
                {
                    totalcount++;
                    total += Double.parseDouble( subtotal[i] );
                    Element item = XMLTool.createElement( doc, items, "item", title[i] );
                    item.setAttribute( "productid", productid[i] );
                    item.setAttribute( "productnumber", productnumber[i] );
                    item.setAttribute( "price", price[i] );
                    item.setAttribute( "count", count[i] );
                }
            }
            items.setAttribute( "total", String.valueOf( total ) );
            items.setAttribute( "count", String.valueOf( totalcount ) );
        }
        else
        {
            String title = (String) formItems.get( "item_title", "" );
            int c = Integer.parseInt( (String) formItems.get( "item_count" ) );
            if ( title != null && title.length() > 0 && c > 0 )
            {
                Element item = XMLTool.createElement( doc, items, "item", title );
                item.setAttribute( "productid", formItems.getString( "item_productid", "" ) );
                item.setAttribute( "productnumber", formItems.getString( "item_productnumber", "" ) );
                item.setAttribute( "price", formItems.getString( "item_price", "" ) );
                item.setAttribute( "count", formItems.getString( "item_count", "" ) );

                items.setAttribute( "total", formItems.getString( "item_subtotal", "" ) );
                items.setAttribute( "count", formItems.getString( "item_count", "" ) );
            }
            else
            {
                items.setAttribute( "total", "0.00" );
                items.setAttribute( "count", "0" );
            }
        }
    }

    public String getContentTitle( ExtendedMap formItems )
    {
        StringBuffer name = new StringBuffer( formItems.getString( "customer_firstname", "" ) );
        if ( name.length() > 0 )
        {
            name.append( ' ' );
        }
        name.append( formItems.getString( "customer_surname" ) );
        return name.toString();
    }

}

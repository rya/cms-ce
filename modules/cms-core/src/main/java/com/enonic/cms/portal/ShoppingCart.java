/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import com.enonic.esl.util.RegexpUtil;
import com.enonic.esl.xml.XMLTool;


public class ShoppingCart
    implements Serializable
{

    private class CartItem
        implements Serializable
    {
        int productId;

        String productNumber;

        String productName;

        double price;

        int count;

        Map<String, String> customValues;
    }

    private Map<Integer, CartItem> f_items;

    public ShoppingCart()
    {
        f_items = new TreeMap<Integer, CartItem>();
    }

    public void addItem( int productId, String productNumber, String productName, double price, int count,
                         Map<String, String> customValues )
    {

        CartItem item = f_items.get( productId );
        if ( item == null )
        {
            item = new CartItem();
            item.productId = productId;
            item.productNumber = productNumber;
            item.productName = productName;
            item.price = price;
            item.count = count;
            item.customValues = customValues;
            f_items.put( item.productId, item );
        }
        else
        {
            item.count += count;
        }
    }

    public String addItemsToMailMessage( String message, String itemLine )
    {
        if ( f_items.size() > 0 )
        {
            StringBuffer msgPart = new StringBuffer();
            String regexp = "\\%item_(productid|productnumber|productname|price|count|subtotal)\\((\\d+)\\,([lcr])\\)\\%";
            String substRegexpStart = "\\%item_";
            String substRegexpEnd = "\\((\\d+)\\,([lcr])\\)\\%";

            for ( Object o : f_items.values() )
            {
                String line = itemLine;
                CartItem item = (CartItem) o;
                Matcher results = RegexpUtil.match( itemLine, regexp );

                while ( results.find() )
                {
                    String lineItem;
                    StringBuffer substRegexp = new StringBuffer( substRegexpStart );
                    substRegexp.append( results.group( 1 ) );
                    substRegexp.append( substRegexpEnd );

                    if ( "productid".equals( results.group( 1 ) ) )
                    {
                        lineItem = String.valueOf( item.productId );
                    }
                    else if ( "productnumber".equals( results.group( 1 ) ) )
                    {
                        lineItem = ( item.productNumber != null ? item.productNumber : "" );
                    }
                    else if ( "productname".equals( results.group( 1 ) ) )
                    {
                        lineItem = item.productName;
                    }
                    else if ( "price".equals( results.group( 1 ) ) )
                    {
                        lineItem = String.valueOf( item.price );
                    }
                    else if ( "count".equals( results.group( 1 ) ) )
                    {
                        lineItem = String.valueOf( item.count );
                    }
                    else
                    {
                        lineItem = String.valueOf( item.price * item.count );
                    }

                    int width = Integer.parseInt( results.group( 2 ) );
                    char align = results.group( 3 ).charAt( 0 );
                    StringBuffer sb = new StringBuffer( Math.max( width, lineItem.length() ) );
                    switch ( align )
                    {
                        case 'l': // left
                            sb.append( lineItem );
                            if ( lineItem.length() > width )
                            {
                                sb.setLength( width );
                            }
                            else
                            {
                                for ( int j = 0; j < width - lineItem.length(); j++ )
                                {
                                    sb.append( ' ' );
                                }
                            }
                            break;
                        case 'r': // right
                            if ( lineItem.length() > width )
                            {
                                sb.append( lineItem );
                                sb.setLength( width );
                            }
                            else
                            {
                                for ( int j = 0; j < width - lineItem.length(); j++ )
                                {
                                    sb.append( ' ' );
                                }
                                sb.append( lineItem );
                            }
                            break;
                        default: // center
                            if ( lineItem.length() > width )
                            {
                                sb.append( lineItem );
                                sb.setLength( width );
                            }
                            else
                            {
                                double half = ( ( width - lineItem.length() ) / 2.0D );
                                int left = (int) Math.ceil( half );
                                int right = (int) Math.floor( half );
                                for ( int j = 0; j < left; j++ )
                                {
                                    sb.append( ' ' );
                                }
                                sb.append( lineItem );
                                for ( int j = 0; j < right; j++ )
                                {
                                    sb.append( ' ' );
                                }
                            }
                            break;
                    }

                    line = RegexpUtil.substituteAll( substRegexp.toString(), sb.toString(), line );
                }

                msgPart.append( line );
            }

            return RegexpUtil.substituteAll( "\\%order_items\\%", msgPart.toString(), message );
        }

        return message;
    }

    public String addTotalToMailMessage( String message )
    {

        String totalString;
        if ( f_items.size() > 0 )
        {
            Iterator iterator = f_items.values().iterator();

            double total = 0.0D;
            while ( iterator.hasNext() )
            {
                CartItem item = (CartItem) iterator.next();
                total += item.price * item.count;
            }
            totalString = String.valueOf( total );
        }
        else
        {
            totalString = "0.00";
        }

        String regexp = "\\%total\\((\\d+)\\,([lcr])\\)\\%";
        Matcher results = RegexpUtil.match( message, regexp );
        if ( results.find() )
        {
            int width = Integer.parseInt( results.group( 1 ) );
            char align = results.group( 2 ).charAt( 0 );
            StringBuffer sb = new StringBuffer( Math.max( width, totalString.length() ) );

            switch ( align )
            {
                case 'l': // left
                    sb.append( totalString );
                    if ( totalString.length() > width )
                    {
                        sb.setLength( width );
                    }
                    else
                    {
                        for ( int j = 0; j < width - totalString.length(); j++ )
                        {
                            sb.append( ' ' );
                        }
                    }
                    break;
                case 'r': // right
                    if ( totalString.length() > width )
                    {
                        sb.append( totalString );
                        sb.setLength( width );
                    }
                    else
                    {
                        for ( int j = 0; j < width - totalString.length(); j++ )
                        {
                            sb.append( ' ' );
                        }
                        sb.append( totalString );
                    }
                    break;
                default: // center
                    if ( totalString.length() > width )
                    {
                        sb.append( totalString );
                        sb.setLength( width );
                    }
                    else
                    {
                        double half = ( ( width - totalString.length() ) / 2.0D );
                        int left = (int) Math.ceil( half );
                        int right = (int) Math.floor( half );
                        for ( int j = 0; j < left; j++ )
                        {
                            sb.append( ' ' );
                        }
                        sb.append( totalString );
                        for ( int j = 0; j < right; j++ )
                        {
                            sb.append( ' ' );
                        }
                    }
                    break;
            }

            message = RegexpUtil.substituteAll( regexp, sb.toString(), message );
        }

        return message;
    }

    public void clear()
    {
        f_items.clear();
    }

    public boolean isEmpty()
    {
        return f_items.size() == 0;
    }

    public boolean removeItem( int itemid )
    {

        return f_items.remove( itemid ) != null;
    }

    public void toDoc( Document doc, Element root, boolean shoppingCart )
    {

        toDoc( doc, root, shoppingCart, true );
    }

    public void toDoc( Document doc, Element root, boolean shoppingCart, boolean full )
    {

        Element elem = shoppingCart ? root : XMLTool.createElement( doc, root, "items" );

        double total = 0.0D;
        int count = 0;
        for ( CartItem cartItem : f_items.values() )
        {
            double subtotal = cartItem.price * cartItem.count;
            total += subtotal;
            count += cartItem.count;
            if ( full )
            {
                Element item = XMLTool.createElement( doc, elem, "item", cartItem.productName );
                if ( shoppingCart )
                {
                    item.setAttribute( "subtotal", String.valueOf( subtotal ) );
                }
                item.setAttribute( "productid", String.valueOf( cartItem.productId ) );
                if ( cartItem.productNumber != null )
                {
                    item.setAttribute( "productnumber", cartItem.productNumber );
                }
                item.setAttribute( "price", String.valueOf( cartItem.price ) );
                item.setAttribute( "count", String.valueOf( cartItem.count ) );

                // set custom attribute values
                if ( cartItem.customValues != null && cartItem.customValues.size() > 0 )
                {
                    for ( Map.Entry<String, String> entry : cartItem.customValues.entrySet() )
                    {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        item.setAttribute( key, value );
                    }
                }
            }
        }
        elem.setAttribute( "total", String.valueOf( total ) );
        elem.setAttribute( "count", String.valueOf( count ) );
    }

    public int[] toIdArray()
    {
        final HashSet<Integer> set = Sets.newHashSet();
        for ( Object o : f_items.values() )
        {
            CartItem ci = (CartItem) o;
            set.add( ci.productId );
        }

        return Ints.toArray( set );
    }

    public boolean updateItem( int itemid, int count )
    {
        CartItem item = f_items.get( itemid );
        if ( item == null )
        {
            return false;
        }
        item.count = count;
        return true;
    }

    public Document toDoc( boolean full )
    {
        Document doc = XMLTool.createDocument( "shoppingcart" );
        Element e = doc.getDocumentElement();
        toDoc( doc, e, true, full );
        return doc;
    }
}
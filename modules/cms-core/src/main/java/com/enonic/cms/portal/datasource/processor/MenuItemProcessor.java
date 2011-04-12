/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.processor;

import com.enonic.cms.portal.datasource.DataSourceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.portal.datasource.methodcall.MethodCall;

/**
 * Apr 30, 2009
 */
public class MenuItemProcessor
    implements DataSourceProcessor
{
    private Element newMenusElem;

    private Element newMenuitemElem;

    public MenuItemProcessor( Element newMenusElem, Element newMenuitemElem )
    {
        this.newMenusElem = newMenusElem;
        this.newMenuitemElem = newMenuitemElem;
    }

    public void postProcess( Document resultDoc, MethodCall methodCall )
    {
        Element root = resultDoc.getDocumentElement();
        if ( "menuitems".equals( root.getTagName() ) )
        {
            processMenuItems( resultDoc, root, methodCall );
        }
        else if ( "menus".equals( root.getTagName() ) )
        {
            processMenus( resultDoc, root, methodCall );
        }
    }

    private void processMenuItems( Document resultDoc, Element root, MethodCall methodCall )
    {
        String key = newMenuitemElem.getAttribute( "key" );
        if ( key == null || key.length() == 0 )
        {
            key = "-1";
            Element menuitemElem = (Element) resultDoc.importNode( newMenuitemElem, true );
            menuitemElem.setAttribute( "key", key );
            String methodName = methodCall.getMethodName();
            Class[] paramTypes = methodCall.getParamTypes();
            Object[] args = methodCall.getArguments();

            boolean match;
            int tagItem;
            if ( "getMenuBranch".equals( methodName ) && paramTypes.length >= 3 )
            {
                match = ( paramTypes[0] == DataSourceContext.class );
                match &= ( paramTypes[1] == Integer.TYPE );
                match &= ( paramTypes[2] == Boolean.TYPE );
                tagItem = (Integer) args[1];
            }
            else if ( "getSubMenu".equals( methodName ) && paramTypes.length >= 3 )
            {
                match = ( paramTypes[0] == DataSourceContext.class );
                match &= ( paramTypes[1] == Integer.TYPE );
                match &= ( paramTypes[2] == Integer.TYPE );
                tagItem = (Integer) args[2];
            }
            else
            {
                match = false;
                tagItem = -1;
            }

            if ( match && tagItem == -1 )
            {
                Element menuitemsElem;
                if ( "getMenuBranch".equals( methodName ) )
                {
                    Element elem = XMLTool.getElement( newMenusElem, "menu" );
                    elem = XMLTool.getElement( elem, "menuitems" );
                    if ( elem != null )
                    {
                        menuitemsElem = (Element) resultDoc.importNode( elem, true );
                        resultDoc.replaceChild( menuitemsElem, root );
                    }
                    else
                    {
                        menuitemsElem = root;
                    }
                }
                else
                {
                    menuitemsElem = root;
                }

                // find menu item's parent
                String parent = menuitemElem.getAttribute( "parent" );
                Element parentElem;
                if ( parent != null && parent.length() > 0 )
                {
                    parentElem = (Element) XMLTool.selectNode( resultDoc, "//menuitem[@key = " + parent + "]" );
                    if ( parentElem != null )
                    {
                        menuitemsElem = XMLTool.getElement( parentElem, "menuitems" );
                    }
                }
                else
                {
                    parentElem = null;
                }

                // add menu item if visible
                boolean visible = "yes".equals( menuitemElem.getAttribute( "visible" ) );
                Element tagStart;
                if ( visible )
                {
                    tagStart = menuitemElem;
                    menuitemsElem.appendChild( menuitemElem );
                }
                else
                {
                    tagStart = parentElem;
                }

                // set path
                tagPath( tagStart );

                // clean up menu items xml
                boolean topItems;
                if ( args[2] instanceof Boolean )
                {
                    topItems = (Boolean) args[2];
                }
                else
                {
                    topItems = true;
                }
                cleanMenuitemsElem( resultDoc.getDocumentElement(), topItems, true );
            }
            else
            {
                root.appendChild( menuitemElem );
            }
        }
        else
        {
            Element parentElem;
            Element menuitemElem = (Element) XMLTool.selectNode( resultDoc, "//menuitem[@key = " + key + "]" );
            if ( menuitemElem == null )
            {
                String parent = newMenuitemElem.getAttribute( "parent" );
                if ( parent != null && parent.length() > 0 )
                {
                    parentElem = (Element) XMLTool.selectNode( resultDoc, "//menuitem[@key = " + parent + "]" );
                }
                else
                {
                    parentElem = null;
                }
            }
            else
            {
                parentElem = null;
            }

            if ( menuitemElem != null || parentElem != null )
            {
                if ( menuitemElem != null )
                {
                    Element menuitemsElem = (Element) menuitemElem.getParentNode();
                    String methodName = methodCall.getMethodName();
                    boolean visible = "yes".equals( newMenuitemElem.getAttribute( "visible" ) );

                    if ( visible || "getMenuItem".equals( methodName ) )
                    {
                        Element newElem = (Element) resultDoc.importNode( newMenuitemElem, true );
                        Element elem = XMLTool.getElement( menuitemElem, "menuitems" );
                        if ( elem != null )
                        {
                            newElem.appendChild( elem );
                        }
                        menuitemsElem.replaceChild( newElem, menuitemElem );
                        menuitemElem = newElem;
                    }
                    else
                    {
                        menuitemsElem.removeChild( menuitemElem );
                        menuitemElem = null;
                    }
                }
                else
                {
                    Element menuitemsElem = XMLTool.getElement( parentElem, "menuitems" );
                    boolean visible = "yes".equals( newMenuitemElem.getAttribute( "visible" ) );
                    if ( visible )
                    {
                        Element newElem = (Element) resultDoc.importNode( newMenuitemElem, true );
                        int newOrder = Integer.parseInt( newElem.getAttribute( "order" ) );
                        Element[] menuitemElems = XMLTool.getElements( menuitemsElem );
                        int index;

                        for ( index = 0; index < menuitemElems.length; index++ )
                        {
                            int currentOrder = Integer.parseInt( menuitemElems[index].getAttribute( "order" ) );
                            if ( currentOrder >= newOrder )
                            {
                                break;
                            }
                        }
                        if ( index < menuitemElems.length )
                        {
                            menuitemsElem.insertBefore( newElem, menuitemElems[index] );
                        }
                        else
                        {
                            menuitemsElem.appendChild( newElem );
                        }

                        menuitemElem = newElem;
                    }
                }

                if ( menuitemElem != null )
                {
                    String methodName = methodCall.getMethodName();
                    Class[] paramTypes = methodCall.getParamTypes();
                    Object[] args = methodCall.getArguments();
                    boolean match;
                    int tagItem;
                    if ( ( "getMenuBranch".equals( methodName ) && paramTypes.length >= 3 ) )
                    {
                        match = ( paramTypes[0] == DataSourceContext.class );
                        match &= ( paramTypes[1] == Integer.TYPE );
                        match &= ( paramTypes[2] == Boolean.TYPE );
                        tagItem = (Integer) args[1];
                    }
                    else if ( ( "getSubMenu".equals( methodName ) && paramTypes.length >= 3 ) )
                    {
                        match = ( paramTypes[0] == DataSourceContext.class );
                        match &= ( paramTypes[1] == Integer.TYPE );
                        match &= ( paramTypes[2] == Integer.TYPE );
                        tagItem = (Integer) args[2];
                    }
                    else
                    {
                        match = false;
                        tagItem = -1;
                    }

                    if ( match && tagItem == Integer.parseInt( key ) )
                    {
                        Element documentElem = XMLTool.getElement( menuitemElem, "document" );
                        if ( documentElem != null )
                        {
                            menuitemElem.removeChild( documentElem );
                        }
                        Element pageElem = XMLTool.getElement( menuitemElem, "page" );
                        if ( pageElem != null )
                        {
                            Element contentobjectsElem = XMLTool.getElement( pageElem, "contentobjects" );
                            if ( contentobjectsElem != null )
                            {
                                pageElem.removeChild( contentobjectsElem );
                            }
                        }
                        menuitemElem.setAttribute( "path", "true" );
                        menuitemElem.setAttribute( "active", "true" );
                        if ( parentElem != null )
                        {
                            parentElem.removeAttribute( "active" );
                        }
                    }
                    else
                    {
                        Element elem = XMLTool.getElement( menuitemElem, "menuitems" );
                        if ( elem != null )
                        {
                            menuitemElem.removeChild( elem );
                        }
                    }
                }
            }
            else
            {
                String methodName = methodCall.getMethodName();
                Class[] paramTypes = methodCall.getParamTypes();
                Object[] args = methodCall.getArguments();
                boolean match;
                int tagItem;
                if ( ( "getMenuBranch".equals( methodName ) && paramTypes.length >= 3 ) )
                {
                    match = ( paramTypes[0] == DataSourceContext.class );
                    match &= ( paramTypes[1] == Integer.TYPE );
                    match &= ( paramTypes[2] == Boolean.TYPE );
                    tagItem = (Integer) args[1];
                }
                else if ( ( "getSubMenu".equals( methodName ) && paramTypes.length >= 3 ) )
                {
                    match = ( paramTypes[0] == DataSourceContext.class );
                    match &= ( paramTypes[1] == Integer.TYPE );
                    match &= ( paramTypes[2] == Integer.TYPE );
                    tagItem = (Integer) args[2];
                }
                else
                {
                    match = false;
                    tagItem = -1;
                }

                if ( match && tagItem == Integer.parseInt( key ) )
                {
                    Element tmpElem = (Element) XMLTool.selectNode( newMenusElem, "//menuitem[@key = " + key + "]" );
                    if ( tmpElem != null && "yes".equals( tmpElem.getAttribute( "visible" ) ) )
                    {
                        tmpElem = (Element) resultDoc.importNode( tmpElem, true );
                        Element elem = XMLTool.getElement( tmpElem, "menuitems" );
                        if ( elem != null )
                        {
                            cleanMenuitemsElem( elem, true, false );
                        }
                        root.appendChild( tmpElem );
                        root.setAttribute( "istop", "yes" );
                        tagPath( tmpElem );
                    }
                }
            }
        }
    }

    private void tagPath( Element menuitemElem )
    {
        if ( menuitemElem != null )
        {
            menuitemElem.setAttribute( "path", "true" );
            menuitemElem.setAttribute( "active", "true" );
            Node parentNode = menuitemElem.getParentNode().getParentNode();
            while ( parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element parentElem = (Element) parentNode;
                parentElem.setAttribute( "path", "true" );
                parentNode = parentElem.getParentNode().getParentNode();
            }
        }
    }

    private void cleanMenuitemsElem( Element menuitemsElem, boolean topItems, boolean top )
    {
        if ( menuitemsElem != null )
        {
            menuitemsElem.setAttribute( "istop", ( top ? "yes" : "no" ) );
            Element[] menuitemElems = XMLTool.getElements( menuitemsElem );
            for ( int i = 0; i < menuitemElems.length; i++ )
            {
                String visible = menuitemElems[i].getAttribute( "visible" );
                String path = menuitemElems[i].getAttribute( "path" );
                if ( !"yes".equals( visible ) || ( !topItems && !"true".equals( path ) ) )
                {
                    menuitemsElem.removeChild( menuitemElems[i] );
                }
                else
                {
                    Element documentElem = XMLTool.getElement( menuitemElems[i], "document" );
                    if ( documentElem != null )
                    {
                        menuitemElems[i].removeChild( documentElem );
                    }
                    Element pageElem = XMLTool.getElement( menuitemElems[i], "page" );
                    if ( pageElem != null )
                    {
                        if ( !pageElem.hasAttribute( "key" ) )
                        {
                            pageElem.setAttribute( "key", "-1" );
                        }
                        Element contentobjectsElem = XMLTool.getElement( pageElem, "contentobjects" );
                        if ( contentobjectsElem != null )
                        {
                            pageElem.removeChild( contentobjectsElem );
                        }
                    }
                    Element accessrightsElem = XMLTool.getElement( menuitemElems[i], "accessrights" );
                    if ( accessrightsElem != null )
                    {
                        menuitemElems[i].removeChild( accessrightsElem );
                    }
                    Element elem = XMLTool.getElement( menuitemElems[i], "menuitems" );
                    cleanMenuitemsElem( elem, true, false );
                }
            }
        }
    }

    private void processMenus( Document resultDoc, Element root, MethodCall methodCall )
    {
        String key = newMenuitemElem.getAttribute( "key" );
        String parentKey = newMenuitemElem.getAttribute( "parent" );
        String menuKey = newMenuitemElem.getAttribute( "menukey" );
        boolean visible = "yes".equals( newMenuitemElem.getAttribute( "visible" ) );
        Element menuElem = XMLTool.getElement( root, "menu" );

        if ( menuElem != null && menuKey.equals( menuElem.getAttribute( "key" ) ) )
        {
            Element menuitemsElem = XMLTool.getElement( menuElem, "menuitems" );
            if ( menuitemsElem == null )
            {
                menuitemsElem = XMLTool.createElement( resultDoc, menuElem, "menuitems" );
            }
            Element parentElem;
            if ( parentKey != null && parentKey.length() != 0 )
            {
                parentElem = (Element) XMLTool.selectNode( menuitemsElem, "//menuitem[@key = " + parentKey + "]" );
                if ( parentElem != null )
                {
                    menuitemsElem = XMLTool.getElement( parentElem, "menuitems" );
                }
            }
            else
            {
                parentElem = null;
            }

            String methodName = methodCall.getMethodName();
            Class[] paramTypes = methodCall.getParamTypes();
            Object[] args = methodCall.getArguments();
            int levels;
            if ( "getMenu".equals( methodName ) )
            {
                if ( paramTypes.length == 3 )
                {
                    levels = (Integer) args[2];
                }
                else if ( paramTypes.length > 3 )
                {
                    levels = (Integer) args[3];
                }
                else
                {
                    levels = -1;
                }
            }
            else if ( "getSubMenu".equals( methodName ) && paramTypes.length > 3 )
            {
                levels = (Integer) args[3];
            }
            else
            {
                levels = -1;
            }

            Element menuitemElem;
            if ( key == null || key.length() == 0 )
            {
                if ( visible && ( levels != 1 || parentKey == null || parentKey.length() == 0 ) )
                {
                    menuitemElem = (Element) resultDoc.importNode( newMenuitemElem, true );
                    Element documentElem = XMLTool.getElement( menuitemElem, "document" );
                    if ( documentElem != null )
                    {
                        menuitemElem.removeChild( documentElem );
                    }
                    menuitemElem.setAttribute( "key", "-1" );
                    menuitemsElem.appendChild( menuitemElem );
                }
                else
                {
                    menuitemElem = null;
                }
            }
            else
            {
                if ( visible && ( levels != 1 || parentKey == null || parentKey.length() == 0 ) )
                {
                    Element oldElem = (Element) XMLTool.selectNode( menuitemsElem, "menuitem[@key=" + key + "]" );
                    if ( oldElem != null )
                    {
                        menuitemElem = (Element) resultDoc.importNode( newMenuitemElem, true );
                        Element documentElem = XMLTool.getElement( menuitemElem, "document" );
                        if ( documentElem != null )
                        {
                            menuitemElem.removeChild( documentElem );
                        }
                        Element oldMenuitemsElem = XMLTool.getElement( oldElem, "menuitems" );
                        Element newMenuitemsElem = XMLTool.getElement( menuitemElem, "menuitems" );
                        if ( oldMenuitemsElem == null )
                        {
                            oldMenuitemsElem = XMLTool.createElement( resultDoc, "menuitems" );
                        }
                        if ( newMenuitemsElem != null )
                        {
                            menuitemElem.replaceChild( oldMenuitemsElem, newMenuitemsElem );
                        }
                        else
                        {
                            menuitemElem.appendChild( oldMenuitemsElem );
                        }
                        menuitemsElem.replaceChild( menuitemElem, oldElem );
                    }
                    else
                    {
                        menuitemElem = (Element) resultDoc.importNode( newMenuitemElem, true );
                        int newOrder = Integer.parseInt( menuitemElem.getAttribute( "order" ) );
                        Element[] menuitemElems = XMLTool.getElements( menuitemsElem );
                        int index;

                        for ( index = 0; index < menuitemElems.length; index++ )
                        {
                            int currentOrder = Integer.parseInt( menuitemElems[index].getAttribute( "order" ) );
                            if ( currentOrder >= newOrder )
                            {
                                break;
                            }
                        }
                        if ( index < menuitemElems.length )
                        {
                            menuitemsElem.insertBefore( menuitemElem, menuitemElems[index] );
                        }
                        else
                        {
                            menuitemsElem.appendChild( menuitemElem );
                        }
                    }
                }
                else
                {
                    Node oldElem = XMLTool.selectNode( menuitemsElem, "menuitem[@key=" + key + "]" );
                    if ( oldElem != null )
                    {
                        menuitemsElem.removeChild( oldElem );
                    }
                    menuitemElem = null;
                }
            }

            if ( menuitemElem != null || parentElem != null )
            {
                if ( ( "getMenu".equals( methodName ) && paramTypes.length > 3 ) ||
                    ( "getSubMenu".equals( methodName ) && paramTypes.length > 3 ) )
                {
                    //int tagItem = ((Integer) args[2]).intValue();
                    if ( menuitemElem != null )
                    {
                        menuitemElem.setAttribute( "path", "true" );
                        menuitemElem.setAttribute( "active", "true" );
                    }
                    if (/*tagItem == -1 &&*/ parentElem != null )
                    {
                        Element tmpElem = parentElem;
                        if ( menuitemElem == null )
                        {
                            parentElem.setAttribute( "active", "true" );
                        }
                        while ( tmpElem != null && "menuitem".equals( tmpElem.getTagName() ) )
                        {
                            tmpElem.setAttribute( "path", "true" );
                            tmpElem = (Element) tmpElem.getParentNode().getParentNode();
                        }
                    }
                }
            }
        }
    }

    public void preProcess( MethodCall methodCall )
    {
    }
}

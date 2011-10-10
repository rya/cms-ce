/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.adminweb.VerticalAdminException;

import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.security.user.User;

public interface ContentXMLBuilder
{

    String getContentTitle( ExtendedMap formItems );

    String getTitleFormKey();

    void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException;

    String buildXML( ExtendedMap formItems, User user, boolean create, boolean excludeContendataXML, boolean usePersistedContendataXML )
        throws VerticalAdminException;

    int[] getRelatedContentKeys( ExtendedMap formItems );

    public BinaryData[] getBinaries( ExtendedMap formItems )
        throws VerticalAdminException;

    public int[] getDeleteBinaries( ExtendedMap formItems )
        throws VerticalAdminException;
}

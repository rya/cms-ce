/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Collection;

import com.enonic.cms.core.security.group.GroupType;
import org.jdom.Element;

import com.enonic.cms.core.security.group.GroupEntity;


public class ContentAccessXmlCreator
{

    public Element createAccessRightsElement( final Collection<ContentAccessEntity> contentAccesses )
    {

        final Element accessrightsEl = new Element( "accessrights" );
        accessrightsEl.setAttribute( "type", "1" ); // // type content

        for ( final ContentAccessEntity contentAccess : contentAccesses )
        {

            accessrightsEl.addContent( doCreateAccessRightElement( contentAccess ) );
        }

        return accessrightsEl;
    }

    private Element doCreateAccessRightElement( final ContentAccessEntity contentAccess )
    {

        final GroupEntity group = contentAccess.getGroup();
        final GroupType groupType = group.getType();

        final Element accessrightEl = new Element( "accessright" );
        accessrightEl.setAttribute( "groupkey", contentAccess.getGroup().getGroupKey().toString() );
        accessrightEl.setAttribute( "grouptype", Integer.toString( groupType.toInteger() ) );

        if ( groupType == GroupType.USER )
        {
            accessrightEl.setAttribute( "uid", group.getUser().getName() );
            accessrightEl.setAttribute( "fullname", group.getUser().getDisplayName() );
            accessrightEl.setAttribute( "qualifiedName", group.getUser().getQualifiedName().toString() );
        }
        else if ( ( groupType == GroupType.USERSTORE_GROUP ) || ( groupType == GroupType.GLOBAL_GROUP ) )
        {
            accessrightEl.setAttribute( "groupname", group.getName() );
            accessrightEl.setAttribute( "qualifiedname", group.getQualifiedName().toString() );
        }
        else
        {
            accessrightEl.setAttribute( "groupname", groupType.getName() );
        }

        if ( contentAccess.isReadAccess() )
        {
            accessrightEl.setAttribute( "read", "true" );
        }
        if ( contentAccess.isUpdateAccess() )
        {
            accessrightEl.setAttribute( "update", "true" );
        }
        if ( contentAccess.isDeleteAccess() )
        {
            accessrightEl.setAttribute( "delete", "true" );
        }

        return accessrightEl;
    }
}

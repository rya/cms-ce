/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem.section;

import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocationSpecification;
import org.jdom.Element;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentLocations;

public class SectionContentXmlCreator
{
    private boolean includeCheckOfDireMenuItemPlacements = false;

    public Element createSectionNamesElement( final ContentEntity content, boolean includeSectionActivationInfo )
    {
        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setIncludeInactiveLocationsInSection( includeSectionActivationInfo );
        final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

        final Element sectionNamesEl = new Element( "sectionnames" );
        sectionNamesEl.setAttribute( "deprecated", "Use location instead" );
        int addCount = 0;

        if ( contentLocations.hasLocations() )
        {
            for ( final ContentLocation contentLocation : contentLocations.getAllLocations() )
            {
                if ( contentLocation.isInSection() )
                {
                    final Element sectionEl = new Element( "sectionname" );

                    sectionEl.setAttribute( "home", Boolean.toString( contentLocation.isUserDefinedSectionHome() ) );
                    sectionEl.setAttribute( "menuitemkey", contentLocation.getMenuItemKey().toString() );
                    sectionEl.setAttribute( "sitekey", contentLocation.getSiteKey().toString() );
                    sectionEl.setAttribute( "menukey", contentLocation.getSiteKey().toString() );

                    if ( includeSectionActivationInfo && contentLocation.isInSection() )
                    {
                        sectionEl.setAttribute( "activated", String.valueOf( contentLocation.isApproved() ) );
                    }

                    sectionEl.addContent( contentLocation.getMenuItemName() );

                    sectionNamesEl.addContent( sectionEl );

                    addCount++;
                }
            }
        }

        sectionNamesEl.setAttribute( "count", Integer.toString( addCount ) );

        return sectionNamesEl;
    }
}

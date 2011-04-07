package com.enonic.cms.core.structure;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentLocation;
import com.enonic.cms.domain.content.ContentLocationSpecification;
import com.enonic.cms.domain.content.ContentLocations;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 4/7/11
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SectionXmlCreator
{
    private SiteDao siteDao;

    private ContentXMLCreator contentXMLCreator = new ContentXMLCreator();

    public SectionXmlCreator( SiteDao siteDao, CategoryAccessResolver categoryAccessResolver, ContentAccessResolver contentAccessResolver )
    {
        this.siteDao = siteDao;
        contentXMLCreator.setIncludeAccessRightsInfo( false );
        contentXMLCreator.setIncludeRelatedContentsInfo( false );
        contentXMLCreator.setIncludeSectionActivationInfo( true );
        contentXMLCreator.setIncludeRepositoryPathInfo( false );
        contentXMLCreator.setIncludeUserRightsInfo( true, categoryAccessResolver, contentAccessResolver );
        contentXMLCreator.setIncludeVersionsInfoForAdmin( false );
        contentXMLCreator.setIncludeOwnerAndModifierData( false );
        contentXMLCreator.setIncludeDraftInfo( true );
        contentXMLCreator.setIncludeContentData( false );
        contentXMLCreator.setIncludeCategoryData( false );
    }

    public XMLDocument createSectionsDocument( UserEntity runningUser, ContentResultSet contentResultSet )
    {
        final Element sectionsEl = new Element( "sections" );
        List<Section> sectionNameList = getUniqueSectionsSorted( contentResultSet );

        sectionsEl.setAttribute( "count", String.valueOf( sectionNameList.size() ) );
        sectionsEl.setAttribute( "contenttotalcount", String.valueOf( contentResultSet.getTotalCount() ) );

        int totalCount = 0;
        for ( Section section : sectionNameList )
        {
            Element sectionEl = new Element( "section" );

            sectionEl.setAttribute( "sitekey", section.getSiteKey().toString() );
            sectionEl.setAttribute( "sitename", section.getSiteName() );
            sectionEl.setAttribute( "menuitemkey", section.getMenuItemKey().toString() );
            sectionEl.setAttribute( "name", section.getMenuItemName() );
            sectionEl.setAttribute( "path", section.getMenuItemPath() );

            int count = 0;
            for ( ContentEntity content : contentResultSet.getContents() )
            {
                ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
                contentLocationSpecification.setIncludeInactiveLocationsInSection( true );
                contentLocationSpecification.setSiteKey( section.getSiteKey() );
                final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

                for ( ContentLocation location : contentLocations.getAllLocations() )
                {
                    if ( location.getMenuItemKey().equals( section.getMenuItemKey() ) )
                    {
                        Element contentEl = contentXMLCreator.createSingleContentVersionElement( runningUser, content.getMainVersion() );
                        sectionEl.addContent( contentEl );
                        count++;
                        totalCount++;
                    }
                }
            }
            sectionEl.setAttribute( "sectioncount", String.valueOf( count ) );

            sectionsEl.addContent( sectionEl );
        }
        sectionsEl.setAttribute( "contentcount", String.valueOf( contentResultSet.getLength() ) );
        sectionsEl.setAttribute( "contentinsectioncount", String.valueOf( totalCount ) );

        return XMLDocumentFactory.create( new Document( sectionsEl ) );
    }

    private List<Section> getUniqueSectionsSorted( ContentResultSet contentResultSet )
    {
        Set<Section> uniqueSectionNames = new HashSet<Section>();
        for ( ContentEntity content : contentResultSet.getContents() )
        {
            ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
            contentLocationSpecification.setIncludeInactiveLocationsInSection( true );

            final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );
            for ( ContentLocation contentLocation : contentLocations.getAllLocations() )
            {
                if ( contentLocation.isInSection() )
                {
                    SiteEntity siteEntity = siteDao.findByKey( contentLocation.getSiteKey().toInt() );
                    uniqueSectionNames.add(
                        new Section( contentLocation.getSiteKey(), contentLocation.getMenuItemKey(), contentLocation.getMenuItemName(),
                                     contentLocation.getMenuItemPathAsString(), siteEntity.getName() ) );
                }

            }
        }

        Section[] sectionNameArray = new Section[uniqueSectionNames.size()];
        uniqueSectionNames.toArray( sectionNameArray );
        List<Section> sectionNameList = Arrays.asList( sectionNameArray );

        Collections.sort( sectionNameList, new CaseInsensitiveSectionComparator() );
        return sectionNameList;
    }

}

class CaseInsensitiveSectionComparator
    implements Comparator<Section>
{

    /**
     * {@inheritDoc}
     *
     * @see java.util.Comparator#compare(Object, Object)
     */
    public int compare( Section s1, Section s2 )
    {
        return s1.compareTo( s2 );
    }

}

class SiteKeyComparator
    implements Comparator<Section>
{

    public int compare( Section s1, Section s2 )
    {
        return s1.compareTo( s2 );
    }

}

class Section
    implements Comparable<com.enonic.cms.core.structure.Section>
{

    private SiteKey siteKey;

    private MenuItemKey menuItemKey;

    private String menuItemName;

    private String menuItemPath;

    private String siteName;

    public Section( SiteKey siteKey, MenuItemKey menuItemKey, String menuItemName, String menuItemPathAsString, String siteName )
    {
        this.menuItemKey = menuItemKey;
        this.siteKey = siteKey;
        this.menuItemName = menuItemName;
        this.menuItemPath = menuItemPathAsString;
        this.siteName = siteName;
    }

    public int compareTo( com.enonic.cms.core.structure.Section o )
    {

        // return menuItemName.compareToIgnoreCase( o.getMenuItemName() );

        String s1path = this.siteName + this.menuItemPath;
        String s2path = o.getSiteName() + o.getMenuItemPath();

        return s1path.compareToIgnoreCase( s2path );

    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public MenuItemKey getMenuItemKey()
    {
        return menuItemKey;
    }

    public String getMenuItemName()
    {
        return menuItemName;
    }

    public String getMenuItemPath()
    {
        return menuItemPath;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        com.enonic.cms.core.structure.Section s = (com.enonic.cms.core.structure.Section) o;

        if ( this.siteKey.equals( s.getSiteKey() ) && this.menuItemPath.equalsIgnoreCase( s.getMenuItemPath() ) )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    @Override
    public int hashCode()
    {
        return menuItemName.hashCode() + siteKey.hashCode();
    }

    public String getSiteName()
    {
        return siteName;
    }

}
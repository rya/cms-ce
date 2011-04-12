package com.enonic.cms.core.structure.menuitem;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.domain.CacheSettings;
import com.enonic.cms.domain.CaseInsensitiveString;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.Path;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Dulko
 * Date: 12.4.11
 * Time: 12.16
 * To change this template use File | Settings | File Templates.
 */
public class MenuItemEntity
    implements Serializable
{
    private int key;

    private String name;

    private String displayName;

    private int order;

    private Date timestamp;

    private String menuName;

    private Boolean hidden;

    private String description;

    private Boolean noAuth;

    private LazyInitializedJDOMDocument xmlData;

    private String keywords;

    private SiteEntity site;

    private MenuItemType menuItemType;

    private MenuItemEntity parent;

    private String url;

    private Boolean openNewWindowForURL;

    private PageEntity page;

    private UserEntity owner;

    private UserEntity modifier;

    private LanguageEntity language;

    private Boolean shortcutForward;

    private MenuItemEntity menuItemShortcut;

    private Map<CaseInsensitiveString, MenuItemEntity> childrenMapByName = new LinkedHashMap<CaseInsensitiveString, MenuItemEntity>();

    private Boolean section;

    private Boolean orderedSection;

    private RunAsType runAs;

    private Set<ContentTypeEntity> contentTypeFilter;

    private Set<SectionContentEntity> sectionContents;

    private transient MenuItemData menuItemData;

    /**
     * PS: It is expected only one content in the set.
     */
    private Set<ContentEntity> contents = new LinkedHashSet<ContentEntity>();

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "UnusedDeclaration"})
    private Map<GroupKey, MenuItemAccessEntity> accesses;

    /**
     * For internal caching of the xml data document.
     */
    private transient Document xmlDataAsJDOMDocument;

    /**
     * Default constructor.
     */
    public MenuItemEntity()
    {
    }

    /**
     * Constructor that creates a new instance as a copy of another menu item.
     *
     * @param menuItem The menu item to copy.
     */
    public MenuItemEntity( MenuItemEntity menuItem )
    {
        this();

        key = menuItem.key;
        name = menuItem.name;
        order = menuItem.order;
        timestamp = menuItem.timestamp;
        menuName = menuItem.menuName;
        hidden = menuItem.hidden;
        description = menuItem.description;
        noAuth = menuItem.noAuth;
        xmlData = (LazyInitializedJDOMDocument) menuItem.xmlData.clone();
        keywords = menuItem.keywords;
        site = menuItem.site;
        menuItemType = menuItem.menuItemType;
        parent = menuItem.parent;
        url = menuItem.url;
        openNewWindowForURL = menuItem.openNewWindowForURL;
        page = menuItem.page;
        owner = menuItem.owner;
        modifier = menuItem.modifier;
        language = menuItem.language;
        shortcutForward = menuItem.shortcutForward;
        menuItemShortcut = menuItem.menuItemShortcut;
        childrenMapByName = menuItem.childrenMapByName;
        section = menuItem.section;
        orderedSection = menuItem.orderedSection;
        runAs = menuItem.runAs;
        contentTypeFilter = menuItem.contentTypeFilter;
        sectionContents = menuItem.sectionContents;

        // no defensive copies are created here, since
        // there are no mutable object fields (String is immutable)
    }

    public int getKey()
    {
        return key;
    }

    public MenuItemKey getMenuItemKey()
    {
        return new MenuItemKey( getKey() );
    }

    public String getName()
    {
        return name;
    }

    public int getOrder()
    {
        return order;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public String getMenuName()
    {
        return menuName;
    }

    public Boolean getHidden()
    {
        return hidden;
    }

    public Boolean showInMenu()
    {
        return !hidden;
    }

    public String getDescription()
    {
        return description;
    }

    public Boolean getNoAuth()
    {
        return noAuth;
    }

    public boolean hasXmlData()
    {
        return xmlData != null;
    }

    public Document getXmlDataAsClonedJDomDocument()
    {
        if ( !hasXmlData() )
        {
            return null;
        }

        if ( xmlDataAsJDOMDocument == null )
        {
            xmlDataAsJDOMDocument = xmlData.getDocument();
        }

        return (Document) xmlDataAsJDOMDocument.clone();
    }

    public String getKeywords()
    {
        return keywords;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public MenuItemType getType()
    {
        return menuItemType;
    }

    public MenuItemEntity getParent()
    {
        return parent;
    }

    public String getUrl()
    {
        return url;
    }

    public boolean isOpenNewWindowForURL()
    {
        return openNewWindowForURL;
    }

    public PageEntity getPage()
    {
        return page;
    }

    public UserEntity getOwner()
    {
        return owner;
    }

    public UserEntity getModifier()
    {
        return modifier;
    }

    public LanguageEntity getLanguage()
    {
        return language;
    }

    public MenuItemEntity getMenuItemShortcut()
    {
        return menuItemShortcut;
    }

    public Boolean isShortcutForward()
    {
        return shortcutForward;
    }

    public boolean isSection()
    {
        return ( section != null ) && section;
    }

    public Boolean isOrderedSection()
    {
        return orderedSection;
    }

    public RunAsType getRunAs()
    {
        return runAs;
    }

    public Set<ContentTypeEntity> getContentTypeFilter()
    {
        return contentTypeFilter;
    }

    public void setContentTypeFilter( Set<ContentTypeEntity> contentTypeFilter )
    {
        this.contentTypeFilter = contentTypeFilter;
    }

    public Set<SectionContentEntity> getSectionContents()
    {
        return sectionContents;
    }

    public void setSectionContent( Set<SectionContentEntity> entities )
    {
        sectionContents = entities;
    }

    /**
     * @return the children of this menu item in correct order.
     */
    public Collection<MenuItemEntity> getChildren()
    {
        return Collections.unmodifiableCollection( childrenMapByName.values() );
    }

    /**
     * Returns the descendants of this menu item to the given number of levels.
     *
     * @param levels The number of levels to go down recursively. A negative value, or a value of <code>0</code> will return an empty
     *               <code>List</code>.
     * @return Descendants of this menu item.
     */
    public List<MenuItemEntity> getDescendants( int levels )
    {

        List<MenuItemEntity> descendants = new ArrayList<MenuItemEntity>();
        if ( levels > 0 )
        {
            doAddDescendantsRecursively( this, levels, descendants );
        }
        return descendants;
    }

    private void doAddDescendantsRecursively( MenuItemEntity parentMenuItem, int levels, List<MenuItemEntity> descendants )
    {

        if ( levels == 0 )
        {
            return;
        }

        Collection<MenuItemEntity> children = parentMenuItem.getChildren();
        if ( children == null )
        {
            return;
        }

        for ( MenuItemEntity child : children )
        {
            descendants.add( child );
            doAddDescendantsRecursively( child, levels - 1, descendants );
        }
    }

    public List<MenuItemEntity> getDescendantSections( int levels )
    {

        List<MenuItemEntity> descendantSections = new ArrayList<MenuItemEntity>();

        List<MenuItemEntity> descendantMenuItems = getDescendants( levels );
        for ( MenuItemEntity menuItem : descendantMenuItems )
        {
            if ( menuItem.isSection() )
            {
                descendantSections.add( menuItem );
            }
        }

        return descendantSections;
    }

    public void setKey( int key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setOrder( int order )
    {
        this.order = order;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setMenuName( String menuName )
    {
        this.menuName = menuName;
    }

    public void setHidden( Boolean hidden )
    {
        this.hidden = hidden;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setNoAuth( Boolean noAuth )
    {
        this.noAuth = noAuth;
    }

    public void setKeywords( String keywords )
    {
        this.keywords = keywords;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public void setType( MenuItemType type )
    {
        menuItemType = type;
    }

    public void setParent( MenuItemEntity parent )
    {
        this.parent = parent;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public void setOpenNewWindowForURL( boolean b )
    {
        openNewWindowForURL = b;
    }

    public void setPage( PageEntity page )
    {
        this.page = page;
    }

    public void setOwner( UserEntity owner )
    {
        this.owner = owner;
    }

    public void setModifier( UserEntity modifier )
    {
        this.modifier = modifier;
    }

    public void setLanguage( LanguageEntity language )
    {
        this.language = language;
    }

    public void setMenuItemShortcut( MenuItemEntity menuItemShortcut )
    {
        this.menuItemShortcut = menuItemShortcut;
    }

    public void setShortcutForward( Boolean shortcutForward )
    {
        this.shortcutForward = shortcutForward;
    }

    public void setSection( Boolean s )
    {
        if ( s == null )
        {
            section = false;
        }
        else
        {
            section = s;
        }
        if ( section )
        {
            if ( orderedSection == null )
            {
                orderedSection = false;
            }
        }
        else
        {
            orderedSection = null;
        }
    }

    public void setOrderedSection( Boolean ordered )
    {
        orderedSection = ordered;
    }

    public void setRunAs( RunAsType runAs )
    {
        this.runAs = runAs;
    }

    public void setXmlData( Document value )
    {
        if ( value == null )
        {
            this.xmlData = null;
        }
        else
        {
            this.xmlData = LazyInitializedJDOMDocument.parse( value );
        }

        // Invalidate the cached JDOM Document
        this.xmlDataAsJDOMDocument = null;
        // Invalidate the cached MenuData
        this.menuItemData = null;
    }

    public void setContent( ContentEntity content )
    {
        this.contents.clear();
        this.contents.add( content );
    }

    public void addChild( MenuItemEntity child )
    {
        CaseInsensitiveString childName = new CaseInsensitiveString( child.getName() );
        if ( childrenMapByName.containsKey( childName ) )
        {
            throw new IllegalArgumentException( "Menu item already exist." );
        }
        this.childrenMapByName.put( childName, child );
    }

    public ContentEntity getContent()
    {
        if ( contents == null || contents.isEmpty() )
        {
            return null;
        }

        if ( contents.size() > 1 )
        {
            throw new IllegalStateException( "Unexpected number of contents (" + contents.size() + ") for menu item: " + getKey() );
        }

        return contents.iterator().next();
    }

    public boolean isRootPage()
    {
        return this.equals( getSite().getFrontPage() );
    }

    public boolean isErrorPage()
    {
        return this.equals( getSite().getErrorPage() );
    }

    public boolean isLoginPage()
    {
        return this.equals( getSite().getLoginPage() );
    }

    /**
     * @return the level of the menu item. Counted from zero at top level.
     */
    public int getLevel()
    {

        int level = 0;
        MenuItemEntity current = this;
        while ( current.getParent() != null )
        {
            current = current.getParent();
            level++;
        }

        return level;
    }

    /**
     * @return The breadcrumbspath of this menu item, with the top level parent as index 0.
     */
    public List<MenuItemEntity> getMenuItemPath()
    {
        List<MenuItemEntity> path = new ArrayList<MenuItemEntity>();
        addPath( path );
        return Collections.unmodifiableList( path );
    }

    private void addPath( List<MenuItemEntity> path )
    {
        MenuItemEntity parent = getParent();
        if ( parent != null )
        {
            parent.addPath( path );
        }
        path.add( this );
    }

    /**
     * @return the path (from top level) of this menu item.
     */
    public Path getPath()
    {

        return new Path( getPathAsStringCollection(), true );
    }

    /**
     * @return the path (from top level) as a collection of strings.
     */
    public Collection<String> getPathAsStringCollection()
    {

        List<String> pathElements = new ArrayList<String>();

        MenuItemEntity curr = this;
        do
        {
            pathElements.add( curr.getName() );
            curr = curr.getParent();
        }
        while ( curr != null );

        Collections.reverse( pathElements );
        return pathElements;
    }

    /**
     * @return the path (from top level) of this menu item.
     */
    public String getPathAsString()
    {

        List<MenuItemEntity> menuItems = getMenuItemPath();
        StringBuffer pathString = new StringBuffer( 25 * menuItems.size() );
        pathString.append( "/" );
        for ( int i = 0; i < menuItems.size(); i++ )
        {
            MenuItemEntity mi = menuItems.get( i );
            pathString.append( mi.getName() );
            if ( i < menuItems.size() - 1 )
            {
                pathString.append( "/" );
            }
        }
        return pathString.toString();
    }

    /**
     * Finds and returns a above parent of this menu item at the given level.
     *
     * @param level The number of levels up to the parent.
     * @return The specified parent.
     */
    public MenuItemEntity getParentAtLevel( int level )
    {

        if ( level < 0 )
        {
            throw new IllegalArgumentException( "Given level must be zero or more: " + level );
        }

        List<MenuItemEntity> path = getMenuItemPath();
        if ( level >= path.size() - 1 )
        {
            throw new IllegalArgumentException( "Given level must be below this menu items's level:" + level );
        }
        return path.get( level );
    }

    public MenuItemEntity getTopLevelMenuItem()
    {

        MenuItemEntity current = this;
        while ( current.getParent() != null )
        {
            current = current.getParent();
        }

        return current;
    }

    public boolean isAtTopLevel()
    {
        return getParent() == null;
    }

    public MenuItemEntity getChildByName( String name )
    {
        return childrenMapByName.get( new CaseInsensitiveString( name ) );
    }

    public MenuItemAccessEntity getAccess( GroupKey groupKey )
    {
        return accesses.get( groupKey );
    }

    /**
     * NB! This method will not check access thru group´s memberhips.
     */
    public boolean hasAccess( GroupEntity group, MenuItemAccessType typeOfAccess )
    {

        if ( group == null )
        {
            throw new IllegalArgumentException( "Given group cannot be null" );
        }

        MenuItemAccessEntity menuItemAccess = accesses.get( group.getGroupKey() );
        if ( menuItemAccess == null )
        {
            return false;
        }
        switch ( typeOfAccess )
        {
            case READ:
                return menuItemAccess.isReadAccess();
            case ADD:
                return menuItemAccess.isAddAccess();
            case ADMINISTRATE:
                return menuItemAccess.isAdminAccess();
            case CREATE:
                return menuItemAccess.isCreateAccess();
            case DELETE:
                return menuItemAccess.isDeleteAccess();
            case PUBLISH:
                return menuItemAccess.isPublishAccess();
            case UPDATE:
                return menuItemAccess.isUpdateAccess();
        }
        return false;
    }

    public CacheSettings getCacheSettings( int defaultSecondsToLive, PageTemplateEntity pageTemplate )
    {

        if ( !hasXmlData() )
        {
            return new CacheSettings( false, CacheSettings.TYPE_DEFAULT, 0 );
        }

        Element dataEl = getXmlDataAsClonedJDomDocument().getRootElement();
        String cachedisabledString = dataEl.getAttributeValue( "cachedisabled" );
        String cachetypeString = dataEl.getAttributeValue( "cachetype" );
        String mincachetimeString = dataEl.getAttributeValue( "mincachetime", String.valueOf( defaultSecondsToLive ) );
        int secondsToLive = Integer.valueOf( mincachetimeString );
        boolean cacheMenuItem = !Boolean.valueOf( cachedisabledString ) && pageTemplate.getDatasources().isCacheable();
        return new CacheSettings( cacheMenuItem, cachetypeString, secondsToLive );
    }

    public Element getDocumentElementAsClonedJDOMElement()
    {
        if ( !hasXmlData() )
        {
            return null;
        }

        Element dataEl = getXmlDataAsClonedJDomDocument().getRootElement();
        Element documentEl = dataEl.getChild( "document" );
        if ( documentEl == null )
        {
            return null;
        }
        return (Element) documentEl.detach();
    }

    public boolean isParentOf( MenuItemEntity potentialChild )
    {
        return potentialChild.hasAsParent( this );
    }

    public boolean hasAsParent( MenuItemEntity potentialParent )
    {

        MenuItemEntity currentParent = this.getParent();

        while ( currentParent != null )
        {
            if ( potentialParent.getKey() == currentParent.getKey() )
            {
                return true;
            }
            currentParent = currentParent.getParent();
        }

        return false;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof MenuItemEntity) )
        {
            return false;
        }

        MenuItemEntity that = (MenuItemEntity) o;

        return key == that.getKey();

    }

    public int hashCode()
    {
        return new HashCodeBuilder( 143, 631 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "key=" ).append( key ).append( ", name='" ).append( getName() ).append( "'" );
        return s.toString();
    }

    public UserEntity resolveRunAsUser( UserEntity currentUser, boolean doFirstLevelCheckOnPageTemplate )
    {
        if ( currentUser.isAnonymous() )
        {
            // Anonymous user cannot run as any other user
            return currentUser;
        }

        RunAsType runAsType = getRunAs();

        if ( runAsType.equals( RunAsType.PERSONALIZED ) )
        {
            return currentUser;
        }
        else if ( runAsType.equals( RunAsType.DEFAULT_USER ) )
        {
            if ( getSite().resolveDefaultRunAsUser() != null )
            {
                return getSite().resolveDefaultRunAsUser();
            }
            return null;
        }
        else if ( runAsType.equals( RunAsType.INHERIT ) )
        {
            if ( doFirstLevelCheckOnPageTemplate && getPage() != null )
            {
                PageTemplateEntity pageTemplate = getPage().getTemplate();
                if ( pageTemplate != null )
                {
                    UserEntity runAsUser = pageTemplate.resolveRunAsUser( currentUser );
                    if ( runAsUser != null )
                    {
                        return runAsUser;
                    }
                }
            }

            MenuItemEntity parent = getParent();
            if ( parent != null )
            {
                return parent.resolveRunAsUser( currentUser, false );
            }
            else
            {
                UserEntity defaultRunAsUser = getSite().resolveDefaultRunAsUser();
                return defaultRunAsUser != null ? defaultRunAsUser : currentUser;
            }
        }
        else
        {
            throw new IllegalArgumentException( "Unsopported runAsType: " + runAsType );
        }
    }

    public MenuItemEntity getClosestParentThatIsNotSection()
    {
        MenuItemEntity parent = getParent();
        if ( parent == null )
        {
            return null;
        }

        while ( parent != null && parent.getType() == MenuItemType.SECTION )
        {
            parent = parent.getParent();
        }
        return parent;
    }

    private MenuItemData getMenuItemData()
    {
        if ( menuItemData == null )
        {
            if ( xmlData != null )
            {
                menuItemData = new MenuItemData( this.getXmlDataAsClonedJDomDocument() );
            }
            else
            {
                menuItemData = new MenuItemData();
            }
        }
        return menuItemData;
    }

    public void removeRequestParameters()
    {
        MenuItemData data = getMenuItemData();
        data.removeRequestParameters();
        setXmlData( data.getJDOMDocument() );
    }

    public void addRequestParameter( final String name, final String value, final String override )
    {
        MenuItemData data = getMenuItemData();
        data.addRequestParameter( name, value, override );
        setXmlData( data.getJDOMDocument() );
    }

    public Map<String, MenuItemRequestParameter> getRequestParameters()
    {
        return getMenuItemData().getRequestParameters();
    }

    public MenuItemRequestParameter getRequestParameter( final String name )
    {
        return getMenuItemData().getRequestParameter( name );
    }

    public String getRequestParameterValue( final String name )
    {
        MenuItemRequestParameter menuItemRequestParameter = getMenuItemData().getRequestParameter( name );
        if ( menuItemRequestParameter == null )
        {
            return null;
        }
        return menuItemRequestParameter.getValue();
    }

    public Document getMenuDataJDOMDocument()
    {
        return getMenuItemData().getJDOMDocument();
    }

    public Boolean getCacheDisabled()
    {
        return getMenuItemData().getCacheDisabled();
    }

    public String getCacheType()
    {
        return getMenuItemData().getCacheType();
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    /**
     * This method should only be called on section type menu items.
     *
     * @return The timestamp of the last time the section content was updated.  If the section has no content, <code>new Date(0)</code> will be returned.
     */
    public Date getLastUpdatedSectionContentTimestamp()
    {
        assert isSection() : "This method is only valid for section type menu items.";
        Date newest = new Date( 0 );

        for ( SectionContentEntity sectionContent : sectionContents )
        {
            if ( sectionContent.getTimestamp().after( newest ) )
            {
                newest = sectionContent.getTimestamp();
            }
        }

        return newest;
    }

    public SectionContentEntity removeSectionContent( ContentKey contentKey )
    {
        assert isSection() : "This method is only valid for section type menu items.";
        SectionContentEntity sectionContentToRemove = null;

        for ( SectionContentEntity sectionContent : sectionContents )
        {
            if ( sectionContent.getContent().getKey().equals( contentKey ) )
            {
                sectionContentToRemove = sectionContent;
                break;
            }
        }

        if ( sectionContentToRemove == null )
        {
            return null;
        }
        else
        {
            sectionContents.remove( sectionContentToRemove );
            return sectionContentToRemove;
        }
    }

    public SectionContentEntity getSectionContent( ContentKey contentKey )
    {
        assert isSection() : "This method is only valid for section type menu items.";
        for ( SectionContentEntity sectionContent : sectionContents )
        {
            if ( sectionContent.getContent().getKey().equals( contentKey ) )
            {
                return sectionContent;
            }
        }
        return null;
    }
}

class MenuItemData
{
    private static final String ROOT_ELEMENT = "data";

    private static final String ATTRIBUTE_CACHE_DISABLED = "cachedisabled";

    private static final String ATTRIBUTE_CACHE_TYPE = "cachetype";

    private Document xmlDoc;

    public MenuItemData( Document xmlDoc )
    {
        this.xmlDoc = xmlDoc;
    }

    public MenuItemData()
    {
        xmlDoc = new Document();
        xmlDoc.addContent( new Element( ROOT_ELEMENT ) );
    }

    public Boolean getCacheDisabled()
    {
        Element element = getAndEnsureRootElement();

        String cacheDisabled = element.getAttributeValue( ATTRIBUTE_CACHE_DISABLED );

        if ( cacheDisabled == null || cacheDisabled.length() == 0 )
        {
            return null;
        }
        return Boolean.valueOf( cacheDisabled );
    }

    public void setCacheDisabled( boolean disabled )
    {
        Element element = getAndEnsureRootElement();
        Attribute cacheDisabled = element.getAttribute( ATTRIBUTE_CACHE_DISABLED );
        if ( cacheDisabled != null )
        {
            cacheDisabled.setValue( Boolean.toString( disabled ) );
        }
        else
        {
            element.setAttribute( ATTRIBUTE_CACHE_DISABLED, Boolean.toString( disabled ) );
        }
    }

    public String getCacheType()
    {
        Element element = getAndEnsureRootElement();

        String cacheType = element.getAttributeValue( ATTRIBUTE_CACHE_TYPE );

        if ( cacheType == null || cacheType.length() == 0 )
        {
            return null;
        }
        return cacheType;
    }

    public void setCacheType( String type )
    {
        Element element = getAndEnsureRootElement();
        Attribute cacheType = element.getAttribute( ATTRIBUTE_CACHE_TYPE );
        if ( cacheType != null )
        {
            cacheType.setValue( type );
        }
        else
        {
            element.setAttribute( ATTRIBUTE_CACHE_TYPE, type );
        }
    }

    public void addRequestParameter( final String name, final String value, final String override )
    {
        Element rootEl = getAndEnsureRootElement();
        Element parametersEl = getAndEnsureElement( rootEl, "parameters" );
        Element parameterEl = new Element( "parameter" );

        parameterEl.setText( value );
        parameterEl.setAttribute( "name", name );
        if ( override != null )
        {
            parameterEl.setAttribute( "override", override );
        }

        parametersEl.addContent( parameterEl );
    }

    public Map<String, MenuItemRequestParameter> getRequestParameters()
    {
        Element rootEl = getAndEnsureRootElement();
        Element parametersEl = getAndEnsureElement( rootEl, "parameters" );
        @SuppressWarnings({"unchecked"}) List<Element> children = parametersEl.getChildren( "parameter" );

        Map<String, MenuItemRequestParameter> parametersByName = new LinkedHashMap<String, MenuItemRequestParameter>();

        for ( Element element : children )
        {
            String name = element.getAttributeValue( "name" );
            String value = element.getText();
            String override = element.getAttributeValue( "override" );

            if ( name != null )
            {
                MenuItemRequestParameter parameter = new MenuItemRequestParameter( name, value, override );
                parametersByName.put( name, parameter );
            }
        }

        return parametersByName;
    }

    public MenuItemRequestParameter getRequestParameter( final String name )
    {
        return getRequestParameters().get( name );
    }

    public void removeRequestParameters()
    {
        Element rootEl = getAndEnsureRootElement();
        Element parametersEl = getAndEnsureElement( rootEl, "parameters" );
        rootEl.removeContent( parametersEl );
    }

    public Set<String> getAllowedPageTypes()
    {
        Element rootEl = getAndEnsureRootElement();
        Element pageTypesEl = getAndEnsureElement( rootEl, "pagetypes" );

        Set<String> pageTypes = new HashSet<String>();

        @SuppressWarnings({"unchecked"}) List<Element> childrenEl = pageTypesEl.getChildren( "allow" );
        for ( Element allowEl : childrenEl )
        {
            pageTypes.add( allowEl.getAttributeValue( "type" ) );
        }

        return Collections.unmodifiableSet( pageTypes );
    }

    private Element getAndEnsureElement( Element parentEl, String childName )
    {
        Element child = parentEl.getChild( childName );
        if ( child == null )
        {
            child = new Element( childName );
            parentEl.addContent( child );
        }

        return child;
    }

    private Element getAndEnsureRootElement()
    {
        Element rootEl;
        if ( xmlDoc == null )
        {
            xmlDoc = new Document();
            rootEl = new Element( ROOT_ELEMENT );
            xmlDoc.setRootElement( rootEl );
        }
        rootEl = xmlDoc.getRootElement();
        return rootEl;
    }

    public byte[] getAsBytes()
    {

        XMLDocument xmlDocument = XMLDocumentFactory.create(xmlDoc);
        try
        {
            return xmlDocument.getAsString().getBytes( "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( "Failed to get as bytes: ", e );
        }
    }

    public Document getJDOMDocument()
    {
        return xmlDoc;
    }

}
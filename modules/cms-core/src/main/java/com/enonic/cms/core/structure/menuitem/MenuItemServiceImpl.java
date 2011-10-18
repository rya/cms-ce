package com.enonic.cms.core.structure.menuitem;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryAccessException;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentHomeDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.SectionContentDao;
import com.enonic.cms.store.dao.UserDao;


@Component("menuItemService")
public class MenuItemServiceImpl
    implements MenuItemService
{
    private final static int ORDER_SPACE = 1000;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private SectionContentDao sectionContentDao;

    @Autowired
    private PageTemplateDao pageTemplateDao;

    @Autowired
    private ContentHomeDao contentHomeDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private UserDao userDao;

    private TimeService timeService;

    /**
     * <p>reads page key by menu path or all keys in folder </p>
     * <p>path may be absolute or relative format</p>
     * <p/>
     * Examples: <br/>
     * <p/>
     * <code>/</code> - all keys in root folder<br/>
     * <code>./</code> - all keys in current folder<br/>
     * <code>fldr</code> - key of fldr folder in current folder<br/>
     * <code>./fldr</code> - key of fldr folder in current folder<br/>
     * <code>../welcome/fldr</code> - more relative path<br/>
     * <code>../././welcome/./fldr/../fldr</code> - complex path<br/>
     * <p/>
     * if parent folder or relative folder does not exist function returns empty string
     *
     * @param menuItemEntity current menu item
     * @param path           menu path to section/page
     * @return comma separated keys of items in folder or empty
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String getPageKeyByPath( MenuItemEntity menuItemEntity, String path )
    {
        final String SEPARATOR = "/";

        MenuItemEntity currentItemEntity = null;
        Collection<MenuItemEntity> itemEntityList;

        boolean isFolder = path.endsWith( SEPARATOR );
        String[] parts = path.split( SEPARATOR ); // split works strange. "////" will return ZERO parts !

        MenuItemSpecification menuItemSpecification = new MenuItemSpecification();
        menuItemSpecification.setSiteKey( menuItemEntity.getSite().getKey() );

        if ( parts.length == 0 || "".equals( parts[0] ) )
        { //  absolute path in format /root/folder
            menuItemSpecification.setRootLevelOnly( true );
            itemEntityList = menuItemDao.findBySpecification( menuItemSpecification );
        }
        else
        { // relative path in format ./relative/path or just relative/path
            // get fresh copy of current menu item from hibernate cache
            currentItemEntity = menuItemDao.findByKey( menuItemEntity.getKey() );
            itemEntityList = currentItemEntity.getChildren();
        }

        searching:
        for ( int num = 0; num < parts.length; num++ )
        {
            String part = parts[num];

            if ( ".".equals( part ) || "".equals( part ) )
            {
                // nothing to do with . and empty parts
            }

            else if ( "..".equals( part ) )
            {
                // check if system is trying go up to /
                if ( currentItemEntity == null ) // already root
                {
                    isFolder = false; // also do not show content of root folder
                    break; // searching
                }

                // go up
                currentItemEntity = currentItemEntity.getParent();

                // read items entity list
                if ( currentItemEntity == null )
                { // read contents of root folder
                    menuItemSpecification.setRootLevelOnly( true );
                    itemEntityList = menuItemDao.findBySpecification( menuItemSpecification );
                }
                else
                { // just enter folder
                    itemEntityList = currentItemEntity.getChildren();
                }
            }

            else

            {
                // something other than . or .. here
                for ( MenuItemEntity itemEntity : itemEntityList )
                {
                    if ( part.equals( itemEntity.getName() ) )
                    {
                        if ( num == parts.length - 1 )
                        { // found
                            if ( isFolder )
                            {
                                itemEntityList = itemEntity.getChildren();
                            }
                            else
                            {
                                currentItemEntity = itemEntity;
                            }

                            // go build result string
                            break searching;
                        }
                        else
                        {
                            currentItemEntity = itemEntity;
                            itemEntityList = currentItemEntity.getChildren();
                            continue searching;
                        }
                    }
                }

                // did not find matching name in current folder
                currentItemEntity = null;
                break;
            }
        }

        String result = "";

        if ( isFolder )
        {
            String separator = "";
            for ( MenuItemEntity itemEntity : itemEntityList )
            {
                result = result + separator + itemEntity.getKey();
                separator = ",";
            }
        }
        else if ( currentItemEntity != null )
        {
            result = "" + currentItemEntity.getKey();
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void execute( final MenuItemServiceCommand... commands )
    {
        for ( final MenuItemServiceCommand command : commands )
        {
            if ( command instanceof SetContentHomeCommand )
            {
                doExecuteSetContentHomeCommand( (SetContentHomeCommand) command );
            }
            else if ( command instanceof AddContentToSectionCommand )
            {
                doExecuteAddContentToSectionCommand( (AddContentToSectionCommand) command );
            }
            else if ( command instanceof RemoveContentsFromSectionCommand )
            {
                doExecuteRemoveContentsFromSectionCommand( (RemoveContentsFromSectionCommand) command );
            }
            else if ( command instanceof ApproveContentInSectionCommand )
            {
                doExecuteApproveContentInSectionCommand( (ApproveContentInSectionCommand) command );
            }
            else if ( command instanceof ApproveContentsInSectionCommand )
            {
                doExecuteApproveContentsInSectionCommand( (ApproveContentsInSectionCommand) command );
            }
            else if ( command instanceof UnapproveContentsInSectionCommand )
            {
                doExecuteUnapproveContentsInSectionCommand( (UnapproveContentsInSectionCommand) command );
            }
            else
            {
                throw new UnsupportedOperationException( "Unsupported menu-item service command: " + command.getClass().getName() );
            }

            sectionContentDao.getHibernateTemplate().flush();
        }
    }

    private void doExecuteSetContentHomeCommand( final SetContentHomeCommand command )
    {
        Preconditions.checkNotNull( command.getSetter(), "setter cannot be null" );
        Preconditions.checkNotNull( command.getContent(), "content to set home for cannot be null" );
        Preconditions.checkNotNull( command.getSection(), "section to set home to cannot be null" );

        final UserEntity setter = doResolveUser( command.getSetter(), "setter" );
        final ContentEntity content = contentDao.findByKey( command.getContent() );
        final MenuItemEntity section = doResolveSection( command.getSection() );

        final CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        if ( !categoryAccessResolver.hasApproveContentAccess( setter, content.getCategory() ) )
        {
            throw new CategoryAccessException( "Cannot set content home", setter.getQualifiedName(), CategoryAccessType.APPROVE,
                                               content.getCategory().getKey() );
        }

        PageTemplateEntity pageTemplate = null;
        if ( command.getPageTemplate() != null )
        {
            pageTemplate = pageTemplateDao.findByKey( command.getPageTemplate().toInt() );
        }

        final ContentHomeEntity existingHome = content.getContentHome( section.getSite().getKey() );
        if ( existingHome != null )
        {
            existingHome.setMenuItem( section );
            existingHome.setPageTemplate( pageTemplate );
        }
        else
        {
            final ContentHomeEntity newContentHome = new ContentHomeEntity();
            newContentHome.setKey( new ContentHomeKey( section.getSite().getKey(), content.getKey() ) );
            newContentHome.setSite( section.getSite() );
            newContentHome.setContent( content );
            newContentHome.setMenuItem( section );
            newContentHome.setPageTemplate( pageTemplate );
            contentHomeDao.storeNew( newContentHome );

            content.addContentHome( newContentHome );
        }
    }

    private void doExecuteAddContentToSectionCommand( final AddContentToSectionCommand command )
    {
        Preconditions.checkNotNull( command, "a command is required" );
        Preconditions.checkNotNull( command.getContributor(), "the command's contributor argument is required" );
        Preconditions.checkNotNull( command.getContent(), "the command's content argument is required" );
        Preconditions.checkNotNull( command.getSection(), "the command's section argument is required" );
        if ( !command.isApproveInSection() )
        {
            Preconditions.checkArgument( !command.isAddOnTop(), "no point in adding content to top when not approving" );
        }

        final ContentEntity content = contentDao.findByKey( command.getContent() );
        Preconditions.checkNotNull( content, "content does not exist: " + command.getContent() );
        Preconditions.checkArgument( !content.isDeleted(), "content is deleted: " + command.getContent() );

        final MenuItemEntity menuItem = doResolveSection( command.getSection() );
        if ( !menuItem.isOrderedSection() )
        {
            Preconditions.checkArgument( command.getOrderContentsInSectionCommand() == null,
                                         "section is not ordered, did not expect to get order specified in command" );
        }

        final UserEntity contributor = doResolveUser( command.getContributor(), "contributor" );
        final MenuItemAccessResolver menuItemAccessResolver = new MenuItemAccessResolver( groupDao );
        menuItemAccessResolver.checkAccessToAddContentToSection( contributor, menuItem, "Cannot add content in section." );
        if ( command.isApproveInSection() )
        {
            menuItemAccessResolver.checkAccessToApproveContentInSection( contributor, menuItem, "Cannot approve content in section." );
        }

        if ( menuItem.getType() == MenuItemType.SECTION && menuItem.hasSectionContentTypeFilter() )
        {
            if ( !menuItem.supportsSectionContentType( content.getCategory().getContentType() ) )
            {
                throw new ContentTypeNotSupportedException( content.getCategory().getContentType(), menuItem );
            }
        }
        else if ( menuItem.getType() == MenuItemType.PAGE && menuItem.getPage().getTemplate().getContentTypes().size() > 0 )
        {
            if ( !menuItem.getPage().getTemplate().supportsContentType( content.getCategory().getContentType() ) )
            {
                throw new ContentTypeNotSupportedException( content.getCategory().getContentType(), menuItem );
            }
        }

        final SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setOrder( 0 );
        sectionContent.setContent( content );
        sectionContent.setMenuItem( menuItem );
        sectionContent.setApproved( command.isApproveInSection() );
        sectionContent.setTimestamp( timeService.getNowAsDateTime().toDate() );
        if ( command.isAddOnTop() && menuItem.isOrderedSection() )
        {
            sectionContent.setOrder( resolveOrderValueForInsertOnTopOfApprovedContentInSection( menuItem ) );
        }

        content.addSectionContent( sectionContent );
        sectionContentDao.getHibernateTemplate().flush();

        sectionContentDao.getHibernateTemplate().getSessionFactory().evictCollection( MenuItemEntity.class.getName() + ".sectionContents",
                                                                                      menuItem.getKey() );

        if ( menuItem.isOrderedSection() )
        {
            if ( command.getOrderContentsInSectionCommand() != null )
            {
                // ensure section will have it's newly added content
                sectionContentDao.getHibernateTemplate().refresh( menuItem );

                List<ContentKey> wantedOrder = command.getOrderContentsInSectionCommand().getWantedOrder();
                ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( wantedOrder, menuItem, ORDER_SPACE );
                orderer.order();
            }
        }

    }

    private void doExecuteRemoveContentsFromSectionCommand( final RemoveContentsFromSectionCommand command )
    {
        Preconditions.checkNotNull( command.getSection(), "section cannot be null" );
        Preconditions.checkNotNull( command.getRemover(), "remover cannot be null" );
        Preconditions.checkNotNull( command.getContentsToRemove(), "content to remove cannot be null" );

        final UserEntity remover = userDao.findByKey( command.getRemover() );
        final MenuItemEntity section = doResolveSection( command.getSection() );

        final MenuItemAccessResolver menuItemAccessResolver = new MenuItemAccessResolver( groupDao );
        for ( ContentKey contentKey : command.getContentsToRemove() )
        {
            final SectionContentEntity sectionContentToRemove = section.getSectionContent( contentKey );
            Preconditions.checkNotNull( sectionContentToRemove, "content in section (" + section.getKey() + ") not found: " + contentKey );

            final boolean contentIsApprovedInSection = sectionContentToRemove.isApproved();
            if ( contentIsApprovedInSection )
            {
                menuItemAccessResolver.checkAccessToUnapproveContentInSection( remover, section,
                                                                               "Cannot remove approved content from section." );
            }
            else
            {
                menuItemAccessResolver.checkAccessToRemoveUnapprovedContentFromSection( remover, section,
                                                                                        "Cannot remove unapproved content from section." );
            }

            final ContentEntity content = contentDao.findByKey( contentKey );
            content.removeSectionContent( command.getSection() );

            sectionContentDao.getHibernateTemplate().flush();

            sectionContentDao.getHibernateTemplate().getSessionFactory().evictCollection(
                MenuItemEntity.class.getName() + ".sectionContents", section.getKey() );

            removeContentHomeIfThisSectionIs( content, section );
        }
    }

    private void doExecuteApproveContentInSectionCommand( final ApproveContentInSectionCommand command )
    {
        Preconditions.checkNotNull( command.getSection(), "section cannot be null" );
        Preconditions.checkNotNull( command.getApprover(), "approver cannot be null" );
        Preconditions.checkNotNull( command.getContentToApprove(), "content to approve cannot be null" );

        final MenuItemEntity section = doResolveSection( command.getSection() );
        final UserEntity approver = doResolveUser( command.getApprover(), "approver" );

        new MenuItemAccessResolver( groupDao ).checkAccessToApproveContentInSection( approver, section,
                                                                                     "Cannot approve content in section." );

        final SectionContentEntity sectionContent = section.getSectionContent( command.getContentToApprove() );
        boolean changed = false;

        if ( !sectionContent.isApproved() )
        {
            sectionContent.setApproved( true );
            changed = true;
        }

        final int newOrder = resolveOrderValueForInsertOnTopOfApprovedContentInSection( section );
        if ( sectionContent.getOrder() != newOrder )
        {
            sectionContent.setOrder( newOrder );
            changed = true;
        }

        if ( changed )
        {
            sectionContent.setTimestamp( timeService.getNowAsDateTime().toDate() );
        }
    }

    private void doExecuteApproveContentsInSectionCommand( final ApproveContentsInSectionCommand command )
    {
        Preconditions.checkNotNull( command.getSection(), "section cannot be null" );
        Preconditions.checkNotNull( command.getApprover(), "approver cannot be null" );
        Preconditions.checkNotNull( command.getContentsToApprove().size(), "no given content to approve in section" );

        final MenuItemEntity section = doResolveSection( command.getSection() );
        final UserEntity approver = doResolveUser( command.getApprover(), "approver" );

        new MenuItemAccessResolver( groupDao ).checkAccessToApproveContentInSection( approver, section,
                                                                                     "Cannot approve content in section." );

        final Set<SectionContentEntity> changedSectionContents = new HashSet<SectionContentEntity>();
        for ( ContentKey contentKey : command.getContentsToApprove() )
        {
            final SectionContentEntity sectionContent = section.getSectionContent( contentKey );
            if ( !sectionContent.isApproved() )
            {
                sectionContent.setApproved( true );
                changedSectionContents.add( sectionContent );
            }
        }

        // handle re-order command
        if ( command.getOrderContentsInSectionCommand() != null )
        {
            if ( section.isOrderedSection() )
            {
                changedSectionContents.addAll(
                    new ContentsInSectionOrderer( command.getOrderContentsInSectionCommand().getWantedOrder(), section,
                                                  ORDER_SPACE ).order() );
            }
        }

        // update timestamp of only those who have changed
        for ( SectionContentEntity changedSectionContent : changedSectionContents )
        {
            changedSectionContent.setTimestamp( timeService.getNowAsDateTime().toDate() );
        }
    }

    private void doExecuteUnapproveContentsInSectionCommand( final UnapproveContentsInSectionCommand command )
    {
        Preconditions.checkNotNull( command.getSection(), "section cannot be null" );
        Preconditions.checkNotNull( command.getUnapprover(), "unapprover cannot be null" );
        Preconditions.checkNotNull( command.getContentToUnapprove().size(), "no given content to unapprove in section" );

        final UserEntity updater = doResolveUser( command.getUnapprover(), "unapprover" );
        final MenuItemEntity section = doResolveSection( command.getSection() );

        new MenuItemAccessResolver( groupDao ).checkAccessToUnapproveContentInSection( updater, section,
                                                                                       "Cannot unapprove section content." );

        for ( ContentKey contentKey : command.getContentToUnapprove() )
        {
            final SectionContentEntity sectionContent = section.getSectionContent( contentKey );
            if ( sectionContent == null )
            {
                continue;
            }
            doUnapproveContentInSection( sectionContent );
        }
    }

    private int resolveOrderValueForInsertOnTopOfApprovedContentInSection( final MenuItemEntity section )
    {
        if ( section.getSectionContents().size() == 0 )
        {
            return 0;
        }

        int lowestOrderValue = Integer.MAX_VALUE;
        for ( SectionContentEntity sectionContent : section.getSectionContents() )
        {
            if ( sectionContent.isApproved() && sectionContent.getOrder() < lowestOrderValue )
            {
                lowestOrderValue = sectionContent.getOrder();
            }
        }

        return lowestOrderValue - ORDER_SPACE;
    }


    private MenuItemEntity doResolveSection( final MenuItemKey sectionKey )
    {
        final MenuItemEntity section = menuItemDao.findByKey( sectionKey );
        Preconditions.checkNotNull( section, "section does not exist: " + sectionKey );
        Preconditions.checkArgument( section.isSection(), "menu item is not a section:" + sectionKey );
        return section;
    }

    private UserEntity doResolveUser( final UserKey userKey, final String subject )
    {
        final UserEntity user = userDao.findByKey( userKey );
        Preconditions.checkNotNull( user, subject + " does not exist: " + userKey );
        return user;
    }

    private void doUnapproveContentInSection( final SectionContentEntity sectionContent )
    {
        boolean changed = false;
        if ( sectionContent.isApproved() )
        {
            sectionContent.setApproved( false );
            changed = true;
        }
        if ( sectionContent.getOrder() != 0 )
        {
            sectionContent.setOrder( 0 );
            changed = true;
        }
        if ( changed )
        {
            sectionContent.setTimestamp( timeService.getNowAsDateTime().toDate() );
        }
    }

    private void removeContentHomeIfThisSectionIs( final ContentEntity content, final MenuItemEntity section )
    {
        final ContentHomeEntity contentHome =
            contentHomeDao.findByKey( new ContentHomeKey( section.getSite().getKey(), content.getKey() ) );

        if ( contentHome != null )
        {
            content.removeContentHome( section.getSite().getKey() );
            contentHomeDao.delete( contentHome );
        }

        contentHomeDao.getHibernateTemplate().getSessionFactory().evictCollection( ContentEntity.class.getName() + ".contentHomes",
                                                                                   content.getKey() );
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }
}

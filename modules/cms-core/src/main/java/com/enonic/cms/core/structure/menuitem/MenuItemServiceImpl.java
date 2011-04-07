/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import com.enonic.cms.domain.structure.menuitem.MenuItemSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SectionContentDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.structure.access.MenuItemAccessResolver;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.core.structure.menuitem.ApproveSectionContentCommand;
import com.enonic.cms.domain.core.structure.menuitem.MenuItemAccessException;
import com.enonic.cms.domain.core.structure.menuitem.RemoveContentFromSectionCommand;
import com.enonic.cms.domain.core.structure.menuitem.UnapproveSectionContentCommand;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessType;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.section.SectionContentEntity;

import java.util.Collection;

public class MenuItemServiceImpl
    implements MenuItemService
{
    @Autowired
    private GroupDao groupDao;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private SectionContentDao sectionContentDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer removeContentFromSection( RemoveContentFromSectionCommand command )
    {
        UserEntity remover = userDao.findByKey( command.getRemover() );
        MenuItemEntity section = menuItemDao.findByKey( command.getSection() );

        if ( !new MenuItemAccessResolver( groupDao ).hasAccess( remover, section, MenuItemAccessType.DELETE ) )
        {
            throw new MenuItemAccessException( "Cannot remove section content.", remover.getQualifiedName(), MenuItemAccessType.DELETE,
                                               command.getSection() );
        }

        for ( ContentKey contentKey : command.getContentToRemove() )
        {
            SectionContentEntity sectionContentToDelete = section.removeSectionContent( contentKey );
            sectionContentToDelete.getContent().removeSectionContent( command.getSection() );
            sectionContentDao.delete( sectionContentToDelete );
        }
        return command.getContentToRemove().size();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer approveSectionContent( ApproveSectionContentCommand command )
    {
        UserEntity updater = userDao.findByKey( command.getUpdater() );
        MenuItemEntity section = menuItemDao.findByKey( command.getSection() );

        if ( !new MenuItemAccessResolver( groupDao ).hasAccess( updater, section, MenuItemAccessType.PUBLISH ) )
        {
            throw new MenuItemAccessException( "Cannot publish section content.", updater.getQualifiedName(), MenuItemAccessType.PUBLISH,
                                               command.getSection() );
        }

        Integer contentOrder = 0;
        for ( ContentKey contentKey : command.getApprovedContentToUpdate() )
        {
            SectionContentEntity sectionContentToUpdate = section.getSectionContent( contentKey );
            if ( sectionContentToUpdate == null )
            {
                throw new IllegalStateException( "Attempting to update non-exiting content.  Please reload and try again." );
            }
            contentOrder++;
            sectionContentToUpdate.setOrder( contentOrder );
            sectionContentToUpdate.setApproved( true );
        }
        return command.getApprovedContentToUpdate().size();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer unapproveSectionContent( UnapproveSectionContentCommand command )
    {
        UserEntity updater = userDao.findByKey( command.getUpdater() );
        MenuItemEntity section = menuItemDao.findByKey( command.getSection() );

        if ( !new MenuItemAccessResolver( groupDao ).hasAccess( updater, section, MenuItemAccessType.PUBLISH ) )
        {
            throw new MenuItemAccessException( "Cannot unpublish section content.", updater.getQualifiedName(), MenuItemAccessType.PUBLISH,
                                               command.getSection() );
        }

        for ( ContentKey contentKey : command.getUnapprovedContentToUpdate() )
        {
            SectionContentEntity sectionContentToUpdate = section.getSectionContent( contentKey );
            if ( sectionContentToUpdate == null )
            {
                throw new IllegalStateException( "Attempting to update non-exiting content.  Please reload and try again." );
            }
            sectionContentToUpdate.setOrder( 0 );
            sectionContentToUpdate.setApproved( false );
        }
        return command.getUnapprovedContentToUpdate().size();
    }

    /**
     * <p>reads page key by menu path or all keys in folder </p>
     * <p>path may be absolute or relative format</p>
     *
     * Examples: <br/>
     *
     *  <code>/</code> - all keys in root folder<br/>
     *  <code>./</code> - all keys in current folder<br/>
     *  <code>fldr</code> - key of fldr folder in current folder<br/>
     *  <code>./fldr</code> - key of fldr folder in current folder<br/>
     *  <code>../welcome/fldr</code> - more relative path<br/>
     *  <code>../././welcome/./fldr/../fldr</code> - complex path<br/>
     *
     * if parent folder or relative folder does not exist function returns empty string
     *
     * @param menuItemEntity current menu item
     * @param path menu path to section/page
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

            else

            if ( "..".equals( part ) )
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
                if (currentItemEntity == null)
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

}

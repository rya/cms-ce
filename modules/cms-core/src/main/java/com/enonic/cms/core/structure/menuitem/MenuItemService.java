/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import com.enonic.cms.domain.core.structure.menuitem.ApproveSectionContentCommand;
import com.enonic.cms.domain.core.structure.menuitem.RemoveContentFromSectionCommand;
import com.enonic.cms.domain.core.structure.menuitem.UnapproveSectionContentCommand;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;


public interface MenuItemService
{
    public Integer removeContentFromSection( RemoveContentFromSectionCommand command );

    public Integer unapproveSectionContent( UnapproveSectionContentCommand updateUnapprovedCommand );

    public Integer approveSectionContent( ApproveSectionContentCommand updateApprovedCommand );

    String getPageKeyByPath(MenuItemEntity menuItem, String path);
}

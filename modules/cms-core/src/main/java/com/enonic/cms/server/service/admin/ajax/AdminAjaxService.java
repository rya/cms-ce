/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.ajax;

import java.util.Collection;

import com.enonic.cms.server.service.admin.ajax.dto.PreferenceDto;
import com.enonic.cms.server.service.admin.ajax.dto.SynchronizeStatusDto;
import com.enonic.cms.server.service.admin.ajax.dto.UserDto;

import com.enonic.cms.domain.country.Region;

public interface AdminAjaxService
{
    String deleteContentVersion( int versionKey );

    String getArchiveSizeByCategory( int categoryKey );

    String getArchiveSizeByUnit( int unitKey );

    String getUsedByAsHtml( int contentKey );

    boolean isContentInUse( String[] contentkeys );

    Collection<Region> getCountryRegions( String countryCode );

    boolean startSyncUserStore( String userStoreKey, boolean users, boolean groups, int batchSize );

    SynchronizeStatusDto getSynchUserStoreStatus( String userStoreKey );

    boolean menuItemNameExistsUnderParent( String menuItemName, int existingMenuItemKey, int parentKey );

    String getContentPath( int contentKey );

    String getPagePath( int menuItemKey );

    Collection<UserDto> findUsers( String name );

    Collection<UserDto> findUsersAndAccessType( String name, int contentKey );

    Collection<PreferenceDto> getUserPreferences( String uid );
}

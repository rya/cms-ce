/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.joda.time.DateTime;

import com.enonic.cms.domain.user.UserInfo;


public interface User
{
    public static final String ROOT_UID = "admin";

    public static final String ANONYMOUS_UID = "anonymous";

    UserKey getKey();

    UserType getType();

    UserStoreKey getUserStoreKey();

    GroupKey getUserGroupKey();

    String getName();

    QualifiedUsername getQualifiedName();

    String getPassword();

    String getEmail();

    String getDisplayName();

    DateTime getTimestamp();

    boolean isBuiltIn();

    boolean isRoot();

    boolean isEnterpriseAdmin();

    boolean isAnonymous();

    boolean isDeleted();

    boolean hasUserGroup();

    UserInfo getUserInfo();
}

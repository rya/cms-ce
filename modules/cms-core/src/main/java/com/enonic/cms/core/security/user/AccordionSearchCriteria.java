package com.enonic.cms.core.security.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.security.userstore.UserStoreKey;

public class AccordionSearchCriteria
{
    private String nameExpression;

    private final List<UserStoreKey> userStoreKeys = new ArrayList<UserStoreKey>();

    public String getNameExpression()
    {
        return nameExpression;
    }

    public void setNameExpression( String nameExpression )
    {
        this.nameExpression = nameExpression;
    }

    public void appendUserStoreKey( UserStoreKey userStoreKey )
    {
        userStoreKeys.add( userStoreKey );
    }

    public List<UserStoreKey> getUserStoreKeys()
    {
        return userStoreKeys;
    }

    public boolean hasCriteria()
    {
        return StringUtils.isNotEmpty( nameExpression ) || !userStoreKeys.isEmpty();
    }
}



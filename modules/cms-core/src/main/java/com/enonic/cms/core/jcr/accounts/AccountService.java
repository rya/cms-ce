package com.enonic.cms.core.jcr.accounts;

import com.enonic.cms.domain.EntityPageList;

public interface AccountService
{
    public EntityPageList<Account> findAccounts( int index, int count, String query, String order );

    public Account findAccount( String accountId );
}

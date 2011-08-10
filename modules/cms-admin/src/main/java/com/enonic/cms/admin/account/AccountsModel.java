package com.enonic.cms.admin.account;

import java.util.ArrayList;
import java.util.List;

public class AccountsModel
{
    private int total;

    private List<AccountModel> accounts;

    public AccountsModel()
    {
        this.accounts = new ArrayList<AccountModel>();
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public List<AccountModel> getAccounts()
    {
        return accounts;
    }

    public void setAccounts( List<AccountModel> accounts )
    {
        this.accounts = accounts;
    }

    public void addAccount( AccountModel account )
    {
        this.accounts.add( account );
    }
}

package com.enonic.cms.admin.user;

import java.util.ArrayList;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;

public final class UserListModel
    extends ExtDirectStoreResponse<UserModel>
{
    public UserListModel()
    {
        this.setRecords(new ArrayList<UserModel>());
        this.setSuccess(true);
    }

    public void add(final UserModel user)
    {
        this.getRecords().add(user);
    }
}

package com.enonic.cms.admin.user;

import java.util.ArrayList;
import java.util.List;

public class UsersModel
{
    private int total;
    private List<UserModel> users;

    public UsersModel()
    {
        this.users = new ArrayList<UserModel>();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public void addUser(UserModel user)
    {
        this.users.add(user);
    }
}

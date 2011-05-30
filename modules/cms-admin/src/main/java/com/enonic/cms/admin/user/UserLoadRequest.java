package com.enonic.cms.admin.user;

import com.enonic.cms.admin.common.LoadStoreRequest;

public final class UserLoadRequest
    extends LoadStoreRequest
{
    public String buildHqlQuery()
    {
        final StringBuilder str = new StringBuilder();
        str.append("x.deleted = 0");
        return str.toString();
    }

    public String buildHqlOrder()
    {
        String property = getSort();

        if ("name".equalsIgnoreCase(property)) {
            property = "x.name";
        } else if ("userStore".equalsIgnoreCase(property)) {
            property = "x.userStore";
        } else if ("lastModified".equalsIgnoreCase(property)) {
            property = "x.timestamp";
        } else {
            property = "x.displayName";
        }

        final StringBuilder str = new StringBuilder();
        str.append(property).append(" ").append(getDirection());
        return str.toString();
    }
}

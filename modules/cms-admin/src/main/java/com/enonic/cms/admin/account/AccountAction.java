package com.enonic.cms.admin.account;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.store.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class AccountAction
{
    @Autowired
    private UserDao userDao;
    
    @ExtDirectMethod(ExtDirectMethodType.STORE_READ)
    public ExtDirectStoreResponse<UserModel> getUsers(final ExtDirectStoreReadRequest request)
    {
        final int index = request.getStart() != null ? request.getStart() : 0;
        final int count = request.getLimit() != null ? request.getLimit() : 10;
        final String query = buildUserHqlQuery(request.getQuery());
        final String order = buildUserHqlOrder(request.getSort(), request.getDir());

        final EntityPageList<UserEntity> list = this.userDao.findAll(index, count, query, order);

        final ExtDirectStoreResponse<UserModel> response = new ExtDirectStoreResponse<UserModel>();
        response.setTotal(list.getTotal());
        response.setSuccess(true);
        response.setRecords(UserModelHelper.toModel(list.getList()));

        return response;
    }

    private String buildUserHqlQuery(final String query)
    {
        final StringBuilder str = new StringBuilder();
        str.append("x.deleted = 0");

        if (query != null) {
            str.append(" AND x.qualifedName = '").append(query).append("'");
        }
        
        return str.toString();
    }

    private String buildUserHqlOrder(final String field, final String dir)
    {
        String property = field;

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
        str.append(property).append(" ").append(dir != null ? dir : "ASC");
        return str.toString();
    }
}

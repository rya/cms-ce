package com.enonic.cms.admin.user;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.filter.StringFilter;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.store.dao.UserDao;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;

@Component
public final class UserAction
{
    @Autowired
    private UserDao userDao;

    @ExtDirectMethod(ExtDirectMethodType.STORE_READ)
    public UserListModel loadAll(final ExtDirectStoreReadRequest request)
    {
        final String filter = buildUserFilter(request.getFilters());
        final String order = buildUserOrder(request.getSorters());

        final EntityPageList<UserEntity> list = this.userDao.findAll(request.getStart(), request.getLimit(), filter, order);
        return UserModelBuilder.toModel(list);
    }

    private String buildUserFilter(final Collection<Filter> filterList)
    {
        final Filter filter = (filterList != null && !filterList.isEmpty()) ? filterList.iterator().next() : null;
        final StringFilter strFilter = (filter instanceof StringFilter) ? (StringFilter)filter : null;
        return buildUserFilter(strFilter);
    }

    private String buildUserOrder(final Collection<SortInfo> sortList)
    {
        final SortInfo sortInfo = (sortList != null && !sortList.isEmpty()) ? sortList.iterator().next() : null;
        return buildUserOrder(sortInfo);
    }

    private String buildUserFilter(final StringFilter filter)
    {
        final String filterField = filter != null ? filter.getField() : null;
        final String filterValue = filter != null ? filter.getValue() : null;

        if (Strings.isNullOrEmpty(filterField)) {
            return null;
        }

        if (Strings.isNullOrEmpty(filterValue)) {
            return null;
        }

        final StringBuffer str = new StringBuffer("x.deleted = 0 AND ");
        str.append("x.").append(filterField).append(" like '%")
                .append(filterValue.replace("'", "''")).append("%'");

        return str.toString();
    }

    private String buildUserOrder(final SortInfo sort)
    {
        String sortField = sort != null ? sort.getProperty() : "displayName";
        final boolean sortDescending = (sort != null) && (sort.getDirection() == SortDirection.DESCENDING);

        if ("lastModified".equalsIgnoreCase(sortField)) {
            sortField = "timestamp";
        }

        final StringBuffer str = new StringBuffer();
        str.append("x.").append(sortField).append(" ").append(sortDescending ? "DESC" : "ASC");
        return str.toString();
    }
}

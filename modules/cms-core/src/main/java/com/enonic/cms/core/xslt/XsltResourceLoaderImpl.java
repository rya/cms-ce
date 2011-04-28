package com.enonic.cms.core.xslt;

import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.store.dao.ResourceDao;
import org.springframework.beans.factory.annotation.Autowired;

public final class XsltResourceLoaderImpl
    implements XsltResourceLoader
{
    @Autowired
    private ResourceDao resourceDao;

    public XsltResource load(final String path)
    {
        final ResourceFile file = this.resourceDao.getResourceFile(ResourceKey.parse(path));
        if (file == null) {
            return null;
        }

        return XsltResource.create(path, file.getDataAsString());
    }
}

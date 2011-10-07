/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.id;

import java.io.Serializable;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.enonic.esl.util.DigestUtil;

import com.enonic.cms.core.security.group.GroupKey;

public class GroupIdentifierGenerator
    implements IdentifierGenerator

{
    public Serializable generate( SessionImplementor session, Object object )
    {
        return new GroupKey( DigestUtil.generateSHA( java.util.UUID.randomUUID().toString() ) );
    }
}

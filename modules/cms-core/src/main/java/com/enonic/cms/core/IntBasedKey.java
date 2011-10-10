/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.io.Serializable;

public interface IntBasedKey
    extends Serializable
{

    //public Integer toInteger();

    int toInt();
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.time;

import org.joda.time.DateTime;

/**
 * Jun 18, 2009
 */
public interface TimeService
{
    DateTime getNowAsDateTime();

    long getNowAsMilliseconds();

}

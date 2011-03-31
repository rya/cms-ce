/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public enum InputType
    implements Serializable
{
    TEXT,
    TEXT_AREA,
    HTML_AREA,
    URL,
    DATE,
    BOOLEAN,
    SELECTOR,
    RELATED_CONTENT,
    RELATED_CONTENTS,
    FILE,
    FILES,
    IMAGE,
    IMAGES,
    BINARY,
    XML,
    SET,
    KEYWORDS
}

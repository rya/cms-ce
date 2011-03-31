<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="displaycolumn_access">
        <xsl:param name="xpath"/>
        <xsl:choose>
            <xsl:when test="boolean($xpath/accessrights/accessright[@grouptype = 7])">
                <img src="images/icon_locked.gif" style="filter: alpha(opacity=30);"/>
            </xsl:when>
            <xsl:otherwise>
                <img src="images/icon_locked.gif"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
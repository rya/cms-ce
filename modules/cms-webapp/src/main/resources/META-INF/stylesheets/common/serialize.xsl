<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:template name="serialize">
        <xsl:param name="xpath"/>
        <xsl:param name="include-self" select="false()"/>

        <xsl:if test="$xpath">
            <xsl:variable name="serialized">
                <xsl:value-of select="admin:serialize($xpath, $include-self)"/>
            </xsl:variable>

            <xsl:choose>
                <xsl:when test="$serialized and not($serialized = '')">
                    <xsl:value-of select="$serialized"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$xpath"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
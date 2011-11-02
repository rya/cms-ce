<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="getsuffix">
        <xsl:param name="fname"/>
        <xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
        <xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
        <xsl:variable name="suffix"><xsl:value-of select="substring-after($fname, '.')"/></xsl:variable>
        <xsl:choose>
            <xsl:when test="$suffix!=''">
                <xsl:call-template name="getsuffix">
                    <xsl:with-param name="fname" select="$suffix"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="translate($fname,$ucletters,$lcletters)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
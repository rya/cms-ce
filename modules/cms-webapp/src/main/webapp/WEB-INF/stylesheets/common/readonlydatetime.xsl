<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="readonlydatetime">
        <xsl:param name="label"/>
        <xsl:param name="name"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan" select="''"/>
        <xsl:param name="includeseconds" select="false()"/>

        <td class="form_labelcolumn" valign="baseline"><xsl:value-of select="$label"/></td>
        <td>
            <xsl:if test="$colspan != ''">
                <xsl:attribute name="colspan">
                    <xsl:value-of select="$colspan"/>
                </xsl:attribute>
            </xsl:if>
            <span>
                <xsl:attribute name="id">
                    <xsl:value-of select="$name"/>
                </xsl:attribute>
                <xsl:call-template name="formatdatetime">
                    <xsl:with-param name="date" select="$selectnode"/>
                    <xsl:with-param name="includeseconds" select="$includeseconds"/>
                </xsl:call-template>
            </span>

            <input type="hidden">
                <xsl:attribute name="name">date<xsl:value-of select="$name"/></xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:call-template name="formatdate">
                        <xsl:with-param name="date" select="$selectnode"/>
                    </xsl:call-template>
                </xsl:attribute>
            </input>
            <xsl:text> </xsl:text>
            <input type="hidden">
                <xsl:attribute name="name">time<xsl:value-of select="$name"/></xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:call-template name="formattime">
                        <xsl:with-param name="date" select="$selectnode"/>
                        <xsl:with-param name="includeseconds" select="$includeseconds"/>
                    </xsl:call-template>
                </xsl:attribute>
            </input>
        </td>
    </xsl:template>

</xsl:stylesheet>
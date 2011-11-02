<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="displaypath">
        <xsl:param name="domainkey" select="''"/>
        <xsl:param name="unitkey" select="''"/>
        <xsl:param name="unitname" select="$unitname"/>
        <xsl:param name="presentationlayerkey" select="''"/>
        <xsl:param name="presentationlayername" select="$menuname"/>
        <xsl:param name="lastelement" select="''"/>
        <xsl:param name="lastelementurl" select="''"/>
        <xsl:param name="nolinks" select="false()"/>

      
        <xsl:choose>
            <xsl:when test="$unitkey != ''">
                <xsl:choose>
                    <xsl:when test="$nolinks">
                        <xsl:value-of select="$unitname"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <a>
                            <xsl:if test="$unitkey != ''">
                                <xsl:attribute name="href">
                                    <xsl:text>adminpage?op=page&amp;page=600</xsl:text>
                                    <xsl:text>&amp;domainkey=</xsl:text><xsl:value-of select="$domainkey"/>
                                    <xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$unitkey"/>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="$unitname"/>
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text> / </xsl:text>
            </xsl:when>
        </xsl:choose>
        <xsl:if test="$presentationlayerkey != ''">
            <xsl:choose>
                <xsl:when test="$nolinks">
                    <xsl:text>%headMenus%</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <a>
                        <xsl:attribute name="href">
                            <xsl:text>adminpage?op=listmenus&amp;page=851</xsl:text>
                            <xsl:text>&amp;domainkey=</xsl:text><xsl:value-of select="$domainkey"/>
                            <xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$unitkey"/>
                            <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                        </xsl:attribute>
                        <xsl:text>%headMenus%</xsl:text>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text> / </xsl:text>
            <xsl:choose>
                <xsl:when test="$nolinks">
                    <xsl:value-of select="$presentationlayername"/>
                </xsl:when>
                <xsl:otherwise>
                    <a>
                        <xsl:attribute name="href">
                            <xsl:text>adminpage?op=browse&amp;page=851</xsl:text>
                            <xsl:text>&amp;domainkey=</xsl:text><xsl:value-of select="$domainkey"/>
                            <xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$unitkey"/>
                            <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                        </xsl:attribute>
                        <xsl:value-of select="$presentationlayername"/>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="$lastelement != ''">
            <xsl:text> / </xsl:text>
            <xsl:choose>
                <xsl:when test="$lastelementurl != ''">
                    <a href="{$lastelementurl}">
                        <xsl:value-of select="$lastelement"/>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$lastelement"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>

    </xsl:template>

</xsl:stylesheet>

<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="sectionheader">
        <xsl:param name="rootname" select="''"/>
        <xsl:param name="op" select="'browse'"/>
        <xsl:param name="rootelem"/>

        <xsl:choose>
            <xsl:when test="$rootelem">
                <xsl:for-each select="$rootelem/sectionnames/sectionname">
                    <xsl:text> / </xsl:text>
                    <xsl:call-template name="sectionheader1">
                        <xsl:with-param name="op" select="$op"/>
                        <xsl:with-param name="section" select="."/>
                        <xsl:with-param name="name">
                            <xsl:choose>
                                <xsl:when test="position() = 1 and $rootname != ''">
                                    <xsl:value-of select="$rootname"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="."/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="/contents">
                <xsl:for-each select="/contents/sectionnames/sectionname">
                    <xsl:text> / </xsl:text>
                    <xsl:call-template name="sectionheader1">
                        <xsl:with-param name="op" select="$op"/>
                        <xsl:with-param name="section" select="."/>
                        <xsl:with-param name="name">
                            <xsl:choose>
                                <xsl:when test="position() = 1 and $rootname != ''">
                                    <xsl:value-of select="$rootname"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="."/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="/sections">
                <xsl:for-each select="/sections/sectionnames/sectionname">
                    <xsl:text> / </xsl:text>
                    <xsl:call-template name="sectionheader1">
                        <xsl:with-param name="op" select="$op"/>
                        <xsl:with-param name="section" select="."/>
                        <xsl:with-param name="name">
                            <xsl:choose>
                                <xsl:when test="position() = 1 and $rootname != ''">
                                    <xsl:value-of select="$rootname"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="."/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:for-each select="/contents/sectionnames/sectionname">
                    <xsl:text> / </xsl:text>
                    <xsl:call-template name="sectionheader1">
                        <xsl:with-param name="op" select="$op"/>
                        <xsl:with-param name="section" select="."/>
                        <xsl:with-param name="name">
                            <xsl:choose>
                                <xsl:when test="position() = 1 and $rootname != ''">
                                    <xsl:value-of select="$rootname"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="."/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="sectionheader1">
        <xsl:param name="section"/>
        <xsl:param name="name" select="''"/>
        <xsl:param name="op"/>

        <a>
            <xsl:attribute name="href">
                <xsl:text>adminpage?page=950</xsl:text>
                <xsl:text>&amp;op=</xsl:text><xsl:value-of select="$op"/>
                <xsl:text>&amp;sec=</xsl:text><xsl:value-of select="@key"/>
                <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
            </xsl:attribute>
            <xsl:value-of select="$name"/>
        </a>
    </xsl:template>

</xsl:stylesheet>

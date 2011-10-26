<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

    <xsl:template name="displayfheader">
        <xsl:param name="category"/>

        <xsl:variable name="supercatkey">
            <xsl:value-of select="$category/@supercategorykey"/>
        </xsl:variable>

        <xsl:choose>

            <xsl:when test="/contents/contentcategory[@key = $supercatkey]">
                <xsl:call-template name="displayfheader">
                    <xsl:with-param name="category" select="/contents/contentcategory[@key = $supercatkey]"/>
                </xsl:call-template>
                <xsl:text> / </xsl:text>
            </xsl:when>

            <xsl:when test="/contents/contentcategories/contentcategory[@key = $supercatkey]">
                <xsl:call-template name="displayfheader">
                    <xsl:with-param name="category" select="/contents/contentcategories/contentcategory[@key = $supercatkey]"/>
                </xsl:call-template>
                <xsl:text> / </xsl:text>
            </xsl:when>

        </xsl:choose>

        <a href="adminpage?page={$page}&amp;op=browse&amp;cat={$category/@key}&amp;selectedunitkey={$selectedunitkey}">
            <xsl:value-of select="$category/name"/>
        </a>

    </xsl:template>

</xsl:stylesheet>
<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="menu" mode="text">
		<xsl:value-of select="@name"/> (<xsl:value-of select="@language"/>)
	</xsl:template>

	<xsl:template match="menu" mode="icon">
		<xsl:variable name="lockstring">
			<xsl:if test="not(@anonread = 'true')">_lock</xsl:if>
        </xsl:variable>

		<xsl:text>icon_site</xsl:text>
		<xsl:value-of select="$lockstring"/>
	</xsl:template>

	<xsl:template match="menu" mode="haschildren">
		<xsl:text>true</xsl:text>
	</xsl:template>

	<xsl:template match="menu" mode="page">
		<xsl:text>851</xsl:text>
	</xsl:template>

	<xsl:template match="menu" mode="loadurl">
		<xsl:text>javascript:loadBranch('menu', </xsl:text>
		<xsl:value-of select="@key"/>
		<xsl:text>);</xsl:text>
	</xsl:template>

	<xsl:template match="menu" mode="extraparams">
		<xsl:text>&amp;menukey=</xsl:text>
		<xsl:value-of select="@key"/>
	</xsl:template>

  <xsl:template match="menu" mode="tooltip">
    <xsl:value-of select="@name"/>
    <xsl:text> (%fldKey%:</xsl:text>
    <xsl:value-of select="@key"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

</xsl:stylesheet>
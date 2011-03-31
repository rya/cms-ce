<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="key" mode="display">
		<xsl:param name="contentxpath"/>

		<xsl:value-of select="saxon:evaluate(concat($contentxpath, '@key'))"/>
	</xsl:template>
	
	<xsl:template match="key" mode="title">
		<xsl:text>%fldKey%</xsl:text>
	</xsl:template>
	
	<xsl:template match="key" mode="orderby">
		<xsl:text>@key</xsl:text>
	</xsl:template>
	
	<xsl:template match="key" mode="width">
		<xsl:text>80</xsl:text>
	</xsl:template>
	
	<xsl:template match="key" mode="titlealign">
		<xsl:text>center</xsl:text>
	</xsl:template>

	<xsl:template match="key" mode="columnalign">
		<xsl:text>right</xsl:text>
	</xsl:template>

</xsl:stylesheet>

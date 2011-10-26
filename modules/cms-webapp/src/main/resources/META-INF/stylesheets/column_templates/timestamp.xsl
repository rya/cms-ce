<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="timestamp" mode="display">
		<xsl:param name="contentelem"/>
		<xsl:call-template name="formatdatetime">
			<xsl:with-param name="date" select="$contentelem/@timestamp"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="timestamp" mode="title">
		<xsl:text>%fldModified%</xsl:text>
	</xsl:template>
	
	<xsl:template match="timestamp" mode="orderby">
		<xsl:text>@timestamp</xsl:text>
	</xsl:template>
	
	<xsl:template match="timestamp" mode="width">
		<xsl:text>100</xsl:text>
	</xsl:template>
	
	<xsl:template match="timestamp" mode="titlealign">
		<xsl:text>center</xsl:text>
	</xsl:template>

	<xsl:template match="timestamp" mode="columnalign">
		<xsl:text>center</xsl:text>
	</xsl:template>

</xsl:stylesheet>

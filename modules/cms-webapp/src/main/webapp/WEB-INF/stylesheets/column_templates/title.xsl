<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="title" mode="display">
		<xsl:param name="contentelem"/>
		<xsl:value-of select="$contentelem/title"/>
	</xsl:template>
	
	<xsl:template match="title" mode="title">
		<xsl:text>%fldTitle%</xsl:text>
	</xsl:template>
	
	<xsl:template match="title" mode="orderby">
		<xsl:text>title</xsl:text>
	</xsl:template>

</xsl:stylesheet>

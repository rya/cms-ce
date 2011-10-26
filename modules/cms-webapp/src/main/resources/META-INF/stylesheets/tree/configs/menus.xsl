<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="menus" mode="icon">
        <xsl:text>icon_sites</xsl:text>
	</xsl:template>
	
	<xsl:template match="menus" mode="text">
		<xsl:text>%mnuSites%</xsl:text>
	</xsl:template>
	
	<xsl:template match="menus" mode="op">
		<xsl:text>listmenus</xsl:text>
	</xsl:template>
	
	<xsl:template match="menus" mode="page">
		<xsl:text>851</xsl:text>
	</xsl:template>
	
	<xsl:template match="menus" mode="sortchildren">
		<xsl:text>@name</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
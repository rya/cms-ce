<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="pagetemplates" mode="icon">
        <xsl:text>icon_frameworks</xsl:text>
	</xsl:template>
	
	<xsl:template match="pagetemplates" mode="text">
		<xsl:text>%mnuFrameworks%</xsl:text>
	</xsl:template>
	
	<xsl:template match="pagetemplates" mode="page">
		<xsl:text>550</xsl:text>
	</xsl:template>
	
	<xsl:template match="pagetemplates" mode="extraparams">
		<xsl:text>&amp;menukey=</xsl:text>
		<xsl:value-of select="parent::node()/@key"/>
	</xsl:template>
	
</xsl:stylesheet>
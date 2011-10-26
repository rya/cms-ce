<?xml version="1.0" encoding="utf-8"?>
    <xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:exslt-common="http://exslt.org/common"
            xmlns:saxon="http://saxon.sf.net/"
            xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="contenttype" mode="display">
		<xsl:param name="contentelem"/>
		<xsl:param name="contenttypeelem"/>
		
		<xsl:value-of select="$contenttypeelem/name"/>
	</xsl:template>
	
	<xsl:template match="contenttype" mode="title">
		<xsl:text>%fldContentType%</xsl:text>
	</xsl:template>
	
	<xsl:template match="contenttype" mode="width">
		90
	</xsl:template>
	
</xsl:stylesheet>

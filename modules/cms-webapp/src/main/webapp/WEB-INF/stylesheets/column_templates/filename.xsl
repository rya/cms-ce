<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">
	
	<xsl:include href="../common/getsuffix.xsl"/>

	<xsl:template match="filename" mode="display">
		<xsl:param name="contentxpath"/>
		
		<xsl:value-of select="saxon:evaluate(concat($contentxpath, @xpath))"/>
	</xsl:template>
	
	<xsl:template match="filename" mode="title">
		<xsl:text>%fldName%</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>

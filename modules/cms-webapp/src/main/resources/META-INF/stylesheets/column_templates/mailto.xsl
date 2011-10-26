<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="mailto" mode="display">
		<xsl:param name="contentxpath"/>
		<a>
			<xsl:attribute name="href">
				<xsl:text>mailto:</xsl:text>
				<xsl:value-of select="saxon:evaluate(concat($contentxpath, @emailxpath))"/>
			</xsl:attribute>
			<xsl:value-of select="saxon:evaluate(concat($contentxpath, @namexpath))"/>
		</a> 

	</xsl:template>
	
	<xsl:template match="mailto" mode="clickable">
		<xsl:text>false</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>

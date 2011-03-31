<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="binarylink" mode="display">
		<xsl:param name="contentxpath"/>
		<a>
			<xsl:if test="@newwindow = 'true'">
				<xsl:attribute name="target">
					<xsl:text>_blank</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="href">
				<xsl:text>_attachment/</xsl:text>
				<xsl:value-of select="saxon:evaluate(concat($contentxpath, @xpath))"/>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="@text">
					<xsl:value-of select="@text"/>
				</xsl:when>
				<xsl:when test="@textxpath">
					<xsl:value-of select="saxon:evaluate(concat($contentxpath, @textxpath))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="saxon:evaluate(concat($contentxpath, @xpath))"/>
				</xsl:otherwise>
			</xsl:choose>
		</a>
	</xsl:template>
	
	<xsl:template match="binarylink" mode="clickable">
		<xsl:text>false</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>

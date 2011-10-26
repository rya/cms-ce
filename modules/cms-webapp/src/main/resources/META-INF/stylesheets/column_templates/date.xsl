<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="date" mode="display">
		<xsl:param name="contentxpath"/>
		
		<xsl:choose>
			<xsl:when test="@dateonly = 'true'">
				<xsl:call-template name="formatdate">
					<xsl:with-param name="date" select="saxon:evaluate(concat($contentxpath, @xpath))"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@timeonly = 'true'">
				<xsl:call-template name="formattime">
					<xsl:with-param name="date" select="saxon:evaluate(concat($contentxpath, @xpath))"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="formatdatetime">
					<xsl:with-param name="date" select="saxon:evaluate(concat($contentxpath, @xpath))"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="date" mode="width">
		<xsl:text>100</xsl:text>
	</xsl:template>
	
	<xsl:template match="date" mode="titlealign">
		<xsl:text>center</xsl:text>
	</xsl:template>

	<xsl:template match="date" mode="columnalign">
		<xsl:text>center</xsl:text>
	</xsl:template>


</xsl:stylesheet>

<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html" />

	<!-- Standard substring replace template -->
	<xsl:template name="escapequotes">
		<xsl:param name="string" />
		
		<xsl:variable name="newstring1">
			<xsl:call-template name="escape">
				<xsl:with-param name="string" select="$string"/>
				<xsl:with-param name="escapestring" select="&quot;'&quot;"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="newstring2">
			<xsl:call-template name="escape">
				<xsl:with-param name="string" select="$newstring1"/>
				<xsl:with-param name="escapestring" select="'&quot;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:value-of select="$newstring2"/>
	</xsl:template>
	
	<xsl:template name="escape">
		<xsl:param name="string"/>
		<xsl:param name="escapestring"/>
		
		<xsl:choose>
			<xsl:when test="contains($string, $escapestring)">
				<xsl:value-of select="substring-before($string, $escapestring)"/>
				<xsl:text>\</xsl:text><xsl:value-of select="$escapestring"/>
				<xsl:call-template name="escape">
					<xsl:with-param name="string" select="substring-after($string, $escapestring)"/>
					<xsl:with-param name="escapestring" select="$escapestring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string" />
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>
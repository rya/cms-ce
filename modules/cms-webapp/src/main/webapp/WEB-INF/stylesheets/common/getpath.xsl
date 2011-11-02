<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html"/>

	<xsl:template name="getpath">
		<xsl:param name="path" select="."/>
		<xsl:param name="__pathstr"/>
		
		<xsl:variable name="pathstr">
			<xsl:value-of select="name($path)"/>

			<xsl:variable name="position">
				<xsl:value-of select="count($path/preceding-sibling::node()[name() = name($path)]) + 1"/>
			</xsl:variable>
			
			<xsl:variable name="count">
				<xsl:value-of select="count($path/following-sibling::node()[name() = name($path)]) + $position"/>
			</xsl:variable>
			
			<xsl:if test="$count > 1">
				<xsl:text>[</xsl:text>
				<xsl:value-of select="$position"/>
				<xsl:text>]</xsl:text>
			</xsl:if>
			<xsl:if test="$__pathstr">
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$__pathstr"/>
			</xsl:if>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$path/parent::node()">
				<xsl:call-template name="getpath">
					<xsl:with-param name="path" select="$path/parent::node()"/>
					<xsl:with-param name="__pathstr" select="$pathstr"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$pathstr"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>

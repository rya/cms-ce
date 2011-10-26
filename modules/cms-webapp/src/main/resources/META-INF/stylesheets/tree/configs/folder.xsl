<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:variable name="shadeicon">
    <xsl:apply-templates select="." mode="shadeicon"/>
  </xsl:variable>

	<xsl:template match="folder" mode="icon">
		<xsl:text>icon_folder_resources</xsl:text>
    <xsl:if test="$subop = 'moveFile' and $path = @fullPath">_shaded</xsl:if>
    <xsl:if test="($subop = 'moveFolder' and ancestor-or-self::*/@fullPath = $path) or ($subop = 'moveFolder' and ./folder/@fullPath = $path)">_shaded</xsl:if>
  </xsl:template>

	<xsl:template match="folder" mode="page">
		<xsl:text>800</xsl:text>
	</xsl:template>

	<xsl:template match="folder" mode="extraparams">
		<xsl:text>&amp;path=</xsl:text><xsl:value-of select="@fullPath" />
	</xsl:template>

	<xsl:template match="folder" mode="key">
		<xsl:value-of select="@hashedFullPath" />
	</xsl:template>

	<xsl:template match="folder" mode="haschildren">
		<xsl:choose>
			<xsl:when test="*[name() = 'folder']">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="folder" mode="hassibling">
		<xsl:choose>
			<xsl:when test="following-sibling::node()[name() = 'folder']">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
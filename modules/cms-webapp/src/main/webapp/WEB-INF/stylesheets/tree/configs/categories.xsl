<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="categories" mode="icon">
		<xsl:text>icon_folder</xsl:text>
		<xsl:if test="@disabled = 'true'">
			<xsl:text>_shaded</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="categories" mode="shadeicon">
		<xsl:choose>
			<xsl:when test="@disabled = 'true'">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:when test="@useraccess = 'false'">
                <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:when test="@usercreate = 'false'">
                <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>false</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
	</xsl:template>
	
	<xsl:template match="categories" mode="text">
		<xsl:text>%mnuContentRepositories%</xsl:text>
	</xsl:template>
	
	<xsl:template match="categories" mode="page">
		<xsl:text>600</xsl:text>
	</xsl:template>
	
	<xsl:template match="categories" mode="extraparams">
		<xsl:if test="@disabled = 'true'">
			<xsl:text>&amp;disabled=true</xsl:text>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>

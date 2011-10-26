<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="menutop" mode="icon">
        <xsl:variable name="typestring">
        	<xsl:text>menuitems</xsl:text>
        </xsl:variable>
        
        <xsl:variable name="shadeicon">
        	<xsl:apply-templates select="." mode="shadeicon"/>
        </xsl:variable>

		<xsl:variable name="shadestring">
			<xsl:if test="$shadeicon = 'true'">_shaded</xsl:if>
		</xsl:variable>
		
		<xsl:text>icon_</xsl:text>
		<xsl:value-of select="$typestring"/>
		<xsl:value-of select="$shadestring"/>
	</xsl:template>
	
	<xsl:template match="menutop" mode="shadeicon">
		<xsl:choose>
			<xsl:when test="parent::node()/@userread = 'false'">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>false</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="menutop" mode="text">
		<xsl:text>%mnuPageBuilder%</xsl:text>
	</xsl:template>
	
	<xsl:template match="menutop" mode="op">
		<xsl:text>browse</xsl:text>
	</xsl:template>
	
	<xsl:template match="menutop" mode="page">
		<xsl:text>850</xsl:text>
	</xsl:template>
	
	<xsl:template match="menutop" mode="sortchildren">
		<xsl:text>@order</xsl:text>
	</xsl:template>
	
	<xsl:template match="menutop" mode="sortchildrentype">
		<xsl:text>number</xsl:text>
	</xsl:template>
	
	<xsl:template match="menutop" mode="extraparams">
		<xsl:text>&amp;menukey=</xsl:text>
		<xsl:value-of select="parent::node()/@key"/>
	</xsl:template>
	
</xsl:stylesheet>
<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:param name="path"/>
  <xsl:param name="subop"/>

	<xsl:include href="categories.xsl"/>
	<xsl:include href="category.xsl"/>
	<xsl:include href="menus.xsl"/>
	<xsl:include href="menu.xsl"/>
	<xsl:include href="menuitem.xsl"/>
	<xsl:include href="menutop.xsl"/>
	<xsl:include href="objects.xsl"/>
	<xsl:include href="pagetemplates.xsl"/>
	<xsl:include href="resources.xsl"/>
	<xsl:include href="folder.xsl"/>
	<xsl:include href="file.xsl"/>
	
	<!--xsl:include href="findparentkey.xsl"/-->
	
	<xsl:template match="*" mode="icon">
		<xsl:text>icon_folder</xsl:text>
	</xsl:template>
	
	<xsl:template match="*" mode="shadeicon">
		<xsl:text>false</xsl:text>
	</xsl:template>
	
	<xsl:template match="*" mode="iconimage">
		<xsl:variable name="src">
			<xsl:apply-templates select="." mode="icon"/>
		</xsl:variable>
		
		<xsl:variable name="shadeicon">
			<xsl:apply-templates select="." mode="shadeicon"/>
		</xsl:variable>

		<img width="16" height="16">
			<xsl:attribute name="src">
				<xsl:text>images/</xsl:text>
				<xsl:value-of select="$src"/>
				<xsl:text>.gif</xsl:text>
			</xsl:attribute>
			<xsl:if test="$shadeicon = 'true'">
				<xsl:attribute name="style">
					<xsl:text>filter: alpha(opacity=30);</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</img>
	</xsl:template>
	
	<xsl:template match="*" mode="text">
		<xsl:value-of select="@name"/>
	</xsl:template>
	
	<!--xsl:template match="*" mode="url"/-->
	
	<xsl:template match="*" mode="op">
		<xsl:text>browse</xsl:text>
	</xsl:template>
	
	<xsl:template match="*" mode="page"/>
	
	<xsl:template match="*" mode="extraparams"/>
	
	<xsl:template match="*" mode="loadurl"/>
	
	<xsl:template match="*" mode="haschildren">
		<xsl:choose>
			<xsl:when test="*">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="*" mode="sortchildren"/>
	
	<xsl:template match="*" mode="tooltip"/>
	
	<xsl:template match="*" mode="sortchildrentype">
		<xsl:text>text</xsl:text>
	</xsl:template>
	
	<xsl:template match="*" mode="key">
		<xsl:value-of select="@key"/>
	</xsl:template>
	
	<xsl:template match="*" mode="hide"/>
	
	<xsl:template match="*" mode="hassibling"/>
			
</xsl:stylesheet>

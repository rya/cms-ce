<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="title" mode="display">
		<xsl:param name="contentelem"/>
		<div style="font-weight: bold"><xsl:value-of select="$contentelem/title"/></div>
        <span style="color: gray">
            <xsl:if test="$contentelem/@repositorypath"><xsl:value-of select="$contentelem/@repositorypath"/>/</xsl:if>
            <xsl:value-of select="$contentelem/name"/>
        </span>
	</xsl:template>

	<xsl:template match="title" mode="title">
		<xsl:text>%fldTitle%</xsl:text>
	</xsl:template>

	<xsl:template match="title" mode="orderby">
		<xsl:text>title</xsl:text>
	</xsl:template>

</xsl:stylesheet>


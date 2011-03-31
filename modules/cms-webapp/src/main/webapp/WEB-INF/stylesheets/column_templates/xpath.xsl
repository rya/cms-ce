<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="xpath" mode="display">
		<xsl:param name="contentxpath"/>
		
		<xsl:variable name="xpath">
			<xsl:value-of select="$contentxpath"/>
			<xsl:value-of select="."/>
		</xsl:variable>

		<xsl:value-of select="saxon:evaluate($xpath)"/>
	</xsl:template>

	<xsl:template match="xpath" mode="orderby">
		<xsl:param name="indexingxpath"/>

        <xsl:variable name="quote">
            <xsl:text>'</xsl:text>
        </xsl:variable>

        <xsl:variable name="double-quote">
            <xsl:text>"</xsl:text>
        </xsl:variable>

		<xsl:variable name="fullxpath">
			<xsl:value-of select="$indexingxpath"/>
			<xsl:text>index[@xpath='</xsl:text>
            <xsl:value-of select="replace(. , $quote, $double-quote)"/>
			<xsl:text>']</xsl:text>
		</xsl:variable>

		<xsl:if test="saxon:evaluate($fullxpath)">
			<xsl:value-of select="."/>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>
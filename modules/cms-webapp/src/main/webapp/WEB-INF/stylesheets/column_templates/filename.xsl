<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">
	
	<xsl:include href="../common/getsuffix.xsl"/>

    <xsl:template match="filename" mode="display">
        <xsl:param name="contentxpath"/>
        <div style="font-weight: bold">
            <xsl:value-of select="saxon:evaluate(concat($contentxpath, @xpath))"/>
        </div>
        <span style="color: gray">
            <xsl:if test="saxon:evaluate(concat($contentxpath, '@repositorypath'))">
                <xsl:value-of select="saxon:evaluate(concat($contentxpath, '@repositorypath'))"/>
                <xsl:text>/</xsl:text>
            </xsl:if>
            <xsl:value-of select="saxon:evaluate(concat($contentxpath, 'name'))"/>
        </span>
    </xsl:template>

	
	<xsl:template match="filename" mode="title">
		<xsl:text>%fldName%</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>

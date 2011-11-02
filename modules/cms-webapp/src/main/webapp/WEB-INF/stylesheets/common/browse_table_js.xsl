<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html"/>
	<!--
		This is a hack!!!
		Firefox < 2.0 is missing the click() method.
		https://bugzilla.mozilla.org/show_bug.cgi?id=148585
	-->
	<xsl:template name="addJSEvent">
		<xsl:param name="key"/>
		<xsl:attribute name="onclick">
			<xsl:text>javascript:if( document.all) {</xsl:text>
			<xsl:text>document.getElementById('operation_edit_</xsl:text>
			<xsl:value-of select="$key"/>
			<xsl:text>').click();</xsl:text>
			<xsl:text> } else { document.location.href = document.getElementById('operation_edit_</xsl:text>
			<xsl:value-of select="$key"/>
			<xsl:text>').href; }</xsl:text>		
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>

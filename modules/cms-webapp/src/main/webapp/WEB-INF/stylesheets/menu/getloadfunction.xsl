<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template name="getloadfunction">
		<xsl:param name="element"/>
		<xsl:param name="elementname"/>

		<xsl:choose>
			<xsl:when test="$elementname = 'category'">
				<xsl:text>javascript:loadBranch('</xsl:text>
				<xsl:value-of select="$elementname"/>
				<xsl:text>', </xsl:text>
				<xsl:value-of select="@unitkey"/>
				<xsl:text>);</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
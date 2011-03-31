<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template name="getnodetext">
		<xsl:choose>
			<xsl:when test="name() = 'unit'"/>
			<xsl:when test="@name">
				<xsl:value-of select="@name"/>
			</xsl:when>
			<xsl:when test="name">
				<xsl:value-of select="name"/>
			</xsl:when>
			<xsl:when test="name() = 'categories'">%mnuContentRepositories%</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
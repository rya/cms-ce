<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template name="getpagekey">
		<xsl:param name="nodename"/>

		<xsl:choose>
			<xsl:when test="$nodename = 'category'">
				<xsl:choose>
					<xsl:when test="@contenttypekey">
						<xsl:value-of select="(@contenttypekey + 999)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>991</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$nodename = 'categories'">
				<xsl:text>600</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>